/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.util.Date;
import java.util.Hashtable;
import org.moses.mdal.Constraints.ClientSLAConstraint;
import org.moses.mdal.Constraints.ClientSLAMonitor;

/**
 *
 * @author dante
 */
public class Agreement {

    private Date expireDate;
    private Double arrivalRate;
    private User user;
    private SLA sla;
    private Hashtable<ClientSLAConstraint, String> slaConstraints;
    private Hashtable<ClientSLAMonitor, String> slaMonitor;

    public Agreement() {
        this.expireDate = null;
        this.arrivalRate = 0.0;
        this.sla = null;
        this.slaConstraints = null;
        this.slaMonitor = null;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Double getArrivalRate() {
        return arrivalRate;
    }

    public void setArrivalRate(Double arrivalRate) {
        this.arrivalRate = arrivalRate;
    }

    public void setUsername(Double arrivalRate) {
        this.arrivalRate = arrivalRate;
    }

    public User getUser() {     //Lazy Loading not necessary. user cannot be null because it is part of primary key
        return user;
    }

    public void setUsername(User user) {
        this.user = user;
    }

    public SLA getSla() {       //Lazy Loading not necessary. sla cannot be null because it is part of primary key
        return sla;
    }

    public void setSla(SLA sla) {
        this.sla = sla;
    }

    public Hashtable<ClientSLAConstraint, String> getSlaConstraints() {
        return slaConstraints;
    }

    public void setSlaConstraints(Hashtable<ClientSLAConstraint, String> slaConstraints) {
        this.slaConstraints = slaConstraints;
    }

    public Hashtable<ClientSLAMonitor, String> getSlaMonitor() {
        return slaMonitor;
    }

    public void setSlaMonitor(Hashtable<ClientSLAMonitor, String> slaMonitor) {
        this.slaMonitor = slaMonitor;
    }
}
