package at.ac.tuwien.dsg.inputProcessing.multiLevelModel.deploymentDescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeploymentUnit")
public class DeploymentUnit {
    @XmlAttribute(name = "defaultImage")
	private String defaultImage="";
 @XmlAttribute(name = "defaultFlavor")
 private String defaultFlavor="";
 @XmlAttribute(name = "serviceUnitID")
 private String serviceUnitID="";
 
public String getDefaultFlavor() {
	return defaultFlavor;
}
public void setDefaultFlavor(String defaultFlavor) {
	this.defaultFlavor = defaultFlavor;
}
public String getDefaultImage() {
	return defaultImage;
}
public void setDefaultImage(String defaultImage) {
	this.defaultImage = defaultImage;
}
public String getServiceUnitID() {
	return serviceUnitID;
}
public void setServiceUnitID(String serviceUnitID) {
	this.serviceUnitID = serviceUnitID;
}
}
