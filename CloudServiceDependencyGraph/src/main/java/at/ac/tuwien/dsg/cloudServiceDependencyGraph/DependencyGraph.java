package at.ac.tuwien.dsg.cloudServiceDependencyGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityMetric;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;

public class DependencyGraph implements Serializable{

	private Node cloudService;

	public Node getCloudService() {
		return cloudService;
	}

	public void setCloudService(Node cloudService) {
		this.cloudService = cloudService;
	}
	public ArrayList<Node> getAllServiceTopologies() {
		ArrayList<Node> topologies = new ArrayList<Node>();
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				topologies.add(n);
				if (n.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
					for (Node n1 : n
							.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
						if (n1.getNodeType() == NodeType.SERVICE_TOPOLOGY)
							unexploredNodes.add(n1);
					}
				}
			}
			unexploredNodes.remove(0);
		}

		return topologies;
	}
	public ArrayList<Node> getAllServiceUnits(){
		ArrayList<Node> units = new ArrayList<Node>();
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				if (n.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
					for (Node n1 : n
							.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
							unexploredNodes.add(n1);
					}
				}
			}else{
				 if (n.getNodeType()==NodeType.SERVICE_UNIT)
					 units.add(n);
			}
			unexploredNodes.remove(0);
		}

		return units;
	}
	
	public ArrayList<Node> getAllServiceTopologiesOfCloudService() {
		ArrayList<Node> topologies = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				topologies.add(n);
			}
		}
		return topologies;
	}
	public ArrayList<Node> getAllVMs() {
		ArrayList<Node> units = new ArrayList<Node>();
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodes()) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
				if (n.getAllRelatedNodes() != null) {
					for (Node n1 : n.getAllRelatedNodes() ){
							unexploredNodes.add(n1);
					}
				}
			
				 if (n.getNodeType()==NodeType.VIRTUAL_MACHINE)
					 units.add(n);
			
			unexploredNodes.remove(0);
		}

		return units;
	}
	public Node findParentNode(String entityID){
		Node parent = null;
		Node currentNode = getNodeWithID(entityID);
		for (Node n: currentNode.getAllRelatedNodes()){
			Relationship rel = currentNode.getRelationshipWithNode(n);
			if (rel.getTargetElement().equalsIgnoreCase(entityID))
				parent=n;
		}
		for (Node n:getAllServiceTopologies()){
			if (n.getRelatedNode(entityID)!=null ){
				Relationship rel =n.getRelationshipWithNode(currentNode);
				if (rel.getTargetElement().equalsIgnoreCase(entityID))
					parent=n;
			}
		}
		for (Node n:getAllServiceUnits()){
			if (n.getRelatedNode(entityID)!=null ){
				Relationship rel =n.getRelationshipWithNode(currentNode);
				if (rel.getTargetElement().equalsIgnoreCase(entityID))
					parent=n;
			}
		}
		return parent;
	}
	
			
	public Node getNodeWithID(String id) {
		if (cloudService.getId().equalsIgnoreCase(id)) return cloudService;
		List<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodes()) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			if (n.getId().equalsIgnoreCase(id))
				return n;
			else if (n.getRelatedNode(id) != null)
				return n.getRelatedNode(id);
			else
				unexploredNodes
						.addAll(n
								.getAllRelatedNodes());
			unexploredNodes.remove(0);

		}
		return null;
	}

	public Object getMetricForNode(String nodeID, String metricName) {
		Node currentNode = getNodeWithID(nodeID);
		return currentNode.getElasticityMetricValue(metricName);
	}

	

	public void setMetricForNode(String nodeID, String metricName,
			Object value, String unit) {
		Node currentNode = getNodeWithID(nodeID);
		if (currentNode.hasElasticityMetric(metricName))
			currentNode.setValueForElasticityMetric(metricName, value);
		else
			currentNode.addElasticityMetric(metricName, unit, value);
	}

	public void setMetricForNode(String nodeID, ElasticityMetric metric) {
		Node currentNode = getNodeWithID(nodeID);
		if (currentNode.getElasticityMetrics().contains(metric))
			currentNode.setValueForElasticityMetric(metric, metric.getValue());
		else
			currentNode.addElasticityMetric(metric);
	}

	public void setMetricForNode(String nodeID, ElasticityMetric metric,
			Object value) {
		Node currentNode = getNodeWithID(nodeID);
		if (currentNode.getElasticityMetrics().contains(metric))
			currentNode.setValueForElasticityMetric(metric, value);
		else
			currentNode.addElasticityMetric(metric);
	}
	public ArrayList<ElasticityRequirement> getAllElasticityRequirements(){
		ArrayList<ElasticityRequirement> elasticityRequirements= new ArrayList<ElasticityRequirement>();
		for (Node n:getAllServiceTopologies())
			elasticityRequirements.addAll(n.getElasticityRequirements());
		for (Node n:getAllServiceUnits())
			elasticityRequirements.addAll(n.getElasticityRequirements());
		return elasticityRequirements;
	}
	public String graphToString(){
		String message = "";
		
		
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		unexploredNodes.add(cloudService);
		
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			message+=n.toString();
			unexploredNodes
						.addAll(n
								.getAllRelatedNodes());
			unexploredNodes.remove(0);

		}
		return message;
	}
}