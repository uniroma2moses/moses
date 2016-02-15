/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoViolationsRegisteredMDALException;
import org.moses.mdal.PerceivedQoSMDALInterface;
import org.moses.mdal.monitor.ViolationResult;

/**
 *
 * @author arale
 */
public class PerceivedQoSMYSQLDAO extends MYSQLDAO implements PerceivedQoSMDALInterface {

    private static String createSTMT = "INSERT " +
            "INTO PerceivedQoS(ProcessName, ServiceName, OperationName, AvgRespTime, Reliability) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static String readSTMT = "SELECT * " +
            "FROM PerceivedQoS A ";
    private static String readSTMTFromProcessName = "SELECT * " +
            "FROM PerceivedQoS A " +
            "WHERE A.ProcessName=?";
    private static String deleteSTMT = "DELETE " +
            "FROM PerceivedQoS " +
            "WHERE ProcessName=? AND ServiceName=? AND OperationName=?";
    private static String truncateSTMT = "DELETE FROM PerceivedQoS";

    public PerceivedQoSMYSQLDAO() throws MDALException {
        super();
    }

    public int truncateTable() throws MDALException {
        int ret = -1;
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = (PreparedStatement) db.prepareStatement(truncateSTMT);

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
                // ignore -- as we can't do anything about it here
            }
        }

        return ret;
    }

    //Adds in the db the data contained in the vector vR
    public void addData(Vector<ViolationResult> vR) throws BadArgumentsMDALException, MDALException {
        Iterator vRIt = vR.iterator();
        while (vRIt.hasNext()) {
            ViolationResult elem = (ViolationResult) vRIt.next();
            Hashtable<String, Double> violForElem = elem.getViolation();
            if (violForElem.get(RELIABILITY) != null) {
                double relValue = (violForElem.get(RELIABILITY)).doubleValue();
                if (violForElem.get(RESPONSE_TIME) != null) {
                    double respTimeValue = violForElem.get(RESPONSE_TIME).doubleValue();
                    addData(elem.getProcessName(), elem.getServiceName(), elem.getOperationName(), respTimeValue, relValue);
                } else {
                    addData(elem.getProcessName(), elem.getServiceName(), elem.getOperationName(), -1, relValue);
                }
            } else if (violForElem.get(RESPONSE_TIME) != null) {
                double respTimeValue = elem.getViolation().get(RESPONSE_TIME).doubleValue();
                addData(elem.getProcessName(), elem.getServiceName(), elem.getOperationName(), respTimeValue, -1);
            } else {
                System.out.println("No violation for entry: " + elem.getProcessName() + " " + elem.getServiceName() + " " + elem.getOperationName());
            }
        }
    }

    //Adds in the db the given violation
    public void addData(String processName, String serviceName, String operationName, double avgResponseTime, double reliability) throws BadArgumentsMDALException, MDALException {
        if ((processName == null) || (operationName == null) || (serviceName == null)) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        if ((processName.equals("")) || (operationName.equals("")) || (serviceName.equals(""))) {
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
            query.setString(1, processName);
            query.setString(2, serviceName);
            query.setString(3, operationName);
            query.setString(4, "" + avgResponseTime);
            query.setString(5, "" + reliability);

            query.executeUpdate();
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
                // ignore -- as we can't do anything about it here
            }
        }
    }

    //Deletes the entry responding to the given data
    public int deleteEntry(String processName, String serviceName, String operationName) throws MDALException, BadArgumentsMDALException {
        int ret = -1;
        if ((processName == null) || (operationName == null) || (serviceName == null)) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        if ((processName.equals("")) || (serviceName.equals("")) || (operationName.equals(""))) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            query = db.prepareStatement(deleteSTMT);
            query.setString(1, processName);
            query.setString(2, serviceName);
            query.setString(3, operationName);

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
                // ignore -- as we can't do anything about it here
            }
        }
        return ret;
    }

    public Vector<ViolationResult> getViolations() throws NoViolationsRegisteredMDALException, MDALException {
        PreparedStatement query = null;
        ResultSet rs = null;
        Vector<ViolationResult> violations = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readSTMT);
            rs = query.executeQuery();
            if (rs.isBeforeFirst()) {
                violations = new Vector<ViolationResult>();
                while (rs.next()) {
                    String processName = (rs.getString(1));
                    String serviceName = (rs.getString(2));
                    String operationName = (rs.getString(3));
                    String avgRespTimeString = (rs.getString(4));
                    String relString = (rs.getString(5));
                    Hashtable<String, Double> violation = new Hashtable<String, Double>();
                    Double value = Double.parseDouble(avgRespTimeString);
                    if (value.doubleValue() != (double) -1) {
                        violation.put(RESPONSE_TIME, value);
                    }
                    value = Double.parseDouble(relString);
                    if (value.doubleValue() != (double) -1) {
                        violation.put(RELIABILITY, value);
                    }
                    ViolationResult elem = new ViolationResult(processName, serviceName, operationName, -1, violation);
                    violations.add(elem);
                }
            } else {
                System.out.println("No violations in DB");
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
        return violations;
    }

    //Returns all the violations in the db
    public Vector<ViolationResult> getViolations(String processName) throws NoViolationsRegisteredMDALException, MDALException {
        PreparedStatement query = null;
        ResultSet rs = null;
        Vector<ViolationResult> violations = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(readSTMTFromProcessName);
            query.setString(1, processName);
            rs = query.executeQuery();
            commitTransactionStmt.execute();
            if (rs.isBeforeFirst()) {
                violations = new Vector<ViolationResult>();
                while (rs.next()) {
                    String serviceName = (rs.getString(2));
                    String operationName = (rs.getString(3));
                    String avgRespTimeString = (rs.getString(4));
                    String relString = (rs.getString(5));
                    Hashtable<String, Double> violation = new Hashtable<String, Double>();
                    Double value = Double.parseDouble(avgRespTimeString);
                    if (value.doubleValue() != -1) {
                        violation.put(RESPONSE_TIME, value);
                    }
                    value = Double.parseDouble(relString);
                    if (value.doubleValue() != -1) {
                        violation.put(RELIABILITY, value);
                    }

                    ViolationResult elem = new ViolationResult(processName, serviceName, operationName, -1, violation);
                    violations.add(elem);
                }
            } else {
                return null;
            }

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
        return violations;
    }
}
