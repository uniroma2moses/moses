/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity;

import java.io.Serializable;

/**
 *
 * @author arale
 */
public class InvocationStatistics implements Serializable {
    private String processName;
    private String serviceName;
    private String operationName;
    private double avgResponseTime; //Average value
    private double vrcResponseTime; //Variance
    private double reliability;


    public InvocationStatistics(String processName, String serviceId, String operationName, double avgResponseTime, double vrcResponseTime, double reliability){
        this.processName = processName;
        this.serviceName = serviceId;
        this.operationName = operationName;
        this.avgResponseTime = avgResponseTime;
        this.vrcResponseTime = vrcResponseTime;
        this.reliability = reliability;
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

    public double getAvgResponseTime(){
        return this.avgResponseTime;
    }

    public double getVrcResponseTime(){
        return this.vrcResponseTime;
    }

    public double getReliability(){
        return this.reliability;
    }


 }
