package at.ac.tuwien.dsg.mela.analysis;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricValue;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceElementMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionOperation;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRule;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesBlock;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesConfiguration;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Daniel Moldovan
 * Insitution: Vienna University of Technology
 * Date: 6/21/13
 * Time: 1:46 PM
 */
public class InstantMonitoringDataEnrichmentEngine {

    private List<ServiceElement.ServiceElementLevel> serviceLevelProcessingOrder;

    {
        serviceLevelProcessingOrder = new ArrayList<ServiceElement.ServiceElementLevel>();
        serviceLevelProcessingOrder.add(ServiceElement.ServiceElementLevel.VM);
        serviceLevelProcessingOrder.add(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
        serviceLevelProcessingOrder.add(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
        serviceLevelProcessingOrder.add(ServiceElement.ServiceElementLevel.SERVICE);
    }


    /**
     * @param compositionRulesConfiguration the metric composition rules to be applied on the service monitoring snapshot
     * @param serviceMonitoringSnapshot     simple service monitoring data
     * @return monitoring data enriched with composite metrics
     */
    public ServiceMonitoringSnapshot enrichMonitoringData(final CompositionRulesConfiguration compositionRulesConfiguration, final ServiceMonitoringSnapshot serviceMonitoringSnapshot) {
        Collection<CompositionRulesBlock> compositionRulesBlockCollection = compositionRulesConfiguration.getMetricCompositionRuleBlocks();

        //start applying composition from VM level upwards
        //for each level, apply all rules that target the level
        for (ServiceElement.ServiceElementLevel level : serviceLevelProcessingOrder) {

            //for each rule block, get target elements, and apply composition rules if the rules target this level
            for (final CompositionRulesBlock compositionRulesBlockBlock : compositionRulesBlockCollection) {
                if (compositionRulesBlockBlock.getTargetServiceElementLevel().equals(level)) {


                    final ServiceElement.ServiceElementLevel targetLevel = compositionRulesBlockBlock.getTargetServiceElementLevel();
                    final Collection<String> targetServiceElementIDs = compositionRulesBlockBlock.getTargetServiceElementIDs();

                    final Map<ServiceElement, ServiceElementMonitoringSnapshot> targetElements = serviceMonitoringSnapshot.getMonitoredData(targetLevel, targetServiceElementIDs);

                    List<Thread> elementCompositionThreads = new ArrayList<Thread>();

                    for (final ServiceElement targetElement : targetElements.keySet()) {
                        //for each composition rule extract metric source elements
//                Thread targetElementCompositionThread = new Thread() {
//                    @Override
//                    public void run() {
                        for (CompositionRule compositionRule : compositionRulesBlockBlock.getCompositionRules()) {

                            ServiceElement.ServiceElementLevel sourceLevel = compositionRule.getMetricSourceServiceElementLevel();
                            Collection<String> sourceServiceElementIDs = compositionRule.getMetricSourceServiceElementIDs();

                            //extract source metric values from CHILDREN of target ServiceElement
                            //get all service element metrics for the targeted level
                            Map<ServiceElement, ServiceElementMonitoringSnapshot> sourceLevelData = serviceMonitoringSnapshot.getMonitoredData(sourceLevel, sourceServiceElementIDs);

                            //get the children having that level because maybe the rule is for the entire level and Here i get the data only for 1 element
                            Map<ServiceElement, ServiceElementMonitoringSnapshot> metricSourceElements = new HashMap<ServiceElement, ServiceElementMonitoringSnapshot>();

                            //traverse the children in a BFS manner
                            {
                                List<ServiceElement> queue = new ArrayList<ServiceElement>();
                                queue.add(targetElement);
                                while (!queue.isEmpty()) {
                                    ServiceElement se = queue.remove(0);
                                    queue.addAll(se.getContainedElements());

                                    //if I find a child that is at the correct level (sourceLevelData), add it in the metricSourceElements
                                    if (sourceLevelData.containsKey(se)) {
                                        metricSourceElements.put(se, sourceLevelData.get(se));
                                    }
                                }
                            }


                            Metric targetMetric = compositionRule.getTargetMetric();

                            List<MetricValue> values = new ArrayList<MetricValue>();

                            //extract the values for the composed metric for each targeted ServiceElement
                            for (ServiceElementMonitoringSnapshot sourceSnapshot : metricSourceElements.values()) {
                                Map<Metric, MetricValue> sourceData = sourceSnapshot.getMonitoredData();

                                if (sourceData.containsKey(targetMetric)) {
                                    values.add(sourceData.get(targetMetric));
                                }

                            }

                            //go trough all composition operations for this rule and check if it references some metric instead of value
                            //if it does, then search the metric at the TARGET level and add the current metric value to the operation
                            Map<Metric, MetricValue> metricsForTargetElement = targetElements.get(targetElement).getMonitoredData();
                            for (CompositionOperation compositionOperation : compositionRule.getOperations()) {
                                String metricName = compositionOperation.getMetricName();

                                if (metricName != null) {
                                    Metric referencedMetric = new Metric(metricName);
                                    if (metricsForTargetElement.containsKey(referencedMetric)) {
                                        compositionOperation.setValue(metricsForTargetElement.get(referencedMetric).getValueRepresentation());
                                    }
                                }
                            }

                            if (values.size() > 0) {
                                MetricValue composedValue = compositionRule.apply(values);

                                //insert the new aggregation for the targeted unit
                                targetElements.get(targetElement).getMonitoredData().put(compositionRule.getResultingMetric(), composedValue);

                            } else {
                                //Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Target metric " + targetMetric + " not found in level " + sourceLevel + " in components " + sourceServiceElementIDs + " for element" + targetElement.getId());
                            }
                        }
                    }

//                };
//                elementCompositionThreads.add(targetElementCompositionThread);
//                targetElementCompositionThread.start();
//            }

                    for (Thread thread : elementCompositionThreads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }

            }
        }

        return serviceMonitoringSnapshot;

    }

    /**
     * NOTE: CURRENTLY only aggregates at VM level and the rules are supplied at Service Unit level
     * The VM level aggregation rules in time are applied. Then the instant data rules are applied to get higher level aggregated data
     *
     * @param instantDataCompositionRulesConfiguration
     *                                   rules to aggregate instant data
     * @param compositionRules           takes composition rules which specify for ServiceUNIT ID what rules to apply at the VM level.
     * @param serviceMonitoringSnapshots
     * @return
     */
    public ServiceMonitoringSnapshot aggregateMonitoringDataOverTime(final CompositionRulesConfiguration instantDataCompositionRulesConfiguration, final List<CompositionRulesBlock> compositionRules, final List<ServiceMonitoringSnapshot> serviceMonitoringSnapshots) {

        ServiceMonitoringSnapshot composedMonitoringSnapshot = new ServiceMonitoringSnapshot();


        Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, List<ServiceElementMonitoringSnapshot>>> dataToAggregate = new LinkedHashMap<ServiceElement.ServiceElementLevel, Map<ServiceElement, List<ServiceElementMonitoringSnapshot>>>();
        {
            dataToAggregate = new LinkedHashMap<ServiceElement.ServiceElementLevel, Map<ServiceElement, List<ServiceElementMonitoringSnapshot>>>();
            dataToAggregate.put(ServiceElement.ServiceElementLevel.VM, new LinkedHashMap<ServiceElement, List<ServiceElementMonitoringSnapshot>>());
            dataToAggregate.put(ServiceElement.ServiceElementLevel.SERVICE_UNIT, new LinkedHashMap<ServiceElement, List<ServiceElementMonitoringSnapshot>>());
            dataToAggregate.put(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY, new LinkedHashMap<ServiceElement, List<ServiceElementMonitoringSnapshot>>());
            dataToAggregate.put(ServiceElement.ServiceElementLevel.SERVICE, new LinkedHashMap<ServiceElement, List<ServiceElementMonitoringSnapshot>>());
        }


        //go trough supplied monitoring snapshots
        for (ServiceMonitoringSnapshot serviceMonitoringSnapshot : serviceMonitoringSnapshots) {
            Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>> monitoredData = serviceMonitoringSnapshot.getMonitoredData();
            //extract for each Level the monitored data
            for (ServiceElement.ServiceElementLevel level : monitoredData.keySet()) {
                Map<ServiceElement, ServiceElementMonitoringSnapshot> dataForLevel = monitoredData.get(level);
                Map<ServiceElement, List<ServiceElementMonitoringSnapshot>> dataToAggregateForLevel = dataToAggregate.get(level);

                //for each monitored ServiceElement at each level add monitoring data
                for (ServiceElement serviceElement : dataForLevel.keySet()) {
                    if (dataToAggregateForLevel.containsKey(serviceElement)) {
                        dataToAggregateForLevel.get(serviceElement).add(dataForLevel.get(serviceElement));
                    } else {
                        List<ServiceElementMonitoringSnapshot> snapshotList = new ArrayList<ServiceElementMonitoringSnapshot>();
                        snapshotList.add(dataForLevel.get(serviceElement));
                        dataToAggregateForLevel.put(serviceElement, snapshotList);
                    }
                }

            }

        }

        //filter elements that do not have the same number of monitoring snapshots. I.E. elements that disappeared are removed, ones which appeared are kept.
        for (Map<ServiceElement, List<ServiceElementMonitoringSnapshot>> datas : dataToAggregate.values()) {
            List<ServiceElement> elementsThatHaveDisappeared = new ArrayList<ServiceElement>();
            for(ServiceElement serviceElement: datas.keySet()){
                if(datas.get(serviceElement).size() < serviceMonitoringSnapshots.size()){
                    elementsThatHaveDisappeared.add(serviceElement);
                }
            }
            for(ServiceElement elementToRemove : elementsThatHaveDisappeared){
                datas.remove(elementToRemove);
            }
        }

        //add service structure in monitoring data
        for (ServiceElement.ServiceElementLevel level : dataToAggregate.keySet()) {
            for (ServiceElement serviceElement : dataToAggregate.get(level).keySet()) {
                ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot(serviceElement, new HashMap<Metric, MetricValue>());
                composedMonitoringSnapshot.addMonitoredData(serviceElementMonitoringSnapshot);
            }
        }

        //CURRENTLY only aggregates at VM level and the rules are supplied at Service Unit level
        for (CompositionRulesBlock compositionRulesBlock : compositionRules) {
            Collection<String> targetServiceUnits = compositionRulesBlock.getTargetServiceElementIDs();
            for (ServiceElement serviceElement : dataToAggregate.get(ServiceElement.ServiceElementLevel.SERVICE_UNIT).keySet()) {
                //if this rule block also targets this service unit
                if (targetServiceUnits == null || targetServiceUnits.size() == 0 || targetServiceUnits.contains(serviceElement.getId())) {
                    //extract service unit VM level children and aggregate their data
                    for (ServiceElement child : serviceElement.getContainedElements()) {
                        if (child.getLevel().equals(ServiceElement.ServiceElementLevel.VM)) {
                            //for each child apply aggregation rule
                            if (dataToAggregate.get(ServiceElement.ServiceElementLevel.VM).containsKey(child)) {
                                List<ServiceElementMonitoringSnapshot> childData = dataToAggregate.get(ServiceElement.ServiceElementLevel.VM).get(child);

                                //for each metric extract list of values to be aggregated
                                Map<Metric, List<MetricValue>> valuesForEachMetric = new HashMap<Metric, List<MetricValue>>();
                                for (ServiceElementMonitoringSnapshot childSnapshot : childData) {
                                    for (Map.Entry<Metric, MetricValue> entry : childSnapshot.getMonitoredData().entrySet()) {
                                        if (valuesForEachMetric.containsKey(entry.getKey())) {
                                            valuesForEachMetric.get(entry.getKey()).add(entry.getValue());
                                        } else {
                                            List<MetricValue> values = new ArrayList<MetricValue>();
                                            values.add(entry.getValue());
                                            valuesForEachMetric.put(entry.getKey(), values);
                                        }
                                    }
                                }
                                //apply aggregation rules
                                Map<Metric, MetricValue> compositeData = new HashMap<Metric, MetricValue>();
                                for (CompositionRule compositionRule : compositionRulesBlock.getCompositionRules()) {
                                    Metric targetMetric = compositionRule.getTargetMetric();
                                    if (valuesForEachMetric.containsKey(targetMetric)) {
                                        compositeData.put(targetMetric, compositionRule.apply(valuesForEachMetric.get(targetMetric)));
                                    }
                                }
                                //add aggregated data to the composed snapshot
                                ServiceElementMonitoringSnapshot childElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot(child, compositeData);
                                composedMonitoringSnapshot.addMonitoredData(childElementMonitoringSnapshot);

                            }

                        }
                    }
                }
            }
        }

        //enrich the composite data since the composite only composes VM level data, thus we need to aggregate it to get Service level data and etc
        return enrichMonitoringData(instantDataCompositionRulesConfiguration, composedMonitoringSnapshot);

    }


}

