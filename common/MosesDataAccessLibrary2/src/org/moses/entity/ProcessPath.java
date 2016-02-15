/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity;

import java.util.Hashtable;

/**
 *
 * @author stefano
 */
public class ProcessPath {

    private Process process;
    private double probability;
    private ProcessSubPath[] subPaths;
    Hashtable<AbstractService, Integer> multipleOcc;

    public ProcessPath()  {
        
    }
    /**
     * Creates an instance of a process path
     * @param p The referred Process
     * @param prob The probability of following this path during process execution
     */
    public ProcessPath(Process p, double prob) {
        this.probability = prob;
        this.process = p;
    }

    /**
     *
     * @return the probabiliy of following this path during process execution
     */
    public double getProbability() {
        return probability;
    }

    /**
     *
     * @return the referred process
     */
    public Process getProcess() {
        return process;
    }

    /**
     *
     * @return every sub-path belonging to this path
     */
    public ProcessSubPath[] getSubPaths() {
        return subPaths;
    }

    /**
     * Sets the sub-paths belonging to this path
     * @param subPaths
     */
    public void setSubPaths(ProcessSubPath[] subPaths) {
        this.subPaths = subPaths;
    }

    /**
     *
     * @return every sub-path belonging to this path
     */
    public Hashtable<AbstractService, Integer> getMultipleOcc() {
        return multipleOcc;
    }

    /**
     * Sets the sub-paths belonging to this path
     * @param subPaths
     */
    public void setMultipleOcc(Hashtable<AbstractService, Integer> multipleOcc) {
        this.multipleOcc = multipleOcc;
    }

}
