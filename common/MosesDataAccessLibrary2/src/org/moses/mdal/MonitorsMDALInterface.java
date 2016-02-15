/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.util.List;
import org.moses.entity.ConcreteService;
import org.moses.entity.Process;
import org.moses.entity.SLA;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractOperationMDALException;
import org.moses.exception.NoSuchAgreementMDALException;
import org.moses.exception.NoSuchProcessMDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;

/**
 * Interface with the CRUD actions to manage the monitoring informations into the MOSES repository
 * @author francesco
 */
public interface MonitorsMDALInterface {

    /**
     * Update the monitoring information about a service
     * @param service The service monitored
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchServiceMDALException
     * @throws org.moses.exception.NoSuchOperationMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    public void updateServiceMonitor(ConcreteService service) throws BadArgumentsMDALException, NoSuchAbstractServiceMDALException, NoSuchAbstractOperationMDALException, MDALException;

    /**
     * Get the monitoring informations about the service with given service name and ID
     * @param serviceName The name of the service
     * @param serviceID The ID of the service
     * @return The monitoring informations about a service with given service name and ID
     * @throws org.moses.exception.NoSuchOperationMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    public ConcreteService getServiceMonitor(String serviceName, Integer serviceID) throws NoSuchAbstractServiceMDALException, MDALException;

    /**
     * Get the monitoring informations about all services with the same service name
     * @param serviceName The name of the service monitored
     * @return A list of services monitored with the same service name
     * @throws org.moses.exception.NoSuchOperationMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    public List<ConcreteService> getServicesMonitor(String serviceName) throws NoSuchAbstractServiceMDALException, MDALException;

    /**
     * Get the monitoring informations about the process with given process name and process class
     * @param processName The name of the process
     * @param processClass The QoS class of the process
     * @return The monitoring informations about a Process with given process name and process class
     * @throws java.lang.Exception
     */
    public Process getProcessMonitor(String processName, String processClass) throws MDALException;

    /**
     * Updates monitored data for a given process
     * @param p
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessMDALException
     * @throws org.moses.exception.MDALException
     */
    public void updateProcessMonitor(Process p) throws BadArgumentsMDALException, NoSuchProcessMDALException, MDALException;

    /**
     * Get the client SLA informations for monitoring
     * @param username
     * @param processName
     * @param processClass
     * @return The client SLA
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.SLA
     */
    public SLA getClientSLAMonitor(String username, String processName, String processClass) throws MDALException;

    /**
     * Update the client SLA informations for monitoring
     * @param sla The SLA to be update
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchProcessClassMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.SLA
     */
    public void updateClientSLAMonitor(SLA sla) throws BadArgumentsMDALException, NoSuchAgreementMDALException, MDALException;

    /**
     * Close the connection with the repository
     * @throws java.lang.Exception
     */
    public void close() throws MDALException;
}
