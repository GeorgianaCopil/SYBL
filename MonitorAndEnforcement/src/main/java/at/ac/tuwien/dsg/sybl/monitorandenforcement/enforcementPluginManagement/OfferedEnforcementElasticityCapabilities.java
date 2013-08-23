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
package at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPluginManagement;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPluginInterface.EnforcementInterface;

public class OfferedEnforcementElasticityCapabilities {
	ManageEnforcementPlugins manageEnforcementPlugins = new ManageEnforcementPlugins();
	Node cloudService;
	public OfferedEnforcementElasticityCapabilities(Node cloudService){
		this.cloudService=cloudService;
	}
	public void callSpecializedMethod(Node method, Node parameter){
		
	}
	public void scaleOut(Node entity){
		Map<Method,EnforcementInterface> methods = manageEnforcementPlugins.getMethods(manageEnforcementPlugins.getAllPlugins(cloudService));
		for (Method method:methods.keySet()){
			System.out.println(method.getName());
			if (method.getName().equalsIgnoreCase("scaleOut")){
				methods.get(method).scaleOut(entity);
			}
		}
	}
	public void scaleIn(Node entity){
		Map<Method,EnforcementInterface> methods = manageEnforcementPlugins.getMethods(manageEnforcementPlugins.getAllPlugins(cloudService));
		for (Method method:methods.keySet()){
			//System.out.println(method.getName());
			if (method.getName().equalsIgnoreCase("scaleIn")){
				methods.get(method).scaleIn(entity);
			}
		}
	}
}