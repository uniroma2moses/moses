/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.proxy.monitor_policy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arale
 */
public class MonitorPolicy {
    private static final String CONFIG_FILE = "proxy_policy.properties";
    private static final String MONITOR_POLICY_CLASS = "monitorPolicyClass";
    private String monitorPolicyClass;

    public MonitorPolicy(){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(CONFIG_FILE);
            Properties p = new Properties();
            p.load(fis);
            fis.close();

            monitorPolicyClass = p.getProperty(MONITOR_POLICY_CLASS);
            
        } catch (IOException ex) {
            Logger.getLogger(MonitorPolicy.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(MonitorPolicy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    

   public MonitorPolicyInterface getMonitorPolicy() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        return(MonitorPolicyInterface) Class.forName(this.monitorPolicyClass).newInstance();
   }
}
