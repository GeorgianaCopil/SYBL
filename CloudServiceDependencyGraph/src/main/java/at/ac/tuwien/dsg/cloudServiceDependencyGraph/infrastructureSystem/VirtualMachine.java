package at.ac.tuwien.dsg.cloudServiceDependencyGraph.infrastructureSystem;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;


public class VirtualMachine extends Node{
	public List<Node> ips = new ArrayList<Node>();
	
	public List<Node> getPublicIps(){
		return null;
	}
	public List<Node> getPrivateIps(){
		return null;
	}
}
