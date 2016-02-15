/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.optimization_engine.utils;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.AbstractService;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.entity.ProcessPath;
import org.moses.entity.ProcessSubPath;
import org.moses.entity.SLA;
import org.moses.entity.solution.EndpointNode;
import org.moses.entity.solution.GroupNode;
import org.moses.entity.solution.OperationNode;
import org.moses.entity.solution.ServiceNode;
import org.moses.mdal.Constraints.ProcessSLAConstraint;
import org.moses.mdal.MDAL;
import org.moses.mdal.PerceivedQoSMDALInterface;
import org.moses.mdal.ProcessesMDALInterface;
import org.moses.mdal.RequestCounter;
import org.moses.mdal.SLAsMDALInterface;
import org.moses.mdal.SolutionsMDALInterface;
import org.moses.mdal.monitor.ViolationResult;

/**
 *
 * @author valeryo
 */
public class CPLEXTool extends NumericComputationTool {

    private static final String CONFIG_FILE = "optimization.properties";
    private static final String QoS_PRESENT = "QoSMonitor";
    private Properties properties;
    private Hashtable<String, Integer> cacheAbsSer; //Hashtable introdotta per evitare di scorrere le liste con i servizi concreti e astratti
    private Hashtable<String, Vector<Integer>> cacheConSer;
    private List<ConcreteService> concreteWS = null;
    private ProcessPath[] exePath = null;
    List<AbstractService> services = null;
    String processClass = null;
    double MAX_TIME = -1;
    double MIN_AVAIL = -1;
    double MAX_PRICE = -1;
    String requestId = null;

    public Boolean cplexOptimizer(List<ConcreteService> concreteWS, ProcessPath[] exePath, List<AbstractService> services, String processName, String processClass, double MAX_TIME, double MAX_AVAIL, double MAX_PRICE, String requestId) {

        List<ConcreteService> choosenImpl = null;
        List<Double> choosenImplPr = null;

        Date d1 = null, d2 = null, d3 = null, d4 = null;

        try {

            MDAL mdal;

            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            properties = new Properties();
            properties.load(fis);
            fis.close();
            String isThereQoS = properties.getProperty(QoS_PRESENT);

            Hashtable<String, ViolationResult> perceivedQoS = null;
            Vector<ViolationResult> violations = null;
            if (isThereQoS.equals("1")) {
                mdal = MDAL.getInstance();
                PerceivedQoSMDALInterface qosMdal = mdal.getPerceivedQoSMDAL();
                violations = qosMdal.getViolations();
                qosMdal.close();
                if (violations != null) {
                    perceivedQoS = new Hashtable<String, ViolationResult>();
                    for (int i = 0; i < violations.size(); i++) {
                        perceivedQoS.put(violations.get(i).getServiceName(), violations.get(i));
                    }
                }
            }

            d1 = new Date();
            Iterator<AbstractService> iter, iter3;
            Iterator<ConcreteService> iter2;

            IloCplex cplex = new IloCplex();

            IloNumVar[] z = new IloNumVar[concreteWS.size()];//ON OFF servizi
            IloNumVar[] y = new IloNumVar[concreteWS.size()];//ON OFF operazioni
            IloNumVar[] exeT = new IloNumVar[services.size()];//Tempo di esecuzione di un servizio
            IloNumVar[] exeTime = new IloNumVar[exePath.length]; //Tempo massimo di esecuzione di un path
            IloNumVar[] avail = new IloNumVar[exePath.length]; //Disponibilità di un path
            IloNumVar[] price = new IloNumVar[exePath.length]; //Costo di un path

            /* Allocazione delle variabili */
            for (int i = 0; i < concreteWS.size(); i++) {
                z[i] = cplex.intVar(0, 1);
                y[i] = cplex.intVar(0, 1);
            }

            for (int i = 0; i < services.size(); i++) {
                exeT[i] = cplex.numVar(0, Double.MAX_VALUE);
            }

            for (int i = 0; i < exePath.length; i++) {
                exeTime[i] = cplex.numVar(0, Double.MAX_VALUE);
                avail[i] = cplex.numVar(Double.NEGATIVE_INFINITY, 0);
                price[i] = cplex.numVar(0, Double.MAX_VALUE);
            }

            buildCacheWS(concreteWS, services);

            /* Realizza (4) e (6) e (7) */
            ViolationResult elem = null;
            iter = services.iterator();
            for (int i = 0; i < services.size(); i++) {
                AbstractService ws = iter.next();
                IloLinearNumExpr app = cplex.linearNumExpr();
                IloLinearNumExpr app2 = cplex.linearNumExpr();
                IloLinearNumExpr app3 = cplex.linearNumExpr();

                Vector<Integer> indexList = cacheConSer.get(ws.getName());
                for (int j = 0; j < indexList.size(); j++) {
                    int index = indexList.get(j);
                    ConcreteService concws = concreteWS.get(index);
                    app.addTerm(1.0, y[index]);
                    app2.addTerm(1.0, z[index]);
                    /* Assunzione IMPORTANTE su servizi e operazioni */
                    ConcreteOperation op = concws.getOperations().iterator().next();
                    Double rt = op.getResponseTime();
                    if (perceivedQoS != null && perceivedQoS.containsKey(concws.getId().toString())) {
                        elem = perceivedQoS.get(concws.getId().toString());
                        if (elem.getViolation().get("RESPONSE_TIME") != null) {
                            rt = elem.getViolation().get("RESPONSE_TIME");
                        }
                    }
                    app3.addTerm(rt, y[index]);
                }

                app3.addTerm(-1.0, exeT[i]);
                cplex.addEq(app3, 0);

                cplex.addEq(app, 1);
                cplex.addEq(app2, 1);
            }

            /* Realizza (5) */
            for (int j = 0; j < concreteWS.size(); j++) {
                IloLinearNumExpr app = cplex.linearNumExpr();
                app.addTerm(1.0, y[j]);
                app.addTerm(-1.0, z[j]);
                cplex.addLe(app, 0);
            }

            /* Realizza (9) */
            for (int i = 0; i < exePath.length; i++) {
                ProcessPath path = exePath[i];
                ProcessSubPath[] subpaths = path.getSubPaths();
                for (int j = 0; j < subpaths.length; j++) {
                    IloLinearNumExpr app = cplex.linearNumExpr();
                    ProcessSubPath subpath = subpaths[j];
                    iter = subpath.getSubPath().iterator();
                    while (iter.hasNext()) {
                        AbstractService aws = iter.next();
                        int index = cacheAbsSer.get(aws.getName());
                        app.addTerm(1.0, exeT[index]);
                    }
                    app.addTerm(-1.0, exeTime[i]);
                    cplex.addLe(app, 0);
                }
            }

            /* Realizza (10) e (11)*/

            for (int i = 0; i < exePath.length; i++) {
                IloLinearNumExpr appExpr = cplex.linearNumExpr();
                IloLinearNumExpr appExpr2 = cplex.linearNumExpr();
                ArrayList<AbstractService> serviceSequence = new ArrayList<AbstractService>();
                HashSet<String> hash = new HashSet<String>();

                ProcessPath path = exePath[i];
                Hashtable<AbstractService, Integer> multiOcc = path.getMultipleOcc();

                ProcessSubPath[] subpaths = path.getSubPaths();

                for (int j = 0; j < subpaths.length; j++) {
                    ProcessSubPath subpath = subpaths[j];
                    iter = subpath.getSubPath().iterator();
                    while (iter.hasNext()) {
                        AbstractService app = iter.next();
                        if (!(hash.contains(app.getName()))) {
                            hash.add(app.getName());
                            Integer occ = multiOcc.get(app);
                            if (occ != null) {
                                for (int k = 0; k < occ; k++) {
                                    serviceSequence.add(app);
                                }
                            } else {
                                serviceSequence.add(app);
                            }
                        }
                    }
                }

                List<AbstractService> awsPath = serviceSequence;
                iter = awsPath.iterator();
                Hashtable<IloNumVar, Double> availHash = new Hashtable<IloNumVar, Double>();
                Hashtable<IloNumVar, Double> costHash = new Hashtable<IloNumVar, Double>();
                while (iter.hasNext()) {
                    AbstractService aws = iter.next();

                    Vector<Integer> indexList = cacheConSer.get(aws.getName());
                    for (int h = 0; h < indexList.size(); h++) {
                        int index = indexList.get(h);
                        ConcreteService ser = concreteWS.get(index);
                        ConcreteOperation op = ser.getOperations().iterator().next();
                        Double reliability = op.getReliability();
                        if (perceivedQoS != null && perceivedQoS.containsKey(ser.getId().toString())) {
                            elem = perceivedQoS.get(ser.getId().toString());
                            if (elem.getViolation().get("RELIABILITY") != null) {
                                reliability = elem.getViolation().get("RELIABILITY");
                            }
                        }

                        if (!availHash.containsKey(y[index])) {
                            availHash.put(y[index], Math.log(reliability));
                            costHash.put(y[index], op.getCost());
                        } else {
                            double oldAvail = availHash.remove(y[index]);
                            availHash.put(y[index], oldAvail + Math.log(reliability));
                            double oldPrice = costHash.remove(y[index]);
                            costHash.put(y[index], oldPrice + op.getCost());
                        }
                    }
                }

                Enumeration<IloNumVar> availEnum = availHash.keys();
                Enumeration<IloNumVar> costEnum = costHash.keys();
                while (availEnum.hasMoreElements()) {
                    IloNumVar var = availEnum.nextElement();
                    appExpr.addTerm(availHash.get(var), var);
                }
                while (costEnum.hasMoreElements()) {
                    IloNumVar var = costEnum.nextElement();
                    appExpr2.addTerm(costHash.get(var), var);
                }

                appExpr.addTerm(-1.0, avail[i]);
                cplex.addEq(appExpr, 0);

                appExpr2.addTerm(-1.0, price[i]);
                cplex.addEq(appExpr2, 0);
            }

            /* Pone il vincolo (15) (16) (17)*/
            for (int i = 0; i < exePath.length; i++) {
                IloLinearNumExpr app = cplex.linearNumExpr();
                app.addTerm(1.0, exeTime[i]);
                cplex.addLe(app, MAX_TIME);

                IloLinearNumExpr app2 = cplex.linearNumExpr();
                app2.addTerm(1.0, avail[i]);
                cplex.addGe(app2, Math.log(MAX_AVAIL));

                IloLinearNumExpr app3 = cplex.linearNumExpr();
                app3.addTerm(1.0, price[i]);
                cplex.addLe(app3, MAX_PRICE);
            }

            IloLinearNumExpr app = cplex.linearNumExpr();
            for (int i = 0; i < exePath.length; i++) {

                /* Rivedere i valori massimi e minimi utilizzati per il calcolo dello score */
                double freq = exePath[i].getProbability();
                /* Score del tempo d'esecuzione */
                app.addTerm(exeTime[i], freq * (-1.0) / (MAX_TIME));
                app.addTerm(cplex.intVar(1, 1), freq * MAX_TIME / (MAX_TIME));
                /* Score dell'availability */
                //app.addTerm(avail[i], freq / (1 - 0));
                //app.addTerm(cplex.intVar(1, 1), freq / (1 - 0));
                /* Score del prezzo d'acquisto */
                //app.addTerm(price[i], freq * (-1.0) / (MAX_PRICE));
                //app.addTerm(cplex.intVar(1, 1), freq * MAX_PRICE / (MAX_PRICE));
            }
            cplex.addMaximize(app);

            d2 = new Date();
            System.out.println("------> Preparazione CPLEX: " + (d2.getTime() - d1.getTime()));
            choosenImpl = new ArrayList();
            choosenImplPr = new ArrayList();
            if (cplex.solve()) {
//                cplex.output().println("Solution status = " + cplex.getStatus());
//                cplex.output().println("Solution value = " + cplex.getObjValue());
                double[] val = cplex.getValues(y);
//                int ncols = cplex.getNcols();
//                iter2 = concreteWS.iterator();
//                for (int j = 0; j < concreteWS.size(); ++j) {
//                    cplex.output().println("Value = " + val[j] + " name " + iter2.next().getEndpointURL());
//                }                

                double[] valRes = cplex.getValues(exeTime);
                double[] valAva = cplex.getValues(avail);
                double[] valPri = cplex.getValues(price);

                for (int i = 0; i < exePath.length; i++) {
                    System.out.println("Valori delle variabili di QoS: " + valRes[i] + " " + valAva[i] + " " + valPri[i]);
                }

                int i = 0;
                iter2 = concreteWS.iterator();
                while (iter2.hasNext()) {
                    if (val[i] >= 0.99) {
                        choosenImpl.add(iter2.next());
                        choosenImplPr.add(1.0);
                    } else {
                        iter2.next();
                    }
                    i++;
                }

            } else {
                choosenImpl = null;
            }

            cplex.end();
            d3 = new Date();
            System.out.println("------> Calcolo CPLEX: " + (d3.getTime() - d2.getTime()));

        } catch (IloException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (choosenImpl != null) {
            Boolean ret = writeSolution(choosenImpl, choosenImplPr, services, processName, processClass, requestId);
            d4 = new Date();
            System.out.println("------> Scrittura soluzione su db: " + (d4.getTime() - d3.getTime()));
            return ret;
        } //return true;
        else {
            return false;
        }

    }

    public Boolean cplexOptimizerLoadAware(List<ConcreteService> concreteWS, ProcessPath[] exePath, List<AbstractService> services, String processName, String processClass, double MAX_TIME, double MAX_AVAIL, double MAX_PRICE, String requestId) {

        Date d1 = new Date();

        List<ConcreteService> choosenImpl = null;
        List<Double> choosenImplPr = null;

        Boolean ret = false;

        try {
            MDAL mdal;

            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            properties = new Properties();
            properties.load(fis);
            fis.close();
            String isThereQoS = properties.getProperty(QoS_PRESENT);

            Hashtable<String, ViolationResult> perceivedQoS = null;
            Vector<ViolationResult> violations = null;
            if (isThereQoS.equals("1")) {
                mdal = MDAL.getInstance();
                PerceivedQoSMDALInterface qosMdal = mdal.getPerceivedQoSMDAL();
                violations = qosMdal.getViolations();
                qosMdal.close();
                if (violations != null) {
                    perceivedQoS = new Hashtable<String, ViolationResult>();
                    for (int i = 0; i < violations.size(); i++) {
                        perceivedQoS.put(violations.get(i).getServiceName(), violations.get(i));
                    }
                }
            }


            Iterator<AbstractService> iter, iter3;
            Iterator<ConcreteService> iter2;

            IloCplex cplex = new IloCplex();

            IloNumVar[] y = new IloNumVar[concreteWS.size()];//ON OFF servizi
            IloNumVar[] x = new IloNumVar[concreteWS.size()];//Probabilità di scelta di un servizio
            IloNumVar[] exeT = new IloNumVar[services.size()];// Peggior tempo di esecuzione di un task
            IloNumVar[] exeTMed = new IloNumVar[services.size()];// Tempo medio di esecuzione di un task
            IloNumVar[] availab = new IloNumVar[services.size()];//Peggior disponibiltà di un task
            IloNumVar[] cost = new IloNumVar[services.size()];//Peggior prezzo di un task
            IloNumVar[] Rk = new IloNumVar[exePath.length]; //Tempo massimo di esecuzione di un path
            IloNumVar[] RkMed = new IloNumVar[exePath.length]; //Tempo massimo di esecuzione di un path
            IloNumVar[] Dk = new IloNumVar[exePath.length]; //Disponibilità minima di un path
            IloNumVar[] Ck = new IloNumVar[exePath.length]; //Costo massimo di un path

            /* Allocazione delle variabili */
            for (int i = 0; i < concreteWS.size(); i++) {
                x[i] = cplex.numVar(0, Double.MAX_VALUE);
                y[i] = cplex.intVar(0, 1);
            }

            for (int i = 0; i < services.size(); i++) {
                exeT[i] = cplex.numVar(0, Double.MAX_VALUE);
                exeTMed[i] = cplex.numVar(0, Double.MAX_VALUE);
                availab[i] = cplex.numVar(Double.NEGATIVE_INFINITY, 0);
                cost[i] = cplex.numVar(0, Double.MAX_VALUE);
            }

            for (int i = 0; i < exePath.length; i++) {
                Rk[i] = cplex.numVar(0, Double.MAX_VALUE);
                RkMed[i] = cplex.numVar(0, Double.MAX_VALUE);
                Dk[i] = cplex.numVar(Double.NEGATIVE_INFINITY, 0);
                Ck[i] = cplex.numVar(0, Double.MAX_VALUE);
            }

            buildCacheWS(concreteWS, services);

            /* La somma delle probabilita di scelta dei servizi concreti deve sommare a 1 */
            iter = services.iterator();
            for (int i = 0; i < services.size(); i++) {
                AbstractService ws = iter.next();
                IloLinearNumExpr app = cplex.linearNumExpr();

                iter2 = concreteWS.iterator();
                /* Possibile un'ottimizzazione utilizzando HASHTABLE */
                for (int j = 0; j < concreteWS.size(); j++) {
                    ConcreteService concws = iter2.next();
                    if (ws.getName().equals(concws.getService().getName())) {
                        app.addTerm(1.0, x[j]);
                    }
                }

                cplex.addEq(app, 1);

            }

            /* La probabilita di scegliere il servizio concreto J non deve eccedere la sua capacità
             * Per ogni servizio j usato va accesa la relativa variabile y[j]
             */
            iter2 = concreteWS.iterator();
            for (int j = 0; j < concreteWS.size(); j++) {

                IloLinearNumExpr app = cplex.linearNumExpr();
                IloLinearNumExpr app2 = cplex.linearNumExpr();

                ConcreteService concws = iter2.next();
                ConcreteOperation concop = concws.getOperations().iterator().next();

                app.addTerm(1.0, x[j]);
                app2.addTerm(1.0, x[j]);
                app2.addTerm(-1.0, y[j]);

                cplex.addLe(app, concop.getLoadProbability());
                cplex.addLe(app2, 0);

            }

            /* Vincoli che realizzano l'analisi del caso peggiore per garantire
             * che il soddisfacimento dei requisiti di QoS sia per richiesta
             */
            ViolationResult elem = null;
            iter = services.iterator();
            for (int i = 0; i < services.size(); i++) {

                AbstractService abser = iter.next();
                iter2 = concreteWS.iterator();
                /* Possibile un'ottimizzazione utilizzando HASHTABLE */
                for (int j = 0; j < concreteWS.size(); j++) {
                    ConcreteService concws = iter2.next();
                    if (abser.getName().equals(concws.getService().getName())) {

                        IloLinearNumExpr app = cplex.linearNumExpr();
                        IloLinearNumExpr app2 = cplex.linearNumExpr();
                        IloLinearNumExpr app3 = cplex.linearNumExpr();

                        ConcreteOperation concop = concws.getOperations().iterator().next();
                        Double rt = concop.getResponseTime();
                        if (perceivedQoS != null && perceivedQoS.containsKey(concws.getId().toString())) {
                            elem = perceivedQoS.get(concws.getId().toString());
                            if (elem.getViolation().get("RESPONSE_TIME") != null) {
                                rt = elem.getViolation().get("RESPONSE_TIME");
                            }
                        }

                        app.addTerm(rt, y[j]);
                        app.addTerm(-1.0, exeT[i]);
                        app2.addTerm(Math.log(concop.getReliability()), y[j]);
                        app2.addTerm(-1.0, availab[i]);
                        app3.addTerm(concop.getCost(), y[j]);
                        app3.addTerm(-1.0, cost[i]);

                        cplex.addLe(app, 0);
                        cplex.addGe(app2, 0);
                        cplex.addLe(app3, 0);
                    }
                }
            }

            /* Calcola il tempo di risposta nel caso peggiore */
//            for (int i = 0; i < exePath.length; i++) {
//                ProcessPath path = exePath[i];
//                ProcessSubPath[] subpaths = path.getSubPaths();
//                for (int j = 0; j < subpaths.length; j++) {
//                    IloLinearNumExpr app = cplex.linearNumExpr();
//                    ProcessSubPath subpath = subpaths[j];
//                    iter = subpath.getSubPath().iterator();
//                    while (iter.hasNext()) {
//                        AbstractService aws = iter.next();
//                        int k = 0;
//                        iter3 = services.iterator();
//                        /* Possibile un'ottimizzazione utilizzando HASHTABLE */
//                        while (iter3.hasNext()) {
//                            if (iter3.next().getName().equals(aws.getName())) {
//                                break;
//                            } else {
//                                k++;
//                            }
//                        }
//                        app.addTerm(1.0, exeT[k]);
//                    }
//                    app.addTerm(-1.0, Rk[i]);
//                    cplex.addLe(app, 0);
//                }
//            }

            for (int i = 0; i < exePath.length; i++) {
                ProcessPath path = exePath[i];
                ProcessSubPath[] subpaths = path.getSubPaths();
                for (int j = 0; j < subpaths.length; j++) {
                    IloLinearNumExpr app = cplex.linearNumExpr();
                    ProcessSubPath subpath = subpaths[j];
                    iter = subpath.getSubPath().iterator();
                    while (iter.hasNext()) {
                        AbstractService aws = iter.next();
                        int index = cacheAbsSer.get(aws.getName());
                        app.addTerm(1.0, exeT[index]);
                    }
                    app.addTerm(-1.0, Rk[i]);
                    cplex.addLe(app, 0);
                }
            }

            /* Calcola la disponibilità e il prezzo nel caso peggiore */
            for (int i = 0; i < exePath.length; i++) {
                IloLinearNumExpr app = cplex.linearNumExpr();
                IloLinearNumExpr app2 = cplex.linearNumExpr();
                ArrayList<AbstractService> serviceSequence = new ArrayList<AbstractService>();
                HashSet<String> hash = new HashSet<String>();

                ProcessPath path = exePath[i];
                Hashtable<AbstractService, Integer> multiOcc = path.getMultipleOcc();

                ProcessSubPath[] subpaths = path.getSubPaths();

                for (int j = 0; j < subpaths.length; j++) {
                    ProcessSubPath subpath = subpaths[j];
                    iter = subpath.getSubPath().iterator();
                    while (iter.hasNext()) {
                        AbstractService absws = iter.next();
                        if (!(hash.contains(absws.getName()))) {
                            hash.add(absws.getName());
                            Integer occ = multiOcc.get(absws);
                            if (occ != null) {
                                for (int k = 0; k < occ; k++) {
                                    serviceSequence.add(absws);
                                }
                            } else {
                                serviceSequence.add(absws);
                            }
                        }
                    }
                }

                List<AbstractService> awsPath = serviceSequence;
                iter3 = awsPath.iterator();
                Hashtable<IloNumVar, Integer> availHash = new Hashtable<IloNumVar, Integer>();
                Hashtable<IloNumVar, Integer> costHash = new Hashtable<IloNumVar, Integer>();
                for (int k = 0; k < serviceSequence.size(); k++) {

                    AbstractService absws2 = iter3.next();
                    iter = services.iterator();
                    /* Possibile un'ottimizzazione utilizzando HASHTABLE */
                    for (int z = 0; z < services.size(); z++) {
                        AbstractService absws3 = iter.next();
                        if (absws2.getName().equals(absws3.getName())) {
                            if (!availHash.containsKey(availab[z])) {
                                availHash.put(availab[z], 1);
                                costHash.put(cost[z], 1);
                            } else {
                                Integer oldAvail = availHash.remove(availab[z]);
                                availHash.put(availab[z], oldAvail + 1);
                                Integer oldCost = costHash.remove(cost[z]);
                                costHash.put(cost[z], oldCost + 1);
                            }
                        }
                    }
                }

                Enumeration<IloNumVar> availEnum = availHash.keys();
                while (availEnum.hasMoreElements()) {
                    IloNumVar var = availEnum.nextElement();
                    app.addTerm(availHash.get(var), var);
                }

                Enumeration<IloNumVar> costEnum = costHash.keys();
                while (costEnum.hasMoreElements()) {
                    IloNumVar var = costEnum.nextElement();
                    app2.addTerm(costHash.get(var), var);
                }

                app.addTerm(-1.0, Dk[i]);
                app2.addTerm(-1.0, Ck[i]);
                cplex.addEq(app, 0);
                cplex.addEq(app2, 0);

            }

            /* Pone il vincolo sui costraints */
            for (int i = 0; i < exePath.length; i++) {
                IloLinearNumExpr app = cplex.linearNumExpr();
                app.addTerm(1.0, Rk[i]);
                cplex.addLe(app, MAX_TIME);

                IloLinearNumExpr app2 = cplex.linearNumExpr();
                app2.addTerm(1.0, Dk[i]);
                cplex.addGe(app2, Math.log(MAX_AVAIL));

                IloLinearNumExpr app3 = cplex.linearNumExpr();
                app3.addTerm(1.0, Ck[i]);
                cplex.addLe(app3, MAX_PRICE);
            }

            /* Funzione obiettivo  */

            IloLinearNumExpr objective = cplex.linearNumExpr();
//            for (int i = 0; i < exePath.length; i++) {
//                IloLinearNumExpr appExpr = cplex.linearNumExpr();
//                IloLinearNumExpr appExpr2 = cplex.linearNumExpr();
//                ArrayList<AbstractService> serviceSequence = new ArrayList<AbstractService>();
//                HashSet<String> hash = new HashSet<String>();
//
//                ProcessPath path = exePath[i];
//                Hashtable<AbstractService, Integer> multiOcc = path.getMultipleOcc();
//
//                ProcessSubPath[] subpaths = path.getSubPaths();
//
//                for (int j = 0; j < subpaths.length; j++) {
//                    ProcessSubPath subpath = subpaths[j];
//                    iter = subpath.getSubPath().iterator();
//                    while (iter.hasNext()) {
//                        AbstractService absws = iter.next();
//                        if (!(hash.contains(absws.getName()))) {
//                            hash.add(absws.getName());
//                            Integer occ = multiOcc.get(absws);
//                            if (occ != null) {
//                                for (int k = 0; k < occ; k++) {
//                                    serviceSequence.add(absws);
//                                }
//                            } else {
//                                serviceSequence.add(absws);
//                            }
//                        }
//                    }
//                }
//
//                List<AbstractService> awsPath = serviceSequence;
//                iter = awsPath.iterator();
//                Hashtable<IloNumVar, Double> availHash = new Hashtable<IloNumVar, Double>();
//                Hashtable<IloNumVar, Double> costHash = new Hashtable<IloNumVar, Double>();
//                while (iter.hasNext()) {
//                    AbstractService aws = iter.next();
//                    iter2 = concreteWS.iterator();
//                    int q = 0;
//                    /* Possibile un'ottimizzazione utilizzando HASHTABLE */
//                    while (iter2.hasNext()) {
//                        ConcreteService ser = iter2.next();
//                        if (aws.getName().equals(ser.getService().getName())) {
//                            ConcreteOperation op = ser.getOperations().iterator().next();
//                            Double reliability = op.getReliability();
//                            Double costOp = op.getCost();
//                            if (!availHash.containsKey(x[q])) {
//                                availHash.put(x[q], Math.log(reliability));
//                                costHash.put(x[q], costOp);
//                            } else {
//                                double oldAvail = availHash.remove(x[q]);
//                                availHash.put(x[q], oldAvail + Math.log(reliability));
//                                double oldPrice = costHash.remove(y[q]);
//                                costHash.put(x[q], oldPrice + costOp);
//                            }
//                        }
//                        q++;
//                    }
//                }
//
//                double freq = exePath[i].getProbability();
//
//                Enumeration<IloNumVar> availEnum = availHash.keys();
//                Enumeration<IloNumVar> costEnum = costHash.keys();
//                while (availEnum.hasMoreElements()) {
//                    IloNumVar var = availEnum.nextElement();
//                    appExpr.addTerm(availHash.get(var) * freq, var);
//                }
//                while (costEnum.hasMoreElements()) {
//                    IloNumVar var = costEnum.nextElement();
//                    appExpr2.addTerm(costHash.get(var) * freq * (-1.0) / (MAX_PRICE), var);
//                }
//
//                appExpr2.addTerm(cplex.intVar(1, 1), freq * MAX_PRICE / (MAX_PRICE));
//
//                objective.add(appExpr);
//                objective.add(appExpr2);
//            }

            /* Tempo medio di esecuzione di un path */
            iter = services.iterator();
            for (int i = 0; i < services.size(); i++) {
                AbstractService ws = iter.next();
                IloLinearNumExpr app = cplex.linearNumExpr();

                iter2 = concreteWS.iterator();
                /* Possibile un'ottimizzazione utilizzando HASHTABLE */
                for (int j = 0; j < concreteWS.size(); j++) {
                    ConcreteService concws = iter2.next();
                    if (ws.getName().equals(concws.getService().getName())) {

                        /* Assunzione IMPORTANTE su servizi e operazioni */
                        ConcreteOperation op = concws.getOperations().iterator().next();
                        Double rt = op.getResponseTime();
                        if (perceivedQoS != null && perceivedQoS.containsKey(concws.getId().toString())) {
                            elem = perceivedQoS.get(concws.getId().toString());
                            if (elem.getViolation().get("RESPONSE_TIME") != null) {
                                rt = elem.getViolation().get("RESPONSE_TIME");
                            }
                        }
                        app.addTerm(rt, x[j]);
                    }
                }
                app.addTerm(-1.0, exeTMed[i]);
                cplex.addEq(app, 0);
            }

            for (int i = 0; i < exePath.length; i++) {
                ProcessPath path = exePath[i];
                ProcessSubPath[] subpaths = path.getSubPaths();
                for (int j = 0; j < subpaths.length; j++) {
                    IloLinearNumExpr app = cplex.linearNumExpr();
                    ProcessSubPath subpath = subpaths[j];
                    iter = subpath.getSubPath().iterator();
                    while (iter.hasNext()) {
                        AbstractService aws = iter.next();
                        int k = 0;
                        iter3 = services.iterator();
                        /* Possibile un'ottimizzazione utilizzando HASHTABLE */
                        while (iter3.hasNext()) {
                            if (iter3.next().getName().equals(aws.getName())) {
                                break;
                            } else {
                                k++;
                            }
                        }
                        app.addTerm(1.0, exeTMed[k]);
                    }
                    app.addTerm(-1.0, RkMed[i]);
                    cplex.addLe(app, 0);
                }
            }

            IloLinearNumExpr appExe = cplex.linearNumExpr();
            for (int i = 0; i < exePath.length; i++) {
                double freq = exePath[i].getProbability();
                appExe.addTerm(RkMed[i], freq * (-1.0) / (MAX_TIME));
                appExe.addTerm(cplex.intVar(1, 1), freq * MAX_TIME / (MAX_TIME));
            }

//            IloLinearNumExpr appExe2 = cplex.linearNumExpr();
//            for (int i = 0; i < exePath.length; i++) {
//                double freq = exePath[i].getProbability();
//                appExe2.addTerm(Rk[i], freq * (-1.0) / (MAX_TIME));
//                appExe2.addTerm(cplex.intVar(1, 1), freq * MAX_TIME / (MAX_TIME));
//            }
//            objective.add(appExe2);
            objective.add(appExe);

            cplex.addMaximize(objective);

            choosenImpl = new ArrayList();
            choosenImplPr = new ArrayList();
            if (cplex.solve()) {
//                cplex.output().println("Solution status = " + cplex.getStatus());
//                cplex.output().println("Solution value = " + cplex.getObjValue());
                double[] val = cplex.getValues(x);
//                int ncols = cplex.getNcols();
                iter2 = concreteWS.iterator();
                for (int j = 0; j < concreteWS.size(); ++j) {
                    cplex.output().println("Value = " + val[j] + " name " + iter2.next().getEndpointURL());
                }

                double[] valExe = cplex.getValues(exeT);
                for (int i = 0; i < services.size(); i++) {
                    System.out.println("Valori delle variabili di exeT: " + valExe[i]);
                }

                double[] valRes = cplex.getValues(Rk);
                double[] valAva = cplex.getValues(Dk);
                double[] valPri = cplex.getValues(Ck);

                for (int i = 0; i < exePath.length; i++) {
                    System.out.println("Valori delle variabili di QoS: " + valRes[i] + " " + valAva[i] + " " + valPri[i]);
                }

                int i = 0;
                iter2 = concreteWS.iterator();
                while (iter2.hasNext()) {
                    if (val[i] > 0) {
                        choosenImpl.add(iter2.next());
                        choosenImplPr.add(val[i]);
                    } else {
                        iter2.next();
                    }
                    i++;
                }

            } else {
                choosenImpl = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (choosenImpl != null) {
            ret = writeSolution(choosenImpl, choosenImplPr, services, processName, processClass, requestId);

            Date d2 = new Date();
            System.out.println("IL COSTO DEL NEW_PER REQ è " + (d2.getTime() - d1.getTime()));
            return ret;
        } else {
            return false;
        }
    }

    private void buildCacheWS(List<ConcreteService> concreteWS, List<AbstractService> services) {

        int i;

        cacheAbsSer = new Hashtable<String, Integer>();
        cacheConSer = new Hashtable<String, Vector<Integer>>();

        Iterator<AbstractService> iter = services.iterator();
        for (i = 0; i < services.size(); i++) {
            AbstractService abs = iter.next();
            cacheAbsSer.put(abs.getName(), i);
            cacheConSer.put(abs.getName(), new Vector<Integer>());
        }

        Iterator<ConcreteService> iter2 = concreteWS.iterator();
        for (i = 0; i < concreteWS.size(); i++) {
            String nome = iter2.next().getService().getName();
            Vector<Integer> app = cacheConSer.get(nome);
            app.add(i);
            cacheConSer.put(nome, app);
        }

        Enumeration<String> keys = cacheAbsSer.keys();
        while (keys.hasMoreElements()) {
            String s = keys.nextElement();
            System.out.println(s + "->" + cacheAbsSer.get(s));
        }

    }

    private Boolean writeSolution(List<ConcreteService> choosenImpl, List<Double> choosenImplPr, List<AbstractService> services, String processName, String processClass, String requestId) {

        Iterator<AbstractService> iter = services.iterator();
        Iterator<ConcreteService> iter2;
        Iterator<Double> iter3;

//        if (choosenImpl.size() != services.size()) {
//            return false;
//        }

        if (choosenImpl.size() == 0) {
            return false;
        }

        List<ServiceNode> snode = new ArrayList();
        int i = 0;
        while (iter.hasNext()) {
            ServiceNode app = new ServiceNode();
            OperationNode onode = new OperationNode();
            app.setServiceName(iter.next().getName());
            List<OperationNode> oplist = new ArrayList();
            List<GroupNode> grlist = new ArrayList();
            iter2 = choosenImpl.iterator();
            iter3 = choosenImplPr.iterator();
            while (iter2.hasNext()) {
                ConcreteService cws = iter2.next();
                Double prob = iter3.next();
                if (cws.getService().getName().equals(app.getServiceName())) {
                    ConcreteOperation cops = cws.getOperations().iterator().next();
                    onode.setOperationName(cops.getOperation().getName());

                    GroupNode gnode = new GroupNode();
                    gnode.setType(GroupNode.Type.ALTERNATE);
                    gnode.setGroupId(i++);
                    gnode.setProbability(new Float(prob));

                    EndpointNode enode = new EndpointNode();
                    enode.setCost(cops.getCost());
                    enode.setEndpointURL(cws.getEndpointURL());
                    enode.setServiceId(cws.getId());
                    enode.setTargetNameSpace(cws.getService().getNameSpace());

                    List<EndpointNode> elist = new ArrayList();
                    elist.add(enode);
                    gnode.setEndpoints(elist);
                    grlist.add(gnode);
                }
            }
            onode.setGroups(grlist);
            oplist.add(onode);
            app.setOperations(oplist);
            snode.add(app);
        }

        MDAL mdal;
        try {
            mdal = MDAL.getInstance();
            SolutionsMDALInterface solutionMDAL = mdal.getSolutionsMDAL();
            solutionMDAL.updateSolution(processName, processClass, snode, requestId);
            solutionMDAL.close();

        } catch (Exception ex) {
            Logger.getLogger(CPLEXTool.class.getName()).log(Level.SEVERE, null, ex);
        }


        return true;
    }

    @Override
    public void printInput(String processName, String processClass, ChangeType type, Double rate) throws Exception {
        MDAL mdal;
        ProcessesMDALInterface procInterface = null;
        SLAsMDALInterface slasMDAL = null;
        concreteWS = new ArrayList();
        requestId = null;

        mdal = MDAL.getInstance();
        requestId = RequestCounter.getCounter();
        procInterface = mdal.getProcessesMDAL();
        org.moses.entity.Process myProc = procInterface.readProcess(processName);
        exePath = myProc.getProcessPaths();
        //Obtaining concrete services list for the given process
        services = new ArrayList<AbstractService>();
        Enumeration<AbstractService> fromHash = myProc.getServices().keys();
        while (fromHash.hasMoreElements()) {
            AbstractService app = fromHash.nextElement();
            services.add(app);
        }

        Iterator<AbstractService> iter = services.iterator();
        while (iter.hasNext()) {

            AbstractService app = iter.next();
            List<ConcreteService> appImpl = app.getServices();
            Iterator<ConcreteService> iter2 = appImpl.iterator();
            while (iter2.hasNext()) {
                ConcreteService cws = iter2.next();
                cws.getOperations();
                concreteWS.add(cws);
            }
        }

        /* Possiedo i servizi concreti e gli execution path del processo */
        /* Invoco il risolutore CPLEX passando questi dati come argomenti */
        slasMDAL = mdal.getSLAsMDAL();
        SLA sla = slasMDAL.readSLA(processName, processClass);
        MAX_TIME = Double.valueOf(sla.getSlaConstraints().get(ProcessSLAConstraint.RESPONSE_TIME));
        MIN_AVAIL = Double.valueOf(sla.getSlaConstraints().get(ProcessSLAConstraint.RELIABILITY));
        MAX_PRICE = Double.valueOf(sla.getSlaConstraints().get(ProcessSLAConstraint.COST));
        procInterface.close();
        slasMDAL.close();
    }

    @Override
    public boolean invoke(String processName) throws Exception {
        boolean ret = false;
        String optimizationVariant = NumericComputationTool.properties.getProperty(NumericComputationTool.OPTIMIZATION_VARIANT);
        if (optimizationVariant.equals("standard")) {
            ret = cplexOptimizer(concreteWS, exePath, services, processName, processClass,
                    MAX_TIME, MIN_AVAIL, MAX_PRICE, "-1");
        } else if (optimizationVariant.equals("load-aware")) {
            ret = cplexOptimizerLoadAware(concreteWS, exePath, services, processName, processClass,
                    MAX_TIME, MIN_AVAIL, MAX_PRICE, "-1");
        }
        return ret;
    }

    @Override
    public void parseAndSaveOutput(String processName, String requestId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
