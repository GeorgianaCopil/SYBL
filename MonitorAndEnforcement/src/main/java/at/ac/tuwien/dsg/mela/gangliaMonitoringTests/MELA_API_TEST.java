package at.ac.tuwien.dsg.mela.gangliaMonitoringTests;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship;
import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Relationship.RelationshipType;
import at.ac.tuwien.dsg.mela.apis.javaAPI.MELA_API;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 */
public class MELA_API_TEST {
    @Test
    public void testName() throws Exception {
        MELA_API mela_api = new MELA_API();

        Node cloudService = new Node();
        cloudService.setId("cs");
        Node serviceTopology = new Node();
        serviceTopology.setId("ST");
        
        ArrayList<Node> serviceTopologies = new ArrayList<Node>();
        for (Node n:serviceTopologies)
        	serviceTopology.addNode(n,new Relationship());
        Relationship rel = new Relationship();
        rel.setSourceElement(cloudService.getId());
        rel.setTargetElement(serviceTopology.getId());
        rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
        cloudService.addNode(serviceTopology,rel);


        {
        	Node serviceTopology1 = new Node();
            serviceTopology1.setId("ST1");
            rel = new Relationship();
            rel.setSourceElement(serviceTopology.getId());
            rel.setTargetElement(serviceTopology1.getId());
            rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);

            serviceTopology.addNode(serviceTopology1,rel);
            ArrayList<Node> serviceUnits = new ArrayList<Node>();

            {
            	Node serviceUnit = new Node();
                serviceUnit.setId("ST1_SU1");
                rel = new Relationship();
                rel.setSourceElement(serviceTopology1.getId());
                rel.setTargetElement(serviceUnit.getId());
                rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                serviceTopology1.addNode(serviceUnit,rel);
            }
        }

        //web services topology
        {
        	Node serviceTopology2 = new Node();
            serviceTopology2.setId("ST2");
            rel = new Relationship();
            rel.setSourceElement(serviceTopology.getId());
            rel.setTargetElement(serviceTopology2.getId());
            rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
            serviceTopology.addNode(serviceTopology2,rel);
            ArrayList<Node> serviceUnits = new ArrayList<Node>();

            {  //web services
            	Node serviceUnit = new Node();
                serviceUnit.setId("ST2_SU1");
                rel = new Relationship();
                rel.setSourceElement(serviceTopology2.getId());
                rel.setTargetElement(serviceUnit.getId());
                rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                serviceTopology2.addNode(serviceUnit,rel);
            }
            {  //load balancer
            	Node serviceUnit = new Node();
                serviceUnit.setId("ST2_SU2");
                rel = new Relationship();
                rel.setSourceElement(serviceTopology2.getId());
                rel.setTargetElement(serviceUnit.getId() );
                rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                serviceTopology2.addNode(serviceUnit,rel);
            }
        }

        mela_api.submitServiceConfiguration(cloudService);

        {
        	Node serviceUnit = new Node();
            serviceUnit.setId("ST2_SU2");
            while(true){
                System.out.println(mela_api.getCpuUsage(serviceUnit));
                Thread.sleep(5000);
            }
        }

    }
}
