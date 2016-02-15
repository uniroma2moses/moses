/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.wsmonitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.mdal.ConcreteOperationsMDALInterface;
import org.moses.mdal.MDAL;
import org.moses.wsmonitor.client.RemoteServiceClient;

/**
 *
 * @author francesco
 */
public class WSMonitorThread extends Thread {

    private ConcreteService service;
    private WSMonitor monitor;

    public WSMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(WSMonitor monitor) {
        this.monitor = monitor;
    }

    public ConcreteService getService() {
        return service;
    }

    public void setService(ConcreteService service) {
        this.service = service;
    }

    @Override
    public void run() {
        super.run();

        //Get the needed informations
        String endpointURL = this.service.getEndpointURL();
        List<ConcreteOperation> operations = this.service.getOperations();

        //Create a list of operations which had a change in the state
        List<ConcreteOperation> operationsChanged = new ArrayList<ConcreteOperation>();
        ConcreteOperationsMDALInterface operationsMDAL = null;
        try {
            MDAL mdal = MDAL.getInstance();
            operationsMDAL = mdal.getConcreteOperationsMDAL();
            //Check the expire date of the service
            if (false) {
                //Set all operations to "notWorking"
                Iterator<ConcreteOperation> operationsIterator = operations.iterator();
                while (operationsIterator.hasNext()) {
                    ConcreteOperation operation = operationsIterator.next();
                    //operation.setIsWorking(false);
                    if (operation.getIsWorking().booleanValue()) {
                        System.out.println("WSMonitor: service " + endpointURL + ", operation " + operation.getOperation().getName() + " EXPIRED");
                        operation.setIsWorking(false);
                        operationsMDAL.updateConcreteOperation(operation);
                        operationsChanged.add(operation);
                    }
                }
            } else {
                //Check all the operations
                Iterator<ConcreteOperation> operationsIterator = operations.iterator();
                while (operationsIterator.hasNext()) {
                    ConcreteOperation operation = operationsIterator.next();
                    String operationName = operation.getOperation().getName();
                    boolean result = RemoteServiceClient.test(endpointURL, operationName);
                    if (result) {
                        System.out.println("WSMonitor: service " + endpointURL + ", operation " + operationName + " ACTIVE");
                        if (!operation.getIsWorking().booleanValue()) {
                            operation.setIsWorking(true);
                            operationsChanged.add(operation);
                        }
                    } else {
                        System.out.println("WSMonitor: service " + endpointURL + ", operation " + operationName + " NOT ACTIVE");
                        if (operation.getIsWorking().booleanValue()) {
                            operation.setIsWorking(false);
                            operationsChanged.add(operation);
                        }
                    }
                    operationsMDAL.updateConcreteOperation(operation);
                }
            }

            if (!operationsChanged.isEmpty()) {
                //service.setOperations(operationsChanged);
                try {
                    //Notify the change of the service to WSMonitor
                    this.monitor.addServiceChanged(service);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                operationsMDAL.close();
            } catch (Exception e) {
            }
        }
    }
}
