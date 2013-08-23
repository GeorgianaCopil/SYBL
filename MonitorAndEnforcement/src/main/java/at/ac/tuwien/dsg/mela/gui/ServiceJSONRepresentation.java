package at.ac.tuwien.dsg.mela.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.Node;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.Metric;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricFilter;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.MetricValue;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceElementMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement.ServiceMonitoringSnapshot;
import at.ac.tuwien.dsg.mela.dataAggregation.topology.ServiceElement;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/25/13
 */
public class ServiceJSONRepresentation {



    //accepts metric filters for cosmetisation_to avoid showing some metrics
    public static String describeInJSON(ServiceMonitoringSnapshot serviceMonitoringSnapshot, List<MetricFilter> filters, String actionName, Node targetEntity) {

    //assumes there is one SERVICE level element per snapshot
    Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>> monitoredData = serviceMonitoringSnapshot.getMonitoredData();
    ServiceElement rootElement = monitoredData.get(ServiceElement.ServiceElementLevel.SERVICE).keySet().iterator().next();

        //apply metric filters (for cosmetisation_
        for(MetricFilter metricFilter : filters){

            if(monitoredData.containsKey(metricFilter.getLevel())){
                for(Map.Entry<ServiceElement,ServiceElementMonitoringSnapshot> entry: monitoredData.get(metricFilter.getLevel()).entrySet()) {
                    //if either the filter applies on all elements at one particular level (targetIDs are null or empty) either the filter targets the service element ID
                    if(metricFilter.getTargetServiceElementIDs() == null ||
                            metricFilter.getTargetServiceElementIDs().size()==0 ||
                            metricFilter.getTargetServiceElementIDs().contains(entry.getKey().getId())){
                        entry.getValue().keepMetrics(metricFilter.getMetrics());
                    }
                }
            }
        }

    JSONObject root = new JSONObject();
    root.put("name", rootElement.getId());
    root.put("type", "" + rootElement.getLevel());



    //going trough the service element tree in a BFS manner
    List<MyPair> processing = new ArrayList<MyPair>();
    processing.add(new MyPair(rootElement,root));

    while(!processing.isEmpty()){
        MyPair myPair = processing.remove(0);
        JSONObject object = myPair.jsonObject;
        ServiceElement element = myPair.serviceElement;
        JSONArray children = (JSONArray) object.get("children");
        if(children== null){
            children = new JSONArray();
            object.put("children", children);
        }

        //add information about executing actions
        {
            if(targetEntity != null && element.getId().equals(targetEntity.getId())){
                  object.put("attention","true");
                  object.put("actionName",actionName);
            }
        }

        JSONArray metrics = new JSONArray();
        ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = monitoredData.get(element.getLevel()).get(element);

        //add metrics
        for(Map.Entry<Metric,MetricValue> entry: serviceElementMonitoringSnapshot.getMonitoredData().entrySet()){
            JSONObject metric = new JSONObject();
            metric.put("name",entry.getValue().getValueRepresentation() + " ["+entry.getKey().getName()+"]");
            metric.put("type","metric");

//                JSONObject value = new JSONObject();
//                value.put("name",""+entry.getValue().getValueRepresentation());
//                value.put("type","metric");
//
//                JSONArray valueChildren = new JSONArray();
//                valueChildren.add(value);
//
//                metric.put("children", valueChildren);

            children.add(metric);
        }
//            object.put("metrics", metrics);

        //add children
        for(ServiceElement child : element.getContainedElements()){
            JSONObject childElement = new JSONObject();
            childElement.put("name", child.getId());
            childElement.put("type", ""+child.getLevel());
            JSONArray childrenChildren = new JSONArray();
            childElement.put("children", childrenChildren);
            processing.add(new MyPair(child,childElement));
            children.add(childElement);
        }
    }



    String string = root.toJSONString();
    string = string.replaceAll("\"","!");

    return string;
}


    public static String describeInJSON(ServiceMonitoringSnapshot serviceMonitoringSnapshot) {

    //assumes there is one SERVICE level element per snapshot
    Map<ServiceElement.ServiceElementLevel, Map<ServiceElement, ServiceElementMonitoringSnapshot>> monitoredData = serviceMonitoringSnapshot.getMonitoredData();
    ServiceElement rootElement = monitoredData.get(ServiceElement.ServiceElementLevel.SERVICE).keySet().iterator().next();

    JSONObject root = new JSONObject();
    root.put("name", rootElement.getId());
    root.put("type", "" + rootElement.getLevel());



    //going trough the service element tree in a BFS manner
    List<MyPair> processing = new ArrayList<MyPair>();
    processing.add(new MyPair(rootElement,root));

    while(!processing.isEmpty()){
        MyPair myPair = processing.remove(0);
        JSONObject object = myPair.jsonObject;
        ServiceElement element = myPair.serviceElement;
        JSONArray children = (JSONArray) object.get("children");
        if(children== null){
            children = new JSONArray();
            object.put("children", children);
        }

        JSONArray metrics = new JSONArray();
        ServiceElementMonitoringSnapshot serviceElementMonitoringSnapshot = monitoredData.get(element.getLevel()).get(element);

        //add metrics
        for(Map.Entry<Metric,MetricValue> entry: serviceElementMonitoringSnapshot.getMonitoredData().entrySet()){
            JSONObject metric = new JSONObject();
            metric.put("name",entry.getValue().getValueRepresentation() + " ["+entry.getKey().getName()+"]");
            metric.put("type","metric");

//                JSONObject value = new JSONObject();
//                value.put("name",""+entry.getValue().getValueRepresentation());
//                value.put("type","metric");
//
//                JSONArray valueChildren = new JSONArray();
//                valueChildren.add(value);
//
//                metric.put("children", valueChildren);

            children.add(metric);
        }
//            object.put("metrics", metrics);

        //add children
        for(ServiceElement child : element.getContainedElements()){
            JSONObject childElement = new JSONObject();
            childElement.put("name", child.getId());
            childElement.put("type", ""+child.getLevel());
            JSONArray childrenChildren = new JSONArray();
            childElement.put("children", childrenChildren);
            processing.add(new MyPair(child,childElement));
            children.add(childElement);
        }
    }



    String string = root.toJSONString();
    string = string.replaceAll("\"","!");

    return string;
}


    private static class MyPair{
        public ServiceElement serviceElement;
        public JSONObject jsonObject;

        private MyPair() {
        }

        public MyPair(ServiceElement serviceElement, JSONObject jsonObject) {
            this.serviceElement = serviceElement;
            this.jsonObject = jsonObject;
        }
    }

}
