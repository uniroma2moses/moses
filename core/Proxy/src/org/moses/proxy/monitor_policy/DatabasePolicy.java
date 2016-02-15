/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.proxy.monitor_policy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.exception.MDALException;
import org.moses.mdal.MDAL;
import org.moses.mdal.QoSDataCollectionMDALInterface;

/**
 *
 * @author arale
 */
public class DatabasePolicy implements MonitorPolicyInterface {

    public void addData(String processName, String operationName, String serviceName, long responseTime) {
        try {
            MDAL mdal = MDAL.getInstance();
            QoSDataCollectionMDALInterface qosMDAL = (QoSDataCollectionMDALInterface) mdal.getQoSDataCollection();
            qosMDAL.addData(processName, operationName, serviceName, responseTime);
            qosMDAL.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MDALException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addData(String processName, String operationName, String serviceName) {
        try {
            MDAL mdal = MDAL.getInstance();
            QoSDataCollectionMDALInterface qosMDAL = (QoSDataCollectionMDALInterface) mdal.getQoSDataCollection();
            qosMDAL.addData(processName, operationName, serviceName);
            qosMDAL.close();
        } catch (MDALException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabasePolicy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
