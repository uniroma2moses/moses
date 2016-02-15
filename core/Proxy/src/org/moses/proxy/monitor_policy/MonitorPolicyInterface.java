/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.proxy.monitor_policy;

/**
 *
 * @author arale
 */
public interface MonitorPolicyInterface {
    
     public void addData(String processName, String operationName, String serviceName, long responseTime) ;
     
     public void addData(String processName, String operationName, String serviceName) ;

}