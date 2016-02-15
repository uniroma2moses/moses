/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.wsmonitor;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ejb.Stateless;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.entity.Process;
import org.moses.mdal.MDAL;
import org.moses.mdal.ProcessesMDALInterface;
import org.moses.mdal.ConcreteServicesMDALInterface;
import org.moses.wsmonitor.client.OptimizationEngineClient;

/**
 *
 * @author dante
 */
@WebService()
@Stateless()
public class WSMonitor {
private static final String PID_FILE_NAME = "/WSMonitor.pid";
    private static final String WS_WAIT_TIME = "WSWaitTime";
    private static final String MODULE_PID_DIRECTORY = "ModulePidDirectory";
    private static final String GRAPH_NODE = "node";
    private static final String GRAPH_NODE_TYPE = "type";
    private static final String GRAPH_NODE_OPERATION_NAME = "operationName";
    private static final String GRAPH_NODE_TYPE_INVOKE = "invoke";
    private static final String WSMONITOR_START_ACTION = "startAction";
    private WSMonitorThread[] threadMonitor = null;
    private List<ConcreteService> servicesChanged = null;
    private static boolean serviceMonitorStarted = false;
    private static final String ENDPOINT_URL = "endpointURL";
    private static final String CONFIG_FILE = "wsmonitor.properties";

    /**
     * Se l'orchestrazione avviene tramite BPEL non occorre effettuare un auto-start
     */

    /**
     * Web service operation
     */
    @WebMethod(operationName = "start")
    public Boolean start() throws Exception{
        Integer waitTime = null;
        String pidFileName = null;
        String endpointURL = null;

        //Get the wait time from the properties file
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();

            waitTime = new Integer(properties.getProperty(WS_WAIT_TIME));
            pidFileName = properties.getProperty(MODULE_PID_DIRECTORY) + PID_FILE_NAME;
            endpointURL = properties.getProperty(ENDPOINT_URL);

        } catch (IOException ex) {
            return false;
        }

        if (serviceMonitorStarted) {
            System.out.println("WSMonitor" + " at " + endpointURL + ": service monitor already started");
            return false;
        }

        serviceMonitorStarted = true;

        this.servicesChanged = new ArrayList<ConcreteService>();

        //Create a temp file
        File pidFile = new File(pidFileName);
        System.out.println(pidFileName);

        if (!pidFile.exists()) {
            try {
                pidFile.createNewFile();
            } catch (IOException ex) {
            }
        }

        pidFile.deleteOnExit();

        try {
            do {
                this.servicesChanged.clear();

                MDAL mdal = MDAL.getInstance();
                ConcreteServicesMDALInterface servicesMDAL = mdal.getConcreteServicesMDAL();
                ProcessesMDALInterface processesMDAL = mdal.getProcessesMDAL();
                //Get a list of all services from the repository and inizialize the thread monitor pool
                List<ConcreteService> services = servicesMDAL.readAllConcreteServices();
                threadMonitor = new WSMonitorThread[services.size()];
                //Create, set and run the thread pool
                for (int i = 0; i < services.size(); i++) {
                    threadMonitor[i] = new WSMonitorThread();
                    threadMonitor[i].setService(services.get(i));
                    threadMonitor[i].setMonitor(this);
                    threadMonitor[i].start();
                }
                //Wait for the termination of the threads
                for (int i = 0; i < threadMonitor.length; i++) {
                    try {
                        threadMonitor[i].join();
                    } catch (InterruptedException ex) {
                    }
                }
                if (!this.servicesChanged.isEmpty()) {
                    List<String> processNames = new ArrayList<String>();
                    //Call the optimization engine
                    Iterator<Process> processIterator = processesMDAL.readAllProcesses().iterator();
                    while (processIterator.hasNext()) {
                        Process process = processIterator.next();
                        String processGraph = process.getProcessGraph();
                        SAXBuilder builder = new SAXBuilder();
                        Reader reader = new StringReader(processGraph);
                        Document document = builder.build(reader);
                        Element root = document.getRootElement();
                        Iterator<Element> nodesIterator = root.getChildren(GRAPH_NODE).iterator();
                        boolean checked = false;
                        while (nodesIterator.hasNext() && !checked) {
                            Element node = nodesIterator.next();
                            if (node.getAttribute(GRAPH_NODE_TYPE) != null && node.getAttributeValue(GRAPH_NODE_TYPE).equals(GRAPH_NODE_TYPE_INVOKE)) {
                                String operationName = node.getAttributeValue(GRAPH_NODE_OPERATION_NAME);
                                Iterator<ConcreteService> servicesIterator = this.servicesChanged.iterator();
                                while (servicesIterator.hasNext() && !checked) {
                                    ConcreteService service = servicesIterator.next();
                                    Iterator<ConcreteOperation> operationsIterator = service.getOperations().iterator();
                                    while (operationsIterator.hasNext() && !checked) {
                                        if (operationName.equals(operationsIterator.next().getOperation().getName())) {
                                            processNames.add(process.getProcessName());
                                            checked = true;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Iterator<String> processNamesIterator = processNames.iterator();
                    while (processNamesIterator.hasNext()) {
                        String processName = processNamesIterator.next();
                        Boolean ret = false;
                        System.out.println("Select OE");
                        System.out.println("OE: " + "optimizationEngine.getEndpointURL()" + " " + "optimizationEngine.getTargetNamespace()");
                        System.out.println("WSMonitor - Call the Optimization Engine at " + "optimizationEngine.getEndpointURL()");
                        ret = OptimizationEngineClient.calculateProcessSolution(processName, "http://localhost:8080/OptimizationEngineService/OptimizationEngine", "http://optimization_engine.moses.org/");
                        System.out.println("WSMonitor - Called the Optimization Engine at " + "optimizationEngine.getEndpointURL()" + "res = " + ret);
                        if (!ret) {
                            //Problem: the process will always fail
                        }
                    }
                }

                servicesMDAL.close();
                processesMDAL.close();

                //Wait for "waitTime" millis
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {
                }
            } while (serviceMonitorStarted);
        } catch (Exception ex) {
            pidFile.delete();
            ex.printStackTrace();
            throw new Exception(ex.getClass().getName() + ":" + ex.getMessage());
        }

        return true;
    }

    @WebMethod(operationName = "stop")
    public Boolean stop() {
        String pidFileName = null;
        //Get the wait time from the properties file
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();
            pidFileName = properties.getProperty(MODULE_PID_DIRECTORY) + PID_FILE_NAME;
        } catch (IOException ex) {
            return new Boolean(false);
        }
        File pidFile = new File(pidFileName);
        pidFile.delete();
        serviceMonitorStarted = false;

        return true;
    }

    protected synchronized void addServiceChanged(ConcreteService service) {
        this.servicesChanged.add(service);
    }

}
