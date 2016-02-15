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
public class GroupNode {

    public enum Type {

        PARALLEL_OR, ALTERNATE
    };
    private Integer groupId = null;
    private List<EndpointNode> endpoints = null;
    private Float probability = null;
    private Type type = null;

    public List<EndpointNode> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointNode> endpoints) {
        this.endpoints = endpoints;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Float getProbability() {
        return probability;
    }

    public void setProbability(Float probability) {
        this.probability = probability;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
