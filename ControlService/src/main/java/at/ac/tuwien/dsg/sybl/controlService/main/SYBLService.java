/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.dsg.sybl.controlService.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.DependencyGraph;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node.NodeType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.controlService.processing.SYBLProcessingThread;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.SyblAPIService;


public class SYBLService {

	private HashMap<String,Node> nodes = new HashMap<String,Node>();
	private HashMap<Node,SYBLProcessingThread> myProcessingThreads = new HashMap<Node,SYBLProcessingThread>() ;
	private DependencyGraph dependencyGraph ;
	private SyblAPIService syblAPI;
	
	public SYBLService(DependencyGraph dependencyGraph,SyblAPIService syblAPI){
		    this.dependencyGraph=dependencyGraph;
		    this.syblAPI=syblAPI;
		    
			initializeEntities();
			
	}

	public void initializeEntities(){
		//Initialize the entities without the annotations
		nodes.put(dependencyGraph.getCloudService().getId(), dependencyGraph.getCloudService());
		Node topology =  dependencyGraph.getCloudService().getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);
		
		nodes.put(topology.getId(), topology);

			List<Node> topologies = new ArrayList<Node>();
			topologies.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
			
			List<Node> componentsToExplore = new ArrayList<Node>();
			if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null)
			componentsToExplore.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
			while (!topologies.isEmpty()){
				Node currentTopology = topologies.get(0);
				nodes.put(currentTopology.getId(), currentTopology);
				topologies.remove(0);
					if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)!=null) 
						if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).size()>0)
						topologies.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
					if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null) 
						if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT).size()>0)
						componentsToExplore.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
				
			}
			List<Node> codeRegions = new ArrayList<Node>();
			while (!componentsToExplore.isEmpty() ){
				Node component =componentsToExplore.get(0);
				nodes.put(component.getId(), component);
				
				if (component.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.CODE_REGION)!=null)
				codeRegions.addAll(component.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.CODE_REGION));
				componentsToExplore.remove(0);
				
			}
			
			while (!codeRegions.isEmpty()){
				Node codeRegion =codeRegions.get(0);
				nodes.put(codeRegion.getId(), codeRegion);
				codeRegions.remove(0);
				
			}
			
		
	}

	public void processAnnotations( String componentID, SYBLAnnotation syblAnnotation){
		if (! myProcessingThreads.containsKey(componentID)){
			Node e = nodes.get(componentID); 
		 ElasticityRequirement elasticityRequirement = new ElasticityRequirement();
		 elasticityRequirement.setAnnotation(syblAnnotation);
		 e.addElasticityRequirement(elasticityRequirement);
		 SYBLProcessingThread p = new SYBLProcessingThread(syblAnnotation, e, dependencyGraph,syblAPI);
		 p.start();
		}
	
	}
	public boolean checkIfContained(Node componentId){
		if (myProcessingThreads.containsKey(componentId))return true;
		else return false;
	}
	public Node getControlledService() {
		return dependencyGraph.getCloudService();
		
	}
	public void setControlledService(Node controlledService) {
		dependencyGraph.setCloudService(controlledService) ;
	}
	
}
