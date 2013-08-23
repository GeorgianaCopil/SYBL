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
package at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPlugins;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPluginInterface.EnforcementInterface;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.Configuration;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.RuntimeLogger;

public class OfferedEnforcementCapabilities {


	public static EnforcementInterface getInstance(Node cloudService){
		String className = Configuration.getEnforcementPlugin();
		//System.out.println(className);
		Class enforcementClass=null;
		try {
			enforcementClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor<?> cons=null;
		try {
			cons = enforcementClass.getConstructor(Node.class);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//RuntimeLogger.logger.info("Instantiating enforcement with constructor "+cons);
			return (EnforcementInterface) cons.newInstance(cloudService);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			RuntimeLogger.logger.error("Error when instantiating");

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		}
	
	
	
}
