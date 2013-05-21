
/**	   Copyright 


**/
package org.aa.auraconfig.resources.creator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.DiffAttribute;
import org.aa.auraconfig.resources.LinkAttribute;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceConstants;
import org.aa.auraconfig.resources.ResourceDiffReportHelper;
import org.aa.auraconfig.resources.ResourceHelper;
import org.aa.auraconfig.resources.applicationmanager.ApplicationManager;
import org.aa.auraconfig.resources.command.CommandManager;
import org.aa.auraconfig.resources.configreader.WASConfigReader;
import org.aa.auraconfig.resources.customcode.CustomCodeManager;
import org.aa.auraconfig.resources.metadata.CommandAttribute;
import org.aa.auraconfig.resources.metadata.CommandLinkAttribute;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.aa.auraconfig.resources.parser.ResourceXMLParser;
import org.aa.auraconfig.resources.parser.ResourceXMLWriter;
import org.aa.auraconfig.resources.rules.RuleValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.aa.common.Constants.DeployValues;
import org.aa.common.Constants.LicenseConstants;
import org.aa.common.deploy.Connection;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertyHelper;
import org.aa.common.version.VersionInfo;
import org.aa.common.wasproduct.WASProduct;
import org.aa.common.wasproduct.WASProductFinder;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.cmdframework.CommandException;
import com.ibm.websphere.management.cmdframework.CommandMgrInitException;
import com.ibm.websphere.management.cmdframework.CommandNotFoundException;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ResourceCreator extends Connection{
	private static final Log logger  = LogFactory.getLog(ResourceCreator.class);	
	ResourceCreatorHelper resourceCreatorHelper = new ResourceCreatorHelper();
	ObjectName scope = null;
	Resource allResources;
	Vector<Resource> modifiedResources = new Vector<Resource> ();
	Vector<Resource>  invalidResource = new Vector<Resource> ();
	int level = 0;
	boolean reportOnlyMode = false;
	DeployInfo mDeployInfo;
	/**
	 * 1: Create a connection to the target server using createAdminClient method from the inherited class
	 * 2: Parse resources.xml using method parseResourcesXML. This return Java object Resource
	 * Resource object contains children Resource Objects, It contains all the objects that should be either created or 
	 * modified. Each Resource object has type, containmentPath, parent, attributelist, configId, arrayList
	 * 3: Get ResourceMetaDataMap. This contains Meta Data information of each resource type. Information like 
	 * shouldCreate, relation to parent, isProperty, findandResolve, containmentAttriute  
	 * 4: Call to processConfigObjects
	 * @throws DeployException
	 */

	public Resource start(String resourceXML,String rulesXML ,InputStream referencedResourceXMLInputStream,
			InputStream resourceXMLMetaDataInputStream,DeployInfo deployInfo) 
		throws DeployException{
		ResourceXMLParser resourceXMLParser = new ResourceXMLParser(); 
		Element rootNode = resourceXMLParser.getResourcesXMLElements(resourceXML,false);
		return start(rootNode,rulesXML,referencedResourceXMLInputStream,resourceXMLMetaDataInputStream,deployInfo);
	}
	
	/**
	 * Note that the return attribute is used only for 
	 * UI. So that GWT UI can display the output
	 * 
	 * @param rootElement
	 * @param referencedResourceXMLInputStream
	 * @param resourceXMLMetaDataInputStream
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 */
	public Resource start(Element rootElement,String rulesXML,InputStream referencedResourceXMLInputStream,
			InputStream resourceXMLMetaDataInputStream,DeployInfo deployInfo) 
		throws DeployException{
		SDLog.log( Calendar.getInstance().getTime().toGMTString());
		SDLog.log( "Connection Mode " + deployInfo.getConnectionMode());
		SDLog.log( "Operation Mode : "+  deployInfo.getOperationMode());
		
		/**
		 * Initialise the variables in ResourceConstants from 
		 * Constants properties file
		 */
		ResourceHelper.initialiseProperites();
		
		mDeployInfo = deployInfo;
		try{

			createAdminClient(deployInfo);
			

			String[] array = configService.getUnsavedChanges(session);
			for (int i=0;i<array.length;i++ ){
				SDLog.log( array[i]);
			}

//			WASProductFinder wasProductFinder = new WASProductFinder();
//			WASProduct wasProduct = wasProductFinder.getProduct(adminClient, sessionID);
			
//			logger.trace( "wasProduct.getWASProduct " +  wasProduct.getWASProduct());
//			logger.trace( "wasProduct.isWASOnly() " +  wasProduct.isWASOnly());

//			if (deployInfo.getLicenseInfo().getEdition().equalsIgnoreCase(LicenseConstants.LICENSE_EDITION_WASONLY) && (!wasProduct.isWASOnly())){
//				SDLog.log("Invalid License, License is valid for WebSphere Application Server only, Target WAS product is " + wasProduct.getWASProduct());
//				throw new DeployException(new Exception("Invalid License,  License is valid for WebSphere Application Server only, Target WAS product is " + wasProduct.getWASProduct()));
//			}

//			WASProduct wASProduct = new WASProduct();
//			wASProduct.getEFixCount();
			
			//UnManagedProcess
			// [AuraConfigLiteResource] WebSphere:cell=widCell,j2eeType=J2EEServer,mbeanIdentifier=cells/widCell/nodes/widNode/servers/
			// server1/server.xml#Server_1208948257500,name=server1,node=widNode,platform=proxy,process=server1,processType=UnManagedPr
			// ocess,type=Server,version=6.0.2.19
			
//			SDLog.log("version " + adminClient.getServerMBean().getKeyProperty("version"));
//			SDLog.log("processType " + adminClient.getServerMBean().getKeyProperty("processType"));
//			SDLog.log("KeyPropertyListString " + adminClient.getServerMBean().getKeyPropertyListString());
			//SDLog.log(adminClient.getServerMBean().getCanonicalName());
		//	WsAdmin wsAdmin = new WsAdmin();
		//	System.out.println (" wsAdmin.getLocation " + wsAdmin.getLocation()); 
			ResourceXMLParser resourceXMLParser = new ResourceXMLParser();
			Resource resources = resourceXMLParser.getResourcesFromXML(rootElement,rulesXML,resourceXMLMetaDataInputStream,false,deployInfo,configService);
			
			
			logger.trace(" Verion value in the passed XML file is " + rootElement.getAttribute("version"));
			VersionInfo versionInfo = new VersionInfo();
			if (rootElement.getAttribute("version")!=null){
				versionInfo.setVersionNumber(rootElement.getAttribute("version").getValue());
			}else{
				versionInfo.setVersionNumber(null);
			}
			deployInfo.setVersionInfo(versionInfo);
			
			List <String> missingAttributeList= resources.getMissingAttributeList();
			
			if (missingAttributeList.size()>0){
				return resources;
			}
			
			Resource referencedResources = resourceXMLParser.getReferenceResources(referencedResourceXMLInputStream,resourceXMLMetaDataInputStream,false,deployInfo);
			
			allResources = resources;
			return processConfigObjects(resources,referencedResources,deployInfo);
			
		}catch(ConnectorException e){
			e.printStackTrace();
			logger.error(e.toString());
			throw new DeployException(e);
		}catch(ConfigServiceException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(AttributeNotFoundException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(AdminException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(IOException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(JDOMException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(MalformedObjectNameException e ){
			logger.error(e.toString());
			throw new DeployException(e);
		}catch(Exception e ){
			logger.error(e.toString());
			throw new DeployException(e);
		}	
	}

	/**
	 * 		 PART 1 : check if ConfigObject exists//
	 *	get connection to AdminClient
	 *	get JMX configService, session objects 
	 *  for each resource in the resources variable
	 *  get resource name, get containment path, and check if resource exists

	 * @param resources
	 */
	private Resource processConfigObjects (Resource resource,Resource referencedResources,DeployInfo deployInfo)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException,Exception{
		
		
		

		SDLog.log("");
		SDLog.log("");
		SDLog.log("");
		if (resource.getChildren()!=null && resource.getChildren().size()>0){
			checkAllChildObjectsIfExists(resource,referencedResources,deployInfo);
		}
		
		
		if (deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC)){
			SDLog.log("************** Start checking for Incoming changes********************");
	
			if (resource.getChildren()!=null && resource.getChildren().size()>0){
				WASConfigReader wasConfigReader = new WASConfigReader();
				wasConfigReader.checkInComingChanges(session,configService,adminClient,resource,referencedResources,resource,deployInfo);
				
				
			}
			SDLog.log("*******************************************");
			SDLog.log("End checking for Incoming changes");
			SDLog.log("*******************************************");
			
		}		
		
		
		if (!deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC)){
			SDLog.log("************* Start validating resources ***************");

			validate(resource, deployInfo);
			SDLog.log("************** End validating resources*******************");
		}else{
			SDLog.log("************** Validating not required *******************");	
		}
		
		
		if (invalidResource.size()>0){
			SDLog.log( "Following resources are invalid.");
			for (int invRes =0 ; invRes < invalidResource.size();invRes ++){
				SDLog.log( "["+ invRes +"] " + ((Resource)invalidResource.get(invRes)).getContainmentPath());
			}
			SDLog.log("As the resource is invalid " );
			SDLog.log("No changes were saved." );
		}else{
			
		
			
			if (!deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC )){	
				SDLog.log("*******************************************");
				SDLog.log("Start Report");
				SDLog.log("*******************************************");
		
				
		
				
				SDLog.log("************** Resource changes are *******************");
				
				ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
				resourceDiffReportHelper.generateReport(modifiedResources); 
		
				SDLog.log("************** End Resource changes *******************");
		
				SDLog.log("");
				SDLog.log("");
		
				
				SDLog.log("************** Config files changes are *******************");
				
				String[] array = configService.getUnsavedChanges(session);
				// This will tell us if the config is going to change, set the property
				if (array.length > 0 ){
					resource.setHasAnyChange(true);
				}
				for (int i=0;i<array.length;i++ ){
					SDLog.log( array[i]);
				}
				SDLog.log("************** End Config files changes *******************");
				SDLog.log("*******************************************");
				SDLog.log("End Report");
				SDLog.log("*******************************************");
			}
		
			if (deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_NORMAL)){
				SDLog.log("Save session");
				configService.save(session, false);
		
			}else{
				SDLog.log("As the operation mode is " + deployInfo.getOperationMode() );
				SDLog.log("No changes were saved." );
				
			}
		}
		ResourceXMLWriter resourceXMLWriter  = new ResourceXMLWriter();
		resourceXMLWriter.createResourceXMLFile(resource,deployInfo); 

		// PART 2: Create if Config Object does not exists //
		// if resource does not exists
		// get the config id of the immediate parent
		// create attribute array
		// get relation to the parent.
		// create objects

		// PART 3: Modify if Config Object does exists //
		//printChild(resource);
		return resource;
	}
	/**
	 * 
	 * @param resource
	 */
	private void printChild(Resource resource){
		
		Iterator it = resource.getChildren().iterator();
		while (it.hasNext()){
			Resource childResource = (Resource)it.next();
			if (childResource.getInComingChildren()!= null){
				printChild(childResource);
			}
		} 
	}
	
	private void validate(Resource resource, DeployInfo deployInfo)
		throws DeployException{
		level ++;
		Resource[] children; 
		RuleValidator ruleValidator =new RuleValidator();
		if (resource.getChildren()!=null){
			
			children = (Resource[]) resource.getChildren().toArray(new Resource[0]);
		}else{
			children = new Resource[0];
		}
		
		for (int childCnt=0;childCnt < children.length;childCnt++){

			String type= children[childCnt].getName();
			//logger.trace("Will validate " + ((Resource)children[childCnt]).getContainmentPath()  + " of type " +  ((Resource)children[childCnt]).getName() + " resource rules metadata as " +  ((Resource)children[childCnt]).getResourceMetaData().getResourceRulesMetaData() + " with editable property as " + ((Resource)children[childCnt]).getResourceMetaData().getEditable());
			if (!ruleValidator.isResourceValid(children[childCnt], "create")){
				System.out.println("Resource is invalid " + ((Resource)children[childCnt]).getContainmentPath() + " of type " +  ((Resource)children[childCnt]).getName());
				invalidResource.add(children[childCnt]);
			}
	         
			validate(children[childCnt],deployInfo);
		}
							
			
		
	}

	
	/**
	 * 1: check if there are children for the resource object
	 * 2: For each child resource object, if isArray is not true
	 * 		Check if Config Object Exists 
	 * 		Create if does not exists
	 * 4: If isArray is true, get the attribute from parent Config Object
	 * 		 Match the name value 
	 * 		 Modify if different
	 * 5: If the current type being searched is ServerCluster then set scope variable
	 * 	
	 * @param resource
	 * @param resourceMetaDataMap
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private void checkAllChildObjectsIfExists(Resource resource, Resource referenceResources,DeployInfo deployInfo)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException,Exception{
		level ++;
		Resource[] children; 
		
		/**
		 * This is used to check if the config object cannot be 
		 * created because it exists in other Provider within same scope.
		 * 
		 */
		boolean couldCreateNewObject = true;
		if (resource.getChildren()!=null){
			
			children = (Resource[]) resource.getChildren().toArray(new Resource[0]);
		}else{
			children = new Resource[0];
		}
		
		for (int childCnt=0;childCnt < children.length;childCnt++){
			SDLog.log("[" +  children[childCnt].getName() + " | " +  ResourceHelper.getResourceIdentifierName(children[childCnt]) + "]" , level );

			String type= children[childCnt].getName();
			/**
			 * Check if the resource specified is only for the extract purpose then 
			 * no need to check if anything has changed in the resources to get difference resport. 
			 * We only need to run extract for this resource. 
			 * 1: If the resources does not have any attributes or 
			 * 2: If the attribute that is specified is reference attribute
			 * 3: except if the resource is designed not to have any attribute the don't ignore the  
			 */
			ResourceMetaData resourceMetaData = children[childCnt].getResourceMetaData();
			if (resourceMetaData.getResourceRulesMetaData()!=null){
				logger.trace(children[childCnt].getContainmentPath() +  " Resource Rules Meta Data is not null");
			}else{
				logger.trace(children[childCnt].getContainmentPath() + " Resource Rules Meta Data is null");
			}
			if (!ResourceHelper.isResourceDummy(children[childCnt],resourceMetaData))  {

	
				logger.trace("Checking is type " + type + " is a property, metadata is" + resourceMetaData);
			//	if (resourceMetaData.isCommandManaged() && (deployInfo.getVersionInfo().getMajorNumber() >=2 )   && (children[childCnt].getResourceMetaData().getCommandMetaData().getShowCommand().trim().length() >0 )){
			//		showConfigObjectUsingCommand(children[childCnt],referenceResources,scope,deployInfo );
			//	}else 
				if (resourceMetaData.isApplicationManaged()){
					if (children[childCnt].getName().equalsIgnoreCase("EARApplication")){
						ApplicationManager applicationManager = new ApplicationManager(session,configService,adminClient,children[childCnt],referenceResources,resource,deployInfo,sessionID);
						modifiedResources = applicationManager.processApplication();
					}
				}else if (resourceMetaData.getIsProperty() && !resourceMetaData.isArray()){
					logger.trace("Type " + type + " is a Property and not an Array");
					processProperty(children[childCnt],resourceMetaData,referenceResources,deployInfo);
					
				}else if (resourceMetaData.getIsProperty() && resourceMetaData.isArray()){
					logger.trace("Type " + type + " is Array and Property");
					
					checkArrayPropertyAttribute(children[childCnt],resourceMetaData,referenceResources,deployInfo);
					//configService.getAttribute(session, children[childCnt].getParent().getConfigId(), type);
					
					
				}else if (resourceMetaData.isArray() && (!resourceMetaData.isCommandManaged()) ){
					logger.trace("Type " + type + " is Array");
					
					checkArrayCollectionAttribute(children[childCnt],resourceMetaData,referenceResources,deployInfo);
					//configService.getAttribute(session, children[childCnt].getParent().getConfigId(), type);
					
				}else{
					logger.trace("Type " + type + " is not an Array");
					// check if the current object exists if not create it 
					boolean doesResourceExistsInTarget = resourceCreatorHelper.checkIfConfigObjectExists(children[childCnt],referenceResources,deployInfo,scope,configService,session,allResources,true );
					if (!doesResourceExistsInTarget){
							// when creating a new object check if command managed or custom code managed then call 
							// AdminTask style commands to create the object.
							ObjectName newObjectConfigId =null; 
							// if version 2 and command managed
							logger.trace("Will create new object for  " + type );
							
							if ((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (children[childCnt].getResourceMetaData().isCommandManaged())){
								newObjectConfigId = createConfigObjectUsingCommand(children[childCnt],referenceResources,scope,deployInfo );
							// if version 2 and custom code managed
							}else{
								newObjectConfigId = createConfigObject(children[childCnt],referenceResources,scope,deployInfo );
								// As new resource does not exists and has been created we will increase the counter of differnt resources
							}
							if (newObjectConfigId==null){
								logger.warn(" New not Created " );
								couldCreateNewObject = false;
							}else{
								logger.trace(" New Created " + newObjectConfigId.getCanonicalName() );
								SDLog.log(" Object Created " );
								children[childCnt].setConfigId(newObjectConfigId);
							}
							
					}else{
						logger.debug("Object Exists " + children[childCnt].getContainmentPath() + " will call modify");
						
						resourceCreatorHelper.modifyConfigObject(children[childCnt],referenceResources,deployInfo,configService,adminClient,session,scope,modifiedResources,allResources);
						
						// If resource is modified we will increase the counter of different resources
					}
	
				}
				
				//TODO: Check this in case of J2EEResourcePropertySet this 
				// is not working
				if (children[childCnt].getModifiedAttributes()!=null){
					logger.trace("Modified count for " + children[childCnt].getContainmentPath() + " is " +  children[childCnt].getModifiedAttributes().size());
				
					if (children[childCnt].getModifiedAttributes().size() > 0){
						children[childCnt].getParent().addDifferentChildCount();
					}
				}
						
				// If type ServerCluster then set scope.
				if (type.equalsIgnoreCase("ServerCluster")){
					if (children[childCnt].getConfigId()!=null){
						scope = children[childCnt].getConfigId();
					}
				}else if (type.equalsIgnoreCase("Node")){
					
					if (children[childCnt].getConfigId()!=null){
						scope = children[childCnt].getConfigId();
					}
				}else if (type.equalsIgnoreCase("Server")){
					if (children[childCnt].getConfigId()!=null){
						scope = children[childCnt].getConfigId();
					}
					
				}else if (type.equalsIgnoreCase("Cell")){
					if (children[childCnt].getConfigId()!=null){
						scope = children[childCnt].getConfigId();
					}
				}			
				// after creating the parent check if child exists
				SDLog.log("");
			
				if (couldCreateNewObject){
					checkAllChildObjectsIfExists(children[childCnt],referenceResources,deployInfo);
				}
				if (children[childCnt].getDifferentChildCount()>0) {
					children[childCnt].getParent().addDifferentChildCount();
				}
			}else{
				// System.out.println(" Resource " + children[childCnt].getContainmentPath() + " to dummy." );
				children[childCnt].setDummy(true);
			}
			level --;
		}
		
	}

	
	/**
	 * 
	 * @param resource
	 * @param resourceMetaData
	 * @param referencedResources
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws MalformedObjectNameException 
	 *  
	 */
	private void processProperty(Resource resource, ResourceMetaData resourceMetaData, Resource referencedResources,DeployInfo deployInfo)
		throws AttributeNotFoundException, ConnectorException,ConfigServiceException,DeployException, MalformedObjectNameException{
		String type = resource.getName();
//		SDLog.log("		Checking if Attribute: " + type + " exists for the Object: " + resource.getParent().getName());
		Object  attrObject = configService.getAttribute(session, resource.getParent().getConfigId(), resourceMetaData.getAttributeName());
		if (attrObject==null){
		
			SDLog.log( "		Attribute: " + type +" does not exists ");
			ObjectName objectName = null; 
			
			
			if (resourceMetaData.getAttributeName()!=null){

				logger.trace("		As the attribute " + resourceMetaData.getAttributeName() + " is missing for type " + resource.getName()  + ", Create a new attribute" );
				AttributeList arrtList = new AttributeList();
				logger.trace("Checking if metadata allows for creation of " + resource.getName() + " with metadata of " + resource.getResourceMetaData().getType());

				if (resource.getResourceMetaData().isShouldCreate()){
					objectName = configService.createConfigData(session, resource.getParent().getConfigId(), resourceMetaData.getAttributeName(), type, arrtList);
					resource.setConfigId(objectName);
				}else{
					SDLog.log("		Will not create this new Attribute " + resource.getContainmentPath() ) ;
				}
				// arrtList= ResourceHelper.getDefaultAttributeList(configService.getAttributesMetaInfo(resourceMetaData.getConfigType()));
			}else{
				logger.trace( "Config Type:" + type + " is same to relation i.e. attibute name:" + type + ".Create new");

				AttributeList arrtList = ResourceHelper.getDefaultAttributeList(configService.getAttributesMetaInfo(type));
				logger.trace("Checking if metadata allows for creation of " + resource.getName() + " with metadata of " + resource.getResourceMetaData().getType());

				if (resource.getResourceMetaData().isShouldCreate()){
					objectName = configService.createConfigData(session, resource.getParent().getConfigId(), type, type, arrtList);
					resource.setConfigId(objectName);
				}else{
					SDLog.log("		Will not create this new Attribute " + resource.getContainmentPath() ) ;
				}
			} 
			SDLog.log( "		Attribute: " + type +" created: " + objectName);
			resource.setConfigId(objectName);
		}else{
			SDLog.log("		Attribute exists");
			ObjectName attrObjectName = ConfigServiceHelper.createObjectName((AttributeList)attrObject);
			resource.setConfigId(attrObjectName );
			resourceCreatorHelper.modifyConfigObject(resource,referencedResources,deployInfo,configService,adminClient,session,scope,modifiedResources,allResources );
		}

	}

	/**
	 * 1: Get the ArrayList from the given config object (Contains AttributeList for BusName, QueueName)
	 * 2: For each array get AttributeList ([name:Name value:BusName type:String],[name:value value:SCA.APPLICATION type:String])
	 * 3: If ArrayList is null then add all the attributes as new
	 * 4: Now check each attribute list and match the attributelist name/value pair with resource name/value pair. where name is containment path.
	 * 5: if value of both matches for containment variable set current resources configId and then goto modify
	 * 6: if non match then add new attributelist to the array.
	 * 
	 * @param resource
	 * @param resourceMetaData
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException 
	 */
	private void checkArrayPropertyAttribute(Resource resource,ResourceMetaData resourceMetaData, Resource referencedResources,DeployInfo deployInfo)
		throws AttributeNotFoundException, ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException, MalformedObjectNameException{


//		SDLog.log( " MetaInfo " + configService.getAttributesMetaInfo(resource.getParent().getName()));
		logger.trace("Getting attribute: " + resourceMetaData.getAttributeName() + " for parent + " + resource.getParent().getConfigId());
		ArrayList arrayOfAttrList = (ArrayList)configService.getAttribute(session, resource.getParent().getConfigId(), resourceMetaData.getAttributeName());
		logger.trace("Got attribute: " + resourceMetaData.getAttributeName() + " for parent + " + resource.getParent().getName());
		LinkAttribute linkAttribute = ResourceHelper.getLinkAttribute(resource,resourceMetaData.getContainmentPath());
		String attributeValueOfResourceObject = null; 
		
		if (linkAttribute !=null){
			attributeValueOfResourceObject = resourceCreatorHelper.getLinkAttributeForMatchAttributeValue(resource, linkAttribute, resourceMetaData.getContainmentPath(), referencedResources, scope,deployInfo, allResources,configService,session).toString();
		}else{
			/**
			 * This is objects like Classloaders which cannot be matched, we will process 1st available object
			 */
			if (resource.getName().equalsIgnoreCase("Classloader")){
				attributeValueOfResourceObject = "null";
			}else{
				if (!resourceMetaData.getContainmentPath().equalsIgnoreCase("null")){
					attributeValueOfResourceObject =  resource.getAttributeList().get(resourceMetaData.getContainmentPath()).toString();
				}
			}
		}
		
		//		logger.trace(" got the attributeList Array for: " + resource.getName() + " size is: " + arrayOfAttrList.size());
		
		if ( arrayOfAttrList.size() >0 ){
			
			// modifyArrayPropertyAttribute(arrayOfAttrList,resource,resourceMetaData);
			
			Iterator arrayOfAttrIterator = arrayOfAttrList.iterator();
			logger.trace("Loop through Iterator size:" + arrayOfAttrList.size());
			boolean matchFound = false;			
			
			while (arrayOfAttrIterator.hasNext()){
				AttributeList attrList ;
				if (resource.getName().equalsIgnoreCase("Classloader")){
					attrList = (AttributeList)arrayOfAttrIterator.next();
					matchFound = true;
					resource.setConfigId(ConfigServiceHelper.createObjectName(attrList));
					logger.trace("Match found:" + resourceMetaData.getContainmentPath().toString() + " to value:" + attributeValueOfResourceObject);

				}else{
					attrList = (AttributeList)arrayOfAttrIterator.next();
					logger.trace("Getting attribute:" + resourceMetaData.getContainmentPath()+ " for resource " + ConfigServiceHelper.createObjectName(attrList) + "resourceMetaData.getType()" + resourceMetaData.getType());
					
					if ((((ObjectName)ConfigServiceHelper.createObjectName(attrList)).getKeyPropertyList().get("_Websphere_Config_Data_Type").equalsIgnoreCase(resourceMetaData.getType()))){
						
						if (resourceMetaData.getContainmentPath().equalsIgnoreCase("Null")){
							matchFound = true;
							logger.trace("resourceMetaData.getContainmentPath():" + resourceMetaData.getContainmentPath() + " match found :" + ConfigServiceHelper.createObjectName(attrList));
							resource.setConfigId(ConfigServiceHelper.createObjectName(attrList));
							
						}else{
							String configObjectAttrValue= ConfigServiceHelper.getAttributeValue(attrList, resourceMetaData.getContainmentPath()).toString();
							logger.trace("Matching value of attribute:" + resourceMetaData.getContainmentPath() + " attributeValueOfResourceObject  :" + attributeValueOfResourceObject + " and configObjectAttrValue " + configObjectAttrValue);
							if (attributeValueOfResourceObject.equalsIgnoreCase(configObjectAttrValue)){
								matchFound = true;
								//modifyAttribueList();
								resource.setConfigId(ConfigServiceHelper.createObjectName(attrList));
								logger.trace("Match found:" + resourceMetaData.getContainmentPath().toString() + " to value:" + attributeValueOfResourceObject);
		
							}
						}
					}	
				}
			//	configService.setAttributes(session, resource.getConfigId(),attrList);
			}
			// comment 6
			if (!matchFound ){
				AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope,deployInfo);


				logger.trace(" Create new Attribute parent:" + resource.getParent().getConfigId() + " relation " + resourceMetaData.getAttributeName() + " type:" + resourceMetaData.getType() + " attributelist:" + newAttrList);
				System.out.println(" Create new Attribute parent:" + resource.getParent().getConfigId() + " relation " + resourceMetaData.getAttributeName() + " type:" + resourceMetaData.getType() + " attributelist:" + newAttrList);
				if (resource.getResourceMetaData().isShouldCreate()){
					ObjectName objectName = 
						configService.createConfigData(session, resource.getParent().getConfigId(),resourceMetaData.getAttributeName(), 
								resource.getName(), newAttrList);
					SDLog.log("		Created new Attribute " ) ;
					resource.setConfigId(objectName);
				}else{
					SDLog.log("		Will not create this new Attribute " + resource.getContainmentPath() ) ;
				}	
				ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
				ArrayList modifiedAttributes = resourceDiffReportHelper.getDiffAttributes(newAttrList);
				resource.setModifiedAttributes(modifiedAttributes );
				modifiedResources.add(resource);

			}else{
				SDLog.log("		Attribute exists" ) ;

				resourceCreatorHelper.modifyConfigObject(resource,referencedResources,deployInfo,configService,adminClient,session,scope,modifiedResources,allResources);
			}
			
		}else{
			
			AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope,deployInfo);
	
			logger.trace(" Create new Attribute parent:" + resource.getParent().getConfigId() + " relation " + resourceMetaData.getAttributeName() + " type:" + resourceMetaData.getType() + " attributelist:" + newAttrList);
			logger.trace("Checking if metadata allows for creation of " + resource.getName() + " with metadata of " + resource.getResourceMetaData().getType());

			if (resource.getResourceMetaData().isShouldCreate()){
				ObjectName objectName = 
				configService.createConfigData(session, resource.getParent().getConfigId(),resourceMetaData.getAttributeName(), 
						resource.getName(), newAttrList);
				resource.setConfigId(objectName);
				SDLog.log("		Created new Attribute " + objectName) ;
			}else{
				SDLog.log("		Will not create this new Attribute " + resource.getContainmentPath() ) ;
			}
			

			ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
			ArrayList modifiedAttributes = resourceDiffReportHelper.getDiffAttributes(newAttrList);
			resource.setModifiedAttributes(modifiedAttributes );
			modifiedResources.add(resource);

		}
		
	}


	
	/**
	 * 1: Get the ArrayList from the given config object (Contains AttributeList for BusName, QueueName)
	 * 2: For each array get AttributeList ([name:Name value:BusName type:String],[name:value value:SCA.APPLICATION type:String])
	 * 2a: If ArrayList is null then add all the attributes as new
	 * 3: Get resourceAttributes ([key:BusName value:SCA.APPLICATION] [key:QueueName value:MyQueue]) 
	 * 4: for each resource get key. (e.g. BusName)
	 * 4a: If name is null i.e. this property/attribute is not specified in resource.xml then ignore this object
	 * 5: get value of name from current AttributeList (e.g. BusName)
	 * 6: set this value to attributeListName 
	 * 
	 * @param resource
	 * @param resourceMetaData
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private void checkArrayCollectionAttribute(Resource resource,ResourceMetaData resourceMetaData, Resource referencedResources,DeployInfo deployInfo)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException{
		
//		SDLog.log( " MetaInfo " + configService.getAttributesMetaInfo(resource.getParent().getName()));
		logger.trace("Getting attribute: " + resourceMetaData.getAttributeName() + " for parent + " + resource.getParent().getName());
//		ArrayList arrayOfAttrList = (ArrayList)configService.getAttribute(session, resource.getParent().getConfigId(), resourceMetaData.getAttributeName());
//		logger.trace(" got the attributeList Array for: " + resource.getName() + " size is: " + arrayOfAttrList.size());
		
		if (!resourceCreatorHelper.checkIfConfigObjectExists(resource,referencedResources,deployInfo,scope,configService,session,allResources,true)){
//			String configObjectAttrName = ConfigServiceHelper.getAttributeValue(attrList, "name").toString();
			
			AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope,deployInfo);
	
			logger.trace(" Create new Attribute parent:" + resource.getParent().getConfigId() + " relation " + resourceMetaData.getAttributeName() + " type:" + resourceMetaData.getType() + " attributelist:" + newAttrList);
			logger.trace("Checking if metadata allows for creation of " + resource.getName() + " with metadata of " + resource.getResourceMetaData().getType());
			if (resource.getResourceMetaData().isShouldCreate()){
				ObjectName objectName = 
					configService.createConfigData(session, resource.getParent().getConfigId(),resourceMetaData.getAttributeName(), 
							resource.getName(), newAttrList);
				SDLog.log("		Created new Attribute " + objectName) ;
				resource.setConfigId(objectName);	
			}else{
				SDLog.log("		Will not create this new Attribute " + resource.getContainmentPath() 
						+ " as creation of " + resource.getResourceMetaData().getType() 
						+ "(" + resource.getResourceMetaData().getParentTree()  + ") is not supported" ) ;
			}
			ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
			ArrayList modifiedAttributes = resourceDiffReportHelper.getDiffAttributes(newAttrList);
			resource.setModifiedAttributes(modifiedAttributes );
			modifiedResources.add(resource);

		}else{

			logger.trace("		++++++++++++++++ Should modify " + resource.getName() ) ;
			logger.trace("		++++++++++++++++ Getting " + resourceMetaData.getAttributeName()  + " from " + resource.getParent().getName()) ;
			ArrayList arrayList = (ArrayList)configService.getAttribute(session, resource.getParent().getConfigId(), resourceMetaData.getAttributeName());
			resourceCreatorHelper.modifyArrayPropertyAttribute(arrayList,resource,referencedResources,deployInfo,configService,session,allResources,scope );
		}
		
		
		/**
		if (resourceAttributes.get("name")!=null){
			String resourceAttrName = resourceAttributes.get("name").toString();
			String resourceAttrValue = 	resourceAttributes.get("value").toString();
		
			AttributeList attrList = new AttributeList();
			if (arrayOfAttrList != null){ 
				for (int arrayAttrsCnt =0 ; arrayAttrsCnt < arrayOfAttrList.size(); arrayAttrsCnt++){
					
					attrList = (AttributeList)arrayOfAttrList.get(arrayAttrsCnt);
		
					String configObjectAttrName = ConfigServiceHelper.getAttributeValue(attrList, "name").toString();
					String configObjectAttrValue = null;
					if (ConfigServiceHelper.getAttributeValue(attrList, "value")!=null){
						configObjectAttrValue = ConfigServiceHelper.getAttributeValue(attrList, "value").toString();
					}
					
					
					
					if (resourceAttrName.equalsIgnoreCase(configObjectAttrName)){
						attrNameFound = true;
						logger.trace(" Matching Name found " + resourceAttrName + " and " + configObjectAttrName);
						
						if((configObjectAttrValue==null ) || configObjectAttrValue.equalsIgnoreCase(resourceAttrValue)){
							ConfigServiceHelper.setAttributeValue(attrList, "value", resourceAttrValue);	
							SDLog.log("		Set Attribute value " + configObjectAttrName + " to " + resourceAttrValue);
			
							logger.trace(" Attach new Attributes to " + ConfigServiceHelper.createObjectName(attrList));
							configService.setAttributes(session,  ConfigServiceHelper.createObjectName(attrList) , attrList);
						}else{
							SDLog.log("		Value of ConfigAttribute:" + configObjectAttrName + " is :" + configObjectAttrValue +" which is same as resource Attr Value:" + resourceAttrValue + " No change for this attribute");
						}
					}
				}
			}
			if(!attrNameFound){
				ConfigServiceHelper.setAttributeValue(attrList, "value", resourceAttrValue);
	
				SDLog.log("		Create new Attribute name:" + resourceAttrName + " value " + resourceAttrValue);
				attrList.add(new Attribute("name",resourceAttrName));	
	
				attrList.add(new Attribute("value",resourceAttrValue));	
	//			logger.trace(" Attach new Attributes to " + ConfigServiceHelper.createObjectName(attrList));
	//			configService.setAttributes(session,  ConfigServiceHelper.createObjectName(attrList) , attrList);
				logger.trace(" Create new Attribute parent:" + resource.getParent().getConfigId() + " relation " + resource.getName() + " type:" + resourceMetaData.getConfigType() + " attributelist:" + attrList);
				ObjectName objectName = configService.createConfigData(session, resource.getParent().getConfigId(), resource.getName(), resourceMetaData.getConfigType(), attrList);
				SDLog.log("		Created new Attribute " + objectName) ;
				resource.setConfigId(objectName);			
			}
		}else{
			logger.trace(" As the attribute " + resource.getName() + " is not specified in resource.xml, ignore.");
		}
		**/
	}

	/**
	 * get resource type from Resource Object
	 * get current Resources's Parent's config id
	 * get current Resources's relation to its parent, from Resource MetaData
	 * get JMX Attribute List from Resources Attribute List
	 * 
	 * @param resource
	 * @param resourceMetaDataMap
	 * @return
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 */
	private ObjectName createConfigObject(Resource resource,Resource referencedResources,ObjectName scope,DeployInfo deployInfo)
		throws AttributeNotFoundException ,ConnectorException,ConfigServiceException,DeployException{
		// get the resource type
		// get attribute list
		// get parent config Id
		// get relation
		// loop the attributes HashMap and create AttributeList
		// call create config.
		String resourceType = resource.getName();

		ResourceMetaData resourceMetaData = resource.getResourceMetaData();
		ObjectName parenConfigId = null;
		if (resource.getParent()!=null){
			parenConfigId = resource.getParent().getConfigId();
		}
		String relation = resourceMetaData.getRelation();

		AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope,deployInfo);
		
		ObjectName newObjectName = null;
		SDLog.log(" Creating Object for parent: " + parenConfigId + " with relation: " + relation + " of type: " + resourceType );
		logger.trace(" Creating Object for parent: " + parenConfigId + " with relation: " + relation + " of type: " + resourceType );
		// SDLog.log( " +++ Relation " + configService.getRelationship(session,resource.getParent().getConfigId(),relation)[0].getCanonicalName() );
		if (resource.getAttributeList().get(ResourceConstants.TEMPLATE) != null){
			String templateName = resource.getAttributeList().get(ResourceConstants.TEMPLATE).toString();
			logger.trace("Looking up templates " + templateName );
			ObjectName[] templateObjectNames = configService.queryTemplates(session, templateName);
			if (templateObjectNames.length > 1){
				logger.warn(" More the 1 templates found for the template Name:" + templateName + " count:" + templateObjectNames.length);
				if (templateObjectNames.length > 0){
					newObjectName = configService.createConfigDataByTemplate(session, parenConfigId, relation,newAttrList,templateObjectNames[0]);
				}
			}
				
		}else{
			try{
				ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper (); 
				resource.setModifiedAttributes(resourceDiffReportHelper.getDiffAttributes(newAttrList));
				modifiedResources.add(resource);
				newObjectName = configService.createConfigData(session, parenConfigId, relation, resourceType, newAttrList);

			}catch(ConfigServiceException e){
				SDLog.log(e.getMessage());
				if (e.getMessage().startsWith("ADMG0037E")){
				//
					return newObjectName;
				}else{
					throw e;
				}

			}
		}
		// variables for difference report generation 	

		return newObjectName;
	}

	/**
	 * 
	 * @param resource
	 * @param referencedResources
	 * @param scope
	 * @param deployInfo
	 * @return
	 * @throws CommandMgrInitException
	 * @throws CommandException
	 * @throws CommandNotFoundException
	 * @throws ConnectorException
	 * @throws AttributeNotFoundException
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws DeployException
	 */
	private ObjectName createConfigObjectUsingCommand(Resource resource,Resource referencedResources,ObjectName scope,DeployInfo deployInfo)
		throws CommandMgrInitException,CommandException,CommandNotFoundException,ConnectorException,
			AttributeNotFoundException ,ConnectorException,ConfigServiceException,DeployException{
		// get the resource type
		// get attribute list
		// get parent config Id
		// get relation
		// loop the attributes HashMap and create AttributeList
		// call create config.
		String resourceType = resource.getName();
	
		ResourceMetaData resourceMetaData = resource.getResourceMetaData();

		AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope,deployInfo);
		CommandManager commandManager = new CommandManager();
		ObjectName newObjectName = null;
		
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper (); 
		resource.setModifiedAttributes(resourceDiffReportHelper.getDiffAttributes(newAttrList));
		modifiedResources.add(resource);
		newObjectName = commandManager.createResource(resource,adminClient, session);
	
		// variables for difference report generation 	
	
		return newObjectName;
	}


	
/**	private void showConfigObjectUsingCommand(Resource resource,Resource referencedResources,ObjectName scope,DeployInfo deployInfo)
		throws CommandMgrInitException,CommandException,CommandNotFoundException,ConnectorException,
			AttributeNotFoundException ,ConnectorException,ConfigServiceException,DeployException{
		// get the resource type
		// get attribute list
		// get parent config Id
		// get relation
		// loop the attributes HashMap and create AttributeList
		// call create config.
		String resourceType = resource.getName();
	
		ResourceMetaData resourceMetaData = resource.getResourceMetaData();
	
		AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope,deployInfo);
		CommandManager commandManager = new CommandManager();
		
			ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper (); 
			resource.setModifiedAttributes(resourceDiffReportHelper.getDiffAttributes(newAttrList));
			modifiedResources.add(resource);
			commandManager.showResource(resource,adminClient, session);
	
		// variables for difference report generation 	
	
	}
**/
	
	/**
	 * get MetaInfo of Attribute
	 * 
	 * @param resource
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private AttributeList getConfigAttributeList(Resource resource,Resource referencedResources,ResourceMetaData resourceMetaData,ObjectName scope,DeployInfo deployInfo)
		throws AttributeNotFoundException, ConfigServiceException,ConnectorException,DeployException{
		// create an attribute list
		
		logger.trace("	Getting attribute from resource.xml for " + resource.getName());
		AttributeList newAttrList = new AttributeList();
		HashMap resourceAttributesMap = resource.getAttributeList();
		Iterator resourceAttributeKeyIterator =  resourceAttributesMap.keySet().iterator();
		AttributeList metaInfo; 
		logger.trace("	Getting Attributes MetaInfo for " + resource.getName());
		metaInfo =  configService.getAttributesMetaInfo(resource.getName());
		logger.trace("	Got MetaInfo " + resource.getName());
		
		
		while (resourceAttributeKeyIterator.hasNext()){
			String key = resourceAttributeKeyIterator.next().toString();
			if (key.equalsIgnoreCase(ResourceConstants.ATTRUBUTENAME)) {
				logger.warn( "		Attribute " + key  + " is on ognore list Ignoring attribute : " + key );
			}else if (key.equalsIgnoreCase(ResourceConstants.TEMPLATE)){
				logger.warn( "		Attribute "  + key  + " is on ognore list Ignoring attribute : " + key );
			}else{
				boolean isReference = ResourceHelper.isAttributeReference(metaInfo,key);
				logger.trace( "		Attribute is: " + key + " isReference is: "  + isReference);

				LinkAttribute linkAttribute = ResourceHelper.getLinkAttribute(resource,key);
				logger.trace( "		Attribute is: " + key + " linkAttribute is: "  + linkAttribute);

				boolean isCollection = ResourceHelper.isCollection(metaInfo,key);
				logger.trace( "		Attribute is: " + key + " isCollection is: "  + isCollection);
				
				String type = ResourceHelper.getAttributeType(metaInfo,key);
				logger.trace( "		Attribute is: " + key + " Type is: "  + type );
				Object value = resourceAttributesMap.get(key).toString();
				
				
				if (linkAttribute != null){
					value = resourceCreatorHelper.getLinkAttributeForMatchAttributeValue(resource,linkAttribute,key,referencedResources,scope,deployInfo,allResources,configService,session);
				}else if (isReference){
					logger.trace( "		As is reference : " + key + " getting matching reference " );
					String variableName = PropertyHelper.getVariableName(value.toString());
					value  = getReferencedConfigObjectName(variableName, scope, referencedResources);
					logger.trace( "		As is reference : " + key + " matching reference is " + value);
					
				}else if (type.equalsIgnoreCase("long")){
					value  = new Long (value.toString());
				}else if (type.equalsIgnoreCase("boolean")){
					value  = new Boolean (value.toString());

				}else if (type.equalsIgnoreCase("int")){
					value  = new Integer (value.toString());
				}else if (isReference){
					value  = (ObjectName)value;
				}else if ((new Boolean(isCollection)).booleanValue() && (type.equalsIgnoreCase("String"))){
					ArrayList classpath = PropertyHelper.getArrayFromCommaSeperated(value.toString());
					
					// String[] classpath = {resourceAttributeValue.toString()};
					
					//String[] classpath = {value.toString(), value.toString()};
					value  = classpath ;

				}else{
					logger.trace(" Will assign value as String:" + key);
					value  = value.toString();
				}
	
				newAttrList.add(new Attribute(key, value ));
				logger.trace( "Adding Attribute " + key + " value " + value);
			}
		}

		return newAttrList;
	}
	
	private AttributeList getAttributeList(HashMap attrMap){
		logger.trace("Start processing attributes from data to create AttributeList");
		AttributeList attrList = new AttributeList();
		String[] keys = (String [])attrMap.keySet().toArray(new String [0]);
		logger.trace("Count of attributes data has is " + keys.length );
		for (int i = 0; i< keys.length; i++){
			String attributeName = keys[i];
			String attributeValue = attrMap.get(attributeName).toString();
			logger.trace(i + ": Value for " + attributeName + " is " + attributeValue );
			attrList.add(new Attribute(attributeName,attributeValue));
		}
		
		logger.trace("Completed processing attributes from data to create AttributeList");
		return attrList;
	}
	



	private void printResources(Resource resource)
	throws DeployException{
		if (resource.getChildren().size()>0){
			Resource[] children = (Resource[]) resource.getChildren().toArray(new Resource[0]);
			for (int childCnt=0;childCnt < children.length;childCnt++){
				String type= children[childCnt].getName();
				SDLog.log(" Type of the object is " + children[childCnt].getName());
				SDLog.log(" Containment Path is " + children[childCnt].getContainmentPath());
				HashMap map = children[childCnt].getAttributeList();
				if (children[childCnt].getAttributeList().size() > 0){
					Iterator keySet = map.keySet().iterator() ;
					while (keySet.hasNext()){
						String key = keySet.next().toString();
						SDLog.log(" 	Attribute Name is " +  key);
						SDLog.log(" 	Attribute Value is " +  map.get( key));
					}
				}
				
				
			}
			
		}
	}

	// Key is the referencedObjectVariable, value is 
	public HashMap parseReferencedObjectXML(){
		HashMap referencedObjectsMap = new HashMap();
		return referencedObjectsMap;
	}
	//While processing attributes, if attribute value starts with ${, then getConfigObject(resource,resourceMetaData,Scope,variableName)

	//look up the definition of the object in ReferencedConfigObject xml
	//Call ConfigService.resolve(type, scope) where type is the name of the xml element and scope is the cluster under which object is definied
	//Loop through this list and attributes name and values pairs and for the matching one return.
	
	public ObjectName getReferencedConfigObjectName(String referencedObjectVariable, ObjectName scope, Resource referencedResources )
		throws ConnectorException,ConfigServiceException{
		
		
		if (referencedResources.getChildren()!=null){
			// get root reference XML element's children
			Iterator rootResourceReference = ((Resource)referencedResources.getChildren().get(0)).getChildren().iterator();
			while (rootResourceReference.hasNext()){
				// check if name of current child is same as the object that we are looking for e.g. __ConnectionDefinition_JMS_ConnectionFactory
				Resource childRefVariableResource = (Resource)rootResourceReference.next();
				logger.trace("Matching resource " +  childRefVariableResource.getName() + " to " + referencedObjectVariable);

				if (childRefVariableResource.getName().equalsIgnoreCase(referencedObjectVariable)){
					Resource childRefResource = ((Resource)(childRefVariableResource.getChildren().get(0)));
					String configObjectName = ((Resource)(childRefVariableResource.getChildren().get(0))).getName();
						
					ObjectName[]  objectNames = configService.resolve(session,  scope,configObjectName);	
					logger.trace(" Matched for the type:" + configObjectName + " in scope:" + scope + " are " + objectNames.length  );
					
					for (int resolvedNamesCnt=0;resolvedNamesCnt< objectNames.length ;resolvedNamesCnt++){
						
						boolean matchingObject = true;
						logger.trace("Getting Attribute List from Java Object ");
						HashMap attributes = childRefResource.getAttributeList();
						Iterator attributeKeys = attributes.keySet().iterator();
						if (attributes.size()>0){
							while(attributeKeys.hasNext() && matchingObject){
								String key = attributeKeys.next().toString();
								logger.trace("Getting Attribute " + key + " from Config Object ");
								String configAttrValue = configService.getAttribute(session, objectNames[resolvedNamesCnt],key).toString();
								logger.trace("Checking if " + configAttrValue + " equals " + (attributes.get(key).toString()));
								if (matchingObject && (configAttrValue.equalsIgnoreCase(attributes.get(key).toString()))){
									matchingObject = true;
								}else{
									matchingObject = false;
								}
							}
							if (matchingObject){
								SDLog.log(" Match for the referenced found " + objectNames[resolvedNamesCnt].getCanonicalName());
								return objectNames[resolvedNamesCnt];
							}else{
								SDLog.log(" No Match for the referenced found " );
							}
						}else{
							SDLog.log(" No Match for the referenced found " );
						}
					}
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		try{

			if ((args.length) < 1){
				SDLog.log(" Please supply folder where resources.xml is located");
			}
			
			String folderLocation = args[0];
			File folder = new File (folderLocation);
			if (!folder.exists() ){
				throw new DeployException(new Exception("Folder " + folder.getAbsolutePath() + " does not exists"));
			}
			if (!folder.isDirectory() ){
				throw new DeployException(new Exception("Folder " + folder.getAbsolutePath() + " should be a folder"));
			}

			ResourceCreator resourceCreator  = new ResourceCreator();
			
			String resourceXML = folder.getAbsolutePath() + File.separatorChar + "resources.xml";
			String rulesXML = folder.getAbsolutePath() + File.separatorChar + "rules.xml";
			
			InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
			InputStream referencedResourceXMLInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
			
			DeployInfo deployInfo = new DeployInfo();
			deployInfo.setHost("Avatar2");	
			deployInfo.setPort("9810");
			deployInfo.setUserName("jatin");
			deployInfo.setPassword("jatin");
			deployInfo.setCell("Avatar2Cell01");
			System.out.println(System.getProperty("ConnectionType"));
			deployInfo.setConnectionType("RMI"); 
			
			resourceCreator.start(resourceXML,rulesXML ,referencedResourceXMLInputStream,resourceXMLMetaDataInputStream,deployInfo);

			if ((args.length) > 1){
				String arg = args[0];
				if (arg.equalsIgnoreCase("reportOnlyMode")){
					resourceCreator.reportOnlyMode = true;
				} 
			}
		//	resourceCreator.parseResourcesXML(referencedResourceXML,false);
		//	resourceCreator.parseResourcesXML(resourceXML,true);
			
		}catch(DeployException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will return the child resources that can be added to resource. 
	 * This is used from UI to allow user to select a resource from drop down to add. 
	 * For e.g. When a user selects Cell and clicks Add child. Drop down will give option of 
	 * ServerCluster, JDBCProvider etc. 
	 * @param resourceXML
	 * @param referencedResourceXMLInputStream
	 * @param resourceXMLMetaDataInputStream
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 */
	public Vector<Resource> startGettingValidChildren(String resourceXML,InputStream referencedResourceXMLInputStream,
			InputStream resourceXMLMetaDataInputStream,DeployInfo deployInfo) 
		throws DeployException,ConfigServiceException,ConnectorException{
		ResourceXMLParser resourceXMLParser = new ResourceXMLParser(); 
		Element rootNode = resourceXMLParser.getResourcesXMLElements(resourceXML,false);
		return startGettingValidChildren(rootNode,referencedResourceXMLInputStream,resourceXMLMetaDataInputStream,deployInfo);
	}
	
	/**
	 * Note that the return attribute is used only for 
	 * UI. So that GWT UI can display the output
	 * 
	 * @param rootElement
	 * @param referencedResourceXMLInputStream
	 * @param resourceXMLMetaDataInputStream
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 */
	public Vector<Resource> startGettingValidChildren(Element rootElement,InputStream referencedResourceXMLInputStream,
			InputStream resourceXMLMetaDataInputStream,DeployInfo deployInfo) 
		throws DeployException,ConfigServiceException,ConnectorException{
		
		/**
		 * Initialise the variables in ResourceConstants from 
		 * Constants properties file
		 */
		ResourceHelper.initialiseProperites();

		mDeployInfo = deployInfo;
		try{

			createAdminClient(deployInfo);
		
			ResourceXMLParser resourceXMLParser = new ResourceXMLParser();
			
			Resource resources = resourceXMLParser.getResourcesFromXML(rootElement,null,resourceXMLMetaDataInputStream,false,deployInfo,configService);
			Resource referencedResources = resourceXMLParser.getReferenceResources(referencedResourceXMLInputStream,resourceXMLMetaDataInputStream,false,deployInfo);
			allResources = resources;
			return getValidChildren(resources,referencedResources,deployInfo);
			
		}catch(ConfigServiceException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(AdminException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(IOException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}catch(JDOMException e ){
			logger.error(e.toString());
			e.printStackTrace();
			throw new DeployException(e);
		}
	}

	private Vector<Resource> getValidChildren (Resource resource, Resource referencedResources,DeployInfo deployInfo)
		throws ConfigServiceException,ConnectorException,DeployException{

		Resource[] children = (Resource[]) resource.getChildren().toArray(new Resource[0]);
		Resource currentResource = resource; 
		while (currentResource.getChildren()!=null){
			currentResource = (Resource)currentResource.getChildren().get(0);  
		}	
		System.out.println( currentResource.getName());
		
		

		Vector<ResourceMetaData> metaDataChildren = currentResource.getResourceMetaData().getChildren();
		
		for (int i = 0 ,  j = metaDataChildren.size(); i < j ; i++){
			System.out.println(" Child : " + metaDataChildren.get(i).getType());
			ResourceHelper resourceHelper = new ResourceHelper();
			Resource validChildResource =  resourceHelper.createSkeletonResource(metaDataChildren.get(i).getType(), currentResource, metaDataChildren.get(i));
			try{
			AttributeList metaInfo =  configService.getAttributesMetaInfo(validChildResource.getName());
			HashMap map = ResourceHelper.getResourceAttributeMetaData(metaInfo);
			validChildResource.setResourceAttrMetaInfo(map);
			}catch(ConfigServiceException e){
				if (e.getMessage().startsWith("ADMG0007E")){
					logger.warn("Will ignore type " + validChildResource.getName() + " because " + e.getMessage());
				} 
				
			}
		}
		
		return null;
	}

	
}
