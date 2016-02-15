/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.optimization_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.moses.entity.AbstractService;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.entity.InvocationResult;
import org.moses.exception.MDALException;
import org.moses.mdal.ConcreteOperationsMDALInterface;
import org.moses.mdal.MDAL;
import org.moses.mdal.PerceivedQoSMDALInterface;
import org.moses.mdal.ProcessesMDALInterface;
import org.moses.mdal.QoSDataCollectionMDALInterface;

/**
 *
 * @author root
 */
public class OptimizationThread extends Thread {

    private static final String CONFIG_FILE = "optimization.properties";
    private static final String QOSMONITOR_CONFIG = "QoSMonitor";
    private static final String QOSMONITOR_INTERVAL = "QoSMonitorInterval";
    private static final String PROCESS_NAME = "ProcessName";
    private static final long DEFAULT_SLEEP = 10000;
    MDAL mdal = null;
    OptimizationEngine e = null;
    FileInputStream configFile = null;
    Properties config = new Properties();
    int invocationNumber = 0;

    public OptimizationThread(OptimizationEngine e) {
        try {
            mdal = MDAL.getInstance();
            this.e = e;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                configFile = new FileInputStream(CONFIG_FILE);
                config.load(configFile);
                String qosMonitorConfig = config.getProperty(QOSMONITOR_CONFIG);
                String processName = config.getProperty(PROCESS_NAME);
                updateEstimatedLoad(processName);
                if (qosMonitorConfig.equals("1")) {
                    long interval = -1;
                    interval = Long.parseLong(config.getProperty(QOSMONITOR_INTERVAL));
                    configFile.close();
                    e.calculateProcessSolutionForQoSChange(processName);
                    PerceivedQoSMDALInterface pqmi = mdal.getPerceivedQoSMDAL();
                    pqmi.truncateTable();
                    pqmi.close();
                    Thread.sleep(interval);
                } else {
                    Thread.sleep(DEFAULT_SLEEP);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEstimatedLoad(String processName) throws InterruptedException {
        QoSDataCollectionMDALInterface qdcmi = null;
        ProcessesMDALInterface pmi = null;
        ConcreteOperationsMDALInterface copmi = null;
        try {
            //Getting the number of invocations
            qdcmi = mdal.getQoSDataCollection();
            int pastInvocationNumber = invocationNumber;
            Hashtable<String, Integer> numberOfRequests = qdcmi.getNumerOfRequest();
            int currentInvocationNumber = 0;
            if (!numberOfRequests.isEmpty()) {
                currentInvocationNumber = numberOfRequests.get(processName);
            }
            System.out.println("Current invocation number: " + currentInvocationNumber);
            invocationNumber = currentInvocationNumber;

            //If the counter has been resetted, we do the same here
            if (invocationNumber < pastInvocationNumber) {
                pastInvocationNumber = 0;
            }
            int invocationDuringPeriod = invocationNumber - pastInvocationNumber;
            long interval = -1;
            try {
                interval = Long.parseLong(config.getProperty(QOSMONITOR_INTERVAL));
            } catch (NumberFormatException ex) {
                interval = DEFAULT_SLEEP;
            }

            //invocationRate represents the number of req/sec to MOSES
            double invocationRate = (double) invocationDuringPeriod / (interval / 1000);
            System.out.append("Invocation rate: " + invocationRate);

            /* The invocation rate must be moltiplied by the average number
             * of visits for each single abstract task in order to compute
             * the load probability of every single concrete service
             */
            pmi = mdal.getProcessesMDAL();
            copmi = mdal.getConcreteOperationsMDAL();
            org.moses.entity.Process p = pmi.readProcess(processName);
            Hashtable<AbstractService, Float> averageVisit = p.getServices();
            Enumeration<AbstractService> aws = averageVisit.keys();
            while (aws.hasMoreElements()) {
                AbstractService app = aws.nextElement();
                Float visit = averageVisit.get(app);
                //Calcolo del tasso effettivo delle richieste verso un abstractService
                //che tiene conto del numero medio di visite effettuato da ogni richiesta
                Double effectiveRate = new Double(visit * invocationRate);
                List<ConcreteService> concreteWs = app.getServices();
                Iterator<ConcreteService> iter = concreteWs.iterator();

                while (iter.hasNext()) {
                    ConcreteService cws = iter.next();
                    List<ConcreteOperation> concreteOp = cws.getOperations();
                    //Assunzione importante su servizi e operazioni
                    ConcreteOperation cOp = concreteOp.iterator().next();
                    Double maxRate = cOp.getRequestRate();
                    if (maxRate > effectiveRate) {
                        cOp.setLoadProbability(1.0d);
                    } else {
                        cOp.setLoadProbability(maxRate / effectiveRate);
                    }
                    copmi.updateLoadProbabilty(cOp);
                }
            }

        } catch (MDALException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } finally {
            try {
                qdcmi.close();
                pmi.close();
                copmi.close();
            } catch (MDALException ex) {
                ex.printStackTrace();
            }
        }
    }
}
