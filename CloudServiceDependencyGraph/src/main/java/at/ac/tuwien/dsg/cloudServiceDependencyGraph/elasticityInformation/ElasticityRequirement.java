package at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation;

import java.io.Serializable;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;


public class ElasticityRequirement {
	private SYBLAnnotation annotation;

	public SYBLAnnotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(SYBLAnnotation annotation) {
		this.annotation = annotation;
	}
}
