/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.util.Hashtable;
import java.util.List;
import org.moses.mdal.AgreementsMDALInterface;
import org.moses.mdal.Constraints.ProcessSLAConstraint;
import org.moses.mdal.Constraints.ProcessSLAMonitor;
import org.moses.mdal.MDAL;

/**
 *
 * @author francesco
 */
public class SLA {

    private String processClass;
    private Process process;
    private String slaFile;
    private List<Agreement> agreements;
    private Hashtable<ProcessSLAConstraint, String> slaConstraints;
    private Hashtable<ProcessSLAMonitor, String> slaMonitor;
    

    public SLA() {
        this.processClass = null;
        this.process = null;
        this.slaFile = null;
        this.agreements = null;
        this.slaConstraints = null;
        this.slaMonitor = null;
        
    }

    public String getProcessClass() {
        return processClass;
    }

    public void setProcessClass(String processClass) {
        this.processClass = processClass;
    }

    public Process getProcess() {       //Lazy Loading not necessary. process must be loaded
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public String getSlaFile() {
        return slaFile;
    }

    public void setSlaFile(String slaFile) {
        this.slaFile = slaFile;
    }

    public List<Agreement> getAgreements() {
        if (agreements == null) {       //Lazy Loading. agreements may be null
            try {
                MDAL mdal = MDAL.getInstance();
                AgreementsMDALInterface ami = mdal.getAgreementsMDAL();
                agreements = ami.readAgreementsBySla(this);
                ami.close();
            } catch (Exception e) {
            }
        }
        return agreements;
    }

    public void setAgreement(List<Agreement> agreements) {
        this.agreements = agreements;
    }

    public Hashtable<ProcessSLAConstraint, String> getSlaConstraints() {
        return slaConstraints;
    }

    public void setSlaConstraints(Hashtable<ProcessSLAConstraint, String> slaConstraints) {
        this.slaConstraints = slaConstraints;
    }

    public Hashtable<ProcessSLAMonitor, String> getSlaMonitor() {
        return slaMonitor;
    }

    public void setSlaMonitor(Hashtable<ProcessSLAMonitor, String> slaMonitor) {
        this.slaMonitor = slaMonitor;
    }
}
