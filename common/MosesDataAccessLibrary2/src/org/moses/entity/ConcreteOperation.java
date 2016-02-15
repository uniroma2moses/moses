/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.util.Hashtable;
import org.moses.mdal.Constraints.OperationSLAConstraint;
import org.moses.mdal.Constraints.OperationSLAMonitor;

/**
 *
 * @author francesco
 */
public class ConcreteOperation {

    private ConcreteService service;
    private AbstractOperation operation;
    private Boolean isWorking;
    private String slaFile;
    private boolean stateful;
    private Double reliability;
    private Double responseTime;
    private Double cost;
    private Double requestRate;
    private Double probability;
    //private Hashtable<OperationSLAConstraint, String> slaConstraints;
    private Hashtable<OperationSLAMonitor, String> slaMonitor;

    public ConcreteOperation() {
        this.service = null;
        this.operation = null;
        this.isWorking = null;
        this.slaMonitor = null;

        this.reliability = null;
        this.responseTime = null;
        this.cost = null;
        this.requestRate = null;

        this.slaFile = null;
        this.stateful = false;

        this.probability = null;
    }

    public ConcreteService getService() {            // Lazy loading not necessary. service cannot be null (part of primary key)
        return service;
    }

    public void setService(ConcreteService service) {
        this.service = service;
    }

    public AbstractOperation getOperation() {       // Lazy loading not necessary. operation cannot be null (part of primary key)
        return operation;
    }

    public void setOperation(AbstractOperation operation) {
        this.operation = operation;
    }

    public Boolean getIsWorking() {
        return isWorking;
    }

    public void setIsWorking(Boolean isWorking) {
        this.isWorking = isWorking;
    }

    public void setIsWorking(String s) {
        this.isWorking = new Boolean(s);
    }

    public String getSlaFile() {
        return slaFile;
    }

    public void setSlaFile(String slaFile) {
        this.slaFile = slaFile;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getReliability() {
        return reliability;
    }

    public void setReliability(Double reliability) {
        this.reliability = reliability;
    }

    public Double getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(Double requestRate) {
        this.requestRate = requestRate;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }

    public void setSlaConstraints(Hashtable<OperationSLAConstraint, String> c) {
        setCost(Double.valueOf(c.get(OperationSLAConstraint.COST)));
        setReliability(Double.valueOf(c.get(OperationSLAConstraint.RELIABILITY)));
        setRequestRate(Double.valueOf(c.get(OperationSLAConstraint.REQUEST_RATE)));
        setResponseTime(Double.valueOf(c.get(OperationSLAConstraint.RESPONSE_TIME)));
    }

    public Hashtable<OperationSLAConstraint, String> getSlaConstraints() {
        Hashtable<OperationSLAConstraint, String> c = new Hashtable<OperationSLAConstraint, String>();
        c.put(OperationSLAConstraint.COST, String.valueOf(getCost()));
        c.put(OperationSLAConstraint.RELIABILITY, String.valueOf(getReliability()));
        c.put(OperationSLAConstraint.REQUEST_RATE, String.valueOf(getRequestRate()));
        c.put(OperationSLAConstraint.RESPONSE_TIME, String.valueOf(getResponseTime()));
        return c;
    }

    public Hashtable<OperationSLAMonitor, String> getSlaMonitor() {
        return slaMonitor;
    }

    public void setSlaMonitor(Hashtable<OperationSLAMonitor, String> slaMonitor) {
        this.slaMonitor = slaMonitor;
    }

    public boolean isStateful() {
        return stateful;
    }

    public boolean getStateful() {
        return isStateful();
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }

    public void setStateful(String stateful) {
        this.stateful = Boolean.parseBoolean(stateful);
    }

    public void setLoadProbability(Double probability) {
        this.probability = probability;
    }

    public Double getLoadProbability() {
        return probability;
    }
}
