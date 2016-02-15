/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.optimization_engine;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ejb.Stateless;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import org.moses.entity.SLA;
import org.moses.mdal.Constraints.ProcessSLAConstraint;
import org.moses.mdal.MDAL;
import org.moses.mdal.PerceivedQoSMDALInterface;
import org.moses.mdal.ProcessesMDALInterface;
import org.moses.mdal.SLAsMDALInterface;
import org.moses.mdal.monitor.ViolationResult;
import org.moses.optimization_engine.utils.CPLEXTool;
import org.moses.optimization_engine.utils.MatlabTool;
import org.moses.optimization_engine.utils.NumericComputationTool;

/**
 *
 * @author dante
 */
@WebService()
@Stateless()
public class OptimizationEngine {

    static {
        OptimizationThread t = new OptimizationThread(new OptimizationEngine());
        t.start();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "calculateProcessSolutionForSLACreation")
    public Boolean calculateProcessSolutionForSLACreation(@WebParam(name = "processName") String processName,
            @WebParam(name = "processClass") String processClass,
            @WebParam(name = "rate") Double rate) throws Exception {
        System.out.println("calculateProcessSolutionForSLACreation");
        return calculateProcessSolution(processName, processClass, NumericComputationTool.ChangeType.SLA_CREATION, rate);
    }

    @WebMethod(operationName = "calculateProcessSolutionForSLADeletion")
    public Boolean calculateProcessSolutionForSLADeletion(@WebParam(name = "processName") String processName, @WebParam(name = "processClass") String processClass, @WebParam(name = "rate") Double rate) throws Exception {
        System.out.println("calculateProcessSolutionForSLADeletion");
        return calculateProcessSolution(processName, processClass, NumericComputationTool.ChangeType.SLA_DELETION, rate);
    }

    @WebMethod(operationName = "calculateProcessSolutionForServiceChange")
    public Boolean calculateProcessSolutionForServiceChange(@WebParam(name = "processName") String processName) throws Exception {
        System.out.println("calculateProcessSolutionForServiceChange");
        return calculateProcessSolution(processName, null, NumericComputationTool.ChangeType.SERVICE_CHANGE, null);
    }

    /**
     * This method retrieves the violations vector saved into MDAL by the QoS Moitor and then
     * calls the optimization on every process for which a violation has been detected.
     * If no violations have been detected an optimization is however done on the process given as input
     * @param processName
     * @return
     * @throws Exception
     */
    @WebMethod(operationName = "calculateProcessSolutionForQoSChange")
    public Boolean calculateProcessSolutionForQoSChange(@WebParam(name = "processName") String processName) throws Exception {
        System.out.println("calculateProcessSolutionForQoSChange");
        MDAL mdal = MDAL.getInstance();
        Boolean resp = true;
        PerceivedQoSMDALInterface mdalQoS = mdal.getPerceivedQoSMDAL();
        Vector<ViolationResult> viol = mdalQoS.getViolations();
        mdalQoS.close();
        if (viol != null && !viol.isEmpty()) {
            Iterator violIt = viol.iterator();
            Vector<String> checked = new Vector<String>();
            while (violIt.hasNext() && resp) {
                //System.out.println("OptEngine: ciclo processi");
                ViolationResult elem = (ViolationResult) violIt.next();
                String elemProcessName = elem.getProcessName();
                if ((elemProcessName != null) && !checked.contains(elemProcessName)) {
                    NumericComputationTool nct = NumericComputationTool.getInstance();
                    if (nct instanceof CPLEXTool) {
                        ProcessesMDALInterface pmi = mdal.getProcessesMDAL();
                        Iterator<SLA> slas = pmi.readProcess(elem.getProcessName()).getSlas().iterator();
                        pmi.close();
                        while (slas.hasNext()) {
                            SLA s = slas.next();
                            resp = calculateProcessSolution(elemProcessName, s.getProcessClass(), NumericComputationTool.ChangeType.QOS_CHANGE, null);
                        }
                        checked.add(elemProcessName);
                    } else if (nct instanceof MatlabTool) {
                        resp = calculateProcessSolution(elemProcessName, null, NumericComputationTool.ChangeType.QOS_CHANGE, null);
                        checked.add(elemProcessName);
                    }
                }
            }
        } else {
            if ((processName != null) && (!processName.equals(""))) {
                NumericComputationTool nct = NumericComputationTool.getInstance();
                if (nct instanceof CPLEXTool) {
                    ProcessesMDALInterface pmi = mdal.getProcessesMDAL();
                    Iterator<SLA> slas = pmi.readProcess(processName).getSlas().iterator();
                    pmi.close();
                    while (slas.hasNext()) {
                        SLA s = slas.next();
                        resp = calculateProcessSolution(processName, s.getProcessClass(), NumericComputationTool.ChangeType.QOS_CHANGE, null);
                    }
                } else if (nct instanceof MatlabTool) {
                    resp = calculateProcessSolution(processName, null, NumericComputationTool.ChangeType.QOS_CHANGE, null);
                }
            }
        }
        return resp;
    }

    @WebMethod(operationName = "calculateProcessSolutionForProcessCreation")
    public Boolean calculateProcessSolutionForProcessCreation(@WebParam(name = "processName") String processName) throws Exception {
        System.out.println("calculateProcessSolutionForProcessCreation");
        return calculateProcessSolution(processName, null, NumericComputationTool.ChangeType.PROCESS_CREATION, null);
    }

    private Boolean calculateProcessSolution(String processName, String processClass, NumericComputationTool.ChangeType type, Double rate) throws Exception {
        boolean result = false;
        File lockFile = null;
        try {
            lockFile = new File("OptimizationLock");
            while (!lockFile.createNewFile());
            long t1 = System.currentTimeMillis();


            NumericComputationTool nct = NumericComputationTool.getInstance();

            //The following instruction writes a file to disk.
            //This file is then taken as input by the selected numeric computation tool
            nct.printInput(processName, processClass, type, rate);

            //The following instruction invokes the optimization process.
            result = nct.invoke(processName);

            if (result || (!result && type.equals(NumericComputationTool.ChangeType.SLA_DELETION))) {
                if (type.equals(NumericComputationTool.ChangeType.SLA_CREATION) || type.equals(NumericComputationTool.ChangeType.SLA_DELETION)) {
                    //Update the TotalRequestRate
                    MDAL mdal = MDAL.getInstance();
                    SLAsMDALInterface slasMDAL = mdal.getSLAsMDAL();
                    SLA sla = slasMDAL.readSLA(processName, processClass);
                    System.out.println("Class " + processClass + ". Rate before: " + sla.getSlaConstraints().get(ProcessSLAConstraint.TOTAL_REQUEST_RATE));
                    String op;
                    if (type.equals(NumericComputationTool.ChangeType.SLA_CREATION)) {
                        op = "adding";
                    } else {
                        op = "subtracting";
                    }
                    System.out.println(op + " " + rate);
                    Double totalRequestRate = Double.valueOf(sla.getSlaConstraints().get(ProcessSLAConstraint.TOTAL_REQUEST_RATE));
                    System.out.println("-------Total request rate: " + totalRequestRate);
                    System.out.println("---------Request rate: " + rate);
                    if (type.equals(NumericComputationTool.ChangeType.SLA_CREATION)) {
                        if (totalRequestRate <= 0.1) {
                            totalRequestRate = rate;
                        } else {
                            totalRequestRate += rate;
                        }
                    } else {
                        totalRequestRate -= rate;
                        if (totalRequestRate < 0.1) {
                            totalRequestRate = 0.1;
                        }
                    }

                    sla.getSlaConstraints().put(ProcessSLAConstraint.TOTAL_REQUEST_RATE, totalRequestRate.toString());
                    slasMDAL.updateSLA(sla);
                    sla = slasMDAL.readSLA(processName, processClass);
                    System.out.println("Result: " + sla.getSlaConstraints().get(ProcessSLAConstraint.TOTAL_REQUEST_RATE));
                    slasMDAL.close();
                }
                if (result) {
                    String requestId = "-1"; //RequestCounter.getCounter();
                    nct.parseAndSaveOutput(processName, requestId);
                }
            }
            long t2 = System.currentTimeMillis();
            System.out.print("**************Opt. Engine execution time: " + (t2 - t1));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getClass().getName() + ":" + ex.getMessage());
        } finally {
            lockFile.delete();
        }

        return result;

    }

    /**
     * Check if there exists a new solution for a process with a given process name when there is a SLA creation for a QoS class
     * @param processName
     * @param processClass
     * @return "true" if there exists a solution; "false" otherwise
     * @throws org.apache.axis2.AxisFault
     */
    @WebMethod(operationName = "existProcessSolution")
    public Boolean existProcessSolution(@WebParam(name = "processName") String processName, @WebParam(name = "processClass") String processClass) throws Exception {
        //Currently not implemented
        return true;
    }
}
