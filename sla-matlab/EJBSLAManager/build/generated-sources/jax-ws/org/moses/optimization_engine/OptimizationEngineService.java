
package org.moses.optimization_engine;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3.3-hudson-757-SNAPSHOT
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "OptimizationEngineService", targetNamespace = "http://optimization_engine.moses.org/", wsdlLocation = "file:/root/ProgettiMoses2/withQos/EJBOptimizationEngine/src/java/META-INF/OptimizationEngineService.wsdl")
public class OptimizationEngineService
    extends Service
{

    private final static URL OPTIMIZATIONENGINESERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.moses.optimization_engine.OptimizationEngineService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = org.moses.optimization_engine.OptimizationEngineService.class.getResource(".");
            url = new URL(baseUrl, "file:/root/ProgettiMoses2/withQos/EJBOptimizationEngine/src/java/META-INF/OptimizationEngineService.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/root/ProgettiMoses2/withQos/EJBOptimizationEngine/src/java/META-INF/OptimizationEngineService.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        OPTIMIZATIONENGINESERVICE_WSDL_LOCATION = url;
    }

    public OptimizationEngineService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public OptimizationEngineService() {
        super(OPTIMIZATIONENGINESERVICE_WSDL_LOCATION, new QName("http://optimization_engine.moses.org/", "OptimizationEngineService"));
    }

    /**
     * 
     * @return
     *     returns OptimizationEngine
     */
    @WebEndpoint(name = "OptimizationEnginePort")
    public OptimizationEngine getOptimizationEnginePort() {
        return super.getPort(new QName("http://optimization_engine.moses.org/", "OptimizationEnginePort"), OptimizationEngine.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns OptimizationEngine
     */
    @WebEndpoint(name = "OptimizationEnginePort")
    public OptimizationEngine getOptimizationEnginePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://optimization_engine.moses.org/", "OptimizationEnginePort"), OptimizationEngine.class, features);
    }

}
