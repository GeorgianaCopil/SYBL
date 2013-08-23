package at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy;

import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricsCompositionRules")
public class CompositionRulesBlock {

    {
        compositionRules = new ArrayList<CompositionRule>();
    }

    @XmlAttribute(name = "TargetServiceElementLevel", required = true)
    private ServiceElement.ServiceElementLevel targetServiceElementLevel;

    @XmlElement(name = "TargetServiceElementID", required = true)
    private Collection<String> targetComponentsIDs;

    @XmlElement(name = "CompositionRule", required = true)
    private Collection<CompositionRule> compositionRules;

    public CompositionRulesBlock() {
        this.compositionRules = new ArrayList<CompositionRule>();
    }

    public ServiceElement.ServiceElementLevel getTargetServiceElementLevel() {
        return targetServiceElementLevel;
    }

    public void setTargetServiceElementLevel(ServiceElement.ServiceElementLevel targetServiceElementLevel) {
        this.targetServiceElementLevel = targetServiceElementLevel;
    }

    public Collection<String> getTargetServiceElementIDs() {
        return targetComponentsIDs;
    }

    public void setTargetComponentsIDs(Collection<String> targetComponentsIDs) {
        this.targetComponentsIDs = targetComponentsIDs;
    }

    //    public String getTargetComponentIP() {
//        return targetComponentIP;
//    }
//
//    public void setTargetComponentIP(String targetComponentIP) {
//        this.targetComponentIP = targetComponentIP;
//    }

    public Collection<CompositionRule> getCompositionRules() {
        return compositionRules;
    }

    public void setCompositionRules(Collection<CompositionRule> compositionRules) {
        this.compositionRules = compositionRules;
    }

     public void addCompositionRule(CompositionRule compositionRule) {
        this.compositionRules.add(compositionRule);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositionRulesBlock that = (CompositionRulesBlock) o;

        if (compositionRules != null ? !compositionRules.equals(that.compositionRules) : that.compositionRules != null)
            return false;
        if (targetComponentsIDs != null ? !targetComponentsIDs.equals(that.targetComponentsIDs) : that.targetComponentsIDs != null)
            return false;
        if (targetServiceElementLevel != that.targetServiceElementLevel) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetServiceElementLevel != null ? targetServiceElementLevel.hashCode() : 0;
        result = 31 * result + (targetComponentsIDs != null ? targetComponentsIDs.hashCode() : 0);
        result = 31 * result + (compositionRules != null ? compositionRules.hashCode() : 0);
        return result;
    }
}