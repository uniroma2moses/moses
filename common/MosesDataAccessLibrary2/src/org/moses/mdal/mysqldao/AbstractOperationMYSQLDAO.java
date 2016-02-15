/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.AbstractOperation;
import org.moses.entity.AbstractService;
import org.moses.exception.AbstractOperationAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractOperationMDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.mdal.AbstractOperationsMDALInterface;

/**
 *
 * @author valeryo
 */
public class AbstractOperationMYSQLDAO extends MYSQLDAO implements AbstractOperationsMDALInterface {

    private static String createSTMT = "INSERT " +
            "INTO AbstractOperation(AbstractService_Name, Name) " +
            "VALUES (?, ?)";
    private static String readSTMT = "SELECT * " +
            "FROM AbstractOperation A " +
            "WHERE A.Name=? AND A.AbstractService_Name=?";
    private static String readBySerSTMT = "SELECT * " +
            "FROM AbstractOperation A " +
            "WHERE A.AbstractService_Name=?";
    private static String deleteSTMT = "DELETE " +
            "FROM AbstractOperation A " +
            "WHERE A.Name=? AND A.AbstractService_Name=?";

    public AbstractOperationMYSQLDAO() throws MDALException {
        super();
    }

    public AbstractOperationMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    public void createAbstractOperation(AbstractOperation abstractOp) throws AbstractOperationAlreadyExistsMDALException, BadArgumentsMDALException, MDALException {
        if (abstractOp.getName() == null || abstractOp.getService().getName() == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (abstractOp.getName().equals("") || abstractOp.getService().getName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(createSTMT);
            query.setString(1, abstractOp.getService().getName());
            query.setString(2, abstractOp.getName());
            query.executeUpdate();
            commitTransactionStmt.execute();

        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new AbstractOperationAlreadyExistsMDALException();
            } else {
                throw new MDALException(ex.getMessage());
            }
        } finally {
            try {
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
            }
        }

    }

    public AbstractOperation readAbstractOperation(String serviceName, String operationName) throws NoSuchAbstractOperationMDALException, NoSuchAbstractServiceMDALException, BadArgumentsMDALException, MDALException {
        if (serviceName == null || operationName == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (serviceName.equals("") || operationName.equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        AbstractOperation op = null;
        AbstractServiceMYSQLDAO reader = null;
        PreparedStatement query = null;
        ResultSet rs = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            reader = new AbstractServiceMYSQLDAO();
            AbstractService service = reader.readAbstractService(serviceName);

            query = db.prepareStatement(readSTMT);
            query.setString(1, operationName);
            query.setString(2, serviceName);
            rs = query.executeQuery();

            if (rs.next()) {
                op = new AbstractOperation();
                op.setName(rs.getString(2));
                op.setService(service);
            } else {
                throw new NoSuchAbstractOperationMDALException();
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            reader.close();
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

        return op;
    }

    public List<AbstractOperation> readAbstractOperationsByAbSer(AbstractService aserv) throws NoSuchAbstractOperationMDALException, NoSuchAbstractServiceMDALException, BadArgumentsMDALException, MDALException {
        if (aserv == null || aserv.getName() == null || aserv.getName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        ResultSet rs = null;
        List<AbstractOperation> operations = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readBySerSTMT);
            query.setString(1, aserv.getName());
            rs = query.executeQuery();
            if (rs.isBeforeFirst()) {
                operations = new ArrayList<AbstractOperation>();
                while (rs.next()) {
                    AbstractOperation operation = new AbstractOperation();
                    operation.setService(aserv);
                    operation.setName(rs.getString(2));
                    operations.add(operation);
                }
            } else {
                throw new NoSuchAbstractOperationMDALException();
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

        return operations;
    }

    //ToDO, for now this method is not necessary and we suppose that we cannot change the primary key
    public int updateAbstractOperation(AbstractOperation abstractOp) throws BadArgumentsMDALException, MDALException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int deleteAbstractOperation(String serviceName, String operationName) throws BadArgumentsMDALException, MDALException {
        int ret = -1;
        if (serviceName == null || operationName == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        if (serviceName.equals("") || operationName.equals("")) {
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
            query.setString(1, operationName);
            query.setString(2, serviceName);
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
}
