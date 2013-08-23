package at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement;

import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 2/2/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Requirement")
public class Requirement {

    @XmlAttribute(name = "ID")
    private String id;

    @XmlAttribute(name = "TargetServiceLevel")
    private ServiceElement.ServiceElementLevel targetServiceElementLevel;

    @XmlElement(name = "TargetServiceElementID")
    private List<String> targetServiceElementIDs;

    @XmlElement(name = "Condition")
    private List<Condition> conditions;

    @XmlElement(name = "TargetMetric", required = true)
    private Metric metric;


    {
        conditions = new ArrayList<Condition>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceElement.ServiceElementLevel getTargetServiceElementLevel() {
        return targetServiceElementLevel;
    }

    public void setTargetServiceElementLevel(ServiceElement.ServiceElementLevel targetServiceElementLevel) {
        this.targetServiceElementLevel = targetServiceElementLevel;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<String> getTargetServiceElementIDs() {
        return targetServiceElementIDs;
    }

    public void setTargetServiceElementIDs(List<String> targetServiceElementIDs) {
        this.targetServiceElementIDs = targetServiceElementIDs;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}
