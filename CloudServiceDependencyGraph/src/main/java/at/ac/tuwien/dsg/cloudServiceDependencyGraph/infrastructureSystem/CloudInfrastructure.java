package at.ac.tuwien.dsg.cloudServiceDependencyGraph.infrastructureSystem;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;


public class CloudInfrastructure extends Node{
private List<VirtualCluster> clusters = new ArrayList<VirtualCluster>();
private List<VirtualMachine> vms = new ArrayList<VirtualMachine>();
public List<VirtualCluster> getClusters() {
	return clusters;
}
public void setClusters(List<VirtualCluster> clusters) {
	this.clusters = clusters;
}
public List<VirtualMachine> getVms() {
	return vms;
}
public void setVms(List<VirtualMachine> vms) {
	this.vms = vms;
}
public void addVM(VirtualMachine vm){
	vms.add(vm);
}
public void addVirtualCluster(VirtualCluster vc){
	clusters.add(vc);
}
}
