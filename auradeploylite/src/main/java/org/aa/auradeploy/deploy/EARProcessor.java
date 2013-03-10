package org.aa.auradeploy.deploy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.management.ObjectName;

import org.aa.auradeploy.Constants.DeployData;
import org.aa.auradeploy.helper.Helper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagementFactory;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

/**
 * 
 * @author Jatin
 *
 * Copyright (C) 

 */
public class EARProcessor {
	
	private static final Log logger  = LogFactory.getLog(EARProcessor.class);
	private boolean defaultValues;
	//	DeployDataReader deployDataReader;
	
	public void startwork(DeployInfo deployInfo1)
		throws AppDeploymentException, SAXException,IOException,DeployException{
		
    	if (deployInfo1.isMultiEAR()){
    		SDLog.log("Deploy type is MultiEAR");
    		SDLog.log("Deploy location " + deployInfo1.getMultiEARLocation());
    		SDLog.log("");

    		File[] files =  Helper.scanDir(deployInfo1.getMultiEARLocation());
			for (int i=0;i<	files.length;i++){
				
				if ((files[i].getName().endsWith(".ear")) || (files[i].getName().endsWith(".EAR"))){
					String earFileName = files[i].getName();
					String applicationName = earFileName.substring(0,earFileName.indexOf(".ear"));
					deployInfo1.setApplicationName(applicationName);
					deployInfo1.setEARFileLocation(files[i].getAbsolutePath());
					deployInfo1.setDeployDataLocation(deployInfo1.getMultiEARDeployData()+File.separator +applicationName + "-deploydata.xml" );
					deployInfo1.setEnvironmentProperties(deployInfo1.getMultiEARDeployData()+ File.separator + applicationName + ".properties" );
					
					if (Helper.isDeployDataPresent(deployInfo1)){
			    		logger.warn("Application Name " + applicationName);
			    		logger.warn("Ear File " + earFileName);
			    		SDLog.log(" ");
						SDLog.log("Processing EAR for application " + applicationName);
		
						doWork(deployInfo1.getEARFileLocation(),deployInfo1.getDeployDataLocation(),deployInfo1);
					}else{
						doWorkWithOutDD(deployInfo1.getEARFileLocation(),deployInfo1);

						SDLog.log("Processing EAR w/o Depoydata for application " + applicationName);
					
					}

				}
			} 
    	}else{
    		SDLog.log("This is a Single EAR ");
    		SDLog.log("location of EAR is " + deployInfo1.getEARFileLocation());
    		SDLog.log("");

    		doWork(deployInfo1.getEARFileLocation(),deployInfo1.getDeployDataLocation(),deployInfo1);
    	}

		
	}

	
	public void doWorkWithOutDD(String earName,DeployInfo deployInfo)
		throws SAXException,IOException,DeployException{
	
		try {
			Hashtable prefs = new Hashtable(); 
			prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault()); 
			logger.trace("Creating instance of AppDeplController");	
			AppDeploymentController flowController =AppManagementFactory.readArchive (earName, prefs);
			
			logger.trace("Created instance of AppDeplController sucessfully");	
			
			
			AppDeploymentTask task =flowController.getFirstTask();
			 while (task != null){
				//SDLog.log("task.getName()"  + task.getName());
			    if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapWebModToVHTask))){
					logger.trace("doVirtualHost(task)");
			        SDLog.log("Mapping Virtual host to Web Modules");
			    	doVirtualHost(task,deployInfo);
			    }
			    task = flowController.getNextTask();
			} 
			flowController.saveAndClose();
		}catch(AppDeploymentException e){
			e.printStackTrace();
		}
}

	
	public void doWork(String earName,String deploydata,DeployInfo deployInfo)
		throws AppDeploymentException,SAXException,IOException,DeployException{
		
		defaultValues = deployInfo.isDefaultValues();
		
		Hashtable prefs = new Hashtable(); 
		prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		if (defaultValues){
			Properties props = new Properties();
			props.put(AppConstants.APPDEPL_DFLTBNDG , AppConstants.YES_KEY);
	//		 props.put (AppConstants.APPDEPL_DFLTLTBNDG_FORCE, AppConstants.YES_KEY);
			prefs.put (AppConstants.APPDEPL_DFLTBNDG, props);
		}
		DeployDataReader deployDataReader = new DeployDataReader(deploydata,deployInfo);           
		logger.trace("Creating instance of AppDeplController");	
		AppDeploymentController flowController =
		AppManagementFactory.readArchive (earName, prefs); 
		logger.trace("Created instance of AppDeplController sucessfully");	

		AppDeploymentTask task =flowController.getFirstTask();
		 while (task != null){
		    if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapWebModToVHTask))){
		    	if (deployInfo.isDefaultValues()){
					logger.trace("doVirtualHost(task)");
			        SDLog.log("Mapping Virtual host to Web Modules using defaul values");
			    	doVirtualHost(task,deployInfo);
		    	}else{
					logger.trace("doVirtualHost(task)");
			        SDLog.log("Mapping Virtual host to Web Modules using DD");
			    	doVirtualHost(task,deployDataReader,deployInfo);
		    	}
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapResRefToEJBTask))){
				logger.trace("doMapResReftoEJB(task);");	
		        SDLog.log("Mapping Resource refs to Resources");
				doMapResReftoEJB(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.BindJndiForEJBNonMessageBindingTask))){
				logger.trace("doMapEJBJNDINonMessaging(task)");
				SDLog.log("Mapping EJB JNDI names to EJBS");
				doMapEJBJNDINonMessaging(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.BindJndiForEJBMessageBindingTask))){
				logger.trace("doMapEJBJNDIMessaging(task)");	
		        SDLog.log("Mapping EJB refs to MDBs");
				doMapEJBJNDIMessaging(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapEJBRefToEJBTask))){
				logger.trace("doMapEJBRefToEJB(task)");	
				SDLog.log("Mapping EJB refs to EJBs");
				doMapEJBRefToEJB(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapRolesToUsersTask))){
				logger.trace("doMapRolesToUsers(task)");	
				SDLog.log("Mapping Security roles to Groups");
				doMapRolesToUsers(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.DataSourceFor10CMPBeansTask))){
				logger.trace("doCMP1DataSource(task)");	
				SDLog.log("Mapping CMP1 Bean datasources");
		    	doCMP1BeanDataSource(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.DataSourceFor20CMPBeansTask))){
				logger.trace(" doCMP2DataSource(task,deployDataReader)");	
				SDLog.log("Mapping CMP2 Bean datasources");
		    	doCMP2BeanDataSource(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.DataSourceFor10EJBModulesTask ))){
				logger.trace("doCMP2DataSource(task)");	
				SDLog.log("Mapping CMP1 module datasources");
				// TODO convert for version 1.1 CMP
		    	doCMP2ModuleDataSource(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.DataSourceFor20EJBModulesTask))){
				logger.trace("doCMP2DataSource(task)");	
				SDLog.log("Mapping CMP2 module datasources");
		    	doCMP2ModuleDataSource(task,deployDataReader);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapResEnvRefToResTask))){
				logger.trace("doResEnvRefToRes(task)");	
				SDLog.log("Mapping Res Env to Res");
				doResEnvRefToRes(task,deployDataReader);

		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapRunAsRolesToUsersTask ))){
				logger.trace("doMapRunAsRolesToUsersTask(task)");	
				SDLog.log("Mapping Run As Role");
				doMapRunAsRolesToUsersTask(task,deployDataReader);

		    }else if ((task != null) && (task.getName().equalsIgnoreCase("WebServicesClientBindPreferredPort"))){
				logger.trace("WebServicesClientBindPreferredPort");	
				SDLog.log("Mapping WebServicesClientBindPreferredPort");
				doEJBWebServiceBinding(task,deployDataReader);
				//doResEnvRefToRes(task,deployDataReader);
				
		    }else if ((task != null) && (task.getName().equalsIgnoreCase("WebServicesClientBindPortInfo"))){
				logger.trace("WebServicesClientBindPortInfo");	
				SDLog.log("Mapping WebServicesClientBindPortInfo");
				doEJBWebServiceBinding(task,deployDataReader);
				//printColumnNames(task);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase("WebServicesServerCustomProperty"))){
				logger.trace("WebServicesServerCustomProperty");	
				SDLog.log("Mapping WebServicesServerCustomProperty");
				
				//doResEnvRefToRes(task,deployDataReader);
				///printColumnNames(task);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase("WebServicesServerBindPort"))){
				logger.trace("WebServicesServerBindPort");	
				SDLog.log("Mapping WebServicesServerBindPort");
				
				//doResEnvRefToRes(task,deployDataReader);
				//printColumnNames(task);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase("WebServicesPublishWSDLInfo"))){
				logger.trace("WebServicesServerCustomProperty");	
				SDLog.log("Mapping WebServicesServerCustomProperty");
				
				//doResEnvRefToRes(task,deployDataReader);
				//printColumnNames(task);
		    }

		    task = flowController.getNextTask();
		}  
		flowController.saveAndClose();
	}

	private void doMapRunAsRolesToUsersTask(AppDeploymentTask deplTask , DeployDataReader deployDataReader )
	throws AppDeploymentException,DeployException{

    	String[] colName = deplTask.getColumnNames();
	    int role = -1;
	    int username = -1;
	    int password = -1;

		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("role")){
				role = i;
			}
			if (colName[i].equalsIgnoreCase("userName")){
				username= i;
			}
			if (colName[i].equalsIgnoreCase("password")){
				password= i;
			}

		}
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		boolean process = true;
		
		Hashtable mapRunAsRolesToUsers= deployDataReader.getRunAsRolesMapping();
		if ((mapRunAsRolesToUsers.size()== 0)  && defaultValues){
			process = false;		
		}else{
			String[] keys =  (String[]) mapRunAsRolesToUsers.keySet().toArray(new String[0]) ;
			
			String[][] data = deplTask.getTaskData();
	
			if (data != null){
				for (int i=0;i<data.length;i++){
	
					logger.trace("Role name from EAR (role)"+ i + " " + data[i][role]);	
	
					for (int j=0;j<mapRunAsRolesToUsers.size();j++){
						
						logger.trace("Role Name from deploy data role "+ j + " " + keys[j]);
						
						if(( data[i][role] ).equals(keys[j])){
		
							logger.trace("Role Name from deploy data role "+ j + " " + keys[j]);	
							logger.trace("Role name from EAR (role)"+ i + " " + data[i][role]);
							
							Hashtable hs = (Hashtable)mapRunAsRolesToUsers.get(keys[j]);
							
							logger.trace("New Group name from deploy data will be " + mapRunAsRolesToUsers);	
	//						System.out.println("New Group name from deploy data will be " + mapRunAsRolesToUsers);	
		
							data[i][username] = (String)hs.get(DeployData.USERNAME);
							data[i][password] = (String)hs.get(DeployData.USERPASSWORD);
		
		
						}
					}			
				} 
			}
			deplTask.setTaskData(data);
		}
		
	}
	
	
	private void doMapRolesToUsers(AppDeploymentTask deplTask , DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{

    	String[] colName = deplTask.getColumnNames();
	    int role = -1;
	    int roleeveryone = -1;
	    int roleallauthuser = -1;
	    int user = -1;
	    int rolegroup= -1;
	    int roleuser= -1;
	    

		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("role")){
				role = i;
			}
			if (colName[i].equalsIgnoreCase("role.everyone")){
				roleeveryone= i;
			}
			if (colName[i].equalsIgnoreCase("role.all.auth.user")){
				roleallauthuser= i;
			}
			if (colName[i].equalsIgnoreCase("user")){
				user= i;
			}
			if (colName[i].equalsIgnoreCase("role.user")){
				roleuser= i;
			}

			if (colName[i].equalsIgnoreCase("role.group")){
				rolegroup= i;
			}

		}

		/**
		 * Get the data for the colums 
		 * 
		 **/


		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		Hashtable mapNonSplRolesToUsers= deployDataReader.getRolesToGroup();
		Hashtable mapSpecialRoles = deployDataReader.getSpecialRolesMapping();
		boolean process = true;
		if ((mapNonSplRolesToUsers.size()==0) && (mapSpecialRoles.size()==0) && defaultValues){
			process = false;
		}else{
			Hashtable mapRolesToUsers = new Hashtable(); 
			mapRolesToUsers.putAll(mapSpecialRoles );
			mapRolesToUsers.putAll(mapNonSplRolesToUsers);
	
			String[] keys =  (String[]) mapRolesToUsers.keySet().toArray(new String[0]) ;
			
			String[][] data = deplTask.getTaskData();
	
			if (data != null){
				for (int i=0;i<data.length;i++){
					data[i][roleallauthuser] = "AppDeploymentOption.No";
					data[i][roleeveryone] = "AppDeploymentOption.No";
					data[i][rolegroup] = "";			
					data[i][roleuser] = "";
				} 	
	
				for (int i=0;i<data.length;i++){
	
					logger.trace("Role name from EAR (role)"+ i + " " + data[i][role]);	
		
					for (int j=0;j<mapRolesToUsers.size();j++){
						
						logger.trace("Role Name from deploy data role "+ j + " " + keys[j]);	
						if(("'" + data[i][role] + "'").equals(keys[j])){
		
							logger.trace("Role Name from deploy data role "+ j + " " + keys[j]);	
							logger.trace("Role name from EAR (role)"+ i + " " + data[i][role]);	
							logger.trace("New Group name from deploy data will be " + (String)mapRolesToUsers.get(keys[j]));	
		
							if (((String)mapRolesToUsers.get(keys[j])).indexOf("AllAuthenticatedUsers")>=0){
								data[i][roleallauthuser] = "AppDeploymentOption.Yes";
								logger.trace("New Group name of All Authentricated is true " );	
								
							}else{
								data[i][roleallauthuser] = "AppDeploymentOption.No";
							}
							
							if (((String)mapRolesToUsers.get(keys[j])).indexOf("Everyone") >= 0){
								data[i][roleeveryone] = "AppDeploymentOption.Yes";
								logger.trace("New Group name of Everyone  is true " );	
		
							}else{
								data[i][roleeveryone] = "AppDeploymentOption.No";
							}
							
							if ( (((String)mapRolesToUsers.get(keys[j])).indexOf("Everyone")==-1) && (((String)mapRolesToUsers.get(keys[j])).indexOf("AllAuthenticatedUsers")== -1)){
								data[i][rolegroup] = (String)mapRolesToUsers.get(keys[j]);
							}
		
		
						}
					}			
				}
			}
			deplTask.setTaskData(data);	
			}

		}

	private void doVirtualHost(AppDeploymentTask deplTask ,DeployDataReader deployDataReader,DeployInfo deployInfo )
		throws AppDeploymentException,DeployException{
		Hashtable mapWebModToVH = deployDataReader.getWebModule();
		String[] keys =  (String[]) mapWebModToVH.keySet().toArray(new String[0]) ;

		/**
		 * Get the columns numbers that we are interested in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
				
    	String[] colName = deplTask.getColumnNames();
	    int virtualHost = -1;
	    int  uri = -1;

		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}
			if (colName[i].equalsIgnoreCase("virtualHost")){
				virtualHost = i;
			}
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/

    	String[][] data = deplTask.getTaskData();

		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		

    	if (data != null){
			for (int i=0;i<data.length;i++){
	
				for (int j=0;j<mapWebModToVH.size();j++){
	
					if(data[i][uri].equals(keys[j])){
	
						logger.trace("Key from deploy data (web module) "+ j + " " + keys[j]);	
						logger.trace("Data from EAR (web module )"+ i + " " + data[i][virtualHost]);	
						logger.trace("New virtual host name will be " + (String)mapWebModToVH.get(keys[j]));
						
						if ((deployInfo.getVirtualHost()!= null) && (deployInfo.getVirtualHost().trim().length()>0 )){
							data[i][virtualHost] = (String)deployInfo.getVirtualHost();
						}else{
							data[i][virtualHost] = (String)mapWebModToVH.get(keys[j]);
						}
						
	
					}
				}			
			}
    	}
    	if (data!=null){
			for (int i=0;i<data.length;i++){
	
				for (int j=0;j<mapWebModToVH.size();j++){
	
					if(data[i][uri].equals(keys[j])){
	
						logger.trace("Key from deploy data (web module) "+ j + " " + keys[j]);	
						logger.trace("Data from EAR (web module )"+ i + " " + data[i][virtualHost]);	
						logger.trace("New virtual host name will be " + (String)mapWebModToVH.get(keys[j]));	
						if ((deployInfo.getVirtualHost()!= null) && (deployInfo.getVirtualHost().trim().length()>0 )){
							data[i][virtualHost] = (String)deployInfo.getVirtualHost();
						}else{
							data[i][virtualHost] = (String)mapWebModToVH.get(keys[j]);
						}

					}
				}			
			}
    	}
		deplTask.setTaskData(data);	
	}

	private void doVirtualHost(AppDeploymentTask deplTask ,DeployInfo deployInfo )
		throws AppDeploymentException{

		

	/**
	 * Get the columns numbers that we are intersted in i.e 
	 * Web Modules URI
	 * Virtual Host 
	 */
			
	String[] colName = deplTask.getColumnNames();
    int virtualHost = -1;
    int  uri = -1;

	for (int i=0;i<colName.length;i++){
		if (colName[i].equalsIgnoreCase("uri")){
			uri = i;
		}
		if (colName[i].equalsIgnoreCase("virtualHost")){
			virtualHost = i;
		}
	}

	/**
	 * Get the data for the colums 
	 * 
	 **/

	String[][] data = deplTask.getTaskData();

	
	/**
	 * Loop though data and deploy data. When the keys match with column data then 
	 * replace the data with data from deploy data.
	 * 
	 **/
	

	if (data != null){
		for (int i=0;i<data.length;i++){


			logger.trace("Data from EAR (web module )"+ i + " " + data[i][virtualHost]);	
			data[i][virtualHost] = (String)deployInfo.getVirtualHost();

		}
	}
	deplTask.setTaskData(data);	
}

	
	private void doMapEJBJNDIMessaging(AppDeploymentTask deplTask ,DeployDataReader deployDataReader)
		throws AppDeploymentException{


		String[] colName = deplTask.getColumnNames();
		int EJBModule = -1;
		int EJB= -1;
		int uri= -1;
		int JNDI= -1;
		int listenerPort= -1;

		

		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * EJB Modules URI
		 * Current JNDI name 
		 * EJB Name
		 */

		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("EJBModule")){
				EJBModule = i;				
			}else if(colName[i].equalsIgnoreCase("EJB")){
				EJB= i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri= i;
			}else if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI= i;
			}else if (colName[i].equalsIgnoreCase("listenerPort")){
				listenerPort=i;
			} 
			
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		boolean process = true;
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
    	if (data!=null){
			for (int i=0;i<data.length;i++){
				boolean entryexistsInDD = false;

				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					 
					Hashtable mapJNDINametoEJB = deployDataReader.getEJBJNDIForMessaging(Helper.getModuleNameFromURI((String)data[i][uri]));
					if (mapJNDINametoEJB.size()==0 && defaultValues){
						process = false;
					}else{
						String[] keys =  (String[]) mapJNDINametoEJB.keySet().toArray(new String[0]) ;
			
						for (int j=0;j<mapJNDINametoEJB.size();j++){
			
							if(data[i][EJB].equals(keys[j])){
								entryexistsInDD = true;	
								logger.trace("Key from deploy data (Res ref) "+ j + " " + keys[j]);	
								logger.trace("Data from EAR (res ref )"+ i + " " + data[i][EJB]);	
								logger.trace("New JNDI name will be " + (String)mapJNDINametoEJB.get(keys[j]));	
								data[i][listenerPort] = (String)mapJNDINametoEJB.get(keys[j]);
			
							}
						}
						if (!entryexistsInDD){
							logger.error( "Listner mapping missing for " +  data[i][EJB]);
							data[i][listenerPort] = "";
						}
					}
				}
	
			}
    	}
    	if (process){
    		deplTask.setTaskData(data);
    	}else{
    		SDLog.log("Will process with default values in EAR");
    	}
	}
	
		

	private void doMapEJBJNDINonMessaging(AppDeploymentTask deplTask ,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
		
		String[] colName = deplTask.getColumnNames();
		
		int EJBModule = -1;
		int EJB= -1;
		int uri= -1;
		int JNDI= -1;

		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * EJB Modules URI
		 * Current JNDI name 
		 * EJB Name
		 */
		
		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("EJBModule")){
				EJBModule = i;				
			}else if(colName[i].equalsIgnoreCase("EJB")){
				EJB= i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri= i;
			}else if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI= i;
			} 
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		
		
		/**
		 * Loop though data from EAR file and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
		boolean process = true;
    	if (data!=null){
			for (int i=0;i<data.length;i++){
				boolean entryExsistsInDD = false;
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					Hashtable  mapJNDINametoEJB = deployDataReader.getEJBJNDIForNonMessaging(Helper.getModuleNameFromURI((String)data[i][uri]));				

					if ((mapJNDINametoEJB.size()== 0) && defaultValues){
							process = false;		
					}else{
						String[] keys =  (String[]) mapJNDINametoEJB.keySet().toArray(new String[0]) ;
			
						for (int j=0;j<mapJNDINametoEJB.size();j++){
							if(data[i][EJB].equals(keys[j])){
								entryExsistsInDD = true;
								logger.trace("Key from deploy data (Res ref) "+ j + " " + keys[j]);	
								logger.trace("Data from EAR (res ref )"+ i + " " + data[i][EJB]);	
								logger.trace("New JNDI name will be " + (String)mapJNDINametoEJB.get(keys[j]));	
								data[i][JNDI] = (String)mapJNDINametoEJB.get(keys[j]);
			
							}
						}
						if (!entryExsistsInDD){
							logger.error( "JNDI name missing for " +  data[i][EJB]);
							data[i][JNDI] = "";
						}
					}
				}
	
			}
    	}
    	if (process){
    		deplTask.setTaskData(data);
    	}else{
    		SDLog.log("Will process with default values in EAR");
    	}
	}

	private void doMapEJBRefToEJB(AppDeploymentTask deplTask ,DeployDataReader deployDataReader )
		throws AppDeploymentException{

    	String[] colName = deplTask.getColumnNames();
	    int referenceBinding = -1;
	    int uri = -1;
	    int JNDI = -1;
	    int module = -1;

		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Current JNDI name 
		 * reference name
		 */
		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("referenceBinding")){
				referenceBinding = i;				
			}else if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI = i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}else if(colName[i].equalsIgnoreCase("module")){
				module = i;
			}
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
    	String[][] data = deplTask.getTaskData();
    	boolean process = true;
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
    	if (data!=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					
					Hashtable  mapEJBReftoEJB = deployDataReader.getEJBRefs(Helper.getModuleNameFromURI((String)data[i][uri]));
					if ((mapEJBReftoEJB.size() == 0) && defaultValues){
						process = false;
					}else{
						String[] keys =  (String[]) mapEJBReftoEJB.keySet().toArray(new String[0]) ;
			
						for (int j=0;j<mapEJBReftoEJB.size();j++){
			
							if(data[i][referenceBinding].equals(keys[j])){
								entryExistsinDD= true;
								logger.trace("Key from deploy data (Res ref) "+ j + " " + keys[j]);	
								logger.trace("Data from EAR (res ref )"+ i + " " + data[i][referenceBinding]);	
								logger.trace("New JNDI name will be " + (String)mapEJBReftoEJB.get(keys[j]));	
								data[i][JNDI] = (String)mapEJBReftoEJB.get(keys[j]);
							}
						}		
						if (!entryExistsinDD){
							logger.error("EJB reference mapping missing for ejb-ref " + data[i][referenceBinding] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
							data[i][JNDI] = "";
						}
					}
				}
			} 
    	}
    	if (process){
    		deplTask.setTaskData(data);
		}else{
			SDLog.log("Will process with default values in EAR");
		}

	}
	
	
	private void doMapResReftoEJB(AppDeploymentTask deplTask ,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
    	String[] colName = deplTask.getColumnNames();
	    int referenceBinding = -1;
	    int uri = -1;
	    int JNDI = -1;
	    int module = -1;
	    int EJB = -1;

	    
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Current JNDI name 
		 * reference name
		 */
//	    for (int i=0;i<colName.length;i++){
//	    	System.out.println(i + " " + colName[i]);
//	    }
	    
	    for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("referenceBinding")){
				referenceBinding = i;				
			}else if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI = i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}else if(colName[i].equalsIgnoreCase("module")){
				module = i;
			}else if (colName[i].equalsIgnoreCase("EJB")){
				EJB = i;
			}
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
    	String[][] data = deplTask.getTaskData();
    	boolean process = true;
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		


    	if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
//					System.out.println( "+++++++++ module  " + Helper.getModuleNameFromURI((String)data[i][uri]));
//					System.out.println( "+++++++++++++ EJB " + ((String)data[i][EJB]));
//					System.out.println( "+++++++++++++ Module " + ((String)data[i][module]));
//					System.out.println( "+++++++++++++ URI " + ((String)data[i][uri]));

					Hashtable  mapResReftoEJB = deployDataReader.getResRefs(Helper.getModuleNameFromURI((String)data[i][uri]),(String)data[i][EJB]);
					if ((mapResReftoEJB.size()==0) && defaultValues){
						process = false;
					}else{
						String[] keys =  (String[]) mapResReftoEJB.keySet().toArray(new String[0]) ;
			
						for (int j=0;j<mapResReftoEJB.size();j++){
			
							if(data[i][referenceBinding].equals(keys[j])){
								entryExistsinDD= true;		
			
	//							System.out.println( "Key from deploy data (Res ref) "+ j + " " + keys[j]);	
	//							System.out.println( "Data from EAR (res ref )"+ i + " " + data[i][referenceBinding]);	
	//							System.out.println( "New JNDI name will be " + (String)mapResReftoEJB.get(keys[j]));	
								data[i][JNDI] = (String)mapResReftoEJB.get(keys[j]);
			
							}
						}
						if (!entryExistsinDD){
							logger.error("Res reference mapping missing for res-ref " + data[i][referenceBinding] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
							data[i][JNDI] = "";
						}
					}					
				}
	
			} 
    	}
    	if (process){
    		deplTask.setTaskData(data);
    	}else{
    		SDLog.log("Will process with default values in EAR");
    	}
    		
	}

	
	
	private void doResEnvRefToRes(AppDeploymentTask deplTask ,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
		String[] colName = deplTask.getColumnNames();
	    int referenceBinding = -1;
	    int uri = -1;
	    int JNDI = -1;
	    int module = -1;
	
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Current JNDI name 
		 * reference name
		 */
		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("referenceBinding")){
				referenceBinding = i;				
			}else if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI = i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}else if(colName[i].equalsIgnoreCase("module")){
				module = i;
			}
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
    	String[][] data = deplTask.getTaskData();
    	boolean process = true;
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		


    	if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					
					Hashtable  mapResEnvtoRes = deployDataReader.getResEnvRefs(Helper.getModuleNameFromURI((String)data[i][uri]));
					if (mapResEnvtoRes.size()==0 && defaultValues){
						process = false;
					}else{
						String[] keys =  (String[]) mapResEnvtoRes.keySet().toArray(new String[0]) ;
			
						for (int j=0;j<mapResEnvtoRes.size();j++){
			
							if(data[i][referenceBinding].equals(keys[j])){
								entryExistsinDD= true;		
			
								logger.trace("Key from deploy data (Res ref) "+ j + " " + keys[j]);	
								logger.trace("Data from EAR (res ref )"+ i + " " + data[i][referenceBinding]);	
								logger.trace("New JNDI name will be " + (String)mapResEnvtoRes.get(keys[j]));	
								data[i][JNDI] = (String)mapResEnvtoRes.get(keys[j]);
			
							}
						}
						if (!entryExistsinDD){
							logger.error("Res Env  mapping missing for res-env " + data[i][referenceBinding] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
							data[i][JNDI] = "";
						}
					}
				}
	
			} 
    	}
    	if (process){ 
    		deplTask.setTaskData(data);	
    	}else{
    		SDLog.log("Will process with default values in EAR");
    	}

	}
	

	public Hashtable mapModulesToServer(String server, String webServer,DeployInfo deployInfo, String webServerString)
		throws IOException,SAXException,DeployException,AppDeploymentException,ConnectorException,ConfigServiceException {
		
		Vector modules =  new Vector();
		Vector webModules =  new Vector();
		HashMap webModulesToWebServer = new HashMap();
		
		// check if deploydata exists, if it does then get list of ejb and webmodules from deploydata	
		if ( Helper.isDeployDataPresent(deployInfo) && !deployInfo.isDefaultValues() ) {
			logger.trace("Deploy data is present");
			DeployDataReader deployDataReader = new DeployDataReader(deployInfo.getDeployDataLocation(),deployInfo);
			modules =  deployDataReader.getEJBModules();
			webModulesToWebServer  =  deployDataReader.getWebModules();
			String[] temp=  (String[])webModulesToWebServer.keySet().toArray(new String[0]);
			for (int i = 0; i < temp.length ; i++){
				webModules.add(temp[i]);
			}
			// else get list of ejb and webmodules from ear file

		}else{
			logger.trace("Deploy data is not present");
			Hashtable hs =  getModulesFromEAR(deployInfo);	
			modules =  (Vector)hs.get("ejbModules");
			webModules =  (Vector)hs.get("webModules");

		}

		Hashtable mapModulesToServer = new Hashtable();
		
		// map ejb modules to application server/cluster only 
		for (int i=0;i<modules .size();i++){
			logger.trace(modules .get(i) + " to " + server);

			mapModulesToServer.put(modules.get(i),server);
		}
		//map web modules to application server/cluster and webserver if specified

		//condition if webserver is specified on the slickdeploy command
		logger.trace(" Web Module from slickdeploy command is " + webServer);
		if ((webServer != null) && (webServer.trim().length()>0 )){
			
			for (int i=0;i<webModules.size();i++){
				logger.trace(webModules .get(i) + " to " + server + "+" + webServer);
				logger.trace(" Mapping Web Module from command " + webServer);
				// if node name is specified then use that node name for webserver
				if (deployInfo.getNode() !=null){
					mapModulesToServer.put(webModules.get(i), server + "+" + "WebSphere:cell="+deployInfo.getCell()+",node="+deployInfo.getNode()+",server="+webServer );
				}else{ // since node name is not specified look up all the webservers for with the given name and map
					mapModulesToServer.put(webModules.get(i), server + "+" + webServerString);
					
				}
				
			}
			//if deploy data exists and webserver is specified in deploydata alongside webmodule, 
			// This is to allow webmodules in application to be bound to different webservers.

		}else if (Helper.isDeployDataPresent(deployInfo)){
			
			for (int i=0;i<webModules.size();i++){
				logger.trace(webModules.get(i) + " to " + server);
				logger.trace(" Mapping Web Module from deploy data command " + webModules.get(i).toString());

				logger.trace(" Getting the Web Module for " + webModules.get(i).toString());

				if ((webModulesToWebServer.get(webModules.get(i).toString())!=null) && (webModulesToWebServer.get(webModules.get(i).toString()).toString().trim().length()  > 0 )){
//					This is to allow landg to pass fully qualified webserver name, so that same application can be deployed to different nodes 
//					mapModulesToServer.put(webModules.get(i), server + "+" + "WebSphere:cell="+deployInfo.getCell()+",node="+deployInfo.getNode()+",server="+ webModulesToWebServer.get(webModules.get(i).toString()).toString());
					System.out.print(" Mapping the Web Module for " + webModules.get(i).toString() + " to "+ webModulesToWebServer.get(webModules.get(i).toString()).toString());
					mapModulesToServer.put(webModules.get(i), server + "+" + webModulesToWebServer.get(webModules.get(i).toString()).toString());
					
				}else{
					mapModulesToServer.put(webModules.get(i), server);
				}
			}
			
			//if deploy data does not exists and webserver is not specified on slickdeploy command line, 
			
		}else{
			for (int i=0;i<webModules.size();i++){
				logger.trace(webModules .get(i) + " to " + server + "+" + webServer);

				mapModulesToServer.put(webModules.get(i), server );
			}
	
		}
		
		return mapModulesToServer;
	}

	
	
	private void doCMP2BeanDataSource(AppDeploymentTask deplTask,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
    	String[] colName = deplTask.getColumnNames();

	    int uri = -1;
	    int EJB = -1;
	    int JNDI = -1;
	    int module = -1;

		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * EJB Modules URI
		 * Current JNDI name 
		 * reference name
		 */
		for (int i=0;i<colName.length;i++){
			if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI = i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}else if(colName[i].equalsIgnoreCase("module")){
				module = i;
			}else if(colName[i].equalsIgnoreCase("EJB")){
				EJB = i;
			}

		} 

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData(); 
		boolean process = true;
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
    	
    	if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					
					Hashtable  mapCMPEJBToJNDI= deployDataReader.getEJBCMP2Datasource(Helper.getModuleNameFromURI((String)data[i][uri]));
					if (mapCMPEJBToJNDI.size()==00 && defaultValues){
						process = false;
					}else{
						String[] keys =  (String[])mapCMPEJBToJNDI.keySet().toArray(new String[0]) ;
						for (int j=0;j<mapCMPEJBToJNDI.size();j++){
							if(data[i][EJB].equals(keys[j])){
								entryExistsinDD= true;		
			
								logger.trace("Key from deploy data (Res ref) "+ j + " " + keys[j]);	
								logger.trace("Data from EAR (res ref )"+ i + " " + data[EJB]);	
								logger.trace("New JNDI name will be " + (String)mapCMPEJBToJNDI.get(keys[j]));	
								data[i][JNDI] = (String)mapCMPEJBToJNDI.get(keys[j]);
			
							}
						}
						if (!entryExistsinDD){
							logger.error("CMP EJB to JNDI name missing for " + data[i][EJB] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
							data[i][JNDI] = "";
						}
					}					
				}
	
			} 
    	}
    	if (process){ 
    		deplTask.setTaskData(data);
    	}else{
    		SDLog.log("Will process with default values in EAR");
    	}
    		
	}

	private void doCMP1BeanDataSource(AppDeploymentTask deplTask,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
		String[] colName = deplTask.getColumnNames();
	
	    int uri = -1;
	    int EJB = -1;
	    int JNDI = -1;
	    int module = -1;
	
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * EJB Modules URI
		 * Current JNDI name 
		 * reference name
		 */
		for (int i=0;i<colName.length;i++){
			if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI = i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}else if(colName[i].equalsIgnoreCase("module")){
				module = i;
			}else if(colName[i].equalsIgnoreCase("EJB")){
				EJB = i;
			}
	
		} 
	
		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData(); 
		boolean process = true;
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
		
		if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					
					Hashtable  mapCMPEJBToJNDI= deployDataReader.getEJBCMP1Datasource(Helper.getModuleNameFromURI((String)data[i][uri]));
					if (mapCMPEJBToJNDI.size()==0 && defaultValues){
						process = false;
					}else{
						String[] keys =  (String[])mapCMPEJBToJNDI.keySet().toArray(new String[0]) ;
			
						for (int j=0;j<mapCMPEJBToJNDI.size();j++){
			
							if(data[i][EJB].equals(keys[j])){
								entryExistsinDD= true;		
			
								logger.trace("Key from deploy data (Res ref) "+ j + " " + keys[j]);	
								logger.trace("Data from EAR (res ref )"+ i + " " + data[EJB]);	
								logger.trace("New JNDI name will be " + (String)mapCMPEJBToJNDI.get(keys[j]));	
								data[i][JNDI] = (String)mapCMPEJBToJNDI.get(keys[j]);
			
							}
						}
						if (!entryExistsinDD){
							logger.error("CMP EJB to JNDI name missing for " + data[i][EJB] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
							data[i][JNDI] = "";
						}
					}				
				}
	
			} 
		}
		if (process){
			deplTask.setTaskData(data);
		}else{
			SDLog.log("Will process with default values in EAR");
		}
		
	}


	private void doCMP2ModuleDataSource(AppDeploymentTask deplTask,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
		String[] colName = deplTask.getColumnNames();
		
		int uri = -1;
		int EJBModule = -1;
		int JNDI = -1;
		
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * EJB Modules URI
		 * Current JNDI name 
		 * reference name
		 */
		for (int i=0;i<colName.length;i++){
			if(colName[i].equalsIgnoreCase("JNDI")){
				JNDI = i;
			}else if(colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}else if(colName[i].equalsIgnoreCase("EJBModule")){
				EJBModule = i;
			}

		} 

		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		boolean process = true;
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
	
		
		
		if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				
				if ((data[i][uri] != null)	&& (((String)data[i][uri]).trim().length()!=0) && (!data[i][uri].equalsIgnoreCase("uri"))){		
					String CMPModToJNDI = deployDataReader.getCMPDSForEJBModule(Helper.getModuleNameFromURI((String)data[i][uri]));
					if (CMPModToJNDI!=null) {
						data[i][JNDI] = CMPModToJNDI;
					}else{
						logger.error(" Default datasource for the missing for module " + (Helper.getModuleNameFromURI((String)data[i][uri])));	
					}
		
				}
					

			} 
		}
		deplTask.setTaskData(data); 	
	}		
	
	public  Vector checkIfResourcesExists (DeployInfo deployInfo, ConfigService configService,Session session)
		throws DeployException,IOException,SAXException {
		Vector missingResources =  new Vector();
		if (Helper.isDeployDataPresent(deployInfo)){
			DeployDataReader deployDataReaderforRes = new DeployDataReader(deployInfo.getDeployDataLocation(),deployInfo);
			Vector resources =  deployDataReaderforRes.getAllResources();
			
			
			Vector jndiNames = new Vector();
			try {
				
				ObjectName scope = null;
				
				// get the all the datasources
				ObjectName pattern = ConfigServiceHelper.createObjectName(null,"DataSource");
				ObjectName[] datasource = configService.queryConfigObjects(session,scope,pattern,null);
				for (int i=0;i<datasource.length;i++ ){
					String jndiName = configService.getAttribute(session,datasource[i],"jndiName").toString();
					jndiNames.add(jndiName );
				}
				
				//get all the MQ Queue
				pattern = ConfigServiceHelper.createObjectName(null,"MQQueue");
				ObjectName[] MQQueue = configService.queryConfigObjects(session,scope,pattern,null);			
				for (int i=0;i<MQQueue.length;i++ ){
					String jndiName = configService.getAttribute(session,MQQueue[i],"jndiName").toString();
					jndiNames.add(jndiName );
				}
	
				// get all MQQueueConnectionFactory
				pattern = ConfigServiceHelper.createObjectName(null,"MQQueueConnectionFactory");
				ObjectName[] MQQueueConnectionFactory = configService.queryConfigObjects(session,scope,pattern,null);			
				for (int i=0;i<MQQueueConnectionFactory.length;i++ ){
					String jndiName = configService.getAttribute(session,MQQueueConnectionFactory[i],"jndiName").toString();
					jndiNames.add(jndiName );
				}
	
				// get all MQTopic
				pattern = ConfigServiceHelper.createObjectName(null,"MQTopic");
				ObjectName[] MQTopic = configService.queryConfigObjects(session,scope,pattern,null);			
				for (int i=0;i<MQTopic.length;i++ ){
					String jndiName = configService.getAttribute(session,MQTopic[i],"jndiName").toString();
					jndiNames.add(jndiName );
				}
	
				// get all MQTopicConnectionFactory
				pattern = ConfigServiceHelper.createObjectName(null,"MQTopicConnectionFactory");
				ObjectName[] MQTopicConnectionFactory = configService.queryConfigObjects(session,scope,pattern,null);			
				for (int i=0;i<MQTopicConnectionFactory.length;i++ ){
					String jndiName = configService.getAttribute(session,MQTopicConnectionFactory[i],"jndiName").toString();
					jndiNames.add(jndiName );
				}
	
			}catch(ConnectorException e){
				e.printStackTrace();
				throw new DeployException(e );
				
			}catch(ConfigServiceException e){
				e.printStackTrace();
				throw new DeployException(e );
			}
			
	        for (int j=0; j<resources.size();j++ ){
	    		boolean exists = false;
	        	logger.debug( "Checking if " + resources.get(j).toString() +" exists"); 
	            if (jndiNames.size()>0){
					for (int k=0;k<jndiNames.size();k++ ){
	            		if ((resources.get(j).toString()).equalsIgnoreCase(jndiNames.get(k).toString())){
	            			exists = true;
	            		}
				//		System.out.println("NAME IS " + ds.getCanonicalName());
	//					System.out.println("JNDI NAME IS " + jndiNames.get(k));
		            }
		            if (exists == false){
		            	missingResources.add(resources.get(j));
		            } 
	            }
	        }
		}
		return missingResources;
			
	}

	public  Vector checkIfInfraResourcesExists (DeployInfo deployInfo, ConfigService configService,Session session)
		throws DeployException,IOException,SAXException {
		DeployDataReader deployDataReaderforRes = new DeployDataReader(deployInfo.getDeployDataLocation(),deployInfo);
		Vector resources =  deployDataReaderforRes.getDataSources();
	
		Vector missingResources =  new Vector();
		Vector jndiNames = new Vector();
		try {
			
			ObjectName scope = null;
			
			// get the all the datasources
			ObjectName pattern = ConfigServiceHelper.createObjectName(null,"DataSource");
			ObjectName[] datasource = configService.queryConfigObjects(session,scope,pattern,null);
			for (int i=0;i<datasource.length;i++ ){
				String jndiName = configService.getAttribute(session,datasource[i],"jndiName").toString();
				jndiNames.add(jndiName );
			}
			
			//get all the MQ Queue
			pattern = ConfigServiceHelper.createObjectName(null,"MQQueue");
			ObjectName[] MQQueue = configService.queryConfigObjects(session,scope,pattern,null);			
			for (int i=0;i<MQQueue.length;i++ ){
				String jndiName = configService.getAttribute(session,MQQueue[i],"jndiName").toString();
				jndiNames.add(jndiName );
			}

			// get all MQQueueConnectionFactory
			pattern = ConfigServiceHelper.createObjectName(null,"MQQueueConnectionFactory");
			ObjectName[] MQQueueConnectionFactory = configService.queryConfigObjects(session,scope,pattern,null);			
			for (int i=0;i<MQQueueConnectionFactory.length;i++ ){
				String jndiName = configService.getAttribute(session,MQQueueConnectionFactory[i],"jndiName").toString();
				jndiNames.add(jndiName );
			}

			// get all MQTopic
			pattern = ConfigServiceHelper.createObjectName(null,"MQTopic");
			ObjectName[] MQTopic = configService.queryConfigObjects(session,scope,pattern,null);			
			for (int i=0;i<MQTopic.length;i++ ){
				String jndiName = configService.getAttribute(session,MQTopic[i],"jndiName").toString();
				jndiNames.add(jndiName );
			}

			// get all MQTopicConnectionFactory
			pattern = ConfigServiceHelper.createObjectName(null,"MQTopicConnectionFactory");
			ObjectName[] MQTopicConnectionFactory = configService.queryConfigObjects(session,scope,pattern,null);			
			for (int i=0;i<MQTopicConnectionFactory.length;i++ ){
				String jndiName = configService.getAttribute(session,MQTopicConnectionFactory[i],"jndiName").toString();
				jndiNames.add(jndiName );
			}

		}catch(ConnectorException e){
			e.printStackTrace();
			throw new DeployException(e );
			
		}catch(ConfigServiceException e){
			e.printStackTrace();
			throw new DeployException(e );
		}

		for (int j=0; j<resources.size();j++ ){
        	Hashtable hs = (Hashtable)resources.get(j);
        	
    		boolean exists = false;
        	logger.debug( "Checking if " + resources.get(j).toString() +" exists"); 
            if (jndiNames.size()>0){
				for (int k=0;k<jndiNames.size();k++ ){
            		if (hs.get("jndiName").toString().equalsIgnoreCase(jndiNames.get(k).toString())){
            			exists = true;
            		}
			//		System.out.println("NAME IS " + ds.getCanonicalName());
//					System.out.println("JNDI NAME IS " + jndiNames.get(k));
	            }
	            if (exists == false){
	            	missingResources.add(resources.get(j));
	            } 
            }
        }

	return missingResources;
		
}

	public  boolean shouldGenerateDeployCode (DeployInfo deployInfo)
	throws DeployException,IOException,SAXException {
		boolean shouldGenerateEJBCode = false;
		if (Helper.isDeployDataPresent(deployInfo)){
			DeployDataReader deployDataReaderforEJBCode = new DeployDataReader(deployInfo.getDeployDataLocation(),deployInfo);
			shouldGenerateEJBCode =  deployDataReaderforEJBCode.getGenerateEJBDeployCode();
		}
		return shouldGenerateEJBCode;
	}
	
	public Hashtable getModulesFromEAR(DeployInfo deployInfo)
			throws AppDeploymentException{
		
		EARProcessor earProcessor = new EARProcessor();
		Hashtable prefs = new Hashtable(); 
		prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault()); 
		AppDeploymentController flowController;
		if (deployInfo.isMultiEAR()){
			flowController =
		AppManagementFactory.readArchive ( deployInfo.getMultiEARLocation() + File.separator + deployInfo.getApplicationName() + ".ear" , prefs); 
		}else{
			flowController =
				AppManagementFactory.readArchive ( deployInfo.getEARFileLocation() , prefs); 
			
		}
		AppDeploymentTask task =flowController.getFirstTask();
		Vector webModules = new Vector();
		Vector allModules = new Vector();
		 while (task != null){
		    if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapModulesToServersTask))){
		        allModules = earProcessor.doModuleToServers(task);
		    }else if ((task != null) && (task.getName().equalsIgnoreCase(AppConstants.MapWebModToVHTask))){
		        webModules = earProcessor.doVirtualHost(task);

		    }
		    task = flowController.getNextTask();
		} 
		flowController.saveAndClose();	
		
		Hashtable hs = new Hashtable(); 
		allModules.removeAll(webModules);
		Vector newEjbModules = new Vector();
		Vector newWebModules = new Vector();
		
		for (int i=0;i<allModules.size();i++){
			String ejbModule  = (String)allModules.get(i);
			ejbModule = ejbModule.replace(',' , '+');
			newEjbModules.add(ejbModule);
		}
		
		for (int i=0;i<webModules.size();i++){
			String webModule  = (String)webModules.get(i);
			webModule = webModule.replace(',','+') ;
			newWebModules.add(webModule);
		}
		
		hs.put("ejbModules", newEjbModules);	
		hs.put("webModules", newWebModules);
		return hs;
	}

	private Vector doVirtualHost(AppDeploymentTask deplTask )
		throws AppDeploymentException{
		Vector v = new Vector();
		/**
		 * Get the columns numbers that we are interested in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
				
		String[] colName = deplTask.getColumnNames();
	    int virtualHost = -1;
	    int uri = -1;
	    int webModule = -1;

	    for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}
			if (colName[i].equalsIgnoreCase("virtualHost")){
				virtualHost = i;
			}
			if (colName[i].equalsIgnoreCase("webModule")){
				webModule = i;
			}
			
		}

		/**
		 * Get the data for the colums 
		 * 
		 **/
	
		String[][] data = deplTask.getTaskData();
	
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		

		if (data != null){
			for (int i=0;i<data.length;i++){
				v.add(data[i][uri]);	
			}
		}
		return v;
	}

	private Vector doModuleToServers(AppDeploymentTask deplTask )
		throws AppDeploymentException{
		Vector v = new Vector();
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
				
		String[] colName = deplTask.getColumnNames();
		int server = -1;
		int  uri = -1;
		int  module = -1;
		
		for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}
			if (colName[i].equalsIgnoreCase("module")){
				module = i;
			}
			if (colName[i].equalsIgnoreCase("server")){
				server = i;
			}
	
		}
		
		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
		
		if (data != null){
			for (int i=0;i<data.length;i++){
				v.add(data[i][uri]);	
			}
		} 
		return v;
	} 

	private void printColumnNames(AppDeploymentTask deplTask )
		throws AppDeploymentException{
		Vector v = new Vector();
		/**
		 * Get the columns numbers that we are interested in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
				
		String[] colName = deplTask.getColumnNames();
	    int virtualHost = -1;
	    int uri = -1;
	    int webModule = -1;
	
	    for (int i=0;i<colName.length;i++){
	    	System.out.println( "column number " + i + " " + colName[i]);
		}
	}
	
	
	private void doEJBWebServiceBinding(AppDeploymentTask deplTask ,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
		String[] colName = deplTask.getColumnNames();
	    int cfgbnd_Module_Name = -1;
	    int cfgbnd_EJB = -1;
	    int cfgbnd_Web_Service = -1;
	    int cfgbnd_Port = -1;
	    int cfgbnd_Port_Type = -1;
	    int cfgbnd_Timeout = -1;
	    int cfgbnd_BasicAuth_ID = -1;
	    int cfgbnd_BasicAuth_Password = -1;
	    int cfgbnd_SSL_Config = -1;
	    int cfgbnd_Overridden_Endpoint = -1;
	    int cfgbnd_Overridden_BindingNamespace = -1;
	
	    
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Current JNDI name 
		 * reference name
		 */
	//    for (int i=0;i<colName.length;i++){
	//    	System.out.println(i + " " + colName[i]);
	//    }

	    for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Module_Name")){
				cfgbnd_Module_Name = i;				
			}else if(colName[i].equalsIgnoreCase("webservices.cfgbnd_EJB")){
				cfgbnd_EJB = i;
			}else if(colName[i].equalsIgnoreCase("webservices.cfgbnd_Web_Service")){
				cfgbnd_Web_Service = i;
			}else if(colName[i].equalsIgnoreCase("webservices.cfgbnd_Timeout")){
				cfgbnd_Timeout = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_BasicAuth_ID")){
				cfgbnd_BasicAuth_ID = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_BasicAuth_Password")){
				cfgbnd_BasicAuth_Password= i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_SSL_Config")){
				cfgbnd_SSL_Config = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Overridden_Endpoint")){
				cfgbnd_Overridden_Endpoint = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Overridden_BindingNamespace")){
				cfgbnd_Overridden_BindingNamespace = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Port")){
				cfgbnd_Port = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Port_Type")){
				cfgbnd_Port_Type = i;
			}
			
		}
	     logger.trace(" column webservices.cfgbnd_Port " + cfgbnd_Port);
		 logger.trace(" column webservices.cfgbnd_Module_Name number " + cfgbnd_Module_Name);
		 logger.trace(" column webservices.cfgbnd_EJB number " + cfgbnd_EJB);
		 logger.trace(" column webservices.cfgbnd_Web_Service number " + cfgbnd_Web_Service);
		 logger.trace(" column webservices.cfgbnd_Timeout number " + cfgbnd_Timeout);
		 logger.trace(" column webservices.cfgbnd_BasicAuth_ID number " + cfgbnd_BasicAuth_ID);
		 logger.trace(" column webservices.cfgbnd_BasicAuth_Password number " + cfgbnd_BasicAuth_Password);
		 logger.trace(" column webservices.cfgbnd_SSL_Config number " + cfgbnd_SSL_Config);
		 logger.trace(" column webservices.cfgbnd_Overridden_Endpoint number " + cfgbnd_Overridden_Endpoint);
		 logger.trace(" column webservices.cfgbnd_Overridden_BindingNamespace number " + cfgbnd_Overridden_BindingNamespace);
		 logger.trace(" column webservices.cfgbnd_Port_Type number " + cfgbnd_Port_Type);
		 
		 
	    
		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
	
	
		if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				logger.trace("Checking if value in cfgbnd_Module_Name column:" + data[i][cfgbnd_Module_Name]);
				if ((data[i][cfgbnd_Module_Name] != null)	&& (((String)data[i][cfgbnd_Module_Name]).trim().length()!=0) && (!data[i][cfgbnd_Module_Name].equalsIgnoreCase("webservices.cfgbnd_Module_Name"))){		
					// System.out.println( "+++++++++++++ Port " + ((String)data[i][cfgbnd_Port]));
					// System.out.println( "+++++++++ module  " + Helper.getModuleNameFromURI((String)data[i][cfgbnd_Module_Name]));

					
					Hashtable  mapEJBWebServices = deployDataReader.getEJBWebServiceBinding(data[i][cfgbnd_Module_Name],data[i][cfgbnd_Web_Service]);			
					

					if (mapEJBWebServices!=null && mapEJBWebServices.size()>0){
						logger.trace("got webservice info from deploy data " );
						entryExistsinDD= true;

						if(mapEJBWebServices.get(DeployData.TIMEOUT)!=null){
							logger.trace(" Got DeployData.TIMEOUT " + mapEJBWebServices.get(DeployData.TIMEOUT));
							data[i][cfgbnd_Timeout] = mapEJBWebServices.get(DeployData.TIMEOUT).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.BASICAUTHID)!=null){
							logger.trace(" Got DeployData.BASICAUTHID " + mapEJBWebServices.get(DeployData.BASICAUTHID));
							data[i][cfgbnd_BasicAuth_ID ] = mapEJBWebServices.get(DeployData.BASICAUTHID).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.BASICAUTHPASSWORD)!=null){
							logger.trace(" Got DeployData.BASICAUTHPASSWORD ");
							data[i][cfgbnd_BasicAuth_Password ] = mapEJBWebServices.get(DeployData.BASICAUTHPASSWORD).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.OVERRIDDENENDPOINT)!=null){
							logger.trace(" Got DeployData.OVERRIDDENENDPOINT " + mapEJBWebServices.get(DeployData.OVERRIDDENENDPOINT));
							data[i][cfgbnd_Overridden_Endpoint ] = mapEJBWebServices.get(DeployData.OVERRIDDENENDPOINT).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.SSLCONFIG)!=null){
							logger.trace(" Got DeployData.cfgbnd_SSL_Config " + mapEJBWebServices.get(DeployData.SSLCONFIG));
							data[i][cfgbnd_SSL_Config] = mapEJBWebServices.get(DeployData.SSLCONFIG).toString();
						}

						if(mapEJBWebServices.get(DeployData.PORT)!=null){
							logger.trace(" Got DeployData.cfgbnd_PORT " + mapEJBWebServices.get(DeployData.PORT));
							data[i][cfgbnd_Port] = mapEJBWebServices.get(DeployData.PORT).toString();
						}

						if(mapEJBWebServices.get(DeployData.PORTTYPE)!=null){
							logger.trace(" Got DeployData.cfgbnd_SSL_Config " + mapEJBWebServices.get(DeployData.PORTTYPE));
							data[i][cfgbnd_Port_Type] = mapEJBWebServices.get(DeployData.PORTTYPE).toString();
						}

					}else{
						logger.trace("no webservice info from deploy data " );
					}
					
/**					if (!entryExistsinDD){
						logger.error("Res reference mapping missing for res-ref " + data[i][referenceBinding] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
						data[i][JNDI] = "";
					}
	**/				
				}
	
			} 
		}
		deplTask.setTaskData(data);	
	}

	private void doEJBWebServicesClientBindPreferredPort(AppDeploymentTask deplTask ,DeployDataReader deployDataReader )
		throws AppDeploymentException,DeployException{
		String[] colName = deplTask.getColumnNames();
	    int cfgbnd_Module_Name = -1;
	    int cfgbnd_EJB = -1;
	    int cfgbnd_Web_Service = -1;
	    int cfgbnd_Port = -1;
	    int cfgbnd_Timeout = -1;
	    int cfgbnd_BasicAuth_ID = -1;
	    int cfgbnd_BasicAuth_Password = -1;
	    int cfgbnd_SSL_Config = -1;
	    int cfgbnd_Overridden_Endpoint = -1;
	    int cfgbnd_Overridden_BindingNamespace = -1;
	
	    
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Current JNDI name 
		 * reference name
		 */
	//    for (int i=0;i<colName.length;i++){
	//    	System.out.println(i + " " + colName[i]);
	//    }

	    for (int i=0;i<colName.length;i++){
			if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Module_Name")){
				cfgbnd_Module_Name = i;				
			}else if(colName[i].equalsIgnoreCase("webservices.cfgbnd_EJB")){
				cfgbnd_EJB = i;
			}else if(colName[i].equalsIgnoreCase("webservices.cfgbnd_Web_Service")){
				cfgbnd_Web_Service = i;
			}else if(colName[i].equalsIgnoreCase("webservices.cfgbnd_Timeout")){
				cfgbnd_Timeout = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_BasicAuth_ID")){
				cfgbnd_BasicAuth_ID = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_BasicAuth_Password")){
				cfgbnd_BasicAuth_Password= i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_SSL_Config")){
				cfgbnd_SSL_Config = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Overridden_Endpoint")){
				cfgbnd_Overridden_Endpoint = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Overridden_BindingNamespace")){
				cfgbnd_Overridden_BindingNamespace = i;
			}else if (colName[i].equalsIgnoreCase("webservices.cfgbnd_Port")){
				cfgbnd_Port = i;
			}
		}
	     logger.trace(" column webservices.cfgbnd_Port " + cfgbnd_Port);
		 logger.trace(" column webservices.cfgbnd_Module_Name number " + cfgbnd_Module_Name);
		 logger.trace(" column webservices.cfgbnd_EJB number " + cfgbnd_EJB);
		 logger.trace(" column webservices.cfgbnd_Web_Service number " + cfgbnd_Web_Service);
		 logger.trace(" column webservices.cfgbnd_Timeout number " + cfgbnd_Timeout);
		 logger.trace(" column webservices.cfgbnd_BasicAuth_ID number " + cfgbnd_BasicAuth_ID);
		 logger.trace(" column webservices.cfgbnd_BasicAuth_Password number " + cfgbnd_BasicAuth_Password);
		 logger.trace(" column webservices.cfgbnd_SSL_Config number " + cfgbnd_SSL_Config);
		 logger.trace(" column webservices.cfgbnd_Overridden_Endpoint number " + cfgbnd_Overridden_Endpoint);
		 logger.trace(" column webservices.cfgbnd_Overridden_BindingNamespace number " + cfgbnd_Overridden_BindingNamespace);
		 
		 
	    
		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/
		
	
	
		if (data !=null){
			for (int i=0;i<data.length;i++){
				boolean entryExistsinDD= false;		
				logger.trace("Checking if value in cfgbnd_Module_Name column:" + data[i][cfgbnd_Module_Name]);
				if ((data[i][cfgbnd_Module_Name] != null)	&& (((String)data[i][cfgbnd_Module_Name]).trim().length()!=0) && (!data[i][cfgbnd_Module_Name].equalsIgnoreCase("webservices.cfgbnd_Module_Name"))){		
					// System.out.println( "+++++++++++++ Port " + ((String)data[i][cfgbnd_Port]));
					// System.out.println( "+++++++++ module  " + Helper.getModuleNameFromURI((String)data[i][cfgbnd_Module_Name]));

					
					Hashtable  mapEJBWebServices = deployDataReader.getEJBWebServiceBinding(data[i][cfgbnd_Module_Name],data[i][cfgbnd_Web_Service]);			
					

					if (mapEJBWebServices!=null && mapEJBWebServices.size()>0){
						logger.trace("got webservice info from deploy data " );
						entryExistsinDD= true;

						if(mapEJBWebServices.get(DeployData.TIMEOUT)!=null){
							logger.trace(" Got DeployData.TIMEOUT " + mapEJBWebServices.get(DeployData.TIMEOUT));
							data[i][cfgbnd_Timeout] = mapEJBWebServices.get(DeployData.TIMEOUT).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.BASICAUTHID)!=null){
							logger.trace(" Got DeployData.BASICAUTHID " + mapEJBWebServices.get(DeployData.BASICAUTHID));
							data[i][cfgbnd_BasicAuth_ID ] = mapEJBWebServices.get(DeployData.BASICAUTHID).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.BASICAUTHPASSWORD)!=null){
							logger.trace(" Got DeployData.BASICAUTHPASSWORD ");
							data[i][cfgbnd_BasicAuth_Password ] = mapEJBWebServices.get(DeployData.BASICAUTHPASSWORD).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.OVERRIDDENENDPOINT)!=null){
							logger.trace(" Got DeployData.OVERRIDDENENDPOINT " + mapEJBWebServices.get(DeployData.OVERRIDDENENDPOINT));
							data[i][cfgbnd_Overridden_Endpoint ] = mapEJBWebServices.get(DeployData.OVERRIDDENENDPOINT).toString();
						}
						
						if(mapEJBWebServices.get(DeployData.SSLCONFIG)!=null){
							logger.trace(" Got DeployData.cfgbnd_SSL_Config " + mapEJBWebServices.get(DeployData.SSLCONFIG));
							data[i][cfgbnd_SSL_Config] = mapEJBWebServices.get(DeployData.SSLCONFIG).toString();
						}

					}else{
						logger.trace("no webservice info from deploy data " );
					}
					
/**					if (!entryExistsinDD){
						logger.error("Res reference mapping missing for res-ref " + data[i][referenceBinding] + " in module" + Helper.getModuleNameFromURI((String)data[i][uri]));
						data[i][JNDI] = "";
					}
	**/				
				}
	
			} 
		}
		deplTask.setTaskData(data);	
	}

	
	public Vector doMapModulesToServers(AppDeploymentTask deplTask,DeployInfo deployInfo )
		throws AppDeploymentException{
		Vector v = new Vector();
		/**
		 * Get the columns numbers that we are intersted in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
				
		String[] colName = deplTask.getColumnNames();
		int server = -1;
		int  uri = -1;
		int  module = -1;
		
		for (int i=0;i<colName.length;i++){
			SDLog.log(colName[i]);
			if (colName[i].equalsIgnoreCase("uri")){
				uri = i;
			}
			if (colName[i].equalsIgnoreCase("module")){
				module = i;
			}
			if (colName[i].equalsIgnoreCase("server")){
				server = i;
			}
	
		}
		
		/**
		 * Get the data for the colums 
		 * 
		 **/
		
		String[][] data = deplTask.getTaskData();
		
		
		/**
		 * Loop though data and deploy data. When the keys match with column data then 
		 * replace the data with data from deploy data.
		 * 
		 **/

		Hashtable hs =  getModulesFromEAR(deployInfo);	
		Vector modules =  (Vector)hs.get("ejbModules");
		Vector webModules =  (Vector)hs.get("webModules");

		
		if (data != null){
			for (int i=0;i<data.length;i++){
				SDLog.log((data[i][module]) +" - "+ (data[i][uri]) + " - "+ (data[i][server]));
				if (data[i][uri].equalsIgnoreCase("easyDeployAppWeb.war+WEB-INF/web.xml")){
					data[i][server] = "WebSphere:cell=Avatar2Cell01,cluster=RemoteGPNWOMA-CL+WebSphere:cell=Avatar2Cell01,cluster=Test-CL";
				}
				//v.add(data[i][uri]);	
			}
		} 
		return v;
	} 

}
