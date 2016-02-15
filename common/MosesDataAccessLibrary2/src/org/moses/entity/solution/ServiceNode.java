/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity.solution;

import java.util.List;

/**
 *
 * @author stefano
 */
public class ServiceNode {

    private String serviceName = null;
    private List<OperationNode> operations = null;

    public List<OperationNode> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationNode> operations) {
        this.operations = operations;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
