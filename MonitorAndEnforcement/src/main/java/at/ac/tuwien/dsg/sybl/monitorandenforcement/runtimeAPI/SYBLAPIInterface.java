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
package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeAPI;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;


public interface SYBLAPIInterface {
    
	public Float getCurrentCPUSize(Node e)  ;
    
	public Float getCostPerHour(Node e)  ;
    	
	public Float getCurrentRAMSize(Node e) ;
    
   
    public Float getCurrentMemUsage(Node e);
    
	public Float getTotalCostSoFar(Node e) ;

    
	public Node getControlledService() ;
    
	public void setControlledService(Node controlledService) ;
    
	public Float getCurrentReadLatency(Node e)  ;
    
	public Float getCurrentReadCount(Node e)  ;
    
    public Float getCurrentWriteLatency(Node e)  ;
    
	public Float getCurrentWriteCount(Node e)  ;
    public Float getMetricValue(String metricName, Node e);
	public Float getCurrentCPUUsage(Node e) ;
	public String getLatestMonitoringData();

    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description);
	public Float getCurrentHDDSize(Node e)  ; 
    
	public void scalein(Node arg0);

    
	public void scaleout(Node arg0);
    	
	public Float getCurrentLatency(Node arg0)  ;

    
	public Float getCurrentOperationCount(Node arg0);

    
	public Float getCurrentHDDUsage(Node e) ;
    public void enforceAction(Node actionName, Node e);

}
