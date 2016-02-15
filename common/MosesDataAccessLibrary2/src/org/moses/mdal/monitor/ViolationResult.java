package org.moses.mdal.monitor;

import java.util.Hashtable;

/**
 *
 * @author arale
 */
public class ViolationResult {
    private String processName;
    private String serviceName;
    private String operationName;
    private Hashtable<String, Double> violation; //Name of the violated parameter and his value
    /*double reliability;
    double responseTime;*/
    int interval;

    public ViolationResult(){
    }

    public ViolationResult(String processName, String serviceName, String operationName, int interval, Hashtable<String, Double> violation){
        this.processName = processName;
        this.serviceName = serviceName;
        this.operationName = operationName;
        this.interval = interval;
        this.violation = violation;
    }

//    public ViolationResult(String serviceName, String operationName, double reliability, double responseTime, int interval){
//        this.serviceName = serviceName;
//        this.operationName = operationName;
//        this.responseTime = responseTime;
//        this.reliability = reliability;
//        this.interval = interval;
//    }

    public void setProcessName(String processName){
        this.processName = processName;
    }

    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }


    public void setOperationName(String operationName){
        this.operationName = operationName;
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public void addViolation(String violName, double value){
        violation.put(violName, value);
    }

    public Hashtable<String, Double> getViolation() {
        return violation;
    }

//    public void setReliability(double reliability){
//        this.reliability = reliability;
//    }
//
//    public void setResponseTime(double responseTime){
//        this.responseTime = responseTime;
//    }
//
    public String getProcessName(){
        return this.processName;
    }

    public String getServiceName(){
        return this.serviceName;
    }

    public String getOperationName(){
        return this.operationName;
    }

    public int getInterval() {
        return this.interval;
    }

//    public double getReliability(){
//        return this.reliability;
//    }
//
//    public double getResponseTime(){
//        return this.responseTime;
//    }

}
