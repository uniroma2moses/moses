
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication13;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import org.moses.entity.AbstractOperation;
import org.moses.entity.AbstractService;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.entity.SLA;
import org.moses.entity.User;
import org.moses.entity.solution.EndpointNode;
import org.moses.entity.solution.GroupNode;
import org.moses.entity.solution.OperationNode;
import org.moses.entity.solution.ServiceNode;
import org.moses.mdal.ConcreteServicesMDALInterface;
import org.moses.mdal.Constraints.OperationSLAConstraint;
import org.moses.mdal.Constraints.OperationSLAMonitor;
import org.moses.mdal.Constraints.ProcessSLAConstraint;
import org.moses.mdal.Constraints.ProcessSLAMonitor;
import org.moses.mdal.MDAL;
import org.moses.mdal.ProcessesMDALInterface;
import org.moses.mdal.SLAsMDALInterface;
import org.moses.mdal.SolutionsMDALInterface;
import org.moses.mdal.UsersMDALInterface;
import org.moses.entity.Process;
import org.moses.entity.ProcessPath;
import org.moses.entity.ProcessSubPath;
import org.moses.mdal.ConcreteOperationsMDALInterface;

/**
 *
 * @author stefano
 */
public class ESECProcess {

    public void insertData() throws Exception {
        MDAL mdal = MDAL.getInstance();

        //Inizio creazione utente
//        UsersMDALInterface umi = mdal.getUsersMDAL();
//        User u = new User();
//        u.setName("Stefano");
//        u.setSurname("Iannucci");
//        u.setUsername("chmod");
//        u.setPassword("chmod");
//        try {
//            umi.createUser(u);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //Fine creazione utente

        //Inizio creazione processo (grafo + servizi astratti)

        String processGraph = new String();

        processGraph = "<graph>" + "<node id='1'>" + "<link to='2' probability='1.0'/>" + "</node>" + "<node id='2' type='other'>" + "<link to='3' probability='1'/>" + "</node>" + "<node id='3' type='fork'>" + "<link to='4' probability='1.0'/>" + "<link to='5' probability='1.0'/>" + "</node>" + "<node id='4' type='invoke' serviceName='S1' operationName='s1op'>" + "<link to='6' probability='1.0'/>" + "</node>" + "<node id='5' type='invoke' serviceName='S3' operationName='s3op'>" + "<link to='7' probability='1.0'/>" + "</node>" + "<node id='6' type='invoke' serviceName='S2' operationName='s2op'>" + "<link to='7' probability='1.0'/>" + "</node>" + "<node id='7' type='join'>" + "<link to='8' probability='1.0'/>" + "</node>" + "<node id='8' type='other'>" + "<link to='2' probability='-1/3'/>" + "<link to='9' probability='2/3'/>" + "</node>" + "<node id='9' type='invoke' serviceName='S4' operationName='s4op'>" + "<link to='10' probability='1.0'/>" + "</node>" + "<node id='10' type='other'>" + "<link to='11' probability='0.7'/>" + "<link to='12' probability='0.3'/>" + "</node>" + "<node id='11' type='invoke' serviceName='S5' operationName='s5op'>" + "<link to='13' probability='1.0'/>" + "</node>" + "<node id='12' type='invoke' serviceName='S6' operationName='s6op'>" + "<link to='13' probability='1.0'/>" + "</node>" + "<node id='13' type='other'>" + "<link to='14' probability='1.0'/>" + "</node>" + "<node id='14'/>" + "<root id='1'/>" + "<sink id='14'/>" + "</graph>";
        ProcessesMDALInterface pmi = mdal.getProcessesMDAL();

        //Create a good process

        Process p = new Process();
        Hashtable<AbstractService, Float> services = new Hashtable<AbstractService, Float>();

        p.setProcessName("ESECProcess");
        p.setProcessGraph(processGraph);
        p.setProcessNS("http://enterprise.netbeans.org/bpel/ESECProcess/ESECProcess");
        p.setProcessOperation("ESECProcessOperation");
        p.setProcessEndpoints("http://core:9080/CompositeESECProcessService1/casaPort1");
        p.setStateful(true);

        AbstractService service = new AbstractService();
        service.setName("S1");
        service.setNameSpace("http://ce/uniroma2/it/xsd");
        AbstractOperation op = new AbstractOperation();
        op.setName("s1op");
        op.setService(service);
        List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
        ops.add(op);
        service.setOperations(ops);

        AbstractService service2 = new AbstractService();
        service2.setName("S2");
        service2.setNameSpace("http://ce/uniroma2/it/xsd");
        AbstractOperation op2 = new AbstractOperation();
        op2.setName("s2op");
        op2.setService(service2);
        List<AbstractOperation> ops2 = new ArrayList<AbstractOperation>();
        ops2.add(op2);
        service2.setOperations(ops2);

        AbstractService service3 = new AbstractService();
        service3.setName("S3");
        service3.setNameSpace("http://ce/uniroma2/it/xsd");
        AbstractOperation op3 = new AbstractOperation();
        op3.setName("s3op");
        op3.setService(service3);
        List<AbstractOperation> ops3 = new ArrayList<AbstractOperation>();
        ops3.add(op3);
        service3.setOperations(ops3);

        AbstractService service4 = new AbstractService();
        service4.setName("S4");
        service4.setNameSpace("http://ce/uniroma2/it/xsd");
        AbstractOperation op4 = new AbstractOperation();
        op4.setName("s4op");
        op4.setService(service4);
        List<AbstractOperation> ops4 = new ArrayList<AbstractOperation>();
        ops4.add(op4);
        service4.setOperations(ops4);

        AbstractService service5 = new AbstractService();
        service5.setName("S5");
        service5.setNameSpace("http://ce/uniroma2/it/xsd");
        AbstractOperation op5 = new AbstractOperation();
        op5.setName("s5op");
        op5.setService(service5);
        List<AbstractOperation> ops5 = new ArrayList<AbstractOperation>();
        ops5.add(op5);
        service5.setOperations(ops5);

        AbstractService service6 = new AbstractService();
        service6.setName("S6");
        service6.setNameSpace("http://ce/uniroma2/it/xsd");
        AbstractOperation op6 = new AbstractOperation();
        op6.setName("s6op");
        op6.setService(service6);
        List<AbstractOperation> ops6 = new ArrayList<AbstractOperation>();
        ops6.add(op6);
        service6.setOperations(ops6);

        services.put(service, new Float(2));
        services.put(service2, new Float(2));
        services.put(service3, new Float(2));
        services.put(service4, new Float(1));
        services.put(service5, new Float(0.5));
        services.put(service6, new Float(0.5));

        p.setServices(services);

        //Setting process path and subpaths
//        ProcessPath[] paths = new ProcessPath[2];
//        paths[0] = new ProcessPath(p, 0.5d);
//        paths[1] = new ProcessPath(p, 0.5d);
//
//
//
//        ProcessSubPath[] subpath1 = new ProcessSubPath[2];
//        subpath1[0] = new ProcessSubPath(p);
//        subpath1[1] = new ProcessSubPath(p);
//
//        subpath1[0].addActivity(service);
//        subpath1[0].addActivity(service2);
//        subpath1[0].addActivity(service4);
//        subpath1[0].addActivity(service5);
//
//        subpath1[1].addActivity(service3);
//        subpath1[1].addActivity(service4);
//        subpath1[1].addActivity(service5);
//
//
//        ProcessSubPath[] subpath2 = new ProcessSubPath[2];
//        subpath2[0] = new ProcessSubPath(p);
//        subpath2[1] = new ProcessSubPath(p);
//
//        subpath2[0].addActivity(service);
//        subpath2[0].addActivity(service2);
//        subpath2[0].addActivity(service4);
//        subpath2[0].addActivity(service6);
//
//        subpath2[1].addActivity(service3);
//        subpath2[1].addActivity(service4);
//        subpath2[1].addActivity(service6);
//
//        paths[0].setSubPaths(subpath1);
//        paths[1].setSubPaths(subpath2);
//
//        p.setProcessPaths(paths);
//        try {
//            pmi.createProcess(p); //Call to the DB
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //builPathAlfa98(p, service, service2, service3, service4, service5, service6);
        builPathAlfa96(p, service, service2, service3, service4, service5, service6);
        try {
            pmi.createProcess(p); //Call to the DB
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        Date d1 = new Date();
        Process pp = pmi.readProcess("ESECProcess");
        Date d2 = new Date();
        System.out.println("Tempo lettura processo: " + (d2.getTime() - d1.getTime()));

        //Fine creazione processo


        //Inizio definizione SLA classe 1
        SLAsMDALInterface slami = mdal.getSLAsMDAL();

        Hashtable<ProcessSLAConstraint, String> processSLAConstraints = new Hashtable<ProcessSLAConstraint, String>();
//        processSLAConstraints.put(ProcessSLAConstraint.COST, "39");
//        //processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.95");
//        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.90");
//        processSLAConstraints.put(ProcessSLAConstraint.REQUEST_RATE, "1.5");
//        processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "14");
//        processSLAConstraints.put(ProcessSLAConstraint.TOTAL_REQUEST_RATE, "0.1");
        processSLAConstraints.put(ProcessSLAConstraint.COST, "55");
        //processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.95");
        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.88");
        processSLAConstraints.put(ProcessSLAConstraint.REQUEST_RATE, "1.5");
        processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "16");
        processSLAConstraints.put(ProcessSLAConstraint.TOTAL_REQUEST_RATE, "0.1");

        SLA processClass = new SLA();
        processClass.setProcessClass("1");
        processClass.setProcess(p);
        processClass.setSlaFile("SLAfile");
        processClass.setSlaConstraints(processSLAConstraints);
        processClass.setSlaMonitor(new Hashtable<ProcessSLAMonitor, String>());
        try {
            slami.createSLA(processClass); //Call to the DB
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Fine definizione SLA classe 1

        //Inizio sottoscrizione SLA 1 per utente

//        slami = mdal.getSLAsMDAL();
//
//        AgreementsMDALInterface ami = mdal.getAgreementsMDAL();
//        Agreement agreement = new Agreement();
//        agreement.setUsername(u);
//        agreement.setSla(processClass);
//        agreement.setExpireDate(new java.util.Date());
//        agreement.setArrivalRate(1.5);
//        try {
//            ami.createAgreement(agreement); //Call to the DB
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //Fine sottoscrizione SLA 1 per utente

        //Inizio definizione SLA classe 2

        processSLAConstraints = new Hashtable<ProcessSLAConstraint, String>();
        processSLAConstraints.put(ProcessSLAConstraint.COST, "50");
//        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.9");
        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.85");
        processSLAConstraints.put(ProcessSLAConstraint.REQUEST_RATE, "1.5");
//        processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "11");
                processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "18");
        processSLAConstraints.put(ProcessSLAConstraint.TOTAL_REQUEST_RATE, "0.1");
        processClass = new SLA();
        processClass.setProcessClass("2");
        processClass.setProcess(p);
        processClass.setSlaFile("SLAfile");
        processClass.setSlaConstraints(processSLAConstraints);
        processClass.setSlaMonitor(new Hashtable<ProcessSLAMonitor, String>());
        try {
            slami.createSLA(processClass); //Call to the DB
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //Fine definizione SLA classe 2


        //Inizio sottoscrizione SLA 2 per utente

//        slami = mdal.getSLAsMDAL();
//
//
//        agreement = new Agreement();
//        agreement.setUsername(u);
//        agreement.setSla(processClass);
//        agreement.setExpireDate(new java.util.Date());
//        agreement.setArrivalRate(1.0);
//        try {
//            ami.createAgreement(agreement); //Call to the DB
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //Fine sottoscrizione SLA 2 per utente


        //Inizio definizione SLA classe 3

        processSLAConstraints = new Hashtable<ProcessSLAConstraint, String>();
        processSLAConstraints.put(ProcessSLAConstraint.COST, "45");
//        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.9");
        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.82");
        processSLAConstraints.put(ProcessSLAConstraint.REQUEST_RATE, "1.5");
//        processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "15");
                processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "20");
        processSLAConstraints.put(ProcessSLAConstraint.TOTAL_REQUEST_RATE, "0.1");
        processClass = new SLA();
        processClass.setProcessClass("3");
        processClass.setProcess(p);
        processClass.setSlaFile("SLAfile");
        processClass.setSlaConstraints(processSLAConstraints);
        processClass.setSlaMonitor(new Hashtable<ProcessSLAMonitor, String>());
        try {
            slami.createSLA(processClass); //Call to the DB
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //Fine definizione SLA classe 3


        //Inizio sottoscrizione SLA 3 per utente

//        slami = mdal.getSLAsMDAL();
//
//
//        agreement = new Agreement();
//        agreement.setUsername(u);
//        agreement.setSla(processClass);
//        agreement.setExpireDate(new java.util.Date());
//        agreement.setArrivalRate(3.0);
//        try {
//            ami.createAgreement(agreement); //Call to the DB
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //Fine sottoscrizione SLA 3 per utente


        //Inizio definizione SLA classe 4

        processSLAConstraints = new Hashtable<ProcessSLAConstraint, String>();
        processSLAConstraints.put(ProcessSLAConstraint.COST, "40");
//        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.85");
        processSLAConstraints.put(ProcessSLAConstraint.RELIABILITY, "0.79");
        processSLAConstraints.put(ProcessSLAConstraint.REQUEST_RATE, "1.5");
//        processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "18");
        processSLAConstraints.put(ProcessSLAConstraint.RESPONSE_TIME, "22");
        processSLAConstraints.put(ProcessSLAConstraint.TOTAL_REQUEST_RATE, "0.1");
        processClass = new SLA();
        processClass.setProcessClass("4");
        processClass.setProcess(p);
        processClass.setSlaFile("SLAfile");
        processClass.setSlaConstraints(processSLAConstraints);
        processClass.setSlaMonitor(new Hashtable<ProcessSLAMonitor, String>());
        try {
            slami.createSLA(processClass); //Call to the DB
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //Fine definizione SLA classe 4


        //Inizio sottoscrizione SLA 4 per utente

//        slami = mdal.getSLAsMDAL();
//
//
//        agreement = new Agreement();
//        agreement.setUsername(u);
//        agreement.setSla(processClass);
//        agreement.setExpireDate(new java.util.Date());
//        agreement.setArrivalRate(1.0);
//        try {
//            ami.createAgreement(agreement); //Call to the DB
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //Fine sottoscrizione SLA 4 per utente


        //Inizio inserimento servizi concreti
        {

            ConcreteServicesMDALInterface csmi = mdal.getConcreteServicesMDAL();

            //Contratto stipulato con i servizi concreti
            Hashtable<OperationSLAConstraint, String> operationSLAConstraints = new Hashtable<OperationSLAConstraint, String>();
            operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");


            //Servizio S1_1
            ConcreteService cService = new ConcreteService();
            ConcreteOperation cOp = new ConcreteOperation();
            List<ConcreteOperation> cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(1);
            cService.setWsdlURL("http://mysql-services:8084/axis2/services/S1?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8084/axis2/services/S1");
            cService.setService(service);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(27);
//            cService.setWsdlURL("http://mysql-services:8089/axis2/services/S1?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8089/axis2/services/S1");
//            cService.setService(service);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }


            //Servizio S1_2
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(2);
            cService.setWsdlURL("http://mysql-services:8085/axis2/services/S1?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8085/axis2/services/S1");
            cService.setService(service);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(28);
//            cService.setWsdlURL("http://mysql-services:8090/axis2/services/S1?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8090/axis2/services/S1");
//            cService.setService(service);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S1_3
            operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(3);
            cService.setWsdlURL("http://mysql-services:8086/axis2/services/S1?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8086/axis2/services/S1");
            cService.setService(service);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(29);
//            cService.setWsdlURL("http://mysql-services:8091/axis2/services/S1?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8091/axis2/services/S1");
//            cService.setService(service);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S1_4
          operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(4);
            cService.setWsdlURL("http://mysql-services:8087/axis2/services/S1?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8087/axis2/services/S1");
            cService.setService(service);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            operationSLAConstraints.put(OperationSLAConstraint.COST, "4.5");
//            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
//            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
//            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "3");
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(30);
//            cService.setWsdlURL("http://mysql-services:8092/axis2/services/S1?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8092/axis2/services/S1");
//            cService.setService(service);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S1_5
//            operationSLAConstraints.put(OperationSLAConstraint.COST, "5.5");
//            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.99");
//            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
//            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(25);
//            cService.setWsdlURL("http://mysql-services:8088/axis2/services/S1?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8088/axis2/services/S1");
//            cService.setService(service);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(51);
//            cService.setWsdlURL("http://mysql-services:8093/axis2/services/S1?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8093/axis2/services/S1");
//            cService.setService(service);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }


            //Servizio S2_1
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op2);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(5);
            cService.setWsdlURL("http://mysql-services:8084/axis2/services/S2?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8084/axis2/services/S2");
            cService.setService(service2);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op2);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(31);
//            cService.setWsdlURL("http://mysql-services:8089/axis2/services/S2?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8089/axis2/services/S2");
//            cService.setService(service2);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }


            //Servizio S2_2
          operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op2);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(6);
            cService.setWsdlURL("http://mysql-services:8085/axis2/services/S2?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8085/axis2/services/S2");
            cService.setService(service2);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op2);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(32);
//            cService.setWsdlURL("http://mysql-services:8090/axis2/services/S2?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8090/axis2/services/S2");
//            cService.setService(service2);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S2_3
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op2);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(7);
            cService.setWsdlURL("http://mysql-services:8086/axis2/services/S2?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8086/axis2/services/S2");
            cService.setService(service2);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op2);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(33);
//            cService.setWsdlURL("http://mysql-services:8091/axis2/services/S2?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8091/axis2/services/S2");
//            cService.setService(service2);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S2_4
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op2);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(8);
            cService.setWsdlURL("http://mysql-services:8087/axis2/services/S2?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8087/axis2/services/S2");
            cService.setService(service2);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op2);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(34);
//            cService.setWsdlURL("http://mysql-services:8092/axis2/services/S2?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8092/axis2/services/S2");
//            cService.setService(service2);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S2_5
//            operationSLAConstraints.put(OperationSLAConstraint.COST, "5");
//            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
//            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
//            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "1");
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op2);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(26);
//            cService.setWsdlURL("http://mysql-services:8088/axis2/services/S2?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8088/axis2/services/S2");
//            cService.setService(service2);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op2);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(52);
//            cService.setWsdlURL("http://mysql-services:8093/axis2/services/S2?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8093/axis2/services/S2");
//            cService.setService(service2);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S3_1
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op3);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(9);
            cService.setWsdlURL("http://mysql-services:8084/axis2/services/S3?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8084/axis2/services/S3");
            cService.setService(service3);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op3);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(35);
//            cService.setWsdlURL("http://mysql-services:8089/axis2/services/S3?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8089/axis2/services/S3");
//            cService.setService(service3);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S3_2
            operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op3);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(10);
            cService.setWsdlURL("http://mysql-services:8085/axis2/services/S3?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8085/axis2/services/S3");
            cService.setService(service3);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op3);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(36);
//            cService.setWsdlURL("http://mysql-services:8090/axis2/services/S3?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8090/axis2/services/S3");
//            cService.setService(service3);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S3_3
        operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op3);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(11);
            cService.setWsdlURL("http://mysql-services:8086/axis2/services/S3?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8086/axis2/services/S3");
            cService.setService(service3);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op3);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(37);
//            cService.setWsdlURL("http://mysql-services:8091/axis2/services/S3?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8091/axis2/services/S3");
//            cService.setService(service3);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S3_4
            operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op3);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(12);
            cService.setWsdlURL("http://mysql-services:8087/axis2/services/S3?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8087/axis2/services/S3");
            cService.setService(service3);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op3);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(38);
//            cService.setWsdlURL("http://mysql-services:8092/axis2/services/S3?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8092/axis2/services/S3");
//            cService.setService(service3);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }


            //Servizio S4_1
          operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op4);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(13);
            cService.setWsdlURL("http://mysql-services:8084/axis2/services/S4?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8084/axis2/services/S4");
            cService.setService(service4);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op4);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(39);
//            cService.setWsdlURL("http://mysql-services:8089/axis2/services/S4?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8089/axis2/services/S4");
//            cService.setService(service4);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S4_2
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op4);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(14);
            cService.setWsdlURL("http://mysql-services:8085/axis2/services/S4?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8085/axis2/services/S4");
            cService.setService(service4);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op4);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(40);
//            cService.setWsdlURL("http://mysql-services:8090/axis2/services/S4?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8090/axis2/services/S4");
//            cService.setService(service4);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S4_3
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op4);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(15);
            cService.setWsdlURL("http://mysql-services:8086/axis2/services/S4?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8086/axis2/services/S4");
            cService.setService(service4);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op4);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(41);
//            cService.setWsdlURL("http://mysql-services:8091/axis2/services/S4?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8091/axis2/services/S4");
//            cService.setService(service4);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S4_4
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op4);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(16);
            cService.setWsdlURL("http://mysql-services:8087/axis2/services/S4?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8087/axis2/services/S4");
            cService.setService(service4);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op4);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(42);
//            cService.setWsdlURL("http://mysql-services:8092/axis2/services/S4?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8092/axis2/services/S4");
//            cService.setService(service4);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }


            //Servizio S5_1
          operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op5);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(17);
            cService.setWsdlURL("http://mysql-services:8084/axis2/services/S5?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8084/axis2/services/S5");
            cService.setService(service5);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op5);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(43);
//            cService.setWsdlURL("http://mysql-services:8089/axis2/services/S5?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8089/axis2/services/S5");
//            cService.setService(service5);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S5_2
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op5);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(18);
            cService.setWsdlURL("http://mysql-services:8085/axis2/services/S5?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8085/axis2/services/S5");
            cService.setService(service5);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op5);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(44);
//            cService.setWsdlURL("http://mysql-services:8090/axis2/services/S5?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8090/axis2/services/S5");
//            cService.setService(service5);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S5_3
            operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op5);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(19);
            cService.setWsdlURL("http://mysql-services:8086/axis2/services/S5?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8086/axis2/services/S5");
            cService.setService(service5);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op5);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(45);
//            cService.setWsdlURL("http://mysql-services:8091/axis2/services/S5?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8091/axis2/services/S5");
//            cService.setService(service5);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S5_4
          operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op5);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(20);
            cService.setWsdlURL("http://mysql-services:8087/axis2/services/S5?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8087/axis2/services/S5");
            cService.setService(service5);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op5);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(46);
//            cService.setWsdlURL("http://mysql-services:8092/axis2/services/S5?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8092/axis2/services/S5");
//            cService.setService(service5);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S6_1
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op6);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(21);
            cService.setWsdlURL("http://mysql-services:8084/axis2/services/S6?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8084/axis2/services/S6");
            cService.setService(service6);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op6);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(47);
//            cService.setWsdlURL("http://mysql-services:8089/axis2/services/S6?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8089/axis2/services/S6");
//            cService.setService(service6);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S6_2
          operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op6);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(22);
            cService.setWsdlURL("http://mysql-services:8085/axis2/services/S6?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8085/axis2/services/S6");
            cService.setService(service6);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op6);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(48);
//            cService.setWsdlURL("http://mysql-services:8090/axis2/services/S6?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8090/axis2/services/S6");
//            cService.setService(service6);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            //Servizio S6_3
            operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op6);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(23);
            cService.setWsdlURL("http://mysql-services:8086/axis2/services/S6?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8086/axis2/services/S6");
            cService.setService(service6);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op6);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(49);
//            cService.setWsdlURL("http://mysql-services:8091/axis2/services/S6?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8091/axis2/services/S6");
//            cService.setService(service6);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            //Servizio S6_4
           operationSLAConstraints.put(OperationSLAConstraint.COST, "1");
            operationSLAConstraints.put(OperationSLAConstraint.RELIABILITY, "0.995");
            operationSLAConstraints.put(OperationSLAConstraint.REQUEST_RATE, "10");
            operationSLAConstraints.put(OperationSLAConstraint.RESPONSE_TIME, "2");
            cService = new ConcreteService();
            cOp = new ConcreteOperation();
            cOps = new ArrayList<ConcreteOperation>();
            cOp.setIsWorking(Boolean.TRUE);
            cOp.setOperation(op6);
            cOp.setService(cService);
            cOp.setSlaFile("");
            cOp.setStateful(true);
            cOp.setSlaConstraints(operationSLAConstraints);
            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
            cOps.add(cOp);
            cService.setId(24);
            cService.setWsdlURL("http://mysql-services:8087/axis2/services/S6?wsdl");
            cService.setExpireDate(new java.util.Date());
            cService.setEndpointURL("http://mysql-services:8087/axis2/services/S6");
            cService.setService(service6);
            cService.setOperations(cOps);
            try {
                csmi.createConcreteService(cService); //Call to the DB
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            cService = new ConcreteService();
//            cOp = new ConcreteOperation();
//            cOps = new ArrayList<ConcreteOperation>();
//            cOp.setIsWorking(Boolean.TRUE);
//            cOp.setOperation(op6);
//            cOp.setService(cService);
//            cOp.setSlaFile("");
//            cOp.setStateful(true);
//            cOp.setSlaConstraints(operationSLAConstraints);
//            cOp.setSlaMonitor(new Hashtable<OperationSLAMonitor, String>());
//            cOps.add(cOp);
//            cService.setId(50);
//            cService.setWsdlURL("http://mysql-services:8092/axis2/services/S6?wsdl");
//            cService.setExpireDate(new java.util.Date());
//            cService.setEndpointURL("http://mysql-services:8092/axis2/services/S6");
//            cService.setService(service6);
//            cService.setOperations(cOps);
//            try {
//                csmi.createConcreteService(cService); //Call to the DB
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

//            ConcreteOperationsMDALInterface comi = mdal.getConcreteOperationsMDAL();
//            for (int i = 1; i <= 4; i++) {
//                ConcreteOperation co = comi.readConcreteOperation(i, "s1op");
//                System.out.println("cost: " + co.getSlaConstraints().get(OperationSLAConstraint.COST));
//            }
//            for (int i = 5; i <= 8; i++) {
//                ConcreteOperation co = comi.readConcreteOperation(i, "s2op");
//                System.out.println("cost: " + co.getSlaConstraints().get(OperationSLAConstraint.COST));
//            }
//            for (int i = 9; i <= 12; i++) {
//                ConcreteOperation co = comi.readConcreteOperation(i, "s3op");
//                System.out.println("cost: " + co.getSlaConstraints().get(OperationSLAConstraint.COST));
//            }
//            for (int i = 13; i <= 16; i++) {
//                ConcreteOperation co = comi.readConcreteOperation(i, "s4op");
//                System.out.println("cost: " + co.getSlaConstraints().get(OperationSLAConstraint.COST));
//            }
//            for (int i = 17; i <= 20; i++) {
//                ConcreteOperation co = comi.readConcreteOperation(i, "s5op");
//                System.out.println("cost: " + co.getSlaConstraints().get(OperationSLAConstraint.COST));
//            }
//            for (int i = 21; i <= 24; i++) {
//                ConcreteOperation co = comi.readConcreteOperation(i, "s6op");
//                System.out.println("cost: " + co.getSlaConstraints().get(OperationSLAConstraint.COST));
//            }

        }


        //Inserimento soluzione

        List<ServiceNode> solution = new ArrayList<ServiceNode>();

        //S1 solution
        List<EndpointNode> s1Endpoints = new ArrayList<EndpointNode>();
        EndpointNode s1Endpoint = new EndpointNode();
        s1Endpoint.setEndpointURL("http://mysql-services:8084/axis2/services/S1");
        s1Endpoint.setTargetNameSpace("http://ce/uniroma2/it/xsd");
        s1Endpoint.setServiceId(1);
        s1Endpoints.add(s1Endpoint);

        List<GroupNode> s1Groups = new ArrayList<GroupNode>();
        GroupNode s1Group = new GroupNode();
        s1Group.setGroupId(1);
        s1Group.setProbability((float) 1);
        s1Group.setType(GroupNode.Type.PARALLEL_OR);
        s1Group.setEndpoints(s1Endpoints);
        s1Groups.add(s1Group);

        List<OperationNode> s1Operations = new ArrayList<OperationNode>();
        OperationNode s1Operation = new OperationNode();
        s1Operation.setOperationName("s1op");
        s1Operation.setGroups(s1Groups);
        s1Operations.add(s1Operation);


        ServiceNode s1Service = new ServiceNode();
        s1Service.setServiceName("S1");
        s1Service.setOperations(s1Operations);
        solution.add(s1Service);

        //S2 solution
        List<EndpointNode> s2Endpoints = new ArrayList<EndpointNode>();
        EndpointNode s2Endpoint = new EndpointNode();
        s2Endpoint.setEndpointURL("http://mysql-services:8084/axis2/services/S2");
        s2Endpoint.setTargetNameSpace("http://ce/uniroma2/it/xsd");
        s2Endpoint.setServiceId(5);
        s2Endpoints.add(s2Endpoint);

        List<GroupNode> s2Groups = new ArrayList<GroupNode>();
        GroupNode s2Group = new GroupNode();
        s2Group.setGroupId(2);
        s2Group.setProbability((float) 1);
        s2Group.setType(GroupNode.Type.PARALLEL_OR);
        s2Group.setEndpoints(s2Endpoints);
        s2Groups.add(s2Group);

        List<OperationNode> s2Operations = new ArrayList<OperationNode>();
        OperationNode s2Operation = new OperationNode();
        s2Operation.setOperationName("s2op");
        s2Operation.setGroups(s2Groups);
        s2Operations.add(s2Operation);


        ServiceNode s2Service = new ServiceNode();
        s2Service.setServiceName("S2");
        s2Service.setOperations(s2Operations);
        solution.add(s2Service);

        //S3 solution
        List<EndpointNode> s3Endpoints = new ArrayList<EndpointNode>();
        EndpointNode s3Endpoint = new EndpointNode();
        s3Endpoint.setEndpointURL("http://mysql-services:8084/axis2/services/S3");
        s3Endpoint.setTargetNameSpace("http://ce/uniroma2/it/xsd");
        s3Endpoint.setServiceId(9);
        s3Endpoints.add(s3Endpoint);

        List<GroupNode> s3Groups = new ArrayList<GroupNode>();
        GroupNode s3Group = new GroupNode();
        s3Group.setGroupId(3);
        s3Group.setProbability((float) 1);
        s3Group.setType(GroupNode.Type.PARALLEL_OR);
        s3Group.setEndpoints(s3Endpoints);
        s3Groups.add(s3Group);

        List<OperationNode> s3Operations = new ArrayList<OperationNode>();
        OperationNode s3Operation = new OperationNode();
        s3Operation.setOperationName("s3op");
        s3Operation.setGroups(s3Groups);
        s3Operations.add(s3Operation);


        ServiceNode s3Service = new ServiceNode();
        s3Service.setServiceName("S3");
        s3Service.setOperations(s3Operations);
        solution.add(s3Service);

        //S4 solution
        List<EndpointNode> s4Endpoints = new ArrayList<EndpointNode>();
        EndpointNode s4Endpoint = new EndpointNode();
        s4Endpoint.setEndpointURL("http://mysql-services:8084/axis2/services/S4");
        s4Endpoint.setTargetNameSpace("http://ce/uniroma2/it/xsd");
        s4Endpoint.setServiceId(13);
        s4Endpoints.add(s4Endpoint);

        List<GroupNode> s4Groups = new ArrayList<GroupNode>();
        GroupNode s4Group = new GroupNode();
        s4Group.setGroupId(4);
        s4Group.setProbability((float) 1);
        s4Group.setType(GroupNode.Type.PARALLEL_OR);
        s4Group.setEndpoints(s4Endpoints);
        s4Groups.add(s4Group);

        List<OperationNode> s4Operations = new ArrayList<OperationNode>();
        OperationNode s4Operation = new OperationNode();
        s4Operation.setOperationName("s4op");
        s4Operation.setGroups(s4Groups);
        s4Operations.add(s4Operation);


        ServiceNode s4Service = new ServiceNode();
        s4Service.setServiceName("S4");
        s4Service.setOperations(s4Operations);
        solution.add(s4Service);

        //S5 solution
        List<EndpointNode> s5Endpoints = new ArrayList<EndpointNode>();
        EndpointNode s5Endpoint = new EndpointNode();
        s5Endpoint.setEndpointURL("http://mysql-services:8084/axis2/services/S5");
        s5Endpoint.setTargetNameSpace("http://ce/uniroma2/it/xsd");
        s5Endpoint.setServiceId(18);
        s5Endpoints.add(s5Endpoint);

        List<GroupNode> s5Groups = new ArrayList<GroupNode>();
        GroupNode s5Group = new GroupNode();
        s5Group.setGroupId(5);
        s5Group.setProbability((float) 1);
        s5Group.setType(GroupNode.Type.PARALLEL_OR);
        s5Group.setEndpoints(s5Endpoints);
        s5Groups.add(s5Group);

        List<OperationNode> s5Operations = new ArrayList<OperationNode>();
        OperationNode s5Operation = new OperationNode();
        s5Operation.setOperationName("s5op");
        s5Operation.setGroups(s5Groups);
        s5Operations.add(s5Operation);


        ServiceNode s5Service = new ServiceNode();
        s5Service.setServiceName("S5");
        s5Service.setOperations(s5Operations);
        solution.add(s5Service);

        //S6 solution
        List<EndpointNode> s6Endpoints = new ArrayList<EndpointNode>();
        EndpointNode s6Endpoint = new EndpointNode();
        s6Endpoint.setEndpointURL("http://mysql-services:8084/axis2/services/S6");
        s6Endpoint.setTargetNameSpace("http://ce/uniroma2/it/xsd");
        s6Endpoint.setServiceId(21);
        s6Endpoints.add(s6Endpoint);

        List<GroupNode> s6Groups = new ArrayList<GroupNode>();
        GroupNode s6Group = new GroupNode();
        s6Group.setGroupId(6);
        s6Group.setProbability((float) 1);
        s6Group.setType(GroupNode.Type.PARALLEL_OR);
        s6Group.setEndpoints(s6Endpoints);
        s6Groups.add(s6Group);

        List<OperationNode> s6Operations = new ArrayList<OperationNode>();
        OperationNode s6Operation = new OperationNode();
        s6Operation.setOperationName("s6op");
        s6Operation.setGroups(s6Groups);
        s6Operations.add(s6Operation);


        ServiceNode s6Service = new ServiceNode();
        s6Service.setServiceName("S6");
        s6Service.setOperations(s6Operations);
        solution.add(s6Service);


        SolutionsMDALInterface smi = mdal.getSolutionsMDAL();
        try {
            smi.createSolution(p.getProcessName(), "1", solution, "-1");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        System.out.println("Soluzione inserita");

        //Soluzione inserita

        UsersMDALInterface umi = mdal.getUsersMDAL();
        User u = new User();
        u.setName("Emanuele");
        u.setSurname("Emanuele");
        u.setUsername("emanuele");
        u.setPassword("emanuele");
        try {
            umi.createUser(u);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Utente inserito

    }

    void builPathAlfa98(Process p, AbstractService service,  AbstractService service2,
                                   AbstractService service3, AbstractService service4,
                                   AbstractService service5, AbstractService service6) {

        ProcessPath[] paths = new ProcessPath[8];
        paths[0] = new ProcessPath(p, 1 / 3d);
        paths[1] = new ProcessPath(p, 1 / 3d);
        paths[2] = new ProcessPath(p, 1 / 9d);
        paths[3] = new ProcessPath(p, 1 / 9d);
        paths[4] = new ProcessPath(p, 1 / 27d);
        paths[5] = new ProcessPath(p, 1 / 27d);
        paths[6] = new ProcessPath(p, 1 / 81d);
        paths[7] = new ProcessPath(p, 1 / 81d);

        /* primi due */
        ProcessSubPath[] subpath1 = new ProcessSubPath[2];
        subpath1[0] = new ProcessSubPath(p);
        subpath1[1] = new ProcessSubPath(p);

        subpath1[0].addActivity(service);
        subpath1[0].addActivity(service2);
        subpath1[0].addActivity(service4);
        subpath1[0].addActivity(service5);

        subpath1[1].addActivity(service3);
        subpath1[1].addActivity(service4);
        subpath1[1].addActivity(service5);

        ProcessSubPath[] subpath2 = new ProcessSubPath[2];
        subpath2[0] = new ProcessSubPath(p);
        subpath2[1] = new ProcessSubPath(p);

        subpath2[0].addActivity(service);
        subpath2[0].addActivity(service2);
        subpath2[0].addActivity(service4);
        subpath2[0].addActivity(service6);

        subpath2[1].addActivity(service3);
        subpath2[1].addActivity(service4);
        subpath2[1].addActivity(service6);

        paths[0].setSubPaths(subpath1);
        paths[1].setSubPaths(subpath2);

        /* secondi due */
        ProcessSubPath[] subpath3 = new ProcessSubPath[4];
        subpath3[0] = new ProcessSubPath(p);
        subpath3[1] = new ProcessSubPath(p);
        subpath3[2] = new ProcessSubPath(p);
        subpath3[3] = new ProcessSubPath(p);

        subpath3[0].addActivity(service);
        subpath3[0].addActivity(service2);
        subpath3[0].addActivity(service);
        subpath3[0].addActivity(service2);
        subpath3[0].addActivity(service4);
        subpath3[0].addActivity(service5);

        subpath3[1].addActivity(service);
        subpath3[1].addActivity(service2);
        subpath3[1].addActivity(service3);
        subpath3[1].addActivity(service4);
        subpath3[1].addActivity(service5);

        subpath3[2].addActivity(service3);
        subpath3[2].addActivity(service);
        subpath3[2].addActivity(service2);
        subpath3[2].addActivity(service4);
        subpath3[2].addActivity(service5);

        subpath3[3].addActivity(service3);
        subpath3[3].addActivity(service3);
        subpath3[3].addActivity(service4);
        subpath3[3].addActivity(service5);

        ProcessSubPath[] subpath4 = new ProcessSubPath[4];
        subpath4[0] = new ProcessSubPath(p);
        subpath4[1] = new ProcessSubPath(p);
        subpath4[2] = new ProcessSubPath(p);
        subpath4[3] = new ProcessSubPath(p);

        subpath4[0].addActivity(service);
        subpath4[0].addActivity(service2);
        subpath4[0].addActivity(service);
        subpath4[0].addActivity(service2);
        subpath4[0].addActivity(service4);
        subpath4[0].addActivity(service6);

        subpath4[1].addActivity(service);
        subpath4[1].addActivity(service2);
        subpath4[1].addActivity(service3);
        subpath4[1].addActivity(service4);
        subpath4[1].addActivity(service6);

        subpath4[2].addActivity(service3);
        subpath4[2].addActivity(service);
        subpath4[2].addActivity(service2);
        subpath4[2].addActivity(service4);
        subpath4[2].addActivity(service6);

        subpath4[3].addActivity(service3);
        subpath4[3].addActivity(service3);
        subpath4[3].addActivity(service4);
        subpath4[3].addActivity(service6);

        paths[2].setSubPaths(subpath3);
        paths[3].setSubPaths(subpath4);

        /* terzi due */

        ProcessSubPath[] subpath5 = new ProcessSubPath[8];
        for (int i = 0; i < 8; i++) {
            subpath5[i] = new ProcessSubPath(p);
        }

        subpath5[0].addActivity(service);
        subpath5[0].addActivity(service2);
        subpath5[0].addActivity(service);
        subpath5[0].addActivity(service2);
        subpath5[0].addActivity(service);
        subpath5[0].addActivity(service2);
        subpath5[0].addActivity(service4);
        subpath5[0].addActivity(service5);

        subpath5[1].addActivity(service);
        subpath5[1].addActivity(service2);
        subpath5[1].addActivity(service);
        subpath5[1].addActivity(service2);
        subpath5[1].addActivity(service3);
        subpath5[1].addActivity(service4);
        subpath5[1].addActivity(service5);

        subpath5[2].addActivity(service);
        subpath5[2].addActivity(service2);
        subpath5[2].addActivity(service3);
        subpath5[2].addActivity(service);
        subpath5[2].addActivity(service2);
        subpath5[2].addActivity(service4);
        subpath5[2].addActivity(service5);

        subpath5[3].addActivity(service);
        subpath5[3].addActivity(service2);
        subpath5[3].addActivity(service3);
        subpath5[3].addActivity(service3);
        subpath5[3].addActivity(service4);
        subpath5[3].addActivity(service5);

        subpath5[4].addActivity(service3);
        subpath5[4].addActivity(service);
        subpath5[4].addActivity(service2);
        subpath5[4].addActivity(service);
        subpath5[4].addActivity(service2);
        subpath5[4].addActivity(service4);
        subpath5[4].addActivity(service5);

        subpath5[5].addActivity(service3);
        subpath5[5].addActivity(service);
        subpath5[5].addActivity(service2);
        subpath5[5].addActivity(service3);
        subpath5[5].addActivity(service4);
        subpath5[5].addActivity(service5);

        subpath5[6].addActivity(service3);
        subpath5[6].addActivity(service3);
        subpath5[6].addActivity(service);
        subpath5[6].addActivity(service2);
        subpath5[6].addActivity(service4);
        subpath5[6].addActivity(service5);

        subpath5[7].addActivity(service3);
        subpath5[7].addActivity(service3);
        subpath5[7].addActivity(service3);
        subpath5[7].addActivity(service4);
        subpath5[7].addActivity(service5);

        ProcessSubPath[] subpath6 = new ProcessSubPath[8];
        for (int i = 0; i < 8; i++) {
            subpath6[i] = new ProcessSubPath(p);
        }

        subpath6[0].addActivity(service);
        subpath6[0].addActivity(service2);
        subpath6[0].addActivity(service);
        subpath6[0].addActivity(service2);
        subpath6[0].addActivity(service);
        subpath6[0].addActivity(service2);
        subpath6[0].addActivity(service4);
        subpath6[0].addActivity(service6);

        subpath6[1].addActivity(service);
        subpath6[1].addActivity(service2);
        subpath6[1].addActivity(service);
        subpath6[1].addActivity(service2);
        subpath6[1].addActivity(service3);
        subpath6[1].addActivity(service4);
        subpath6[1].addActivity(service6);

        subpath6[2].addActivity(service);
        subpath6[2].addActivity(service2);
        subpath6[2].addActivity(service3);
        subpath6[2].addActivity(service);
        subpath6[2].addActivity(service2);
        subpath6[2].addActivity(service4);
        subpath6[2].addActivity(service6);

        subpath6[3].addActivity(service);
        subpath6[3].addActivity(service2);
        subpath6[3].addActivity(service3);
        subpath6[3].addActivity(service3);
        subpath6[3].addActivity(service4);
        subpath6[3].addActivity(service6);

        subpath6[4].addActivity(service3);
        subpath6[4].addActivity(service);
        subpath6[4].addActivity(service2);
        subpath6[4].addActivity(service);
        subpath6[4].addActivity(service2);
        subpath6[4].addActivity(service4);
        subpath6[4].addActivity(service6);

        subpath6[5].addActivity(service3);
        subpath6[5].addActivity(service);
        subpath6[5].addActivity(service2);
        subpath6[5].addActivity(service3);
        subpath6[5].addActivity(service4);
        subpath6[5].addActivity(service6);

        subpath6[6].addActivity(service3);
        subpath6[6].addActivity(service3);
        subpath6[6].addActivity(service);
        subpath6[6].addActivity(service2);
        subpath6[6].addActivity(service4);
        subpath6[6].addActivity(service6);

        subpath6[7].addActivity(service3);
        subpath6[7].addActivity(service3);
        subpath6[7].addActivity(service3);
        subpath6[7].addActivity(service4);
        subpath6[7].addActivity(service6);

        paths[4].setSubPaths(subpath5);
        paths[5].setSubPaths(subpath6);

        /* 16!!!!!!!!!!!! */
        ProcessSubPath[] subpath7 = new ProcessSubPath[16];
        for (int i = 0; i < 16; i++) {
            subpath7[i] = new ProcessSubPath(p);
        }

        subpath7[0].addActivity(service);
        subpath7[0].addActivity(service2);
        subpath7[0].addActivity(service);
        subpath7[0].addActivity(service2);
        subpath7[0].addActivity(service);
        subpath7[0].addActivity(service2);
        subpath7[0].addActivity(service);
        subpath7[0].addActivity(service2);
        subpath7[0].addActivity(service4);
        subpath7[0].addActivity(service5);

        subpath7[1].addActivity(service);
        subpath7[1].addActivity(service2);
        subpath7[1].addActivity(service);
        subpath7[1].addActivity(service2);
        subpath7[1].addActivity(service);
        subpath7[1].addActivity(service2);
        subpath7[1].addActivity(service3);
        subpath7[1].addActivity(service4);
        subpath7[1].addActivity(service5);

        subpath7[2].addActivity(service);
        subpath7[2].addActivity(service2);
        subpath7[2].addActivity(service);
        subpath7[2].addActivity(service2);
        subpath7[2].addActivity(service3);
        subpath7[2].addActivity(service);
        subpath7[2].addActivity(service2);
        subpath7[2].addActivity(service4);
        subpath7[2].addActivity(service5);

        subpath7[3].addActivity(service);
        subpath7[3].addActivity(service2);
        subpath7[3].addActivity(service);
        subpath7[3].addActivity(service2);
        subpath7[3].addActivity(service3);
        subpath7[3].addActivity(service3);
        subpath7[3].addActivity(service4);
        subpath7[3].addActivity(service5);

        subpath7[4].addActivity(service);
        subpath7[4].addActivity(service2);
        subpath7[4].addActivity(service3);
        subpath7[4].addActivity(service);
        subpath7[4].addActivity(service2);
        subpath7[4].addActivity(service);
        subpath7[4].addActivity(service2);
        subpath7[4].addActivity(service4);
        subpath7[4].addActivity(service5);

        subpath7[5].addActivity(service);
        subpath7[5].addActivity(service2);
        subpath7[5].addActivity(service3);
        subpath7[5].addActivity(service);
        subpath7[5].addActivity(service2);
        subpath7[5].addActivity(service3);
        subpath7[5].addActivity(service4);
        subpath7[5].addActivity(service5);

        subpath7[6].addActivity(service);
        subpath7[6].addActivity(service2);
        subpath7[6].addActivity(service3);
        subpath7[6].addActivity(service3);
        subpath7[6].addActivity(service);
        subpath7[6].addActivity(service2);
        subpath7[6].addActivity(service4);
        subpath7[6].addActivity(service5);

        subpath7[7].addActivity(service);
        subpath7[7].addActivity(service2);
        subpath7[7].addActivity(service3);
        subpath7[7].addActivity(service3);
        subpath7[7].addActivity(service3);
        subpath7[7].addActivity(service4);
        subpath7[7].addActivity(service5);

        subpath7[8].addActivity(service3);
        subpath7[8].addActivity(service);
        subpath7[8].addActivity(service2);
        subpath7[8].addActivity(service);
        subpath7[8].addActivity(service2);
        subpath7[8].addActivity(service);
        subpath7[8].addActivity(service2);
        subpath7[8].addActivity(service4);
        subpath7[8].addActivity(service5);

        subpath7[9].addActivity(service3);
        subpath7[9].addActivity(service);
        subpath7[9].addActivity(service2);
        subpath7[9].addActivity(service);
        subpath7[9].addActivity(service2);
        subpath7[9].addActivity(service3);
        subpath7[9].addActivity(service4);
        subpath7[9].addActivity(service5);

        subpath7[10].addActivity(service3);
        subpath7[10].addActivity(service);
        subpath7[10].addActivity(service2);
        subpath7[10].addActivity(service3);
        subpath7[10].addActivity(service3);
        subpath7[10].addActivity(service4);
        subpath7[10].addActivity(service5);

        subpath7[11].addActivity(service3);
        subpath7[11].addActivity(service);
        subpath7[11].addActivity(service2);
        subpath7[11].addActivity(service3);
        subpath7[11].addActivity(service);
        subpath7[11].addActivity(service2);
        subpath7[11].addActivity(service4);
        subpath7[11].addActivity(service5);

        subpath7[12].addActivity(service3);
        subpath7[12].addActivity(service3);
        subpath7[12].addActivity(service);
        subpath7[12].addActivity(service2);
        subpath7[12].addActivity(service);
        subpath7[12].addActivity(service2);
        subpath7[12].addActivity(service4);
        subpath7[12].addActivity(service5);

        subpath7[13].addActivity(service3);
        subpath7[13].addActivity(service3);
        subpath7[13].addActivity(service);
        subpath7[13].addActivity(service2);
        subpath7[13].addActivity(service3);
        subpath7[13].addActivity(service4);
        subpath7[13].addActivity(service5);

        subpath7[14].addActivity(service3);
        subpath7[14].addActivity(service3);
        subpath7[14].addActivity(service3);
        subpath7[14].addActivity(service);
        subpath7[14].addActivity(service2);
        subpath7[14].addActivity(service4);
        subpath7[14].addActivity(service5);

        subpath7[15].addActivity(service3);
        subpath7[15].addActivity(service3);
        subpath7[15].addActivity(service3);
        subpath7[15].addActivity(service3);
        subpath7[15].addActivity(service4);
        subpath7[15].addActivity(service5);

        ProcessSubPath[] subpath8 = new ProcessSubPath[16];
        for (int i = 0; i < 16; i++) {
            subpath8[i] = new ProcessSubPath(p);
        }

        subpath8[0].addActivity(service);
        subpath8[0].addActivity(service2);
        subpath8[0].addActivity(service);
        subpath8[0].addActivity(service2);
        subpath8[0].addActivity(service);
        subpath8[0].addActivity(service2);
        subpath8[0].addActivity(service);
        subpath8[0].addActivity(service2);
        subpath8[0].addActivity(service4);
        subpath8[0].addActivity(service6);

        subpath8[1].addActivity(service);
        subpath8[1].addActivity(service2);
        subpath8[1].addActivity(service);
        subpath8[1].addActivity(service2);
        subpath8[1].addActivity(service);
        subpath8[1].addActivity(service2);
        subpath8[1].addActivity(service3);
        subpath8[1].addActivity(service4);
        subpath8[1].addActivity(service6);

        subpath8[2].addActivity(service);
        subpath8[2].addActivity(service2);
        subpath8[2].addActivity(service);
        subpath8[2].addActivity(service2);
        subpath8[2].addActivity(service3);
        subpath8[2].addActivity(service);
        subpath8[2].addActivity(service2);
        subpath8[2].addActivity(service4);
        subpath8[2].addActivity(service6);

        subpath8[3].addActivity(service);
        subpath8[3].addActivity(service2);
        subpath8[3].addActivity(service);
        subpath8[3].addActivity(service2);
        subpath8[3].addActivity(service3);
        subpath8[3].addActivity(service3);
        subpath8[3].addActivity(service4);
        subpath8[3].addActivity(service6);

        subpath8[4].addActivity(service);
        subpath8[4].addActivity(service2);
        subpath8[4].addActivity(service3);
        subpath8[4].addActivity(service);
        subpath8[4].addActivity(service2);
        subpath8[4].addActivity(service);
        subpath8[4].addActivity(service2);
        subpath8[4].addActivity(service4);
        subpath8[4].addActivity(service6);

        subpath8[5].addActivity(service);
        subpath8[5].addActivity(service2);
        subpath8[5].addActivity(service3);
        subpath8[5].addActivity(service);
        subpath8[5].addActivity(service2);
        subpath8[5].addActivity(service3);
        subpath8[5].addActivity(service4);
        subpath8[5].addActivity(service6);

        subpath8[6].addActivity(service);
        subpath8[6].addActivity(service2);
        subpath8[6].addActivity(service3);
        subpath8[6].addActivity(service3);
        subpath8[6].addActivity(service);
        subpath8[6].addActivity(service2);
        subpath8[6].addActivity(service4);
        subpath8[6].addActivity(service6);

        subpath8[7].addActivity(service);
        subpath8[7].addActivity(service2);
        subpath8[7].addActivity(service3);
        subpath8[7].addActivity(service3);
        subpath8[7].addActivity(service3);
        subpath8[7].addActivity(service4);
        subpath8[7].addActivity(service6);

        subpath8[8].addActivity(service3);
        subpath8[8].addActivity(service);
        subpath8[8].addActivity(service2);
        subpath8[8].addActivity(service);
        subpath8[8].addActivity(service2);
        subpath8[8].addActivity(service);
        subpath8[8].addActivity(service2);
        subpath8[8].addActivity(service4);
        subpath8[8].addActivity(service6);

        subpath8[9].addActivity(service3);
        subpath8[9].addActivity(service);
        subpath8[9].addActivity(service2);
        subpath8[9].addActivity(service);
        subpath8[9].addActivity(service2);
        subpath8[9].addActivity(service3);
        subpath8[9].addActivity(service4);
        subpath8[9].addActivity(service6);

        subpath8[10].addActivity(service3);
        subpath8[10].addActivity(service);
        subpath8[10].addActivity(service2);
        subpath8[10].addActivity(service3);
        subpath8[10].addActivity(service3);
        subpath8[10].addActivity(service4);
        subpath8[10].addActivity(service6);

        subpath8[11].addActivity(service3);
        subpath8[11].addActivity(service);
        subpath8[11].addActivity(service2);
        subpath8[11].addActivity(service3);
        subpath8[11].addActivity(service);
        subpath8[11].addActivity(service2);
        subpath8[11].addActivity(service4);
        subpath8[11].addActivity(service6);

        subpath8[12].addActivity(service3);
        subpath8[12].addActivity(service3);
        subpath8[12].addActivity(service);
        subpath8[12].addActivity(service2);
        subpath8[12].addActivity(service);
        subpath8[12].addActivity(service2);
        subpath8[12].addActivity(service4);
        subpath8[12].addActivity(service6);

        subpath8[13].addActivity(service3);
        subpath8[13].addActivity(service3);
        subpath8[13].addActivity(service);
        subpath8[13].addActivity(service2);
        subpath8[13].addActivity(service3);
        subpath8[13].addActivity(service4);
        subpath8[13].addActivity(service6);

        subpath8[14].addActivity(service3);
        subpath8[14].addActivity(service3);
        subpath8[14].addActivity(service3);
        subpath8[14].addActivity(service);
        subpath8[14].addActivity(service2);
        subpath8[14].addActivity(service4);
        subpath8[14].addActivity(service6);

        subpath8[15].addActivity(service3);
        subpath8[15].addActivity(service3);
        subpath8[15].addActivity(service3);
        subpath8[15].addActivity(service3);
        subpath8[15].addActivity(service4);
        subpath8[15].addActivity(service6);

        paths[6].setSubPaths(subpath7);
        paths[7].setSubPaths(subpath8);
        p.setProcessPaths(paths);
    }

    void builPathAlfa96(Process p, AbstractService service,  AbstractService service2,
                                   AbstractService service3, AbstractService service4,
                                   AbstractService service5, AbstractService service6) {

        ProcessPath[] paths = new ProcessPath[6];
        paths[0] = new ProcessPath(p, 1 / 3d);
        paths[1] = new ProcessPath(p, 1 / 3d);
        paths[2] = new ProcessPath(p, 1 / 9d);
        paths[3] = new ProcessPath(p, 1 / 9d);
        paths[4] = new ProcessPath(p, 1 / 27d + 0.018518519);
        paths[5] = new ProcessPath(p, 1 / 27d + 0.018518519);

        Hashtable<AbstractService, Integer> multipleOcc = new Hashtable<AbstractService, Integer>();


        /* primi due */
        ProcessSubPath[] subpath1 = new ProcessSubPath[2];
        subpath1[0] = new ProcessSubPath(p);
        subpath1[1] = new ProcessSubPath(p);

        subpath1[0].addActivity(service);
        subpath1[0].addActivity(service2);
        subpath1[0].addActivity(service4);
        subpath1[0].addActivity(service5);

        subpath1[1].addActivity(service3);
        subpath1[1].addActivity(service4);
        subpath1[1].addActivity(service5);

        ProcessSubPath[] subpath2 = new ProcessSubPath[2];
        subpath2[0] = new ProcessSubPath(p);
        subpath2[1] = new ProcessSubPath(p);

        subpath2[0].addActivity(service);
        subpath2[0].addActivity(service2);
        subpath2[0].addActivity(service4);
        subpath2[0].addActivity(service6);

        subpath2[1].addActivity(service3);
        subpath2[1].addActivity(service4);
        subpath2[1].addActivity(service6);

        paths[0].setSubPaths(subpath1);
        paths[0].setMultipleOcc(multipleOcc);
        paths[1].setSubPaths(subpath2);
        paths[1].setMultipleOcc(multipleOcc);

        /* secondi due */
        ProcessSubPath[] subpath3 = new ProcessSubPath[4];
        subpath3[0] = new ProcessSubPath(p);
        subpath3[1] = new ProcessSubPath(p);
        subpath3[2] = new ProcessSubPath(p);
        subpath3[3] = new ProcessSubPath(p);

        subpath3[0].addActivity(service);
        subpath3[0].addActivity(service2);
        subpath3[0].addActivity(service);
        subpath3[0].addActivity(service2);
        subpath3[0].addActivity(service4);
        subpath3[0].addActivity(service5);

        subpath3[1].addActivity(service);
        subpath3[1].addActivity(service2);
        subpath3[1].addActivity(service3);
        subpath3[1].addActivity(service4);
        subpath3[1].addActivity(service5);

        subpath3[2].addActivity(service3);
        subpath3[2].addActivity(service);
        subpath3[2].addActivity(service2);
        subpath3[2].addActivity(service4);
        subpath3[2].addActivity(service5);

        subpath3[3].addActivity(service3);
        subpath3[3].addActivity(service3);
        subpath3[3].addActivity(service4);
        subpath3[3].addActivity(service5);

        ProcessSubPath[] subpath4 = new ProcessSubPath[4];
        subpath4[0] = new ProcessSubPath(p);
        subpath4[1] = new ProcessSubPath(p);
        subpath4[2] = new ProcessSubPath(p);
        subpath4[3] = new ProcessSubPath(p);

        subpath4[0].addActivity(service);
        subpath4[0].addActivity(service2);
        subpath4[0].addActivity(service);
        subpath4[0].addActivity(service2);
        subpath4[0].addActivity(service4);
        subpath4[0].addActivity(service6);

        subpath4[1].addActivity(service);
        subpath4[1].addActivity(service2);
        subpath4[1].addActivity(service3);
        subpath4[1].addActivity(service4);
        subpath4[1].addActivity(service6);

        subpath4[2].addActivity(service3);
        subpath4[2].addActivity(service);
        subpath4[2].addActivity(service2);
        subpath4[2].addActivity(service4);
        subpath4[2].addActivity(service6);

        subpath4[3].addActivity(service3);
        subpath4[3].addActivity(service3);
        subpath4[3].addActivity(service4);
        subpath4[3].addActivity(service6);

        multipleOcc = new Hashtable<AbstractService, Integer>();
        multipleOcc.put(service, 2);
        multipleOcc.put(service2, 2);
        multipleOcc.put(service3, 2);
        paths[2].setSubPaths(subpath3);
        paths[2].setMultipleOcc(multipleOcc);
        paths[3].setSubPaths(subpath4);
        paths[3].setMultipleOcc(multipleOcc);

        /* terzi due */

        ProcessSubPath[] subpath5 = new ProcessSubPath[8];
        for (int i = 0; i < 8; i++) {
            subpath5[i] = new ProcessSubPath(p);
        }

        subpath5[0].addActivity(service);
        subpath5[0].addActivity(service2);
        subpath5[0].addActivity(service);
        subpath5[0].addActivity(service2);
        subpath5[0].addActivity(service);
        subpath5[0].addActivity(service2);
        subpath5[0].addActivity(service4);
        subpath5[0].addActivity(service5);

        subpath5[1].addActivity(service);
        subpath5[1].addActivity(service2);
        subpath5[1].addActivity(service);
        subpath5[1].addActivity(service2);
        subpath5[1].addActivity(service3);
        subpath5[1].addActivity(service4);
        subpath5[1].addActivity(service5);

        subpath5[2].addActivity(service);
        subpath5[2].addActivity(service2);
        subpath5[2].addActivity(service3);
        subpath5[2].addActivity(service);
        subpath5[2].addActivity(service2);
        subpath5[2].addActivity(service4);
        subpath5[2].addActivity(service5);

        subpath5[3].addActivity(service);
        subpath5[3].addActivity(service2);
        subpath5[3].addActivity(service3);
        subpath5[3].addActivity(service3);
        subpath5[3].addActivity(service4);
        subpath5[3].addActivity(service5);

        subpath5[4].addActivity(service3);
        subpath5[4].addActivity(service);
        subpath5[4].addActivity(service2);
        subpath5[4].addActivity(service);
        subpath5[4].addActivity(service2);
        subpath5[4].addActivity(service4);
        subpath5[4].addActivity(service5);

        subpath5[5].addActivity(service3);
        subpath5[5].addActivity(service);
        subpath5[5].addActivity(service2);
        subpath5[5].addActivity(service3);
        subpath5[5].addActivity(service4);
        subpath5[5].addActivity(service5);

        subpath5[6].addActivity(service3);
        subpath5[6].addActivity(service3);
        subpath5[6].addActivity(service);
        subpath5[6].addActivity(service2);
        subpath5[6].addActivity(service4);
        subpath5[6].addActivity(service5);

        subpath5[7].addActivity(service3);
        subpath5[7].addActivity(service3);
        subpath5[7].addActivity(service3);
        subpath5[7].addActivity(service4);
        subpath5[7].addActivity(service5);

        ProcessSubPath[] subpath6 = new ProcessSubPath[8];
        for (int i = 0; i < 8; i++) {
            subpath6[i] = new ProcessSubPath(p);
        }

        subpath6[0].addActivity(service);
        subpath6[0].addActivity(service2);
        subpath6[0].addActivity(service);
        subpath6[0].addActivity(service2);
        subpath6[0].addActivity(service);
        subpath6[0].addActivity(service2);
        subpath6[0].addActivity(service4);
        subpath6[0].addActivity(service6);

        subpath6[1].addActivity(service);
        subpath6[1].addActivity(service2);
        subpath6[1].addActivity(service);
        subpath6[1].addActivity(service2);
        subpath6[1].addActivity(service3);
        subpath6[1].addActivity(service4);
        subpath6[1].addActivity(service6);

        subpath6[2].addActivity(service);
        subpath6[2].addActivity(service2);
        subpath6[2].addActivity(service3);
        subpath6[2].addActivity(service);
        subpath6[2].addActivity(service2);
        subpath6[2].addActivity(service4);
        subpath6[2].addActivity(service6);

        subpath6[3].addActivity(service);
        subpath6[3].addActivity(service2);
        subpath6[3].addActivity(service3);
        subpath6[3].addActivity(service3);
        subpath6[3].addActivity(service4);
        subpath6[3].addActivity(service6);

        subpath6[4].addActivity(service3);
        subpath6[4].addActivity(service);
        subpath6[4].addActivity(service2);
        subpath6[4].addActivity(service);
        subpath6[4].addActivity(service2);
        subpath6[4].addActivity(service4);
        subpath6[4].addActivity(service6);

        subpath6[5].addActivity(service3);
        subpath6[5].addActivity(service);
        subpath6[5].addActivity(service2);
        subpath6[5].addActivity(service3);
        subpath6[5].addActivity(service4);
        subpath6[5].addActivity(service6);

        subpath6[6].addActivity(service3);
        subpath6[6].addActivity(service3);
        subpath6[6].addActivity(service);
        subpath6[6].addActivity(service2);
        subpath6[6].addActivity(service4);
        subpath6[6].addActivity(service6);

        subpath6[7].addActivity(service3);
        subpath6[7].addActivity(service3);
        subpath6[7].addActivity(service3);
        subpath6[7].addActivity(service4);
        subpath6[7].addActivity(service6);

        multipleOcc = new Hashtable<AbstractService, Integer>();
        multipleOcc.put(service, 3);
        multipleOcc.put(service2, 3);
        multipleOcc.put(service3, 3);
        paths[4].setSubPaths(subpath5);
        paths[4].setMultipleOcc(multipleOcc);
        paths[5].setSubPaths(subpath6);
        paths[5].setMultipleOcc(multipleOcc);

        p.setProcessPaths(paths);
    }
}

