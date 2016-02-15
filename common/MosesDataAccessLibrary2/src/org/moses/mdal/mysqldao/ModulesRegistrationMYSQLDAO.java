/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.StringTokenizer;
import java.util.Vector;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.ModuleAlreadyRegisteredMDALException;
import org.moses.exception.NoModulesRegisteredMDALException;
import org.moses.mdal.ModulesRegistrationMDALInterface;
import org.moses.mdal.monitor.Address;

/**
 *
 * @author arale
 */
public class ModulesRegistrationMYSQLDAO extends MYSQLDAO implements ModulesRegistrationMDALInterface {

    private static String createSTMT = "INSERT " +
            "INTO ModulesRegistration(Address, ModuleName) " +
            "VALUES (?, ?)";
    private static String readSTMT = "SELECT * " +
            "FROM ModulesRegistration A ";
    private static String readSTMTFromName = "SELECT * " +
            "FROM ModulesRegistration A " +
            "WHERE A.ModuleName=?";
    private static String deleteSTMT = "DELETE " +
            "FROM ModulesRegistration " +
            "WHERE Address=?";

    public ModulesRegistrationMYSQLDAO() throws MDALException {
        super();
    }

    public void registerModule(String address, String moduleName) throws BadArgumentsMDALException, ModuleAlreadyRegisteredMDALException, MDALException {
        if ((address == null) || (moduleName == null)) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        if ((address.equals("")) || (moduleName.equals(""))) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(createSTMT);
            query.setString(1, address);
            query.setString(2, moduleName);

            query.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new ModuleAlreadyRegisteredMDALException();
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
            }
        }

    }

    public int deregisterModule(String address) throws MDALException, BadArgumentsMDALException {
        int ret = -1;
        if (address == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        if (address.equals("")) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }

        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(deleteSTMT);
            query.setString(1, address);
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
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public Vector<Address> getAddresses() throws NoModulesRegisteredMDALException, MDALException {
        PreparedStatement query = null;
        ResultSet rs = null;
        Vector<Address> addresses = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readSTMT);
            rs = query.executeQuery();
            if (rs.isBeforeFirst()) {
                addresses = new Vector<Address>();
                while (rs.next()) {
                    String address = (rs.getString(1));
                    StringTokenizer addTok = new StringTokenizer(address, ":");
                    String port, baseAddr;
                    baseAddr = addTok.nextToken();
                    port = addTok.nextToken();
                    Address elem = new Address(baseAddr, port);
                    addresses.add(elem);
                }
            } else {
                throw new NoModulesRegisteredMDALException();
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                query.close();
                rs = null;
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return addresses;
    }

    public Vector<Address> getAddresses(String moduleName) throws NoModulesRegisteredMDALException, MDALException {
        PreparedStatement query = null;
        ResultSet rs = null;
        Vector<Address> addresses = null;
         PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readSTMTFromName);
            query.setString(1, moduleName);
            rs = query.executeQuery();
            if (rs.isBeforeFirst()) {
                addresses = new Vector<Address>();
                while (rs.next()) {
                    String address = (rs.getString(1));
                    StringTokenizer addTok = new StringTokenizer(address, ":");
                    String port, baseAddr;
                    baseAddr = addTok.nextToken();
                    port = addTok.nextToken();
                    Address elem = new Address(baseAddr, port);
                    addresses.add(elem);
                }
            } else {
                return null;
            }
            commitTransactionStmt.execute();

        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                query.close();
                rs = null;
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return addresses;
    }
}
