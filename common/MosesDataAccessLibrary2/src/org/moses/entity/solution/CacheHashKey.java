/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity.solution;

/**
 *
 * @author stefano
 */
public class CacheHashKey {

    private String processName;
    private String processClass;
    private String serviceName;
    private String operationName;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getProcessClass() {
        return processClass;
    }

    public void setProcessClass(String processClass) {
        this.processClass = processClass;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    

}
