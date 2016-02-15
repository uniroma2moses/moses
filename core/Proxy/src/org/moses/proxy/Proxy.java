/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import org.moses.entity.solution.EndpointNode;
import org.moses.entity.solution.GroupNode;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchSolutionMDALException;
import org.moses.mdal.MDAL;
import org.moses.mdal.SolutionsMDALInterface;
import org.moses.proxy.monitor_policy.MonitorPolicy;
import org.moses.proxy.monitor_policy.MonitorPolicyInterface;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dante
 */
public class Proxy {

    private static final String SERVICE_NAMESPACE_IDENTIFIER = "servicens";
    private static final String SCHEMA_NAMESPACE = "http://xml.netbeans.org/schema/ProxySchema";
    private static final String SCHEMA_NAMESPACE_IDENTIFIER = "proxyns";
    private Element parallelResult = null;
    private int deadChilds = 0;
    private int childs = 0;
    private Exception lastException = null;
    private String processName = null;
    private String operationName = null;
    /**
     * Cost patch - part0
     */
    private double cost = -1;

    public synchronized double getCost() {
        return cost;
    }

    public synchronized void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Cost patch - part0 - end
     */
    /** setLastException is used by client threads to set their exceptions (if any)
     * only the last exception is saved
     */
    protected synchronized void setLastException(Exception a) {
        this.lastException = a;
    }

    /** incrementDeadChilds is used by this class to know when all thread childs are dead
     * in that case an exception is thrown
     */
    protected synchronized void incrementDeadChilds() {
        deadChilds++;
        if (parallelResult != null || deadChilds == childs) {
            this.notify();
        }

    }

    /** setParallelResult is used by client threads to set result message from web service
     */
    protected synchronized void setParallelResult(Element result) {
        this.parallelResult = result;
    }

    /**Sequential web service invoke
     * @param option with action already setted
     * @param payload message to send as parameter to destination webservices
     * @param endpoints of destination webservices
     * @return result message from the first usable web service, exception if there is not usable webservice inside the address list
     */
    private Element seqWsInvoke(String operationName, Element payloadElement, List<EndpointNode> endpoints) throws SOAPException, Exception {


        Iterator<EndpointNode> endpointIterator = endpoints.iterator();
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        //MDAL mdal = MDAL.getInstance();
        while (endpointIterator.hasNext()) {
            // ConcreteOperationsMDALInterface comi = mdal.getConcreteOperationsMDAL();
            SOAPMessage result = null;

            try {
                EndpointNode e = endpointIterator.next();
                /**
                 * Cost patch - part1
                 */
//                ConcreteOperation co = comi.readConcreteOperation(e.getServiceId(), operationName);
//                comi.close();
//                Hashtable<OperationSLAConstraint, String> slaC = co.getSlaConstraints();
//                String scost = slaC.get(OperationSLAConstraint.COST);
//                if (getCost() == -1) {
//                    setCost(Float.valueOf(scost));
//                } else {
//                    setCost(getCost() + Float.valueOf(scost));
//                }
                if (getCost() == -1) {
                    setCost(e.getCost());
                } else {
                    setCost(getCost() + e.getCost());
                }
                /**
                 * Cost patch - part1 - end
                 */
                SOAPMessage request = messageFactory.createMessage();
                SOAPBody requestBody = request.getSOAPBody();
                SOAPElement element = soapFactory.createElement(payloadElement.getLocalName(), SERVICE_NAMESPACE_IDENTIFIER, e.getTargetNameSpace());
                SOAPElement payloadElementSoap = soapFactory.createElement(payloadElement);
                Iterator<SOAPElement> payloadElementIterator = payloadElementSoap.getChildElements();
                while (payloadElementIterator.hasNext()) {
                    SOAPElement elem = payloadElementIterator.next();
                    element.addChildElement(elem);
                }
                requestBody.addChildElement(element);
                SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
                SOAPConnection connection = factory.createConnection();

                long start = System.currentTimeMillis();
                result = connection.call(request, e.getEndpointURL() + "/" + operationName);
                connection.close();
                long end = System.currentTimeMillis();

                long responseTime;


                String serviceId = String.valueOf( e.getServiceId());
                if (result == null) {
                    responseTime = -1;
                } else {
                    responseTime = end - start;
                }
                MonitorPolicy mp = new MonitorPolicy();
                MonitorPolicyInterface mpi = mp.getMonitorPolicy();
                if (responseTime == -1) {
                    mpi.addData(processName, operationName, serviceId);
                } else {
                    mpi.addData(processName, operationName, serviceId, responseTime);
                }


                SOAPBody responseBody = result.getSOAPBody();
                if (responseBody.getFault() == null) {
                    return (Element) responseBody.getFirstChild();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
            }
        }
        System.out.println("------> Eccezione Disponibilita");
        throw new Exception("No usable services for this solution");
//        return null;
    }

    /*Parallel web service invoke
     * @param option with action already setted
     * @param payload message to send as parameter to destination webservices
     * @param addressElelements of destination webservices
     * @return result message from the first usable web service, exception if there is not usable webservice inside the address list
     */
    private synchronized Element parWsInvoke(String operationName, Element payloadElement, List<EndpointNode> endpoints) throws SOAPException, InterruptedException, Exception {

        EndpointNode e = null;
        MessageFactory messageFactory = MessageFactory.newInstance();
        this.childs = endpoints.size();
//        MDAL mdal = MDAL.getInstance();

        for (int i = 0; i < endpoints.size(); i++) {
            e = endpoints.get(i);

            /**
             * Cost patch - part2
             */
//           ConcreteOperationsMDALInterface comi = mdal.getConcreteOperationsMDAL();
//            ConcreteOperation co = comi.readConcreteOperation(e.getServiceId(), operationName);
//            comi.close();
//            Hashtable<OperationSLAConstraint, String> slaC = co.getSlaConstraints();
//            String scost = slaC.get(OperationSLAConstraint.COST);
//            if (getCost() == -1) {
//                setCost(Float.valueOf(scost));
//            } else {
//                setCost(getCost() + Float.valueOf(scost));
//            }
            if (getCost() == -1) {
                setCost(e.getCost());
            } else {
                setCost(getCost() + e.getCost());
            }
            /**
             * Cost patch - part2 - end
             */
            SOAPMessage request = messageFactory.createMessage();
            SOAPBody requestBody = request.getSOAPBody();
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            SOAPElement element = soapFactory.createElement(payloadElement.getLocalName(), SERVICE_NAMESPACE_IDENTIFIER, e.getTargetNameSpace());
            SOAPElement payloadElementSoap = soapFactory.createElement(payloadElement);
            Iterator<SOAPElement> payloadElementIterator = payloadElementSoap.getChildElements();
            while (payloadElementIterator.hasNext()) {
                SOAPElement elem = payloadElementIterator.next();
                element.addChildElement(elem);
            }
            requestBody.addChildElement(element);
            WSClientThread client = new WSClientThread(e.getEndpointURL() + "/" + operationName, request, this);

            String serviceId = String.valueOf(e.getServiceId());
            client.setProcessName(processName);
            client.setOperationName(operationName);
            client.setServiceName(serviceId);
            client.start();
        }
        this.wait();
        if (parallelResult == null) {
            //Logger.getLogger(Proxy.class.getName()).log(Level.SEVERE, null, "Eccezione Disponibilita");
            System.out.println("------> Eccezione Disponibilita");
            throw new Exception("No usable services for this solution");
        }
        //System.out.println("Dead childs: " + deadChilds);
        return parallelResult;
    }

    public Node invoke(Node source) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, BadArgumentsMDALException, NoSuchSolutionMDALException, MDALException, SOAPException, InterruptedException, Exception {
//        System.out.println("----> Sono il proxy!!!");

        Element processClassElement = (Element) source.getFirstChild();                     //QOS
        Element processNameElement = (Element) processClassElement.getNextSibling();        //ProcessName
        Element payloadElement = (Element) processNameElement.getNextSibling();             //Payload
        Element serviceNameElement = (Element) payloadElement.getNextSibling();             //ServiceName
        Element operationNameElement = (Element) serviceNameElement.getNextSibling();       //OperationName
        Element clientIdentifierElement = (Element) operationNameElement.getNextSibling();
        Element costElement = (Element) clientIdentifierElement.getNextSibling();

        String processClass = processClassElement.getTextContent();
        String serviceName = serviceNameElement.getTextContent();
        String operationName = operationNameElement.getTextContent();
        String processName = processNameElement.getTextContent();
        this.processName = processName;
        this.operationName = operationName;
        String clientIdentifier = clientIdentifierElement.getTextContent();
        String cost = costElement.getTextContent();

        System.out.println("------> Proxy clientIdentifier: " + clientIdentifier);
        MDAL mdal = MDAL.getInstance();
        /**
         * The follow 3 lines work with the first MDAL
         */
        //ProcessesMDALInterface pmi = mdal.getProcessesMDAL();
        //List<GroupNode> solution = pmi.getSolution(processName, processClass, serviceName, operationName);
        //pmi.close();
        /**
         * The follow 3 lines work with MDAL2
         */
        Date d1 = new Date();
        SolutionsMDALInterface smi = mdal.getSolutionsMDAL();
        List<GroupNode> solution = smi.readSolution(processName, processClass, serviceName, operationName, clientIdentifier);
        smi.close();
        Date d2 = new Date();
//        Random r1 = new Random();
//        int rand = r1.nextInt();
        System.out.println("Tempo lettura soluzione: " + (d2.getTime() - d1.getTime()));
        SOAPElement result = null;

        Random r = new Random();
        float f = r.nextFloat();
        float counter = 0;
        Iterator<GroupNode> solutionIterator = solution.iterator();
        while (solutionIterator.hasNext()) {
            GroupNode g = solutionIterator.next();
            counter += g.getProbability();
            if (counter >= f) { //Occorre inserire un break dopo il result altrimenti considero anche gli altri gruppi sono interessato all'esecuzione di un solo gruppo con una certa probabilita'
                if (g.getType().equals(g.getType().ALTERNATE)) {
                    d1 = new Date();
                    result = (SOAPElement) seqWsInvoke(operationName, payloadElement, g.getEndpoints());
                    d2 = new Date();
                    System.out.println("Tempo invocazione seq: " + (d2.getTime() - d1.getTime()));

                } else if (g.getType().equals(g.getType().PARALLEL_OR)) {
                    d1 = new Date();
                    result = (SOAPElement) parWsInvoke(operationName, payloadElement, g.getEndpoints());
                    d2 = new Date();
                    System.out.println("Tempo invocazione par: " + (d2.getTime() - d1.getTime()));
                }
                break;
            }
        }

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        SOAPMessage request = messageFactory.createMessage();
        SOAPBody requestBody = request.getSOAPBody();
        SOAPElement invokeRes = soapFactory.createElement("invokeResponse", SCHEMA_NAMESPACE_IDENTIFIER, SCHEMA_NAMESPACE);

        SOAPElement resultElement = soapFactory.createElement("result", SCHEMA_NAMESPACE_IDENTIFIER, SCHEMA_NAMESPACE);

        if (result == null) {
            System.out.println("!!!!!!!!!!!!!Problemi!!!!!!!!!!!");
        }
        Iterator<SOAPElement> payloadElementIterator = result.getChildElements();
        while (payloadElementIterator.hasNext()) {
            SOAPElement elem = payloadElementIterator.next();
            resultElement.addChildElement(elem);
        }


        SOAPElement clientID = soapFactory.createElement(clientIdentifierElement);
        clientID.setTextContent("");
        SOAPElement retCost = soapFactory.createElement(costElement);
        retCost.setTextContent((String.valueOf(Float.valueOf(cost) + getCost())));


        invokeRes.addChildElement(resultElement);
        invokeRes.addChildElement(clientID);
        invokeRes.addChildElement(retCost);
        requestBody.addChildElement(invokeRes);
        return (Node) requestBody.getFirstChild();
    }
}
