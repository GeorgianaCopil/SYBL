
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for elasticityRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elasticityRequirement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="annotation" type="{http://runtimeAPI.monitorandenforcement.sybl.dsg.tuwien.ac.at/}syblAnnotation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "elasticityRequirement", propOrder = {
    "annotation"
})
public class ElasticityRequirement {

    protected SyblAnnotation annotation;

    /**
     * Gets the value of the annotation property.
     * 
     * @return
     *     possible object is
     *     {@link SyblAnnotation }
     *     
     */
    public SyblAnnotation getAnnotation() {
        return annotation;
    }

    /**
     * Sets the value of the annotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SyblAnnotation }
     *     
     */
    public void setAnnotation(SyblAnnotation value) {
        this.annotation = value;
    }

}
