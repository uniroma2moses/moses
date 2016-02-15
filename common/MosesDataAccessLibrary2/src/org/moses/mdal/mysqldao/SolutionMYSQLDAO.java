/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.solution.EndpointNode;
import org.moses.entity.solution.GroupNode;
import org.moses.entity.solution.ServiceNode;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.mdal.SolutionsMDALInterface;

/**
 *
 * @author valeryo
 */
public class SolutionMYSQLDAO extends MYSQLDAO implements SolutionsMDALInterface {

    private static String insGroupST = "INSERT " +
            "INTO `TempGroup`(IDGroup, SLA_ProcessClass, SLA_Process_ProcessName, Type, Probability) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static String insCocOpHasGroupST = "INSERT " +
            "INTO TempConcreteOperation_has_Group(ConcreteOperation_ConcreteService_ID, ConcreteOperation_Name, Group_IDGroup, Group_SLA_ProcessClass, Group_SLA_Process_ProcessName) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static String deleteSolutionSTMT = "DELETE " +
            "FROM `Group` " +
            "WHERE SLA_ProcessClass=? AND SLA_Process_ProcessName=?";
    private static String selectSolutionSimplifiedPerRequest = "select IDGroup, Type, Probability, ConcreteOperation_ConcreteService_ID, endpointURL, AbstractService_NameSpace, Cost from solution WHERE RequestID=? AND SLA_Process_ProcessName=? AND SLA_ProcessClass=?  " +
            "AND AbstractService_Name=? AND ConcreteOperation_Name=? ORDER BY id ASC";
    private static String selectSolutionSimplifiedPerFlow = "select IDGroup, Type, Probability, ConcreteOperation_ConcreteService_ID, endpointURL, AbstractService_NameSpace, Cost from solution WHERE SLA_Process_ProcessName=? AND SLA_ProcessClass=?  " +
            "AND AbstractService_Name=? AND ConcreteOperation_Name=? ORDER BY id ASC";
    private static String deleteSolutionCache = "DELETE FROM solution WHERE SLA_Process_ProcessName=? AND SLA_ProcessClass=?";
    private static String populateSolutionCache = "INSERT INTO solution (IDGroup, Type, Probability, ConcreteOperation_ConcreteService_ID, endpointURL,AbstractService_NameSpace, Cost, SLA_Process_ProcessName, SLA_ProcessClass, AbstractService_Name, ConcreteOperation_Name, id, RequestID) " +
            " SELECT g.IDGroup, g.Type, g.Probability, c.ConcreteOperation_ConcreteService_ID, s.endpointURL, s.AbstractService_NameSpace, co.Cost, g.SLA_Process_ProcessName, g.SLA_ProcessClass, s.AbstractService_Name, c.ConcreteOperation_Name, c.id, ? AS RequestID from MOSESDB2.TempGroup g, MOSESDB2.TempConcreteOperation_has_Group c, MOSESDB2.ConcreteService s, MOSESDB2.ConcreteOperation co WHERE g.Probability>0 AND g.IDGroup=c.Group_IDGroup AND g.SLA_ProcessClass=c.Group_SLA_ProcessClass AND g.SLA_Process_ProcessName=c.Group_SLA_Process_ProcessName AND s.ID=c.ConcreteOperation_ConcreteService_ID AND co.ConcreteService_ID=c.ConcreteOperation_ConcreteService_ID AND co.Name=c.ConcreteOperation_Name";
    private static String createTempGroup = "CREATE TEMPORARY TABLE TempGroup LIKE MOSESDB2.Group";
    private static String createTempConcreteOperationHasGroup = "CREATE TEMPORARY TABLE TempConcreteOperation_has_Group LIKE ConcreteOperation_has_Group";
    private static String dropTempGroup = "DROP TEMPORARY TABLE TempGroup";
    private static String dropTempConcreteOperationHasGroup = "DROP TEMPORARY TABLE TempConcreteOperation_has_Group";


    public SolutionMYSQLDAO() throws MDALException {
        super();
    }

    public SolutionMYSQLDAO(Connection db) throws MDALException {
        this.db = db;
    }

    public void createSolutionTransaction(String processName, String processClass, List<ServiceNode> services, boolean withTransaction, String requestID) throws BadArgumentsMDALException, MDALException {
        PreparedStatement createTemporaryGroupStmt = null;
        PreparedStatement createTempConcreteOperationHasGroupStmt = null;
        PreparedStatement dropTemporaryGroupStmt = null;
        PreparedStatement dropTempConcreteOperationHasGroupStmt = null;
        PreparedStatement query = null;
       
        //Checking arguments
        if (processName == null || processName.length() == 0 || processClass == null || processClass.length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }
        try {

            /* Creating temporary group table */
            createTemporaryGroupStmt = db.prepareStatement(createTempGroup);
            createTemporaryGroupStmt.executeUpdate();

            /* Creating temporary concreteOperationHasGroup relation */
            createTempConcreteOperationHasGroupStmt = db.prepareStatement(createTempConcreteOperationHasGroup);
            createTempConcreteOperationHasGroupStmt.executeUpdate();

            Iterator<ServiceNode> servIt = services.iterator();
            while (servIt.hasNext()) {
                ServiceNode service = servIt.next();
                List<org.moses.entity.solution.OperationNode> operationNode = service.getOperations();
                Iterator<org.moses.entity.solution.OperationNode> opeIt = operationNode.iterator();

                while (opeIt.hasNext()) {

                    org.moses.entity.solution.OperationNode operation = opeIt.next();
                    List<org.moses.entity.solution.GroupNode> groupNode = operation.getGroups();
                    Iterator<org.moses.entity.solution.GroupNode> grouIt = groupNode.iterator();



                    while (grouIt.hasNext()) {

                        GroupNode group = grouIt.next();
                        query = db.prepareStatement(insGroupST);
                        query.setInt(1, group.getGroupId());
                        query.setString(2, processClass);
                        query.setString(3, processName);
                        query.setString(4, group.getType().name());
                        query.setFloat(5, group.getProbability());
                        query.executeUpdate();

                        List<org.moses.entity.solution.EndpointNode> endpointNode = group.getEndpoints();
                        Iterator<org.moses.entity.solution.EndpointNode> endpointIt = endpointNode.iterator();

                        while (endpointIt.hasNext()) {

                            EndpointNode endpoint = endpointIt.next();
                            query = db.prepareStatement(insCocOpHasGroupST);
                            query.setInt(1, endpoint.getServiceId());
                            query.setString(2, operation.getOperationName());
                            query.setInt(3, group.getGroupId());
                            query.setString(4, processClass);
                            query.setString(5, processName);
                            query.executeUpdate();

                        }

                    }
                }
            }
            copySolution(processName, processClass, requestID);

            /* Dropping temporary tables */
            dropTemporaryGroupStmt = db.prepareStatement(dropTempGroup);
            dropTempConcreteOperationHasGroupStmt = db.prepareStatement(dropTempConcreteOperationHasGroup);
            dropTemporaryGroupStmt.executeUpdate();
            dropTempConcreteOperationHasGroupStmt.executeUpdate();


        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                query.close();
                query = null;
                createTemporaryGroupStmt.close();
                createTemporaryGroupStmt = null;
                createTempConcreteOperationHasGroupStmt.close();
                createTempConcreteOperationHasGroupStmt = null;
                dropTemporaryGroupStmt.close();
                dropTemporaryGroupStmt = null;
                dropTempConcreteOperationHasGroupStmt.close();
                dropTempConcreteOperationHasGroupStmt = null;
//                if (withTransaction) {
//                    startTransactionStmt.close();
//                    commitTransactionStmt.close();
//                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createSolution(String processName, String processClass, List<ServiceNode> services, String requestID) throws BadArgumentsMDALException, MDALException {
        createSolutionTransaction(processName, processClass, services, true, requestID);
    }


    public List<GroupNode> readSolution(String processName, String processClass, String serviceName, String operationName, String requestId) throws MDALException {
        PreparedStatement query = null;
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        ResultSet rs = null;
        List<GroupNode> groupNodes = null;
        List<EndpointNode> endpNode = null;

        if (processName == null || processName.length() == 0 || processClass == null || processClass.length() == 0 || serviceName == null || serviceName.length() == 0 || operationName == null || operationName.length() == 0) {
            throw new BadArgumentsMDALException(BadArgumentsMDALException.BAD_ARGUMENTS_MSG);
        }


        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            groupNodes = new ArrayList<GroupNode>();

            //Assuming that per-flow requests have requestId=-1
            try {
                Integer.parseInt(requestId);
                query = db.prepareStatement(selectSolutionSimplifiedPerFlow);
                query.setString(1, processName);
                query.setString(2, processClass);
                query.setString(3, serviceName);
                query.setString(4, operationName);

            } catch (NumberFormatException e) {
                query = db.prepareStatement(selectSolutionSimplifiedPerRequest);
                query.setString(1, requestId);
                query.setString(2, processName);
                query.setString(3, processClass);
                query.setString(4, serviceName);
                query.setString(5, operationName);
            }



            int curGroupId = -1;
            GroupNode node = null;
            EndpointNode app = null;
            rs = query.executeQuery();

            while (rs.next()) {
                if (rs.getInt(1) != curGroupId) {
                    curGroupId = rs.getInt(1);
                    if (node != null) {
                        node.setEndpoints(endpNode);
                        groupNodes.add(node);

                    }
                    node = new GroupNode();
                    endpNode = new ArrayList<EndpointNode>();
                }
                node.setGroupId(rs.getInt(1));
                node.setType(GroupNode.Type.valueOf(rs.getString(2)));
                node.setProbability(rs.getFloat(3));
                app = new EndpointNode();
                app.setServiceId(rs.getInt(4));
                app.setEndpointURL(rs.getString(5));
                app.setTargetNameSpace(rs.getString(6));
                app.setCost(rs.getDouble(7));
                endpNode.add(app);
            }
            if (node == null && (groupNodes == null || groupNodes.size() == 0)) {
                System.out.println("----->No solution!");
            }
            node.setEndpoints(endpNode);
            groupNodes.add(node);



        } catch (SQLException ex) {
            System.out.println("-------------Query.toString(): " + query.toString());
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                rs.close();
                rs = null;
                query.close();
                startTransactionStmt.close();
                commitTransactionStmt.close();
                query = null;
                startTransactionStmt = null;
                commitTransactionStmt = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupNodes;
    }

    private void copySolution(String processName, String processClass, String requestId) {
      
        PreparedStatement deleteCacheStmt = null;
        PreparedStatement populateCacheStmt = null;
        try {
            populateCacheStmt = db.prepareStatement(populateSolutionCache);
            populateCacheStmt.setString(1, requestId);
            if (requestId.equals("-1")) {
                deleteCacheStmt = db.prepareStatement(deleteSolutionCache);
                deleteCacheStmt.setString(1, processName);
                deleteCacheStmt.setString(2, processClass);
                deleteCacheStmt.executeUpdate();
            }


            populateCacheStmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (requestId.equals("-1")) {
                    deleteCacheStmt.close();
                }
                populateCacheStmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int updateSolution(String processName, String processClass, List<ServiceNode> services, String requestId) throws BadArgumentsMDALException, MDALException {
        java.util.Date d1 = new java.util.Date();
        PreparedStatement startTransactionStmt = null;
        PreparedStatement commitTransactionStmt = null;
        int ret = -1;

        try {
            startTransactionStmt = db.prepareStatement(super.startTransaction);
            commitTransactionStmt = db.prepareStatement(super.commitTransaction);
            startTransactionStmt.execute();
            this.createSolutionTransaction(processName, processClass, services, false, requestId);
            ret = 1;
            commitTransactionStmt.execute();
        } catch (Exception ex) {
            throw new MDALException();
        } finally {
            try {
                startTransactionStmt.close();
                commitTransactionStmt.close();
                startTransactionStmt = null;
                commitTransactionStmt = null;
            } catch (SQLException ex) {
                Logger.getLogger(SolutionMYSQLDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        java.util.Date d2 = new java.util.Date();
        System.out.println("Optimization time: " + (d2.getTime() - d1.getTime()));
        return ret;
    }

    public int deleteSolution(String processName, String processClass) throws BadArgumentsMDALException, MDALException {
        int ret = -1;
        PreparedStatement querySTMT = null;
        try {
            querySTMT = db.prepareStatement(deleteSolutionSTMT);
            querySTMT.setString(1, processClass);
            querySTMT.setString(2, processName);
            ret = querySTMT.executeUpdate();

        } catch (SQLException ex) {
            throw new MDALException(ex.getMessage());
        } finally {
            try {
                querySTMT.close();
                querySTMT = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }
}
