/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.entity;

import java.util.Hashtable;
import java.util.List;
import org.moses.mdal.AbstractOperationsMDALInterface;
import org.moses.mdal.ConcreteServicesMDALInterface;
import org.moses.mdal.MDAL;
import org.moses.mdal.ProcessesMDALInterface;

/**
 *
 * @author dante
 */
public class AbstractService {

    private String name;
    private String nameSpace;
    private Hashtable<Process, Float> processes;
    private List<AbstractOperation> operations;
    private List<ConcreteService> services;

    public AbstractService() {
        this.name = null;
        this.nameSpace = null;
        this.processes = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameSpace() {
        return this.nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Hashtable<Process, Float> getProcesses() {
        if(processes == null) {     //Lazy Loading.
            try {
                MDAL mdal = MDAL.getInstance();
                ProcessesMDALInterface pmi = mdal.getProcessesMDAL();
                processes = pmi.readProcessesByAbSer(this);
                pmi.close();
            } catch(Exception e) {
            }
        }
        return processes;
    }

    public void setProcesses(Hashtable<Process, Float> processes) {
        this.processes = processes;
    }

    public List<AbstractOperation> getOperations() {
        if(operations == null) {     //Lazy Loading.
            try {
                MDAL mdal = MDAL.getInstance();
                AbstractOperationsMDALInterface ami = mdal.getAbstractOperationsMDAL();
                operations = ami.readAbstractOperationsByAbSer(this);
                ami.close();
            } catch(Exception e) {
            }
        }
        return operations;
    }

    public void setOperations(List<AbstractOperation> operations) {
        this.operations = operations;
    }

    public List<ConcreteService> getServices() {
        if(services == null) {     //Lazy Loading.
            try {
                MDAL mdal = MDAL.getInstance();
                ConcreteServicesMDALInterface cmi = mdal.getConcreteServicesMDAL();
                services = cmi.readConcreteServicesByAbServ(this);
                cmi.close();
            } catch(Exception e) {
            }
        }
        return services;
    }

    public void setServices(List<ConcreteService> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object service) {
        boolean ret = false;
        if (service instanceof AbstractService) {
            AbstractService app = (AbstractService) service;
            if(this.name.equals(app.getName()) && this.nameSpace.equals(app.getNameSpace()))
                ret = true;
        }
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 11 * hash + (this.nameSpace != null ? this.nameSpace.hashCode() : 0);
        return hash;
    }
}
