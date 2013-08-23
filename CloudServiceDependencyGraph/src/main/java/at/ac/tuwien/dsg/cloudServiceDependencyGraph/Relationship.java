package at.ac.tuwien.dsg.cloudServiceDependencyGraph;

import java.io.Serializable;

public class Relationship implements Serializable{
	private String sourceElement;// parent
	private String targetElement; //child
	private RelationshipType type = RelationshipType.COMPOSITION_RELATIONSHIP;
	private ElasticityInfoRelationship elasticityRelationship;
	public static enum RelationshipType{
		   COMPOSITION_RELATIONSHIP,HOSTED_ON_RELATIONSHIP, ASSOCIATED_AT_RUNTIME_RELATIONSHIP, RUNS_ON, MASTER_OF, PEER_OF;
		 }
	public String getSourceElement() {
		return sourceElement;
	}
	public void setSourceElement(String parent) {
		this.sourceElement = parent;
	}
	public String getTargetElement() {
		return targetElement;
	}
	public void setTargetElement(String child) {
		this.targetElement = child;
	}
	public RelationshipType getType() {
		return type;
	}
	public void setType(RelationshipType type) {
		this.type = type;
	}
	public ElasticityInfoRelationship getElasticityRelationship() {
		return elasticityRelationship;
	}
	public void setElasticityRelationship(ElasticityInfoRelationship elasticityRelationship) {
		this.elasticityRelationship = elasticityRelationship;
	}

}
