/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.optimization_engine.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author francesco
 */
public abstract class NumericComputationTool {

    private static final String CONFIG_FILE = "optimization.properties";
    private static final String NUMERIC_COMPUTATION_CLASS = "numericComputationClass";
    protected static final String OPTIMIZATION_VARIANT = "optimizationVariant";
    protected static Properties properties = null;

    public enum ChangeType {

        SLA_CREATION, SLA_DELETION, SERVICE_CHANGE, PROCESS_CREATION, QOS_CHANGE
    };

    public static NumericComputationTool getInstance() throws FileNotFoundException, IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        FileInputStream fis = new FileInputStream(CONFIG_FILE);
        properties = new Properties();
        properties.load(fis);
        fis.close();

        return (NumericComputationTool) Class.forName(properties.getProperty(NUMERIC_COMPUTATION_CLASS)).newInstance();
    }

    public abstract void printInput(String processName, String processClass, ChangeType type, Double rate) throws Exception;


    public abstract boolean invoke(String processName) throws Exception;

    public abstract void parseAndSaveOutput(String processName, String requestId) throws Exception;
}
