package at.ac.tuwien.dsg.mela.analysis.report;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.*;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Collections.*;

/**
 * Author: Daniel Moldovan
 * Insitution: Vienna University of Technology
 * Date: 6/21/13
 * Time: 6:09 PM
 */
public class AnalysisReport {
    // stores requirement analysis information by LEVEL, then by ServiceElement. Service Element also stores hierarchical info
    //list of  AnalysisReportEntry as there is 1 report for all conditions for each target METRIC
    private Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, List<AnalysisReportEntry>>> analysisReport;


    public AnalysisReport(ServiceMonitoringSnapshot serviceMonitoringSnapshot, Requirements requirements) {

        analysisReport = new ConcurrentHashMap<ServiceElement.ServiceElementLevel, Map<ServiceElement, List<AnalysisReportEntry>>>(serviceMonitoringSnapshot.getMonitoredData().keySet().size());

        List<Requirement> requirementList = requirements.getRequirements();

        for (Requirement requirement : requirementList) {
            ServiceElement.ServiceElementLevel targetLevel = requirement.getTargetServiceElementLevel();
            Map<ServiceElement, ServiceElementMonitoringSnapshot> targetElements = serviceMonitoringSnapshot.getMonitoredData(targetLevel, requirement.getTargetServiceElementIDs());


            Metric targetMetric = requirement.getMetric();

            //for each element targeted by the restriction, find and evaluate the targeted metric.
            for (Map.Entry<ServiceElement, ServiceElementMonitoringSnapshot> entry : targetElements.entrySet()) {

                List<AnalysisReportEntry> analysis = Collections.synchronizedList(new ArrayList());

                //get the value of the targeted metric
                MetricValue targetMetricValue = entry.getValue().getValueForMetric(targetMetric);
                if (targetMetricValue == null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Metric " + targetMetric + "not found on " + entry.getKey());
                } else {
                    AnalysisReportEntry analysisReportEntry = new AnalysisReportEntry(targetMetric, targetMetricValue, requirement.getConditions(), entry.getKey());
                    analysis.add(analysisReportEntry);
                }

                if (analysisReport.containsKey(targetLevel)) {
                    Map<ServiceElement, List<AnalysisReportEntry>> map = analysisReport.get(targetLevel);
                    if (map.containsKey(entry.getKey())) {
                        map.get(entry.getKey()).addAll(analysis);
                    } else {
                        map.put(entry.getKey(), analysis);
                    }
                } else {
                    Map<ServiceElement, List<AnalysisReportEntry>> map = new ConcurrentHashMap<ServiceElement, List<AnalysisReportEntry>>();
                    map.put(entry.getKey(), analysis);
                    analysisReport.put(targetLevel, map);
                }
            }
        }

    }


    public Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, List<AnalysisReportEntry>>> getAnalysisReport() {
        return analysisReport;
    }

    @Override
    public String toString() {
        String description = "AnalysisReport{";
        //traverse in DFS the tree

        for (Map.Entry<ServiceElement.ServiceElementLevel, Map<ServiceElement,  List<AnalysisReportEntry>>> entry : analysisReport.entrySet()) {

            String space = "";
            switch (entry.getKey()) {
                case SERVICE:
                    space = "";
                    break;
                case SERVICE_TOPOLOGY:
                    space = "\t";
                    break;
                case SERVICE_UNIT:
                    space = "\t\t";
                    break;
                case VM:
                    space = "\t\t\t";
                    break;

            }

            for (Map.Entry<ServiceElement,  List<AnalysisReportEntry>> reportEntry : entry.getValue().entrySet()) {
                description += "\n" + space + reportEntry.getKey().getName() ;
                for(AnalysisReportEntry analysisReportEntry : reportEntry.getValue()){
                    description += "\n" + space + "\t"+ analysisReportEntry.getMetric() + " = " + analysisReportEntry.getMetricValue() + " unfulfilled " + analysisReportEntry.getUnfulfilledConditions() + " fulfilled " + analysisReportEntry.getFulfilledConditions();
                }
            }


        }

        return description;
    }

}
