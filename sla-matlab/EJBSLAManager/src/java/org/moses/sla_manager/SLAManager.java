/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.sla_manager;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ejb.Stateless;
import org.moses.sla_manager.client.OptimizationEngineClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moses.entity.Agreement;
import org.moses.entity.SLA;
import org.moses.entity.User;
import org.moses.entity.Process;
import org.moses.exception.MDALException;
import org.moses.exceptions.ExistingAgreementException;
import org.moses.exceptions.SLAManagerException;
import org.moses.mdal.AgreementsMDALInterface;
import org.moses.mdal.Constraints.ClientSLAConstraint;
import org.moses.mdal.Constraints.ClientSLAMonitor;
import org.moses.mdal.Constraints.ProcessSLAConstraint;
import org.moses.mdal.MDAL;
import org.moses.mdal.ProcessesMDALInterface;
import org.moses.mdal.SLAsMDALInterface;
import org.moses.mdal.UsersMDALInterface;

/**
 *
 * @author dante
 */
@WebService()
@Stateless()
public class SLAManager {

    private final static Object lock = new Object();

    /**
     * Web service operation
     */
    @WebMethod(operationName = "createUser")
    public User createUser(@WebParam(name = "username") String username, @WebParam(name = "name") String name, @WebParam(name = "surname") String surname, @WebParam(name = "password") String password) throws MDALException {

        User user = new User();
        UsersMDALInterface userMDAL = null;

        try {
            MDAL mdal = MDAL.getInstance();
            userMDAL = mdal.getUsersMDAL();
            user.setName(name);
            user.setPassword(password);
            user.setSurname(surname);
            user.setUsername(username);
            userMDAL.createUser(user);
        } catch (Exception ex) {
            throw new MDALException(ex.getClass().getName() + ":" + ex.getMessage());
        } finally {
            userMDAL.close();
        }

        return user;
    }

    /**
     * Given a process name it returns a list of process classes.
     * @param processName
     * @return
     * @throws MDALException
     */
    @WebMethod(operationName = "getAvailableProcessClasses")                            //CHECKED OK tranne optimization
    public SLA[] getAvailableProcessClasses(@WebParam(name = "processName") String processName) throws MDALException {

        SLA[] s = null;
        SLAsMDALInterface slaMDAL = null;
        ProcessesMDALInterface processMDAL = null;

        try {
            MDAL mdal = MDAL.getInstance();
            slaMDAL = mdal.getSLAsMDAL();
            processMDAL = mdal.getProcessesMDAL();

            Process p = processMDAL.readProcess(processName);

            Iterator<SLA> classIterator = slaMDAL.readSLAsByPr(p).iterator();
            List<SLA> availableSlas = new ArrayList<SLA>();
            SLA sla;
            while (classIterator.hasNext()) {
                sla = classIterator.next();
                String processClass = sla.getProcessClass();

                Boolean existSolution = OptimizationEngineClient.calculateProcessSolution(processName, processClass, null, OptimizationEngineClient.ClientType.EXIST); //nel properties
                if (existSolution) {
                    availableSlas.add(sla);
                }
            }
            s = new SLA[availableSlas.size()];
            Iterator<SLA> slasIterator = availableSlas.iterator();
            int i = 0;
            while (slasIterator.hasNext()) {
                sla = slasIterator.next();
                sla.setAgreement(null);
                sla.setProcess(null);
                s[i] = sla;
                i++;
            }
        } catch (Exception ex) {
            throw new MDALException(ex.getClass().getName() + ":" + ex.getMessage());
        } finally {
            slaMDAL.close();
            processMDAL.close();
        }
        return s;
    }

    /**
     * Methodfor SLA registration
     * @param processName
     * @param processClass
     * @param username
     * @param password
     * @param expireDate
     * @param arrivalRate
     * @return
     * @throws MDALException
     */
    @WebMethod(operationName = "createSlaAgreement")                                        //OK. invocazione a optimization
    public Boolean createSlaAgreement(@WebParam(name = "processName") String processName,
            @WebParam(name = "processClass") String processClass,
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "rate") Double rate,
            @WebParam(name = "expireDate") Date expireDate) throws MDALException {

        synchronized (lock) {
            Agreement agreement = null;
            Boolean retValue = false;
            AgreementsMDALInterface agreementMDAL = null;
            UsersMDALInterface usersMDAL = null;
            SLAsMDALInterface slasMDAL = null;

            try {
                MDAL mdal = MDAL.getInstance();
                agreementMDAL = mdal.getAgreementsMDAL();
                slasMDAL = mdal.getSLAsMDAL();
                usersMDAL = mdal.getUsersMDAL();

                //Checking if there exists a SLA with given process name, process class and username
                if (agreementMDAL.readAgreement(username, processName, processClass) != null) {
                    //Don't want to check it right now
                    //throw new ExistingAgreementException("There is a SLA for these parameters");
                }
                //Check if there exists the process with given process name and process class
                if (slasMDAL.readSLA(processName, processClass) == null) {
                    throw new SLAManagerException("Process name and process class are incorrect");
                }
                //Get the user with a given username
                User user = usersMDAL.readUser(username);
                if (user == null) {
                    throw new SLAManagerException("User not registered");
                }
                //Verify password
                if (!user.getPassword().equals(password)) {
                    throw new SLAManagerException("Password incorrect");
                }
                //Get the SLA with given process name and process class
                SLA sla = slasMDAL.readSLA(processName, processClass);
                ////Call the optimization engine to verify if there is a solution
                Boolean existSolution = OptimizationEngineClient.calculateProcessSolution(processName, processClass, rate, OptimizationEngineClient.ClientType.SLA_CREATION);
                if (existSolution) {
                    agreement = new Agreement();
                    agreement.setSla(sla);
                    agreement.setUsername(user);
                    agreement.setExpireDate(expireDate);
                    agreement.setArrivalRate(rate);
                    //agreement.setSlaFile("");
                    agreement.setSlaConstraints(new Hashtable<ClientSLAConstraint, String>());
                    agreement.getSlaConstraints().put(ClientSLAConstraint.COST, sla.getSlaConstraints().get(ProcessSLAConstraint.COST));
                    agreement.getSlaConstraints().put(ClientSLAConstraint.RELIABILITY, sla.getSlaConstraints().get(ProcessSLAConstraint.RELIABILITY));
                    agreement.getSlaConstraints().put(ClientSLAConstraint.REQUEST_RATE, String.valueOf(rate));
                    agreement.getSlaConstraints().put(ClientSLAConstraint.RESPONSE_TIME, sla.getSlaConstraints().get(ProcessSLAConstraint.RESPONSE_TIME));
                    agreement.setSlaMonitor(new Hashtable<ClientSLAMonitor, String>());
                    agreement.getSlaMonitor().put(ClientSLAMonitor.AVG_COST, "0");
                    agreement.getSlaMonitor().put(ClientSLAMonitor.AVG_RELIABILITY, "0");
                    agreement.getSlaMonitor().put(ClientSLAMonitor.AVG_REQUEST_RATE, "0");
                    agreement.getSlaMonitor().put(ClientSLAMonitor.AVG_RESPONSE_TIME, "0");
                    //Write down the SLA into the repository
                    agreementMDAL.createAgreement(agreement);
                    retValue = true;
                } else {
                    //throw new SLAManagerException("It isn't possible to create a new Agreement for this process in this QoS class");
                }

            } catch (Exception ex) {
//            throw new MDALException(ex.getClass().getName() + ":" + ex.getMessage());
                ex.printStackTrace();
                throw new MDALException(ex.getMessage());
            } finally {
                slasMDAL.close();
                agreementMDAL.close();
                usersMDAL.close();
            }

            return retValue;
        }

    }

    /**
     * Method used to verify the existence of a unexpired SLA
     * @param processName
     * @param processClass
     * @param username
     * @param password
     * @return
     * @throws MDALException
     */
    @WebMethod(operationName = "checkAgreemnt")
    public Boolean checkAgreemnt(@WebParam(name = "processName") String processName, @WebParam(name = "processClass") String processClass, @WebParam(name = "username") String username, @WebParam(name = "password") String password) throws MDALException {

        synchronized (lock) {
            Boolean result = false;
            AgreementsMDALInterface agreementMDAL = null;
            UsersMDALInterface usersMDAL = null;

            try {
                MDAL mdal = MDAL.getInstance();
                agreementMDAL = mdal.getAgreementsMDAL();
                usersMDAL = mdal.getUsersMDAL();
                Agreement agreement = null;

                //If the password doesn't match with the correct user's password in the database throw a SLAManagerException
                User user = usersMDAL.readUser(username);
                if (!user.getPassword().equals(password)) {
                    throw new SLAManagerException("The password is incorrect");
                }
                //Checking if there exists a SLA with given process name, process class and username
                if ((agreement = agreementMDAL.readAgreement(username, processName, processClass)) != null) {
                    result = true;
                    //If the SLA expired throw a SLAManagerException
                /*if (agreement.getExpireDate().before(new Date())) {           //DA DECOMMENTARE
                    throw new AgreementExpiredException("Agreement expired");
                    }*/
                }

            } catch (Exception ex) {
                Logger.getLogger(SLAManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new MDALException(ex.getClass().getName() + ":" + ex.getMessage());
            } finally {
                agreementMDAL.close();
                usersMDAL.close();
            }

            return result;
        }
    }

    /**
     * Remove agreement
     * @param processName
     * @param processClass
     * @param username
     * @param password
     * @return
     * @throws MDALException
     */
    @WebMethod(operationName = "deleteAgreement")
    public Boolean deleteAgreement(@WebParam(name = "processName") //OK. Da provare invocazione a Optimization
            String processName, @WebParam(name = "processClass") String processClass, @WebParam(name = "username") String username, @WebParam(name = "password") String password) throws MDALException {

        synchronized (lock) {
            AgreementsMDALInterface agreementMDAL = null;
            UsersMDALInterface usersMDAL = null;
            try {
                MDAL mdal = MDAL.getInstance();
                agreementMDAL = mdal.getAgreementsMDAL();
                usersMDAL = mdal.getUsersMDAL();
                Agreement agreement = null;

                if ((agreement = agreementMDAL.readAgreement(username, processName, processClass)) != null) {
                    User user = usersMDAL.readUser(username);
                    if (!user.getPassword().equals(password)) {
                        throw new SLAManagerException("The password is incorrect");
                    }

                    Boolean existSolution = OptimizationEngineClient.calculateProcessSolution(processName, processClass, agreement.getArrivalRate(), OptimizationEngineClient.ClientType.SLA_DELETION);
                    if (!existSolution) {
                        //Problem: manage a possible failure of the process
                    }

                    agreementMDAL.deleteAgreement(username, processName, processClass);
                } else {
                    throw new SLAManagerException("The Agreement doesn't exist");
                }

            } catch (Exception ex) {
                Logger.getLogger(SLAManager.class.getName()).log(Level.SEVERE, null, ex);
                //throw new MDALException(ex.getClass().getName() + ":" + ex.getMessage());
            } finally {
                agreementMDAL.close();
                usersMDAL.close();
            }
            return true;
        }
    }
}
