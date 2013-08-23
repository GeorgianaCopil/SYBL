package at.ac.tuwien.dsg.mela.apis;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.*;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionOperationType;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesConfiguration;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;
import at.ac.tuwien.dsg.mela.utils.exceptions.ConfigurationException;

import java.util.Collection;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 */
public interface ConfigurationServiceAPI {

    public void submitServiceConfiguration(ServiceElement serviceElement) throws ConfigurationException;
    public void submitMetricCompositionRulesConfiguration(CompositionRulesConfiguration compositionRulesConfiguration) throws ConfigurationException;
    public void submitRequirementsConfiguration(Requirements requirements) throws ConfigurationException;

    public Collection<CompositionOperationType> getMetricCompositionOperations();
    public Collection<Condition.Type> getRequirementConditionTypes();

    public Collection<Metric> getAvailableMetricsForServiceElement(ServiceElement serviceElement) throws ConfigurationException;

    public void addMetricFilter(MetricFilter metricFilter) throws ConfigurationException;
    public void addMetricFilters(Collection<MetricFilter> metricFilters) throws ConfigurationException;
    public void removeMetricFilter(MetricFilter metricFilter) throws ConfigurationException;
    public void removeMetricFilters(Collection<MetricFilter> metricFilters) throws ConfigurationException;

}
