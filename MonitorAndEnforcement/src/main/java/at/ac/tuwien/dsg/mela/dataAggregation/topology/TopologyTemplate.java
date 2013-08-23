package at.ac.tuwien.dsg.mela.dataAggregation.topology;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TopologyTemplate")
public class TopologyTemplate {
    @XmlElement(name = "Service", required = false)
    private ServiceElement component;

    public ServiceElement getService() {
        return component;
    }

    public void setComponent(ServiceElement component) {
        this.component = component;
    }
}
