package org.aa.auradeploy.deploy;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.log.SDLog;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.exception.ConnectorException;

/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
public class DeployHelper {
	private static final Log logger  = LogFactory.getLog(DeployHelper.class);
	public Vector listApplication(DeployInfo deployInfo, AdminClient adminClient,String sessionId){
		logger.trace("Enter the listApplication method.");
		
		ObjectName appManagement = null;
        String query = "WebSphere:type=AppManagement,*";
		Vector appList = null;             

		try {
			logger.trace("Create the query object.");
	        ObjectName queryName = new ObjectName (query);
	        
			logger.trace("Fire the query.");
	        Set s = adminClient.queryNames(queryName, null);

            if (!s.isEmpty()){
                appManagement = (ObjectName)s.iterator().next();
                
				String opName ="listApplications";
		        String signature[] = {"java.util.Hashtable" , "java.lang.String"};

		        Hashtable tbl = new Hashtable();
		        tbl.put(AppConstants.APPDEPL_LOCALE ,Locale.getDefault());
				Object params[] = {tbl ,sessionId};
				logger.trace("Invoke the query on the adminclient to get the list of application.");
		        appList = (Vector)adminClient.invoke(appManagement, opName, params, signature);
            }
            else
            {
                logger.error("AppManagement  not found");
                System.exit(-1);
            }
            
		}
        catch (MalformedObjectNameException e)
        {
            logger.error(e.getMessage(),e);

        	e.printStackTrace();
            System.exit(-1);
        }
        catch (ConnectorException e)
        {
            logger.error(e.getMessage(),e);
			e.printStackTrace();
            System.exit(-1);
        }
		catch (Exception e)
        {
            logger.error(e.getMessage(),e);
			e.printStackTrace();
            SDLog.log("Exception invoking launchProcess: " + e);
        }        
		return appList;
	}
	

	public void regenPlugin(DeployInfo deployInfo, AdminClient adminClient){
		logger.trace("Enter the regen plugin method.");
		
		ObjectName plugin = null;
        String query = "WebSphere:type=PluginCfgGenerator,*";
		Vector appList = null;             

		try {
			logger.trace("Create the query object.");
	        ObjectName queryName = new ObjectName (query);
			logger.trace("Fire the query.");
	        Set s = adminClient.queryNames(queryName, null);

            if (!s.isEmpty()){
                plugin = (ObjectName)s.iterator().next();
                System.out.println(" ******************************" + plugin.getCanonicalName()); 
				String opName ="generate";
//$AdminControl invoke WebSphere:platform=common,cell=Avatar1Cell01,version=6.0.0.1,name=PluginCfgGenerator,mbeanIdentifier=PluginCfgGenerator,type=PluginCfgGenerator,node=Avatar1CellManager01,process=dmgr generate 
// "C:/IBM/WebSphere6/AppServer C:/IBM/WebSphere6/AppServer/profiles/Dmgr01/config/ Avatar1Cell01 Avatar1CellManager01 null plugin-cfg.xml"
		        String signature[] = {"java.util.String" , "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
				Object params[] = {"C:/IBM/WebSphere6/AppServer","C:/IBM/WebSphere6/AppServer/profiles/Dmgr01/config/","Avatar1Cell01","Avatar1CellManager01","null","plugin-cfg.xml"};
				logger.trace("Invoke the query on the adminclient to regen the plugin.");
		        appList = (Vector)adminClient.invoke(plugin, opName, params, signature);
            }
		}
        catch (MalformedObjectNameException e)
        {
			e.printStackTrace();
            System.exit(-1);
        }
        catch (ConnectorException e)
        {
			e.printStackTrace();
            System.exit(-1);
        }
		catch (Exception e)
        {
			e.printStackTrace();
            SDLog.log("Exception invoking launchProcess: " + e);
        }        
	}
}


