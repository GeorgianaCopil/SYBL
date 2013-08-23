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
@XmlRootElement(name = "EXTRA_DATA")
public class GangliaExtraDataInfo {

    @XmlElement(name = "EXTRA_ELEMENT")
    private Collection<GangliaExtraElementInfo> gangliaExtraElementInfo;

    {
        gangliaExtraElementInfo = new ArrayList<GangliaExtraElementInfo>();
    }

    public Collection<GangliaExtraElementInfo> getGangliaExtraElementInfo() {
        return gangliaExtraElementInfo;
    }

    public void setGangliaExtraElementInfo(Collection<GangliaExtraElementInfo> gangliaExtraElementInfo) {
        this.gangliaExtraElementInfo = gangliaExtraElementInfo;
    }

    @Override
    public String toString() {
        String info = "ExtraDataInfo{" +
                "ExtraElementInfo=";
        for (GangliaExtraElementInfo elementInfo : gangliaExtraElementInfo) {
            info += "\t " + elementInfo + "\n";
        }
        info += '}';
        return info;
    }
}