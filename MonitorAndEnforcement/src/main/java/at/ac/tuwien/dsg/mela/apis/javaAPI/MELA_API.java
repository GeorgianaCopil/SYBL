package at.ac.tuwien.dsg.mela.apis.javaAPI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricFilter;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricValue;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Requirement;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Requirements;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceElementMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.control.SystemControl;
import at.ac.tuwien.dsg.mela.control.SystemControlFactory;
import at.ac.tuwien.dsg.mela.dataAccess.DataAccess;
import at.ac.tuwien.dsg.mela.dataAccess.impl.GangliaDataAccess;
import at.ac.tuwien.dsg.mela.dataAccess.impl.GangliaDataSourceI;
import at.ac.tuwien.dsg.mela.dataAccess.impl.GangliaLiveDataSource;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionOperation;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionOperationType;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRule;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesBlock;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesConfiguration;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;
import at.ac.tuwien.dsg.mela.gui.ServiceJSONRepresentation;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPluginInterface.MonitoringInterface;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.RuntimeLogger;

import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 */
public class MELA_API implements MonitoringInterface {
    private SystemControl systemControl;
    private ServiceMonitoringSnapshot latestMonitoringData;
    private ServiceMonitoringSnapshot latestAggregatedMonitoringData;

    private boolean existsStructureData = false;
    private List<ServiceMonitoringSnapshot> historicalData;
    private Timer updateDataTimer;
    private double unitCost = 0.001;
    private CompositionRulesConfiguration compositionRulesConfiguration;
    private String actionName;
    private Node entity;
    private List<CompositionRulesBlock> historicalDataCompositionRules;
    //6 means 6 snapshots are aggregated. If the snapshot is collected every 5 seconds, means 30 seconds per aggregation
    private int aggregationCount = 4;

    {
        historicalData = new ArrayList<ServiceMonitoringSnapshot>();
        historicalDataCompositionRules = new ArrayList<>();
    }

    // creating aggregation rules
    {
        compositionRulesConfiguration = new CompositionRulesConfiguration();

        Collection<CompositionRulesBlock> compositionRulesBlockList = new ArrayList<CompositionRulesBlock>();

        compositionRulesConfiguration.setMetricCompositionRuleBlocks(compositionRulesBlockList);

        //rules for VM level
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            compositionRulesBlockList.add(compositionRulesBlock);

            compositionRulesBlock.setTargetServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
            Collection<CompositionRule> compositionRules = new ArrayList<CompositionRule>();

            compositionRulesBlock.setCompositionRules(compositionRules);

            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_free");
                Metric resultingMetric = new Metric("mem_free_in_GB");
                resultingMetric.setMeasurementUnit("GB");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //divide by 1024^3
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.DIV);
                    compositionOperation.setValue("1048576");
                    compositionOperations.add(compositionOperation);
                }

            }


            //mem_total convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_total");
                Metric resultingMetric = new Metric("mem_total_in_GB");
                compositionRule.setTargetMetric(targetMetric);
                resultingMetric.setMeasurementUnit("GB");
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //divide by 1024^3
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.DIV);
                    compositionOperation.setValue("1048576");
                    compositionOperations.add(compositionOperation);
                }

            }

//            //read_latency in ms
//            {
//                CompositionRule compositionRule = new CompositionRule();
//                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
//                Metric targetMetric =  new Metric("read_latency");
//                Metric resultingMetric =  new Metric("read_latency_ms");
//                compositionRule.setTargetMetric(targetMetric);
//                resultingMetric.setMeasurementUnit("ms");
//                compositionRule.setResultingMetric(resultingMetric);
//
//                compositionRules.add(compositionRule);
//
//                //creating the list of operations to be applied on the target metric
//                Collection<CompositionOperation> compositionOperations  = new ArrayList<CompositionOperation>();
//                compositionRule.setOperations(compositionOperations);
//
//                {
//                    CompositionOperation compositionOperation = new CompositionOperation();
//                    compositionOperation.setOperation(CompositionOperationType.MUL);
//                    compositionOperation.setValue("1000");
//                    compositionOperations.add(compositionOperation);
//                }
//
//            }
//            //write_latency in ms
//            {
//                CompositionRule compositionRule = new CompositionRule();
//                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
//                Metric targetMetric =  new Metric("write_latency");
//                Metric resultingMetric =  new Metric("write_latency_ms");
//                compositionRule.setTargetMetric(targetMetric);
//                resultingMetric.setMeasurementUnit("ms");
//                compositionRule.setResultingMetric(resultingMetric);
//
//                compositionRules.add(compositionRule);
//
//                //creating the list of operations to be applied on the target metric
//                Collection<CompositionOperation> compositionOperations  = new ArrayList<CompositionOperation>();
//                compositionRule.setOperations(compositionOperations);
//
//                {
//                    CompositionOperation compositionOperation = new CompositionOperation();
//                    compositionOperation.setOperation(CompositionOperationType.MUL);
//                    compositionOperation.setValue("1000");
//                    compositionOperations.add(compositionOperation);
//                }
//
//            }


            //MEM_used= -mem_free+mem_total
            //
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_free_in_GB");
                Metric resultingMetric = new Metric("mem_used");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //like subtracting from 100 just that it is inverse

                //multiply by -1
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.MUL);
                    compositionOperation.setValue("-1");
                    compositionOperations.add(compositionOperation);
                }

                //add 100
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.ADD);
                    compositionOperation.setValue("0");
                    compositionOperation.setReferenceMetricName("mem_total_in_GB");
                    compositionOperations.add(compositionOperation);
                }

            }


            //sum up pkts_in and pkts_out at VM level
            //done by setting TargetMetric as "pkyts_in" and operation referenceMetricName as "pkts_out"
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("pkts_in");
                Metric resultingMetric = new Metric("pkts_total");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.ADD);
                    compositionOperation.setValue("0");
                    compositionOperation.setReferenceMetricName("pkts_out");
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("serviceUnitID");
                Metric resultingMetric = new Metric("vmCount");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SET_VALUE);
                    compositionOperation.setValue("1");
                    compositionOperations.add(compositionOperation);
                }

            }

            //CPU usage from cpu_idle
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("cpu_idle");
                Metric resultingMetric = new Metric("cpu_usage");
                resultingMetric.setMeasurementUnit("%");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //like subtracting from 100 just that it is inverse

                //multiply by -1
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.MUL);
                    compositionOperation.setValue("-1");
                    compositionOperations.add(compositionOperation);
                }

                //add 100
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.ADD);
                    compositionOperation.setValue("100");
                    compositionOperations.add(compositionOperation);
                }

            }

        }

//
        //rules for SERVICE_UNIT level
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            compositionRulesBlockList.add(compositionRulesBlock);

            compositionRulesBlock.setTargetServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
            Collection<CompositionRule> compositionRules = new ArrayList<CompositionRule>();

            compositionRulesBlock.setCompositionRules(compositionRules);


            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_free_in_GB");
                Metric resultingMetric = new Metric("mem_free");
                resultingMetric.setMeasurementUnit("GB");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("throughput");
                Metric resultingMetric = new Metric("throughput_average");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("read_latency");
                Metric resultingMetric = new Metric("read_latency");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("activeConnections");
                Metric resultingMetric = new Metric("clientsNb");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.KEEP);
                    compositionOperations.add(compositionOperation);
                }

            }
            
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("write_latency");
                Metric resultingMetric = new Metric("write_latency");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("vmCount");
                Metric resultingMetric = new Metric("vmCount");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            //responseTime
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("responseTime");
                Metric resultingMetric = new Metric("responseTime");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            //throughput
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("throughput");
                Metric resultingMetric = new Metric("throughput");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //sum
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_free_in_GB");
                Metric resultingMetric = new Metric("mem_free");
                resultingMetric.setMeasurementUnit("GB");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            //cost per service unit
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("vmCount");
                Metric resultingMetric = new Metric("cost");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //SUM
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

                //multiply by 0.5
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.MUL);
                    compositionOperation.setValue(String.valueOf(unitCost));
                    compositionOperations.add(compositionOperation);
                }


            }

            //cost per hour 
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("cost");
                Metric resultingMetric = new Metric("costPerHour");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //multiply by -1
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.MUL);
                    compositionOperation.setValue("3600");
                    compositionOperations.add(compositionOperation);
                }

            }

            //cpu_idle
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("cpu_idle");
                Metric resultingMetric = new Metric("cpu_idle");
                resultingMetric.setMeasurementUnit("");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("disk_free");
                Metric resultingMetric = new Metric("disk_free");
                resultingMetric.setMeasurementUnit("");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("disk_total");
                Metric resultingMetric = new Metric("disk_total");
                resultingMetric.setMeasurementUnit("");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            //read_count
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("read_count");
                Metric resultingMetric = new Metric("read_count");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            //write_count
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("write_count");
                Metric resultingMetric = new Metric("write_count");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            //mem_total convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_total_in_GB");
                Metric resultingMetric = new Metric("mem_total");
                resultingMetric.setMeasurementUnit("GB");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            //mem_total convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_total_in_GB");
                Metric resultingMetric = new Metric("mem_total");
                resultingMetric.setMeasurementUnit("GB");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("pkts_total");
                Metric resultingMetric = new Metric("pkts_total");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("cpu_usage");
                Metric resultingMetric = new Metric("cpu_usage");
                resultingMetric.setMeasurementUnit("%");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.VM);
                Metric targetMetric = new Metric("mem_used");
                Metric resultingMetric = new Metric("mem_used");
                resultingMetric.setMeasurementUnit("%");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

        }
//
//
        //rules for SERVICE_TOPOLOGY level
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            compositionRulesBlockList.add(compositionRulesBlock);

            compositionRulesBlock.setTargetServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
            Collection<CompositionRule> compositionRules = new ArrayList<CompositionRule>();

            compositionRulesBlock.setCompositionRules(compositionRules);

            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("mem_free");
                Metric resultingMetric = new Metric("mem_free");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("clientsNb");
                Metric resultingMetric = new Metric("clientsNb");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.KEEP);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("throughput_average");
                Metric resultingMetric = new Metric("throughput_average");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            //responseTime 
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("responseTime");
                Metric resultingMetric = new Metric("responseTime");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            //throughput
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("throughput");
                Metric resultingMetric = new Metric("throughput");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //sum
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            //cost per hour 
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("costPerHour");
                Metric resultingMetric = new Metric("costPerHour");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add cost per service unit
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("mem_used");
                Metric resultingMetric = new Metric("mem_used");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);


                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("cost");
                Metric resultingMetric = new Metric("cost");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);


                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("cpu_idle");
                Metric resultingMetric = new Metric("cpu_idle");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {

                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("read_latency");
                Metric resultingMetric = new Metric("read_latency");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("write_latency");
                Metric resultingMetric = new Metric("write_latency");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("read_count");
                Metric resultingMetric = new Metric("read_count");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("write_count");
                Metric resultingMetric = new Metric("write_count");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            //mem_total convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("mem_total");
                Metric resultingMetric = new Metric("mem_total");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("pkts_total");
                Metric resultingMetric = new Metric("pkts_total");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("vmCount");
                Metric resultingMetric = new Metric("vmCount");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                Metric targetMetric = new Metric("cpu_usage");
                Metric resultingMetric = new Metric("cpu_usage");
                resultingMetric.setMeasurementUnit("%");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);


                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

        }


        //rules for SERVICE level
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            compositionRulesBlockList.add(compositionRulesBlock);

            compositionRulesBlock.setTargetServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE);
            Collection<CompositionRule> compositionRules = new ArrayList<CompositionRule>();

            compositionRulesBlock.setCompositionRules(compositionRules);

            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("mem_free");
                Metric resultingMetric = new Metric("mem_free");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("clientsNb");
                Metric resultingMetric = new Metric("clientsNb");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.KEEP);
                    compositionOperations.add(compositionOperation);
                }

            }
            
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("costPerHour");
                Metric resultingMetric = new Metric("costPerClientPerHour");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.DIV);
                    compositionOperation.setReferenceMetricName("clientsNb");
                    compositionOperations.add(compositionOperation);
                }

            }
            
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("throughput_average");
                Metric resultingMetric = new Metric("throughput_average");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("vmCount");
                Metric resultingMetric = new Metric("vmCount");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            //responseTime 
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("responseTime");
                Metric resultingMetric = new Metric("responseTime");
                resultingMetric.setMeasurementUnit("ms");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            //throughput
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("throughput");
                Metric resultingMetric = new Metric("throughput");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //sum
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            //costPerHour
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("costPerHour");
                Metric resultingMetric = new Metric("costPerHour");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //SUM
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("cost");
                Metric resultingMetric = new Metric("cost");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);


                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }
            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("read_latency");
                Metric resultingMetric = new Metric("read_latency");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("write_latency");
                Metric resultingMetric = new Metric("write_latency");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("read_count");
                Metric resultingMetric = new Metric("read_count");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }


            //memory_free convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("write_count");
                Metric resultingMetric = new Metric("write_count");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            //mem_total convert to GB from bytes
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("mem_total");
                Metric resultingMetric = new Metric("mem_total");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //average
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }


            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("pkts_total");
                Metric resultingMetric = new Metric("pkts_total");
                resultingMetric.setMeasurementUnit("no");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                //add the value of pkts_out metric
                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.SUM);
                    compositionOperations.add(compositionOperation);
                }

            }

            
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setMetricSourceServiceElementLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);
                Metric targetMetric = new Metric("cpu_usage");
                Metric resultingMetric = new Metric("cpu_usage");
                resultingMetric.setMeasurementUnit("%");
                compositionRule.setTargetMetric(targetMetric);
                compositionRule.setResultingMetric(resultingMetric);

                compositionRules.add(compositionRule);

                //creating the list of operations to be applied on the target metric
                Collection<CompositionOperation> compositionOperations = new ArrayList<CompositionOperation>();
                compositionRule.setOperations(compositionOperations);

                {
                    CompositionOperation compositionOperation = new CompositionOperation();
                    compositionOperation.setOperation(CompositionOperationType.AVG);
                    compositionOperations.add(compositionOperation);
                }

            }

        }


    }

    //historical RAW data aggregation rules
    {
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("cpu_idle"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.AVG);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }

        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("mem_total"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.AVG);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("mem_free"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.AVG);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }


        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("pkts_in"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.SUM);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }

        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("pkts_in"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.SUM);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }

        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("serviceUnitID"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.KEEP);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }
        
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("read_latency"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.AVG);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }
        
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("serviceUnitID"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.KEEP);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("responseTime"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.AVG);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }
        
        {
            CompositionRulesBlock compositionRulesBlock = new CompositionRulesBlock();
            Collection<String> targetIDs = new ArrayList<String>();

            compositionRulesBlock.setTargetComponentsIDs(targetIDs);
            {
                CompositionRule compositionRule = new CompositionRule();
                compositionRule.setTargetMetric(new Metric("throughput"));
                {
                    CompositionOperation operation = new CompositionOperation();
                    operation.setOperation(CompositionOperationType.AVG);
                    compositionRule.addOperation(operation);
                }
                compositionRulesBlock.addCompositionRule(compositionRule);
            }
            historicalDataCompositionRules.add(compositionRulesBlock);
        }
        
    }
    
    


    {
//        GangliaDataSourceI gangliaDataSourceI = new GangliaFileDataSource("/home/daniel-tuwien/Documents/DSG_SVN/ElasticitySpaceAPI/data/monitoring/monitoringFri_Jun_21_16_16_17_CEST_2013");
        GangliaDataSourceI gangliaDataSourceI = new GangliaLiveDataSource();
        DataAccess dataAccess = new GangliaDataAccess(gangliaDataSourceI);
        SystemControlFactory systemControlFactory = new SystemControlFactory(dataAccess);
        systemControl = systemControlFactory.getSystemControlInstance();
//        try {
//            JAXBContext context = JAXBContext.newInstance(CompositionRulesConfiguration.class);
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//            marshaller.marshal(compositionRulesConfiguration,new File("C:\\Users\\Georgiana\\experiment\\compositionRules.xml"));
//           System.exit(1);
//        } catch (JAXBException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        systemControl.setCompositionRulesConfiguration(compositionRulesConfiguration);
        systemControl.addHistoricalDataAggregationRules(historicalDataCompositionRules);
        updateDataTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ServiceMonitoringSnapshot monitoringData = systemControl.getAggregatedMonitoringData();
                    if (monitoringData != null){
                        latestMonitoringData = systemControl.getAggregatedMonitoringData();
	                    historicalData.add(systemControl.getRawMonitoringData());
	                    if (historicalData.size() > aggregationCount) {
	                        historicalData.remove(0);
	                    }
                    }
                    latestAggregatedMonitoringData = systemControl.getAggregatedMonitoringDataOverTime(historicalData);
                } catch (Exception e) {
                    RuntimeLogger.logger.error("In MELA_API, actionPerformed method of the update timer" + e.toString());
                }

            }
        });

        //create metric filter that leaves everything in


    }
    public MELA_API(){
    	
    }
    private List<MetricFilter> getMetricFilters() {
        List<MetricFilter> filters = new ArrayList<MetricFilter>();
        MetricFilter metricFilter = new MetricFilter();
        metricFilter.setId("VMLevelCassandra");
        Metric m;
        metricFilter.setLevel(ServiceElement.ServiceElementLevel.VM);
        Collection<Metric> metrics = new ArrayList<Metric>();
        metrics.add(new Metric("cpu_usage"));
        metrics.add(new Metric("mem_used"));
        metrics.add(new Metric("pkts_total"));

        metricFilter.setMetrics(metrics);
        filters.add(metricFilter);


        metricFilter = new MetricFilter();

        metricFilter.setId("SERVICE_UNITLevel");
        metricFilter.setLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);

        metrics = new ArrayList<Metric>();
        m = new Metric("cpu_usage");
        metrics.add(m);
        m = new Metric("read_latency");
        metrics.add(m);
        m = new Metric("costPerHour");
        metrics.add(m);
        m = new Metric("write_latency");
        metrics.add(m);
        m = new Metric("responseTime");
        metrics.add(m);
        m = new Metric("throughput");
        metrics.add(m);
        m = new Metric("throughput_average");
        metrics.add(m);
        m = new Metric("clientsNb");
        metrics.add(m);
        metricFilter.setMetrics(metrics);


        filters.add(metricFilter);
        metricFilter = new MetricFilter();
        metricFilter.setId("SERVICE_TOPOLOGYLevel");
        metricFilter.setLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);

        metrics = new ArrayList<Metric>();
        m = new Metric("costPerHour");
        metrics.add(m);
        m = new Metric("responseTime");
        metrics.add(m);
        m = new Metric("clientsNb");
        metrics.add(m);
        metricFilter.setMetrics(metrics);

        filters.add(metricFilter);
        metricFilter = new MetricFilter();
        metricFilter.setId("SERVICELevel");
        metricFilter.setLevel(ServiceElement.ServiceElementLevel.SERVICE);
        metrics = new ArrayList<Metric>();
        metrics.add(new Metric("cpu_usage"));
        m = new Metric("clientsNb");
        metrics.add(m);
        m = new Metric("costPerHour");
        
        metrics.add(m);

        
        m = new Metric("costPerClientPerHour");
        metrics.add(m);
        metricFilter.setMetrics(metrics);

        filters.add(metricFilter);
        return filters;

    }

    public void submitServiceConfiguration(Node cloudService) {
        ServiceElement element = new ServiceElement();
        element.setId(cloudService.getId());
        element.setLevel(ServiceElement.ServiceElementLevel.SERVICE);

        MELA_UTILS.convertServiceTopology(element, cloudService);

        systemControl.setServiceConfiguration(element);

        latestMonitoringData = systemControl.getAggregatedMonitoringData();
        historicalData.add(systemControl.getRawMonitoringData());
        if (historicalData.size() > aggregationCount) {
            historicalData.remove(0);
        }
        latestAggregatedMonitoringData = systemControl.getAggregatedMonitoringDataOverTime(historicalData);
        updateDataTimer.start();
        existsStructureData = true;
    }


    public float getCpuUsage(Node entity) {
        Metric metric = new Metric("cpu_usage");
        RuntimeLogger.logger.info("For entity "+entity.getId()+" Cpu usage is "+getMetricValue(metric,entity)+"cpu idle is" + getMetricValue(new Metric("cpu_idle"), entity));
        return getMetricValue(metric, entity);
    }

    public float getMemoryAvailable(Node entity) {
        Metric metric = new Metric("mem_free_in_GB");
        return getMetricValue(metric, entity);
    }

    public float getMemorySize(Node entity) {
        Metric metric = new Metric("mem_total_in_GB");
        return getMetricValue(metric, entity);
    }

    public float getMemoryUsage(Node entity) {
        Metric metric = new Metric("mem_used");
        return getMetricValue(metric, entity);
    }

    public float getDiskSize(Node entity) {
        Metric metric = new Metric("disk_total");
        return getMetricValue(metric, entity);
    }

    public float getDiskAvailable(Node entity) {
        Metric metric = new Metric("disk_free");
        return getMetricValue(metric, entity);
    }

    //TODO: define agg rule in procentaj
    public float getDiskUsage(Node entity) {

        return (getDiskSize(entity) - getDiskAvailable(entity)) / getDiskSize(entity) * 100;
    }

    public float getCPUSpeed(Node entity) {

        Metric metric = new Metric("cpu_speed");
        return getMetricValue(metric, entity);
    }

    //TODO: define agg rule in TOTAL
    public float getPkts(Node entity) {
        Metric metric = new Metric("pkts_total");
        return getMetricValue(metric, entity);
    }

    public float getPktsIn(Node entity) {
        Metric metric = new Metric("pkts_in");
        return getMetricValue(metric, entity);
    }

    public float getPktsOut(Node entity) {
        Metric metric = new Metric("pkts_out");
        return getMetricValue(metric, entity);
    }

    public float getReadLatency(Node entity) {
        Metric metric = new Metric("read_latency");
        return getMetricValue(metric, entity);
    }

    public float getWriteLatency(Node entity) {
        Metric metric = new Metric("write_latency");
        return getMetricValue(metric, entity);
    }

    public float getReadCount(Node entity) {
        Metric metric = new Metric("read_count");
        return getMetricValue(metric, entity);
    }

    public float getCostPerHour(Node entity) {
        Metric metric = new Metric("costPerHour");
        return getMetricValue(metric, entity);
    }

    public float getWriteCount(Node entity) {
        Metric metric = new Metric("write_count");
        return getMetricValue(metric, entity);
    }

    //TODO: can;t be done currentlu
    public float getTotalCostSoFar(Node entity) {
        Metric metric = new Metric("costPerHour");
        return getMetricValue(metric, entity);
    }

    /**
     * @return currently, all metrics for the first VM that is monitored. While MELA can return metrics for different VMs belonging to different service units,
     *         it would require a Service_unit_id to be added as parameter to this call
     */
    public List<String> getAvailableMetrics() {
        ServiceMonitoringSnapshot monitoringSnapshot = systemControl.getRawMonitoringData();
        Map<ServiceElement, ServiceElementMonitoringSnapshot> monitoringData = monitoringSnapshot.getMonitoredData(ServiceElement.ServiceElementLevel.VM);

        Collection<Metric> metrics = monitoringData.values().iterator().next().getMonitoredData().keySet();
        List<String> strings = new ArrayList<String>();
        for (Metric metric : metrics) {
            strings.add(metric.getName());
        }
        return strings;
    }

    //should at least have an action name in name
    public void notifyControlActionStarted(String actionName, Node entity) {
        this.actionName = actionName;
        this.entity = entity;
    }

    public void notifyControlActionEnded(String actionName, Node actionTargetEntity) {
        this.actionName = null;
        this.entity = null;
    }

    public float getMetricValue(String metricName, Node entity) {
        Metric metric = new Metric(metricName);
        
        return getMetricValue(metric, entity);
    }

    public String getLatestMonitoringData() {
        if (existsStructureData)
            return ServiceJSONRepresentation.describeInJSON(systemControl.getAggregatedMonitoringData(), getMetricFilters(), actionName, entity);
        else
        {
        	RuntimeLogger.logger.error("We do not have yet monitoring data! ");
        	return null;
        }
    }


    public float getMetricValue(Metric metric, Node entity) {
        //get the Entity level so I can search it in the monitored snapshot easily
        ServiceElement.ServiceElementLevel level = MELA_UTILS.getElementLevelFromEntity(entity);
        ServiceElement element = new ServiceElement();
        element.setId(entity.getId());
        element.setLevel(level);
        
        //search in the aggregated data over time for the target entity
        ServiceElementMonitoringSnapshot monitoringSnapshot = latestAggregatedMonitoringData.getMonitoredData(element);

        //metric we are searching for
        MetricValue value = monitoringSnapshot.getValueForMetric(metric);
        	
        if (value != null) {
            return Float.parseFloat(value.getValueRepresentation());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, metric + " not found for entity " + entity.getId());
            return -1;
        }
    }


    private static class MELA_UTILS {

        //works as side effect
        public static void convertServiceTopology(ServiceElement serviceElement, Node cloudService) {
        	//RuntimeLogger.logger.info("Related nodes for node "+ cloudService +" are "+ cloudService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP));
        	Node serviceTopology = cloudService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP).get(0);
            ServiceElement serviceTopologyElement = new ServiceElement();
            serviceTopologyElement.setId(serviceTopology.getId());
            serviceTopologyElement.setLevel(ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY);

            if (serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
               
            	for (Node serviceUnit : serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
                    if (serviceUnit.getNodeType()==NodeType.SERVICE_UNIT){
            		ServiceElement serviceUnitElement = new ServiceElement();
                    serviceUnitElement.setId(serviceUnit.getId());
                    serviceUnitElement.setLevel(ServiceElement.ServiceElementLevel.SERVICE_UNIT);
                    serviceTopologyElement.addElement(serviceUnitElement);
                    }
                }
            }

            serviceElement.addElement(serviceTopologyElement);

            if (serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
                for (Node subTopology : serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
                    if (subTopology.getNodeType()==NodeType.SERVICE_TOPOLOGY){
                    convertServiceTopology(serviceTopologyElement, subTopology);
                    }
                }
            }

        }

        public static ServiceElement.ServiceElementLevel getElementLevelFromEntity(Node entity) {
            if (entity.getNodeType()==NodeType.CLOUD_SERVICE) {
                return ServiceElement.ServiceElementLevel.SERVICE;
            } else if (entity.getNodeType()==NodeType.SERVICE_TOPOLOGY) {
                return ServiceElement.ServiceElementLevel.SERVICE_TOPOLOGY;
            } else if (entity.getNodeType()==NodeType.SERVICE_UNIT) {
                return ServiceElement.ServiceElementLevel.SERVICE_UNIT;
            } else {
                throw new UncheckedExecutionException(new Throwable("Error. Cannot determine the source class of entity " + entity));
            }
        }
    }


    @Override
    public void submitElasticityRequirements(
            ArrayList<ElasticityRequirement> description) {
        Requirements requirements = new Requirements();
        requirements.setRequirements(new ArrayList<Requirement>());


    }

    @Override
    public float getNumberInstances(Node entity) {
        Metric metric = new Metric("vmCount");
        return getMetricValue(metric, entity);
    }


}
