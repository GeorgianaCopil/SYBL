package at.ac.tuwien.dsg.inputProcessing.tosca;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.Definitions;
import org.oasis_open.docs.tosca.ns._2011._12.TExtensibleElements;
import org.oasis_open.docs.tosca.ns._2011._12.TDefinitions.Extensions;
import org.oasis_open.docs.tosca.ns._2011._12.TDefinitions.Types;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TServiceTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TTopologyTemplate;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.DependencyGraph;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.utils.Configuration;

public class TOSCAProcessing {

	public Definitions readTOSCADescriptionsFile(){
		   
		try {	 
			 
			 JAXBContext a = JAXBContext.newInstance( Definitions.class );
			 Unmarshaller u  = a.createUnmarshaller();
			    Definitions def = (Definitions) u.unmarshal( InputProcessing.class.getClassLoader().getResourceAsStream(Configuration.getCloudServiceTOSCADescription())) ;

			    return def;			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

	}
	public HashMap<String,Node> parseTOSCAGraph(HashMap<String,Node> nodes,List<TExtensibleElements> currentElements,String parentID){
		for (TExtensibleElements extensibleElements: currentElements){
			if (extensibleElements instanceof TServiceTemplate){
				Node n = new Node();
				TServiceTemplate serviceTemplate = (TServiceTemplate)extensibleElements;
				n.setId(serviceTemplate.getId());
				n.setNodeType(NodeType.CLOUD_SERVICE);
				
				Node topology = new Node();
				
				topology.setId(serviceTemplate.getTopologyTemplate().getOtherAttributes().get(new QName("id")));
				
				topology.setNodeType(NodeType.SERVICE_TOPOLOGY);
				Relationship rel  = new Relationship();
				rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
				rel.setSourceElement(n.getId());
				rel.setTargetElement(topology.getId());
				n.addNode(topology,rel );
				nodes.put(topology.getId(), topology);
				nodes.put(((TServiceTemplate) extensibleElements).getId(), n);
				List<TExtensibleElements> c = new ArrayList<TExtensibleElements>();
				c.add(serviceTemplate.getTopologyTemplate());
				return parseTOSCAGraph(nodes, c, n.getId());
			}
			if (extensibleElements instanceof TTopologyTemplate){
				Node serviceTopology = null;
				
				TTopologyTemplate topologyTemplate = (TTopologyTemplate)extensibleElements;
				
				if (nodes.containsKey(topologyTemplate.getOtherAttributes().get(new QName("id")))){
					serviceTopology=nodes.get(topologyTemplate.getOtherAttributes().get(new QName("id")));
				}else{
					serviceTopology = new Node();
					serviceTopology.setId(topologyTemplate.getOtherAttributes().get(new QName("id")));
					serviceTopology.setNodeType(NodeType.SERVICE_TOPOLOGY);

				}
				List<TExtensibleElements> c = new ArrayList<TExtensibleElements>();
				for (TExtensibleElements tExt:topologyTemplate.getNodeTemplateOrRelationshipTemplate()){
					if (tExt instanceof TNodeTemplate){
						TNodeTemplate nodeTemplate =(TNodeTemplate)tExt;
						Node serviceUnit=null;
						if (nodes.containsKey(nodeTemplate.getId())){
							serviceUnit=nodes.get(nodeTemplate.getId());
						}else{
							serviceUnit = new Node();
							serviceUnit.setId(nodeTemplate.getId());
							serviceUnit.setNodeType(NodeType.SERVICE_UNIT);

						}
						serviceUnit.setId(nodeTemplate.getId());
						serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
						Relationship rel  = new Relationship();
						rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
						serviceTopology.addNode(serviceUnit,rel );
						nodes.put(serviceUnit.getId(), serviceUnit);
						
					}else{
						if (tExt instanceof TTopologyTemplate){
							TTopologyTemplate topTemplate =(TTopologyTemplate)tExt;
							Node serviceUnit=null;
							if (nodes.containsKey(topTemplate.getOtherAttributes().get(new QName("id")))){
								serviceUnit=nodes.get(topTemplate.getOtherAttributes().get(new QName("id")));
							}else{
								serviceUnit = new Node();
								serviceUnit.setId(topTemplate.getOtherAttributes().get(new QName("id")));
								serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);

							}
							serviceUnit.setId(topTemplate.getOtherAttributes().get(new QName("id")));
							serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);
							Relationship rel  = new Relationship();
							rel.setSourceElement(serviceTopology.getId());
							rel.setTargetElement(serviceUnit.getId());
							rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
							serviceTopology.addNode(serviceUnit,rel );
							nodes.put(serviceUnit.getId(), serviceUnit);
						}else{
							if (tExt instanceof TRelationshipTemplate){
								TRelationshipTemplate relationship = (TRelationshipTemplate) tExt;
							
							}
						}
					}
					
					
					
				
				}
				nodes.put(serviceTopology.getId(), serviceTopology);

			}
			if (extensibleElements instanceof TNodeTemplate){
				
				
			}
		}
		return nodes;
	}
	public DependencyGraph toscaDescriptionToDependencyGraph(){
		DependencyGraph dependencyGraph = new DependencyGraph();
		HashMap<String,Node> nodes = new HashMap<String,Node>();//String - id of the node, for easier access and modification of its relationships
		Relationship rel = new Relationship();
		//TODO: take each construct present in TOSCA and transform it to our model
		Definitions definitions = readTOSCADescriptionsFile();
		parseTOSCAGraph(nodes, definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation(), "");
		for (Node n:nodes.values())
			if (n.getNodeType()==NodeType.CLOUD_SERVICE)
				dependencyGraph.setCloudService(n);
		return dependencyGraph;
	}

}
