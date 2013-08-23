
package at.ac.tuwien.dsg.mela.apis.impl;

import at.ac.tuwien.dsg.mela.analysis.report.AnalysisReport;
import at.ac.tuwien.dsg.mela.apis.MonitoringServiceAPI;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.control.SystemControl;
import at.ac.tuwien.dsg.mela.control.SystemControlFactory;


/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 * Each API method operates on fresh monitoring data. A sequence of getRawMonitoringData() and a getAggregatedMonitoringData() calls
 * will operate on potentially different monitoring data
 */
public class MonitoringServiceManager implements MonitoringServiceAPI {

    private SystemControl systemControl;

    public MonitoringServiceManager(SystemControlFactory systemControlFactory) {
        this.systemControl = systemControlFactory.getSystemControlInstance();
    }

    @Override
    public ServiceMonitoringSnapshot getRawMonitoringData() {
        return systemControl.getRawMonitoringData();
    }

    @Override
    public ServiceMonitoringSnapshot getAggregatedMonitoringData() {
        return systemControl.getAggregatedMonitoringData();
    }

    @Override
    public AnalysisReport getRequirementsAnalysisReport() {
        return systemControl.analyzeAggregatedMonitoringData();
    }
}
