/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.Agreement;
import org.moses.entity.SLA;
import org.moses.entity.User;
import org.moses.exception.AgreementAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.NoSuchSLAMDALException;
import org.moses.exception.NoSuchUserMDALException;
import org.moses.exception.MDALException;
import org.moses.mdal.AgreementsMDALInterface;
import org.moses.mdal.Constraints.ClientSLAConstraint;
import org.moses.mdal.Constraints.ClientSLAMonitor;

/**
 *
 * @author valeryo
 */
public class AgreementMYSQLDAO extends MYSQLDAO implements AgreementsMDALInterface {

    private static String createSTMT = "INSERT " +
            "INTO Agreement(User_Username, SLA_ProcessClass, SLA_Process_ProcessName, ExpireDate, ArrivalRate, SLAConstraints, SLAMonitor)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static String readSTMT = "SELECT * " +
            "FROM Agreement A " +
            "WHERE A.User_Username=? AND A.SLA_ProcessClass=? AND A.SLA_Process_ProcessName=?";
    private static String readByUserSTMT = "SELECT * " +
            "FROM Agreement A " +
            "WHERE A.User_Username=?";
    private static String readBySLASTMT = "SELECT * " +
            "FROM Agreement A " +
            "WHERE A.SLA_ProcessClass=? AND A.SLA_Process_ProcessName=?";
    private static String updateSTMT = "UPDATE Agreement A " +
            "SET A.ExpireDate=?, A.ArrivalRate=?, A.SLAConstraints=?, A.SLAMonitor=? " +
            "WHERE A.User_Username=? AND A.SLA_ProcessClass=? AND A.SLA_Process_ProcessName=?";
    private static String deleteSTMT = "DELETE " +
            "FROM Agreement " +
            "WHERE User_Username=? AND SLA_ProcessClass=? AND SLA_Process_ProcessName=?";

    public AgreementMYSQLDAO() throws MDALException {
        super();
    }

    public AgreementMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    public void createAgreement(Agreement agreement) throws AgreementAlreadyExistsMDALException, BadArgumentsMDALException, MDALException {

        User user = agreement.getUser();
        SLA sla = agreement.getSla();

        if (user.getName() == null || user.getName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (sla.getProcess().getProcessName() == null || sla.getProcess().getProcessName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (sla.getProcessClass() == null || sla.getProcessClass().equals("")) {
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
            query.setString(1, user.getUsername());
            query.setString(2, sla.getProcessClass());
            query.setString(3, sla.getProcess().getProcessName());
            query.setDate(4, new Date(agreement.getExpireDate().getTime()));
            query.setDouble(5, agreement.getArrivalRate());//TOFIX arrivalRate TYPE
            query.setObject(6, agreement.getSlaConstraints());
            query.setObject(7, agreement.getSlaMonitor());
            query.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new AgreementAlreadyExistsMDALException();
            } else {
                throw new MDALException(ex.getMessage());
            }
        } finally {
            try {
                query.close();
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                // ignore -- as we can't do anything about it here
            }
        }

    }

    public Agreement readAgreement(String userName, String processName, String processClass) throws NoSuchUserMDALException, NoSuchSLAMDALException, BadArgumentsMDALException, MDALException {

        if (userName == null || userName.equals("") || processName == null || processName.equals("") || processClass == null || processClass.equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        UsersMYSQLDAO readerUser = null;
        SLAsMYSQLDAO readerSLA = null;
        PreparedStatement query = null;
        ResultSet rs = null;
        Agreement agreement = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            readerUser = new UsersMYSQLDAO();
            User user = readerUser.readUser(userName);
            readerSLA = new SLAsMYSQLDAO();
            SLA sla = readerSLA.readSLA(processName, processClass);
            query = db.prepareStatement(readSTMT);
            query.setString(1, userName);
            query.setString(2, processClass);
            query.setString(3, processName);
            rs = query.executeQuery();

            if (rs.next()) {
                agreement = new Agreement();
                agreement.setUsername(user);
                agreement.setSla(sla);
                agreement.setExpireDate(rs.getDate(4));
                agreement.setArrivalRate(rs.getDouble(5));
                agreement.setSlaConstraints((Hashtable<ClientSLAConstraint, String>) rs.getObject(6));
                agreement.setSlaMonitor((Hashtable<ClientSLAMonitor, String>) rs.getObject(7));
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
            //throw new MDALException(ex.getMessage());
        } finally {
            readerUser.close();
            readerSLA.close();
            try {
                rs.close();
                rs = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                // ignore -- as we can't do anything about it here
            }
            try {
                query.close();
                query = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
                // ignore -- as we can't do anything about it here
            }
        }

        return agreement;
    }

    public List<Agreement> readAgreementsByUser(User user) throws NoSuchUserMDALException, BadArgumentsMDALException, MDALException {
        if (user == null || user.getUsername() == null || user.getUsername().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        SLAsMYSQLDAO readerSLA = null;
        PreparedStatement query = null;
        ResultSet rs = null;
        List<Agreement> agreements = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();

            query = db.prepareStatement(readByUserSTMT);
            query.setString(1, user.getUsername());
            rs = query.executeQuery();
            if (rs.isBeforeFirst()) {
                readerSLA = new SLAsMYSQLDAO();
                agreements = new ArrayList<Agreement>();
                while (rs.next()) {
                    SLA sla = readerSLA.readSLA(rs.getString(3), rs.getString(2));
                    Agreement agreement = new Agreement();
                    agreement.setUsername(user);
                    agreement.setSla(sla);
                    agreement.setExpireDate(rs.getDate(4));
                    agreement.setArrivalRate(rs.getDouble(5));
                    agreement.setSlaConstraints((Hashtable<ClientSLAConstraint, String>) rs.getObject(6));
                    agreement.setSlaMonitor((Hashtable<ClientSLAMonitor, String>) rs.getObject(7));
                    agreements.add(agreement);
                }
            }
            commitTransactionStmt.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new MDALException(ex.getMessage());
        } finally {
            readerSLA.close();
            try {
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                ex.printStackTrace();
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                ex.printStackTrace();
            }
        }
        return agreements;
    }

    public List<Agreement> readAgreementsBySla(SLA sla) throws NoSuchSLAMDALException, BadArgumentsMDALException, MDALException {
        if (sla == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        UsersMYSQLDAO readerUser = null;
        PreparedStatement query = null;
        ResultSet rs = null;
        List<Agreement> agreements = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareCall(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readBySLASTMT);
            query.setString(1, sla.getProcessClass());
            query.setString(2, sla.getProcess().getProcessName());
            rs = query.executeQuery();

            if (rs.isBeforeFirst()) {
                readerUser = new UsersMYSQLDAO();
                agreements = new ArrayList<Agreement>();
                while (rs.next()) {
                    User user = readerUser.readUser(rs.getString(1));
                    Agreement agreement = new Agreement();
                    agreement.setUsername(user);
                    agreement.setSla(sla);
                    agreement.setExpireDate(rs.getDate(4));
                    agreement.setArrivalRate(rs.getDouble(5));
                    agreement.setSlaConstraints((Hashtable<ClientSLAConstraint, String>) rs.getObject(6));
                    agreement.setSlaMonitor((Hashtable<ClientSLAMonitor, String>) rs.getObject(7));
                    agreements.add(agreement);
                }
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            readerUser.close();
            try {
                query.close();
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(AgreementMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
                query = null;
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(AgreementMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return agreements;
    }

    public int updateAgreement(Agreement agreement) throws BadArgumentsMDALException, MDALException {

        int ret = -1;
        User user = agreement.getUser();
        SLA sla = agreement.getSla();

        if (user.getName() == null || user.getName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (sla.getProcess().getProcessName() == null || sla.getProcess().getProcessName().equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        if (sla.getProcessClass() == null || sla.getProcessClass().equals("")) {
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
            query.setString(5, user.getUsername());
            query.setString(6, sla.getProcessClass());
            query.setString(7, sla.getProcess().getProcessName());
            query.setDate(1, (Date) agreement.getExpireDate());
            query.setDouble(2, agreement.getArrivalRate());//TOFIX arrivalRate TYPE
            query.setObject(3, agreement.getSlaConstraints());
            query.setObject(4, agreement.getSlaMonitor());
            ret = query.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                query.close();
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(AgreementMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    public int deleteAgreement(String userName, String processName, String processClass) throws BadArgumentsMDALException, MDALException {

        int ret = -1;

        if (userName == null || userName.equals("") || processName == null || processName.equals("") || processClass == null || processClass.equals("")) {
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
            query.setString(1, userName);
            query.setString(2, processClass);
            query.setString(3, processName);
            ret = query.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                query.close();
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
                Logger.getLogger(AgreementMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
}
