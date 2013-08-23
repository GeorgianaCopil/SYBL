package at.ac.tuwien.dsg.mela.jaxbGangliaEntities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ==============================
 *
 * @author: Daniel Moldovan
 * Technical University of Vienna
 * ==============================
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EXTRA_ELEMENT")
public class GangliaExtraElementInfo {

    @XmlAttribute(name = "NAME")
    private String name;

    @XmlAttribute(name = "VAL")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ExtraElementInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}