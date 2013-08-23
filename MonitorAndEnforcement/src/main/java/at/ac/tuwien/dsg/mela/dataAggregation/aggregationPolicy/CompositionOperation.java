package at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompositionOperation")
public class CompositionOperation {
    @XmlAttribute(name = "name", required = true)
    private CompositionOperationType operation;
    @XmlAttribute(name = "value")
    private String value;

    //if a metric is referenced, the metric value is retrieved somehow
    @XmlAttribute(name = "referenceMetric")
    private String referenceMetric;

    public CompositionOperationType getOperation() {
        return operation;
    }

    public void setOperation(CompositionOperationType operation) {
        this.operation = operation;
    }

    public String getMetricName() {
        return referenceMetric;
    }

    public void setReferenceMetricName(String metricName) {
        this.referenceMetric = metricName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositionOperation that = (CompositionOperation) o;

        if (operation != that.operation) return false;
        if (referenceMetric != null ? !referenceMetric.equals(that.referenceMetric) : that.referenceMetric != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = operation != null ? operation.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (referenceMetric != null ? referenceMetric.hashCode() : 0);
        return result;
    }
}
