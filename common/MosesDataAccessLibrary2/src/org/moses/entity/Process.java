/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.util.Hashtable;
import java.util.List;
import org.moses.mdal.AbstractServicesMDALInterface;
import org.moses.mdal.MDAL;
import org.moses.mdal.SLAsMDALInterface;

/**
 *
 * @author francesco
 */
public class Process {

    private String processName;
    private String processNS;
    private String processOperation;
    private String processGraph;
    private String processEndpoint;
    private boolean stateful;

    /*Process paths and subpaths*/
    private ProcessPath[] processPaths;

    private Hashtable<AbstractService, Float> services;
    private List<SLA> slas;

    public Process() {
        this.processName = null;
        this.processNS = null;
        this.processOperation = null;
        this.processGraph = null;
        this.processEndpoint = null;
        this.stateful = false;
        this.services = null;
        this.slas = null;

    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessGraph() {
        return processGraph;
    }

    public void setProcessGraph(String processGraph) {
        this.processGraph = processGraph;
    }

    public String getProcessNS() {
        return processNS;
    }

    public void setProcessNS(String processNS) {
        this.processNS = processNS;
    }

    public String getProcessOperation() {
        return processOperation;
    }

    public void setProcessOperation(String processOperation) {
        this.processOperation = processOperation;
    }

    public String getProcessEndpoints() {
        return processEndpoint;
    }

    public void setProcessEndpoints(String processEndpoint) {
        this.processEndpoint = processEndpoint;
    }

    public boolean isStateful() {
        return stateful;
    }

    public boolean getStateful() {
        return isStateful();
    }

    public void setStateful(String stateful) {
        this.stateful = Boolean.parseBoolean(stateful);
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }

    public Hashtable<AbstractService, Float> getServices() {
        if (services == null) {      //Lazy Loading. cannot be null
            try {
                MDAL mdal = MDAL.getInstance();
                AbstractServicesMDALInterface ami = mdal.getAbstractServicesMDAL();
                services = ami.readAbstractServicesByPr(this);
                ami.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return services;
    }

    public void setServices(Hashtable<AbstractService, Float> services) {
        this.services = services;
    }

    public List<SLA> getSlas() {
        if (slas == null) {      //Lazy Loading. cannot be null
            try {
                MDAL mdal = MDAL.getInstance();
                SLAsMDALInterface smi = mdal.getSLAsMDAL();
                slas = smi.readSLAsByPr(this);
                smi.close();
            } catch (Exception e) {
            }
        }
        return slas;
    }

    public void setSlas(List<SLA> slas) {
        this.slas = slas;
    }

    public ProcessPath[] getProcessPaths() {
        return processPaths;
    }

    public void setProcessPaths(ProcessPath[] processPaths) {
        this.processPaths = processPaths;
    }

  
}
