package at.ac.tuwien.dsg.cloudServiceDependencyGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;


public class Node implements Serializable{
	protected ArrayList<ElasticityRequirement> elasticityRequirements = new ArrayList<ElasticityRequirement>();
	protected ArrayList<ElasticityCapability> elasticityCapabilities = new ArrayList<ElasticityCapability>();
	protected ArrayList<ElasticityMetric> elasticityMetrics = new ArrayList<ElasticityMetric>();
	protected HashMap<Node,Relationship> relatedNodes = new HashMap<Node,Relationship>(); // KEEP THESE TWO SYNCHRONIZED
	protected HashMap<RelationshipType,ArrayList<Node>> relationships = new HashMap<RelationshipType,ArrayList<Node>>(); // KEEP THESE TWO SYNCHRONIZED
	protected String id;
	protected HashMap<String, Object> staticInformation = new HashMap<String,Object> ();
	protected NodeType nodeType;
	public static enum NodeType{
		   CLOUD_SERVICE,CODE_REGION,SERVICE_TOPOLOGY,SERVICE_UNIT,OS_PROCESS,VIRTUAL_MACHINE,VIRTUAL_CLUSTER,CLOUD_INFRASTRUCTURE;
		 }
	public ArrayList<ElasticityRequirement> getElasticityRequirements() {
		return elasticityRequirements;
	}
	public void setElasticityRequirements(ArrayList<ElasticityRequirement> elasticityRequirements) {
		this.elasticityRequirements = elasticityRequirements;
	}
	public ArrayList<ElasticityCapability> getElasticityCapabilities() {
		return elasticityCapabilities;
	}
	public void setElasticityCapabilities(ArrayList<ElasticityCapability> elasticityCapabilities) {
		this.elasticityCapabilities = elasticityCapabilities;
	}
	public void addElasticityRequirement(ElasticityRequirement elasticityRequirement){
		elasticityRequirements.add(elasticityRequirement);
	}
	public void addElasticityCapability(ElasticityCapability elasticityCapability){
		elasticityCapabilities.add(elasticityCapability);
	}
	public ArrayList<ElasticityMetric> getElasticityMetrics() {
		return elasticityMetrics;
	}
	public void setElasticityMetrics(ArrayList<ElasticityMetric> elasticityMetrics) {
		this.elasticityMetrics = elasticityMetrics;
	}
	public void addElasticityMetric(ElasticityMetric elasticityMetric){
		this.elasticityMetrics.add(elasticityMetric);
	}
	public void setValueForElasticityMetric(ElasticityMetric metric, Object value){
		if (elasticityMetrics.contains(metric)){
			elasticityMetrics.get(elasticityMetrics.indexOf(metric)).setValue(value);
		}else{
			metric.setValue(value);
			elasticityMetrics.add(metric);
		}
	}
	public void setValueForElasticityMetric(String metricName, Object value){
		for (ElasticityMetric m:elasticityMetrics){
			if (m.getMetricName().equalsIgnoreCase(metricName)){
				m.setValue(value);
				return;
			}
		}
		GraphLogger.logger.info("Cannot set metric value. Metric not found with the name "+metricName);
	}
	public void addNode(Node node, Relationship rel){
		if	(getRelatedNode(node.getId())!=null){
		removeNode(node.getId());
			
			
		}
		getRelatedNodes().put(node, rel);
			if (getRelationships().containsKey(rel.getType())){
				Node foundNode = null;
				for (Node myNode : getAllRelatedNodesOfType(rel.getType())){
					if ((myNode.getId().equalsIgnoreCase(node.getId())) ){
						foundNode =myNode;
					}
							
				}
				if (foundNode!=null)
					relationships.get(rel.getType()).remove(foundNode);
				getRelationships().get(rel.getType()).add(node);
				
			}
			else
			{
				ArrayList<Node> strings = new ArrayList<Node>();
				strings.add(node);
				getRelationships().put(rel.getType(), strings);
			}
	}
	public  Node getRelatedNode(String id){
		for (Node n:getRelatedNodes().keySet()){
			if (n.getId().equalsIgnoreCase(id)){
				return n;
			}
		}
		return null;
	}
	public  Set<Node> getAllRelatedNodes(){
		return  getRelatedNodes().keySet();
	}
	public  ArrayList<Node> getAllRelatedNodesOfType(RelationshipType relationshipType){
		if (relationships==null || relationships.entrySet().size()==0){
			//GraphLogger.logger.info("Populating relationships map");

			this.relationships = new HashMap<RelationshipType,ArrayList<Node>>();
			for (Entry<Node, Relationship> e:relatedNodes.entrySet()){
				if (relationships.containsKey(e.getValue().getType())){
					relationships.get(e.getValue().getType()).add(e.getKey());
				}else{
					ArrayList<Node> nodes = new ArrayList<Node>();
					nodes.add(e.getKey());
					relationships.put(e.getValue().getType(), nodes);
				}
			}
		}
		return (ArrayList<Node>) getRelationships().get(relationshipType);
	}
public Relationship getRelationshipWithNode(Node string){
	return getRelatedNodes().get(string);
}
public  ArrayList<Node> getAllRelatedNodesOfType(RelationshipType relationshipType,NodeType nodeType){
	if (relationships==null || relationships.entrySet().size()==0){
		//GraphLogger.logger.info("Populating relationships map");
		this.relationships = new HashMap<RelationshipType,ArrayList<Node>>();
		for (Entry<Node, Relationship> e:relatedNodes.entrySet()){
			if (relationships.containsKey(e.getValue().getType())){
				relationships.get(e.getValue().getType()).add(e.getKey());
			}else{
				ArrayList<Node> nodes = new ArrayList<Node>();
				nodes.add(e.getKey());
				relationships.put(e.getValue().getType(), nodes);
			}
		}
	}
		ArrayList<Node> myNodes = new ArrayList<Node>();
		if (relationships.get(relationshipType)!=null)
		for (Node string:getRelationships().get(relationshipType)){
			if (string.getNodeType()==nodeType) myNodes.add(string);
		}
		return myNodes;
	}
	public Set<Relationship.RelationshipType> getAllRelTypesExistentForThisNode(){
		return  getRelationships().keySet();
	}
	public  void removeNode(Node node){
		
		Relationship rel = getRelatedNodes().get(node);
		getRelationships().get(rel).remove(node);
		getRelatedNodes().remove(node);
	}
	public  void removeNode(String id){
		Node string = null;
		for (Node n:getRelatedNodes().keySet()){
			if (n.getId().equalsIgnoreCase(id)){
				string = n;
			}
		}
		Relationship rel = getRelatedNodes().get(string);
		getRelationships().get(rel).remove(string);
		getRelatedNodes().remove(string);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
	public Object getElasticityMetricValue(String metricName){
		for (ElasticityMetric m:elasticityMetrics){
			if (m.getMetricName().equalsIgnoreCase(metricName)){
				return m.getValue();
			}
		}
		GraphLogger.logger.info("Cannot get metric value. Metric not found with the name "+metricName);
		return 0;
	}
	public boolean hasElasticityMetric(String metricName){
		for (ElasticityMetric m:elasticityMetrics){
			if (m.getMetricName().equalsIgnoreCase(metricName)){
				return true;
			}
		}
		return false;
	}
	public void addElasticityMetric(String metricName, String unit, Object value){
		ElasticityMetric elasticityMetric=new ElasticityMetric();
		elasticityMetric.setValue(value);
		elasticityMetric.setMetricName(metricName);
		elasticityMetric.setMeasurementUnit(unit);
	}
	public ArrayList<String> getAssociatedIpsToCurrentNode(ArrayList<String> ips){
		//TODO: find all service units - get associated vms and add ips to list
		if (nodeType==NodeType.SERVICE_UNIT){
			for (Node n:getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP)){
				try{
				ips.addAll((List<String>)n.getStaticInformation("ip"));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		if (nodeType==NodeType.SERVICE_TOPOLOGY || nodeType==NodeType.CLOUD_SERVICE){
			for (Node n:getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)){
				n.getAssociatedIpsToCurrentNode(ips);
			}
		}
		
		return ips;
 	}
	public ArrayList<String> getAssociatedIps(){
		return (ArrayList<String>) getAssociatedIpsToCurrentNode(new ArrayList<String>());
	}
	public String toString(){
		String message = " Current node has id "+id +" and is of type "+ nodeType+". ";
		for (RelationshipType type:getRelationships().keySet()){
		message+="Nodes related with "+id+" with relationship "+type+" are: ";
		
		for (Node string: getRelationships().get(type)){
			message+=" "+string.getId()+" ";
		}
		message += " . ";
		}
		if (!message.equalsIgnoreCase(" "))
			return message+"\n";
		else return message;
		
	}
	public HashMap<String, Object> getStaticInformation() {
		return staticInformation;
	}
	public void setStaticInformation(HashMap<String, Object> staticInformation) {
		this.staticInformation = staticInformation;
	}
	public Object getStaticInformation(String infoType){
		return staticInformation.get(infoType);
	}
	public void putStaticInformation(String infoType, String value){
		staticInformation.put(infoType, value);
	}
	private HashMap<RelationshipType,ArrayList<Node>> getRelationships() {
		return relationships;
	}
	private void setRelationships(HashMap<RelationshipType,ArrayList<Node>> relationships) {
		this.relationships = relationships;
	}
	public HashMap<Node,Relationship> getRelatedNodes() {
		return relatedNodes;
	}
	public void setRelatedNodes(HashMap<Node,Relationship> relatedNodes) {
		this.relatedNodes = relatedNodes;
		this.relationships = new HashMap<RelationshipType,ArrayList<Node>>();
		for (Entry<Node, Relationship> e:relatedNodes.entrySet()){
			if (relationships.containsKey(e.getValue().getType())){
				relationships.get(e.getValue().getType()).add(e.getKey());
			}else{
				ArrayList<Node> nodes = new ArrayList<Node>();
				nodes.add(e.getKey());
				relationships.put(e.getValue().getType(), nodes);
			}
		}
	}
}