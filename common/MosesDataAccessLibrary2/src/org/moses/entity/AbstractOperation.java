/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.util.List;
import org.moses.mdal.ConcreteOperationsMDALInterface;
import org.moses.mdal.MDAL;

/**
 *
 * @author dante
 */
public class AbstractOperation {

    private String name;
    private AbstractService service;
    private List<ConcreteOperation> operations;

    public AbstractOperation() {
        this.name = null;
        this.service = null;
        this.operations = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractService getService() {   //we always have an AbstractService Object in an AbstractOperation because it is necessary for the identification of the operation. Lazy not possible
        return service;
    }

    public void setService(AbstractService service) {
        this.service = service;
    }

    public List<ConcreteOperation> getOperations() {
        if (operations == null) {     //Lazy Loading.
            try {
                MDAL mdal = MDAL.getInstance();
                ConcreteOperationsMDALInterface comi = mdal.getConcreteOperationsMDAL();
                operations = comi.readConcreteOperationsByAbOp(this);
                comi.close();
            } catch (Exception e) {
            }
        }
        return operations;
    }

    public void setOperations(List<ConcreteOperation> operations) {
        this.operations = operations;
    }
}
