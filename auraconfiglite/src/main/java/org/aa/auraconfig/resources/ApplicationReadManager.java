/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.management.ObjectName;

import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.aa.auraconfig.resources.metadata.ResourceMetaDataHelper;
import org.aa.auraconfig.resources.parser.ResourceParserHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.ejs.ras.SystemOutStream;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementFactory;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ApplicationReadManager {

	Session session;
	ConfigService configService;
	AdminClient adminClient;
	Resource resource;
	Resource allResource;
	Resource referencedResources;
	ResourceHelper resourceHelper = new ResourceHelper();
	DeployInfo deployInfo;
	private static final Log logger  = LogFactory.getLog(ApplicationReadManager.class);	

	AppManagement appManagement ;
    Hashtable props ;


	/**
	 * For configuration
	 * @param session
	 * @param configService
	 * @param adminClient
	 * @param resource
	 * @param referencedResources
	 * @param allResource
	 * @param deployInfo
	 */
	
	public ApplicationReadManager(Session session,
		ConfigService configService, AdminClient adminClient, Resource resource,
		Resource referencedResources, Resource allResource,DeployInfo deployInfo) {
		logger.trace(">> in the constructor");
		this.configService = configService;
		this.session = session;
		this.adminClient = adminClient;
		this.referencedResources = referencedResources;
		this.resource = resource;
		this.allResource = allResource;
		this.deployInfo = deployInfo;
		logger.trace("<< in the constructor");
	}

	/**
	 * For DeployData creation
	 * 
	 * @param session
	 * @param configService
	 * @param adminClient
	 * @param resource
	 * @param referencedResources
	 * @param allResource
	 * @param deployInfo
	 */
	public ApplicationReadManager(Resource resource,DeployInfo deployInfo) {
			logger.trace(">> in the constructor");
			this.resource = resource;
			this.deployInfo = deployInfo;
			logger.trace("<< in the constructor");
	}
	
	public void processApplicationEAR(String earName)
		throws Exception {

		Hashtable prefs = new Hashtable(); 
		prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault()); 
		System.out.println("Creating instance of AppDeplController");	
		System.out.println("Created instance of AppDeplController sucessfully");	
		
		System.out.println("Created instance of AppDeplController sucessfully");	
		prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		Properties props = new Properties();
		props.put(AppConstants.APPDEPL_DFLTBNDG , AppConstants.YES_KEY);
//		 props.put (AppConstants.APPDEPL_DFLTLTBNDG_FORCE, AppConstants.YES_KEY);
		prefs.put (AppConstants.APPDEPL_DFLTBNDG, props);
		AppDeploymentController flowController =	AppManagementFactory.readArchive (earName, prefs); 

		AppDeploymentTask task = flowController.getFirstTask();
		
		String applicationName = (new File(earName)).getName();
		SDLog.log("Incoming ConfigObjects for the containment path " + resource.getContainmentPath() + " "+ applicationName);

		Resource newResource = createNewResource(applicationName);
		
		 while (task != null){
			// System.out.println("task.getName() " +  task.getName());
			 if (task.getTaskData()!=null){
				// System.out.println("	Data not null " +  task.getName());
					processChildren(task,newResource,newResource);

			 }else{
				// System.out.println("	Data is null " +  task.getName());
			 }
			    task = flowController.getNextTask();

		 }
		
		
	
		//processChildren(applicationDataVector,newResource);
				
		logger.trace("<< process EAR Application");
	}
	
	public void processApplication()
		throws Exception {
		logger.trace(">> process Application");
		appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);
	    props = new Hashtable();
		props.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		
		
		Vector applications = appManagement.listApplications(props, session.getSessionId());
		logger.trace(" Got Application list, size is " + applications.size());

		if (resource.getName().equalsIgnoreCase("EARApplication")){
			for (int i = 0; i < applications.size();i++){
				
				if (applications.get(i).toString().equals(ResourceHelper.getResourceIdentifierName(resource))) {
					logger.trace("Process the resource with application name " + applications.get(i).toString() + " for attributes");
					processChildren(ResourceHelper.getResourceIdentifierName(resource),resource, resource);
					 
				}else if (ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData())){
					SDLog.log("Incoming ConfigObjects for the containment path " + resource.getContainmentPath() + " "+ applications.get(i).toString());
					logger.trace("Process the dummy resource for all application, current app is  ["  + i +  "] " +  applications.get(i).toString() + " for attributes");
					
					logger.trace("Will create a new resource for application " + applications.get(i).toString());
					
					Resource newResource = createNewResource(applications.get(i).toString());
					newResource.setContainmentPath(newResource.getContainmentPath() + "=" + applications.get(i).toString() );
					processChildren(applications.get(i).toString(),newResource, resource);
				}
					// 	
	
					
					// resource.getParent().addDifferentChildCount();
			}
		}else{
			logger.trace(" Resources is not application, is is " + resource.getName() +  " hence will not process");
		}
		logger.trace("<< process Application");
		
	}

	
	private Resource createNewResource(String applicationName)
		throws AdminException,AppDeploymentException,DeployException {
		
		logger.trace("Creating Skelton resource for " + resource.getName() + " " + applicationName);
		Resource  newResource = resourceHelper.createSkeletonResource(resource.getName(), resource.getParent(), null);
		//resource.getParent().addChild(newResource);
		/** Resource  newResource = new Resource();
		newResource.setName(resource.getName());
		newResource.setIncoming(true);
		newResource.setParent(resource.getParent());
		newResource.setParentTree(resource.getParentTree());
		newResource.setResourceMetaData(resource.getResourceMetaData());
		**/
		SDLog.log("In application Manager the new resource meta data type is  " + resource.getResourceMetaData().getType());
		HashMap attributeList = new HashMap();
		attributeList.put("name", applicationName);
		
		newResource.setAttributeList(attributeList);
		
		//Vector currectDataVector = appManagement.getApplicationInfo(applicationName,props,session.getSessionId()) ;

		 

		return newResource;
	}
	
	
	
	private void processChildren(String applicationName,Resource parentResource, Resource originalResource)
		throws AdminException,AppDeploymentException,DeployException {
		
		
			Vector currectDataVector = appManagement.getApplicationInfo(applicationName,props,session.getSessionId()) ;
			
			boolean extractAll = false;
			
			
			logger.trace("Checking if resource " + originalResource.getContainmentPath() + " has in children");
			// if the client want to extract MapUserRole for all applications then we need to get all application using the dummy EARApplication
			// then for each application add the new dummy resource like MapServerCLuster, or UserRole
			if (originalResource.getChildren() == null)  {
				logger.trace("Resource " + parentResource.getContainmentPath() + " does not have children");
				extractAll = true;
			}
			
			else{
				logger.trace("Resource " + parentResource.getContainmentPath() + " does have children");
				Vector<Resource> children = originalResource.getChildren();
					for (int l=0 ; l < children.size();l++ ){
						logger.trace("Adding child " + children.get(l).getContainmentPath() +  " to " + parentResource.getContainmentPath() );
						
						parentResource.addChild( resourceHelper.createSkeletonResource(children.get(l).getResourceMetaData().getType() ,parentResource,null));
						// due my laziness to investigate the issue (if Incoming children is not set to null, then output xml should duplicate data hence seting to null) 
						// if I get time then need to fix this properly
						parentResource.setInComingChildren(null);
					}
			}
		
			
			for (int j = 0; j < currectDataVector.size();j++){
				AppDeploymentTask task = (AppDeploymentTask)currectDataVector.get(j);
				String[][] data = task.getTaskData();
			//	System.out.println("$$$$$$$$ Task Name is " + task.getName() );
			//	printColumnName(task);
				if (data!=null){
					Resource  childResource ;
					if (extractAll){
						/**
						 * If there is no child resource in the source xml then we will extract all the data
						 */
						logger.trace("Will extract all the children of application " + applicationName);
						childResource = resourceHelper.createSkeletonResource(task.getName(),parentResource,null);
	
					}else{
						/**
						 * If the is child in the source xml then match that child and only process that child	
						 */
						logger.trace("Will selectively extract the children of application " + applicationName + " passing parent resources as " + parentResource.getContainmentPath());
						childResource = shouldAddThisData(parentResource,task.getName());
					}
					
					if (childResource !=null){
						childResource.setAttributeList(new HashMap());
						processIncomingColumnNames(task,childResource);
					}
				}
			} 
		
		
	}
	
	private void processChildren(AppDeploymentTask task,Resource parentResource , Resource originalResource)
		throws AdminException,AppDeploymentException,DeployException {
		
		
			
			boolean extractAll = false;
			
			if (parentResource.getChildren()==null){
				extractAll = true;
			}
		
			extractAll = true;
			
			if (task!=null){
				logger.trace("Start processing " + task.getName() );
				String[][] data = task.getTaskData();
			//	System.out.println("$$$$$$$$ Task Name is " + task.getName() );
			//	printColumnName(task);
				if (data!=null){

					Resource  childResource ;
					if (extractAll){
						/**
						 * If there is no child resource in the source xml then we will extract all the data
						 */
						SDLog.log("Processing " + task.getName());

						childResource = resourceHelper.createSkeletonResource(task.getName(),parentResource,null);

					}else{
						/**
						 * If the is child in the source xml then match that child and only process that child	
						 */
						SDLog.log("Processing " + task.getName());
						childResource = shouldAddThisData(parentResource,task.getName());
					}
					
					if (childResource !=null){
						childResource.setAttributeList(new HashMap());
						processIncomingColumnNames(task,childResource);
						
					}
				}else{
					logger.trace("Data is null " + task.getName());
				}
			}else{
				//System.out.println("Task was null");
			}
		
		
		
	}

	private Resource shouldAddThisData(Resource parentResource, String dataName){
		Resource originalResource;
		
		logger.trace(">> In shouldAdd");
		logger.trace("Checking for " + ResourceHelper.getResourceIdentifierName(parentResource) + " for data " + dataName);

		if ((parentResource.getChildren()!=null))  {
			
			Vector children = parentResource.getChildren();
			
			for (int i =0, n = children.size() ; i < n; i++ ){
				Resource childResource = (Resource)children.get(i);
				logger.trace(" In source XML the child is " + childResource.getName());
				if (((Resource)children.get(i)).getName().equalsIgnoreCase(dataName)){
					logger.trace("Will return from shouldAdd " + ((Resource) children.get(i)).getContainmentPath() + " for data " + dataName);
					logger.trace("<< In shouldAdd");
					
					return (Resource) children.get(i);
					
				}
			}
		} 

		logger.trace("Will not add " + ResourceHelper.getResourceIdentifierName(parentResource) + " for data " + dataName);
		logger.trace("<< In shouldAdd");
		return null;
	}

	private Resource getMatchingDataResource(Resource parentResource, String dataName){
		logger.trace(">> In getMatchingDataResource");
		logger.trace("Checking for " + ResourceHelper.getResourceIdentifierName(parentResource) + " for data " + dataName);

		if ((parentResource.getChildren()!=null))  {
			
			Vector children = parentResource.getChildren();
			for (int i =0, n = children.size() ; i < n; i++ ){
				Resource childResource = (Resource)children.get(i);
				System.out.println(" In source XML the child is " + childResource.getName());
				String resourceIdentifierName = ResourceHelper.getResourceIdentifierName(childResource);
				if (resourceIdentifierName .equalsIgnoreCase(dataName)){
					return (Resource) children.get(i);
					
				}
			}
		} 
		return null;
	}
	
	private void processIncomingColumnNames(AppDeploymentTask deplTask, Resource resource )
		throws AppDeploymentException,DeployException{
		/**
		 * Get the columns numbers that we are interested in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
		
		String[] colName = deplTask.getColumnNames();
		int columnLength = colName.length;

		String[][] data = deplTask.getTaskData();
		int dataRows = data.length;

		if (data!=null){
			// System.out.println("Data is not null " + deplTask.getName());
			Vector columnName = new Vector();
			HashMap columnMap = new HashMap();
			int columnMatchNumber =-1;

		    for (int dataRowCnt=0 ; dataRowCnt < dataRows; dataRowCnt++){
		    	boolean nochildren =false;
				HashMap attributeList = new HashMap();

		    	Resource  childResource = null;
		    	Vector children = resource.getChildren();
		    	if (children !=null){
			    	for (int i =0; i < children.size(); i++){
			    		Resource currentSourceChildResource = (Resource)children.get(i);
			    		if  (currentSourceChildResource.getName().equalsIgnoreCase("data")){
			    			if (!ResourceHelper.isResourceDummy(currentSourceChildResource, currentSourceChildResource.getResourceMetaData())){
			    				//if (ResourceHelper.getResourceIdentifierName(currentSourceChildResource)
			    				/**
			    				 * If the current resources containment attr value is same as the data value.
			    				 */
			    				String matchAttribute = ((Resource)children.get(i)).getResourceMetaData().getContainmentAttribute();
			    				ApplicationManagerHelper helper = new ApplicationManagerHelper();
			    				int matchColumnNumber = helper.getDataColumnNumber(deplTask, matchAttribute);
			    				/**
			    				 * Check if the resource already exists in the input xml, the process this resource for any missing 
			    				 * data i.e. attributes
			    				 */
			    				if (data[dataRowCnt][matchColumnNumber].equalsIgnoreCase(ResourceHelper.getResourceIdentifierName((Resource)children.get(i)) )){
			    					childResource =(Resource)children.get(i);
			    					attributeList = ((Resource)children.get(i)).getAttributeList();
			    				}
			    			}
			    		} 
			    	}
		    	}
		    		    	
		    	
		    	if (childResource == null){
			    	childResource = resourceHelper.createSkeletonResource("data",resource,null);
		    	}
		    	
		    	HashMap incomingAttributeList = new HashMap();
		    	for (int columnCnt=0 ; columnCnt< columnLength; columnCnt++){
			    	
		    		/**
		    		 * This is resolve the coulmn number from column name
		    		 */
			    	
		    		if (dataRowCnt==0){
		    			columnMap.put(data[dataRowCnt][columnCnt],columnCnt);
		    			columnName.add(data[dataRowCnt][columnCnt]);
		    		}else{
		    			if (data[dataRowCnt][columnCnt]!=null){
		    				/**
		    				 * Check if the column already exists in the input xml, if it does then do not add
		    				 */
		    				if (attributeList.get(columnName.get(columnCnt))==null)
		    					logger.trace(" Adding " + columnName.get(columnCnt) + " value " + data[dataRowCnt][columnCnt]);
		    				
		    					incomingAttributeList.put(columnName.get(columnCnt), data[dataRowCnt][columnCnt]);
		    			}
		    		}
		    		
		    		//System.out.print( " Row No: " +  dataRowCnt + " "  + " column number " + columnCnt + " : "+ data[dataRowCnt][columnCnt]); 
		    	}
				childResource.getAttributeList().putAll(incomingAttributeList) ;
		    	
				ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
				ArrayList incomingAttrList = resourceDiffReportHelper.getDiffAttributesForIncoming(incomingAttributeList);
				if (childResource.getModifiedAttributes()==null)
					childResource.setModifiedAttributes(incomingAttrList);
				else
					childResource.getModifiedAttributes().addAll(incomingAttrList);

		    	System.out.println( ""); 
		  
		    }
		}
		    
	}
	
	private void printColumnName(AppDeploymentTask deplTask){
		String[] colName = deplTask.getColumnNames();
		int columnLength = colName.length;

		for (int columnCnt=0 ; columnCnt< columnLength; columnCnt++){
			System.out.println("		Column Number is " + columnCnt + " column Name is " + colName[columnCnt] );
		}

	}
}
