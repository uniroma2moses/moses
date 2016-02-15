/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.util.List;
import org.moses.entity.SLA;
import org.moses.entity.Process;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAgreementMDALException;
import org.moses.exception.NoSuchProcessMDALException;
import org.moses.exception.NoSuchSLAMDALException;
import org.moses.exception.NoSuchUserMDALException;
import org.moses.exception.SLAAlreadyExistsMDALException;

/**
 * Interface with the CRUD actions to manage SLA informations into the MOSES repository
 * @author francesco
 */
public interface SLAsMDALInterface {

    /**
     * Add a SLA to the repository
     * @param sla The SLA to add to the repository
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.SLAAlreadyExistsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.SLA
     */
    public int createSLA(SLA sla) throws BadArgumentsMDALException, SLAAlreadyExistsMDALException, MDALException;

    /**
     * Get a SLA from the repository with given process name, process class and username
     * @param processName The name of the process
     * @param processClass The QoS class of the process
     * @return The SLA for a process and a QoS level
     * @throws org.moses.exception.NoSuchUserMDALException
     * @throws org.moses.exception.NoSuchProcessClassMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.SLA
     */
    public SLA readSLA(String processName, String processClass) throws  NoSuchSLAMDALException,  NoSuchProcessMDALException, MDALException;

    public List<SLA> readSLAsByPr(Process p) throws NoSuchProcessMDALException, MDALException;

    /**
     * Update the SLA information into the repository
     * @param sla The SLA to update
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.NoSuchProcessClassMDALException
     * @throws org.moses.exception.NoSuchSLAMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.SLA
     */
    public int updateSLA(SLA sla) throws BadArgumentsMDALException, MDALException;

    /**
     * Delete a SLA from the repository with given process name, process class
     * @param processName The name of the process
     * @param processClass The QoS class of the process
     * @throws java.lang.Exception
     */
    public int deleteSLA(String processName, String processClass) throws MDALException;

    /**
     * Checking for the SLA existence
     * @param processName The name of the process
     * @param processClass The QoS class of the process
     * @param username The username of the user who should be subscribed the SLA
     * @return "true" if the SLA exists; "false" otherwise
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.NoSuchProcessClassMDALException
     * @throws org.moses.exception.MDALException
     */
    /*public boolean existSLA(String processName, String processClass, String username) throws NoSuchProcessMDALException,
            NoSuchProcessClassMDALException, MDALException;

    /**
     * Close the connection with the repository
     * @throws org.moses.exception.MDALException
     */
    public void close() throws MDALException;
}
