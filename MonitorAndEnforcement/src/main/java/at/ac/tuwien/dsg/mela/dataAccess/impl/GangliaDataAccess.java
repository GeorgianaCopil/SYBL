package at.ac.tuwien.dsg.mela.dataAccess.impl;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.*;
import at.ac.tuwien.dsg.mela.dataAccess.DataAccess;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;
import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaClusterInfo;
import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaHostInfo;
import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaMetricInfo;
import at.ac.tuwien.dsg.mela.utils.Configuration;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXParseException;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/21/13
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class GangliaDataAccess extends DataAccess {


    private GangliaDataSourceI gangliaDataSourceI;

    public GangliaDataAccess(GangliaDataSourceI gangliaDataSourceI) {
        this.gangliaDataSourceI = gangliaDataSourceI;
    }

    /**
     * @param serviceElement the root element of the Service Structure hierarchy
     * @return ServiceMonitoringSnapshot containing the monitored data organized both in tree and by level
     *         Searches in the Ganglia HOSTS monitoring for serviceElement ID, and if it finds such ID searches it in the supplied
     *         Service structure, after, adds the monitoring information as a sub-element ServiceElement of VM level to
     *         the element having the found ID
     */

    @Override
    public synchronized ServiceMonitoringSnapshot getMonitoredData(ServiceElement serviceElement) {

        ServiceElement structureRoot = serviceElement.clone();

        GangliaClusterInfo gangliaClusterInfo = null;
        ServiceMonitoringSnapshot serviceMonitoringSnapshot = new ServiceMonitoringSnapshot();

        try {
            gangliaClusterInfo = gangliaDataSourceI.getGangliaMonitoringInfo();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Terminating execution");
            System.exit(1);
        } catch (JAXBException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Terminating execution");
            System.exit(1);
        }

        /**
         * Linear representation of serviceElement hierarchical tree.
         */
        Map<ServiceElement, ServiceElement> elements = new LinkedHashMap<ServiceElement, ServiceElement>();


        //traverse the ServiceElement hierarchical tree in BFS and extract the service elements
        List<ServiceElement> bfsTraversalQueue = new ArrayList<ServiceElement>();
        bfsTraversalQueue.add(structureRoot);

        while (!bfsTraversalQueue.isEmpty()) {
            ServiceElement element = bfsTraversalQueue.remove(0);
            elements.put(element, element);
            bfsTraversalQueue.addAll(element.getContainedElements());

            //add empty monitoring data for each service element, to serve as a place where in the future composed metrics can be added
            {
                ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot(element, new LinkedHashMap<Metric, MetricValue>());
                serviceMonitoringSnapshot.addMonitoredData(serviceElementMonitoringSnapshot);
            }
        }


        //iterate trough the GangliaCluster, extract each VM monitoring data, build an ServiceElementMonitoringSnapshot from it and add it to the ServiceMonitoringSnapshot

        Collection<GangliaHostInfo> gangliaHostsInfo = gangliaClusterInfo.getHostsInfo();

        for (GangliaHostInfo gangliaHostInfo : gangliaHostsInfo) {
            Map<Metric, MetricValue> monitoredMetricValues = new LinkedHashMap<Metric, MetricValue>();
//            ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot();
            ServiceElement monitoredElement = null;
            //represent all monitored metrics in mapToElasticitySpace
            for (GangliaMetricInfo gangliaMetricInfo : gangliaHostInfo.getMetrics()) {
                Metric metric = new Metric();
                metric.setName(gangliaMetricInfo.getName());
                metric.setMeasurementUnit(gangliaMetricInfo.getUnits());
                MetricValue metricValue = new MetricValue(gangliaMetricInfo.getConvertedValue());
                monitoredMetricValues.put(metric, metricValue);
                if (metric.getName().equals(Configuration.getServiceElementIDMetricName())) {
                    monitoredElement = new ServiceElement();
                    monitoredElement.setId(gangliaMetricInfo.getValue());
                }
            }
            //if we have found a metric containing a serviceElementID, and if that ID is present in our structure
            //add it as VM level child to the found Service ID (this is the logic under our ganglia deployment so far)
            if (monitoredElement != null && elements.containsKey(monitoredElement)) {
                ServiceElement structureElement = elements.get(monitoredElement);
                ServiceElement vmLevelElement = new ServiceElement();
                vmLevelElement.setId(gangliaHostInfo.getIp());
                vmLevelElement.setName(gangliaHostInfo.getIp());
                vmLevelElement.setLevel(ServiceElement.ServiceElementLevel.VM);
                structureElement.addElement(vmLevelElement);

                ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot(vmLevelElement, monitoredMetricValues);
                serviceMonitoringSnapshot.addMonitoredData(serviceElementMonitoringSnapshot);
            }
        }

        // filter the monitoredMetricValues according to the metric filters if such exist
        // the filtering is done here after collections since I iterate trough all metrics above to find the ServiceElementID
        // which is later used to determine to which level I need to map the data
        // also i can use the code below when I get data at diff levels and I move from Ganglia
        serviceMonitoringSnapshot.applyMetricFilters(metricFilters);

        return serviceMonitoringSnapshot;

    }

    @Override
    public synchronized ServiceElementMonitoringSnapshot getSingleElementMonitoredData(ServiceElement serviceElement) {

        GangliaClusterInfo gangliaClusterInfo = null;
        try {
            gangliaClusterInfo = gangliaDataSourceI.getGangliaMonitoringInfo();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Terminating execution");
            System.exit(1);
        } catch (JAXBException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Terminating execution");
            System.exit(1);
        }

        Collection<GangliaHostInfo> gangliaHostsInfo = gangliaClusterInfo.getHostsInfo();
        for (GangliaHostInfo gangliaHostInfo : gangliaHostsInfo) {
            Map<Metric, MetricValue> monitoredMetricValues = new LinkedHashMap<Metric, MetricValue>();
//            ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot();
            ServiceElement monitoredElement = null;
            //represent all monitored metrics in mapToElasticitySpace
            for (GangliaMetricInfo gangliaMetricInfo : gangliaHostInfo.getMetrics()) {
                Metric metric = new Metric();
                metric.setName(gangliaMetricInfo.getName());
                metric.setMeasurementUnit(gangliaMetricInfo.getUnits());
                MetricValue metricValue = new MetricValue(gangliaMetricInfo.getConvertedValue());
                monitoredMetricValues.put(metric, metricValue);
                if (metric.getName().equals(Configuration.getServiceElementIDMetricName())) {
                    monitoredElement = new ServiceElement();
                    monitoredElement.setId(gangliaMetricInfo.getValue());
                }
            }
            //if we have found a metric containing a serviceElementID, and if that ID is present in our structure
            //add it as VM level child to the found Service ID (this is the logic under our ganglia deployment so far)
            if (monitoredElement != null && serviceElement.equals(monitoredElement)) {

                ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot(serviceElement, monitoredMetricValues);

                //filters are applied sequentially in cascade
                if(metricFilters.containsKey(serviceElement.getLevel())){
                    for(MetricFilter filter: metricFilters.get(serviceElement.getLevel())) {
                        //if either the filter applies on all elements at one particular level (targetIDs are null or empty) either the filter targets the service element ID
                       if(filter.getTargetServiceElementIDs() == null || filter.getTargetServiceElementIDs().size()==0 || filter.getTargetServiceElementIDs().contains(serviceElement.getId())){
                            serviceElementMonitoringSnapshot.keepMetrics(filter.getMetrics());
                        }
                    }
                }
                return serviceElementMonitoringSnapshot;

            }
        }
        return null;
    }


    @Override
    public Collection<Metric> getAvailableMetricsForServiceElement(ServiceElement serviceElement) {
        GangliaClusterInfo gangliaClusterInfo = null;
        try {
            gangliaClusterInfo = gangliaDataSourceI.getGangliaMonitoringInfo();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Terminating execution");
            System.exit(1);
        } catch (JAXBException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Terminating execution");
            System.exit(1);
        }

        Collection<GangliaHostInfo> gangliaHostsInfo = gangliaClusterInfo.getHostsInfo();
        for (GangliaHostInfo gangliaHostInfo : gangliaHostsInfo) {
            Map<Metric, MetricValue> monitoredMetricValues = new LinkedHashMap<Metric, MetricValue>();
//            ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = new ServiceElementMonitoringSnapshot();
            ServiceElement monitoredElement = null;
            //represent all monitored metrics in mapToElasticitySpace
            for (GangliaMetricInfo gangliaMetricInfo : gangliaHostInfo.getMetrics()) {
                Metric metric = new Metric();
                metric.setName(gangliaMetricInfo.getName());
                metric.setMeasurementUnit(gangliaMetricInfo.getUnits());
                MetricValue metricValue = new MetricValue(gangliaMetricInfo.getConvertedValue());
                monitoredMetricValues.put(metric, metricValue);
                if (metric.getName().equals(Configuration.getServiceElementIDMetricName())) {
                    monitoredElement = new ServiceElement();
                    monitoredElement.setId(gangliaMetricInfo.getValue());
                }
            }
            //if we have found a metric containing a serviceElementID, and if that ID is present in our structure
            //add it as VM level child to the found Service ID (this is the logic under our ganglia deployment so far)
            if (monitoredElement != null && serviceElement.equals(monitoredElement)) {
                return monitoredMetricValues.keySet();
            }
        }
        return new ArrayList<Metric>();
    }

}

