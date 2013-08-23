SYBL
====

SYBL Language and its runtime for elasticity control of cloud services.

Instalation and usage instructions 
This repository contains SYBL, a system for multi-level specification and control of elasticity of cloud services.

For running the system the following steps need to be followed:
  
  ControlService configuration:
		
		1. The cloud service needs to be described, for now following the structure in the serviceDescription.xml(\ControlService\src\main\resources\config) example based on service units and service topologies.
		
		2. SYBL supports elasticity requirements specified either through Java Annotations or through XML descriptions.
		   2.1 SYBL annotations are subset of java annotations and can be specified in a Java file, for which you need to import LocalMonitor library (current folder, generate using maven) and to deploy the LocalControlService jar. In the LocalControlService config file should be specified both rmi registry name and port through which these can communicate. 
		   
		   2.2 Elasticity requirements can be specified in an XML either separately (see ElasticityRequirementsSpecification.xml from controlService\src\main\resources) or integrated in the service description file(as for the serviceDescription.xml). If specified in separate XML description file, this needs to be stated in the config file of the Control Service (SYBLDirectives = ./config/ElasticityRequirementsSpecification.xml).  
		
		3. Depending on where we run the Control Service and the Cloud Service, the path towards the resulting web service needs to be specified in the config file of the Control Service.
	
	
	
   MonitorAndEnforcement configuration:
   
		4. Monitoring Plugin Configuration - plugins offered - Simple ganglia based or MELA
			4.1 For working with simple ganglia plugin some information has to be specified:
				4.1.1 The ganglia plugin class needs to be specified in the config file of the MonitoringAndEnforcement Service (MonitoringPlugin = at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPlugins.gangliaMonitoring.MonitoringGangliaAPI)
				4.1.2 Ganglia IP needs to be specified in the config file of the MonitoringAndEnforcement Service 
				4.1.3 Ganglia Port needs to be specified in the config file of the MonitoringAndEnforcement Service
			
			4.2 For working with MELA plugin some information has to be specified:
				4.2.1. The main plugin class implementing MonitoringInterface needs to be specified in the config file of the MonitoringAndEnforcement Service (MonitoringPlugin = at.ac.tuwien.dsg.mela.apis.javaAPI.MELA_API)
				4.2.2 ACCESS_MACHINE_IP needs to be specified (a machine having ganglia installed with the same port as the rest of the application)
				4.2.3 GANGLIA_PORT needs to be specified
		
		5. Enforcement Plugin Configuration
		   5.1 The openstack plugin class needs to be specified in the config file of the MonitoringAndEnforcement Service (EnforcementPlugin =  at.ac.tuwien.dsg.sybl.monitorandenforcement.enforcementPlugins.openstack.EnforcementOpenstackAPI)
		   
		   5.2 Some basic information need to be specified for being able to connect to the cloud for enforcing actions:
				5.2.1 The name of the certificate used - CertificateName
				5.2.2 The path towards the certificate - CertificatePath
				5.2.3 The deployment description path (association between service units and snapshots, for being able to scale out when necessary) -  DeploymentDescriptionPath (e.g. config/deploymentDescription.xml)
				5.2.4 The machine which is the access machine, on which some elasticity scripts have to be run (i.e. decomissioning cassandra node) - AccessIP - in the case the Monitoring and Enforcement service is deployed on the access machine, the value equals localhost
				5.2.5 Type of the cloud middleware used - CloudAPIType (in this case openstack-nova)
				5.2.6 Endpoint for accessing the cloud infrastructure - CloudAPIEndpoint (in this case DSG local cloud 	 http://openstack.infosys.tuwien.ac.at:5000/v2.0)
				5.2.7 Username for accessing cloud infrastructure - CloudUser
				5.2.8 Password for accessing cloud infrastructure - CloudPassword

			
   LocalControlService - used for the case we annotate SYBL Directives inside the code of the service units
   
         6.  Configure RMIRegistryPort and RMIRegistryName

		
   LocalMonitor configuration and usage
   
         7.  Configure RMIRegistryPort and RMIRegistryName - the same as for LocalControlService


   Compilation 
   
         8. Run "mvn install" for the following modules in the following order:
			  1. Model
			  2. MonitorAndEnforcement
			  3. ControlService
			  4. LocalCommunication
			  5. LocalControlService
			  6. LocalMonitor
		
		
  Deployment 
  
     For deployment of controller which catches code region directives:
	 
        9.1 Copy LocalControlService and run it on each vm
		9.2 Import LocalMonitor in the application and use annotations @SYBL_CodeRegionDirective, @SYBL_ServiceUnitDirective, @SYBL_ServiceTopologyDirective ,@SYBL_CloudServiceDirective.   
		9.3 Deploy the ControlService.war on the virtual machine which has the public IP  - in the tomcat distribution in webapps folder
		9.4 Deploy the AnalysisMonitoringEnforcementService-0.0.1.war on the virtual machine which has the public IP - in the tomcat distribution in webapps folder
		9.5 Start the tomcat server
		
	For deployment of controller for an application without code region directives:
	
		10.1 Deploy the ControlService.war on the virtual machine which has the public IP or on the local machine - in the tomcat distribution in webapps folder
		10.2 Deploy the AnalysisMonitoringEnforcementService-0.0.1.war on the virtual machine which has the public IP or on the local machine (in case we deploy it on the local machine we need to set in the MonitorAndEnforcement module the ACCESS_MACHINE_IP to the public IP) - in the tomcat distribution in webapps folder
		10.3 Start the tomcat server
		
		