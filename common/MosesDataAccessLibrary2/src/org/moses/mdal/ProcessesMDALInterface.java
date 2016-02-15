/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.util.Hashtable;
import java.util.List;
import org.moses.entity.AbstractService;
import org.moses.entity.Process;
import org.moses.exception.AbstractOperationAlreadyExistsMDALException;
import org.moses.exception.AbstractServiceAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.exception.NoSuchProcessMDALException;
import org.moses.exception.ProcessAlreadyExistsMDALException;

/**
 *
 * @author francesco
 */
public interface ProcessesMDALInterface {

    /**
     * Given a process, adds it to repository
     * @param p
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */
    public void createProcess(Process p) throws BadArgumentsMDALException, ProcessAlreadyExistsMDALException, AbstractServiceAlreadyExistsMDALException, AbstractOperationAlreadyExistsMDALException, MDALException;

    /**
     *
     * @param processName
     * @return
     * @throws BadArgumentsMDALException
     * @throws NoSuchProcessMDALException
     * @throws MDALException
     */
    public Process readProcess(String processName)  throws BadArgumentsMDALException, NoSuchProcessMDALException, MDALException;

    /**
     * Update a process
     * @param p
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */

    public List<Process> readAllProcesses()  throws BadArgumentsMDALException, NoSuchAbstractServiceMDALException, MDALException;

    public Hashtable<Process, Float> readProcessesByAbSer(AbstractService aser)  throws BadArgumentsMDALException, NoSuchAbstractServiceMDALException, MDALException;

    public int updateProcess(Process p) throws BadArgumentsMDALException, MDALException;

    /**
     * Deletes a process (including all classes) with "cascade" on other modules
     * @param processName
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    public int deleteProcess(String processName) throws BadArgumentsMDALException, MDALException;

    /**
     * Given a process name, returns a list of classes about that process
     * @param processName
     * @return
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<String> getProcessClasses(String processName) throws BadArgumentsMDALException, NoSuchProcessClassMDALException, MDALException;

    /**
     * Given a process name and a process class returns the process object
     * @param processName
     * @param processClass
     * @return
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    //public Process getProcess(String processName, String processClass) throws BadArgumentsMDALException, NoSuchProcessMDALException, MDALException;

    /**
     * Get all processes with given process name
     * @param processName
     * @return A list of processes
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<Process> getProcesses(String processName) throws BadArgumentsMDALException, NoSuchProcessMDALException, MDALException;

    /**
     * Get all process names
     * @return A list of process names
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<String> getProcessNames() throws NoSuchProcessMDALException, MDALException;

    /**
     * Get all processes
     * @return A list of processes
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessClassMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<Process> getProcesses() throws NoSuchProcessMDALException, BadArgumentsMDALException, NoSuchProcessClassMDALException, MDALException;

    /**
     * Deletes a process class with "cascade" on other modules
     * @param processName
     * @param processClass
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    //public void deleteProcessClass(String processName, String processClass) throws BadArgumentsMDALException, NoSuchProcessClassMDALException, MDALException;

    /**
     * Given a process name, a class, a service name and an operation name, returns the stored solution
     * @param processName
     * @param processClass
     * @param serviceName
     * @param operationName
     * @return
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchSolutionMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<GroupNode> getSolution(String processName, String processClass, String serviceName, String operationName) throws BadArgumentsMDALException, NoSuchSolutionMDALException, MDALException;

    /**
     * updates a solution, if present. If not present, creates the solution.
     * @param processName
     * @param processClass
     * @param services
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */
    //public void updateSolution(String processName, String processClass, List<ServiceNode> services) throws BadArgumentsMDALException, NoSuchProcessMDALException, NoSuchProcessClassMDALException, MDALException;

    /**
     * Updates the solution for given processName, processClass, services. If some write error occurs, new solution isn't written and old solution is lost
     * @param processName
     * @param processClass
     * @return
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */
    //public boolean existProcess(String processName, String processClass) throws BadArgumentsMDALException, MDALException;

    /**
     * Closes connection to backend
     * @throws org.moses.exception.MDALException
     */
    public void close() throws MDALException;
}
