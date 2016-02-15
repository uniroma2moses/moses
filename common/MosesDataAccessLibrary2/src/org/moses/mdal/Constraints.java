/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import java.io.Serializable;

/**
 *
 * @author francesco
 */
public class Constraints implements Serializable {

    public enum ClientSLAConstraint {

        RELIABILITY, RESPONSE_TIME, COST, REQUEST_RATE
    };

    public enum OperationSLAConstraint {

        RELIABILITY, RESPONSE_TIME, COST, REQUEST_RATE
    };

    public enum ProcessSLAConstraint {

        RELIABILITY, RESPONSE_TIME, COST, REQUEST_RATE, TOTAL_REQUEST_RATE
    };

    public enum ClientSLAMonitor {

        AVG_RELIABILITY, AVG_RESPONSE_TIME, AVG_COST, AVG_REQUEST_RATE
    };

    public enum OperationSLAMonitor {

        AVG_RELIABILITY, AVG_RESPONSE_TIME, AVG_COST, AVG_REQUEST_RATE
    };

    public enum ProcessSLAMonitor {

        AVG_RELIABILITY, AVG_RESPONSE_TIME, AVG_COST, AVG_REQUEST_RATE
    };

    public enum MosesModuleName {

        SLAManager, ProxyModule, OptimizationEngine, WSMonitor, CompositionManager, ServiceManager, QoSMonitor
    };
    public static final String BPEL_ENGINE = "BPELEngine";
    public static final String MOSES_NODE = "MosesNode";
    public static final String SWITCH_MODULE = "SwitchModule";

    public enum MosesModuleMonitor {

        RESPONSE_TIME
    };
}
