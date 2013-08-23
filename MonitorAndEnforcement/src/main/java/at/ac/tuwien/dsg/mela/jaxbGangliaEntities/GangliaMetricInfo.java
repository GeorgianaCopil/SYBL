package at.ac.tuwien.dsg.mela.jaxbGangliaEntities;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ==============================
 *
 * @author: Daniel Moldovan
 * Technical University of Vienna
 * ==============================
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "METRIC")
public class GangliaMetricInfo {

    @XmlAttribute(name = "NAME", required = true)
    private String name;

    @XmlAttribute(name = "VAL", required = true)
    private String value;

    @XmlAttribute(name = "TYPE", required = true)
    private String type;

    @XmlAttribute(name = "UNITS")
    private String units;

    @XmlAttribute(name = "TN")
    private String tn;

    @XmlAttribute(name = "TMAX")
    private String tmax;

    @XmlAttribute(name = "DMAX")
    private String dmax;

    @XmlAttribute(name = "SLOPE")
    private String slope;


    @XmlAttribute(name = "SOURCE")
    private String source;

    @XmlElement(name = "EXTRA_DATA")
    private Collection<GangliaExtraDataInfo> gangliaExtraDataInfoCollection;

    {
        gangliaExtraDataInfoCollection = new ArrayList<GangliaExtraDataInfo>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Float, Integer or String representations of the value stored as String by Ganglia
     */
    public Object getConvertedValue() {
        if (type.contains("float") || type.contains("double")) {
            try{
                return Float.parseFloat(value);
            }catch(NumberFormatException e){
               return new Float(Float.NaN);
            }
        } else if (type.contains("int")) {
            try{
                return Integer.parseInt(value);
            }catch(NumberFormatException e){
                return new Float(Float.NaN);
            }
        } else {
            return value;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public String getTmax() {
        return tmax;
    }

    public void setTmax(String tmax) {
        this.tmax = tmax;
    }

    public String getDmax() {
        return dmax;
    }

    public void setDmax(String dmax) {
        this.dmax = dmax;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Collection<GangliaExtraDataInfo> getGangliaExtraDataInfoCollection() {
        return gangliaExtraDataInfoCollection;
    }

    public void setGangliaExtraDataInfoCollection(Collection<GangliaExtraDataInfo> gangliaExtraDataInfoCollection) {
        this.gangliaExtraDataInfoCollection = gangliaExtraDataInfoCollection;
    }

    @Override
    public String toString() {
        String info = "GangliaMetricInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", units='" + units + '\'' +
                ", tn='" + tn + '\'' +
                ", tmax='" + tmax + '\'' +
                ", dmax='" + dmax + '\'' +
                ", slope='" + slope + '\'' +
                ", source='" + source + '\'' +
                ", gangliaExtraDataInfoCollection=";

        for (GangliaExtraDataInfo dataInfo : gangliaExtraDataInfoCollection) {
            info += "\t " + dataInfo.toString() + "\n";
        }
        info += '}';
        return info;
    }
}