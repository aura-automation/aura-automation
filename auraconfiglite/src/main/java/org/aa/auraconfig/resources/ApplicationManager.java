/**	   Copyright 


**/
package org.aa.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
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
import org.aa.common.properties.helper.PropertyHelper;
import com.ibm.ejs.ras.SystemOutStream;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ApplicationManager {

	Session session;
	String sessionId;
	ConfigService configService;
	AdminClient adminClient;
	Resource resource;
	Resource allResource;
	Resource referencedResources;
	ResourceHelper resourceHelper = new ResourceHelper();
	DeployInfo deployInfo;
	Vector<Resource> modifiedResource = new Vector<Resource>();
	private static final Log logger  = LogFactory.getLog(ApplicationManager.class);	

	AppManagement appManagement ;
    Hashtable props ;
	

	/**
	 * 	
	 * @param session
	 * @param configService
	 * @param adminClient
	 * @param resource
	 * @param referencedResources
	 * @param allResource
	 * @param deployInfo
	 * @param sessionId
	 */
	public ApplicationManager(Session session,
		ConfigService configService, AdminClient adminClient, Resource resource,
		Resource referencedResources, Resource allResource,DeployInfo deployInfo,String sessionId) {
		
		this.configService = configService;
		this.session = session;
		this.adminClient = adminClient;
		this.referencedResources = referencedResources;
		this.resource = resource;
		this.allResource = allResource;
		this.deployInfo = deployInfo;
		this.sessionId = "A" + sessionId;
		//this.modifiedResources = modifiedResources;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vector<Resource> processApplication()
		throws Exception {
		boolean isModified = false; 
		logger.trace(">> In process Application Manager.");
		appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);
	    props = new Hashtable();
		props.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		
		logger.trace("Get Application list from WebSphere.");
		Vector applications = appManagement.listApplications(props, sessionId);
		
		if (resource.getName().equalsIgnoreCase("EARApplication")){
			for (int i = 0; i < applications.size();i++){
				logger.trace("Matching application name to the resource name.");
				if (applications.get(i).toString().equals(ResourceHelper.getResourceIdentifierName(resource))) {
					logger.trace("Application name in the source matched for " + ResourceHelper.getResourceIdentifierName(resource));
					processChildren(ResourceHelper.getResourceIdentifierName(resource),resource);	
					resource.getParent().addDifferentChildCount();
				}
			}
		}
		logger.trace("<< In process Application Manager.");
		return modifiedResource;
	}

	/**
	 * 
	 * @param applicationName
	 * @param parentResource
	 * @throws AdminException
	 * @throws AppDeploymentException
	 * @throws DeployException
	 */
	private void  processChildren(String applicationName,Resource parentResource)
		throws AdminException,AppDeploymentException,DeployException {
		
		boolean isModified = false;
			Vector currectDataVector = appManagement.getApplicationInfo(applicationName,props,sessionId);
			for (int j = 0; j < currectDataVector.size();j++){
				AppDeploymentTask task = (AppDeploymentTask)currectDataVector.get(j);
				//SDLog.log("************** Task Name is " + task.getName()) ;
				Resource  childResource = shouldAddThisData(parentResource,task.getName());
				
				if (childResource!=null){
					processColumnNames(task,childResource);
					//currectDataVector.add(modifiedTask );
					//printColumn(task);
				}
			} 
			appManagement.setApplicationInfo(applicationName,props,sessionId, currectDataVector);
			
			
		
	}
	
	/**
	 * 
	 * @param parentResource
	 * @param dataName
	 * @return
	 */
	private Resource shouldAddThisData(Resource parentResource, String dataName){
		
		if ((parentResource.getChildren()==null) || parentResource.getChildren().size()==0){
			
		}else{
			Vector children = parentResource.getChildren();
			for (int i =0, n = children.size() ; i < n; i++ ){
				if (((Resource)children.get(i)).getName().equalsIgnoreCase(dataName)){
					return (Resource) children.get(i);
					
				}
			}
			
		} 
		return null;
	}
	
	/**
	 * 
	 * @param currentSourceChildResource
	 * @param deplTask
	 * @param dataRowCnt
	 * @return
	 */
	private boolean addtionaAttributesMatched(Resource currentSourceChildResource, AppDeploymentTask deplTask, int dataRowCnt){
		System.out.println(">>in Addtional Attr match for " + currentSourceChildResource.getContainmentPath() );
		String[][] data = deplTask.getTaskData();
		ApplicationManagerHelper helper = new ApplicationManagerHelper();
		HashMap resourceAttributeMap = currentSourceChildResource.getAttributeList();
		String[] additionalMatchAttributes =  currentSourceChildResource.getResourceMetaData().getAdditionalContainmentAttribute();
		boolean matchFound = true;
		
		if ((additionalMatchAttributes!=null) && (additionalMatchAttributes.length > 0)){
			System.out.println("additionalMatchAttributes is not null and size is greater then 0 ");
			for (int x=0; x < additionalMatchAttributes.length; x++ ){
				String addMatchAttribute = additionalMatchAttributes[x];

				int addMatchColumnNumber = helper.getDataColumnNumber(deplTask, addMatchAttribute);
				System.out.println("match for Attribute " + addMatchAttribute + " column number is " + addMatchColumnNumber);	

				String matchDataValue = data[dataRowCnt][addMatchColumnNumber];
				String matchResourceValue = null;
				if (resourceAttributeMap.get(addMatchAttribute) != null){
					matchResourceValue = resourceAttributeMap.get(addMatchAttribute).toString();
				}
				if (!data[dataRowCnt][addMatchColumnNumber].equalsIgnoreCase(matchResourceValue)){
					matchFound = false;
					x = additionalMatchAttributes.length;
				}
			}	
			
		}
		System.out.println("<<in Addtional Attr match");
		return matchFound;
	}
	
	/**
	 * 
	 * @param deplTask
	 * @param resource
	 * @throws AppDeploymentException
	 * @throws DeployException
	 */
	private void processColumnNames(AppDeploymentTask deplTask, Resource resource )
		throws AppDeploymentException,DeployException{
		
		
		logger.trace(">> In process Column data.");
		/**
		 * Get the columns numbers that we are interested in i.e 
		 * Web Modules URI
		 * Virtual Host 
		 */
		// variables for difference report generation 	
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper (); 

	
		// get java resource resource attr list
		
		String[] colName = deplTask.getColumnNames();
	
		String[][] data = deplTask.getTaskData();
		int dataRows = data.length;
	
		if (data!=null){
			

	    	Vector children = resource.getChildren();
	    	if (children !=null){
	    		logger.trace("Children count for resource " + resource.getContainmentPath() + " " + resource.getChildren().size());
		    	for (int i =0; i < children.size(); i++){

		    		Resource currentSourceChildResource = (Resource)children.get(i);
		    		if  (currentSourceChildResource.getName().equalsIgnoreCase("data")){
		    			if (!ResourceHelper.isResourceDummy(currentSourceChildResource, currentSourceChildResource.getResourceMetaData())){
		    				//if (ResourceHelper.getResourceIdentifierName(currentSourceChildResource)
		    				/**
		    				 * If the current resources containment attr value is same as the data value.
		    				 */
				    		ArrayList modifiedAttributes = new ArrayList();

		    				String matchAttribute = currentSourceChildResource.getResourceMetaData().getContainmentAttribute();
		    				
		    				String resourceDataName = ResourceHelper.getResourceIdentifierName(currentSourceChildResource);

		    				ApplicationManagerHelper helper = new ApplicationManagerHelper();
		    				int matchColumnNumber = helper.getDataColumnNumber(deplTask, matchAttribute);
		    				/**
		    				 * Check if the resource already exists in the input xml, the process this resource for any missing 
		    				 * data i.e. attributes
		    				 */
		    				boolean matchFound =false;
		    			    for (int dataRowCnt=0 ; dataRowCnt < dataRows; dataRowCnt++){
		    					HashMap resourceAttributeMap = currentSourceChildResource.getAttributeList();
		    					
		    					/**
		    					 * match attribute and additional attributes to find the correct match
		    					 */
		    					
		    					if (data[dataRowCnt][matchColumnNumber].equalsIgnoreCase(resourceDataName) && addtionaAttributesMatched(currentSourceChildResource,deplTask,dataRowCnt)){
			    					matchFound = true;

			    					Iterator attributeNameIterator = resourceAttributeMap.keySet().iterator();
			    					while (attributeNameIterator.hasNext()){
			    						String resourceAttributeName = (String )attributeNameIterator.next();
			    						String resourceAttributeValue= resourceAttributeMap.get(resourceAttributeName).toString() ;
			    						String configAttributeValue = "-";
			    						logger.trace("Modifying Resource:" + resource.getContainmentPath() + " AttributeName:" + resourceAttributeName + " AttributeValue:" + resourceAttributeValue);

			    			    		/**
			    			    		 * This is resolve the coulmn number from column name
			    			    		 */
			    				    	
		    			    		
		    							int attrColumnNumber = helper.getDataColumnNumber(deplTask, resourceAttributeName);
		    							
		    			    			if (data[dataRowCnt][attrColumnNumber]!=null){
		    			    				configAttributeValue = data[dataRowCnt][attrColumnNumber];
		    			    			}
	    			    				/**
	    			    				 * Check if the column already exists in the input xml, if it does then check if the 
	    			    				 * values are different
	    			    				 */
	    			    				if (!resourceAttributeValue.equals(configAttributeValue) ){
	    			    					//System.out.println("Adding modified attr " + resourceAttributeName);
	    			    	
	    			    					modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
	    			    					logger.trace( "		AttributeName:" + resourceAttributeName + "  oldValue: " + configAttributeValue + " new value:" + resourceAttributeValue);
	    			    					
	    									data[dataRowCnt][attrColumnNumber] = resourceAttributeValue;
	    									
	    			    				}
		    						}
			    					
			    			    	System.out.println( ""); 

		    			    	}
		    				}
		    			    
		    			    if (matchFound){
		    			    	SDLog.log("Object Exists.");	
			    				currentSourceChildResource.setModifiedAttributes(modifiedAttributes);
			    				if ((modifiedAttributes!=null) && (modifiedAttributes.size()>0)) {
				    			    //System.out.println("Adding modified attribute with size " + modifiedAttributes.size() + " to " + currentSourceChildResource.getContainmentPath() );
				    			    modifiedResource.add(currentSourceChildResource);
			    					//modifiedResources.add(currentSourceChildResource);
			    				}
			    				
		    					deplTask.setTaskData(data);

		    			    }else{
		    			    	SDLog.log("Object does not Exists.");
		    			    	resource.setModifiedAttributes(resourceDiffReportHelper.getDiffAttributes(getAttributeList(currentSourceChildResource)));
		    			    	modifiedResource.add(currentSourceChildResource);
		    			    	//modifiedResources.add(currentSourceChildResource);
		    			    	
		    			    }

		    			    
	    			    }
		    		} 
		    	}
	    	}
		}
		logger.trace("<< In process Column data.");
	}

	private AttributeList getAttributeList(Resource resource){
		// create an attribute list
		
		logger.trace("	Getting attribute from resource.xml for " + resource.getName());
		AttributeList newAttrList = new AttributeList();
		HashMap resourceAttributesMap = resource.getAttributeList();
		Iterator resourceAttributeKeyIterator =  resourceAttributesMap.keySet().iterator();
		logger.trace("	Getting Attributes MetaInfo for " + resource.getName());
		logger.trace("	Got MetaInfo " + resource.getName());
		
		
		while (resourceAttributeKeyIterator.hasNext()){
			String key = resourceAttributeKeyIterator.next().toString();
			if (key.equalsIgnoreCase(ResourceConstants.ATTRUBUTENAME) || key.equalsIgnoreCase(ResourceConstants.TEMPLATE)){
				logger.trace( "Ignoring attribute : " + key );
			}else{
				logger.trace(" Will assign value as String:" + key);
				newAttrList.add(new Attribute(key, resourceAttributesMap.get(key).toString()));
			}
		}
		return newAttrList;
	}
	
	
	private void printColumn(AppDeploymentTask deplTask )
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
			//System.out.println("Data is not null " + deplTask.getName());
			Vector columnName = new Vector();
			HashMap columnMap = new HashMap();
			int columnMatchNumber =-1;
	
		    for (int dataRowCnt=0 ; dataRowCnt < dataRows; dataRowCnt++){
		    	boolean nochildren =false;
				HashMap attributeList = new HashMap();
	
		    	
		    	for (int columnCnt=0 ; columnCnt< columnLength; columnCnt++){
			    	
		    		/**
		    		 * This is resolve the column number from column name
		    		 */
			    	
		    		if (dataRowCnt==0){
		    			columnMap.put(data[dataRowCnt][columnCnt],columnCnt);
		    			columnName.add(data[dataRowCnt][columnCnt]);
		    		}
		    		
		    		//System.out.println( " Row No: " +  dataRowCnt + " "  + " column number " + columnCnt + " : "+ data[dataRowCnt][columnCnt]); 
		    	}
		  
		    }
		}
		
	}

	
	
}
