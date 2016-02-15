/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.sla_manager.client;

/**
 *
 * @author francesco
 */
public class OptimizationEngineClient {

    public enum ClientType {

        EXIST, SLA_CREATION, SLA_DELETION
    };
    public static final String CONFIG_FILE = "slamanager.properties";
    public static final String OPTIMIZATION_ENGINE_EXIST_PROCESS_SOLUTION_OPERATION = "OptimizationEngine.existProcessSolution";
    public static final String OPTIMIZATION_ENGINE_CALCULATE_SOLUTION_FOR_SLA_CREATION_OPERATION = "OptimizationEngine.calculateProcessSolutionForSLACreation";
    public static final String OPTIMIZATION_ENGINE_CALCULATE_SOLUTION_FOR_SLA_DELETION_OPERATION = "OptimizationEngine.calculateProcessSolutionForSLADeletion";

    public static Boolean calculateProcessSolution(String processName, String processClass, Double rate, ClientType type) throws Exception {
        /*FileInputStream fis = new FileInputStream(CONFIG_FILE);
        Properties properties = new Properties();
        properties.load(fis);
        fis.close();

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPFactory soapFactory = SOAPFactory.newInstance();

        /*String optimizationEngineNamespace = properties.getProperty(targetNamespace);
        String optimizationEngineEndpointURL = properties.getProperty(endpointURL);*/
        /*String optimizationEngineNamespace = targetNamespace;
        String optimizationEngineEndpointURL = endpointURL;
        String optimizationEngineOperationName = null;*/
        java.lang.Boolean result = false;

        if (type.equals(ClientType.EXIST)) {
            try { // Call Web Service Operation

                org.moses.optimization_engine.OptimizationEngineService service = new org.moses.optimization_engine.OptimizationEngineService();
                org.moses.optimization_engine.OptimizationEngine port = service.getOptimizationEnginePort();

                result = port.existProcessSolution(processName, processClass);
                //System.out.println("Result = "+result);
            } catch (Exception ex) {
                // TODO handle custom exceptions here
            }
            //optimizationEngineOperationName = properties.getProperty(OPTIMIZATION_ENGINE_EXIST_PROCESS_SOLUTION_OPERATION);
        } else if (type.equals(ClientType.SLA_CREATION)) {
            try { // Call Web Service Operation
                org.moses.optimization_engine.OptimizationEngineService service = new org.moses.optimization_engine.OptimizationEngineService();
                org.moses.optimization_engine.OptimizationEngine port = service.getOptimizationEnginePort();

                result = port.calculateProcessSolutionForSLACreation(processName, processClass, rate);
                //System.out.println("Result = "+result);
            } catch (Exception ex) {
                // TODO handle custom exceptions here
            }

            //optimizationEngineOperationName = properties.getProperty(OPTIMIZATION_ENGINE_CALCULATE_SOLUTION_FOR_SLA_CREATION_OPERATION);
        } else {
            try { // Call Web Service Operation
                org.moses.optimization_engine.OptimizationEngineService service = new org.moses.optimization_engine.OptimizationEngineService();
                org.moses.optimization_engine.OptimizationEngine port = service.getOptimizationEnginePort();

                result = port.calculateProcessSolutionForSLADeletion(processName, processClass, rate);
                //System.out.println("Result = "+result);
            } catch (Exception ex) {
                // TODO handle custom exceptions here
            }

            //optimizationEngineOperationName = properties.getProperty(OPTIMIZATION_ENGINE_CALCULATE_SOLUTION_FOR_SLA_DELETION_OPERATION);
        }

        /*SOAPMessage request = messageFactory.createMessage();
        SOAPBody requestBody = request.getSOAPBody();
        SOAPElement rootElement = null;

        if (type.equals(ClientType.EXIST)) {
            rootElement = soapFactory.createElement("existProcessSolution", "ns", optimizationEngineNamespace);
        } else if (type.equals(ClientType.SLA_CREATION)) {
            rootElement = soapFactory.createElement("calculateProcessSolutionForSLACreation", "ns", optimizationEngineNamespace);
        } else {
            rootElement = soapFactory.createElement("calculateProcessSolutionForSLADeletion", "ns", optimizationEngineNamespace);
        }

        SOAPElement child1 = soapFactory.createElement("processName", "ns", optimizationEngineNamespace);
        child1.setTextContent(processName);
        SOAPElement child2 = soapFactory.createElement("processClass", "ns", optimizationEngineNamespace);
        child2.setTextContent(processClass);

        rootElement.addChildElement(child1);
        rootElement.addChildElement(child2);
        requestBody.addChildElement(rootElement);

        SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = factory.createConnection();

        SOAPMessage result = null;
        result = connection.call(request, optimizationEngineEndpointURL);
        connection.close();

        Boolean ret = Boolean.valueOf(result.getSOAPBody().getFirstChild().getTextContent());
        */



        
        return result;

        //return ret;
    }
}
