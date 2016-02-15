/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author francesco
 */
public class MDAL {

    private String abstractOperationsClass;
    private String abstractServicesClass;
    private String agreementsClass;
    private String concreteOperationsClass;
    private String concreteServicesClass;
    private String processesClass;
    private String slasClass;
    private String usersClass;
    private String solutionsClass;
    private String monitorClass;
    private String modulesRegistrationClass;
    private String perceivedQoSClass;
    private String qosDataCollectionClass;
    private static final String CONFIG_FILE = "mdal.properties";
    private static final String ABSTRACT_OPERATIONS_CLASS = "abstractOperationsClass";
    private static final String ABSTRACT_SERVICES_CLASS = "abstractServicesClass";
    private static final String AGREEMENTS_CLASS = "agreementsClass";
    private static final String CONCRETE_OPERATIONS_CLASS = "concreteOperationsClass";
    private static final String CONCRETE_SERVICES_CLASS = "concreteServicesClass";
    private static final String PROCESSES_CLASS = "processesClass";
    private static final String SLAS_CLASS = "slasClass";
    private static final String SOLUTIONS_CLASS = "solutionsClass";
    private static final String USERS_CLASS = "usersClass";
    private static final String MONITOR_CLASS = "monitorClass";
    private static final String PERCEIVED_QOS_CLASS = "perceivedQoSClass";
    private static final String MODULES_REGISTRATION_CLASS = "modulesRegistrationClass";
    private static final String QOS_DATA_COLLECTION_CLASS = "qosDataCollectionClass";
    private static MDAL mdal = null;

    private MDAL() throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(CONFIG_FILE);
        Properties p = new Properties();
        p.load(fis);
        fis.close();
        this.abstractOperationsClass = p.getProperty(ABSTRACT_OPERATIONS_CLASS);
        this.abstractServicesClass = p.getProperty(ABSTRACT_SERVICES_CLASS);
        this.agreementsClass = p.getProperty(AGREEMENTS_CLASS);
        this.concreteOperationsClass = p.getProperty(CONCRETE_OPERATIONS_CLASS);
        this.concreteServicesClass = p.getProperty(CONCRETE_SERVICES_CLASS);
        this.processesClass = p.getProperty(PROCESSES_CLASS);
        this.slasClass = p.getProperty(SLAS_CLASS);
        this.solutionsClass = p.getProperty(SOLUTIONS_CLASS);
        this.usersClass = p.getProperty(USERS_CLASS);
        this.modulesRegistrationClass = p.getProperty(MODULES_REGISTRATION_CLASS);
        this.perceivedQoSClass = p.getProperty(PERCEIVED_QOS_CLASS);
        this.monitorClass = p.getProperty(MONITOR_CLASS);
        this.qosDataCollectionClass = p.getProperty(QOS_DATA_COLLECTION_CLASS);

    }

    public static MDAL getInstance() throws FileNotFoundException, IOException {
        if (mdal == null) {
            MDAL.mdal = new MDAL();
        }
        return MDAL.mdal;
    }

    public AbstractOperationsMDALInterface getAbstractOperationsMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (AbstractOperationsMDALInterface) Class.forName(this.abstractOperationsClass).newInstance();
    }

    public AbstractServicesMDALInterface getAbstractServicesMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (AbstractServicesMDALInterface) Class.forName(this.abstractServicesClass).newInstance();
    }

    public AgreementsMDALInterface getAgreementsMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (AgreementsMDALInterface) Class.forName(this.agreementsClass).newInstance();
    }

    public ConcreteOperationsMDALInterface getConcreteOperationsMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (ConcreteOperationsMDALInterface) Class.forName(this.concreteOperationsClass).newInstance();
    }

    public ConcreteServicesMDALInterface getConcreteServicesMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (ConcreteServicesMDALInterface) Class.forName(this.concreteServicesClass).newInstance();
    }

    public ProcessesMDALInterface getProcessesMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (ProcessesMDALInterface) Class.forName(this.processesClass).newInstance();
    }

    public SLAsMDALInterface getSLAsMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (SLAsMDALInterface) Class.forName(this.slasClass).newInstance();
    }

    public SolutionsMDALInterface getSolutionsMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (SolutionsMDALInterface) Class.forName(this.solutionsClass).newInstance();
    }

    public UsersMDALInterface getUsersMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (UsersMDALInterface) Class.forName(this.usersClass).newInstance();
    }

    public ModulesRegistrationMDALInterface getModuleAddressesMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (ModulesRegistrationMDALInterface) Class.forName(this.modulesRegistrationClass).newInstance();
    }

    public PerceivedQoSMDALInterface getPerceivedQoSMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (PerceivedQoSMDALInterface) Class.forName(this.perceivedQoSClass).newInstance();
    }

    public QoSDataCollectionMDALInterface getQoSDataCollection() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (QoSDataCollectionMDALInterface) Class.forName(this.qosDataCollectionClass).newInstance();
    }

    public MonitorMDALInterface getMonitorMDAL() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (MonitorMDALInterface) Class.forName(this.monitorClass).newInstance();
    }
}
