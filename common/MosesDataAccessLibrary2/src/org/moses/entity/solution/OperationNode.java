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
public class OperationNode {

    private String operationName = null;
    private List<GroupNode> groups = null;

    public List<GroupNode> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupNode> groups) {
        this.groups = groups;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

}
