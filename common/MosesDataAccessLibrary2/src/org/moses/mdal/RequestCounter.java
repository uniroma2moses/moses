/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class RequestCounter {

    private static int counter = 0;
    private static String hostname = "";

    static {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            hostname = localMachine.getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(RequestCounter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static synchronized String getCounter() {
        return (++counter)+hostname;
    }
}
