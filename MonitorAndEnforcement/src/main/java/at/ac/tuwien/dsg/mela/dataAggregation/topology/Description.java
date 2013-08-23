package at.ac.tuwien.dsg.mela.dataAggregation.topology;

import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Requirements;
import at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy.CompositionRulesConfiguration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DescriptionTemplate")
public class Description {
    @XmlElement(name = "TopologyTemplate", required = false)
    private TopologyTemplate topology;

    @XmlElement(name = "CompositionRulesConfiguration", required = false)
    private CompositionRulesConfiguration compositionRulesConfiguration;

    @XmlElement(name = "Requirements", required = false)
    private Requirements requirements;

    public Description() {
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public TopologyTemplate getTopology() {
        return topology;
    }

    public void setTopology(TopologyTemplate topology) {
        this.topology = topology;
    }

    public CompositionRulesConfiguration getCompositionRulesConfiguration() {
        return compositionRulesConfiguration;
    }

    public void setCompositionRulesConfiguration(CompositionRulesConfiguration compositionRulesConfiguration) {
        this.compositionRulesConfiguration = compositionRulesConfiguration;
    }
}
