/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.AbstractOperation;
import org.moses.entity.AbstractService;
import org.moses.entity.Process;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.AbstractOperationAlreadyExistsMDALException;
import org.moses.exception.AbstractServiceAlreadyExistsMDALException;
import org.moses.exception.NoSuchProcessMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.mdal.AbstractServicesMDALInterface;

/**
 *
 * @author valeryo
 */
public class AbstractServiceMYSQLDAO extends MYSQLDAO implements AbstractServicesMDALInterface {

    private static String createSTMT = "INSERT " +
            "INTO AbstractService(Name, NameSpace) " +
            "VALUES (?, ?)";
    private static String readSTMT = "SELECT * " +
            "FROM AbstractService A " +
            "WHERE A.Name=?";
    private static String readByProcSTMT = "SELECT S.`Name`, S.NameSpace, A.AverageVisit " +
            "FROM Process_has_AbstractService A, AbstractService S " +
            "WHERE A.Process_ProcessName=? AND A.AbstractService_Name=S.`Name`";
    private static String updateSTMT = "UPDATE AbstractService A " +
            "SET A.NameSpace=?" +
            "WHERE A.`Name`=?";
    private static String deleteSTMT = "DELETE " +
            "FROM AbstractService A " +
            "WHERE A.Name=?";

    public AbstractServiceMYSQLDAO() throws MDALException {
        super();
    }

    public AbstractServiceMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    public void createAbstractService(AbstractService abstractSer) throws AbstractOperationAlreadyExistsMDALException, AbstractServiceAlreadyExistsMDALException, BadArgumentsMDALException, MDALException {
        if (abstractSer.getName() == null || abstractSer.getName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (abstractSer.getNameSpace() == null || abstractSer.getNameSpace().equals("") || abstractSer.getOperations() == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        AbstractOperationMYSQLDAO writer = null;
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(createSTMT);
            query.setString(1, abstractSer.getName());
            query.setString(2, abstractSer.getNameSpace());
            query.executeUpdate();
            commitTransactionStmt.execute();

            Iterator<AbstractOperation> operations = abstractSer.getOperations().iterator();
            writer = new AbstractOperationMYSQLDAO();
            startTransactionStmt.execute();
            while (operations.hasNext()) {
                writer.createAbstractOperation(operations.next());
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                /** This abstract service already exists... hoping for the best */
                //throw new AbstractServiceAlreadyExistsMDALException();
            } else {
                ex.printStackTrace();
                throw new MDALException(ex.getMessage());
            }
        } finally {

            try {
                writer.close();
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (Exception ex) {
                // ignore -- as we can't do anything about it here
            }
        }

    }

    public AbstractService readAbstractService(String serviceName) throws NoSuchAbstractServiceMDALException, BadArgumentsMDALException, MDALException {
        if (serviceName == null || serviceName.equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement query = null;
        ResultSet rs = null;
        AbstractService service = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readSTMT);
            query.setString(1, serviceName);
            rs = query.executeQuery();

            if (rs.next()) {
                service = new AbstractService();
                service.setName(rs.getString(1));
                service.setNameSpace(rs.getString(2));
            } else {
                throw new NoSuchAbstractServiceMDALException("ERROR: no such AbstractService!");
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
            }
            try {
                query.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
            }

        }
        return service;
    }

    public Hashtable<AbstractService, Float> readAbstractServicesByPr(Process process) throws NoSuchProcessMDALException, BadArgumentsMDALException, MDALException {
        if (process == null || process.getProcessName() == null || process.getProcessName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        Hashtable<AbstractService, Float> services = null;
        PreparedStatement query = null;
        ResultSet rs = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readByProcSTMT);
            query.setString(1, process.getProcessName());
            rs = query.executeQuery();

            if (rs.isBeforeFirst()) {
                services = new Hashtable<AbstractService, Float>();
                while (rs.next()) {
                    AbstractService service = new AbstractService();                    
                    service.setName(rs.getString(1));
                    service.setNameSpace(rs.getString(2));
                    services.put(service, rs.getFloat(3));
                }
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                // ignore -- as we can't do anything about it here
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                // ignore -- as we can't do anything about it here
            }
        }
        return services;
    }

    //we suppose that we cannot change the primary key
    public int updateAbstractService(AbstractService abstractSer) throws BadArgumentsMDALException, MDALException {

        int ret = -1;

        if (abstractSer.getName() == null || abstractSer.getName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (abstractSer.getNameSpace() == null || abstractSer.getNameSpace().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(updateSTMT);
            query.setString(1, abstractSer.getNameSpace());
            query.setString(2, abstractSer.getName());
            ret = query.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
            }
        }
        return ret;
    }

    public int deleteAbstractService(String serviceName) throws BadArgumentsMDALException, MDALException {

        int ret = -1;

        if (serviceName == null || serviceName == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(deleteSTMT);
            query.setString(1, serviceName);
            ret = query.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(AbstractServiceMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
}
