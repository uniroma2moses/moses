/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.wsmonitor.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

/**
 *
 * @author francesco
 */
public class OptimizationEngineClient {

    public static final String CONFIG_FILE = "wsmonitor.properties";
    public static final String OPTIMIZATION_ENGINE_CALCULATE_SOLUTION_FOR_SERVICE_CHANGE_OPERATION = "OptimizationEngine.calculateProcessSolutionForServiceChange";

    public static Boolean calculateProcessSolution(String processName, String endpointURL, String targetNamespace) throws FileNotFoundException, IOException, Exception {
        /*FileInputStream fis = new FileInputStream(CONFIG_FILE);
        Properties properties = new Properties();
        properties.load(fis);
        fis.close();

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPFactory soapFactory = SOAPFactory.newInstance();

        String optimizationEngineOperationName = null;
        optimizationEngineOperationName = properties.getProperty(OPTIMIZATION_ENGINE_CALCULATE_SOLUTION_FOR_SERVICE_CHANGE_OPERATION);

        SOAPMessage request = messageFactory.createMessage();
        SOAPBody requestBody = request.getSOAPBody();
        SOAPElement rootElement = null;

        rootElement = soapFactory.createElement("calculateProcessSolutionForServiceChange", "ns", targetNamespace);
        SOAPElement child1 = soapFactory.createElement("processName", "ns", targetNamespace);
        child1.setTextContent(processName);

        rootElement.addChildElement(child1);
        requestBody.addChildElement(rootElement);

        SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = factory.createConnection();

        SOAPMessage result = null;

        result = connection.call(request, endpointURL);
        connection.close();

        Boolean ret = Boolean.valueOf(result.getSOAPBody().getFirstChild().getTextContent());*/

        java.lang.Boolean result = false;
        
        try { // Call Web Service Operation
            org.moses.optimization_engine.OptimizationEngineService service = new org.moses.optimization_engine.OptimizationEngineService();
            org.moses.optimization_engine.OptimizationEngine port = service.getOptimizationEnginePort();
            result = port.calculateProcessSolutionForServiceChange(processName);
            //System.out.println("Result = "+result);
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO handle custom exceptions here
        }


        /*try { // Call Web Service Operation
            org.moses.optimization_engine.OptimizationEngineService service = new org.moses.optimization_engine.OptimizationEngineService();
            org.moses.optimization_engine.OptimizationEngine port = service.getOptimizationEnginePort();
            // TODO initialize WS operation arguments here
            //java.lang.String processName = "";
            java.lang.String processClass = "";
            // TODO process result here
            java.lang.Boolean result1 = port.existProcessSolution(processName, processClass);
            System.out.println("Result = "+result1);
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }*/

        return result;
        //return ret;
    }
}
