/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stefano
 */
public class ProcessSubPath {
    Process process = null;
    List<AbstractService> subPath = null;

    public ProcessSubPath() {
    }

    /**
     * Creates an instance of process sub-path. The process parameter is mandatory
     * @param p
     */
   public ProcessSubPath(Process p) {
       this.process = p;
       subPath = new ArrayList<AbstractService>();
   }

   /**
    * Creates an instance of process sub-path, given a Process p and a list of
    * abstract services that contitute the sub-path
    * @param p
    * @param subPath
    */
   public ProcessSubPath(Process p, List<AbstractService> subPath) {
       this.process = p;
       this.subPath = subPath;
   }

   /**
    *
    * @return a list of abstract services that constitute a sub-path
    */
   public List<AbstractService> getSubPath() {
        return subPath;
    }

   /**
    * Sets a subPath 
    * @param subPath
    */
    public void setSubPath(List<AbstractService> subPath) {
        this.subPath = subPath;
    }

   /**
    * Appends an activity (i.e. an abstract service) to the sub-path
    * @param s
    */
   public void addActivity(AbstractService s) {
       subPath.add(s);
   }

   /**
    * Adds an activity (i.e. an abstract service) to a given position in the sub-path
    * @param s
    * @param p
    */
   public void addActivity(AbstractService s, int p) {
       subPath.add(p, s);
   }

   /**
    * Removes an activity from the sub-path
    * @param s
    */
   public void removeActivity(AbstractService s) {
       subPath.remove(s);
   }





}
