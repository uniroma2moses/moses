/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity.solution;

/**
 *
 * @author stefano
 */
public class EndpointNode {

    private Integer serviceId = null;
    private String targetNameSpace = null;
    private String endpointURL = null;
    private double cost = -1;

    public String getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getTargetNameSpace() {
        return targetNameSpace;
    }

    public void setTargetNameSpace(String targetNameSpace) {
        this.targetNameSpace = targetNameSpace;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }


    
}
