/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.InvocationResult;
import org.moses.entity.InvocationStatistics;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.mdal.QoSDataCollectionMDALInterface;

/**
 *
 * @author arale
 */
public class QoSDataCollectionMYSQLDAO extends MYSQLDAO implements QoSDataCollectionMDALInterface {

    private static String addInvocationResultSTMT = "INSERT " +
            "INTO InvocationResult(ProcessName, ServiceName, OperationName, ResponseTime, Response) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static String readInvocationResultSTMT = "SELECT * " +
            "FROM InvocationResult " +
            "WHERE ProcessName=? AND ServiceName=? AND OperationName=?";
    private static String deleteInvocationResultSTMT = "DELETE " +
            "FROM InvocationResult " +
            "WHERE ProcessName=? AND ServiceName=? AND OperationName=?";
    private static String truncateTableSTMT = "DELETE " +
            "FROM InvocationResult ";
    private static String getAllSTMT = "SELECT * " +
            "FROM InvocationResult";
    private static String getStatisticsSTMT = "select ProcessName,ServiceName,OperationName,avg(ResponseTime),variance(ResponseTime),avg(Response) from InvocationResult where response=1 group by ProcessName, ServiceName, OperationName";

    private static String getNumRequestSTMT = "select ProcessName, count(*) from NumRequest group by ProcessName";

    private static String truncateTableReqSTMT = "DELETE " +
            "FROM NumRequest ";

    public QoSDataCollectionMYSQLDAO() throws MDALException {
        super();
    }

    public int addData(String processName, String operationName, String serviceName) throws MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;
        if (processName == null || operationName == null || serviceName == null) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        if ((processName.equals("")) || (operationName.equals("")) || (serviceName.equals(""))) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(addInvocationResultSTMT);
            querySTMT.setString(1, processName);
            querySTMT.setString(2, serviceName);
            querySTMT.setString(3, operationName);
            querySTMT.setObject(4, null);
            querySTMT.setObject(5, "0");
            ret = querySTMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            try {
                throw new MDALException(ex.getMessage());
            } catch (MDALException ex1) {
                Logger.getLogger(QoSDataCollectionMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
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

    //Only for processes that provide a response
    public int addData(String processName, String operationName, String serviceName, long responseTime) {
        int ret = -1;
        PreparedStatement querySTMT = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;

        if (processName == null || operationName == null || serviceName == null || responseTime <= 0) {
            try {
                throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
            } catch (BadArgumentsMDALException ex) {
                Logger.getLogger(QoSDataCollectionMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if ((processName.equals("")) || (operationName.equals("")) || (serviceName.equals(""))) {
            try {
                throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
            } catch (BadArgumentsMDALException ex) {
                Logger.getLogger(QoSDataCollectionMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(addInvocationResultSTMT);
            querySTMT.setString(1, processName);
            querySTMT.setString(2, serviceName);
            querySTMT.setString(3, operationName);
            querySTMT.setObject(4, responseTime);
            querySTMT.setObject(5, "1");    //Response provided
            ret = querySTMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            try {
                throw new MDALException(ex.getMessage());
            } catch (MDALException ex1) {
                Logger.getLogger(QoSDataCollectionMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
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

    public Vector<InvocationResult> getInvocationResults(String processName, String serviceName, String operationName) throws MDALException {
        Vector<InvocationResult> results = null;
        InvocationResult iR = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();

            querySTMT = db.prepareStatement(readInvocationResultSTMT);
            querySTMT.setString(1, processName);
            querySTMT.setString(2, serviceName);
            querySTMT.setString(3, operationName);
            rs = querySTMT.executeQuery();
            commitTransactionStmt.execute();
            int i = 0;
            if (rs.isBeforeFirst()) {
                results = new Vector<InvocationResult>();
                while (rs.next()) {
                    String response = rs.getString(5);
                    String responseTime;
                    if (response.equals("1")) {
                        responseTime = rs.getString(4);
                        iR = new InvocationResult(processName, serviceName, operationName, Long.parseLong(responseTime));
                    } else {
                        iR = new InvocationResult(processName, serviceName, operationName);
                    }
                    results.add(iR);
                    i++;
                }
            }
            System.out.println("Getting " + i + " elements = " + results.size());
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
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public Vector<InvocationResult> getInvocationResults() throws MDALException {
        Vector<InvocationResult> results = null;
        InvocationResult iR = null;
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(getAllSTMT);

            rs = querySTMT.executeQuery();
            commitTransactionStmt.execute();

            if (rs.isBeforeFirst()) {
                results = new Vector<InvocationResult>();
                while (rs.next()) {
                    String processName = rs.getString(1);
                    String serviceName = rs.getString(2);
                    String operationName = rs.getString(3);
                    String response = rs.getString(5);
                    String responseTime;
                    if (response.equals("1")) {
                        responseTime = rs.getString(4);
                        iR = new InvocationResult(processName, serviceName, operationName, Long.parseLong(responseTime));
                    } else {
                        iR = new InvocationResult(processName, serviceName, operationName);
                    }
                    results.add(iR);
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
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public int deleteResults() throws MDALException {
        PreparedStatement querySTMT = null;
        PreparedStatement query2STMT = null;
        int ret, ret2;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(truncateTableSTMT);
            query2STMT = db.prepareStatement(truncateTableReqSTMT);
            ret = querySTMT.executeUpdate();
            ret2 = query2STMT.executeUpdate();
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                query2STMT.close();
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

    public int deleteResults(String processName, String serviceName, String operationName) throws MDALException {
        PreparedStatement querySTMT = null;
        int ret;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            querySTMT = db.prepareStatement(deleteInvocationResultSTMT);
            querySTMT.setString(1, processName);
            querySTMT.setString(2, processName);
            querySTMT.setString(3, processName);
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

    public Vector<InvocationStatistics> getStatistics() {
        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        Vector<InvocationStatistics> stats = new Vector<InvocationStatistics>();
        try {
            querySTMT = db.prepareStatement(getStatisticsSTMT);
            rs = querySTMT.executeQuery();
            while (rs.next()) {
                InvocationStatistics i = new InvocationStatistics(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
                stats.add(i);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public Hashtable<String, Integer> getNumerOfRequest() {

        PreparedStatement querySTMT = null;
        ResultSet rs = null;
        Hashtable<String, Integer> num = new Hashtable<String, Integer>();
        try {
            querySTMT = db.prepareStatement(getNumRequestSTMT);
            rs = querySTMT.executeQuery();
            while (rs.next()) {
                num.put(rs.getString(1), rs.getInt(2));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return num;
    }
}
