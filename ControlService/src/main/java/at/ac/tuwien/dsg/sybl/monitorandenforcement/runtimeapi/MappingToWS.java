package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.controlService.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.RelatedNodes;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.StaticInformation;

public class MappingToWS {
 public static Node mapNodeToNode(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node string){
	
	 Node resultNode = new Node();
 
	 List<ElasticityCapability> elasticityCapabilities = new ArrayList<ElasticityCapability>();
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability elasticityCapability:string.getElasticityCapabilities()){
		 elasticityCapabilities.add(mapCapability(elasticityCapability));
	 }
	 List<ElasticityRequirement> elasticityRequirements = new ArrayList<ElasticityRequirement>();
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement elasticityRequirement:string.getElasticityRequirements()){
		 elasticityRequirements.add(mapElasticityRequirement(elasticityRequirement));
	 }
	 List<ElasticityMetric> elasticityMetrics = new ArrayList<ElasticityMetric>();
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric elasticityMetric:string.getElasticityMetrics()){
		 elasticityMetrics.add(mapElasticityMetric(elasticityMetric));
	 }
	 
	 resultNode.elasticityRequirements=elasticityRequirements;
	 resultNode.elasticityMetrics=elasticityMetrics;
	 resultNode.elasticityCapabilities=elasticityCapabilities;
	 resultNode.setId(string.getId());
	 switch (string.getNodeType()){
	 case CLOUD_SERVICE: resultNode.setNodeType(NodeType.CLOUD_SERVICE); break;
	 case CODE_REGION: resultNode.setNodeType(NodeType.CODE_REGION); break;
	 case OS_PROCESS: resultNode.setNodeType(NodeType.OS_PROCESS); break;
	 case VIRTUAL_MACHINE: resultNode.setNodeType(NodeType.VIRTUAL_MACHINE); break;
	 case VIRTUAL_CLUSTER: resultNode.setNodeType(NodeType.VIRTUAL_CLUSTER); break;
	 case CLOUD_INFRASTRUCTURE: resultNode.setNodeType(NodeType.CLOUD_INFRASTRUCTURE); break;
	 case SERVICE_TOPOLOGY: resultNode.setNodeType(NodeType.SERVICE_TOPOLOGY); break;
	 case SERVICE_UNIT: resultNode.setNodeType(NodeType.SERVICE_UNIT); break;
	 default:  resultNode.setNodeType(NodeType.CLOUD_SERVICE); break;
	 }
	 HashMap<String,Object> staticInfo = string.getStaticInformation();
	 StaticInformation information=new StaticInformation();
	 List<at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.StaticInformation.Entry> entries = new ArrayList<at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.StaticInformation.Entry>();
	 for (Entry entry:staticInfo.entrySet()){
		 at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.StaticInformation.Entry e = new at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.StaticInformation.Entry();
		 e.setKey((String) entry.getKey());
		 e.setValue(entry.getValue());
		 entries.add(e );
	 }
	 information.entry=entries;
	 resultNode.setStaticInformation(information);
	 Node.RelatedNodes relNodes = new Node.RelatedNodes();
	
	 List<Node.RelatedNodes.Entry> entrRelNodes = new ArrayList<Node.RelatedNodes.Entry>();
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType type:string.getAllRelTypesExistentForThisNode())
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node relNode:string.getAllRelatedNodesOfType(type)){
		Node.RelatedNodes.Entry relatedNode = new Node.RelatedNodes.Entry();
		Node mappedNode = mapNodeToNode(relNode);
		relatedNode.key=mappedNode;
		Relationship rel = new Relationship();
		rel.setSourceElement(resultNode.getId());
		rel.setTargetElement(mappedNode.getId());
		rel.setType(mapRelationshipType(type));
		relatedNode.value = rel;
		entrRelNodes.add(relatedNode);
		}
	 
	 
	 relNodes.entry=entrRelNodes;
	 	//PlanningLogger.logger.info("Related nodes for node "+resultNode.id+ " "+ entrRelNodes.size());
	 resultNode.setRelatedNodes(relNodes);
	 return resultNode;
 }
 public static RelationshipType mapRelationshipType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType relType){
	 switch (relType){
	 case COMPOSITION_RELATIONSHIP: 
		 return RelationshipType.COMPOSITION_RELATIONSHIP;
	
	 case HOSTED_ON_RELATIONSHIP: 
		 return RelationshipType.HOSTED_ON_RELATIONSHIP;
	 
	 case MASTER_OF: 
		 return RelationshipType.MASTER_OF;
	 
	 case PEER_OF: 
		 return RelationshipType.PEER_OF;
	 
	 default: return RelationshipType.COMPOSITION_RELATIONSHIP;
	 }
 }
public static ElasticityCapability mapCapability(at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability capability){
	
	ElasticityCapability elasticityCapability=new ElasticityCapability();
	elasticityCapability.setApiMethod(capability.getApiMethod());
	elasticityCapability.setName(capability.getName());
	elasticityCapability.setParameter(capability.getParameter());
	elasticityCapability.setValue(capability.getValue());
	return elasticityCapability;
}
public static ElasticityRequirement mapElasticityRequirement(at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement requirement){
	ElasticityRequirement elasticityRequirement = new ElasticityRequirement();
	elasticityRequirement.setAnnotation(mapAnnotation(requirement.getAnnotation()));
	
	return elasticityRequirement;
}
public static ElasticityMetric mapElasticityMetric(at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric metric){
	ElasticityMetric elasticityMetric = new ElasticityMetric();
	elasticityMetric.setMeasurementUnit(metric.getMeasurementUnit());
	elasticityMetric.setMetricName(metric.getMetricName());
	elasticityMetric.setValue(metric.getValue());
	return elasticityMetric;
}
public static SyblAnnotation mapAnnotation(SYBLAnnotation annotation){
	 SyblAnnotation syblAnnotation = new  SyblAnnotation();
	 switch (annotation.getAnnotationType()){
	 case CLOUD_SERVICE : syblAnnotation.setAnnotationType(AnnotationType.CLOUD_SERVICE);break;
	 case SERVICE_TOPOLOGY : syblAnnotation.setAnnotationType(AnnotationType.SERVICE_TOPOLOGY);break;
	 case SERVICE_UNIT : syblAnnotation.setAnnotationType(AnnotationType.SERVICE_UNIT);break;
	 
	 case CODE_REGION : syblAnnotation.setAnnotationType(AnnotationType.CODE_REGION);break;
	 case RELATIONSHIP : syblAnnotation.setAnnotationType(AnnotationType.RELATIONSHIP);break;
	 default: syblAnnotation.setAnnotationType(AnnotationType.CLOUD_SERVICE);break;
	 }
	 syblAnnotation.setConstraints(annotation.getConstraints());
	 syblAnnotation.setEntityID(annotation.getEntityID());
	 syblAnnotation.setMonitoring(annotation.getMonitoring());
	 syblAnnotation.setStrategies(annotation.getStrategies());
	 syblAnnotation.setPriorities(annotation.getPriorities());
	 return syblAnnotation;
}

}
