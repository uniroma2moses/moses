package org.moses.mdal.monitor;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.moses.entity.InvocationStatistics;
import org.moses.exception.MDALException;
import org.moses.mdal.MonitorMDALInterface;

/**
 *
 * @author arale
 */
public class MonitorMDAL implements MonitorMDALInterface {
    private static String logFile = "qos.log";
    private static String violationFile = "violation.log";

    /*public MonitorMDAL(){
        logFile = "qos.log";
        violationFile = "violation.log";
        try {
            //Deleting old records
            BufferedWriter writerLog = new BufferedWriter(new FileWriter(new File(this.violationFile)));
            writerLog.write("");
            BufferedWriter writerViol = new BufferedWriter(new FileWriter(new File(this.logFile)));
            writerViol.write("");
            writerViol.close();
            writerLog.close();
        } catch (IOException ex) {
            Logger.getLogger(MonitorMDAL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    /*public MonitorMDAL(String logFile, String violationFile){
        this.logFile = logFile;
        this.violationFile = violationFile;
        
    }*/

    /*******************************************************
     *  Format for logfile:
     * nInterval#operationName-serviceName-avgRespTime-varRespTime-reliability;[NEXT service]
     *
     *
     *  Format for violationsLogFile
     * nInterval#operationName-serviceName-violation:value[|otherViolation];[NEXT SERVICE]
     *
     *
     *
     *******************************************************/

    public synchronized int removeData(){
        int ret = 0;
        System.out.println("MDAL: removing data from files");
        try {
            //Deleting old records
            BufferedWriter writerLog = new BufferedWriter(new FileWriter(new File(this.violationFile)));
            writerLog.write("");
            BufferedWriter writerViol = new BufferedWriter(new FileWriter(new File(this.logFile)));
            writerViol.write("");
            writerViol.close();
            writerLog.close();
        } catch (IOException ex) {
            ret = -1;
            Logger.getLogger(MonitorMDAL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }


    public synchronized void addLogData(Vector<InvocationStatistics> toAdd, int interval){
        //System.out.println("MDAL: in addLogData");
        try {
            Iterator toAddIt = toAdd.iterator();
            BufferedWriter writer = null;
            writer = new BufferedWriter(new FileWriter(new File(logFile), true));
            if(writer != null){
                writer.append(interval+"#");
                while(toAddIt.hasNext()){
                    InvocationStatistics elem = (InvocationStatistics)toAddIt.next();
                    writer.append(elem.getProcessName()+"-"+elem.getOperationName()+"-"+elem.getServiceName()+"-"+elem.getAvgResponseTime()+"-"+elem.getVrcResponseTime()+"-"+elem.getReliability());
                    if(toAddIt.hasNext())
                        writer.append(";");
                }
                writer.append("\n");
                writer.close();
            }
            else
                System.out.println("MDAL: writer null in addLogData");
        } catch (IOException ex) {
            Logger.getLogger(MonitorMDAL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    //Copies the violiotions form the vector toAdd to the violation logfile
    public synchronized void addViolationData(Vector<ViolationResult> toAdd){
        Iterator toAddIt = toAdd.iterator();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(violationFile), true));
            Boolean first = true;
            int lastInterval = -1;
            while(toAddIt.hasNext()){
                //Element to add
                ViolationResult vRes = (ViolationResult) toAddIt.next();
                if(lastInterval == -1)  //first element of the vector
                    lastInterval = vRes.getInterval();
                //It could be possible (but it shouldn't happen)
                //that the data in the vector are about different intervals
                if(lastInterval != vRes.getInterval())
                    first = true;
                if(first){  //first element of the interval
                    //Every violation of the same interval goes on the same line
                    writer.append(vRes.getInterval()+"#");
                    first = false;
                }
                writer.append(vRes.getProcessName()+"-"+vRes.getOperationName()+"-"+vRes.getServiceName()+"-");
                Hashtable viol = vRes.getViolation();
                Set keys = viol.keySet();
                Iterator keysIt = keys.iterator();
                while(keysIt.hasNext()){
                   String violN = (String)keysIt.next();
                   writer.append(violN+":"+(Double)viol.get(violN));
                   if(keysIt.hasNext())
                       writer.append("|");
                }

                if(toAddIt.hasNext())
                    writer.append(";");
            }
            writer.append("\n");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(MonitorMDAL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Vector<ViolationResult> parseLine(String line){
        Vector<ViolationResult> result = new Vector<ViolationResult>();
        StringTokenizer nIntTok = new StringTokenizer(line, "#");
        Integer nInt = Integer.parseInt(nIntTok.nextToken());
        StringTokenizer procTok = new StringTokenizer(nIntTok.nextToken(), ";");
        while(procTok.hasMoreTokens()){
            String process = procTok.nextToken();
            StringTokenizer elems = new StringTokenizer(process, "-");
            String processName = elems.nextToken();
            String operationName = elems.nextToken();
            String serviceName = elems.nextToken();
            StringTokenizer violTok = new StringTokenizer(elems.nextToken(), "|");
            Hashtable<String, Double> violation = new Hashtable<String, Double>();
            while(violTok.hasMoreTokens()){
                String violString = violTok.nextToken();
                StringTokenizer violEl = new StringTokenizer(violString, ":");
                String violName = violEl.nextToken();
                Double violValue = Double.parseDouble(violEl.nextToken());
                violation.put(violName, violValue);
            }
            result.add(new ViolationResult(processName, serviceName, operationName, nInt, violation));
        }

        return result;
    }

    //Returns all the data about violations in the last window intervals
    public Vector<ViolationResult> getViolationData(int begin, int end){
        Vector<ViolationResult> result = null;
        //The hashtable will contain last window lines
        Vector<String> lines  = new Vector<String>();
        try {
            BufferedReader bR = new BufferedReader(new FileReader(violationFile));
            String line = null;

            line = bR.readLine();
            while((line != null)){
                int nIntForLine = getNIntervalFromLine(line);
                //Every record about one interval is written in one line
                //We don't need records before begin
                if((nIntForLine > begin) && (nIntForLine < end)){
                    if(result == null)
                        result = new Vector<ViolationResult>();
                    Vector<ViolationResult> resLine = parseLine(line);
                    result.addAll(resLine);
                    lines.add(line);
                }
                line = bR.readLine();
            }
            bR.close();
            /*if(lines.size() < end-begin){
                System.out.println("No values for some intervals");
            }*/
            if(lines.size() > end-begin)
                System.out.println("Problem: more lines than window!!!");
            
        } catch (IOException ex) {
            Logger.getLogger(MonitorMDAL.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return result;
    }

    //Return last window violations about the given process, service and operation
    public Vector<ViolationResult> getViolationData(String processName, String serviceName, String operationName, int begin, int end){
        Vector<ViolationResult> result = new Vector<ViolationResult>();
        Vector<ViolationResult> parResult;
        parResult = getViolationData(begin, end);
        Iterator resIt = parResult.iterator();
        while(resIt.hasNext()){
            ViolationResult elem = (ViolationResult)resIt.next();
            if(elem.getProcessName().equals(processName) && elem.getOperationName().equals(operationName) && elem.getServiceName().equals(serviceName))
                result.add(elem);
        }
        return result;
    }

    //Return last window violations about the given process
    public Vector<ViolationResult> getViolationData(String processName, int begin, int end){
        Vector<ViolationResult> result = new Vector<ViolationResult>();
        Vector<ViolationResult> parResult;
        parResult = getViolationData(begin, end);
        Iterator resIt = parResult.iterator();
        while(resIt.hasNext()){
            ViolationResult elem = (ViolationResult)resIt.next();
            if(elem.getProcessName().equals(processName))
                result.add(elem);
        }
        return result;
    }

    private int getNIntervalFromLine(String line) {
        StringTokenizer nIntTok = new StringTokenizer(line, "#");
        Integer nInt = Integer.parseInt(nIntTok.nextToken());
        return nInt.intValue();
    }

    public void close() throws MDALException {

    }

}
