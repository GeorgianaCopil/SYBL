package at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement;

import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 * FILTERS will be applied SEQUENTIALLY
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricFilter")
public class MetricFilter  {
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "TargetServiceElementLevel", required = true)
    private ServiceElement.ServiceElementLevel level;

    @XmlElement(name = "TargetServiceElementIDs", required = false)
    private Collection<String> targetServiceElementIDs;

    @XmlElement(name = "MetricToMonitor", required = false)
    private Collection<Metric> metrics;

    {
        metrics = new ArrayList<Metric>();
        targetServiceElementIDs = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceElement.ServiceElementLevel getLevel() {
        return level;
    }

    public void setLevel(ServiceElement.ServiceElementLevel level) {
        this.level = level;
    }

    public Collection<String> getTargetServiceElementIDs() {
        return targetServiceElementIDs;
    }

    public void setTargetServiceElementIDs(Collection<String> targetServiceElementIDs) {
        this.targetServiceElementIDs = targetServiceElementIDs;
    }

    public Collection<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Collection<Metric> metrics) {
        this.metrics = metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricFilter that = (MetricFilter) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (level != that.level) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        return result;
    }
}