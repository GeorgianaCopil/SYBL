package at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompositionRulesConfiguration")
public class CompositionRulesConfiguration {

    @XmlAttribute(name = "TargetServiceID", required = true)
    private String targetServiceID;

    public String getTargetServiceID() {
        return targetServiceID;
    }

    public void setTargetServiceID(String targetServiceID) {
        this.targetServiceID = targetServiceID;
    }

    @XmlElement(name = "MetricsCompositionRules", required = false)
    private Collection<CompositionRulesBlock> metricCompositionRuleBlocks;

    @XmlElement(name = "HistoricalMetricDataAggregationPolicies", required = false)
    private Collection<CompositionRulesBlock> historicDataAggregationPolicies;

    public CompositionRulesConfiguration() {
        this.metricCompositionRuleBlocks = new ArrayList<CompositionRulesBlock>();
        this.historicDataAggregationPolicies = new ArrayList<CompositionRulesBlock>();
    }

    public Collection<CompositionRulesBlock> getMetricCompositionRuleBlocks() {
        return metricCompositionRuleBlocks;
    }

    public void setMetricCompositionRuleBlocks(Collection<CompositionRulesBlock> metricCompositionRuleBlocks) {
        this.metricCompositionRuleBlocks = metricCompositionRuleBlocks;
    }

    public Collection<CompositionRulesBlock> getHistoricDataAggregationPolicies() {
        return historicDataAggregationPolicies;
    }

    public void setHistoricDataAggregationPolicies(Collection<CompositionRulesBlock> historicDataAggregationPolicies) {
        this.historicDataAggregationPolicies = historicDataAggregationPolicies;
    }
}
