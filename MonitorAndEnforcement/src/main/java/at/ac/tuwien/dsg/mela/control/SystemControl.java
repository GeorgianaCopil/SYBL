package at.ac.tuwien.dsg.mela.control;

import at.ac.tuwien.dsg.mela.analysis.InstantMonitoringDataAnalysisEngine;
import at.ac.tuwien.dsg.mela.analysis.InstantMonitoringDataEnrichmentEngine;
import at.ac.tuwien.dsg.mela.analysis.report.AnalysisReport;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.*;
import at.ac.tuwien.dsg.mela.dataAccess.DataAccess;
import at.ac.tuwien.dsg.mela.dataAccess.impl.GangliaDataAccess;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRule;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesBlock;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesConfiguration;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;
import at.ac.tuwien.dsg.mela.utils.exceptions.ConfigurationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 * Delegates the functionality of configuring MELA for instant monitoring and analysis
 */
public class SystemControl{

    private DataAccess dataAccess;
    private Requirements requirements;
    private CompositionRulesConfiguration compositionRulesConfiguration;
    private ServiceElement serviceConfiguration;
    private InstantMonitoringDataEnrichmentEngine instantMonitoringDataEnrichmentEngine;
    private InstantMonitoringDataAnalysisEngine instantMonitoringDataAnalysisEngine;

    protected SystemControl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        instantMonitoringDataEnrichmentEngine = new InstantMonitoringDataEnrichmentEngine();
        instantMonitoringDataAnalysisEngine = new InstantMonitoringDataAnalysisEngine();
    }

    private List<CompositionRulesBlock> historicalDataAggregationRules;

    {
        historicalDataAggregationRules = new ArrayList<CompositionRulesBlock>();
    }

    public ServiceElement getServiceConfiguration() {
        return serviceConfiguration;
    }

    public synchronized  void setServiceConfiguration(ServiceElement serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    public synchronized Requirements getRequirements() {
        return requirements;
    }

    public synchronized void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public synchronized CompositionRulesConfiguration getCompositionRulesConfiguration() {
        return compositionRulesConfiguration;
    }

    public synchronized void setCompositionRulesConfiguration(CompositionRulesConfiguration compositionRulesConfiguration) {
    	 for(CompositionRulesBlock compositionRulesBlock : compositionRulesConfiguration.getMetricCompositionRuleBlocks()){
             for(CompositionRule compositionRule : compositionRulesBlock.getCompositionRules()){
                 MetricFilter metricFilter = new MetricFilter();
                 metricFilter.setId(compositionRule.getResultingMetric().getName());
                 metricFilter.setLevel(compositionRule.getMetricSourceServiceElementLevel());
                 Collection<Metric> metrics = new ArrayList<Metric>();
                 metrics.add(new Metric(compositionRule.getTargetMetric().getName()));
                 metricFilter.setMetrics(metrics);
                 dataAccess.addMetricFilter(metricFilter);
             }
         }
         this.compositionRulesConfiguration = compositionRulesConfiguration;
    }

    public synchronized DataAccess getDataAccess() {
        return dataAccess;
    }

    public synchronized void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public synchronized ServiceMonitoringSnapshot getRawMonitoringData() {
        return dataAccess.getMonitoredData(serviceConfiguration);
    }


    public synchronized ServiceMonitoringSnapshot getAggregatedMonitoringDataOverTime(List<ServiceMonitoringSnapshot> serviceMonitoringSnapshots) {
        return instantMonitoringDataEnrichmentEngine.aggregateMonitoringDataOverTime( compositionRulesConfiguration, historicalDataAggregationRules, serviceMonitoringSnapshots);
    }


    public synchronized ServiceMonitoringSnapshot getAggregatedMonitoringData() {
        return instantMonitoringDataEnrichmentEngine.enrichMonitoringData(compositionRulesConfiguration,dataAccess.getMonitoredData(serviceConfiguration));
    }

    public synchronized AnalysisReport analyzeAggregatedMonitoringData() {
        return instantMonitoringDataAnalysisEngine.analyzeRequirements(instantMonitoringDataEnrichmentEngine.enrichMonitoringData(compositionRulesConfiguration,dataAccess.getMonitoredData(serviceConfiguration)),requirements);
    }

    public synchronized Collection<Metric> getAvailableMetricsForServiceElement(ServiceElement serviceElement) throws ConfigurationException{
       return dataAccess.getAvailableMetricsForServiceElement(serviceElement);
    }

    public synchronized void addMetricFilter(MetricFilter metricFilter) {
       dataAccess.addMetricFilter(metricFilter);
    }

    public synchronized void addMetricFilters(Collection<MetricFilter> newFilters) {
        dataAccess.addMetricFilters(newFilters);
    }

    public synchronized void removeMetricFilter(MetricFilter metricFilter) {
        dataAccess.removeMetricFilter(metricFilter);
    }


    public synchronized void removeMetricFilters(Collection<MetricFilter> filtersToRemove) {
        dataAccess.removeMetricFilters(filtersToRemove);
    }

    public synchronized void addHistoricalDataAggregationRule(CompositionRulesBlock compositionRulesBlock){
        this.historicalDataAggregationRules.add(compositionRulesBlock);
    }

    public synchronized void addHistoricalDataAggregationRules(List<CompositionRulesBlock> compositionRulesBlocks){
        this.historicalDataAggregationRules.addAll(compositionRulesBlocks);
    }

    public synchronized void removeHistoricalDataAggregationRule(CompositionRulesBlock compositionRulesBlock){
        this.historicalDataAggregationRules.remove(compositionRulesBlock);
    }

}
