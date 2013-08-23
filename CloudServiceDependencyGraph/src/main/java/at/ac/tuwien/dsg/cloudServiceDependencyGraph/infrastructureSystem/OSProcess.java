package at.ac.tuwien.dsg.cloudServiceDependencyGraph.infrastructureSystem;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;

public class OSProcess extends Node {

	private String processId = "";

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

}
