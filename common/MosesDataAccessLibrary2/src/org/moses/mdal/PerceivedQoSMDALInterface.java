/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.util.Vector;
import org.moses.exception.MDALException;
import org.moses.mdal.monitor.ViolationResult;

/**
 *
 * @author arale
 */
public interface PerceivedQoSMDALInterface {
    public static final String RELIABILITY = "RELIABILITY";
    public static final String RESPONSE_TIME = "RESPONSE_TIME";

    public void addData(Vector<ViolationResult> vR) throws MDALException;

    public void addData(String processName, String serviceName, String operationName, double avgResponseTime, double reliability) throws MDALException;

    public abstract Vector<ViolationResult> getViolations() throws MDALException;

    public Vector<ViolationResult> getViolations(String processName) throws MDALException;

    public abstract int deleteEntry(String processName, String serviceName, String operationName) throws MDALException;

    public abstract int truncateTable() throws MDALException;
    
    public void close() throws MDALException ;

}
