
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

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
@WebService(serviceName="SyblAPIService")//, endpointInterface ="at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeAPI.SYBLAPIInterface")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)

public class SyblAPIService implements SYBLAPIInterface{
    private SyblAPI syblAPI;
	public SyblAPIService(){

	     syblAPI=new SyblAPI();
	}
	@WebMethod
	public void setControlledService(Node cloudService){
		syblAPI.setControlledService(cloudService);
	}
	
	@WebMethod
	public Float getCurrentCPUSize(Node e) {
		return syblAPI.getCurrentCPUSize(e);
	}
	@WebMethod
	public Float getCostPerHour(Node e) {
		return syblAPI.getCostPerHour(e);

	}
	@WebMethod
	public Float getCurrentRAMSize(Node e) {
		return syblAPI.getCurrentRAMSize(e);

	}
	@WebMethod
	public String Test() {
		return "Working";
	}
	@WebMethod
	public Float getCurrentMemUsage(Node e) {
		return syblAPI.getCurrentMemUsage(e);

	}

	@WebMethod
	public Float getTotalCostSoFar(Node e) {
		return syblAPI.getTotalCostSoFar(e);

	}

	@WebMethod
	public Node getControlledService() {
		return syblAPI.getControlledService();

	}
	@WebMethod
	public Float getCurrentReadLatency(Node e) {

		return syblAPI.getCurrentReadLatency(e);

	}
	@WebMethod
	public Float getCurrentReadCount(Node e) {
		return syblAPI.getCurrentReadCount(e);

	}
	@WebMethod
	public Float getCurrentWriteLatency(Node e) {
		return syblAPI.getCurrentWriteLatency(e);

	}
	@WebMethod
	public Float getCurrentWriteCount(Node e) {
		return syblAPI.getCurrentWriteCount(e);

	}
	@WebMethod
	public Float getCurrentCPUUsage(Node e) {
		//RuntimeLogger.logger.info("Current cpu usage"+ syblAPI.getCurrentCPUUsage(e)+"for entity "+e.getId());
		return syblAPI.getCurrentCPUUsage(e);

	}
	@WebMethod
	public Float getCurrentHDDSize(Node e) {
		return syblAPI.getCurrentHDDSize(e);

	}
	@WebMethod
	public void scalein(Node e) {
	//	RuntimeLogger.logger.info("SYBL Service scaling in "+e.getId());
		//syblAPI.scalein(e);
		
	}
	@WebMethod
	public void scaleout(Node e) {
		//RuntimeLogger.logger.info("SYBL Service scaling out "+e.getId());
		//syblAPI.scaleout(e);
		
	}
	@WebMethod
	public Float getCurrentLatency(Node e) {
		
		return syblAPI.getCurrentLatency(e);

	}
	@WebMethod
	public Float getCurrentOperationCount(Node e) {
		return syblAPI.getCurrentOperationCount(e);

	}
	@WebMethod
	public Float getCurrentHDDUsage(Node e) {
		return syblAPI.getCurrentHDDUsage(e);

	}
	@WebMethod
	public String getLatestMonitoringData(){
		return syblAPI.getLatestMonitoringData();
	}
	@WebMethod
	public Float getMetricValue(String metricName, Node e) {
		return syblAPI.getMetricValue(metricName, e);
	}
	@WebMethod
	public void enforceAction(Node actionName, Node e) {
		syblAPI.enforceAction(actionName, e);
	}
	@WebMethod
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		syblAPI.submitElasticityRequirements(description);
	}
 
}
