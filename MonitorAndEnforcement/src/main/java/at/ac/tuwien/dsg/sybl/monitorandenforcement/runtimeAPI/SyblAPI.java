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
*/package at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeAPI;


import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPluginInterface.EnforcementInterface;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPlugins.OfferedEnforcementCapabilities;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPluginInterface.MonitoringInterface;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPlugins.OfferedMonitoredMetrics;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.RuntimeLogger;


public class SyblAPI implements SYBLAPIInterface{
	private  static HashMap<Node, ArrayList<Float>> avgRunningTimes = new HashMap<Node,ArrayList<Float>>();
	
	private boolean executingControlAction = false;
	
    private Node controlledService;
    private EnforcementInterface offeredCapabilities;
    private MonitoringInterface  offeredMonitoringMetrics;
	public SyblAPI(){
		
	}
	

	
	
	public Float getCurrentCPUSize(Node e)  {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	try{
	return offeredMonitoringMetrics.getCPUSpeed(e);
	}catch(Exception ex){
		RuntimeLogger.logger.error("In get current cpu" + ex.toString());
		return 0.0f;
	}
	}
	
	public Float getCostPerHour(Node e)  {

		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getCostPerHour(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error("In get cost per hour "+ ex.toString());
			return 0.0f;
		}	
	
	}
	
	public Float getCurrentRAMSize(Node e) {
		
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getMemorySize(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(" In get ram size "+ex.toString());
			return 0.0f;
		}
}
	
	public String Test() {
		
		return "TEST Working";
	}
	
	public Float getCurrentMemUsage(Node e) {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getMemoryUsage(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error("In get memory usage "+ex.toString());
			return 0.0f;
		}
	}
	
	
	public Float getTotalCostSoFar(Node e) {
	  return offeredMonitoringMetrics.getTotalCostSoFar(e);
	}


	public Node getControlledService() {
		//RuntimeLogger.logger.info("Returning cloud service"+controlledService);
		//RuntimeLogger.logger.info("example of component" + controlledService.getComponentTopology().getComponentTopology().get(0).getComponents().get(0).getAssociatedIps());

		return controlledService;
	}

	
	public void setControlledService(Node controlledService) {
		//RuntimeLogger.logger.info("ID of the controlled service is "+controlledService.getId());
//		RuntimeLogger.logger.info("example of component" + controlledService.getComponentTopology().getComponentTopology().get(0).getComponents().get(0).getAssociatedIps());

		this.controlledService = controlledService;
		controlledService.getAllRelatedNodes();
	  RuntimeLogger.logger.info("Set the service "+ controlledService.toString());
		offeredCapabilities = OfferedEnforcementCapabilities.getInstance(this.controlledService);
		//System.err.println("AAAAAAAAAAAAAAAAAAAAAAAAA"+offeredCapabilities);
		offeredMonitoringMetrics  = OfferedMonitoredMetrics.getInstance(this.controlledService);
		offeredMonitoringMetrics.submitServiceConfiguration(this.controlledService);
		offeredCapabilities.setMonitoringPlugin(offeredMonitoringMetrics);
	}
	
	
	
	
	public Float getCurrentReadLatency(Node e)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
	return offeredMonitoringMetrics.getReadLatency(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
		}
	
	public Float getCurrentReadCount(Node e)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getReadCount(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
		}
	
	public Float getCurrentWriteLatency(Node e)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getWriteLatency(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
		}
	
	public Float getCurrentWriteCount(Node e)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getWriteCount(e);	
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
		}

	
	public Float getCurrentCPUUsage(Node e)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	//	System.err.println("Cpu usage method for entity "+e.getId()+" ips"+e.getAssociatedIps().size());
		//RuntimeLogger.logger.info("At cpu usage"+offeredMonitoringMetrics);
try{
		return offeredMonitoringMetrics.getCpuUsage(e);
}catch(Exception ex){
	RuntimeLogger.logger.error(ex.toString());
	return 0.0f;
}
	}


	
	public Float getCurrentHDDSize(Node e)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getDiskSize(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
	}

	

	
	
	
	public Float getCurrentLatency(Node arg0)   {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
			float returnedLatency = 0.0f;
			if (!getCurrentReadLatency(arg0).isNaN() && !getCurrentWriteLatency(arg0).isNaN())
				returnedLatency= (getCurrentReadLatency(arg0)+getCurrentWriteLatency(arg0))/2;
			if (getCurrentReadLatency(arg0).isNaN())
				if (getCurrentWriteLatency(arg0).isNaN())
					returnedLatency =0.0f;
				else returnedLatency= getCurrentWriteLatency(arg0);
			else
				returnedLatency= getCurrentReadLatency(arg0);
			//RuntimeLogger.logger.info("Current latency for entity "+arg0.getId()+" is "+returnedLatency);

			return returnedLatency;
		}catch (Exception ex){
			return 0.0f;
		}	
		
	}


	public String getLatestMonitoringData(){
		return offeredMonitoringMetrics.getLatestMonitoringData();
	}
	public Float getCurrentOperationCount(Node arg0){
		return (getCurrentReadCount(arg0)+getCurrentWriteCount(arg0));

	}



	
	public Float getCurrentHDDUsage(Node e) {
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getDiskUsage(e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
	}
	public Float getMetricValue(String metricName, Node e){
		if (isExecutingControlAction())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try{
		return offeredMonitoringMetrics.getMetricValue(metricName, e);
		}catch(Exception ex){
			RuntimeLogger.logger.error(ex.toString());
			return 0.0f;
		}
	}
	public boolean isExecutingControlAction() {
		return executingControlAction;
	}



	public void scalein(Node arg0) {
		executingControlAction=true;
		offeredMonitoringMetrics.notifyControlActionStarted("scaleIn",arg0);
		offeredCapabilities.scaleIn(arg0);
		offeredMonitoringMetrics.notifyControlActionEnded("scaleIn",arg0);
		executingControlAction=false;
	}




	public void scaleout(Node arg0) {
		executingControlAction=true;
		offeredMonitoringMetrics.notifyControlActionStarted("scaleOut",arg0);
		offeredCapabilities.scaleOut(arg0);
		offeredMonitoringMetrics.notifyControlActionEnded("scaleOut",arg0);
		executingControlAction=false;
	}




	@Override
	public void enforceAction(Node actionName, Node e) {
		offeredCapabilities.enforceAction(actionName, e);
	}




	@Override
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		offeredMonitoringMetrics.submitElasticityRequirements(description);
		
	}





	


}
