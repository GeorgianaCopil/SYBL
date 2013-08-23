/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.dsg.sybl.monitorandenforcement.utils;

import java.io.IOException;
import java.util.Date;


import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;


public class RuntimeLogger {
	   public static Logger logger = Logger.getLogger(RuntimeLogger.class);
	   static{
		      SimpleLayout layout = new SimpleLayout();    
			   FileAppender appender=null;
			   Date date = new Date();
			   
			try {
				appender = new FileAppender(layout,"./logs/RuntimeLogger_"+date.getDay()+"_"+date.getMonth()+"_"+date.getHours()+"_"+date.getMinutes()+".txt",false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			      logger.addAppender(appender);

			      logger.setLevel((Level) Level.DEBUG);

	   }
	
}
