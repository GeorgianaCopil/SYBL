package at.ac.tuwien.dsg.mela.dataAccess.impl;

import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaClusterInfo;
import at.ac.tuwien.dsg.mela.utils.Configuration;

import org.yaml.snakeyaml.Yaml;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/21/13
 * Time: 11:05 AM
 */
public class GangliaFileDataSource implements GangliaDataSourceI {
    private String file;
    private Yaml yaml = new Yaml();
    private Iterator<Object> monitoredDataIterator;

    public GangliaFileDataSource(String file) {
        this.file = file;
    }


    @Override
    public GangliaClusterInfo getGangliaMonitoringInfo() throws IOException, JAXBException {
        if(monitoredDataIterator == null){
        	
        	InputStream is = Configuration.class.getClassLoader().getResourceAsStream(file);

        	
            monitoredDataIterator = yaml.loadAll(is).iterator();
        }
        if(monitoredDataIterator.hasNext()){
            return (GangliaClusterInfo) monitoredDataIterator.next();
        }else{
            System.exit(1);
            return null;
        }
    }
}
