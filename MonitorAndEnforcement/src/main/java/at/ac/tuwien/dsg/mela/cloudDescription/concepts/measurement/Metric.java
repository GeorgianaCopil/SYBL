package at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement;

import javax.xml.bind.annotation.*;

/**
 * ==============================
 *
 * @author: Daniel Moldovan
 * Technical University of Vienna
 * ==============================
 * <p/>
 * Encapsulates a measurable metric.
 * Computes hashCode on name and measurementUnit
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Metric")
public class Metric {
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "measurementUnit", required = true)
    private String measurementUnit;

    @XmlAttribute(name = "type", required = true)
    private MetricType type = MetricType.RESOURCE;

    public Metric(String name, String measurementUnit) {
        this.name = name;
        this.measurementUnit = measurementUnit;
    }

    public Metric(String name, String measurementUnit, MetricType type) {
        this.name = name;
        this.measurementUnit = measurementUnit;
        this.type = type;
    }

    public Metric(String name) {
        this.name = name;
    }

    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public Metric() {
    }

    public String getName() {
        return name;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        if (name != null ? !name.equals(metric.name) : metric.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", measurementUnit='" + measurementUnit + '\'' +
                '}';
    }

    public Metric clone() {
        return new Metric(name, measurementUnit);
    }

    public static final String PERCENTAGE = "%";
    public static final String EURO = "euro";
    public static final String DOLLARS = "dollars";

    public static final String MEGA_BYTES = "MB";
    public static final String GIGA_BYTES = "GB";
    public static final String VIRTUAL_CPU = "V_CPU";
    public static final String TYPE = "ValueType";
    public static final String MONTH_HOURS = "hours/month";
    public static final String MONTHLY = "Month";
    public static final String HOURLY = "Hour";
    public static final String LEVEL = "Level";

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Metric")
    @XmlEnum
    public enum MetricType {
        @XmlEnumValue("RESOURCE")
        RESOURCE,
        @XmlEnumValue("COST")
        COST,
        @XmlEnumValue("QUALITY")
        QUALITY
    }

}
