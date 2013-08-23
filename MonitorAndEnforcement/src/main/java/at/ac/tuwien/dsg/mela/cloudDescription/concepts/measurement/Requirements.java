package at.ac.tuwien.dsg.mela.cloudDescription.concepts.measurement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 2/2/13
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Requirements")
public class Requirements {

    @XmlAttribute(name = "TargetServiceID", required = true)
    private String targetServiceID;

    @XmlElement(name = "Requirement")
    private List<Requirement> requirements;

    {
        requirements = new ArrayList<Requirement>();
    }

    public String getTargetServiceID() {
        return targetServiceID;
    }

    public void setTargetServiceID(String targetServiceID) {
        this.targetServiceID = targetServiceID;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }
}
