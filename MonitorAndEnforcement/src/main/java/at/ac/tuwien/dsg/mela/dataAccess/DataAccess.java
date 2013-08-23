package at.ac.tuwien.dsg.mela.dataAccess;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricFilter;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceElementMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;
import at.ac.tuwien.dsg.mela.utils.Configuration;
import sun.applet.resources.MsgAppletViewer;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/21/13
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataAccess{

    protected Map<ServiceElement.ServiceElementLevel, List<MetricFilter>> metricFilters;

    {
        metricFilters = new LinkedHashMap<ServiceElement.ServiceElementLevel, List<MetricFilter>>();

        //add default filter to keep the ServiceElementIDMetric for all monitored VMs
        MetricFilter metricFilter = new MetricFilter();
        metricFilter.getMetrics().add(new Metric(Configuration.getServiceElementIDMetricName()));
        metricFilter.setLevel(ServiceElement.ServiceElementLevel.VM);
        addMetricFilter(metricFilter);
    }

    public void addMetricFilter(MetricFilter metricFilter) {
        if (metricFilters.containsKey(metricFilter.getLevel())) {
            List<MetricFilter> list = metricFilters.get(metricFilter.getLevel());
            if (!list.contains(metricFilter)) {
                list.add(metricFilter);
            }
        } else {
            List<MetricFilter> list = new ArrayList<MetricFilter>();
            list.add(metricFilter);
            metricFilters.put(metricFilter.getLevel(), list);
        }
    }

    public void addMetricFilters(Collection<MetricFilter> newFilters) {
        for (MetricFilter metricFilter : newFilters) {
            if (metricFilters.containsKey(metricFilter.getLevel())) {
                List<MetricFilter> list = metricFilters.get(metricFilter.getLevel());
                if (!list.contains(metricFilter)) {
                    list.add(metricFilter);
                }
            } else {
                List<MetricFilter> list = new ArrayList<MetricFilter>();
                list.add(metricFilter);
                metricFilters.put(metricFilter.getLevel(), list);
            }
        }
    }

    public void removeMetricFilter(MetricFilter metricFilter) {
        if (metricFilters.containsKey(metricFilter.getLevel())) {
            List<MetricFilter> list = metricFilters.get(metricFilter.getLevel());
            if (list.contains(metricFilter)) {
                list.remove(metricFilter);
            }
        }
    }


    public void removeMetricFilters(Collection<MetricFilter> filtersToRemove) {
        for (MetricFilter metricFilter : filtersToRemove) {
            if (metricFilters.containsKey(metricFilter.getLevel())) {
                List<MetricFilter> list = metricFilters.get(metricFilter.getLevel());
                if (list.contains(metricFilter)) {
                    list.remove(metricFilter);
                }
            }
        }
    }

    public Map<ServiceElement.ServiceElementLevel, List<MetricFilter>> getMetricFilters() {
        return metricFilters;
    }

    /**
     * @return a map containing as key the ID of the Service Element, and as value another map containing all the monitored metrics and their values for that particular  ServiceElement
     *         traverses the supplied tree and returns data about the monitored element and their children.
     */
    public abstract ServiceMonitoringSnapshot getMonitoredData(ServiceElement serviceElement);

    /**
     * @param serviceElement the ServiceElement for which to retrieve the data
     * @return all the monitored metrics and their values for that particular  ServiceElement
     *         Does not return data also about the element children
     */
    public abstract ServiceElementMonitoringSnapshot getSingleElementMonitoredData(ServiceElement serviceElement);


    /**
     * @param serviceElement the element for which the available monitored metrics is retrieved
     * @return
     */
    public abstract Collection<Metric> getAvailableMetricsForServiceElement(ServiceElement serviceElement);


}
