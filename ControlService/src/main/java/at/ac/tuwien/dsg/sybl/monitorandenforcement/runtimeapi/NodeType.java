
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for nodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="nodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CLOUD_SERVICE"/>
 *     &lt;enumeration value="CODE_REGION"/>
 *     &lt;enumeration value="SERVICE_TOPOLOGY"/>
 *     &lt;enumeration value="SERVICE_UNIT"/>
 *     &lt;enumeration value="OS_PROCESS"/>
 *     &lt;enumeration value="VIRTUAL_MACHINE"/>
 *     &lt;enumeration value="VIRTUAL_CLUSTER"/>
 *     &lt;enumeration value="CLOUD_INFRASTRUCTURE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "nodeType")
@XmlEnum
public enum NodeType {

    CLOUD_SERVICE,
    CODE_REGION,
    SERVICE_TOPOLOGY,
    SERVICE_UNIT,
    OS_PROCESS,
    VIRTUAL_MACHINE,
    VIRTUAL_CLUSTER,
    CLOUD_INFRASTRUCTURE;

    public String value() {
        return name();
    }

    public static NodeType fromValue(String v) {
        return valueOf(v);
    }

}
