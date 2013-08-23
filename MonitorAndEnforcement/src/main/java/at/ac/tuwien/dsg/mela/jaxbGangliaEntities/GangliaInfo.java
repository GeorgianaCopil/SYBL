package at.ac.tuwien.dsg.mela.jaxbGangliaEntities;


import javax.xml.bind.annotation.*;
import java.util.Collection;

/**
 * ==============================
 *
 * @author: Daniel Moldovan
 * Technical University of Vienna
 * ==============================
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GANGLIA_XML")
public class GangliaInfo {

    @XmlElement(name = "CLUSTER")
    private Collection<GangliaClusterInfo> clusters;

    @XmlAttribute(name = "VERSION")
    private String version;

    @XmlAttribute(name = "SOURCE")
    private String source;


    public Collection<GangliaClusterInfo> getClusters() {
        return clusters;
    }

    public void setClusters(Collection<GangliaClusterInfo> clusters) {
        this.clusters = clusters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
