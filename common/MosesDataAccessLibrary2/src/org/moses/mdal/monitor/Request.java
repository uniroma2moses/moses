/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal.monitor;

import java.io.Serializable;

/**
 *
 * @author arale
 */
public class Request implements Serializable {
    //It is possible to extend the class to add services we want to monitor (to duplicate monitor)
    private int nInterval;

    public Request(int nInterval){
        this.nInterval = nInterval;
    }

    public int getNInterval(){
        return this.nInterval;
    }

    public void setNInterval(int nInterval){
        this.nInterval = nInterval;
    }
}
