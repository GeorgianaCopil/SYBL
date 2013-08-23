package at.ac.tuwien.dsg.mela.dataAccess.impl;

import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaClusterInfo;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/21/13
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GangliaDataSourceI {
    public GangliaClusterInfo getGangliaMonitoringInfo() throws IOException, JAXBException;
}
