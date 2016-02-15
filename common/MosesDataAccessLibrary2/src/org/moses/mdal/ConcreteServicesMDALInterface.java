/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.util.List;
import org.moses.entity.AbstractService;
import org.moses.entity.ConcreteService;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.AbstractServiceAlreadyExistsMDALException;
import org.moses.exception.ConcreteOperationAlreadyExistsMDALException;
import org.moses.exception.ConcreteServiceAlreadyExistsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.exception.NoSuchConcreteServiceMDALException;

/**
 * Interface with the CRUD actions to manage Service informations into the MOSES repository
 * @author francesco
 */
public interface ConcreteServicesMDALInterface {


    /**
     * Add a service to the repository
     * @param service The service to be add to the repository
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.ServiceAlreadyExistsMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    public void createConcreteService(ConcreteService service) throws BadArgumentsMDALException, ConcreteServiceAlreadyExistsMDALException, ConcreteOperationAlreadyExistsMDALException, MDALException;

    /**
     * 
     * @param id
     * @return
     * @throws NoSuchServiceMDALException
     * @throws MDALException
     */
    public ConcreteService readConcreteService(int id) throws NoSuchConcreteServiceMDALException, MDALException;

    public List<ConcreteService> readConcreteServicesByAbServ(AbstractService aserv) throws NoSuchAbstractServiceMDALException, MDALException;

    public List<ConcreteService> readAllConcreteServices() throws NoSuchAbstractServiceMDALException, MDALException;

    public AbstractService readAbstractServiceByConServ(ConcreteService cserv) throws MDALException;

    /**
     * Update the informations about the service into the repository
     * @param service The service to be update
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.NoSuchServiceMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    public int updateConcreteService(ConcreteService service) throws BadArgumentsMDALException, MDALException;

    /**
     * Delete all service with same service name from the repository
     * @param serviceName The name of the service to delete
     * @throws org.moses.exception.MDALException
     */
    public int deleteConcreteService(int id) throws MDALException;

    /**
     * Get all services from the repository
     * @return A list of all services
     * @throws org.moses.exception.NoSuchServiceMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */

    //public List<ConcreteService> getAllServices() throws NoSuchServiceMDALException, MDALException;

    /**
     * Get all services with same service name from the repository
     * @param serviceName The name of the service
     * @return A list of services with same service name
     * @throws org.moses.exception.NoSuchServiceMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    //public List<ConcreteService> getServices(String serviceName) throws NoSuchServiceMDALException, MDALException;

    /**
     * Get the operation with given service name, operation name and operation id
     * @param serviceName The name of the service
     * @param operationName The name of the operation
     * @param operationID The id of the operation
     * @return The operation with given service name, operation name and operation id or "null"
     * @throws org.moses.exception.NoSuchServiceMDALException
     * @throws org.moses.exception.NoSuchOperationMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Operation
     */
    //public ConcreteOperation getOperation(String serviceName, String operationName, Integer operationID) throws NoSuchServiceMDALException, NoSuchOperationMDALException, MDALException;

    /**
     * Check if a service exists in the repository
     * @param service The service to be check
     * @return "true" if the service exists; "false" otherwise
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.ServiceAlreadyExistsMDALException
     * @throws org.moses.exception.MDALException
     * @see org.moses.entity.Service
     */
    //public boolean existService(ConcreteService service) throws BadArgumentsMDALException, ServiceAlreadyExistsMDALException, MDALException;

    /**
     * Delete the service with a given service name and a given ID
     * @param serviceName The name of the service
     * @param serviceID The ID of the service
     * @throws org.moses.exception.NoSuchServiceMDALException
     * @throws org.moses.exception.MDALException
     */
    //public void deleteService(String serviceName, Integer serviceID) throws NoSuchServiceMDALException, MDALException;

    /**
     * Close the connection with the repository
     * @throws org.moses.exception.MDALException
     */
    public void close() throws MDALException;
}
