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
import java.util.Hashtable;
import java.util.List;
import org.moses.entity.Process;
import org.moses.entity.SLA;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchProcessMDALException;
import org.moses.exception.NoSuchSLAMDALException;
import org.moses.exception.NoSuchUserMDALException;
import org.moses.exception.SLAAlreadyExistsMDALException;
import org.moses.mdal.Constraints.ProcessSLAConstraint;
import org.moses.mdal.Constraints.ProcessSLAMonitor;
import org.moses.mdal.SLAsMDALInterface;

/**
 *
 * @author dante
 */
public class SLAsMYSQLDAO extends MYSQLDAO implements SLAsMDALInterface {

    private static String createSLASTMT = "INSERT " +
            "INTO SLA(ProcessClass, Process_ProcessName, SLAFile, SLAConstraints, SLAMonitor) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static String readSLASTMT = "SELECT * " +
            "FROM SLA " +
            "WHERE ProcessClass=? AND Process_ProcessName=?";
    private static String deleteSLASTMT = "DELETE " +
            "FROM SLA " +
            "WHERE ProcessClass=? AND Process_ProcessName=?";
    private static String updateSLASTMT = "UPDATE SLA " +
            "SET SLAFile=? , SLAConstraints=?, SLAMonitor=? " +
            "WHERE ProcessClass=? AND Process_ProcessName=?";
    private static String readSLASByPrSTMT = "SELECT * " +
            "FROM SLA " +
            "WHERE Process_ProcessName=? ORDER BY ProcessClass";

    public SLAsMYSQLDAO() throws MDALException {
        super();
    }

    public SLAsMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    /**
     *
     * @param sla
     * @throws BadArgumentsMDALException
     * @throws SLAAlreadyExistsMDALException
     * @throws NoSuchProcessMDALException
     * @throws MDALException
     */
    public int createSLA(SLA sla) throws BadArgumentsMDALException, SLAAlreadyExistsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;
        if (sla.getProcessClass() == null || sla.getProcessClass().length() == 0 ||
                sla.getProcess() == null || sla.getProcess().getProcessName() == null || sla.getProcess().getProcessName().length() == 0 ||
                sla.getSlaFile() == null || sla.getSlaFile().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(createSLASTMT);
            querySTMT.setString(1, sla.getProcessClass());
            querySTMT.setString(2, sla.getProcess().getProcessName());
            querySTMT.setString(3, sla.getSlaFile());
            querySTMT.setObject(4, sla.getSlaConstraints());
            querySTMT.setObject(5, sla.getSlaMonitor());
            ret = querySTMT.executeUpdate();
            commitTransactionStmt.execute();

        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new SLAAlreadyExistsMDALException();
            }
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                querySTMT = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
            } catch (Exception e) {
            }
        }

        return ret;
    }

    /**
     *
     * @param processName
     * @param processClass
     * @return
     * @throws NoSuchUserMDALException
     * @throws NoSuchProcessClassMDALException
     * @throws NoSuchProcessMDALException
     * @throws MDALException
     */
    public SLA readSLA(String processName, String processClass) throws NoSuchSLAMDALException, NoSuchProcessMDALException, MDALException {
        SLA sla = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        ProcessesMYSQLDAO reader = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(readSLASTMT);
            querySTMT.setString(1, processClass);
            querySTMT.setString(2, processName);
            rs = querySTMT.executeQuery();
            commitTransactionStmt.execute();
            if (rs.next()) {
                sla = new SLA();
                sla.setProcessClass(rs.getString(1));
                reader = new ProcessesMYSQLDAO(/*db*/);
                Process p = reader.readProcess(processName);
                sla.setProcess(p);
                sla.setSlaFile(rs.getString(3));
                sla.setSlaConstraints((Hashtable<ProcessSLAConstraint, String>) rs.getObject(4));
                sla.setSlaMonitor((Hashtable<ProcessSLAMonitor, String>) rs.getObject(5));
            } else {
                throw new NoSuchSLAMDALException();
            }
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            reader.close();
            reader = null;
            try {
                rs.close();
                rs = null;
                querySTMT.close();
                querySTMT = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
            } catch (Exception e) {
            }
        }

        return sla;
    }

    /**
     *
     * @param sla
     * @throws BadArgumentsMDALException
     * @throws NoSuchProcessMDALException
     * @throws NoSuchProcessClassMDALException
     * @throws NoSuchSLAMDALException
     * @throws MDALException
     */
    public int updateSLA(SLA sla) throws BadArgumentsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;
        if (sla.getProcessClass() == null || sla.getProcessClass().length() == 0 ||
                sla.getProcess() == null || sla.getProcess().getProcessName() == null || sla.getProcess().getProcessName().length() == 0 ||
                sla.getSlaFile() == null || sla.getSlaFile().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(updateSLASTMT);
            querySTMT.setString(1, sla.getSlaFile());
            querySTMT.setString(4, sla.getProcessClass());
            querySTMT.setString(5, sla.getProcess().getProcessName());
            querySTMT.setObject(2, sla.getSlaConstraints());
            querySTMT.setObject(3, sla.getSlaMonitor());
            ret = querySTMT.executeUpdate();
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
            } catch (Exception e) {
            }
        }

        return ret;
    }

    /**
     * 
     * @param processName
     * @param processClass
     * @throws NoSuchProcessMDALException
     * @throws NoSuchProcessClassMDALException
     * @throws BadArgumentsMDALException
     * @throws MDALException
     */
    public int deleteSLA(String processName, String processClass) throws MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(deleteSLASTMT);
            querySTMT.setString(1, processClass);
            querySTMT.setString(2, processName);
            ret = querySTMT.executeUpdate();
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
            } catch (Exception e) {
            }
        }

        return ret;
    }

    public List<SLA> readSLAsByPr(Process p) throws NoSuchProcessMDALException, MDALException {
        List<SLA> slas = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;

        if (p == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(readSLASByPrSTMT);
            querySTMT.setString(1, p.getProcessName());
            rs = querySTMT.executeQuery();
            commitTransactionStmt.execute();

            SLA sla = null;

            if (rs.isBeforeFirst()) {
                slas = new ArrayList<SLA>();
                while (rs.next()) {
                    sla = new SLA();
                    sla.setProcessClass(rs.getString(1));
                    sla.setProcess(p);
                    sla.setSlaFile(rs.getString(3));
                    sla.setSlaConstraints((Hashtable<ProcessSLAConstraint, String>) rs.getObject(4));
                    sla.setSlaMonitor((Hashtable<ProcessSLAMonitor, String>) rs.getObject(5));
                    slas.add(sla);
                }
            }


        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                rs = null;
                querySTMT.close();
                querySTMT = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
            } catch (Exception e) {
            }
        }
        return slas;
    }
}
