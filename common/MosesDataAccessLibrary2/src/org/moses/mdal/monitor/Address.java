/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal.monitor;

/**
 *
 * @author arale
 */
public class Address {
    private String address;
    private String port;

    public Address(String address, String port){
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }
}
