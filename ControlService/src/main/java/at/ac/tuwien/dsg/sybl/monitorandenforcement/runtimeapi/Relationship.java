
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for relationship complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="relationship">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="elasticityRelationship" type="{http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/}elasticityInfoRelationship" minOccurs="0"/>
 *         &lt;element name="sourceElement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetElement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/}relationshipType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationship", propOrder = {
    "elasticityRelationship",
    "sourceElement",
    "targetElement",
    "type"
})
public class Relationship {

    protected ElasticityInfoRelationship elasticityRelationship;
    protected String sourceElement;
    protected String targetElement;
    protected RelationshipType type;

    /**
     * Gets the value of the elasticityRelationship property.
     * 
     * @return
     *     possible object is
     *     {@link ElasticityInfoRelationship }
     *     
     */
    public ElasticityInfoRelationship getElasticityRelationship() {
        return elasticityRelationship;
    }

    /**
     * Sets the value of the elasticityRelationship property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElasticityInfoRelationship }
     *     
     */
    public void setElasticityRelationship(ElasticityInfoRelationship value) {
        this.elasticityRelationship = value;
    }

    /**
     * Gets the value of the sourceElement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceElement() {
        return sourceElement;
    }

    /**
     * Sets the value of the sourceElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceElement(String value) {
        this.sourceElement = value;
    }

    /**
     * Gets the value of the targetElement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetElement() {
        return targetElement;
    }

    /**
     * Sets the value of the targetElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetElement(String value) {
        this.targetElement = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link RelationshipType }
     *     
     */
    public RelationshipType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationshipType }
     *     
     */
    public void setType(RelationshipType value) {
        this.type = value;
    }

}
