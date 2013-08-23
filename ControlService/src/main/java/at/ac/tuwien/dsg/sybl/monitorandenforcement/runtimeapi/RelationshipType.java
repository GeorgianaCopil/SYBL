
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for relationshipType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="relationshipType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COMPOSITION_RELATIONSHIP"/>
 *     &lt;enumeration value="HOSTED_ON_RELATIONSHIP"/>
 *     &lt;enumeration value="ASSOCIATED_AT_RUNTIME_RELATIONSHIP"/>
 *     &lt;enumeration value="RUNS_ON"/>
 *     &lt;enumeration value="MASTER_OF"/>
 *     &lt;enumeration value="PEER_OF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "relationshipType")
@XmlEnum
public enum RelationshipType {

    COMPOSITION_RELATIONSHIP,
    HOSTED_ON_RELATIONSHIP,
    ASSOCIATED_AT_RUNTIME_RELATIONSHIP,
    RUNS_ON,
    MASTER_OF,
    PEER_OF;

    public String value() {
        return name();
    }

    public static RelationshipType fromValue(String v) {
        return valueOf(v);
    }

}
