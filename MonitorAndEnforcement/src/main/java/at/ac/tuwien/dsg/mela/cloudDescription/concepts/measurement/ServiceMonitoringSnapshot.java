package at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement;

import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/21/13
 * Time: 9:39 AM
 * Contains monitoring data structured by ServiceElementLevel and also hierarchically
 */
public class ServiceMonitoringSnapshot implements Serializable {

    // stores monitoring information by LEVEL, then by ServiceElement. Service Element also stores hierarchical info
    private Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>> monitoredData;

    {
        monitoredData = new LinkedHashMap<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>>();
    }

    public Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>> getMonitoredData() {
        return monitoredData;
    }

    public void setMonitoredData(Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>> monitoredData) {
        this.monitoredData = monitoredData;
    }

    public void addMonitoredData(ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot) {
        ServiceElement serviceElement = serviceElementMonitoringSnapshot.getServiceElement();
        ServiceElement.ServiceElementLevel level = serviceElement.getLevel();

        //if data contains level and if contains element, than just add metrics, otherwise put new metrics
        if (monitoredData.containsKey(level)) {
            if(monitoredData.get(level).containsKey(serviceElement)){
                monitoredData.get(level).get(serviceElement).getMonitoredData().putAll(serviceElementMonitoringSnapshot.getMonitoredData());
            }else{
                monitoredData.get(level).put(serviceElement, serviceElementMonitoringSnapshot);
            }
        } else {
            Map<ServiceElement, ServiceElementMonitoringSnapshot> map = new LinkedHashMap<ServiceElement, ServiceElementMonitoringSnapshot>();
            map.put(serviceElement, serviceElementMonitoringSnapshot);
            monitoredData.put(level, map);
        }

    }

    /**
     * @param level
     * @return the monitored snapshots and service element for the specified service level
     */
    public Map<ServiceElement, ServiceElementMonitoringSnapshot> getMonitoredData(ServiceElement.ServiceElementLevel level) {
        return monitoredData.get(level);
    }

    /**
     * @param level
     * @param serviceElementIDs
     * @return the monitored snapshots and service element for the specified service level and specified service elements IDs
     */
    public Map<ServiceElement, ServiceElementMonitoringSnapshot> getMonitoredData(ServiceElement.ServiceElementLevel level, Collection<String> serviceElementIDs) {
        if (!monitoredData.containsKey(level)) {
            return new LinkedHashMap<ServiceElement, ServiceElementMonitoringSnapshot>();
        }
        if (serviceElementIDs == null || serviceElementIDs.size() == 0) {
            return monitoredData.get(level);
        } else {
            Map<ServiceElement, ServiceElementMonitoringSnapshot> filtered = new LinkedHashMap<ServiceElement, ServiceElementMonitoringSnapshot>();

            for (Map.Entry<ServiceElement, ServiceElementMonitoringSnapshot> entry : monitoredData.get(level).entrySet()) {
                if (serviceElementIDs.contains(entry.getKey().getId())) {
                    filtered.put(entry.getKey(), entry.getValue());
                }
            }
            return filtered;
        }

    }

    public  ServiceElementMonitoringSnapshot getMonitoredData(ServiceElement serviceElement) {
        if (!monitoredData.containsKey(serviceElement.getLevel())) {
            return new ServiceElementMonitoringSnapshot(serviceElement,new LinkedHashMap<Metric, MetricValue>());
        }
            Map<ServiceElement, ServiceElementMonitoringSnapshot> filtered = new LinkedHashMap<ServiceElement, ServiceElementMonitoringSnapshot>();

            for (Map.Entry<ServiceElement, ServiceElementMonitoringSnapshot> entry : monitoredData.get(serviceElement.getLevel()).entrySet()) {
                if (serviceElement.equals(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return new ServiceElementMonitoringSnapshot(serviceElement,new LinkedHashMap<Metric, MetricValue>());
    }


    public Collection<ServiceElement> getMonitoredServiceElements(ServiceElement.ServiceElementLevel level) {
        return monitoredData.get(level).keySet();
    }

    public Collection<ServiceElement> getMonitoredServiceElements(ServiceElement.ServiceElementLevel level, Collection<String> serviceElementIDs) {
        if (!monitoredData.containsKey(level)) {
            return new ArrayList<ServiceElement>();
        }
        if (serviceElementIDs == null || serviceElementIDs.size() == 0) {
            return monitoredData.get(level).keySet();
        } else {
            Collection<ServiceElement> filtered = new ArrayList<ServiceElement>();

            for (Map.Entry<ServiceElement, ServiceElementMonitoringSnapshot> entry : monitoredData.get(level).entrySet()) {
                if (serviceElementIDs.contains(entry.getKey().getId())) {
                    filtered.add(entry.getKey());
                }
            }
            return filtered;
        }

    }


    public void applyMetricFilters(Map<ServiceElement.ServiceElementLevel, List<MetricFilter>> metricFilters) {


        for (ServiceElement.ServiceElementLevel level : monitoredData.keySet()) {
            if (metricFilters.containsKey(level)) {

                Map<ServiceElement, List<Metric>> metricsToKeep = new LinkedHashMap<ServiceElement, List<Metric>>();

                List<MetricFilter> filters = metricFilters.get(level);

                //make one pass over the filters to extract a common metric list from multiple filters for one service (ex filter for all Units and then keep VM)
                //for each filter
                for (MetricFilter filter : filters) {

                    //get elements targeted by filter
                    Map<ServiceElement, ServiceElementMonitoringSnapshot> targetElements = getMonitoredData(level, filter.getTargetServiceElementIDs());

                    //foreach element create a common list of metrics to keep
                    for (ServiceElement element : targetElements.keySet()) {
                        if (metricsToKeep.containsKey(element)){
                            metricsToKeep.get(element).addAll(filter.getMetrics());
                        }else{
                            List<Metric> metrics = new ArrayList<Metric>();
                            metrics.addAll(filter.getMetrics());
                            metricsToKeep.put(element,metrics);
                        }
                    }
                }

                for (ServiceElement element : monitoredData.get(level).keySet()) {
                    List<Metric> toKeep = metricsToKeep.get(element);
                    ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = monitoredData.get(level).get(element);
                    serviceElementMonitoringSnapshot.keepMetrics(toKeep);
                }
            }
        }

        //make second pass in which we apply the filters on the targeted elements
    }


    @Override
    public String toString() {
        String description = "ServiceMonitoringSnapshot{";
        //traverse in DFS the tree

        for (Map.Entry<ServiceElement, ServiceElementMonitoringSnapshot> entry : monitoredData.get(ServiceElement.ServiceElementLevel.SERVICE).entrySet()) {

            List<ServiceElement> stack = new ArrayList<ServiceElement>();
            stack.add(entry.getKey());
            while (!stack.isEmpty()) {
                ServiceElement currentElement = stack.remove(stack.size() - 1);
                stack.addAll(currentElement.getContainedElements());
                String space = "";
                switch (currentElement.getLevel()) {
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
                description += "\n" + space + currentElement.getLevel() + ": " + currentElement.getId() + " Metrics:" + monitoredData.get(currentElement.getLevel()).get(currentElement).getMonitoredData().size();
            }


        }

        return description;
    }
}
