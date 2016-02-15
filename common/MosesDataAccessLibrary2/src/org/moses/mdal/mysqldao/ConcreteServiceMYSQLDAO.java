/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.AbstractService;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.exception.ConcreteOperationAlreadyExistsMDALException;
import org.moses.exception.ConcreteServiceAlreadyExistsMDALException;
import org.moses.exception.NoSuchConcreteServiceMDALException;
import org.moses.mdal.ConcreteServicesMDALInterface;

/**
 *
 * @author dante
 */
public class ConcreteServiceMYSQLDAO extends MYSQLDAO implements ConcreteServicesMDALInterface {

    private static String createConcreteServiceSTMT = "INSERT " +
            "INTO ConcreteService(ID, endpointURL, wsdlURL, expireDate, AbstractService_Name, AbstractService_NameSpace) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static String readConcreteServiceSTMT = "SELECT  * " +
            "FROM ConcreteService " +
            "WHERE ID=?";
    private static String readAllConcreteServiceSTMT = "SELECT  * " +
            "FROM ConcreteService ORDER BY ID";
    private static String updateConcreteServiceSTMT = "UPDATE ConcreteServiceSTMT " +
            "SET endpointURL=?, wsdlURL=?, expireDate=?, AbstractService_Name=?, AbstractService_NameSpace=? " +
            "WHERE ID=?";
    private static String deleteConcreteServiceSTMT = "DELETE " +
            "FROM ConcreteService " +
            "WHERE ID=?";
    private static String readConcreteServiceByAbSerSTMT = "SELECT  * " +
            "FROM ConcreteService " +
            "WHERE AbstractService_Name=? ORDER BY ID";
    private static String readAbstractServiceByConServSTMT = "SELECT `Name` , NameSpace " +
            "FROM AbstractService, ConcreteService " +
            "WHERE ID=? AND AbstractService_Name = `Name` AND AbstractService_NameSpace = NameSpace ";

    public ConcreteServiceMYSQLDAO() throws MDALException {
        super();
    }

    public ConcreteServiceMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    public void createConcreteService(ConcreteService s) throws BadArgumentsMDALException, ConcreteServiceAlreadyExistsMDALException, ConcreteOperationAlreadyExistsMDALException, MDALException {
        PreparedStatement querySTMT = null;
        ConcreteOperationMYSQLDAO writer = null;

        if (s.getId() == null ||
                s.getEndpointURL() == null || s.getEndpointURL().length() == 0 ||
                s.getWsdlURL() == null || s.getWsdlURL().length() == 0 ||
                s.getService() == null ||
                s.getOperations() == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(createConcreteServiceSTMT);
            querySTMT.setInt(1, s.getId());
            querySTMT.setString(2, s.getEndpointURL());
            querySTMT.setString(3, s.getWsdlURL());
            querySTMT.setDate(4, new Date(s.getExpireDate().getTime()));
            querySTMT.setString(5, s.getService().getName());
            querySTMT.setString(6, s.getService().getNameSpace());
            querySTMT.executeUpdate();
            commitTransactionStmt.execute();

            Iterator<ConcreteOperation> it = s.getOperations().iterator();
            startTransactionStmt.execute();
            while (it.hasNext()) {
                ConcreteOperation o = it.next();
                writer = new ConcreteOperationMYSQLDAO(/*db*/);
                writer.createConcreteOperation(o);
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_ENTRY_FOR_KEY) {
                throw new ConcreteServiceAlreadyExistsMDALException();
            } else {
                throw new MDALException(ex.getMessage());
            }
        } finally {
            writer.close();
            try {
                querySTMT.close();
                querySTMT = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
            } catch (Exception e) {
            }
        }

    }

    public ConcreteService readConcreteService(int id) throws NoSuchConcreteServiceMDALException, MDALException {
        ConcreteService cs = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(readConcreteServiceSTMT);
            querySTMT.setInt(1, id);
            rs = querySTMT.executeQuery();

            if (rs.next()) {
                cs = new ConcreteService();
                cs.setId(rs.getString(1));
                cs.setEndpointURL(rs.getString(2));
                cs.setWsdlURL(rs.getString(3));
                cs.setExpireDate(rs.getDate(4));

            } else {
                throw new NoSuchConcreteServiceMDALException();
            }
            commitTransactionStmt.execute();
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

        return cs;
    }

    public int updateConcreteService(ConcreteService s) throws BadArgumentsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;

        if (s.getId() == null ||
                s.getEndpointURL() == null || s.getEndpointURL().length() == 0 ||
                s.getWsdlURL() == null || s.getWsdlURL().length() == 0 ||
                s.getService() == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(updateConcreteServiceSTMT);
            querySTMT.setString(1, s.getEndpointURL());
            querySTMT.setString(2, s.getWsdlURL());
            querySTMT.setDate(3, (Date) s.getExpireDate());
            querySTMT.setString(4, s.getService().getName());
            querySTMT.setString(5, s.getService().getNameSpace());
            querySTMT.setInt(6, s.getId());
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

    public int deleteConcreteService(int id) throws MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;

        try {
            querySTMT = db.prepareStatement(deleteConcreteServiceSTMT);
            querySTMT.setInt(1, id);
            ret = querySTMT.executeUpdate();

        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                querySTMT = null;
            } catch (SQLException ex) {
            } catch (Exception e) {
            }
        }

        return ret;
    }

    public List<ConcreteService> readConcreteServicesByAbServ(AbstractService aserv) throws NoSuchAbstractServiceMDALException, MDALException {
        PreparedStatement querySTMT = null;
        List<ConcreteService> concreteServices = null;
        ResultSet rs = null;

        if (aserv == null || aserv.getName() == null || aserv.getName().length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
          PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(readConcreteServiceByAbSerSTMT);
            querySTMT.setString(1, aserv.getName());
            rs = querySTMT.executeQuery();
            ConcreteService c = null;
            concreteServices = new ArrayList<ConcreteService>();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    c = new ConcreteService();
                    c.setId(rs.getInt(1));
                    c.setEndpointURL(rs.getString(2));
                    c.setWsdlURL(rs.getString(3));
                    c.setExpireDate(rs.getDate(4));
                    c.setService(aserv);
                    concreteServices.add(c);
                }
            }
            commitTransactionStmt.execute();

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

        return concreteServices;
    }

    public AbstractService readAbstractServiceByConServ(ConcreteService cserv) throws MDALException {
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        AbstractService service = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;

        try {
             startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(readAbstractServiceByConServSTMT);
            querySTMT.setInt(1, cserv.getId());
            rs = querySTMT.executeQuery();
            if (rs.next()) {
                service = new AbstractService();
                service.setName(rs.getString(1));
                service.setNameSpace(rs.getString(2));
            }
            commitTransactionStmt.execute();

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

        return service;
    }

    public List<ConcreteService> readAllConcreteServices() throws NoSuchAbstractServiceMDALException, MDALException {

        List<ConcreteService> services = null;
        ConcreteService c = null;
        ResultSet rs = null;
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readAllConcreteServiceSTMT);
            rs = query.executeQuery();

            services = new ArrayList<ConcreteService>();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    c = new ConcreteService();
                    c.setId(rs.getInt(1));
                    c.setEndpointURL(rs.getString(2));
                    c.setWsdlURL(rs.getString(3));
                    c.setExpireDate(rs.getDate(4));
                    services.add(c);
                }
            }
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(ConcreteServiceMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                rs = null;
                query.close();
                query = null;
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                // ignore -- as we can't do anything about it here
            } catch (Exception e) {
                // ignore -- as we can't do anything about it here
            }
        }

        return services;
    }
}
