package at.ac.tuwien.dsg.mela.jaxbGangliaEntities;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ==============================
 *
 * @author: Daniel Moldovan
 * Technical University of Vienna
 * ==============================
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HOST")
public class GangliaHostInfo {
    @XmlAttribute(name = "NAME", required = true)
    private String name;

    @XmlAttribute(name = "IP", required = true)
    private String ip;

    @XmlAttribute(name = "LOCATION", required = true)
    private String location;

    @XmlAttribute(name = "TAGS")
    private String tags;

    @XmlAttribute(name = "REPORTED")
    private String reported;

    @XmlAttribute(name = "TN")
    private String tn;

    @XmlAttribute(name = "TMAX")
    private String tmax;

    @XmlAttribute(name = "DMAX")
    private String dmax;

    @XmlAttribute(name = "GMOND_STARTED")
    private String gmondStarted;

    @XmlAttribute(name = "SOURCE")
    private String source;

    @XmlElement(name = "METRIC")
    Collection<GangliaMetricInfo> metrics;

    {
        metrics = new ArrayList<GangliaMetricInfo>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public String getTmax() {
        return tmax;
    }

    public void setTmax(String tmax) {
        this.tmax = tmax;
    }

    public String getDmax() {
        return dmax;
    }

    public void setDmax(String dmax) {
        this.dmax = dmax;
    }

    public String getGmondStarted() {
        return gmondStarted;
    }

    public void setGmondStarted(String gmondStarted) {
        this.gmondStarted = gmondStarted;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Collection<GangliaMetricInfo> getMetrics() {
        return metrics;
    }

    public void setMetrics(Collection<GangliaMetricInfo> metrics) {
        this.metrics = metrics;
    }

    /**
     * @param name name to search for. All Metrics that CONTAIN the supplied name
     *             will be returned
     * @return
     */
    public Collection<GangliaMetricInfo> searchMetricsByName(String name) {
        List<GangliaMetricInfo> metrics = new ArrayList<GangliaMetricInfo>();
        for (GangliaMetricInfo metricInfo : this.metrics) {
            if (metricInfo.getName().contains(name)) {
                metrics.add(metricInfo);
            }
        }
        return metrics;
    }

    @Override
    public String toString() {
        String info = "HostInfo{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", location='" + location + '\'' +
                ", tags='" + tags + '\'' +
                ", reported='" + reported + '\'' +
                ", tn='" + tn + '\'' +
                ", tmax='" + tmax + '\'' +
                ", dmax='" + dmax + '\'' +
                ", gmondStarted='" + gmondStarted + '\'' +
                ", source='" + source + '\'' + ", metrics=";

        for (GangliaMetricInfo metricInfo : metrics) {
            info += "\n\t " + metricInfo.toString();
        }
        info += '}';
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GangliaHostInfo that = (GangliaHostInfo) o;

        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        return result;
    }
}
