
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi;

import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.controlService.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node.StaticInformation.Entry;

public class MappingFromWS {
 public static at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node mapNodeToNode(Node node,ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node> strings){
	 at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node resultNode = new at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node();
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node n:strings){
		 if (node.getId().equalsIgnoreCase(n.getId())) resultNode = n;
	 }
	 ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability> elasticityCapabilities = new ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability>();
	 for (ElasticityCapability elasticityCapability:node.getElasticityCapabilities()){
		 elasticityCapabilities.add(mapCapability(elasticityCapability));
	 }
	 ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement> elasticityRequirements = new ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement>();
	 for (ElasticityRequirement elasticityRequirement:node.getElasticityRequirements()){
		 elasticityRequirements.add(mapElasticityRequirement(elasticityRequirement));
	 }
	 ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric> elasticityMetrics = new ArrayList<at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric>();
	 for (ElasticityMetric elasticityMetric:node.getElasticityMetrics()){
		 elasticityMetrics.add(mapElasticityMetric(elasticityMetric));
	 }
	 
	 resultNode.setElasticityRequirements(elasticityRequirements);
	 resultNode.setElasticityMetrics(elasticityMetrics);
	 resultNode.setElasticityCapabilities(elasticityCapabilities);
	 resultNode.setId(node.getId());
	 switch (node.getNodeType()){
	 case CLOUD_SERVICE: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.CLOUD_SERVICE); break;
	 case CODE_REGION: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.CODE_REGION); break;
	 case OS_PROCESS: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.OS_PROCESS); break;
	 case VIRTUAL_MACHINE: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.VIRTUAL_MACHINE); break;
	 case VIRTUAL_CLUSTER: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.VIRTUAL_CLUSTER); break;
	 case CLOUD_INFRASTRUCTURE: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.CLOUD_INFRASTRUCTURE); break;
	 case SERVICE_TOPOLOGY: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.SERVICE_TOPOLOGY); break;
	 case SERVICE_UNIT: resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.SERVICE_UNIT); break;
	 default:  resultNode.setNodeType(at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType.CLOUD_SERVICE); break;
	 }
	 HashMap<String,Object> staticInfo = new HashMap<String,Object>();
	 for (Entry entry:node.getStaticInformation().getEntry()){
		 staticInfo.put(entry.getKey(), entry.getValue());
	 }
	 resultNode.setStaticInformation(staticInfo);

	 for (Node.RelatedNodes.Entry relNode:node.relatedNodes.entry){
		at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node mappedNode = mapNodeToNode(relNode.key,strings);
		at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship rel = new at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship();
		rel.setSourceElement(resultNode.getId());
		rel.setTargetElement(mappedNode.getId());
		 strings.add(mappedNode);

		rel.setType(mapRelationshipType(relNode.value.type));
		resultNode.addNode(mappedNode,rel);
		}
	 for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node n:resultNode.getAllRelatedNodes()){
		at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship rel= resultNode.getRelationshipWithNode(n);
		at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node currentNode=null;
		for (at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node n1:strings){
			if (n1.getId().equalsIgnoreCase(n.getId())) currentNode=n1;
		}
		if (currentNode!=null){
			rel.setTargetElement(currentNode.getId());
			PlanningLogger.logger.info("Setting related node with "+node.id+ " the "+ currentNode.getId());
			resultNode.addNode(currentNode, rel);
		}
	 }
		PlanningLogger.logger.info("Related nodes for node "+ resultNode);

	 return resultNode;
 }
 public static at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType mapRelationshipType(RelationshipType relType){
	 switch (relType){
	 case COMPOSITION_RELATIONSHIP: 
		 return at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType.COMPOSITION_RELATIONSHIP;
	
	 case HOSTED_ON_RELATIONSHIP: 
		 return at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP;
	 
	 case MASTER_OF: 
		 return at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType.MASTER_OF;
	 
	 case PEER_OF: 
		 return at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType.PEER_OF;
	 
	 default: return at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType.COMPOSITION_RELATIONSHIP;
	 }
 }
public static at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability mapCapability(ElasticityCapability capability){
	
	at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability elasticityCapability=new at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability();
	elasticityCapability.setApiMethod(capability.getApiMethod());
	elasticityCapability.setName(capability.getName());
	elasticityCapability.setParameter(capability.getParameter());
	elasticityCapability.setValue(capability.getValue());
	return elasticityCapability;
}
public static at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement mapElasticityRequirement(ElasticityRequirement requirement){
	at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement elasticityRequirement = new at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement();
	elasticityRequirement.setAnnotation(mapAnnotation(requirement.getAnnotation()));
	
	return elasticityRequirement;
}
public static at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric mapElasticityMetric(ElasticityMetric metric){
	at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric elasticityMetric = new at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric();
	elasticityMetric.setMeasurementUnit(metric.getMeasurementUnit());
	elasticityMetric.setMetricName(metric.getMetricName());
	elasticityMetric.setValue(metric.getValue());
	return elasticityMetric;
}
public static at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation mapAnnotation(SyblAnnotation annotation){
	 at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation syblAnnotation = new  at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation();
	 switch (annotation.getAnnotationType()){
	 case CLOUD_SERVICE : syblAnnotation.setAnnotationType(SYBLAnnotation.AnnotationType.CLOUD_SERVICE);break;
	 case SERVICE_TOPOLOGY : syblAnnotation.setAnnotationType(SYBLAnnotation.AnnotationType.SERVICE_TOPOLOGY);break;
	 case SERVICE_UNIT : syblAnnotation.setAnnotationType(SYBLAnnotation.AnnotationType.SERVICE_UNIT);break;
	 
	 case CODE_REGION : syblAnnotation.setAnnotationType(SYBLAnnotation.AnnotationType.CODE_REGION);break;
	 case RELATIONSHIP : syblAnnotation.setAnnotationType(SYBLAnnotation.AnnotationType.RELATIONSHIP);break;
	 default: syblAnnotation.setAnnotationType(SYBLAnnotation.AnnotationType.CLOUD_SERVICE);break;
	 }
	 syblAnnotation.setConstraints(annotation.getConstraints());
	 syblAnnotation.setEntityID(annotation.getEntityID());
	 syblAnnotation.setMonitoring(annotation.getMonitoring());
	 syblAnnotation.setStrategies(annotation.getStrategies());
	 syblAnnotation.setPriorities(annotation.getPriorities());
	 return syblAnnotation;
}

}
