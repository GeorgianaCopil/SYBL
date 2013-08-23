package at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "SYBLElasticityRequirementsDescription")
public class SYBLElasticityRequirementsDescription {
    @XmlElement(name = "SYBLSpecification", required = true)
	private List<SYBLSpecification> syblSpecifications = new ArrayList<SYBLSpecification>();

	public List<SYBLSpecification> getSyblSpecifications() {
		return syblSpecifications;
	}

	public void setSyblSpecifications(List<SYBLSpecification> syblSpecifications) {
		this.syblSpecifications = syblSpecifications;
	}

}
