/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity;

/**
 *
 * @author arale
 */
public class InvocationResult {
    private String serviceName;
    private String operationName;
    private String processName;
    private long responseTime;
    Boolean response;

    public InvocationResult(){
        /*this.operationName = "";
        this.processName = "";
        this.response = false;*/
    }

    //if response = true, responseTime must have a value
    public InvocationResult(String processName, String serviceName, String operationName, long responseTime){
        this.serviceName = serviceName;
        this.operationName = operationName;
        this.responseTime = responseTime;
        this.processName = processName;
        this.response = true;
    }

    //if no response, responseTime doesn't have value
    public InvocationResult(String processName, String serviceName, String operationName){
        this.serviceName = serviceName;
        this.operationName = operationName;
        this.processName = processName;
        this.response = false;
    }

    public String getProcessName(){
        return this.processName;
    }

    public String getOperationName(){
        return this.operationName;
    }

    public String getServiceName(){
        return this.serviceName;
    }

    public long getResponseTime(){
        return this.responseTime;
    }

    public Boolean getResponse(){
        return this.response;
    }
}
