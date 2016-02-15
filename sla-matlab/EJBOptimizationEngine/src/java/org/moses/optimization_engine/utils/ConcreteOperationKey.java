/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.optimization_engine.utils;

/**
 *
 * @author dante
 */
public class ConcreteOperationKey {

    private Integer serviceID;
    private String operationName;

    public ConcreteOperationKey(Integer serviceID, String operationName) {
        this.serviceID = serviceID;
        this.operationName = operationName;
    }

    public void setServiceID(Integer serviceID) {
        this.serviceID = serviceID;
    }

    public Integer getServiceID() {
        return this.serviceID;
    }

    public void setoperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getoperationName() {
        return this.operationName;
    }

}