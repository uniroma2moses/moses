/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.util.Hashtable;
import java.util.Vector;
import org.moses.entity.InvocationResult;
import org.moses.entity.InvocationStatistics;
import org.moses.exception.MDALException;

/**
 *
 * @author arale
 */
public interface QoSDataCollectionMDALInterface {
    
    public abstract int addData(String processName, String operationName, String serviceName, long responseTime) throws MDALException ;

    public abstract int addData(String processName, String operationName, String serviceName) throws MDALException ;

    public abstract int deleteResults() throws MDALException ;

    public abstract int deleteResults(String processName, String serviceName, String operationName) throws MDALException;

    public abstract Vector<InvocationResult> getInvocationResults(String processName, String serviceName, String operationName) throws MDALException;

    public abstract Vector<InvocationResult> getInvocationResults() throws MDALException ;

    public Vector<InvocationStatistics> getStatistics();
    
    public Hashtable<String, Integer> getNumerOfRequest();

    public void close() throws MDALException ;
}
