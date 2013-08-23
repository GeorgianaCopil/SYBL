package at.ac.tuwien.dsg.mela.jaxbGangliaEntities;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ==============================
 *
 * @author: Daniel Moldovan
 * Technical University of Vienna
 * ==============================
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CLUSTER")
public class GangliaClusterInfo {
    @XmlAttribute(name = "NAME")
    private String name;

    @XmlAttribute(name = "OWNER")
    private String owner;

    @XmlAttribute(name = "LATLONG")
    private String latlong;

    @XmlAttribute(name = "URL")
    private String url;

    @XmlAttribute(name = "LOCALTIME")
    private String localtime;

    @XmlElement(name = "HOST")
    private Collection<GangliaHostInfo> hostsInfo;

    {
        hostsInfo = new ArrayList<GangliaHostInfo>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public Collection<GangliaHostInfo> getHostsInfo() {
        return hostsInfo;
    }

    public void setHostsInfo(Collection<GangliaHostInfo> hostsInfo) {
        this.hostsInfo = hostsInfo;
    }

    public Collection<GangliaHostInfo> searchHostsByName(String name) {
        Collection<GangliaHostInfo> hosts = new ArrayList<GangliaHostInfo>();
        for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getName().contains(name)) {
                hosts.add(hostInfo);
            }
        }
        return hosts;
    }

    public Collection<GangliaHostInfo> searchHostsByIP(String ip) {
        Collection<GangliaHostInfo> hosts = new ArrayList<GangliaHostInfo>();
        for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getIp().contains(ip)) {
                hosts.add(hostInfo);
            }
        }
        return hosts;
    }

    //if gmodstart has same value means same machine
    public Collection<GangliaHostInfo> searchHostsByGmodStart(String gmodstarted) {
        Collection<GangliaHostInfo> hosts = new ArrayList<GangliaHostInfo>();
        for (GangliaHostInfo hostInfo : this.hostsInfo) {
            if (hostInfo.getGmondStarted().contains(gmodstarted)) {
                hosts.add(hostInfo);
            }
        }
        return hosts;
    }

    @Override
    public String toString() {
        String info = "ClusterInfo{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", latlong='" + latlong + '\'' +
                ", url='" + url + '\'' +
                ", localtime='" + localtime + '\'' + ", hostsInfo=";


        for (GangliaHostInfo hostInfo : hostsInfo) {
            info += "\n " + hostInfo.toString() + "\n";
        }


        info += '}';
        return info;
    }
}
