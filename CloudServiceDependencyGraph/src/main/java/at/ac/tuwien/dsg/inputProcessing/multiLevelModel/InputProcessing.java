package at.ac.tuwien.dsg.inputProcessing.multiLevelModel;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.DependencyGraph;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.GraphLogger;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML.SYBLAnnotationXML;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML.ActionXML;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentUnit;

import at.ac.tuwien.dsg.utils.Configuration;
import at.ac.tuwien.dsg.utils.DependencyGraphLogger;




public class InputProcessing {
	private  SYBLElasticityRequirementsDescription syblSpecifications;
	private DeploymentDescription deploymentDescription;
	private CloudServiceXML cloudServiceXML;
	private void loadDeploymentDescription(){
		try {		
			//load deployment description and populate the dependency graph
			
			JAXBContext a = JAXBContext.newInstance( DeploymentDescription.class );
			Unmarshaller u  = a.createUnmarshaller();
			String deploymentDescriptionPath = Configuration.getDeploymentDescriptionPath();
		//	RuntimeLogger.logger.info("Got here "+deploymentDescriptionPath);
		//	RuntimeLogger.logger.info("Got here "+this.getClass().getClassLoader().getResourceAsStream(deploymentDescriptionPath));

			if (deploymentDescriptionPath!=null)
		     deploymentDescription = (DeploymentDescription) u.unmarshal( InputProcessing.class.getClassLoader().getResourceAsStream(deploymentDescriptionPath)) ;

			//RuntimeLogger.logger.info("Read deployment Descrption"+deploymentDescription.toString());

		} catch (Exception e) {
			DependencyGraphLogger.logger.error("Error in reading deployment description"+e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void loadModel(){
		 JAXBContext jc;
		    cloudServiceXML = null;
		try {			
			jc = JAXBContext.newInstance( CloudServiceXML.class );
			Unmarshaller u = jc.createUnmarshaller();
			


			//JAXBElement element=  u.unmarshal( new File(Configuration.getModelDescrFile()));
			// cloudS = (CloudServiceXML) element.getValue();
			 cloudServiceXML = (CloudServiceXML) u.unmarshal(InputProcessing.class.getClassLoader().getResourceAsStream(Configuration.getModelDescrFile()));

			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//populate ips for above levels
		//populateIps();
		
			try {			
				JAXBContext a = JAXBContext.newInstance( SYBLElasticityRequirementsDescription.class );
				Unmarshaller u  = a.createUnmarshaller();
				String directivePath = Configuration.getDirectivesPath();
				if (directivePath!=null)
			     syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal( this.getClass().getClassLoader().getResourceAsStream(directivePath)) ;
	
				for (SYBLAnnotation syblAnnotation:parseXMLInjectedAnnotations(cloudServiceXML)){
					if (syblSpecifications==null)
						syblSpecifications=new SYBLElasticityRequirementsDescription();
					//SYBLDirectivesEnforcementLogger.logger.info("FOUND HERE THE STRATEGY "+syblAnnotation.getStrategies());
					//if (syblAnnotation.getStrategies()!=null && syblAnnotation.getStrategies()!="")
			    	syblSpecifications.getSyblSpecifications().add(SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(syblAnnotation));
			    }
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	public DependencyGraph loadDependencyGraph(){
		DependencyGraph graph = new DependencyGraph();
		Node cloudService = new Node();
		loadModel();
		loadDeploymentDescription();
		cloudService.setId(cloudServiceXML.getId());
		cloudService.setNodeType(NodeType.CLOUD_SERVICE);
		if (cloudServiceXML.getAnnotation()!=null){
		ElasticityRequirement elReq = new ElasticityRequirement();
		elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(cloudService.getId(),cloudServiceXML.getXMLAnnotation(),cloudServiceXML.getAnnotation().getAnnotationType()));
		}
		List<ServiceTopologyXML> remainingServiceTopologies = new ArrayList<ServiceTopologyXML>();
		Node parent = cloudService;
		HashMap<String,Node> nodes = new HashMap<String,Node>();
		remainingServiceTopologies.add(cloudServiceXML.getServiceTopology());
		ServiceTopologyXML firstServTopology = cloudServiceXML.getServiceTopology();
		Node serviceTopologyFirst = new Node();
		serviceTopologyFirst.setId(firstServTopology.getId());
		serviceTopologyFirst.setNodeType(NodeType.SERVICE_TOPOLOGY);
		 Relationship rel = new Relationship();
		rel.setSourceElement(cloudService.getId());
		rel.setTargetElement(serviceTopologyFirst.getId());
		rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
		if (firstServTopology.getAnnotation()!=null){
			ElasticityRequirement elReq = new ElasticityRequirement();
			elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyFirst.getId(),firstServTopology.getXMLAnnotation(),firstServTopology.getAnnotation().getAnnotationType()));
			serviceTopologyFirst.getElasticityRequirements().add(elReq);
		}
		nodes.put(serviceTopologyFirst.getId(),serviceTopologyFirst);
		nodes.put(cloudService.getId(), cloudService);
		while (!remainingServiceTopologies.isEmpty()){
			ServiceTopologyXML serviceTopologyXML=remainingServiceTopologies.get(0);
			Node serviceTopology  = new Node();
			if (nodes.containsKey(serviceTopologyXML.getId()))
			 serviceTopology = nodes.get(serviceTopologyXML.getId()); 
		
			if (serviceTopologyXML.getServiceUnits()!=null && !serviceTopologyXML.getServiceUnits().isEmpty())
				for (ServiceUnitXML serviceUnitXML:serviceTopologyXML.getServiceUnits()){
					Node serviceUnit = new Node();
					serviceUnit.setId(serviceUnitXML.getId());
					serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
					  rel = new Relationship();
					rel.setSourceElement(serviceTopology.getId());
					rel.setTargetElement(serviceUnit.getId());
					rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
					if (serviceUnitXML.getAnnotation()!=null){
						ElasticityRequirement elReq = new ElasticityRequirement();
						elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceUnitXML.getId(),serviceUnitXML.getXMLAnnotation(),serviceUnitXML.getAnnotation().getAnnotationType()));
						serviceUnit.getElasticityRequirements().add(elReq);
					}
					
					if (serviceUnitXML.getActions()!=null && !serviceUnitXML.getActions().isEmpty()){
					for (ActionXML actionXML : serviceUnitXML.getActions()){
						ElasticityCapability elCapability = new ElasticityCapability();
						elCapability.setApiMethod(actionXML.getApiMethod());
						elCapability.setName(actionXML.getName());
						elCapability.setParameter(actionXML.getParameter());
						elCapability.setValue(actionXML.getValue());
						serviceUnit.addElasticityCapability(elCapability);
					}
					
				}
					serviceTopology.addNode(serviceUnit, rel);
					nodes.put(serviceUnit.getId(), serviceUnit);

				}
			if (serviceTopologyXML.getServiceTopology()!=null && !serviceTopologyXML.getServiceTopology().isEmpty())
				for (ServiceTopologyXML serviceTopologyXML2:serviceTopologyXML.getServiceTopology()){
					Node serviceTopology2 = new Node();
					serviceTopology2.setId(serviceTopologyXML2.getId());
					serviceTopology2.setNodeType(NodeType.SERVICE_TOPOLOGY);
					 rel = new Relationship();
					rel.setSourceElement(serviceTopology.getId());
					rel.setTargetElement(serviceTopology2.getId());
					rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
					if (serviceTopologyXML2.getAnnotation()!=null){
						ElasticityRequirement elReq = new ElasticityRequirement();
						elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyXML2.getId(),serviceTopologyXML2.getXMLAnnotation(),serviceTopologyXML2.getAnnotation().getAnnotationType()));
						serviceTopology2.getElasticityRequirements().add(elReq);
					}
					serviceTopology.addNode(serviceTopology2, rel);
					nodes.put(serviceTopology2.getId(), serviceTopology2);
					remainingServiceTopologies.add(serviceTopologyXML2);
					//GraphLogger.logger.info("Added service topology "+serviceTopology2.getId()+" to service topology "+serviceTopology);
				}
			nodes.put(serviceTopology.getId(), serviceTopology);
			remainingServiceTopologies.remove(0);
		}
		cloudService.addNode(serviceTopologyFirst, rel);
	
		graph.setCloudService(cloudService);
		
		//Populate with deployment information
		for (DeploymentUnit deploymentUnit :deploymentDescription.getDeployments()){
			
			Node string = graph.getNodeWithID(deploymentUnit.getServiceUnitID());
			if (string!=null){
			string.getStaticInformation().put("DefaultFlavor", deploymentUnit.getDefaultFlavor());
			string.getStaticInformation().put("DefaultImage", deploymentUnit.getDefaultImage());
			}else{
				GraphLogger.logger.error("Cannot find node "+deploymentUnit.getServiceUnitID()+". Current graph is "+graph.graphToString());
				
			}
		}
		
		//Populate with elasticity requirements information
		for (SYBLSpecification specification: syblSpecifications.getSyblSpecifications()){
			ElasticityRequirement elRequirement = new ElasticityRequirement();
			elRequirement.setAnnotation(SYBLDirectiveMappingFromXML.mapFromXMLRepresentation(specification));
			if (graph.getNodeWithID(specification.getComponentId())!=null)
			graph.getNodeWithID(specification.getComponentId()).addElasticityRequirement(elRequirement);
			else
				GraphLogger.logger.error("Specification targets entity which is not found: "+specification.getComponentId());
		}
		
		return graph;
	}
	
	public SYBLAnnotation mapFromXMLAnnotationToSYBLAnnotation(String entityID,SYBLAnnotationXML syblAnnotationXML,SYBLAnnotation.AnnotationType annotationType){
		SYBLAnnotation syblannotation = new SYBLAnnotation();

			syblannotation=new SYBLAnnotation();
			syblannotation.setPriorities(syblAnnotationXML.getPriorities());
			syblannotation.setConstraints(syblAnnotationXML.getConstraints());
			syblannotation.setStrategies(syblAnnotationXML.getStrategies());
			syblannotation.setMonitoring(syblAnnotationXML.getMonitoring());
			syblannotation.setEntityID(entityID);
			syblannotation.setAnnotationType(annotationType);
		return syblannotation;
	}
	public List<SYBLAnnotation> parseXMLInjectedAnnotations(CloudServiceXML cloudService){
		boolean found=false;
		List<SYBLAnnotation> annotations = new ArrayList<SYBLAnnotation>();
		if (cloudService.getXMLAnnotation()!=null )
			annotations.add(mapFromXMLAnnotationToSYBLAnnotation(cloudService.getId(), cloudService.getXMLAnnotation(), SYBLAnnotation.AnnotationType.CLOUD_SERVICE));
		
		ServiceTopologyXML topology =  cloudService.getServiceTopology();
	
		if (topology.getXMLAnnotation()!=null )
			annotations.add(mapFromXMLAnnotationToSYBLAnnotation(topology.getId(), topology.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_TOPOLOGY));
	
		List<ServiceTopologyXML> topologies = new ArrayList<ServiceTopologyXML>();
		
		topologies.addAll(topology.getServiceTopology());
		
		List<ServiceUnitXML> componentsToExplore = new ArrayList<ServiceUnitXML>();
		if (topology.getServiceUnits()!=null)
		componentsToExplore.addAll(topology.getServiceUnits());
		while (!found && !topologies.isEmpty()){
			ServiceTopologyXML currentTopology = topologies.get(0);
			topologies.remove(0);
			if (currentTopology.getXMLAnnotation()!=null )
				annotations.add(mapFromXMLAnnotationToSYBLAnnotation(currentTopology.getId(), currentTopology.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_TOPOLOGY));
		
				if (currentTopology.getServiceTopology()!=null && currentTopology.getServiceTopology().size()>0)
					topologies.addAll(currentTopology.getServiceTopology());
				if (currentTopology.getServiceUnits()!=null && currentTopology.getServiceUnits().size()>0)
					componentsToExplore.addAll(currentTopology.getServiceUnits());
		}
		
		while (!found && !componentsToExplore.isEmpty()){
			ServiceUnitXML component =componentsToExplore.get(0);
			
			if (component.getXMLAnnotation()!=null )
				annotations.add(mapFromXMLAnnotationToSYBLAnnotation(component.getId(), component.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_UNIT));
		
			componentsToExplore.remove(0);
			
		}
		
		return annotations;
	}
	
}
