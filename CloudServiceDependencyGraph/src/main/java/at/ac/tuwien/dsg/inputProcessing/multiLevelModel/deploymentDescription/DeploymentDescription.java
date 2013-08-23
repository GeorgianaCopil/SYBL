package at.ac.tuwien.dsg.inputProcessing.multiLevelModel.deploymentDescription;

import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="DeploymentDescription", namespace="")
public class DeploymentDescription {
	@XmlElement(name = "DeploymentUnit")
	private List<DeploymentUnit> deployments = new ArrayList<DeploymentUnit>();
	
	@XmlAttribute(name="CloudServiceID")
	private String cloudServiceID="";
	public List<DeploymentUnit> getDeployments() {
		return deployments;
	}

	public void setDeployments(List<DeploymentUnit> deployments) {
		this.deployments = deployments;
	}

	public String getCloudServiceID() {
		return cloudServiceID;
	}

	public void setCloudServiceID(String cloudServiceID) {
		this.cloudServiceID = cloudServiceID;
	}
}
