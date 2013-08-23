package at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SYBLDirective")
public class SYBLAnnotationXML {
       private String entityID;
	   @XmlAttribute(name = "Constraints")
	   private String constraints ="";
	   	@XmlAttribute(name="Monitoring")
		private String monitoring ="";
	   	@XmlAttribute (name="Priorities")
	   	private String priorities ="";
	   	@XmlAttribute(name="Strategies")
		private String strategies ="";

		public String getStrategies() {
			return strategies;
		}

		public void setStrategies(String strategies) {
			this.strategies = strategies;
		}

		public String getMonitoring() {
			return monitoring;
		}

		public void setMonitoring(String monitoring) {
			this.monitoring = monitoring;
		}

		public String getConstraints() {
			return constraints;
		}

		public void setConstraints(String constraint) {
			this.constraints = constraint;
		}

		public String getEntityID() {
			return entityID;
		}

		public void setEntityID(String entityID) {
			this.entityID = entityID;
		}

		public String getPriorities() {
			return priorities;
		}

		public void setPriorities(String priorities) {
			this.priorities = priorities;
		}
	   	
	   	
	   	

}
