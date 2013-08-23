package at.ac.tuwien.dsg.mela.dataAggregation.aggregationPolicy;

import javax.xml.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 1/30/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompositionOperationType")
@XmlEnum
public enum CompositionOperationType {
    @XmlEnumValue("SUM")
    SUM,
    @XmlEnumValue("MAX")
    MAX,
    @XmlEnumValue("MIN")
    MIN,
    @XmlEnumValue("AVG")
    AVG,
    @XmlEnumValue("DIV")
    DIV,
    @XmlEnumValue("ADD")
    ADD,
    @XmlEnumValue("SUB")
    SUB,
    @XmlEnumValue("MUL")
    MUL,
    @XmlEnumValue("CONCAT")
    CONCAT,
    @XmlEnumValue("UNION")
    UNION,
    @XmlEnumValue("KEEP")
    KEEP,
    @XmlEnumValue("KEEP_LAST")
    KEEP_LAST,
    @XmlEnumValue("KEEP_FIRST")
    KEEP_FIRST,
    @XmlEnumValue("SET_VALUE")
    SET_VALUE
}

