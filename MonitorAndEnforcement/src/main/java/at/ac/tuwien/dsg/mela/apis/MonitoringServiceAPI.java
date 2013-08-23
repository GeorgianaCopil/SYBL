
package at.ac.tuwien.dsg.mela.apis;

import at.ac.tuwien.dsg.mela.analysis.report.AnalysisReport;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/22/13
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MonitoringServiceAPI {
    /**
     *
     * @return gets directly collected monitoring data
     */
    public ServiceMonitoringSnapshot getRawMonitoringData();

    /**
     *
     * @return monitoring data over which the composition rules have been applied
     */
    public ServiceMonitoringSnapshot getAggregatedMonitoringData();


    /**
     *
     * @return a report analyzing the service requirements over the aggregated monitoring data
     */
    public AnalysisReport getRequirementsAnalysisReport();

}
