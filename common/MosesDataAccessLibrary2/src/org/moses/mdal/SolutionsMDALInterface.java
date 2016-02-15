/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.util.Hashtable;
import java.util.List;
import org.moses.entity.solution.CacheHashKey;
import org.moses.entity.solution.GroupNode;
import org.moses.entity.solution.ServiceNode;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;

/**
 *
 * @author dante
 */
public interface SolutionsMDALInterface {

    void createSolutionTransaction(String processName, String processClass, List<ServiceNode> services, boolean withTransaction, String requestId) throws BadArgumentsMDALException, MDALException;
    void createSolution(String processName, String processClass, List<ServiceNode> services, String requestId) throws BadArgumentsMDALException, MDALException;

    int updateSolution(String processName, String processClass, List<ServiceNode> services, String requestId) throws BadArgumentsMDALException, MDALException;

    int deleteSolution(String processName, String processClass) throws BadArgumentsMDALException, MDALException;

    public List<GroupNode> readSolution(String processName, String processClass, String serviceName, String operationName, String requestId) throws MDALException;

    public void close() throws MDALException;
}
