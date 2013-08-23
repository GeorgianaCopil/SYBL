package at.ac.tuwien.dsg.mela.analysis;

import at.ac.tuwien.dsg.mela.analysis.report.AnalysisReport;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Requirements;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;

/**
 * Author: Daniel Moldovan
 * Institution Vienna University of Technology
 * Date: 6/21/13
 * Time: 5:23 PM
 */
public class InstantMonitoringDataAnalysisEngine {

    public AnalysisReport analyzeRequirements(ServiceMonitoringSnapshot serviceMonitoringSnapshot, Requirements requirements){
        return new AnalysisReport(serviceMonitoringSnapshot,requirements);
    }

}
