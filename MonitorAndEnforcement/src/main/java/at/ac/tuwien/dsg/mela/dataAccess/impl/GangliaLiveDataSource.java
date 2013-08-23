package at.ac.tuwien.dsg.mela.dataAccess.impl;

import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaClusterInfo;
import at.ac.tuwien.dsg.mela.jaxbGangliaEntities.GangliaInfo;

import org.xml.sax.SAXParseException;
import org.yaml.snakeyaml.Yaml;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import at.ac.tuwien.dsg.mela.utils.Configuration;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.monitoringPlugins.gangliaMonitoring.GangliaMonitor.MyUserInfo;
import at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.RuntimeLogger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: daniel-tuwien
 * Date: 6/21/13
 * Time: 11:01 AM
 * Retrieves ganglia monitoring data from a live source and stores it in YAML currently.
 */
public class GangliaLiveDataSource implements GangliaDataSourceI {

    private String fileName;
    private Yaml yaml = new Yaml();

    {
        fileName = Configuration.getApplicationMonitoringFolder()+"monitoring" + new Date().toString();
        fileName = fileName.replace(" ","_");
        fileName = fileName.replace(":","_");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"Saving monitoring data at " + fileName);
    }
    private byte[] readFile (String file) throws IOException {
        // Open file
    	InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
    	

        try {
            // Get and check length
        	ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        	int nRead;
        	byte[] data = new byte[16384];

        	while ((nRead = is.read(data, 0, data.length)) != -1) {
        	  buffer.write(data, 0, nRead);
        	}

        	buffer.flush();
            return buffer.toByteArray();
        }
        finally {
            is.close();
        }
    }
    

    Session session;
    private String execute(String rootIPAddress, String securityCertificatePath, String gangliaPort,String command) throws JSchException {
        if (session==null){
     	   JSch jSch = new JSch();
    		
            byte[] prvkey=null;
    		try {
    			prvkey = readFile(securityCertificatePath);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} // Private key must be byte array
            final byte[] emptyPassPhrase = new byte[0]; // Empty passphrase for now, get real passphrase from MyUserInfo

            jSch.addIdentity(
                "ubuntu",    // String userName
                prvkey,          // byte[] privateKey 
                null,            // byte[] publicKey //maybe generate a public key and try with it
                emptyPassPhrase  // byte[] passPhrase
            );
            
             session = jSch.getSession("ubuntu", rootIPAddress, 22);
            session.setConfig("StrictHostKeyChecking", "no"); //         Session session = jSch.getSession("ubuntu", rootIPAddress, 22);

            UserInfo ui = new MyUserInfo(); // MyUserInfo implements UserInfo
            session.setUserInfo(ui);
            session.connect();
        }
         ChannelExec channel=(ChannelExec) session.openChannel("exec");
         ((ChannelExec)channel).setCommand(command);
          channel.connect();
         InputStream stdout =null;
 		try {
 			stdout = channel.getInputStream();
 		} catch (IOException e2) {
 			// TODO Auto-generated catch block
 			e2.printStackTrace();
 		}
       
        
 		BufferedReader reader =  new BufferedReader(new InputStreamReader(stdout));
 		 String line = null;
          String content = "";
       
          try {
 			while ((line = reader.readLine()) != null) {
 			      //if ganglia does not respond
 			      if (line.contains("Unable to connect")) {
 			    	 Logger.getLogger(this.getClass().getName()).log(Level.WARNING,  "" + rootIPAddress + " does not respond to monitoring request");
 			          return null;
 			      }
 			      if (line.contains("<") || line.endsWith("]>")) {
 			          content += line + "\n";
 			      }
 			  }
 		} catch (IOException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
          channel.disconnect();
          return content;

     }
    
//  String command = "telnet localhost " + gangliaPort;
//
//	String content= "";
//	try {
//		content = execute(rootIPAddress, securityCertificatePath, gangliaPort, command);
//	} catch (JSchException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	}
//		
//	StringReader stringReader = new StringReader(content);
//	
    
    @Override
    public GangliaClusterInfo getGangliaMonitoringInfo() throws IOException, JAXBException {

    	String cmd = "telnet localhost " +Configuration.getGangliaPort();
        

        
    	String content= "";
    	StringReader stringReader = null;
    	if (!Configuration.getAccessMachineIP().equalsIgnoreCase("localhost")){
    	try {
    		
    		content = execute(Configuration.getAccessMachineIP(), Configuration.getSecurityCertificatePath(), Configuration.getGangliaPort(), cmd);
    	} catch (JSchException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
    		
    	stringReader = new StringReader(content);
    	}else{
    		
    	
    	
        Process p = Runtime.getRuntime().exec(cmd);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {

            //if ganglia does not respond
            if (line.contains("Unable to connect")) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unable to execute " + cmd);
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
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "" + "Unable to execute " + cmd);
            return null;
        }
         stringReader = new StringReader(content);
    	}
       // RuntimeLogger.logger.info(content);

    	try{
        JAXBContext jc = JAXBContext.newInstance(GangliaInfo.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GangliaInfo info = (GangliaInfo) unmarshaller.unmarshal(stringReader);
        GangliaClusterInfo gangliaClusterInfo = info.getClusters().iterator().next();
        stringReader.close();

        saveRawDataToFile(fileName,gangliaClusterInfo);

        return gangliaClusterInfo;
    	}catch (Exception e){
    		session=null;
        	RuntimeLogger.logger.info( e.getMessage());
        	return new GangliaClusterInfo();
        
    	}
    }


    private void saveRawDataToFile(String file,GangliaClusterInfo gangliaClusterInfo){
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"Collected monitoring data at " + new Date());
        try {
            String elasticity = yaml.dump(gangliaClusterInfo);
            //better to open close buffers as there are less chances I get the file in unstable state if I terminate the
            //program execution abruptly
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.newLine();
            bufferedWriter.write("--- " + elasticity);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,e.getMessage(),e);
          e.printStackTrace();
        }
    }
}
