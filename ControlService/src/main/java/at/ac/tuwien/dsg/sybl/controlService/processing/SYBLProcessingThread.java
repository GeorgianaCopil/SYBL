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
package at.ac.tuwien.dsg.sybl.controlService.processing;


import java.util.HashMap;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.DependencyGraph;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.controlService.utils.SYBLDirectivesEnforcementLogger;

import at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.SyblAPIService;





public class SYBLProcessingThread implements Runnable {
    public HashMap<Node,Boolean> cons = new HashMap<Node,Boolean>();
    SyblAPIService syblAPI ;
    Thread t;
	boolean ok = true;
	Utils utils ;
	long REFRESH_TIME=50000;
	Node currentEntity ;
	private DependencyGraph dependencyGraph;
    public SYBLProcessingThread(SYBLAnnotation syblAnnotation, Node ent, DependencyGraph dependencyGraph,SyblAPIService syblAPI){
    	this.dependencyGraph = dependencyGraph;
    	try {
		    currentEntity = ent;
		    this.syblAPI=syblAPI;
		} catch (Exception e) {
		    SYBLDirectivesEnforcementLogger.logger.error("Client exception rmiSYBLRuntime: " + e.toString());
		    e.printStackTrace();
		}

    	utils = new Utils(currentEntity,syblAnnotation.getPriorities(),syblAnnotation.getMonitoring(),syblAnnotation.getConstraints(),syblAnnotation.getStrategies(),syblAPI,dependencyGraph);
		t = new Thread(this);	
		
	}
    
	public void stop(){
		ok = false;
		try{
		t.stop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void singleRun(){
		utils.processSyblSpecifications();
	}
	@Override
	public void run() {
		while (ok){
			utils.processSyblSpecifications();
			utils.clearDisabledRules();
		
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public void start(){
		t.start();
	}
}
