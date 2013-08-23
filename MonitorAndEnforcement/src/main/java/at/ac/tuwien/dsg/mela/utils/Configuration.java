/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.mela.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author daniel-tuwien
 */
public class Configuration {

    private static Properties configuration;

    static {
        configuration = new Properties();
        try {
        	InputStream is = Configuration.class.getClassLoader().getResourceAsStream("./data/config/Config.properties");

            configuration.load(is);
        } catch (Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getOpenStackRegion() {
        return "myregion";
    }

    public static String getApiType() {
        return "openstack-nova";
    }

    public static String getApiEndpoint() {
        return configuration.getProperty("OPEN_STACK_API_ENDPOINT");
    }

    public static String getCloudUser() {
        return configuration.getProperty("CLOUD_USER");
    }

    public static String getCloudPassword() {
        return configuration.getProperty("CLOUD_PASSWORD");
    }

    public static String getSecurityCertificatePath() {
        return configuration.getProperty("PEM_CERT_PATH");
    }

    public static String getGangliaPort() {
        return configuration.getProperty("GANGLIA_PORT");
    }

    public static String getAccessMachineIP() {
        return configuration.getProperty("ACCESS_MACHINE_IP");
    }

    public static String getTopologyFile() {
        return configuration.getProperty("TOPOLOGY_FILE");
    }

    public static String getVMMachinesNameRoot() {
        return configuration.getProperty("MACHINES_NAME_ROOT");
    }

    public static String getKeyPairName() {
        return configuration.getProperty("MACHINE_ACCESS_KEY_PAIR_NAME");
    }

    public static String getImageID() {
        return configuration.getProperty("DEFAULT_IMAGE_ID");
    }

    public static String getFlavorId() {
        return configuration.getProperty("DEFAULT_FLAVOR_ID");
    }

    public static String getControllerMachineName() {
        return configuration.getProperty("CONTROLLER_MACHINE_NAME");
    }

    public static String getCloudSpaceFolder() {
        return "./data/config/cloudDescription";
    }

    public static String getApplicationMonitoringFolder() {
        return configuration.getProperty("APPLICATION_MONITORING_FOLDER");
    }

    public static String getApplicationMonitoringLevel() {
        return configuration.getProperty("APPLICATION_MONITORING_LEVEL");
    }

    public static String getCassandraSeedIP() {
        return configuration.getProperty("CASSANDRA_SEED_IP");
    }

    public static String getD3_JSTemplateFile() {
        return configuration.getProperty("D3_JS_TEMPLATE_FILE");
    }

    public static int getMonitoringInterval() {
        String monitoringInterval = configuration.getProperty("MONITORING_INTERVAL_IN_SECONDS");
        if(monitoringInterval == null){
            return 1;
        }else{
            return Integer.parseInt(monitoringInterval);
        }

    }

    public static int getMonitoringAggregationNumber() {
        String monitoringInterval = configuration.getProperty("MONITORING_AGGREGATION_STEPS");
        if(monitoringInterval == null){
            return 1;
        }else{
            return Integer.parseInt(monitoringInterval);
        }
    }

    public static String getMonitoringCommand() {
        return configuration.getProperty("GANGLIA_MONITORING_COMMAND");
    }

    public static String getServiceElementIDMetricName() {
        return configuration.getProperty("SERVICE_ELEMENT_ID_METRIC_NAME");
    }
}
