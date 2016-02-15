/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.io.IOException;
import java.util.Vector;
import org.moses.entity.InvocationStatistics;
import org.moses.exception.MDALException;
import org.moses.mdal.monitor.ViolationResult;

/**
 *
 * @author arale
 */
public interface MonitorMDALInterface {
    public static final String RELIABILITY = "RELIABILITY";
    public static final String RESPONSE_TIME = "RESPONSE_TIME";
    
    public abstract int removeData() throws IOException;

    public abstract void addLogData(Vector<InvocationStatistics> toAdd, int interval);

    public void addViolationData(Vector<ViolationResult> toAdd);

    public Vector<ViolationResult> getViolationData(int begin, int end);

    public Vector<ViolationResult> getViolationData(String processName, String serviceName, String operationName, int begin, int end);

    public Vector<ViolationResult> getViolationData(String processName, int begin, int end);

    public void close() throws MDALException;
}
