/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.InvocationStatistics;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.mdal.MonitorMDALInterface;
import org.moses.mdal.monitor.ViolationResult;

/**
 *
 * @author arale
 *
 * Mi sa che non serve, l'ho implementata come qosdatacollection
 *
 */
public class MonitorMYSQLDAO extends MYSQLDAO implements MonitorMDALInterface {

    private static String createSTMTlog = "INSERT " +
            "INTO LogQoS(ProcessName, ServiceName, OperationName, AvgResponseTime, VrcResponseTime, Reliability, NInterval) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static String truncateSTMTlog = "DELETE FROM LogQoS";
    private static String createSTMTviol = "INSERT " +
            "INTO Violations(ProcessName, ServiceName, OperationName, RespTime, Reliability, NInterval) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static String readSTMTviolFromNInterval = "SELECT * " +
            "FROM Violations " +
            "WHERE NInterval=?";
    private static String truncateSTMTviol = "DELETE FROM Violations";

    public MonitorMYSQLDAO() throws MDALException {
        super();
    }

    //Removes data from both log tables
    public int removeData() {
        int ret = -1;
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = (PreparedStatement) db.prepareStatement(truncateSTMTlog);

            ret = query.executeUpdate();
        } catch (SQLException ex) {
            try {
                throw new MDALException(ex.getMessage());
            } catch (MDALException ex1) {
                Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                query.close();
                query = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (ret != -1) {
            try {
                query = (PreparedStatement) db.prepareStatement(truncateSTMTviol);

                ret = query.executeUpdate();
            } catch (SQLException ex) {
                try {
                    throw new MDALException(ex.getMessage());
                } catch (MDALException ex1) {
                    Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } finally {
                try {
                    query.close();
                    query = null;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        }
        try {
            commitTransactionStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                startTransactionStmt.close();
                commitTransactionStmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ret;
    }

    public void addLogData(Vector<InvocationStatistics> toAdd, int interval) {
        if (toAdd != null && !toAdd.isEmpty()) {
            Iterator toAddIt = toAdd.iterator();
            while (toAddIt.hasNext()) {
                InvocationStatistics iS = (InvocationStatistics) toAddIt.next();
                try {
                    addLogData(iS.getProcessName(), iS.getServiceName(), iS.getOperationName(), iS.getAvgResponseTime(), iS.getVrcResponseTime(), iS.getReliability(), interval);
                } catch (BadArgumentsMDALException ex) {
                    Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MDALException ex) {
                    Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void addLogData(String processName, String serviceName, String operationName, double avgResponseTime, double vrcResponseTime, double reliability, int nInterval) throws BadArgumentsMDALException, MDALException {
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
            query = db.prepareStatement(createSTMTlog);

            query.setString(1, processName);
            query.setString(2, serviceName);
            query.setString(3, operationName);
            query.setString(4, "" + avgResponseTime);
            query.setString(5, "" + vrcResponseTime);
            query.setString(6, "" + reliability);
            query.setString(7, "" + nInterval);

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
            }
        }

    }

    public void addViolationData(Vector<ViolationResult> toAdd) {
        Iterator vRIt = toAdd.iterator();
        while (vRIt.hasNext()) {
            ViolationResult elem = (ViolationResult) vRIt.next();
            if (elem.getViolation().get(RELIABILITY) != null) {

                double relValue = ((elem.getViolation()).get(RELIABILITY)).doubleValue();
                if (elem.getViolation().get(RESPONSE_TIME) != null) {
                    double respTimeValue = elem.getViolation().get(RESPONSE_TIME).doubleValue();
                    addViolationData(elem.getProcessName(), elem.getServiceName(), elem.getOperationName(), respTimeValue, relValue, elem.getInterval());
                } else {
                    addViolationData(elem.getProcessName(), elem.getServiceName(), elem.getOperationName(), -1, relValue, elem.getInterval());
                }
            } else if (elem.getViolation().get(RESPONSE_TIME) != null) {
                double respTimeValue = elem.getViolation().get(RESPONSE_TIME).doubleValue();
                addViolationData(elem.getProcessName(), elem.getServiceName(), elem.getOperationName(), respTimeValue, -1, elem.getInterval());
            } else {
                System.out.println("No violation for entry: " + elem.getProcessName() + " " + elem.getServiceName() + " " + elem.getOperationName());
            }
        }
    }

    public void addViolationData(String processName, String serviceName, String operationName, double respTime, double reliability, int interval) {
        if ((processName == null) || (operationName == null) || (serviceName == null)) {
            try {
                throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
            } catch (BadArgumentsMDALException ex) {
                ex.printStackTrace();
                Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if ((processName.equals("")) || (operationName.equals("")) || (serviceName.equals(""))) {
            try {
                throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
            } catch (BadArgumentsMDALException ex) {
                ex.printStackTrace();
                Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            query = db.prepareStatement(createSTMTviol);
            query.setString(1, processName);
            query.setString(2, serviceName);
            query.setString(3, operationName);
            query.setString(4, "" + respTime);
            query.setString(5, "" + reliability);
            query.setString(6, "" + interval);

            query.executeUpdate();
            commitTransactionStmt.close();
        } catch (SQLException ex) {
            try {
                throw new MDALException(ex.getMessage());
            } catch (MDALException ex1) {
                Logger.getLogger(MonitorMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex1);
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

    //Returns all the data about violations in the last window intervals
    public Vector<ViolationResult> getViolationData(int begin, int end) {
        //System.out.println("In get violation data! ");
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        ResultSet rs = null;
        Vector<ViolationResult> violations = new Vector<ViolationResult>();
        int i;
        for (i = begin; i < end; i++) {
            try {
                startTransactionStmt = db.prepareStatement(super.startTransaction);
                commitTransactionStmt = db.prepareStatement(super.commitTransaction);
                startTransactionStmt.execute();
                query = db.prepareStatement(readSTMTviolFromNInterval);
                query.setString(1, "" + i);
                rs = query.executeQuery();
                if (rs.isBeforeFirst()) {
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
                }
                commitTransactionStmt.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
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
        }
        if (violations.isEmpty()) {
            return null;
        }
        return violations;
    }

    public Vector<ViolationResult> getViolationData(String processName, String serviceName, String operationName, int begin, int end) {
        Vector<ViolationResult> violations = null;

        Vector<ViolationResult> all = getViolationData(begin, end);
        if (all == null || all.isEmpty()) {
            return null;
        }
        Iterator it = all.iterator();
        while (it.hasNext()) {
            ViolationResult elem = (ViolationResult) it.next();
            if (elem.getProcessName().equals(processName) && (elem.getOperationName().equals(operationName)) && (elem.getServiceName().equals(serviceName))) {
                if (violations == null) {
                    violations = new Vector<ViolationResult>();
                }
                violations.add(elem);
            }
        }
        return violations;
    }

    public Vector<ViolationResult> getViolationData(String processName, int begin, int end) {

        Vector<ViolationResult> violations = null;

        Vector<ViolationResult> all = getViolationData(begin, end);
        if (all == null || all.isEmpty()) {
            return null;
        }
        Iterator it = all.iterator();
        while (it.hasNext()) {
            ViolationResult elem = (ViolationResult) it.next();
            if (elem.getProcessName().equals(processName)) {
                if (violations == null) {
                    violations = new Vector<ViolationResult>();
                }
                violations.add(elem);
            }
        }
        return violations;
    }
}
