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
package at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPluginInterface;

import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;

import at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPluginInterface.MonitoringInterface;

public interface EnforcementInterface {
public void scaleOut(Node toBeScaled);
public void scaleIn(Node toBeScaled);
public List<String> getElasticityCapabilities();
public void enforceAction(Node actionName, Node entity);
public void setControlledService(Node controlledService);
public Node getControlledService();
public void setMonitoringPlugin(MonitoringInterface monitoring);
}
