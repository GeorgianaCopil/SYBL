
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import at.ac.tuwien.dsg.sybl.controlService.utils.Configuration;



/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SyblAPIService", targetNamespace = "http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/", wsdlLocation = "http://localhost:8080/AnalysisMonitoringEnforcementService-0.0.1/SyblAPIService?wsdl")
public class SyblAPIService_Service
    extends Service
{

    private final static URL SYBLAPISERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.SyblAPIService_Service.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.SyblAPIService_Service.class.getResource(".");
            url = new URL(baseUrl,Configuration.getMonitoringServiceURL());
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://localhost:8080/AnalysisMonitoringEnforcementService-0.0.1/SyblAPIService?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SYBLAPISERVICE_WSDL_LOCATION = url;
    }

    public SyblAPIService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SyblAPIService_Service() {
        super(SYBLAPISERVICE_WSDL_LOCATION, new QName("http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/", "SyblAPIService"));
    }

    /**
     * 
     * @return
     *     returns SyblAPIService
     */
    @WebEndpoint(name = "SyblAPIServicePort")
    public SyblAPIService getSyblAPIServicePort() {
        return super.getPort(new QName("http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/", "SyblAPIServicePort"), SyblAPIService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SyblAPIService
     */
    @WebEndpoint(name = "SyblAPIServicePort")
    public SyblAPIService getSyblAPIServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/", "SyblAPIServicePort"), SyblAPIService.class, features);
    }

}
