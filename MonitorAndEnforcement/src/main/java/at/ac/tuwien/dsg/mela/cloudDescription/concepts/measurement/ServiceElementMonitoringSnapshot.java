package at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement;

import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/21/13
 * Time: 9:39 AM
 * Contains a ServiceElement ID and associated metrics. Used in the ServiceMonitoringSnapshotṡ
 */
public class ServiceElementMonitoringSnapshot {

    private ServiceElement serviceElement;
    private Map<Metric, MetricValue> monitoredData;

    {
        monitoredData = new LinkedHashMap<Metric, MetricValue>();
    }

    public ServiceElementMonitoringSnapshot(ServiceElement serviceElement, Map<Metric, MetricValue> monitoredData) {
        this.serviceElement = serviceElement;
        this.monitoredData = monitoredData;
    }



    public Map<Metric, MetricValue> getMonitoredData() {
        return monitoredData;
    }


    public ServiceElement getServiceElement() {
        return serviceElement;
    }

    public MetricValue getValueForMetric(Metric metric) {
        return monitoredData.get(metric);
    }


    public void keepMetrics(Collection<Metric> metrics) {
        Map<Metric, MetricValue> filteredMonitoredData = new LinkedHashMap<Metric, MetricValue>();
        for (Metric metric : metrics) {
            //* means keep all metrics
            if (metric.getName().equals("*")) {
                filteredMonitoredData = monitoredData;
                break;
            } else if (monitoredData.containsKey(metric)) {
                filteredMonitoredData.put(metric, monitoredData.get(metric));
            } else {
//               Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"Metric " + metric + " not found for " + serviceElement + " in filtering process");
            }
        }
        monitoredData = filteredMonitoredData;
    }
}
