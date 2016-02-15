/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.moses.exception.MDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.UserAlreadyExistsMDALException;
import org.moses.exception.NoSuchUserMDALException;
import org.moses.mdal.UsersMDALInterface;
import org.moses.entity.User;

/**
 *
 * @author dante
 */
public class UsersMYSQLDAO extends MYSQLDAO implements UsersMDALInterface {

    private static String createUserSTMT = "INSERT " +
            "INTO User(Username, Password, Surname, Name) " +
            "VALUES (?, ?, ?, ?)";
    private static String readUserSTMT = "SELECT * " +
            "FROM User " +
            "WHERE Username=?";
    private static String deleteUserSTMT = "DELETE " +
            "FROM User " +
            "WHERE Username=?";
    private static String updateUserSTMT = "UPDATE User " +
            "SET Password=?, Surname=?, Name=? " +
            "WHERE Username=?";

    public UsersMYSQLDAO() throws MDALException {
        super();
    }

    public UsersMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    /**
     * Adds a user in mySQL repository
     *
     * @param u
     * @throws BadArgumentsMDALException
     * @throws UserAlreadyExistsMDALException
     * @throws MDALException
     */
    public int createUser(User u) throws BadArgumentsMDALException, UserAlreadyExistsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;

        if (u.getName() == null || u.getName().length() == 0 ||
                u.getPassword() == null || u.getPassword().length() == 0 ||
                u.getSurname() == null || u.getSurname().length() == 0 ||
                u.getUsername() == null || u.getUsername().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(createUserSTMT);
            querySTMT.setString(1, u.getUsername());
            querySTMT.setString(2, u.getPassword());
            querySTMT.setString(3, u.getSurname());
            querySTMT.setString(4, u.getName());
            ret = querySTMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new UserAlreadyExistsMDALException();
            } else {
                throw new MDALException(ex.getMessage());
            }
        } finally {
            try {
                querySTMT.close();
                querySTMT = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException e) {
            } catch (Exception ex) {
            }
        }
        return ret;
    }

    /**
     * Given a username returns a User ObjectUser u = new User();
     * @param username
     * @return
     * @throws NoSuchUserMDALException
     * @throws MDALException
     */
    public User readUser(String username) throws NoSuchUserMDALException, BadArgumentsMDALException, MDALException {
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        User u = null;

        if (username == null || username.length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(readUserSTMT);
            querySTMT.setString(1, username);
            rs = querySTMT.executeQuery();
            commitTransactionStmt.execute();
            if (rs.next()) {
                u = new User();
                u.setUsername(rs.getString(1));
                u.setPassword(rs.getString(2));
                u.setSurname(rs.getString(3));
                u.setName(rs.getString(4));
            } else {
                throw new NoSuchUserMDALException();
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
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return u;
    }

    /**
     * Given a user, updates informations about that user
     * @param u
     * @throws NoSuchUserMDALException
     * @throws BadArgumentsMDALException
     * @throws MDALException
     */
    public int updateUser(User u) throws BadArgumentsMDALException, MDALException {
        PreparedStatement querySTMT = null;
        int ret = -1;

        if (u.getName() == null || u.getName().length() == 0 || u.getPassword() == null ||
                u.getPassword().length() == 0 || u.getSurname() == null || u.getSurname().length() == 0 || u.getUsername() == null || u.getUsername().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(updateUserSTMT);
            querySTMT.setString(1, u.getPassword());
            querySTMT.setString(2, u.getSurname());
            querySTMT.setString(3, u.getName());
            querySTMT.setString(4, u.getUsername());
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
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Deletes a user and all associated informations
     * @param username
     * @throws NoSuchUserMDALException
     * @throws MDALException
     */
    public int deleteUser(String username) throws BadArgumentsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;

        if (username == null || username.length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(deleteUserSTMT);
            querySTMT.setString(1, username);
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
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}

