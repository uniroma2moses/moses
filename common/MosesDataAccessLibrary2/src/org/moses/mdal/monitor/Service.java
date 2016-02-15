/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal.monitor;

/**
 *
 * @author arale
 */
public class Service {
    private String serviceName;
    private String operationName;
    private String processName;

    public Service(String processName, String serviceName, String operationName){
        this.processName = processName;
        this.serviceName = serviceName;
        this.operationName = operationName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setOperationName(String operationName){
        this.operationName = operationName;
    }

    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    public String getServiceName(){
        return this.serviceName;
    }

    public String getOperationName(){
        return this.operationName;
    }

    public String getProcessName() {
        return processName;
    }
}
