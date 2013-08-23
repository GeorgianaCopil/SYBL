package at.ac.tuwien.dsg.mela.apis.impl;

import at.ac.tuwien.dsg.mela.apis.ConfigurationServiceAPI;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Condition;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricFilter;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Requirements;
import at.ac.tuwien.dsg.mela.control.SystemControl;
import at.ac.tuwien.dsg.mela.control.SystemControlFactory;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionOperationType;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesConfiguration;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;
import at.ac.tuwien.dsg.mela.utils.exceptions.ConfigurationException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 * Provides a centralized API (Facade actually) for the SystemControl
 */
public class ConfigurationServiceManager implements ConfigurationServiceAPI {

    private SystemControl systemControl;

    public ConfigurationServiceManager(SystemControlFactory systemControlFactory) {
        this.systemControl = systemControlFactory.getSystemControlInstance();
    }

    @Override
    public void submitServiceConfiguration(ServiceElement serviceElement) throws ConfigurationException {
       systemControl.setServiceConfiguration(serviceElement);
    }

    @Override
    public void submitMetricCompositionRulesConfiguration(CompositionRulesConfiguration compositionRulesConfiguration) throws ConfigurationException {
        systemControl.setCompositionRulesConfiguration(compositionRulesConfiguration);
    }

    @Override
    public void submitRequirementsConfiguration(Requirements requirements) throws ConfigurationException {
        systemControl.setRequirements(requirements);
    }

    @Override
    public Collection<CompositionOperationType> getMetricCompositionOperations() {
        Collection<CompositionOperationType> operationTypes = new ArrayList<CompositionOperationType>();
        operationTypes.add(CompositionOperationType.SUM);
        operationTypes.add(CompositionOperationType.MAX);
        operationTypes.add(CompositionOperationType.MIN);
        operationTypes.add(CompositionOperationType.AVG);
        operationTypes.add(CompositionOperationType.DIV);
        operationTypes.add(CompositionOperationType.ADD);
        operationTypes.add(CompositionOperationType.SUB);
        operationTypes.add(CompositionOperationType.MUL);
        operationTypes.add(CompositionOperationType.CONCAT);
        operationTypes.add(CompositionOperationType.UNION);
        operationTypes.add(CompositionOperationType.KEEP);
        operationTypes.add(CompositionOperationType.KEEP_LAST);
        operationTypes.add(CompositionOperationType.KEEP_FIRST);
        return operationTypes;
    }

    @Override
    public Collection<Condition.Type> getRequirementConditionTypes() {
        Collection<Condition.Type> operationTypes = new ArrayList<Condition.Type>();
        operationTypes.add(Condition.Type.LESS_THAN);
        operationTypes.add(Condition.Type.LESS_EQUAL);
        operationTypes.add(Condition.Type.GREATER_THAN);
        operationTypes.add(Condition.Type.GREATER_EQUAL);
        operationTypes.add(Condition.Type.EQUAL);
        operationTypes.add(Condition.Type.RANGE);
        operationTypes.add(Condition.Type.ENUMERATION);
        return operationTypes;
    }

    @Override
    public Collection<Metric> getAvailableMetricsForServiceElement(ServiceElement serviceElement) throws ConfigurationException {
        return systemControl.getDataAccess().getAvailableMetricsForServiceElement(serviceElement);
    }

    @Override
    public void addMetricFilters(Collection<MetricFilter> metricFilters) throws ConfigurationException {
        systemControl.getDataAccess().addMetricFilters(metricFilters);
    }

    @Override
    public void removeMetricFilters(Collection<MetricFilter> metricFilters) throws ConfigurationException {
        systemControl.getDataAccess().removeMetricFilters(metricFilters);
    }
    @Override
    public void addMetricFilter(MetricFilter metricFilter) throws ConfigurationException {
        systemControl.getDataAccess().addMetricFilter(metricFilter);
    }

    @Override
    public void removeMetricFilter(MetricFilter metricFilter) throws ConfigurationException {
        systemControl.getDataAccess().removeMetricFilter(metricFilter);
    }
}
