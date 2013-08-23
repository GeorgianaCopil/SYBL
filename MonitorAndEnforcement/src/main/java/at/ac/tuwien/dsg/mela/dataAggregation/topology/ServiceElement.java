package at.ac.tuwien.dsg.mela.dataAggregation.topology;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 9:44 AM
 * IS Unique based on ID. For VM level ID=IP
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Service")
public class ServiceElement implements Iterable<ServiceElement> {
    @XmlAttribute(name = "id", required = true)
    private String id;
//    @XmlAttribute(name = "ip", required = false)
//    private String ip;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "level", required = true)
    private ServiceElementLevel level;

    @XmlElement(name = "ServiceElement", required = false)
    private Collection<ServiceElement> containedElements;

    public ServiceElement() {
        containedElements = new ArrayList<ServiceElement>();
    }

    public ServiceElement(String id) {
        containedElements = new ArrayList<ServiceElement>();
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addElement(ServiceElement serviceElement){
        containedElements.add(serviceElement);
    }

    public void removeElement(ServiceElement serviceElement){
        containedElements.remove(serviceElement);
    }

//    public String getIp() {
//        return ip;
//    }
//
//    public void setIp(String ip) {
//        this.ip = ip;
//    }

    public ServiceElementLevel getLevel() {
        return level;
    }

    public void setLevel(ServiceElementLevel level) {
        this.level = level;
    }

    public Collection<ServiceElement> getContainedElements() {
        return containedElements;
    }

    public void setContainedElements(Collection<ServiceElement> containedElements) {
        this.containedElements = containedElements;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "ServiceElementLevel")
    @XmlEnum
    public enum ServiceElementLevel {
        @XmlEnumValue("SERVICE_TOPOLOGY")
        SERVICE_TOPOLOGY,
        @XmlEnumValue("SERVICE_UNIT")
        SERVICE_UNIT,
        @XmlEnumValue("VM")
        VM,
        @XmlEnumValue("VIRTUAL_CLUSTER")
        VIRTUAL_CLUSTER,
        @XmlEnumValue("SERVICE")
        SERVICE

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceElement that = (ServiceElement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
//        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
//        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (ip != null ? ip.hashCode() : 0);
//        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


    /**
     * @return BREADTH_FIRST iterator
     */
    @Override
    public Iterator<ServiceElement> iterator() {
        return new ApplicationComponentIterator(this);
    }


    //traverses the monitored data tree in a breadth-first manner
    public class ApplicationComponentIterator implements Iterator<ServiceElement> {

        //        private ApplicationNodeMonitoredData root;
        private List<ServiceElement> elements;
        private Iterator<ServiceElement> elementsIterator;

        {
            elements = new ArrayList<ServiceElement>();
        }

        private ApplicationComponentIterator(ServiceElement root) {
//            this.root = root;

            //breadth-first tree traversal to create hierarchical tree structure
            List<ServiceElement> applicationNodeMonitoredDataList = new ArrayList<ServiceElement>();

            applicationNodeMonitoredDataList.add(root);
            elements.add(root);

            while (!applicationNodeMonitoredDataList.isEmpty()) {
                ServiceElement data = applicationNodeMonitoredDataList.remove(0);

                for (ServiceElement subData : data.getContainedElements()) {
                    applicationNodeMonitoredDataList.add(subData);
                    elements.add(subData);
                }
            }
            elementsIterator = elements.iterator();

        }

        @Override
        public boolean hasNext() {
            return elementsIterator.hasNext();
        }

        @Override
        public ServiceElement next() {
            return elementsIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Unsupported yet");
        }
    }



    public ServiceElement clone(){
        ServiceElement newServiceElement = new ServiceElement();
        newServiceElement.level = level;
        newServiceElement.id=id;
        newServiceElement.name=name;

        Collection<ServiceElement> elements = new ArrayList<ServiceElement>();

        //do not clone VM level. That is retrieved and updated from monitoring system
        for(ServiceElement el : containedElements){
            if(el.getLevel()!=ServiceElementLevel.VM){
                elements.add(el.clone());
            }
        }
        newServiceElement.containedElements = elements;
        return newServiceElement;

    }

    @Override
    public String toString() {
        return "ServiceElement{" +
                ", id='" + id + '\'' +
                ", level=" + level +
                ", name='" + name + '\'' +
                "containedElements=" + containedElements +
                '}';
    }
}
