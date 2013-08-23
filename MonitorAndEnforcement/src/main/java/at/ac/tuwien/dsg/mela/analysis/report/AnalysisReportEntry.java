package at.ac.tuwien.dsg.mela.analysis.report;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Condition;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricValue;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import java.util.*;

/**
 * Author: Daniel Moldovan
 * Insitution: Vienna University of Technology
 * Date: 6/21/13
 * Time: 5:58 PM
 */
public class AnalysisReportEntry {
    private Metric metric;
    private MetricValue metricValue;
    //<fulfilled = TRUE || FALSE, condition>
    private Map<Boolean,List<Condition>> metricConditions;
    private ServiceElement serviceElement;

    {
        metricConditions = new LinkedHashMap<Boolean,List<Condition>>();
        metricConditions.put(false,new ArrayList<Condition>());
        metricConditions.put(true,new ArrayList<Condition>());
    }

    private AnalysisReportEntry() {
    }

    public AnalysisReportEntry(Metric metric, MetricValue metricValue, Collection<Condition> metricCondition, ServiceElement serviceElement) {
        this.metric = metric;
        this.metricValue = metricValue;
        for(Condition condition : metricCondition){
            metricConditions.get(condition.isRespectedByValue(metricValue)).add(condition);
        }
        this.serviceElement = serviceElement;
    }

    public Metric getMetric() {
        return metric;
    }

    public MetricValue getMetricValue() {
        return metricValue;
    }

    public Map<Boolean, List<Condition>> getMetricConditions() {
        return metricConditions;
    }

    public ServiceElement getServiceElement() {
        return serviceElement;
    }

    public List<Condition> getFulfilledConditions(){
        return  metricConditions.get(true);
    }

    public List<Condition> getUnfulfilledConditions(){
        return  metricConditions.get(false);
    }
}
