package at.ac.tuwien.dsg.cloudServiceDependencyGraph.infrastructureSystem;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;


public class VirtualCluster extends Node{
 private List<VirtualMachine> vms = new ArrayList<VirtualMachine>();

public List<VirtualMachine> getVms() {
	return vms;
}

public void setVms(List<VirtualMachine> vms) {
	this.vms = vms;
}
 public void addVm(VirtualMachine vm){
	 vms.add(vm);
 }
}
