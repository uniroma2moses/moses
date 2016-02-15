/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.moses.entity.AbstractService;
import org.moses.entity.Process;
import org.moses.entity.ProcessPath;
import org.moses.entity.ProcessSubPath;
import org.moses.exception.AbstractOperationAlreadyExistsMDALException;
import org.moses.exception.AbstractServiceAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.exception.NoSuchProcessMDALException;
import org.moses.exception.ProcessAlreadyExistsMDALException;
import org.moses.mdal.AbstractServicesMDALInterface;
import org.moses.mdal.MDAL;
import org.moses.mdal.ProcessesMDALInterface;

/**
 *
 * @author dante
 */
public class ProcessesMYSQLDAO extends MYSQLDAO implements ProcessesMDALInterface {

    private static String readProcessSTMT = "SELECT  * " +
            "FROM Process " +
            "WHERE ProcessName=?";
    private static String readAllProcessSTMT = "SELECT  * " +
            "FROM Process";
    private static String createProcessSTMT = "INSERT " +
            "INTO Process(ProcessName, ProcessNS, ProcessOperation, ProcessEndpoint, ProcessGraph, Stateful) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static String createProcessServiceSTMT = "INSERT " +
            "INTO Process_has_AbstractService(Process_ProcessName, AbstractService_Name) " +
            "VALUES (?, ?)";
    private static String updateProcessSTMT = "UPDATE Process " +
            "SET ProcessNS=?, ProcessOperation=?, ProcessEndpoint=?, ProcessGraph=?, Stateful=? " +
            "WHERE ProcessName=?";
    private static String deleteProcessSTMT = "DELETE " +
            "FROM Process " +
            "WHERE ProcessName=?";
    private static String readProcessByAbServ = "SELECT ProcessName, ProcessNS, ProcessOperation, ProcessEndpoint, ProcessGraph, Stateful, AverageVisit " +
            "FROM Process_has_AbstractService, Process " +
            "WHERE AbstractService_Name = ? AND Process_ProcessName=ProcessName";
    private static String insertPath = "INSERT INTO ProcessPath(ProcessName,Probability) VALUES (?,?)";
    private static String insertSubPath = "INSERT INTO ProcessSubPath(ProcessPathID,SeqNumber,AbstractServiceName) VALUES (?,?,?)";
    private static String insertServiceOccurrence = "INSERT INTO ServiceOccurrence(AbstractService,ProcessName,ProcessPathID,Count) VALUES (?,?,?,?)";
    private static String readPath = "SELECT ProcessPathID,Probability FROM ProcessPath WHERE ProcessName=?";
    private static String readSubPathID = "SELECT DISTINCT ProcessSubPathID FROM ProcessSubPath WHERE ProcessPathID=?";
    private static String readSubPath = "SELECT AbstractServiceName from ProcessSubPath WHERE " +
            "ProcessPathID=? AND ProcessSubPathID=? ORDER BY SeqNumber";
    private static String readServiceOccurrence = "SELECT AbstractService,Count FROM ServiceOccurrence WHERE ProcessName=? AND ProcessPathID=?";
    private static String deletePath = "DELETE FROM ProcessPath WHERE ProcessName=?";

    /**
     * 
     * @throws MDALException
     */
    public ProcessesMYSQLDAO() throws MDALException {
        super();
    }

    public ProcessesMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    /**
     *
     * @param p
     * @throws BadArgumentsMDALException
     * @throws MDALException
     */
    public void createProcess(Process p) throws BadArgumentsMDALException, ProcessAlreadyExistsMDALException, AbstractServiceAlreadyExistsMDALException, AbstractOperationAlreadyExistsMDALException, MDALException {
        PreparedStatement querySTMT = null;
        PreparedStatement pathStatement = null;
        PreparedStatement serviceOccurrenceStatement = null;
        PreparedStatement subpathStatement = null;
        AbstractServiceMYSQLDAO writer = null;
        //Checking parameters
        if (p.getProcessName() == null || p.getProcessName().length() == 0 ||
                p.getProcessNS() == null || p.getProcessNS().length() == 0 ||
                p.getProcessOperation() == null || p.getProcessOperation().length() == 0 ||
                p.getProcessEndpoints() == null || p.getProcessEndpoints().length() == 0 ||
                p.getProcessGraph() == null || p.getProcessGraph().length() == 0 ||
                p.getServices() == null || p.getProcessPaths() == null || p.getProcessPaths().length == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(createProcessSTMT);
            querySTMT.setString(1, p.getProcessName());
            querySTMT.setString(2, p.getProcessNS());
            querySTMT.setString(3, p.getProcessOperation());
            querySTMT.setString(4, p.getProcessEndpoints());
            querySTMT.setString(5, p.getProcessGraph());
            querySTMT.setBoolean(6, p.getStateful());
            querySTMT.executeUpdate();
            commitTransactionStmt.execute();

            Enumeration<AbstractService> it = p.getServices().keys();
            startTransactionStmt.execute();
            while (it.hasMoreElements()) {
                AbstractService s = it.nextElement();
                writer = new AbstractServiceMYSQLDAO(/*db*/);
                writer.createAbstractService(s);

                querySTMT = db.prepareStatement(createProcessServiceSTMT);
                querySTMT.setString(1, p.getProcessName());
                querySTMT.setString(2, s.getName());
                querySTMT.executeUpdate();
            }


            /* Following code will populate tables for path and subpath management */
            ProcessPath[] processPaths = p.getProcessPaths();
            pathStatement = db.prepareStatement(insertPath);
            serviceOccurrenceStatement = db.prepareStatement(insertServiceOccurrence);
            subpathStatement = db.prepareStatement(insertSubPath);
            for (int i = 0; i < processPaths.length; i++) {
                //Inserting process path
                ProcessPath path = processPaths[i];
                pathStatement.setString(1, p.getProcessName());
                pathStatement.setDouble(2, path.getProbability());
                pathStatement.executeUpdate();
                ResultSet rs = pathStatement.getGeneratedKeys();
                rs.next();
                int processPathID = rs.getInt(1);

                //Inserting services count for this path
                Hashtable<AbstractService, Integer> occ = path.getMultipleOcc();
                Enumeration<AbstractService> services = occ.keys();
                while (services.hasMoreElements()) {
                    AbstractService s = services.nextElement();
                    Integer count = occ.get(s);
                    serviceOccurrenceStatement.setString(1, s.getName());
                    serviceOccurrenceStatement.setString(2, p.getProcessName());
                    serviceOccurrenceStatement.setInt(3, processPathID);
                    serviceOccurrenceStatement.setInt(4, count);
                    serviceOccurrenceStatement.executeUpdate();
                }

                for (int j = 0; j < path.getSubPaths().length; j++) {
                    ProcessSubPath subPath = path.getSubPaths()[j];
                    subpathStatement.setInt(1, processPathID);
                    Iterator<AbstractService> iter = subPath.getSubPath().iterator();
                    int k = 1;
                    while (iter.hasNext()) {
                        AbstractService s = iter.next();
                        subpathStatement.setInt(2, k);
                        subpathStatement.setString(3, s.getName());
                        subpathStatement.executeUpdate();
                        k++;
                    }
                }
            }
            /* End of path and subpath management */
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new ProcessAlreadyExistsMDALException();
            } else {
                throw new MDALException(ex.getMessage());
            }
        } finally {
            writer.close();
            writer = null;
            try {
                querySTMT.close();
                querySTMT = null;
                pathStatement.close();
                pathStatement = null;
                subpathStatement.close();
                subpathStatement = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
                serviceOccurrenceStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param processName
     * @return
     * @throws BadArgumentsMDALException
     * @throws NoSuchProcessMDALException
     * @throws MDALException
     */
    public Process readProcess(String processName) throws BadArgumentsMDALException, NoSuchProcessMDALException, MDALException {
        Process p = null;
        PreparedStatement querySTMT = null;
        PreparedStatement pathSTMT = null;
        PreparedStatement serviceOccurrenceStatement = null;
        PreparedStatement subPathIDSTMT = null;
        PreparedStatement subPathSTMT = null;
        ResultSet rs = null;
        ResultSet subPathRS = null;
        ResultSet subPathIDRS = null;
        ResultSet pathRS = null;
        ResultSet serviceOccRS = null;
        AbstractServicesMDALInterface asmi = null;

        if (processName == null || processName.length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            MDAL mdal = MDAL.getInstance();
            asmi = mdal.getAbstractServicesMDAL();
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(readProcessSTMT);
            querySTMT.setString(1, processName);
            rs = querySTMT.executeQuery();

            if (rs.next()) {
                p = new Process();
                p.setProcessName(rs.getString(1));
                p.setProcessNS(rs.getString(2));
                p.setProcessOperation(rs.getString(3));
                p.setProcessEndpoints(rs.getString(4));
                p.setProcessGraph(rs.getString(5));
                p.setStateful(rs.getBoolean(6));

                /* Performance PATCH */
                Hashtable<AbstractService, Float> tasks = asmi.readAbstractServicesByPr(p);
                Enumeration<AbstractService> abs = tasks.keys();
                Hashtable<String, AbstractService> hash = new Hashtable<String, AbstractService>();
                while (abs.hasMoreElements()) {
                    AbstractService aws = abs.nextElement();
                    hash.put(aws.getName(), aws);
                }
                /* END Performance PATCH */

                /** Managing paths **/
                pathSTMT = db.prepareStatement(readPath);
                pathSTMT.setString(1, p.getProcessName());
                pathRS = pathSTMT.executeQuery();
                ArrayList<ProcessPath> paths = new ArrayList<ProcessPath>();
                int i = 0;

                serviceOccurrenceStatement = db.prepareStatement(readServiceOccurrence);
                while (pathRS.next()) {
                    ArrayList<ProcessSubPath> subPaths = new ArrayList<ProcessSubPath>();
                    int pathID = pathRS.getInt(1);
                    paths.add(new ProcessPath(p, pathRS.getDouble(2)));
                    subPathIDSTMT = db.prepareStatement(readSubPathID);
                    subPathIDSTMT.setInt(1, pathID);
                    subPathIDRS = subPathIDSTMT.executeQuery();
                    int j = 0;
                    subPathSTMT = db.prepareStatement(readSubPath);
                    subPathSTMT.setInt(1, pathID);
                    while (subPathIDRS.next()) {
                        int subPathID = subPathIDRS.getInt(1);
                        subPathSTMT.setInt(2, subPathID);
                        subPathRS = subPathSTMT.executeQuery();
                        ProcessSubPath subPath = new ProcessSubPath(p);
                        AbstractService as = null;
                        while (subPathRS.next()) {
                            String activity = subPathRS.getString(1);

                            /* Performance PATCH */
                            as = hash.get(activity);
                            /* END Performance PATCH */
                            subPath.addActivity(as);
                        }
                        subPaths.add(subPath);
                        j++;
                    }
                    subPathIDRS.close();
                    ProcessSubPath[] subPathArray = new ProcessSubPath[subPaths.size()];
                    subPaths.toArray(subPathArray);
                    paths.get(i).setSubPaths(subPathArray);


                    //Retrieving Service Occurrence from db
                    serviceOccurrenceStatement.setString(1, p.getProcessName());
                    serviceOccurrenceStatement.setInt(2, pathID);
                    serviceOccRS = serviceOccurrenceStatement.executeQuery();
                    Hashtable<AbstractService, Integer> multipleOcc = new Hashtable<AbstractService, Integer>();
                    while (serviceOccRS.next()) {
                        String serviceName = serviceOccRS.getString(1);
                        int count = serviceOccRS.getInt(2);
                        AbstractService s = asmi.readAbstractService(serviceName);
                        multipleOcc.put(s, count);
                    }
                    paths.get(i).setMultipleOcc(multipleOcc);

                    i++;



                }
                pathRS.close();
                ProcessPath[] pathArray = new ProcessPath[paths.size()];
                paths.toArray(pathArray);
                p.setProcessPaths(pathArray);

                /** End of path management **/
                commitTransactionStmt.close();
            } else {
                throw new NoSuchProcessMDALException();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MDALException(ex.getMessage());
        } finally {
            asmi.close();
            try {
                rs.close();
                rs = null;
                subPathRS.close();
                subPathRS = null;
                subPathIDRS.close();
                subPathIDRS = null;
                querySTMT.close();
                querySTMT = null;
                pathSTMT.close();
                pathSTMT = null;
                subPathSTMT.close();
                subPathSTMT = null;
                subPathIDSTMT.close();
                subPathIDSTMT = null;
                serviceOccRS.close();
                serviceOccurrenceStatement.close();


                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return p;
    }

    /**
     * 
     * @param p
     * @throws BadArgumentsMDALException
     * @throws MDALException
     */
    public int updateProcess(Process p) throws BadArgumentsMDALException, MDALException {
        deleteProcess(p.getProcessName());
        createProcess(p);
        return 0;
    }

    /**
     * Deletes a process from the data back-end. Note that we rely on triggers
     * for sub-path removal
     *
     * @param processName
     * @throws BadArgumentsMDALException
     * @throws NoSuchProcessMDALException
     * @throws MDALException
     */
    public int deleteProcess(String processName) throws BadArgumentsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;
        PreparedStatement deletePathSTMT = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(deleteProcessSTMT);
            querySTMT.setString(1, processName);
            ret = querySTMT.executeUpdate();
            deletePathSTMT = db.prepareStatement(deletePath);
            deletePathSTMT.setString(1, processName);
            ret &= deletePathSTMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                querySTMT = null;

                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * 
     * @param serviceName
     * @return
     * @throws BadArgumentsMDALException
     * @throws NoSuchProcessMDALException
     * @throws MDALException
     */
    public Hashtable<Process, Float> readProcessesByAbSer(AbstractService aserv) throws BadArgumentsMDALException, NoSuchAbstractServiceMDALException, MDALException {
        PreparedStatement querySTMT = null;
        PreparedStatement pathSTMT = null;
        PreparedStatement subPathIDSTMT = null;
        PreparedStatement subPathSTMT = null;
        ResultSet subPathRS = null;
        ResultSet subPathIDRS = null;
        ResultSet pathRS = null;
        MDAL mdal = null;
        AbstractServicesMDALInterface asmi = null;

        ResultSet rs = null;
        Hashtable<Process, Float> processes = null;

        if (aserv == null || aserv.getName() == null || aserv.getName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            mdal = MDAL.getInstance();
            asmi = mdal.getAbstractServicesMDAL();
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            
            querySTMT = db.prepareStatement(readProcessByAbServ);
            querySTMT.setString(1, aserv.getName());
            rs = querySTMT.executeQuery();
            commitTransactionStmt.execute();

            Process p = null;
            processes = new Hashtable<Process, Float>();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    p = new Process();
                    p.setProcessName(rs.getString(1));
                    p.setProcessNS(rs.getString(2));
                    p.setProcessOperation(rs.getString(3));
                    p.setProcessEndpoints(rs.getString(4));
                    p.setProcessGraph(rs.getString(5));
                    p.setStateful(rs.getBoolean(6));

                    /** Managing paths **/
                    pathSTMT = db.prepareStatement(readPath);
                    pathSTMT.setString(1, p.getProcessName());
                    pathRS = pathSTMT.executeQuery();
                    ArrayList<ProcessPath> paths = new ArrayList<ProcessPath>();
                    ArrayList<ProcessSubPath> subPaths = new ArrayList<ProcessSubPath>();
                    int i = 0;

                    while (pathRS.next()) {
                        int pathID = pathRS.getInt(1);
                        paths.add(new ProcessPath(p, pathRS.getDouble(2)));
                        subPathIDSTMT = db.prepareStatement(readSubPathID);
                        subPathIDSTMT.setInt(1, pathID);
                        subPathIDRS = subPathIDSTMT.executeQuery();
                        int j = 0;
                        subPathSTMT = db.prepareStatement(readSubPath);
                        subPathSTMT.setInt(1, pathID);
                        while (subPathIDRS.next()) {
                            int subPathID = subPathIDRS.getInt(1);
                            subPathSTMT.setInt(2, subPathID);
                            subPathRS = subPathSTMT.executeQuery();
                            ProcessSubPath subPath = new ProcessSubPath(p);
                            AbstractService as = null;
                            while (subPathRS.next()) {
                                String activity = subPathRS.getString(1);
                                as = asmi.readAbstractService(activity);
                                subPath.addActivity(as);
                            }
                            subPaths.add(subPath);
                            j++;
                        }
                        subPathIDRS.close();
                        ProcessSubPath[] subPathArray = new ProcessSubPath[subPaths.size()];
                        subPaths.toArray(subPathArray);
                        paths.get(i).setSubPaths(subPathArray);
                        i++;
                    }
                    pathRS.close();
                    ProcessPath[] pathArray = new ProcessPath[paths.size()];
                    paths.toArray(pathArray);
                    p.setProcessPaths(pathArray);
                    /** Paths managed **/
                    processes.put(p, rs.getFloat(7));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                rs = null;
                querySTMT.close();
                querySTMT = null;
                subPathRS.close();
                subPathRS = null;
                subPathIDRS.close();
                subPathIDRS = null;
                subPathRS.close();
                subPathRS = null;
                pathSTMT.close();
                pathSTMT = null;
                subPathSTMT.close();
                subPathSTMT = null;
                subPathIDSTMT.close();
                subPathIDSTMT = null;
                asmi.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
            } catch (Exception e) {
            }
        }

        return processes;
    }

    public List<Process> readAllProcesses() throws BadArgumentsMDALException, NoSuchAbstractServiceMDALException, MDALException {
        List<Process> processes = null;
        Process p = null;
        ResultSet rs = null;
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;

        PreparedStatement pathSTMT = null;
        PreparedStatement subPathIDSTMT = null;
        PreparedStatement subPathSTMT = null;
        ResultSet subPathRS = null;
        ResultSet subPathIDRS = null;
        ResultSet pathRS = null;
        MDAL mdal = null;
        AbstractServicesMDALInterface asmi = null;


        try {
            mdal = MDAL.getInstance();
            asmi = mdal.getAbstractServicesMDAL();
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readAllProcessSTMT);
            rs = query.executeQuery();
            commitTransactionStmt.execute();
            processes = new ArrayList<Process>();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    p = new Process();
                    p.setProcessName(rs.getString(1));
                    p.setProcessNS(rs.getString(2));
                    p.setProcessOperation(rs.getString(3));
                    p.setProcessEndpoints(rs.getString(4));
                    p.setProcessGraph(rs.getString(5));
                    p.setStateful(rs.getBoolean(6));

                    /** Managing paths **/
                    pathSTMT = db.prepareStatement(readPath);
                    pathSTMT.setString(1, p.getProcessName());
                    pathRS = pathSTMT.executeQuery();
                    ArrayList<ProcessPath> paths = new ArrayList<ProcessPath>();
                    ArrayList<ProcessSubPath> subPaths = new ArrayList<ProcessSubPath>();
                    int i = 0;

                    while (pathRS.next()) {
                        int pathID = pathRS.getInt(1);
                        paths.add(new ProcessPath(p, pathRS.getDouble(2)));
                        subPathIDSTMT = db.prepareStatement(readSubPathID);
                        subPathIDSTMT.setInt(1, pathID);
                        subPathIDRS = subPathIDSTMT.executeQuery();
                        int j = 0;
                        subPathSTMT = db.prepareStatement(readSubPath);
                        subPathSTMT.setInt(1, pathID);
                        while (subPathIDRS.next()) {
                            int subPathID = subPathIDRS.getInt(1);
                            subPathSTMT.setInt(2, subPathID);
                            subPathRS = subPathSTMT.executeQuery();
                            ProcessSubPath subPath = new ProcessSubPath(p);
                            AbstractService as = null;
                            while (subPathRS.next()) {
                                String activity = subPathRS.getString(1);
                                as = asmi.readAbstractService(activity);
                                subPath.addActivity(as);
                            }
                            subPaths.add(subPath);
                            j++;
                        }
                        subPathIDRS.close();
                        ProcessSubPath[] subPathArray = new ProcessSubPath[subPaths.size()];
                        subPaths.toArray(subPathArray);
                        paths.get(i).setSubPaths(subPathArray);
                        i++;
                    }
                    pathRS.close();
                    ProcessPath[] pathArray = new ProcessPath[paths.size()];
                    paths.toArray(pathArray);
                    p.setProcessPaths(pathArray);
                    /** End of path management **/
                    processes.add(p);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                rs = null;
                query.close();
                query = null;
                subPathRS.close();
                subPathRS = null;
                subPathIDRS.close();
                subPathIDRS = null;
                subPathRS.close();
                subPathRS = null;
                pathSTMT.close();
                pathSTMT = null;
                subPathSTMT.close();
                subPathSTMT = null;
                subPathIDSTMT.close();
                subPathIDSTMT = null;
                asmi.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
            } catch (Exception e) {
                // ignore -- as we can't do anything about it here
            }
        }

        return processes;

    }
}
