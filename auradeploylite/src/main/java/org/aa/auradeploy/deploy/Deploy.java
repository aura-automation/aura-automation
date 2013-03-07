/**
 * 

 */

/** TODO: Code to generate the deployed code of the EJB modules tat do not have deployed code generated
 * TODO: Check if Virtual host exists before binding the web module
**/
/**
 * This is the class from which deploy is called. Command to call the deploy is as follows. 
 * "%JAVA_HOME%\bin\java" "-Dwas.install.root=%WAS_HOME%" -DApplicationName=JNDITest 
 * -DCellName=Avatar1Network -DCluster=Test-CL -DEAR=C:/Software/WebSpere5/ClassloaderTest/JNDITest.ear 
 * -Ddeploydata=C:/IBM/WSAD/Workspace/JMXAdminClient/deploydata/DeployData.xml 
 * -Duser=jatin -Dpassword=jatin -Dport=8889 -Dnode=Avatar1 -Dhost=Avatar1 
 * -Dopertation=all -classpath "%WAS_CLASSPATH%;c:\IBM\WebSphere\DeploymentManager\classes;
 * c:\IBM\WebSphere\DeploymentManager\lib\admin.jar;C:\IBM\WebSphere\DeploymentManager\lib\wasjmx.jar;
 * C:\IBM\WebSphere\DeploymentManager\lib\wjmxapp.jar;C:\IBM\WebSphere\DeploymentManager\lib\deploy\xercesImpl.jar;
 * C:\IBM\WebSphere\DeploymentManager\lib\deploy\xmlParserAPIs.jar;
 * C:\IBM\WebSphere\DeploymentManager\lib\deploy\resolver.jar;
 * C:\IBM\WebSphere\DeploymentManager\lib\deploy\xml-apis.jar" 
 * com.cmware.easydeploy.deploy.Deploy  %*
 * 
 * The above command is called from bin directory of the WebSphere Client i.e. copy of the WebSphere Deployment 
 * installation on the Build/Deploy Server.
 * 
 * The classes are deployed in the classes directory or jarred and deployed in lib directory. 
 * 
 * Start method
 * doWork() => 
 * 				removeApplication() | installApplication() 
 * 
 * 
 * 
 * TODO
 * 1: ClassLoader Policy and ClassLoading Mode. 
 * 2: 			<res-env-ref name="jms/AsyncSenderQueue" ext-jndi-name="jdbc/Test/Trade_DSEJB"/>
 * 
 * Todo Inportatnt
 * 			 server.ClusterOrServerExists(deploInfo, adminClient,configService,session);
			 LookUp lookup = new LookUp();
			 lookup.doLookup("ad") ;

 * **/


package org.aa.auradeploy.deploy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.Constants.JMXApplication;
import org.aa.auradeploy.application.Application;
import org.aa.auradeploy.cluster.Cluster;
import org.aa.auradeploy.helper.Helper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import org.aa.common.Constants.LicenseConstants;
import org.aa.common.deploy.Connection;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.wasproduct.WASProduct;
import org.aa.common.wasproduct.WASProductFinder;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementHelper;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.FileTransferConfig;
import com.ibm.websphere.management.filetransfer.client.FileTransferClient;
import com.ibm.websphere.management.filetransfer.client.FileTransferOptions;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;
import com.ibm.ws.management.fileservice.FileTransferFactory;
//import com.ibm.ws.management.fileservice.FileTransferFactory;

public class Deploy extends Connection{
	
	private static final Log logger  = LogFactory.getLog(Deploy.class);	
	public DeployNotificationListener deployNotificationListener = new DeployNotificationListener();
	ObjectName node1 ;	
	ObjectName server1;
	// required in the handle notification
	DeployInfo mDeployInfo;
	Vector stats = new Vector();
	long uninstallStartTimeInMillis = 0;
	long uninstallEndTimeInMillis = 0;
	long installStartTimeInMillis = 0;
	long installEndTimeInMillis = 0;

	
	/**
 * Main is the method that is called from the bat file to start the deploy. 
 * The parameters are passed in the class using java D variable or environment variable.
 * 
 * For testing these values can be set in the class as follows
 * 
 *  deployInfo.setApplicationName("JNDITest"); 
 * 	deployInfo.setCell("Avatar1Network"); 
 * 	deployInfo.setCluster("Test-CL"); 
 * 	deployInfo.setEARFileLocation("C:/Software/WebSpere5/ClassloaderTest/JNDITest.ear"); 
 * 	deployInfo.setDeployDataLocation("C:/IBM/WSAD/Workspace/JMXAdminClient/deploydata/DeployData.xml");
 * 	deployInfo.setUserName("jatin");
 * 	deployInfo.setPassword("jatin");
 * 	deployInfo.setPort("8889");
 * 	deployInfo.setNode("Avatar1");
 * 	deployInfo.setHost("Avatar1");
 * 	deployInfo.setOperation("all");
 * 
 * **/


    public static void main(String[] args)
    	throws DeployException{
    	try{
    		SDLog.log("    SlickDeploy Version " + DeployValues.VERSION );
    		SDLog.log("    AparTech." );

    		SDLog.log("Starting the deploy process");
    		logger.trace( "Entered in the main method of the Deploy.");

    		logger.trace("Creating the Deploy Instance.");
			Deploy deploy = new Deploy();
			logger.trace("Created the Deploy Instance sucessfully.");

			logger.trace("Create the DeployInfo.");
			DeployInfo deployInfo = new DeployInfo();
			logger.trace("Created the DeployInfo sucessfully.");
			DeployInfoReader deployInfoReader = new DeployInfoReader();
			deployInfo  = deployInfoReader.populateDeployInfo(); 
	
			/** Validate that all the values required for the deploy are specified.
			  * If values are missing or files do not exists then fail the deploy and 
			  * exit from this program.
			  * **/

			 
			deploy.startWork(deployInfo);
    	}catch(DeployException e){
    		e.printStackTrace();
    		System.out.println("Nested  exceptions " );
    		e.getCause().printStackTrace();
    		throw new DeployException (e);
    	}catch(ConnectorException e){
    		e.printStackTrace();
    		System.out.println("Nested  exceptions " );
    		e.getCause().printStackTrace();
    		throw new DeployException (e);
    	}catch(ConfigServiceException e){
    		e.printStackTrace();
    		System.out.println("Nested  exceptions " );
    		e.getCause().printStackTrace();
    		throw new DeployException (e);
    	}catch(AdminException e){
    		e.printStackTrace();
    		System.out.println("Nested  exceptions " );
    		e.getCause().printStackTrace();
    		throw new DeployException (e);
    	}catch(TransferFailedException e){
    		e.printStackTrace();
    		System.out.println("Nested  exceptions " );
    		e.getCause().printStackTrace();
    		throw new DeployException (e);
    	}
    }

    private void uploadApplication(DeployInfo deployInfo)
    	throws AdminException,TransferFailedException{
    	
    	System.out.println("Will Upload EAR");
    	String uploadedFile = "C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp.ear";
    	FileTransferClient fileTransferClient  = FileTransferFactory.getFileTransferClient(adminClient);
    	String staging = fileTransferClient.getServerStagingLocation();
    	
    	FileTransferConfig fileTransferConfig = fileTransferClient.getFileTransferConfig();
    	System.out.println("staging " + fileTransferConfig.getStagingLocation() );
    	System.out.println("TransferRequestTimeout " + fileTransferConfig.getTransferRequestTimeout() );
    	System.out.println("TransferRetryCount " + fileTransferConfig.getTransferRetryCount() );
    	
    	//fileTransferConfig.setStagingLocation( fileTransferConfig.getStagingLocation() + "\\upload");
    	//fileTransferClient.setFileTransferConfig(fileTransferConfig);

    	//fileTransferOptions.setOverwrite(true);
    	
    	File file = new File(uploadedFile);
    	String filename = file.getName();
    	fileTransferClient.uploadFile(file, filename + sessionID );
    	deployInfo.setRemoteEARDirectory(fileTransferConfig.getStagingLocation() );
    	System.out.println("EAR uploaded");
    	
    //	File earFile = new File("C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp.ear");
    //	fileTransferClient.uploadFile(earFile, "upload\\Temp") ;
    }

    private void registerNotificationListener () throws DeployException
    {
   		logger.trace("Entered the register notification method.");

        try
        {
			logger.trace("Getting reference to AppManagement MBean.");
	
            String query = "WebSphere:type=AppManagement,*";
   			ObjectName ApplicationManager = null;
            ObjectName queryName = new ObjectName (query);
            Set s = adminClient.queryNames(queryName, null);
			logger.trace("Got reference to set of AppManagement MBean.");
        
            if (!s.isEmpty()){
				ApplicationManager = (ObjectName)s.iterator().next();
				logger.trace("AppManagement MBean found.");

            }
//            ObjectName on = ApplicationManager ; //get MBean (type=AppManagement)
			logger.trace("Adding Notification to AppManagemeng MBean.");
			adminClient.addNotificationListener(ApplicationManager , deployNotificationListener, null, null);
			logger.trace("Added Notification to AppManagemeng MBean sucessfully.");

        }catch (InstanceNotFoundException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (MalformedObjectNameException e){
        	e.printStackTrace();
        	throw new DeployException(e);
        }
    }

    private void unRegisterNotificationListener () throws DeployException
    {
   		logger.trace("Entered the register notification method.");

        try
        {
			logger.trace("Getting reference to AppManagement MBean.");
	
            String query = "WebSphere:type=AppManagement,*";
   			ObjectName ApplicationManager = null;
            ObjectName queryName = new ObjectName (query);
            Set s = adminClient.queryNames(queryName, null);
			logger.trace("Got reference to set of AppManagement MBean.");
        
            if (!s.isEmpty()){
				ApplicationManager = (ObjectName)s.iterator().next();
				logger.trace("AppManagement MBean found.");

            }
			logger.trace("Adding Notification to AppManagemeng MBean.");
			adminClient.removeNotificationListener(ApplicationManager , deployNotificationListener);

			logger.trace("Added Notification to AppManagemeng MBean sucessfully.");

        }catch (InstanceNotFoundException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (MalformedObjectNameException e){
        	e.printStackTrace();
        	throw new DeployException(e);
        }catch (ListenerNotFoundException e){
        	e.printStackTrace();
        	throw new DeployException(e);
        }
    }

    
    public String startWork(DeployInfo deployInfo)
    	throws DeployException,ConfigServiceException,ConnectorException,AdminException,TransferFailedException{
    	String antNodesVariable = "";
		SDLog.log("******************************");

		SDLog.log("Aura Deploy Lite Version "+DeployValues.VERSION+", Centauros-Solutions Ltd, www.centauros-solutions.co.uk." );
    	SDLog.log("Copyright Apartech Ltd ");
		SDLog.log("" );

		SDLog.log( Calendar.getInstance().getTime().toGMTString());
		SDLog.log( "Connection Mode " + deployInfo.getConnectionMode());
		SDLog.log( "Operation Mode : "+  deployInfo.getOperationMode());

		int historyId = -1;

		
    	try{
	    	DeployState.uninstallStatus = JMXApplication.NOTIFICATION_NOT_STARTED;
	    	DeployState.installStatus = JMXApplication.NOTIFICATION_NOT_STARTED;
	    	DeployState.deployInfo = deployInfo;
	    	/** Setting empty to avoid null pointer **/
	    	DeployState.nodes = new Vector();
	    	DeployState.distStatus = false;
	    	DeployState.syncCount = 0;
	    	 
	    	
	    		
	    	mDeployInfo = deployInfo;
	    	logger.trace(" Deploy Operation is " + mDeployInfo.getOperation());
    	
			createAdminClient(deployInfo);

		//	WASProductFinder wasProductFinder = new WASProductFinder();
		//	WASProduct wasProduct = wasProductFinder.getProduct(adminClient, sessionID);


	    	if (deployInfo.isMultiEAR()){
	
	    		logger.warn("This is a MultiEAR deploy");
	    		File[] files =  Helper.scanDir(deployInfo.getMultiEARLocation(),true);
	    		ArrayList failedDeploy = new ArrayList();
	    		if (files.length == 0){
	    			SDLog.log("No files found");
	    		}
				logger.trace("Create the AdminClient.");
				logger.trace("Created the AdminClient sucessfully.");
				/**
				 * Register for the Notification.
				 * **/	
				int artefactHistoryId = -1;
	
	    		for (int i=0;i<	files.length;i++){
					if ((files[i].getName().endsWith(".ear")) || (files[i].getName().endsWith(".EAR")) 
							|| (files[i].getName().endsWith(".war")) || (files[i].getName().endsWith(".WAR"))){
						try{
							/**
							 * Create the admin client connection to the Host and Port and set the instance to the 
							 * Global Admin client Map.
							 * **/	
							logger.trace("Create the AdminClient.");
		
							createAdminClient(deployInfo);
							
				    		SDLog.log("Deploy type is MultiEAR"); 
				    		SDLog.log("Location of the EAR is " + deployInfo.getMultiEARLocation());
				    		SDLog.log("");

						//	uploadApplication(deployInfo);
							
							ObjectName[] configIDs = configService.resolve(session, "Cell=");
							if (configIDs.length > 0){
								String cellName = configService.getAttribute(session, (ObjectName)configIDs[0], "name").toString();
								deployInfo.setCell(cellName);
								System.out.println(" Cell name is " + cellName);
							}
							
							logger.trace("Created the AdminClient sucessfully.");
		
							/**
							 * Register for the Notification.
							 * **/	
							
							registerNotificationListener();
		
							
							String earFileName = files[i].getName();
							String applicationName = earFileName.substring(0,earFileName.indexOf(".ear"));
							deployInfo.setApplicationName(applicationName);
							
							
							if (deployInfo.getRemoteEARDirectory() !=null){
								if (deployInfo.getTargetOS()!=null){
									if (deployInfo.getTargetOS().equalsIgnoreCase("Windows")){
										deployInfo.setEARFileLocation(deployInfo.getRemoteEARDirectory() + "\\" + files[i].getName());
									}else{
										deployInfo.setEARFileLocation(deployInfo.getRemoteEARDirectory() + "//" + files[i].getName());
									}
								}else{
									deployInfo.setEARFileLocation(deployInfo.getRemoteEARDirectory() + File.separator + files[i].getName());
								}
								
								
							}else{
			/**					FileTransferClient ftClient =  FileTransferFactory.getFileTransferClient(adminClient);
								logger.trace( " Staging Location " + ftClient.getFileTransferConfig().getStagingLocation());
								logger.trace( " Staging Location " + ftClient.getFileTransferConfig().getStagingLocation());
								FileTransferConfig config = ftClient.getFileTransferConfig();
								logger.trace( " " + config.getStagingLocation()); 
								ftClient.uploadFile(new File(deployInfo.getMultiEARLocation() + File.separator + files[i].getName()), "upload" + File.separatorChar  + sessionID + File.separatorChar +  files[i].getName());
								deployInfo.setEARFileLocation(config.getStagingLocation() + File.separator  + "upload" + File.separatorChar + sessionID + File.separatorChar +  files[i].getName());
				**/			
							}
							
							deployInfo.setDeployDataLocation(deployInfo.getMultiEARDeployData()+ File.separator + applicationName + "-deploydata.xml" );
							deployInfo.setEnvironmentProperties(deployInfo.getMultiEARDeployData()+ File.separator + applicationName + ".properties" );
							SDLog.log( "******************************************************");
							SDLog.log( "*** Application Name " + applicationName + ".***");
							SDLog.log( "*** earFileName " + earFileName + ".***");
							SDLog.log( "*** Application number " + (i+1)  + " of " +  files.length + ".***");
							SDLog.log( "*** Operation " + deployInfo.getOperation());
							SDLog.log( "*** Number of Applications failed " + failedDeploy.size() + ".***");
							SDLog.log( "******************************************************");
							deployInfo.setApplicationNumber(i);
							logger.trace("Application Name " + applicationName);
				    		logger.trace("earFile " + earFileName);
		
				    		DeployState.distStatus = false;	
				    		DeployState.uninstallStatus = JMXApplication.NOTIFICATION_NOT_STARTED; 
							DeployState.installStatus = JMXApplication.NOTIFICATION_NOT_STARTED;
							antNodesVariable = doWork(deployInfo);
							SDLog.log("Save the session " );
							save(deployInfo);
							
	
						}catch(DeployException e){
							failedDeploy.add(deployInfo.getApplicationName()); 
							e.printStackTrace();	
						}					
					}
					if (i== files.length){
						
						SDLog.log("[SAVE]	MultiEAR deploy completed sucessfully  " + deployInfo.getApplicationName() + " using deploydata " );
					//	System.exit(0);
					} 
	
				} 
	
				if (failedDeploy.size() >0){
					SDLog.log("Following applications failed ");
					SDLog.log( "******************************************************");
	
				}
				for(int p=0 ; p<failedDeploy.size();p++){
					SDLog.log("**** " +  (String)failedDeploy.get(p) + " ****");
	
				}
	    		if (failedDeploy.size() >0){
					SDLog.log( "******************************************************");
					if (!deployInfo.isFailOnError() && deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UNINSTALL)){
						SDLog.log( "Will ignore the failure as FailOnError is false and opertion is uninstall");
						Helper.displayStats(stats);
					}else{
						Helper.displayStats(stats);
						throw new DeployException(new Exception(failedDeploy.size() + " applications failed to install."));
					}
				}else{
					if (deployInfo.isReStartClusterAfterDeploy()){
						Cluster cluster = new Cluster(deployInfo);
						cluster.restartCluster();
					}	
				}
	
				if (failedDeploy.size() >0){
					SDLog.log("Following applications failed ");
					SDLog.log( "******************************************************");
	
				}
				for(int p=0 ; p<failedDeploy.size();p++){
					SDLog.log("**** " +  (String)failedDeploy.get(p) + " ****");
	
				}
	
	    		
	    	}else{
	
	    		SDLog.log("Deploy Type is SingleEAR " );
	
				/**
				 * Create the admin client connection to the Host and Port and set the instance to the 
				 * Global Admin client Map.
				 * **/	
				logger.trace("Create the AdminClient.");
	
				createAdminClient(deployInfo);
				ObjectName[] configIDs = configService.resolve(session, "Cell=");
				if (configIDs.length > 0){
					String cellName = configService.getAttribute(session, (ObjectName)configIDs[0], "name").toString();
					deployInfo.setCell(cellName);
				}
	
				logger.trace("Created the AdminClient sucessfully.");
	
				/**
				 * Register for the Notification.
				 * **/	
				
				registerNotificationListener();    		
	    		logger.warn("This is a SingleEAR deploy");
	
	    		antNodesVariable  = doWork(deployInfo);
	    		
				if (deployInfo.isReStartClusterAfterDeploy()){
					Cluster cluster = new Cluster(deployInfo);
					cluster.restartCluster();
				}	
	    	}
	    	Helper.displayStats(stats);
	    	return antNodesVariable;
	    	
    	}catch (DeployException e){
    		throw e;
    	}catch (ConnectorException e){
    		throw e;
    	}catch (AdminException e){
    		throw e;
    	}catch (Exception e){
    		throw new DeployException(e);

    		/**	}catch (TransferFailedException e){
    		throw e; **/
    	}
    }
    
    private String doWork(DeployInfo deployInfo)
    	throws DeployException{
    	String antNodesVariable = "";;
    	
        // Run until killed
    	logger.debug("Start the thread.");

    	SDLog.log("Check if uninstall is required for the application " + deployInfo.getApplicationName());

    	logger.warn("Check if uninstall is required and if uninstalled was completed sucessfully in this thread before");
        logger.warn("Checking if uninstall was completed sucessfully so that Install can be started.");
        
//        regenPlugin(deployInfo);
        boolean noExit = true;
        boolean distNotComplete = true;
        DeployState.uninstallStatus = JMXApplication.NOTIFICATION_NOT_STARTED;
        DeployState.installStatus  = JMXApplication.NOTIFICATION_NOT_STARTED;
        
    	try
        {
	        boolean isUnistallRequired = isUnistallRequired(deployInfo);
//	        mapWebservers(deployInfo);
//	        System.exit(-1);
            while (noExit )
            {
            	logger.trace("Operation is " +  deployInfo.getOperation());
		        Thread.sleep(9000);

				if (DeployState.uninstallStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_NOT_STARTED) && deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UNINSTALL) && (!isUnistallRequired(deployInfo)) ){
	  				SDLog.log(" Application " + deployInfo.getApplicationName() + " does not exists, hence uninstall not required");
	  				noExit = false;
	  			}
		        if (DeployState.installStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_NOT_STARTED) && deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_INSTALL) && (isUnistallRequired(deployInfo)) ){
	  				SDLog.log(" Application " + deployInfo.getApplicationName() + " exists, hence install not required");
	  				noExit = false;
	  			}
		        
		        if (deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UPDATE)){
      				SDLog.log("In the loop update for the application " + deployInfo.getOperation());

	      			if (DeployState.uninstallStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_NOT_STARTED) ){
	      				SDLog.log("Start update for the application " + deployInfo.getApplicationName());

	      				updateApplication(deployInfo);
	      			}else if ((DeployState.installStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED)) ){
	      				noExit = false;
	      			}
		        
		        // Uninstall only if operation is reinstall or uninstall, else skip this step
            	}else if (deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_REINSTALL) || deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UNINSTALL)){
		        	logger.trace("After check if operation is uninstall or reinstall");
	      			if (DeployState.uninstallStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_NOT_STARTED) && isUnistallRequired){
			        	logger.trace("After check if uninstall is not started and uninstall is required ");

	      				SDLog.log("Start uninstall for the application " + deployInfo.getApplicationName());
	      				
	      				logger.warn("Starting uninstall.");
	      				DeployState.uninstallStatus = JMXApplication.NOTIFICATION_INPROGRESS;
	      				uninstallStartTimeInMillis = Calendar.getInstance().getTimeInMillis();
		      			removeApplication(deployInfo);
	      			}else if (DeployState.uninstallStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_FAILED)){
	      				SDLog.log("Uninstall failed for the application " + deployInfo.getApplicationName());
	      				SDLog.log("");
	      				logger.error("UnInstall of the Application failed hence stopping the thread.");
			        	noExit = false;
		        		uninstallEndTimeInMillis = Calendar.getInstance().getTimeInMillis();
		        		System.out.println("Checking if Uninstall is required " + isUnistallRequired);
		        		if (isUnistallRequired){
		        			stats.add( Helper.recordTime(deployInfo.getApplicationName(), deployInfo.getApplicationNumber(),
		        				uninstallStartTimeInMillis , uninstallEndTimeInMillis, "uninstall", deployInfo.getOperation()));
		        		}
			        	throw new DeployException(new Exception("UnInstall of the Application failed hence stopping the thread."));
	
		  			}else if (((DeployState.uninstallStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED)) && deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UNINSTALL))){
				        	save(deployInfo);
				        	for (int x=0;x<DeployState.nodes.size();x++){
				        		
				        		SDLog.log("Syncronising node :" + DeployState.nodes.get(x));
				        		syncNode(DeployState.nodes.get(x).toString());
				        		antNodesVariable= antNodesVariable + DeployState.nodes.get(x).toString() +  ",";
				        		noExit = false;
				        		uninstallEndTimeInMillis = Calendar.getInstance().getTimeInMillis();
				        		System.out.println("Checking if Uninstall is required " + isUnistallRequired);
				        		if (isUnistallRequired){
				        			stats.add( Helper.recordTime(deployInfo.getApplicationName(), deployInfo.getApplicationNumber(),
				        				uninstallStartTimeInMillis , uninstallEndTimeInMillis, "uninstall", deployInfo.getOperation()));
				        		}
				        	}
				        	

		  			}
		        }
		        
		        // install only if operation is reinstall or install, else skip this step
		        if (deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_REINSTALL) || deployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_INSTALL)){
		        	
					if ((DeployState.uninstallStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED)) || (!isUnistallRequired(deployInfo))){

						if (DeployState.installStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_NOT_STARTED)){
			        		uninstallEndTimeInMillis = Calendar.getInstance().getTimeInMillis();
			        		System.out.println("Checking if Uninstall is required " + isUnistallRequired);
			        		if (isUnistallRequired){
			        			stats.add( Helper.recordTime(deployInfo.getApplicationName(), 
			        				deployInfo.getApplicationNumber(), uninstallStartTimeInMillis , 
			        				uninstallEndTimeInMillis, "uninstall", deployInfo.getOperation()));
			        		}

							
							installStartTimeInMillis = Calendar.getInstance().getTimeInMillis();
			        	//	stats.add( Helper.recordTime(deployInfo.getApplicationName(),deployInfo.getApplicationNumber(), installStartTimeInMillis , installEndTimeInMillis, "install", deployInfo.getOperation()));
			        		
							SDLog.log("Application Name to install: " + deployInfo.getApplicationName());
							SDLog.log("Deploydata location :" + deployInfo.getDeployDataLocation());
							SDLog.log("EAR Location :" + deployInfo.getEARFileLocation());
	
							logger.warn("Starting install.");
							DeployState.installStatus= JMXApplication.NOTIFICATION_INPROGRESS;
							
	//			   			doResources(deployInfo);	
					        installApplication(deployInfo);
	//						regenPlugin(deployInfo);				
							
						}else  if ((DeployState.installStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED)) ){
							editApplication(deployInfo);
				        	logger.warn("Save the session.");
				        	save(deployInfo);
				        	for (int x=0;x<DeployState.nodes.size();x++){
	
				        		SDLog.log("Syncronising node :" + DeployState.nodes.get(x));
				        		syncNode(DeployState.nodes.get(x).toString());
				        		antNodesVariable= antNodesVariable + DeployState.nodes.get(x).toString() +  ",";
				        		noExit = false;
				        		
				        	}
				        	
				        	if(!deployInfo.isMultiEAR()) 	
				        	{		
				        		SDLog.log("Save the session" );
					        	save(deployInfo);
				        		logger.warn("Stop the thread.");
				        		noExit = false;
				        	}

						}else if ((DeployState.installStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_FAILED))){
							installEndTimeInMillis = Calendar.getInstance().getTimeInMillis();
			 //       		stats.add( Helper.recordTime(deployInfo.getApplicationName(), deployInfo.getApplicationNumber(),installStartTimeInMillis, installEndTimeInMillis, "install", deployInfo.getOperation()));

							SDLog.log(" ****************************************** ");
							throw new DeployException( new Exception( "Install failed")); 
						}
					}
				}
				if ((DeployState.installStatus.equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED))	){
	
		            Hashtable props = new Hashtable();
					props.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
					AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);
					int waitcount = 0; 		
		            while (distNotComplete)
		            {
		            	Thread.sleep(deployInfo.getSleepTimeForSyncRequest());
		            	if (DeployState.syncCount <=0)
		            		DeployState.syncCount = 0;
		            	
		            	if ((DeployState.syncCount <=0) || (waitcount >= 2)){
		            		waitcount = 0;
		            		DeployState.syncCount ++;
		            		SDLog.log("Requesting the status of the syncronisation." );
		            		appManagement.getDistributionStatus(deployInfo.getApplicationName(),props,session.toString());
		            	}else{
		            		waitcount ++;	
		            		SDLog.log("Waiting for the response of the syncronisation status" + DeployState.syncCount);
	
		            	}
//		            	 Condition to come out of distribution loop if distribution is completed.
//		            	 To call only when operation is reinstall

		            	if (DeployState.distStatus){
		            		distNotComplete = false;
		            		unRegisterNotificationListener();
		            		installEndTimeInMillis = Calendar.getInstance().getTimeInMillis();
			        		stats.add( Helper.recordTime(deployInfo.getApplicationName(), deployInfo.getApplicationNumber(), installStartTimeInMillis , installEndTimeInMillis, "install", deployInfo.getOperation()));

		            	}
		            	
//		            	 Condition to come out of distribution loop if uninstall only distribution is completed.
//		            	 To call only when operation is uninstall
		            	
		            	if (mDeployInfo.getOperation().equalsIgnoreCase(DeployValues.APPLICATION_OPERATION_UNINSTALL) && DeployState.distStatusForUninstall){
		            		
		            		distNotComplete = false;
		            		unRegisterNotificationListener();
		            		
		            	}
		            }
				}
            }
            
            // start the application is should start is true from the ANT Task
			if (deployInfo.isShouldStart())
			{
				SDLog.log("Attempting to start the application");
				Application application = new Application(deployInfo);
			    application.startApplication();
			}
        }
        catch (InterruptedException e)
        {
        	logger.error(e.getMessage() );
        	e.printStackTrace();
        	throw new DeployException(e);

        }catch (ConnectorException e){
        	logger.error(e.getMessage(),e );
        	e.printStackTrace();
        	throw new DeployException(e);
        	
        }catch(ConfigServiceException e){
	    	logger.error(e.getMessage(),e );
	    	e.printStackTrace();
        	throw new DeployException(e);
        
        
    	}catch(Exception e){
        	logger.error(e.getMessage(),e );
        	e.printStackTrace();
        	throw new DeployException(e);
        	
        }
    	
        return antNodesVariable;
    }
 
 	private void save(DeployInfo deploInfo){
 		
			logger.trace("Enter the save method.");
			try{
				logger.trace("Saving Config Service.");
				configService.save(session,false); 
				logger.trace("Saved the session.");

			}catch(ConnectorException e){
				e.printStackTrace();
	        	throw new RuntimeException(e);

			}catch(ConfigServiceException e){
				e.printStackTrace();
	        	throw new RuntimeException(e);
			}				
 	
 	}


 	private void removeApplication(DeployInfo deployInfo)
 		throws DeployException{

		try {
			logger.trace("Get the AppManagement from the adminClient.");
			AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);

			logger.trace("Create the Application Manager.");
			ObjectName ApplicationManager = null;
			
			logger.trace("Create the Application Manager.");
            String query = "WebSphere:type=AppManagement,*";
			logger.trace("Created a Application Manager.");
            
			logger.trace("Creating a query String.");
            ObjectName queryName = new ObjectName (query);
			logger.trace("Created a query String.");
            
			logger.trace("Fire the query to adminclient.");
            Set s = adminClient.queryNames(queryName, null);
			logger.trace("Fired the query to adminclient and got the results.");
   			logger.trace("Check if the results are not empty.");

            if (!s.isEmpty()){
	   			logger.trace("Get the results from the application manager.");
                ApplicationManager = (ObjectName)s.iterator().next();
            }

//			ObjectName on = ApplicationManager ; //get MBean (type=AppManagement)

			Hashtable props = new Hashtable();
			props.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
   			logger.trace("Stop the application.");
			appManagement.stopApplication(deployInfo.getApplicationName(), props, null);


  			appManagement.uninstallApplication (deployInfo.getApplicationName(), props, null);
   			logger.trace("Application uninstalled sucessfully.");

		}catch (Exception e) {
			
			e.printStackTrace();
			if (deployInfo.isFailOnError()){
				throw new DeployException(e);
			}else{
				SDLog.log("Ignoring above error as failOnError is " + deployInfo.isFailOnError());
			}

		}

 	}

	/**
	 * 1) AppDeplController prepares the EAR file for deployment by reading the DeployData and changing the 
	 * DeployDescriptors in the EAR file to the values of the DeployData.
	 * 2) Gets the AppManagement JMX and call the installApplication method on the MBean. 
	 * 
	 * **/
 	private void installApplication(DeployInfo deployInfo)
 		throws DeployException{

 		try {
			if (deployInfo.getEARFileLocation().endsWith(".war")){
				System.out.println("Deploy war files");
				wrapWarFile(deployInfo);
			}
			
			logger.trace("Enter the install application method.");
   			logger.trace("Create the instance of EARProcessor.");
   			SDLog.log("Process the EAR with the deploy data info for application " + deployInfo.getApplicationName() );
			EARProcessor earProcessor = new EARProcessor() ;
   			logger.trace("Created the instance of EARProcessor.");

   			SDLog.log("Check for missing resources ");
  			logger.trace("Check if the resource mentioned in the deploy data exists.");
   			Vector missingResources = earProcessor.checkIfResourcesExists (deployInfo,configService,session);
   			if (missingResources.size() > 0){
   				for(int i=0; i<missingResources.size();i++){
   					SDLog.log("Missing resource is " + missingResources.get(i));
   					logger.error("Missing resource is " + missingResources.get(i));
   				}
   				if (deployInfo.getFailIfResourceAbsent()){
   					SDLog.log("Stop the deploy as above resources are missing" );
	   				logger.error("Stop the deploy as above resources are missing");
//	   				Thread.currentThread().stop();
   				}
   			}else{  
   				SDLog.log("All resources are configured" );
   			}
   			logger.trace("Checking of missing resource complete."); 

   			logger.trace("Enter the EAR processer to process the EAR with the deploy data.");
//			earProcessor.doWork(deployInfo.getEARFileLocation(),deployInfo.getDeployDataLocation()) ;
//			Log.log("EAR processing complete." );
   			logger.trace("EAR processer complete.");

   			logger.trace("Get the AppManagement Instance.");
			AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);

   			logger.trace("Create the props hastable.");
			Hashtable props = new Hashtable();

			logger.trace("Add AppConstants.APPDEPL_LOCALE in the props.");
			props.put (AppConstants.APPDEPL_LOCALE, Locale.getDefault());
			
   			
			logger.trace("Add APPDEPL_USE_BINARY_CONFIG in the props.");
			props.put (AppConstants.APPDEPL_USE_BINARY_CONFIG, new Boolean(false) );

			/**
			 * 
			 * Commented out as the requirment is not to pre-compile the JSPs
			 */
			logger.trace("Add APPDEPL_PRECOMPILE_JSP in the props.");
			props.put (AppConstants.APPDEPL_PRECOMPILE_JSP ,new Boolean(false));

			boolean shouldGenerateEJBDeployCode = earProcessor.shouldGenerateDeployCode(deployInfo);
			
			
			if (deployInfo.isGenerateEJBDeployCode()){
				logger.trace("Add APPDEPL_PROCESS_EMBEDDEDCFG_INSTALL in the props.");
				props.put (AppConstants.APPDEPL_PROCESS_EMBEDDEDCFG_INSTALL,new Boolean(false));
					
				logger.trace("Add AppConstants.APPDEPL_DEPLOYEJB_OPTIONS in the props.");
	
				Hashtable ejbdeplopt = new Hashtable();
				ejbdeplopt.put(AppConstants.APPDEPL_DEPLOYEJB_CLASSPATH_OPTION,"c:/ffff");
				ejbdeplopt.put(AppConstants.APPDEPL_DEPLOYEJB_RMIC_OPTION,"");
				ejbdeplopt.put(AppConstants.APPDEPL_DEPLOYEJB_VALIDATE_OPTION_DEFAULT,Boolean.TRUE);
	
				props.put (AppConstants.APPDEPL_DEPLOYEJB_CMDARG_DEFAULT, Boolean.FALSE );
				props.put (AppConstants.APPDEPL_DEPLOYEJB_CMDARG, Boolean.TRUE );
	
				props.put (AppConstants.APPDEPL_DEPLOYEJB_OPTIONS, ejbdeplopt);
	//			props.put (AppConstants.EJBDeployOptionsTask, ejbdeplopt);
				
			}else if (shouldGenerateEJBDeployCode){
				logger.trace("Add APPDEPL_PROCESS_EMBEDDEDCFG_INSTALL in the props.");
				props.put (AppConstants.APPDEPL_PROCESS_EMBEDDEDCFG_INSTALL,new Boolean(false));
					
				logger.trace("Add AppConstants.APPDEPL_DEPLOYEJB_OPTIONS in the props.");
	
				Hashtable ejbdeplopt = new Hashtable();
				ejbdeplopt.put(AppConstants.APPDEPL_DEPLOYEJB_CLASSPATH_OPTION,"c:/ffff");
				ejbdeplopt.put(AppConstants.APPDEPL_DEPLOYEJB_RMIC_OPTION,"");
				ejbdeplopt.put(AppConstants.APPDEPL_DEPLOYEJB_VALIDATE_OPTION_DEFAULT,Boolean.TRUE);
	
				props.put (AppConstants.APPDEPL_DEPLOYEJB_CMDARG_DEFAULT, Boolean.FALSE );
				props.put (AppConstants.APPDEPL_DEPLOYEJB_CMDARG, Boolean.TRUE );
	
				props.put (AppConstants.APPDEPL_DEPLOYEJB_OPTIONS, ejbdeplopt);
	//			props.put (AppConstants.EJBDeployOptionsTask, ejbdeplopt);
			
			}	
			 
			//AppConstants.APPDEPL_DISTRIBUTE_APP
			
			if ( deployInfo.isZeroBinaryCopy ()){
				props.put (AppConstants.APPDEPL_ZERO_BINARY_COPY,new Boolean(true));
				props.put (AppConstants.APPDEPL_DISTRIBUTE_APP ,new Boolean(false));
				props.put (AppConstants.APPDEPL_INSTALL_DIR,deployInfo.getRemoteEARDirectory());

				
			}	
			if (deployInfo.isParentLastClassLoaderMode()){	
				logger.trace("Add APPDEPL_CLASSLOADINGMODE_PARENTLAST in the props.");
				props.put (AppConstants.APPDEPL_CLASSLOADINGMODE_PARENTLAST,new Boolean(true));
//				props.put (AppConstants.APPDEPL_CLASSLOADINGMODE_PARENTFIRST,new Boolean(true));
			}else{
				logger.trace("Add APPDEPL_CLASSLOADINGMODE_PARENTFIRST in the props.");
//				props.put (AppConstants.APPDEPL_CLASSLOADINGMODE_PARENTLAST,new Boolean(deployInfo.isParentLastClassLoaderMode()));
				props.put (AppConstants.APPDEPL_CLASSLOADINGMODE_PARENTFIRST,new Boolean(true));
				
			}
//			props.put (AppConstants.APPDEPL_ZERO_BINARY_COPY,new Boolean(true));
			
			
			
			logger.trace("Add APPDEPL_CLASSLOADERPOLICY_SINGLE in the props.");
			props.put (AppConstants.APPDEPL_CLASSLOADERPOLICY_SINGLE ,"Application");

			logger.trace("Add in the props.");
			
   			SDLog.log("Install the application.");

   			Hashtable moduleToServer = null;
   			String mapServerCluster = Helper.getServerOrClusterFullString(deployInfo);	
   			configService.resolve(session, "Server");
   			String webServerString = getWebServerFullStrings(deployInfo);
   			logger.trace(webServerString);
   			moduleToServer =  earProcessor.mapModulesToServer(mapServerCluster,deployInfo.getWebserver(),deployInfo,webServerString);
   			logger.trace("Got the list of the modules.");
			SDLog.log("Mapping modules to " + mapServerCluster );
			logger.trace("Add APPDEPL_MODULE_TO_SERVER in the props.");
			props.put(AppConstants.APPDEPL_MODULE_TO_SERVER, moduleToServer );
   			
   			
			appManagement.installApplication (deployInfo.getEARFileLocation(),
					deployInfo.getApplicationName() , 
					props, 
					session.toString());
			

        }catch (MalformedObjectNameException e) {
        	logger.error(e.getMessage(),e);
			e.printStackTrace();
        	throw new DeployException(e);
        }catch (InstanceNotFoundException e) {
        	logger.error(e.getMessage(),e);
			e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e) {
        	logger.error(e.getMessage(),e);
			e.printStackTrace();
        	throw new DeployException(e);
		}catch(AdminException e){
        	logger.error(e.getMessage(),e);
        	e.printStackTrace();
        	throw new DeployException(e);
        }catch(AppDeploymentException e){
        	logger.error(e.getMessage(),e);
        	e.printStackTrace(); 
        	throw new DeployException(e);
		}catch (IOException e) {
        	logger.error(e.getMessage(),e);
			e.printStackTrace();
        	throw new DeployException(e);
		}catch (SAXException e){
        	logger.error(e.getMessage(),e);
			e.printStackTrace();
        	throw new DeployException(e);
		}catch(Exception e){
        	logger.error(e.getMessage(),e);
			e.printStackTrace();
        	throw new DeployException(e);
		}
	
 	}    


	private boolean isUnistallRequired(DeployInfo deployInfo)
		throws DeployException{
		
		boolean isRequired = false;
		try{	
			Hashtable prefs = new Hashtable();
		    logger.trace(" +++++++++++ Checking if application exists " );
			AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);

			isRequired = appManagement.checkIfAppExists(deployInfo.getApplicationName(),prefs,null) ;
		    logger.trace(" +++++++++++ AppExists " + isRequired );
		}catch(AdminException e){
			e.printStackTrace();
	    	throw new DeployException(e);
		}catch(Exception e){
			e.printStackTrace();
	    	throw new DeployException(e);
		}

		return isRequired;
	
	}



 	private void startServer(DeployInfo deployInfo)
		throws DeployException{
 		

		try {
   			logger.trace("Get the Server Instance.");
			ObjectName Server = null;
            ObjectName server = null;
            String query = "WebSphere:type=Server,name="+deployInfo.getServer() +",*";
            ObjectName queryName = new ObjectName (query);
            Set s = adminClient.queryNames(queryName, null);
            
            if (!s.isEmpty()){
                server  = (ObjectName)s.iterator().next();
            }

			String opName ="start";
	        String signature[] = {};
			String params[] = { };
	        adminClient.invoke(server  , opName, params, signature);
			SDLog.log(" Server starting= ");
            


        }catch (MalformedObjectNameException e) {
			e.printStackTrace();
        	throw new DeployException(e);
        }catch (InstanceNotFoundException e) {
			e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e) {
			e.printStackTrace();
        	throw new DeployException(e);
		}catch(Exception e){
			e.printStackTrace();
        	throw new DeployException(e);
		} 
	
 	}    


 	private void stopServer(DeployInfo deployInfo)
		throws DeployException{
 		

		try {
   			logger.trace("Get the Server Instance.");
			ObjectName server = null;
            String query = "WebSphere:type=Server,name="+deployInfo.getServer() +",*";
            ObjectName queryName = new ObjectName (query);
            Set s = adminClient.queryNames(queryName, null);
            
            if (!s.isEmpty()){
                server   = (ObjectName)s.iterator().next();
            }

			String opName ="stop";
	        String signature[] = {};
			String params[] = { };
	        adminClient.invoke(server, opName, params, signature);
			SDLog.log(" Server starting= ");
            


        }catch (MalformedObjectNameException e) {
			e.printStackTrace();
        	throw new DeployException(e);
        }catch (InstanceNotFoundException e) {
			e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e) {
			e.printStackTrace();
        	throw new DeployException(e);
		}catch(Exception e){
			e.printStackTrace();
        	throw new DeployException(e);
		}
	
 	}    


	private void regenPlugin(DeployInfo deploInfo){
		DeployHelper deployHelper = new DeployHelper();
		deployHelper.regenPlugin(deploInfo,adminClient); 
	}

	private void unmapWebMebservers(DeployInfo deployInfo)
		throws DeployException{
		
		try{
			ObjectName[] targets = configService.resolve(session, "Deployment="+deployInfo.getApplicationName()+":ApplicationDeployment:WebModuleDeployment:DeploymentTargetMapping:");
			for(int i = 0 ; i < targets.length ; i++){
				ObjectName target = (ObjectName)configService.getAttribute(session, (ObjectName)targets[i], "target");
				String serverName = configService.getAttribute(session, target, "name").toString();
				ObjectName[] targetServer = configService.resolve(session, "Server="+serverName);
				if ((target!=null) && (targetServer.length >0)) {
					String serverType = configService.getAttribute(session, (ObjectName)targetServer[0], "serverType").toString();
					if (serverType.equalsIgnoreCase("WEB_SERVER")){
						configService.deleteConfigData(session, target);
					} 
				} 
			}
			configService.save(session, true);
			
		}catch(ConfigServiceException e ){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(ConnectorException e){
			e.printStackTrace();
			throw new DeployException(e);
		}
	}

	private void mapWebservers(DeployInfo deployInfo)
		throws DeployException{
	
/**		try{
			ObjectName[] targets = configService.resolve(session, "Deployment="+deployInfo.getApplicationName()+":ApplicationDeployment:WebModuleDeployment:");
			for(int i = 0 ; i < 1 ; i++){
				ObjectName[] targetServer = configService.resolve(session, "ServerTarget=IHS");
				System.out.println( targetServer.length); 
				System.out.println( targetServer[0].getCanonicalName());
				AttributeList webserverAttrList = new AttributeList();
				webserverAttrList.add(new Attribute("DeployedObject", targets[0]));
				webserverAttrList.add(new Attribute("enable", new Boolean(true)));
				webserverAttrList.add(new Attribute("target", targetServer[0]));
				configService.createConfigData(session, targets[0], "targetMappings", "DeploymentTargetMapping", webserverAttrList);
			}
			configService.save(session, true);
			
		}catch(ConfigServiceException e ){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(ConnectorException e){
			e.printStackTrace();
			throw new DeployException(e);
		} **/
		try{
			/**
//			Hashtable moduleToServer = null;
			String mapServerCluster = Helper.getServerOrClusterFullString(deployInfo);	
//			EARProcessor earProcessor = new EARProcessor() ;
//			moduleToServer =  earProcessor.mapModulesToServer(mapServerCluster,deployInfo.getWebserver(),deployInfo);
			Hashtable moduleToServer = new Hashtable();
			moduleToServer.put( "easyDeployAppWeb.war+WEB-INF/web.xml" , "WebSphere:cell=Avatar2Cell01,cluster=RemoteGPNWOMA-CL+WebSphere:cell=Avatar2Cell01,cluster=Test-CL");
			logger.trace("Got the list of the modules.");
			SDLog.log("Mapping modules to " + mapServerCluster );
			logger.trace("Add APPDEPL_MODULE_TO_SERVER in the props.");
			props.put(AppConstants.APPDEPL_MODULE_TO_SERVER,moduleToServer);
	**/
			AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);

			logger.trace("Create the props hastable.");
			Hashtable props = new Hashtable();

			Vector currectDataVector = appManagement.getModuleInfo(deployInfo.getApplicationName(), props,"easyDeployAppWeb.war+WEB-INF/web.xml" , "null") ;
			for (int i = 0; i < currectDataVector.size();i++){
				AppDeploymentTask task = (AppDeploymentTask)currectDataVector.get(i);
				if (task.getName().equalsIgnoreCase(AppConstants.MapModulesToServersTask)) {
					EARProcessor earProcessor = new EARProcessor(); 
					earProcessor.doMapModulesToServers(task,deployInfo);
				}
			} 
			appManagement.setModuleInfo(deployInfo.getApplicationName(),props,"easyDeployAppWeb.war+WEB-INF/web.xml" , null, currectDataVector)	;
			save(deployInfo);
		
		}catch(AdminException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(IOException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(SAXException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(AppDeploymentException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DeployException(e);
		}	
	}
	
	private String getWebServerFullStrings(DeployInfo deployInfo)
		throws ConfigServiceException,ConnectorException{
		String webserverString = "";
		ArrayList array =  getArrayFromCommaSeperated(deployInfo.getWebserver());
		logger.trace("Looking up Nodes for webserver mapping");
		ObjectName[] nodeNames =  configService.resolve(session, "Node");
		for (int i = 0,  n = nodeNames.length; i < n ; i++){
			String nodeName = configService.getAttribute(session, nodeNames[i] , "name").toString();
			logger.trace("Found Node for webserver mapping " + nodeName);
			logger.trace(" array.size() " + array.size());
			for (int j=0 , k = array.size() ; j < k; j++){ 
				String webServername= array.get(j).toString();
				ObjectName[] webServerNames =  configService.resolve(session, "Cell=" +deployInfo.getCell() +":Node=" + nodeName + ":Server=" + webServername);
				if ((webServerNames != null) && (webServerNames.length > 0)){
					System.out.println("Found webserver on " + webServername  + "  " + nodeName);
					if (webserverString.length()==0){
						webserverString = "WebSphere:cell="+deployInfo.getCell()+",node=" + nodeName + ",server=" + webServername;
					}else{
						webserverString = webserverString + "+WebSphere:cell="+deployInfo.getCell()+",node=" + nodeName + ",server=" + webServername;
					}
				}
			}
		}
		return webserverString;
	}
	
	private void editApplication(DeployInfo deployInfo)
		throws DeployException{

		try {
	
			ObjectName rootID = configService.resolve(session, "Deployment="+deployInfo.getApplicationName())[0];
			
			
			
			ObjectName appDeplPattern = ConfigServiceHelper.createObjectName(null, "ApplicationDeployment");
			logger.trace( "Created the object name for ApplicationDeployment.");

			ObjectName appDeplID = configService.queryConfigObjects(session, rootID, appDeplPattern, null)[0];
			logger.trace( "Got the the Application Deployment object from the object name.");

			AttributeList attrList = new AttributeList();
			
			for(int i = 0 ; i < attrList.size(); attrList.size() ){
				
				System.out.println( attrList.get(i));
			}
			
			attrList.add(new Attribute("warClassLoaderPolicy", "SINGLE"));
			logger.trace( "Set the warClassLoaderPolicy to Single.");
			SDLog.log("Starting Weight from command line " + deployInfo.getStartingWeight());
			attrList.add(new Attribute("startingWeight",deployInfo.getStartingWeight()));

   			logger.trace("Get list of the modules.");
   			
			/**EARProcessor earProcessor = new EARProcessor() ;
			moduleToServer =  earProcessor.mapModulesToServer(Helper.getServerOrClusterFullString(deployInfo),Helper.getWebServerFullString(deployInfo),deployInfo);
   			logger.trace("Got the list of the modules.");
			Log.log("Mapping modules to " +Helper.getServerOrClusterFullString(deployInfo) );
			logger.trace("Add APPDEPL_MODULE_TO_SERVER in the props.");
			attrList.add(new Attribute(AppConstants.APPDEPL_MODULE_TO_SERVER, moduleToServer ));
			**/
			
//			Hashtable moduleToServer = new Hashtable();
//			moduleToServer.put("ServiceDeskWeb.war,WEB-INF/web.xml", "WebSphere:cell=Avatar2Cell01,cluster=ServiceDesk-CL+WebSphere:cell=Avatar2Cell01,node=Avatar2Node02,server=IHS"); 
//			attrList.add(new Attribute(AppConstants.APPDEPL_MODULE_TO_SERVER, moduleToServer ));
			
			Vector libs = new Vector();
			boolean isEnable = true;
			if (Helper.isDeployDataPresent(deployInfo)){
				DeployDataReader deployDataReaderforLibs = new DeployDataReader(deployInfo.getDeployDataLocation(),deployInfo);
				isEnable = deployDataReaderforLibs.getEnable();
				libs =  deployDataReaderforLibs.getLibraries();
				
				if (deployDataReaderforLibs.getStartingWeight().intValue() > 0){
					SDLog.log("Starting Weight from deploydata " + deployDataReaderforLibs.getStartingWeight() );
					attrList.add(new Attribute("startingWeight",deployDataReaderforLibs.getStartingWeight()));
				}
	
			}

			// Disable application after installation
			
			
			if (!isEnable){
				System.out.println("Disable application " );
				AttributeList deployedObjects = configService.getAttributes(session, rootID, new String[] {"deployedObject"}, false);
				ObjectName appDeplAttrs =  (ObjectName)ConfigServiceHelper.getAttributeValue(deployedObjects, "deployedObject");
				ArrayList childAttrs = (ArrayList)configService.getAttribute(session, appDeplAttrs, "targetMappings");
				AttributeList targetMappingsAttrs = null;
				targetMappingsAttrs = (AttributeList)childAttrs.get(0);
				AttributeList newAttrList = new AttributeList();
				newAttrList.add(new Attribute("enable", new Boolean(false)));
				configService.setAttributes(session, ConfigServiceHelper.createObjectName(targetMappingsAttrs), newAttrList);
				targetMappingsAttrs = (AttributeList)childAttrs.get(0);
			}
			
			

			AttributeList clList = (AttributeList) configService.getAttribute (session, appDeplID, "classloader");
			
			ArrayList attributes = new ArrayList(); 
			if (libs.size()>0){
				
				for (int i = 0; i<libs.size();i++){
					AttributeList myAttrList = new AttributeList();	
					myAttrList.add(new Attribute ("libraryName",libs.get(i)));
					myAttrList.add(new Attribute ("sharedClassloader",new Boolean( true )));
					attributes.add(myAttrList);
				}
				
			} 
			ConfigServiceHelper.setAttributeValue(clList,"libraries",attributes);
			// set the new values
			if (deployInfo.isParentLastClassLoaderMode()){
				ConfigServiceHelper.setAttributeValue (clList, "mode", "PARENT_LAST");
				logger.trace( "Set the ClassLoaderPolicyMode to PARENT_LAST.");
			}else{
				ConfigServiceHelper.setAttributeValue(clList, "mode", "PARENT_FIRST");
				logger.trace( "Set the ClassLoaderPolicyMode to PARENT_FIRST.");
			}
			
			attrList.add (new Attribute ("classloader", clList));

			
			configService.setAttributes(session,  appDeplID, attrList);

			// save your changes
			configService.save(session, false);

			
		}catch(AttributeNotFoundException e){
			e.printStackTrace();	
			throw new DeployException(e);
		}catch(ConfigServiceException e ){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(ConnectorException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(IOException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(SAXException e){
			e.printStackTrace();
			throw new DeployException(e);
		}

	}

 	private void syncNode (String nodeName) throws DeployException
    {
   		logger.trace("Entered Node Sync.");

        try
        {
			logger.trace("Getting reference to CellSync MBean.");
	
            String query = "WebSphere:type=CellSync,*";
   			ObjectName CellSync = null;
            ObjectName queryName = new ObjectName (query);
            Set s = adminClient.queryNames(queryName, null);
			logger.trace("Got reference to set of CellSync MBean.");
        
            if (!s.isEmpty()){
				CellSync = (ObjectName)s.iterator().next();
				logger.trace("CellSync MBean found.");

				ObjectName on = CellSync  ; //get MBean (type=AppManagement)
				logger.trace("Run CellSYnc .");
				logger.trace( " Status of Sync is "+ (adminClient.invoke(CellSync, "syncNode", new String[]{nodeName}, new String[]{String.class.getName()})).toString());
				logger.trace("Ran CellSync successfully.");

            }

        }catch (MBeanException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (InstanceNotFoundException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ReflectionException e){
            e.printStackTrace();
        	throw new DeployException(e);

        }catch (ConnectorException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (MalformedObjectNameException e){
        	e.printStackTrace();
        	throw new DeployException(e);
        }
    }
 	
    private void doResources(DeployInfo deployInfo)
    	throws ConnectorException,ConfigServiceException, DeployException,SAXException,IOException{
    	
		EARProcessor earProcessor = new EARProcessor() ;
		logger.trace("Created the instance of EARProcessor.");
		
		Vector missingResources = earProcessor.checkIfInfraResourcesExists(deployInfo,configService,session);
		SDLog.log("Missing Rresource " + missingResources.size());
		searchResource();
//		createJAASSecurity();
//		createJDBCDriver();  does not work
//		createDatasource(missingResources);
		
    }

    private void createJDBCDriver()
    	throws ConfigServiceException,ConnectorException{
			// Use this group to add to the node scoped resource.xml.
			ObjectName node1 = ConfigServiceHelper.createObjectName(null,"Node", null);
			ObjectName[] matches = configService.queryConfigObjects(session,null, node1, null);
			node1 = matches[0];     // use the first node found

			String providerName = "DB2 JDBC Provider (XA)";
			System.out.println("Creating JDBCProvider " + providerName );
			
			// Prepare the attribute list
			AttributeList provAttrs = new AttributeList();
			provAttrs.add(new Attribute("name", providerName));
			provAttrs.add(new Attribute("implementationClassName", "COM.ibm.db2.jdbc.DB2XADataSource"));
			provAttrs.add(new Attribute("description","DB2 JDBC2-compliant XA Driver"));
			
			  //create it 
			ObjectName jdbcProv = configService.createConfigData(session,node1,"JDBCProvider", "resources.jdbc:JDBCProvider",provAttrs);
			// now plug in the classpath
			configService.addElement(session,jdbcProv,"classpath","c:",-1);    
    
    }
    
    private void createJAASSecurity()
    	throws ConfigServiceException,ConnectorException{
    
    	 ObjectName security = ConfigServiceHelper.createObjectName(null,"Security", null);
    	 ObjectName[] securityName = configService.queryConfigObjects(session,null, security, null);
         security=securityName[0];

           // Prepare the attribute list
         AttributeList authDataAttrs = new AttributeList();
         authDataAttrs.add(new Attribute("alias", "jatin"));
         authDataAttrs.add(new Attribute("userId", "jatin"));
         authDataAttrs.add(new Attribute("password", "jatin"));
         authDataAttrs.add(new Attribute("description","Auto created alias for datasource"));

           //create it 
         ObjectName authDataEntry = configService.createConfigData(session,security,"authDataEntries", "JAASAuthData",authDataAttrs);
    	
    }
    
    private void searchResource()
    	throws ConfigServiceException,ConnectorException{
    	
    	ObjectName[] security = configService.resolve(session,"JAASAuthData");
    	System.out.println( "Security " + security.length); 
    	for (int i = 0 ; i < security.length ; i++){
    		
    		
    		System.out.println( "Canno Name " + security[i].getCanonicalName() );
    		System.out.println( "Alias Name " + security[i].getKeyPropertyList());
    	}
    	
    	
    }
    private void createDatasource(Vector missingResource)
    	throws ConfigServiceException,ConnectorException{
    	ObjectName JDBCProviderId = configService.resolve(session,"Cell=Avatar1Cell01:ServerCluster=Test-CL:JDBCProvider=DB2 Universal JDBC Driver Provider")[0];
        AttributeList dsAttrs = new AttributeList();
        dsAttrs.add(new Attribute("name", "Trade_DS34"));
        dsAttrs.add(new Attribute("jndiName", "jdbc/Trade_DS34"));
        dsAttrs.add(new Attribute("datasourceHelperClassname", "com.ibm.websphere.rsadapter.DB2DataStoreHelper"));
        dsAttrs.add(new Attribute("statementCacheSize", new Integer(10)));
//        dsAttrs.add(new Attribute("relationalResourceAdapter", rra)); 
        // this is where we make the link to "builtin_rra"
        dsAttrs.add(new Attribute("description", "JDBC Datasource for mark section CMP 2.0 test"));
//        dsAttrs.add(new Attribute("authDataAlias",authDataAlias));

        // Create the datasource
        System.out.println("  **  Creating datasource");
        ObjectName dataSource = configService.createConfigData(session,JDBCProviderId ,"DataSource","resources.jdbc:DataSource",dsAttrs);

        // Add a propertySet.
        AttributeList propSetAttrs = new AttributeList();
        ObjectName resourcePropertySet =
        configService.createConfigData(session,dataSource,"propertySet","",propSetAttrs);

        // Add resourceProperty databaseName
        AttributeList propAttrs1 = new AttributeList();
        propAttrs1.add(new Attribute("name", "databaseName"));
        propAttrs1.add(new Attribute("type", "java.lang.String"));
        propAttrs1.add(new Attribute("value", "Trade"));

        configService.addElement(session,resourcePropertySet,"resourceProperties",propAttrs1,-1);    	
    	
    	
    	/**    	for (int i=0 ; i < missingResource.size();i++){
    	
    		Hashtable ht = (Hashtable)missingResource.get(i);
    		String [] keys = (String[])ht.keySet().toArray(new String[0]);
    		for ()
    		
    	}**/
    	
    }
    
    private void wrapWarFile(DeployInfo deployInfo)
    	throws AppDeploymentException {
    	
		Hashtable props = new Hashtable();
		File file = new File(deployInfo.getEARFileLocation());
		String warName = file.getName();
		String earLocation = deployInfo.getWorkArea() + File.separator +   deployInfo.getApplicationName() + ".ear";
		props.put(AppConstants.APPDEPL_WEBMODULE_CONTEXTROOT, deployInfo.getContextRoot() );
		
		AppManagementHelper.wrapModule(deployInfo.getEARFileLocation(), 
				earLocation , 
    			warName + ",WEB-INF/web.xml", 
    			props);
		
		deployInfo.setEARFileLocation(earLocation);
		
    }
    
    private void updateApplication(DeployInfo deployInfo)
	 		throws DeployException{
	
			try {
				logger.trace("Get the AppManagement from the adminClient.");
				AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);
	
				logger.trace("Create the Application Manager.");
				ObjectName ApplicationManager = null;
				
				logger.trace("Create the Application Manager.");
	            String query = "WebSphere:type=AppManagement,*";
				logger.trace("Created a Application Manager.");
	            
				logger.trace("Creating a query String.");
	            ObjectName queryName = new ObjectName (query);
				logger.trace("Created a query String.");
	            
				logger.trace("Fire the query to adminclient.");
	            Set s = adminClient.queryNames(queryName, null);
				logger.trace("Fired the query to adminclient and got the results.");
	   			logger.trace("Check if the results are not empty.");
	
	            if (!s.isEmpty()){
		   			logger.trace("Get the results from the application manager.");
	                ApplicationManager = (ObjectName)s.iterator().next();
	            }
	
	//			ObjectName on = ApplicationManager ; //get MBean (type=AppManagement)
	
				Hashtable props = new Hashtable();
				props.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
			//	props.put(AppConstants.UPDATE_MODE,"update");
				props.put(AppConstants.APPUPDATE_CONTENTTYPE ,AppConstants.APPUPDATE_CONTENT_APP);
			//	props.put(AppConstants.APPUPDATE_OPERATION  , AppConstants.APPUPDATE_UPDATE );
			//	props.put(AppConstants.APPUPDATE_CONTENT_FILE  ,"C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp");
	
	//			appManagement.updateApplication("easyDeployApp", nu,"C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp",  "update",props, sessionID);
				System.out.println("Updating the application");
				appManagement.updateApplication("easyDeployApp", null,"C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp.ear",AppConstants.APPUPDATE_UPDATE,props, sessionID);
	
	
			}catch (Exception e) {
				
				e.printStackTrace();
				if (deployInfo.isFailOnError()){
					throw new DeployException(e);
				}else{
					SDLog.log("Ignoring above error as failOnError is " + deployInfo.isFailOnError());
				}
	
			}
	
	 	}



	/**
     * 
     * @param attributeValue
     * @return
     */
	public static ArrayList getArrayFromCommaSeperated(String attributeValue){
		ArrayList arrayList = new ArrayList();
		
		if ((attributeValue!=null) && attributeValue.length()>0){
		StringBuffer semicommaSeperatedString  = new StringBuffer(attributeValue);

			while(semicommaSeperatedString.indexOf(";")>-1){
				int i = semicommaSeperatedString.indexOf(";");
				String value = semicommaSeperatedString.substring( 0,i);
				arrayList.add(value);
				
				semicommaSeperatedString = new StringBuffer( semicommaSeperatedString.substring(i+1));
			}
			if (semicommaSeperatedString.indexOf(";")==-1){
				arrayList.add(semicommaSeperatedString);
			}
		}
		return arrayList; 
	} 
    
    
}
