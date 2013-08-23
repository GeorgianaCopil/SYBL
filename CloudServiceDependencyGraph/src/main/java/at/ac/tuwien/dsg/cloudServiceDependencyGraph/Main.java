package at.ac.tuwien.dsg.cloudServiceDependencyGraph;

import java.util.ArrayList;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.inputProcessing.tosca.TOSCAProcessing;


public class Main {

		//return root node
	public Node  constructExampleDependencyGraph(){
		Node rootCloudService = new Node();
	rootCloudService.setId("CloudService");
	rootCloudService.setNodeType(NodeType.CLOUD_SERVICE);
	
	Node serviceTopology = new Node();
	Relationship relationship = new Relationship();
	relationship.setSourceElement(rootCloudService.getId());
	relationship.setTargetElement(serviceTopology.getId());
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	serviceTopology.setId("DataServiceTopology");
	serviceTopology.setNodeType(NodeType.SERVICE_TOPOLOGY);
	ArrayList<ElasticityRequirement> elasticityRequirements = new ArrayList<ElasticityRequirement>();
	ElasticityRequirement elasticityRequirement = new ElasticityRequirement();
	SYBLAnnotation annotation = new SYBLAnnotation();
	annotation.setConstraints("Co1: CONSTRAINT cpu.usage>20%");
	annotation.setStrategies("St1: STRATEGY minimize(cost)");
	elasticityRequirement.setAnnotation(annotation);
	elasticityRequirements.add(elasticityRequirement);
	serviceTopology.setElasticityRequirements(elasticityRequirements);
	rootCloudService.addNode(serviceTopology, relationship);
	
	Node serviceUnit = new Node();
	relationship.setSourceElement(rootCloudService.getId());
	relationship.setTargetElement(serviceTopology.getId());
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	serviceUnit.setId("CassandraDB");
	serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);
	elasticityRequirements = new ArrayList<ElasticityRequirement>();
	elasticityRequirement = new ElasticityRequirement();
	annotation = new SYBLAnnotation();
	annotation.setConstraints("Co1: CONSTRAINT cpu.usage>20%");
	annotation.setStrategies("St1: STRATEGY minimize(cost)");
	elasticityRequirement.setAnnotation(annotation);
	elasticityRequirements.add(elasticityRequirement);
	serviceUnit.setElasticityRequirements(elasticityRequirements);

	
		
	Node virtualMachine1 = new Node();
	Node virtualMachine2 = new Node();
	Node virtualMachine3 = new Node();
	virtualMachine1.setId("VM1");
	virtualMachine2.setId("VM2");
	virtualMachine3.setId("VM3");
	Node virtualCluster = new Node();
	relationship = new Relationship();
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	relationship.setSourceElement(virtualCluster.getId());
	relationship.setTargetElement(virtualMachine1.getId());	
	virtualCluster.addNode(virtualMachine1, relationship);
	virtualCluster.setId("VirtualCluster1");
	relationship = new Relationship();
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	relationship.setSourceElement(virtualCluster.getId());
	relationship.setTargetElement(virtualMachine2.getId());
	virtualCluster.addNode(virtualMachine2, relationship);
	relationship = new Relationship();
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	relationship.setSourceElement(virtualCluster.getId());
	relationship.setTargetElement(virtualMachine3.getId());
	virtualCluster.addNode(virtualMachine3, relationship);
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualCluster.getId());
	serviceUnit.addNode(virtualCluster, relationship);
	
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualMachine1.getId());
	serviceUnit.addNode(virtualMachine1, relationship);
	
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualMachine2.getId());
	serviceUnit.addNode(virtualMachine2, relationship);
	
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualMachine3.getId());
	serviceUnit.addNode(virtualMachine3, relationship);
	serviceTopology.addNode(serviceUnit, relationship);
	return rootCloudService;
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		Node cloudService = m.constructExampleDependencyGraph();
		
		DependencyGraph dependencyGraph = new DependencyGraph();
		dependencyGraph.setCloudService(cloudService);
		
		//System.out.println(dependencyGraph.graphToString());
		DependencyGraph fromTosca = new TOSCAProcessing().toscaDescriptionToDependencyGraph();
		//System.out.println(fromTosca.graphToString());
		
		InputProcessing inputProcessing=new InputProcessing();
		System.out.println(inputProcessing.loadDependencyGraph().graphToString());
	}
	
	}
