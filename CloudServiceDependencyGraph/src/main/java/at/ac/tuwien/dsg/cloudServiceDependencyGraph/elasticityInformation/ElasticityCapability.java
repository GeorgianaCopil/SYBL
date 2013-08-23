package at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation;

import java.io.Serializable;

public class ElasticityCapability{
	  public ElasticityCapability(){
	      	
      }
      private String value;
      private String name;
      private String apiMethod;
      private String parameter;
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getApiMethod() {
			return apiMethod;
		}
		public void setApiMethod(String apiMethod) {
			this.apiMethod = apiMethod;
		}
		public String getParameter() {
			return parameter;
		}
		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

}
