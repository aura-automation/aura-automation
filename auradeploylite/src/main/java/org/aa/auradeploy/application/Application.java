/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.auradeploy.application;

import java.io.File;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.deploy.DeployInfo;
import org.aa.auradeploy.helper.Helper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.deploy.Connection;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.exception.AdminException;

public class Application extends Connection{
	
	private static final Log logger  = LogFactory.getLog(Application.class);
	private DeployInfo mdeployInfo;
	AppManagement appManagement = null;
	
    private Application(){
    	
    }
    
    public void doWork()
    	throws DeployException{

    	if (mdeployInfo.isMultiEAR()){

			SDLog.log("Deploy type is MultiEAR"); 
			SDLog.log("");

			logger.warn("This is a MultiEAR deploy");
			File[] files =  Helper.scanDir(mdeployInfo.getMultiEARLocation(),true);
			
			if (files.length == 0){
				SDLog.log("No files found");
			}

			for (int i=0;i<	files.length;i++){
				if ((files[i].getName().endsWith(".ear")) || (files[i].getName().endsWith(".EAR"))){
					String earFileName = files[i].getName();
					String applicationName = earFileName.substring(0,earFileName.indexOf(".ear"));
					SDLog.log( "******************************************************");
					SDLog.log( "*** Application Name " + applicationName + ".***");
					SDLog.log( "******************************************************");
					SDLog.log( "operation is  " + mdeployInfo.getOperation());
					mdeployInfo.setApplicationName(applicationName);	
					if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_START)){
						SDLog.log( "Will start Application " + applicationName);
						startApplication(applicationName);
					}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_STOP)){
						SDLog.log( "Will stop Application " + applicationName);
						stopApplication(applicationName);
					}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_EXPORT)){
						SDLog.log( "Will export Application " + applicationName);
						exportApplication(applicationName);
					}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UPDATE)){
						SDLog.log( "Will update Application " + applicationName);
						updateApplication(applicationName);
					}
				}
			}
    	}else{
			if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_START)){
				startApplication();
			}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_STOP)){
				stopApplication();
			}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_EXPORT)){
				exportApplication();
			}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UPDATE)){
				updateApplication();
			}
    	}
    }
    
    
	public Application(DeployInfo deployInfo)
		throws DeployException{
		mdeployInfo = deployInfo;
		try{
			SDLog.log("******************************");

			SDLog.log("Aura Deploy Lite Version "+DeployValues.VERSION+", Apartech Ltd, www.apartech.co.uk." );
	    	SDLog.log("Copyright Apartech Ltd ");
			SDLog.log("" );

			SDLog.log( Calendar.getInstance().getTime().toGMTString());
			SDLog.log( "Connection Mode " + deployInfo.getConnectionMode());
			SDLog.log( "Operation Mode : "+  deployInfo.getOperationMode());

			createAdminClient(deployInfo);
		
			appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);
		}catch(Exception e){
			throw new DeployException(e);
			
		}
//	    String query = "WebSphere:type=Cluster,name="+deployInfo.getCluster() +",*";
//		cluster = getObjectName(query);
		
	}

	public void startApplication()
		throws DeployException{
	
		startApplication(mdeployInfo.getApplicationName());	
	}

	public void startApplication(String applicationName)
		throws DeployException{
	
		try {
			if (isAppExists()){
			
				Hashtable prefs = new Hashtable();
			    String started = appManagement.startApplication(applicationName,prefs,null) ;
			    
			    if (null == started){
			        SDLog.log("Application " + applicationName +" cannot be started");
			    }
			    SDLog.log ("Application started on following servers: " + started);
		    
			}else{
			    SDLog.log ("Application not started as application " + mdeployInfo.getApplicationName() +" does not exists : ");
				
			}	
	
		}catch(AdminException e){
			e.printStackTrace();
	    	throw new DeployException(e);
		}
	
	}

		
	public void stopApplication()
		throws DeployException{
	
		stopApplication(mdeployInfo.getApplicationName());	
	}
	
	public void stopApplication(String applicationName)
		throws DeployException{
	
		try {
			
			Hashtable prefs = new Hashtable();
			if (isAppExists()){
			    String stopped = appManagement.stopApplication(applicationName,prefs,null) ;
			    SDLog.log(" Status of stop application " + stopped);
			    if (null == stopped){
			        SDLog.log("Application " + applicationName +" cannot be stopped");
			    }
			    SDLog.log ("Application stopped on following servers: " + stopped);
			}else{
			    SDLog.log ("Application not stopped as application " + mdeployInfo.getApplicationName() +" does not exists : ");
				
			}	
	
	
	    
		}catch(AdminException e){
			e.printStackTrace();
	    	throw new DeployException(e);
		}
	
	}

	private boolean isAppExists()
		throws DeployException{
		boolean isRequired = false;
		try{	
			Hashtable prefs = new Hashtable();
		    logger.trace(" +++++++++++ Checking if application exists " );

			isRequired = appManagement.checkIfAppExists(mdeployInfo.getApplicationName(),prefs,null) ;
		    logger.trace(" +++++++++++ AppExists " + isRequired );
		}catch(AdminException e){
			e.printStackTrace();
	    	throw new DeployException(e);
		}
	    
		return isRequired;
	
	}

	public void exportApplication()
		throws DeployException{

		exportApplication(mdeployInfo.getApplicationName());	
	}

	/**
	 * 
	 * @param deployInfo
	 * @throws AdminException
	 * @throws Exception
	 */
	public void exportApplication(String applicationName)
		throws DeployException{
		
		try{
			
			Hashtable prefs = new Hashtable();
			if (isAppExists()){

				SDLog.log ("Exporting to " + mdeployInfo.getEARExportLocation() + File.separator + applicationName+".ear");
				appManagement.exportApplication(applicationName, mdeployInfo.getEARExportLocation() + File.separator + applicationName+".ear", prefs, null);
			    SDLog.log ("Exported to " + mdeployInfo.getEARExportLocation() + File.separator + applicationName+".ear");

			}else{
			    SDLog.log ("Application not stopped as application " + mdeployInfo.getApplicationName() +" does not exists : ");

			}			
		}catch(AdminException e){
			throw new DeployException(e);
		}catch (Exception e){
			throw new DeployException(e);
		}
	}
	
	/**
	 * 
	 * @throws DeployException
	 */
	public void updateApplication()
		throws DeployException{

		updateApplication(mdeployInfo.getApplicationName());	
	}

	/**
	 * 
	 * @param applicationName
	 * @throws DeployException
	 */
	public void updateApplication(String applicationName)
		throws DeployException{
		
		try{
			
			Hashtable prefs = new Hashtable();
			if (isAppExists()){

			//	SDLog.log ("Exporting to " + mdeployInfo.getEARExportLocation() + File.separator + applicationName+".ear");
				appManagement.updateApplication("easyDeployApp", "C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp.ear", null, "update",prefs, sessionID);
			//    SDLog.log ("Exported to " + mdeployInfo.getEARExportLocation() + File.separator + applicationName+".ear");

			}else{
			    SDLog.log ("Application cannot be updates as application " + mdeployInfo.getApplicationName() +" does not exists : ");

			}			
		}catch(AdminException e){
			throw new DeployException(e);
		}catch (Exception e){
			throw new DeployException(e);
		}
	}

}
