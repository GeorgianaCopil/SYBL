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
package at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPlugins;

import java.lang.reflect.Constructor;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPluginInterface.MonitoringInterface;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.Configuration;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.RuntimeLogger;


public class OfferedMonitoredMetrics {


	public static MonitoringInterface getInstance(Node cloudService){
		String className = Configuration.getMonitoringPlugin();
		System.out.println(className);
		Class monitoringClass=null;
		try {
			monitoringClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor<?> cons=null;
		//RuntimeLogger.logger.error(monitoringClass);
		try {
			cons = monitoringClass.getConstructor();

			//RuntimeLogger.logger.info(cons);
			MonitoringInterface monitoring = (MonitoringInterface) cons.newInstance();
			return monitoring;
		} catch (Exception  e) {
			try{
				cons = monitoringClass.getConstructor(Node.class);

				//RuntimeLogger.logger.info(cons);
				MonitoringInterface monitoring = (MonitoringInterface) cons.newInstance(cloudService);
				return monitoring;

			}catch(Exception ex){
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error("Error on instantiation"+ex.toString());
			e.printStackTrace();}
		}
		return null;
	}

	
}
