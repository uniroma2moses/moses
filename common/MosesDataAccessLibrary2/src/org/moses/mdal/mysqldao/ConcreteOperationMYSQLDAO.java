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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.AbstractOperation;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractOperationMDALException;
import org.moses.exception.ConcreteOperationAlreadyExistsMDALException;
import org.moses.exception.NoSuchConcreteOperationMDALException;
import org.moses.exception.NoSuchConcreteServiceMDALException;
import org.moses.mdal.ConcreteOperationsMDALInterface;
import org.moses.mdal.Constraints.OperationSLAConstraint;
import org.moses.mdal.Constraints.OperationSLAMonitor;

/**
 *
 * @author dante
 */
public class ConcreteOperationMYSQLDAO extends MYSQLDAO implements ConcreteOperationsMDALInterface {

    private static String createConcreteOperationSTMT = "INSERT " +
            "INTO ConcreteOperation(ConcreteService_ID, Name, isWorking, stateful, AbstractOperation_AbstractService_Name, SLAFile," +
            " Reliability, ResponseTime, Cost, RequestRate, SLAMonitor) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static String readConcreteOperationSTMT = "SELECT  * " +
            "FROM ConcreteOperation " +
            "WHERE ConcreteService_ID=? and Name=? ORDER BY ID";
    private static String readConcOpByAbsOpSTMT = "SELECT  * " +
            "FROM ConcreteOperation " +
            "WHERE AbstractOperation_AbstractService_Name=? and `Name`=? ORDER BY ConcreteService_ID, ID";
    private static String readConcOpByConcSerSTMT = "SELECT  * " +
            "FROM ConcreteOperation " +
            "WHERE ConcreteService_ID=? ORDER BY ID";
    private static String updateConcreteOperationSTMT = "UPDATE ConcreteOperation " +
            "SET isWorking=?, stateful=?, SLAFile=?, Reliability=?, ResponseTime=?, Cost=?, RequestRate=?, SLAMonitor=? " +
            "WHERE ConcreteService_ID=? and Name=?";
    private static String deleteConcreteOperationSTMT = "DELETE " +
            "FROM ConcreteOperation " +
            "WHERE ConcreteService_ID=? and Name=?";
    private static String updateLoadProbabilitySTMT = "UPDATE ConcreteOperation " +
            "SET Probability=? " +
            "WHERE ConcreteService_ID=? and Name=?";


    public ConcreteOperationMYSQLDAO() throws MDALException {
        super();
    }

    public ConcreteOperationMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    public void createConcreteOperation(ConcreteOperation o) throws BadArgumentsMDALException, ConcreteOperationAlreadyExistsMDALException, MDALException {
        if (o.getService() == null || o.getService().getId() == null ||
                o.getOperation() == null || o.getOperation().getName() == null || o.getOperation().getName().length() == 0 ||
                o.getOperation().getService() == null || o.getOperation().getService().getName() == null || o.getOperation().getService().getName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement querySTMT = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;

        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(createConcreteOperationSTMT);
            querySTMT.setInt(1, o.getService().getId());
            querySTMT.setString(2, o.getOperation().getName());
            querySTMT.setBoolean(3, o.getIsWorking());
            querySTMT.setBoolean(4, o.isStateful());
            querySTMT.setString(5, o.getOperation().getService().getName());
            querySTMT.setString(6, o.getSlaFile());
            querySTMT.setDouble(7, o.getReliability());
            querySTMT.setDouble(8, o.getResponseTime());
            querySTMT.setDouble(9, o.getCost());
            querySTMT.setDouble(10, o.getRequestRate());
            querySTMT.setObject(11, o.getSlaMonitor());
            querySTMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new ConcreteOperationAlreadyExistsMDALException();
            } else {
                throw new MDALException(ex.getMessage());
            }
        } finally {
            try {
                querySTMT.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ConcreteOperation readConcreteOperation(int id, String name) throws NoSuchConcreteOperationMDALException, MDALException {

        ConcreteServiceMYSQLDAO reader1 = null;
        AbstractOperationMYSQLDAO reader2 = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        ConcreteOperation co = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            reader1 = new ConcreteServiceMYSQLDAO();
            ConcreteService service = reader1.readConcreteService(id);
            querySTMT = db.prepareStatement(readConcreteOperationSTMT);
            querySTMT.setInt(1, id);
            querySTMT.setString(2, name);
            rs = querySTMT.executeQuery();

            if (rs.next()) {
                co = new ConcreteOperation();
                co.setIsWorking(rs.getBoolean(3));
                co.setStateful(rs.getBoolean(4));
                co.setSlaFile(rs.getString(6));
                co.setReliability(rs.getDouble(7));
                co.setResponseTime(rs.getDouble(8));
                co.setCost(rs.getDouble(9));
                co.setRequestRate(rs.getDouble(10));
                co.setSlaMonitor((Hashtable<OperationSLAMonitor, String>) rs.getObject(11));
                co.setService(service);
                co.setLoadProbability(rs.getDouble(13));
                reader2 = new AbstractOperationMYSQLDAO();
                AbstractOperation operation = reader2.readAbstractOperation(rs.getString(5), name);
                co.setOperation(operation);
            } else {
                throw new NoSuchConcreteOperationMDALException();
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            reader1.close();
            reader2.close();
            try {
                rs.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                querySTMT.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return co;
    }

    public List<ConcreteOperation> readConcreteOperationsByAbOp(AbstractOperation aop) throws BadArgumentsMDALException, NoSuchAbstractOperationMDALException, NoSuchConcreteServiceMDALException, MDALException {
        if (aop == null || aop.getName() == null || aop.getName().equals("") ||
                aop.getService() == null || aop.getService().getName() == null || aop.getService().getName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        ConcreteServiceMYSQLDAO reader3 = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        List<ConcreteOperation> operations = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(readConcOpByAbsOpSTMT);
            querySTMT.setString(1, aop.getService().getName());
            querySTMT.setString(2, aop.getName());
            rs = querySTMT.executeQuery();

            if (rs.isBeforeFirst()) {
                operations = new ArrayList<ConcreteOperation>();
                reader3 = new ConcreteServiceMYSQLDAO();
                while (rs.next()) {
                    ConcreteService service = reader3.readConcreteService(rs.getInt(1));
                    ConcreteOperation co = new ConcreteOperation();
                    co.setIsWorking(rs.getBoolean(3));
                    co.setStateful(rs.getBoolean(4));
                    co.setSlaFile(rs.getString(6));
                    co.setReliability(rs.getDouble(7));
                    co.setResponseTime(rs.getDouble(8));
                    co.setCost(rs.getDouble(9));
                    co.setRequestRate(rs.getDouble(10));
                    co.setSlaMonitor((Hashtable<OperationSLAMonitor, String>) rs.getObject(11));
                    co.setService(service);
                    co.setOperation(aop);
                    co.setLoadProbability(rs.getDouble(13));
                    operations.add(co);
                }

            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            reader3.close();
            try {
                querySTMT.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return operations;
    }

    public List<ConcreteOperation> readConcreteOperationsByConServ(ConcreteService cserv) throws NoSuchConcreteServiceMDALException, NoSuchAbstractOperationMDALException, MDALException {

        AbstractOperationMYSQLDAO reader2 = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        List<ConcreteOperation> operations = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
           
            querySTMT = db.prepareStatement(readConcOpByConcSerSTMT);
            querySTMT.setInt(1, cserv.getId());
            rs = querySTMT.executeQuery();
            if (rs.isBeforeFirst()) {
                operations = new ArrayList<ConcreteOperation>();
                reader2 = new AbstractOperationMYSQLDAO();
                while (rs.next()) {
                    AbstractOperation absOp = reader2.readAbstractOperation(rs.getString(5), rs.getString(2));
                    ConcreteOperation cop = new ConcreteOperation();
                    cop.setIsWorking(rs.getBoolean(3));
                    cop.setStateful(rs.getBoolean(4));
                    cop.setSlaFile(rs.getString(6));
                    cop.setReliability(rs.getDouble(7));
                    cop.setResponseTime(rs.getDouble(8));
                    cop.setCost(rs.getDouble(9));
                    cop.setRequestRate(rs.getDouble(10));
                    cop.setSlaMonitor((Hashtable<OperationSLAMonitor, String>) rs.getObject(11));
                    cop.setService(cserv);
                    cop.setOperation(absOp);
                    cop.setLoadProbability(rs.getDouble(13));
                    operations.add(cop);
                }
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            reader2.close();
            try {
                querySTMT.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return operations;
    }

    public int updateConcreteOperation(ConcreteOperation o) throws BadArgumentsMDALException, MDALException {

        int ret = -1;

        if (o.getService() == null || o.getService().getId() == null ||
                o.getOperation() == null || o.getOperation().getName() == null || o.getOperation().getName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        PreparedStatement querySTMT = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(updateConcreteOperationSTMT);
            querySTMT.setBoolean(1, o.getIsWorking());
            querySTMT.setBoolean(2, o.getStateful());
            querySTMT.setString(3, o.getSlaFile());
            querySTMT.setDouble(4,o.getReliability());
            querySTMT.setDouble(5,o.getResponseTime());
            querySTMT.setDouble(6, o.getCost());
            querySTMT.setDouble(7, o.getRequestRate());
            querySTMT.setObject(8, o.getSlaMonitor());
            querySTMT.setInt(9, o.getService().getId());
            querySTMT.setString(10, o.getOperation().getName());
            ret = querySTMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }   

    public int deleteConcreteOperation(int id, String name) throws MDALException {
        int ret = -1;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        PreparedStatement querySTMT = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(deleteConcreteOperationSTMT);
            querySTMT.setInt(1, id);
            querySTMT.setString(2, name);
            ret = querySTMT.executeUpdate();
            commitTransactionStmt.execute();

        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(ConcreteOperationMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    @Override
    public int updateLoadProbabilty(ConcreteOperation o) throws BadArgumentsMDALException, MDALException {
        int ret = -1;

        if (o.getService() == null || o.getService().getId() == null ||
                o.getOperation() == null || o.getOperation().getName() == null || o.getOperation().getName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement querySTMT = null;
        try {

            querySTMT = db.prepareStatement(updateLoadProbabilitySTMT);
            querySTMT.setDouble(1, o.getLoadProbability());
            querySTMT.setInt(2, o.getService().getId());
            querySTMT.setString(3, o.getOperation().getName());
            ret = querySTMT.executeUpdate();

        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                ex.printStackTrace();
            }
        }

        return ret;
    }
}
