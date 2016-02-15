/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.proxy;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import org.moses.proxy.monitor_policy.MonitorPolicy;
import org.moses.proxy.monitor_policy.MonitorPolicyInterface;
import org.w3c.dom.Element;

/**
 *
 * @author dante
 */
public class WSClientThread extends Thread {

    private String endpoint = null;
    private SOAPMessage message = null;
    private Proxy caller = null;
        private String processName = null;
        private String serviceName = null;
    private String operationName = null;

    public WSClientThread(String endpoint, SOAPMessage message, Proxy caller) {
        this.endpoint = endpoint;
        this.message = message;
        this.caller = caller;
    }

       public void setProcessName(String processName){
        this.processName = processName;
    }
        public void setOperationName(String operationName){
        this.operationName = operationName;
    }

    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    @Override
    public void run() {
        super.run();
        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();


            long start = System.currentTimeMillis();
            SOAPMessage result = connection.call(message, endpoint);
            connection.close();
            long end = System.currentTimeMillis();
            long responseTime;

            if (result == null) {
                responseTime = -1;
            } else {
                responseTime = end - start;
            }

            MonitorPolicy mp = new MonitorPolicy();
            MonitorPolicyInterface mpi = mp.getMonitorPolicy();
            if (responseTime == -1) {
                mpi.addData(processName, operationName, serviceName);
            } else {
                mpi.addData(processName, operationName, serviceName, responseTime);
            }


            SOAPBody responseBody = result.getSOAPBody();
            if (responseBody.getFault() == null) {
                caller.setParallelResult((Element) responseBody.getFirstChild());
            }
        } catch (Exception ex) {
            caller.setLastException(ex);
        }
        caller.incrementDeadChilds();
    }
}
