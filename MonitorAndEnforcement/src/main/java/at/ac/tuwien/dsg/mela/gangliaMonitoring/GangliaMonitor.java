package at.ac.tuwien.dsg.mela.gangliaMonitoring;

import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaClusterInfo;
import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaHostInfo;
import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaInfo;
import at.ac.tuwien.dsg.mela.utils.Configuration;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 11/11/12
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class GangliaMonitor {

//    private  String openStackRegion = "myregion";
//    private  String apiType = "openstack-nova";
//    private  String cloudUser = "CELAR:dmoldovan";
//    private  String cloudPassword = "Bee8sah1";
//    private  String apiEndpoint = "http://openstack.infosys.tuwien.ac.at:5000/v2.0";
//    private  String openStackCertificatePath = "/home/daniel-tuwien/.ssh/tiramolatestkey.pem";
//    private  String gangliaPort = "8650";
//    private  String gangliaPort = "8649";   

    //    private  ServerApi serverApi;
    private JFrame visualRepr;


//    private  Iterable<Module> modules;
//    private  ComputeServiceContext computeServiceContext;

    {
//        modules = ImmutableSet.<Module>of(
//                new SLF4JLoggingModule());
//        computeServiceContext = ContextBuilder.newBuilder(Configuration.getApiType())
//                .credentials(Configuration.getCloudUser(), Configuration.getCloudPassword())
//                .endpoint(Configuration.getApiEndpoint())
//                .modules(modules)
//                .buildView(ComputeServiceContext.class);
//
//        NovaApi client = (NovaApi) computeServiceContext.unwrap(NovaApiMetadata.CONTEXT_TOKEN).getApi();

//        serverApi = client.getServerApiForZone(Configuration.getOpenStackRegion());
    }

    /**
     * JClouds is used to list all openstack VMs which contains in their name openStackVMsNameFilter
     * The private IPs of all VMs are retrieved and used to "telnet IP ganliaPort" from the access machine
     * in order to retrieve monitoring data.
     *
     * @return
     */
    public GangliaClusterInfo monitorGangliaNodesIndirectly(List<String> ipsToBeMonitored, String accessIP) throws JAXBException, IOException {


        GangliaClusterInfo monitoredData = monitorGangliaMachineIndirectly(accessIP);

        //contains only IPs that are wanted
        GangliaClusterInfo filteredData = new GangliaClusterInfo();


        if(monitoredData == null){
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"Cannot gather data accessing " +accessIP+ " for " + ipsToBeMonitored);
            return filteredData;
        }
        Collection<GangliaHostInfo> filteredDataHostsInfo = filteredData.getHostsInfo();

        filteredData.setLatlong(monitoredData.getLatlong());
        filteredData.setLocaltime(monitoredData.getLocaltime());
        filteredData.setName(monitoredData.getName());
        filteredData.setOwner(monitoredData.getOwner());
        filteredData.setUrl(monitoredData.getUrl());

        //extract from cluster all hosts that are not equal to the desired IP
        for (String address : ipsToBeMonitored) {
            filteredDataHostsInfo.addAll(monitoredData.searchHostsByIP(address));
        }

        return filteredData;
    }


    public GangliaClusterInfo monitorGangliaNodesDirectly(List<String> ipsToBeMonitored) throws JAXBException, IOException {


        GangliaClusterInfo gangliaClusterInfo = null;

        for (String address : ipsToBeMonitored) {
            GangliaClusterInfo info = monitorGangliaMachineDirectly(address);
            if (info == null) {
                continue;
            }
            if (gangliaClusterInfo == null) {
                gangliaClusterInfo = info;
            } else {
                gangliaClusterInfo.getHostsInfo().addAll(info.getHostsInfo());
            }
        }


        return gangliaClusterInfo;
    }



    /**
     * @param rootIPAddress
     * @return Information about ALL hosts monitored by Ganglia
     * @throws IOException
     * @throws JAXBException
     */
    public GangliaClusterInfo monitorGangliaMachineIndirectly(String rootIPAddress) throws IOException, JAXBException {
//        String cmd = "ssh -i " + Configuration.getSecurityCertificatePath() + " ubuntu@" + rootIPAddress + " telnet " + targetIP + " " + Configuration.getGangliaPort();
        String cmd = "ssh -i " + Configuration.getSecurityCertificatePath() + " ubuntu@" + rootIPAddress + " telnet localhost " + Configuration.getGangliaPort();


        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        String content = "";
        while ((line = reader.readLine()) != null) {

            //if ganglia does not respond
            if (line.contains("Unable to connect")) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "" + rootIPAddress + " does not respond to monitoring request");
                return null;
            }
            if (line.contains("<") || line.endsWith("]>")) {
                content += line + "\n";
            }
        }

        p.getInputStream().close();
        p.getErrorStream().close();
        p.getOutputStream().close();
        p.destroy();


        //if ganglia does not respond
        if (content == null || content.length() == 0) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "" + rootIPAddress + " does not respond to monitoring request");
            return null;
        }

        StringReader stringReader = new StringReader(content);

        JAXBContext jc = JAXBContext.newInstance(GangliaInfo.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GangliaInfo info = (GangliaInfo) unmarshaller.unmarshal(stringReader);
        GangliaClusterInfo gangliaClusterInfo = info.getClusters().iterator().next();
        stringReader.close();

        //extract from cluster all hosts that are not equal to the desired IP
        //while ganglia monitors all machines in a cluster, this is done to avoid
        //problems when ganglia does not monitor open stack machines that have only cloud-internal IPs
        //commented as to return everything. Avoids calling repeatedly ganglia trough SSH
//        {
//            Collection<GangliaHostInfo> hostInfos = gangliaClusterInfo.searchHostsByIP(targetIP);
//            gangliaClusterInfo.setHostsInfo(hostInfos);
//        }

        return gangliaClusterInfo;

    }

    public GangliaClusterInfo monitorGangliaMachineDirectly(String targetIP) throws IOException, JAXBException {
        String cmd = "telnet " + targetIP + " " + Configuration.getGangliaPort();


        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        String content = "";
        while ((line = reader.readLine()) != null) {

            //if ganglia does not respond
            if (line.contains("Unable to connect")) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "" + targetIP + " does not respond to monitoring request");
                return null;
            }
            if (line.contains("<") || line.endsWith("]>")) {
                content += line + "\n";
            }
        }

        p.getInputStream().close();
        p.getErrorStream().close();
        p.getOutputStream().close();
        p.destroy();


        //if ganglia does not respond
        if (content == null || content.length() == 0) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "" + targetIP + " does not respond to monitoring request");
            return null;
        }

        StringReader stringReader = new StringReader(content);

        JAXBContext jc = JAXBContext.newInstance(GangliaInfo.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GangliaInfo info = (GangliaInfo) unmarshaller.unmarshal(stringReader);
        GangliaClusterInfo gangliaClusterInfo = info.getClusters().iterator().next();
        stringReader.close();
//        //extract from cluster all hosts that are not equal to the desired IP
//        //while ganglia monitors all machines in a cluster, this is done to avoid
//        //problems when ganglia does not monitor open stack machines that have only cloud-internal IPs
//        {
//            Collection<GangliaHostInfo> hostInfos = gangliaClusterInfo.searchHostsByIP(targetIP);
//            gangliaClusterInfo.setHostsInfo(hostInfos);
//        }

        return gangliaClusterInfo;

    }


    public void close() {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


}
