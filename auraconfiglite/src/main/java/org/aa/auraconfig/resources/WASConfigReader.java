/**	   Copyright [2009] [www.apartech.com]


 **/
package org.aa.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.customcode.CustomCodeManager;
import org.aa.auraconfig.resources.metadata.CommandAttribute;
import org.aa.auraconfig.resources.metadata.CommandLinkAttribute;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.aa.auraconfig.resources.metadata.ResourceMetaDataHelper;
import org.aa.auraconfig.resources.parser.ResourceParserHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertiesConstant;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.cmdframework.CommandException;
import com.ibm.websphere.management.cmdframework.CommandMgrInitException;
import com.ibm.websphere.management.cmdframework.CommandNotFoundException;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;


public class WASConfigReader {
	private static final Log logger = LogFactory.getLog(WASConfigReader.class);
	private int level;
	private ObjectName scope;
	int count;

	WASConfigReaderHelper wasConfigReaderHelper = new WASConfigReaderHelper();

	/**
	 * 1: check if there are children for the resource object 2: For each child
	 * resource object, if isArray is not true Check if Config Object Exists
	 * Create if does not exists 4: If isArray is true, get the attribute from
	 * parent Config Object Match the name value Modify if different 5: If the
	 * current type being searched is ServerCluster then set scope variable
	 * 
	 * @param resource
	 * @param resourceMetaDataMap
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public void checkInComingChanges(Session session,
			ConfigService configService,AdminClient adminClient, Resource resource,
			Resource referencedResources, Resource allResource,DeployInfo deployInfo)
			throws ConfigServiceException, ConnectorException,
			AttributeNotFoundException, DeployException,MalformedObjectNameException,Exception {
		
		level++;
		//setting memeber variable so that it can be used, Not a neat solution 
		// as configService and session is passed to each method instead of setting it as member.
		Resource[] children = new Resource[0];
		
		if (resource.getChildren() != null) {

				children = (Resource[]) resource.getChildren().toArray(new Resource[0]);
		} 

		String type = null;

		for (int childCnt = 0; childCnt < children.length; childCnt++) {
			 
			if (type != children[childCnt].getName()) {
				type = children[childCnt].getName();	
				logger.trace(" >>>>>>>>>> Processing resource type " + type + " " + children.length);
				
				// set dummy
				resource.setDummy( ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData()));
				
				if (!resource.isDummy()){
				
					ResourceMetaData resourceMetaData = children[childCnt].getResourceMetaData();
					if (resourceMetaData!=null){
						
					
						if (resourceMetaData.isApplicationManaged()){
							ApplicationReadManager applicationReadManager = new ApplicationReadManager(session,configService,adminClient,
									children[childCnt],referencedResources,resource,deployInfo);
							applicationReadManager.processApplication();
							
						} else if (resourceMetaData.getIsProperty() && !resourceMetaData.isArray()) {
							logger.debug("               TODO Sync of resource which is property." );
							logger.trace(" <<<<<<<<<< Processing resource type " + type );
							// processProperty(children[childCnt],resourceMetaData,referencedResources);
			
						} else if (resourceMetaData.getIsProperty()	&& resourceMetaData.isArray()) {
							logger.debug("               TODO Sync of resource which is property." );
							//getMissingConfigObjectForType(session, configService, children[childCnt],	referencedResources);
							logger.trace(" <<<<<<<<<< Processing resource type " + type );
			
						} else if (resourceMetaData.isArray()) {
							
							getMissingConfigObjectForType(session, configService, children[childCnt], referencedResources,deployInfo);
							
							
							//getMissingConfigObjectForArray(session, configService, children[childCnt],	referencedResources);
						//	getMissingConfigObjectForType(session, configService, children[childCnt],	referencedResources, allResource);
							logger.trace(" <<<<<<<<<< Processing resource type " + type );
			
						} else {
							// check if the current object exists if not create it
							SDLog.log("["
									+ children[childCnt].getName()
									+ " | "
									+ ResourceHelper
											.getResourceIdentifierName(children[childCnt])
									+ "]", level);
							
							logger.trace("Checking missing resources in AURA resources for type " + type);
							logger.trace("		" + type + "  typeOnExcludeList " +  typeOnExcludeList(type));
							//if (!typeOnExcludeList(type))
							Vector incomingResource = getMissingConfigObjectForType(session, configService, children[childCnt],	referencedResources,deployInfo);
							
						
							
							logger.trace(" <<<<<<<<<< Processing resource type " + type );
							for (int inComingCnt = 0; inComingCnt < incomingResource.size(); inComingCnt++){
								logger.trace(" >>>>>>>>>> Processing nested incoming resource type " + type );
								checkInComingChanges(session, configService,adminClient, (Resource)incomingResource.get(inComingCnt), referencedResources, allResource, deployInfo);
								logger.trace(" <<<<<<<<<< Processing nested incoming resource type " + type );
							}
							// children[childCnt].setConfigId(newObjectConfigId);
			
						}
					}
				}else{
					logger.trace("Parent is dummy hence will not process this " + resource.getContainmentPath());
				}
			}
			/** else{
				SDLog.log( children[childCnt].getName() + " not supported");
			} **/

			// If type ServerCluster then set scope.
			if (type.equalsIgnoreCase("ServerCluster")) {
				if (children[childCnt].getConfigId() != null) {
					scope = children[childCnt].getConfigId();
					
				}
			} else if (type.equalsIgnoreCase("Node")) {

				if (children[childCnt].getConfigId() != null) {
					scope = children[childCnt].getConfigId();
				}
			} else if (type.equalsIgnoreCase("Server")) {
				if (children[childCnt].getConfigId() != null) {
					scope = children[childCnt].getConfigId();
				}

			} else if (type.equalsIgnoreCase("Cell")) {
				if (children[childCnt].getConfigId() != null) {
					scope = children[childCnt].getConfigId();
				}
			}
			// after creating the parent check if child exists
			/**
			 * Let check if there is any incoming children and if so  check if any child is missing for those incoming parents  
			if (doIncoming && children[childCnt].getChildren()!=null){
				doCheckIncomingChildren(session,configService,children[childCnt].getParent().getInComingChildren(),referencedResources,allResource,deployInfo,false);
			}else if ((children[childCnt].getParent().getInComingChildren()!=null) && (!doIncoming)){
				SDLog.log("Checking missing resources in AURA resources for Incoming type " + type + children[childCnt].getParent().getInComingChildren().size());
				doCheckIncomingChildren(session,configService,children[childCnt].getParent().getInComingChildren(),referencedResources,allResource,deployInfo,true);
			}else{
				SDLog.log("No Incoming type " + type + children[childCnt].getContainmentPath()  + " " + doIncoming);
			} 

			 */

			SDLog.log("");

			checkInComingChanges(session, configService, adminClient,
					children[childCnt], referencedResources, allResource,deployInfo);
			level--;
			
		}
		
	}



	/**
	 * Missing resources of the type are added to Incoming list 
	 * and
	 * we return the list as well so that further incoming  resources for parent incoming
	 * can be checked.
	 *   
	 * @param session
	 * @param configService
	 * @param resource
	 * @param referencedResources
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws AttributeNotFoundException
	 * @throws MalformedObjectNameException
	 */	
	private Vector getMissingConfigObjectForType(Session session,ConfigService configService, 
			Resource resource,Resource referencedResources,DeployInfo deployInfo)
			throws DeployException, ConnectorException, ConfigServiceException,AttributeNotFoundException,MalformedObjectNameException {
		
		ResourceFinder resourceFinder = new ResourceFinder();
		/** if (ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData() )){
			resource.setContainmentPath(resource.getContainmentPath().substring(0, resource.getContainmentPath().length()-1));
			resource.setContainmentPath(resource.getContainmentPath()+":");
		} **/
		/*
		 * Get all resources for this config type in the currect scope
		 */
		ObjectName[] configIDs = resourceFinder.findAllResourcesInConfig(session, configService, resource, scope, true) ;
		Vector<Resource> incomingResources = new Vector<Resource>();
		/**
		 * If a different attribute name is used to match instead of containment attribute, like in case of Datasource jndi name is used instead
		 * of name of the datasource. This is then passed to method that checks if the resources got from the WAS config already exists in 
		 * XML file.
		 */
		String attributeNameForDuplicateObjectTypeChild = null;
		if (resource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME)!=null){
			attributeNameForDuplicateObjectTypeChild  = resource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString();
		} 
		logger.trace("Got " + configIDs.length + " for resources type " + resource.getName());

		//Loop through each config object and check the object is in AURA xml, 
		// if not create an entry in InComing list
		if (configIDs.length > 0) {
			count = 0;
			logger.trace("Got " + configIDs.length + " for resources type " + resource.getName());
			
			
		//	if (!resource.getResourceMetaData().getContainmentPath().toString().equalsIgnoreCase("null")){
				for (int configObjectCnt = 0; configObjectCnt < configIDs.length; configObjectCnt++) {
					/**
					 * This is used for the prematch that is required for types like Queue Connection factories, Queue etc
					 */	
					boolean syncPreMatch  = resourceFinder.doesConfigObjectPreMatch(session, configService, resource, configIDs[configObjectCnt], referencedResources);
					
					if (syncPreMatch && 
							(getMatchingResourceForConfigObject(configService, session, resource, configIDs[configObjectCnt], configObjectCnt,referencedResources,attributeNameForDuplicateObjectTypeChild,deployInfo ) == null)){
					//if ((getMatchingResourceForConfigObject(configService, session, resource, configIDs[configObjectCnt], configObjectCnt) == null)){
						/**
						 * If type like servercluster is a dummy then get all server cluster, used in UI to populate the tree.
						 * else if servercluster is used as a scope for e.g. to look for JDBCProvider then exclude server cluster
						 */
						resource.setDummy( ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData()));
						
						if ((!typeOnExcludeList(resource.getName())) || ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData()) || ResourceHelper.isResourceDummyInSource(resource,deployInfo)){
						
							Resource newResource = wasConfigReaderHelper.createNewResource(session, configService, resource, configIDs[configObjectCnt],referencedResources,deployInfo,"",count);
							resource.getParent().addDifferentChildCount();
							resource.getParent().addInComingChild(newResource);
							incomingResources.add(newResource);
							if (resource.getResourceMetaData().isSyncFindModeContainmentPath()){
								SDLog.log("Incoming ConfigObjects for the containment path " + resource.getContainmentPath()); 
							}else if (scope == null){
								SDLog.log("Incoming ConfigObjects for the type " + resource.getName()+ " name " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) ); 
							}else{
								SDLog.log("Incoming ConfigObjects for the type " + resource.getName() + " in scope " + configService.getAttribute(session, scope,"name") + " name " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) ); 
							}
						}else{
							logger.trace(resource.getName() + " is on exclude list.");
						}
					}
					

					if (resource.getResourceMetaData().isSyncFindModeContainmentPath()){
						logger.trace("<<< Processing complete for ConfigObjects for the containment path " + resource.getContainmentPath() +  " ] " ); 
					}else if (scope == null){
						logger.trace("<<< Processing complete for ConfigObjects for the type " + resource.getName()+ " to find " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) ); 
					}else{
						logger.trace("<<< Processing complete for ConfigObjects for the type " + resource.getName() + " in scope " + configService.getAttribute(session, scope,"name") + " to find " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) ); 
					}

				}
	
				
		/**	}else{
				logger.trace("Containment path is null so check if type exists " + resource.getName());
				if (configIDs.length ==1){
					checkIfAnyAttributeIsIncoming (session,configService,resource,configIDs[0],referencedResources,deployInfo);
					logger.info("		Match found "); 
				}else if (configIDs.length == 0){
					logger.info("		Object does not exists");
				}else {
					SDLog.log("		WARNING: Mulitple matches found, count is " + configIDs.length );
				}
			} **/
		}
		
		logger.trace("Checking if resource is dummy " + resource.getContainmentPath());
		if (ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData() )){
			logger.trace("resource is dummy " + resource.getContainmentPath());
			if (resource.getParent().getInComingChildren()!=null){
				logger.trace("resource.getParent().getInComingChildren().size() "+ resource.getParent().getInComingChildren().size() + " " + incomingResources.size()); 
				Iterator incomingChildren = incomingResources.iterator();
				
				while (incomingChildren.hasNext() ){
					Resource incomingChild = (Resource)incomingChildren.next();
					logger.trace("IncomingChild child" + incomingChild.getName());
					logger.trace("Matching incomingChild.getName()" + incomingChild.getName() + " to resource.getName() " + resource.getName());
					if (incomingChild.getName().equalsIgnoreCase(resource.getName())){
						logger.trace("Setting children for " + incomingChild.getContainmentPath());
						if (resource.getChildren()!=null){
							Iterator dummyChildren = resource.getChildren().iterator();
							while (dummyChildren.hasNext()){
								
								Resource child = (Resource)dummyChildren.next();
								Resource newchild = (Resource)child.clone();
								logger.trace("Adding child containment " + child.getParent().getContainmentPath() + ResourceHelper.getResourceIdentifierName(incomingChild)+ ":"  + child.getName() );

								if (!child.getParent().getContainmentPath().trim().endsWith("=")){
									newchild.setContainmentPath(child.getParent().getContainmentPath()+ "="+ResourceHelper.getResourceIdentifierName(incomingChild) + ":" + child.getName());
									
								}else{
									newchild.setContainmentPath(child.getParent().getContainmentPath()+ ResourceHelper.getResourceIdentifierName(incomingChild) + ":" + child.getName());			
								}
								
								newchild.setParent(incomingChild);
								//child.setContainmentPath(containmentPath)
								incomingChild.addChild(newchild );
							}
						}
						
					}
				}
				
			}
		}else{
			logger.trace("resource is not dummy " + resource.getContainmentPath());
		} 
		
		
		
		//SDLog.log( " << " + resource.getName());
		if (resource.getParent().getInComingChildren()!=null)
			logger.trace( resource.getName() + " size is " + resource.getParent().getInComingChildren().size());
		
		return incomingResources;
	}

	/**
	 * Method to get the matching peer resource for the given config Type.
	 * 
	 * This method will try and match the peer resources for the config object using 
	 * 1: Match Attribute
	 * 2: Containment path 
	 * 3: __attribute Name
	 * 
	 * @param configService
	 * @param session
	 * @param resource
	 * @param configObject
	 * @param configObjectCnt
	 * @param referencedResources
	 * @param attributeName
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws AttributeNotFoundException
	 * @throws DeployException
	 */
	private Resource getMatchingResourceForConfigObject(ConfigService configService,Session session, Resource resource,ObjectName configObject,
			int configObjectCnt,Resource referencedResources,String attributeName,DeployInfo deployInfo )
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{
		
		Vector combinedListOfResource = new Vector();
		
		//List resourceChildren = resource.getChildren();
		//configObject
		if (resource.getParent().getChildren()!=null) 
			combinedListOfResource.addAll(resource.getParent().getChildren());
		
		if (resource.getParent().getInComingChildren()!=null)
			combinedListOfResource.addAll(resource.getParent().getInComingChildren());
		
		return getMatchingResourceFromThisListOfResourceForTheConfigObject(configService,session, resource,configObject,
				configObjectCnt,referencedResources,attributeName,combinedListOfResource,deployInfo );
	}

	/**
	 * Method to get the matching peer resource for the given config Type.
	 * 
	 * This method will try and match the children resources for the config object using 
	 * 1: Match Attribute
	 * 2: Containment path 
	 * 3: __attribute Name
	 * 
	 * @param configService
	 * @param session
	 * @param resource
	 * @param configObject
	 * @param configObjectCnt
	 * @param referencedResources
	 * @param attributeName
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws AttributeNotFoundException
	 * @throws DeployException
	 */

	private Resource getMatchingResourceFromThisListOfResourceForTheConfigObject(ConfigService configService,Session session, Resource resource,ObjectName configObject,
			int configObjectCnt,Resource referencedResources,String attributeName,List resourceChildren,DeployInfo deployInfo)
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{
		
		if (!ResourceHelper.isResourceDummy(resource, resource.getResourceMetaData())){
			//logger.trace ("Getting attribute " + resource.getResourceMetaData().getContainmentPath().toString() + " from config object " + configObject. );
			String matchAttribute = resource.getResourceMetaData().getContainmentPath().toString();
			
			if (resource.getResourceMetaData().getMatchAttribute()!=null){
				matchAttribute = resource.getResourceMetaData().getMatchAttribute();
			}
			
			String attributeValueOfConfigObject = "null";
			
			if (configService.getAttribute(session,configObject,matchAttribute)!=null){
				attributeValueOfConfigObject = configService.getAttribute(session,configObject,matchAttribute).toString();
			}
			
			LinkAttribute  linkAttribute = ResourceHelper.getLinkAttribute(resource, matchAttribute); 
	
			if (linkAttribute != null){
				attributeValueOfConfigObject = wasConfigReaderHelper.getLinkAttributeValue (session, configService, linkAttribute, attributeValueOfConfigObject).toString() ;
			}
	
			// SDLog.log("		Checking if resource " + attributeValueOfConfigObject + " exists "); 
	
			for (int i = 0; i < resourceChildren.size(); i++) {
				
				Resource childResource = (Resource) resourceChildren.get(i);
				if (!ResourceHelper.isResourceDummy(childResource, childResource.getResourceMetaData())){
					if (childResource.getName().equalsIgnoreCase(resource.getName())){
						// Added this logic as sometimes there is case like nodeagent that has threadpool without names.
						if (childResource.getAttributeList().get(matchAttribute)!=null){
							String attributeValueOfMatchAttributeInResource = childResource.getAttributeList().get(matchAttribute).toString();
							logger.trace( "(" + (configObjectCnt +1) +  ")	Matching Resource in AURA " + attributeValueOfMatchAttributeInResource + " to " + attributeValueOfConfigObject ); 
							
							if (attributeValueOfConfigObject.equalsIgnoreCase(attributeValueOfMatchAttributeInResource)) {
								count++;
								logger.trace("	Match found for " + childResource.getContainmentPath()  + " using attribute " + matchAttribute );
								logger.trace(" TODO: check if any of the attributes have changed.");
								logger.debug("	Match found : TODO: check if any of the attributes have changed."); 
								if (childResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME)!=null){
									if (childResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString().equalsIgnoreCase(attributeName)){
										logger.trace("There is a child resource of type " + childResource.getName() + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
										i = resourceChildren.size();
										wasConfigReaderHelper.checkIfAnyAttributeIsIncoming (session,configService,childResource,configObject,referencedResources,deployInfo,count);
										return childResource;
			
									}
								}else{
									i = resourceChildren.size();
									wasConfigReaderHelper.checkIfAnyAttributeIsIncoming (session,configService,childResource,configObject,referencedResources,deployInfo,count);
									return childResource;
								}
							}
						}
					}
				}
			}
			logger.info("	Match Not found: " + resource.getName() + " identifier " + attributeValueOfConfigObject);
			// SDLog.log("	Match Not found: " + resource.getName() + " identifier " + attributeValueOfConfigObject);
		}
		return null;
	}
	
	

	private void getMissingConfigObjectForArray(Session session,ConfigService configService, Resource resource,
			Resource referenceResource)
		throws ConnectorException, ConfigServiceException {

/**		
		if (!checkIfConfigObjectExists(resource)){
//			String configObjectAttrName = ConfigServiceHelper.getAttributeValue(attrList, "name").toString();
			
			AttributeList newAttrList = getConfigAttributeList(resource,referencedResources,resourceMetaData,scope);
	
			logger.trace(" Create new Attribute parent:" + resource.getParent().getConfigId() + " relation " + resourceMetaData.getAttributeName() + " type:" + resourceMetaData.getType() + " attributelist:" + newAttrList);
			ObjectName objectName = 
				configService.createConfigData(session, resource.getParent().getConfigId(),resourceMetaData.getAttributeName(), 
						resource.getName(), newAttrList);
			SDLog.log("		Created new Attribute " + objectName) ;
			resource.setConfigId(objectName);			
		}else{

			SDLog.log("		++++++++++++++++ Should modify " + resource.getName() + " " + resource.getConfigId()) ;
		}
		
	**/	
	
		
	}

	private boolean typeOnExcludeList(String type){
		String[] excludeListArray = ResourceConstants.SYNC_IGNORE_TYPE_LIST;
		
		for (int i=0; i < excludeListArray.length; i++){
			if (excludeListArray[i].equalsIgnoreCase(type)){
				return true;
			}
		}
		return false;
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
		 */
	private void getMissingArrayPropertyAttribute(Session session, ConfigService configService, Resource resource,Resource referencedResources,DeployInfo deployInfo)
		throws AttributeNotFoundException, ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException{
		
		ResourceMetaData  resourceMetaData  = resource.getResourceMetaData();
		ArrayList arrayOfAttrList = (ArrayList)configService.getAttribute(session, resource.getParent().getConfigId(), resourceMetaData.getAttributeName());
		logger.trace("Got attribute: " + resourceMetaData.getAttributeName() + " for parent + " + resource.getParent().getName());
		LinkAttribute linkAttribute = ResourceHelper.getLinkAttribute(resource,resourceMetaData.getContainmentPath());
		String attributeValueOfResourceObject; 
		
		if (linkAttribute !=null){
			logger.debug(" ***************** todo link attribute");
			attributeValueOfResourceObject = "";
//				attributeValueOfResourceObject = getLinkAttributeValue(resource, linkAttribute, resourceMetaData.getContainmentPath(), referencedResources, scope).toString();
		}else{
			attributeValueOfResourceObject=  resource.getAttributeList().get(resourceMetaData.getContainmentPath()).toString();
		}
		
		//		logger.trace(" got the attributeList Array for: " + resource.getName() + " size is: " + arrayOfAttrList.size());
		if ( arrayOfAttrList.size() >0 ){
			
			// modifyArrayPropertyAttribute(arrayOfAttrList,resource,resourceMetaData);
			
			Iterator arrayOfAttrIterator = arrayOfAttrList.iterator();
			logger.trace("Loop through Iterator size:" + arrayOfAttrList.size());
			int cnt = 0;
			while (arrayOfAttrIterator.hasNext()){
				cnt ++;
				AttributeList attrList = (AttributeList)arrayOfAttrIterator.next();
				logger.trace("Getting attribute:" + resourceMetaData.getContainmentPath()+" for resource " + resource.getName());
				String configObjectAttrValue= ConfigServiceHelper.getAttributeValue(attrList, resourceMetaData.getContainmentPath()).toString();
		
		
				logger.trace("Matching value of attribute:" + resourceMetaData.getContainmentPath() + " attributeValueOfResourceObject  :" + attributeValueOfResourceObject + " and configObjectAttrValue " + configObjectAttrValue);
				if (!attributeValueOfResourceObject.equalsIgnoreCase(configObjectAttrValue)){
					logger.info("	Match Not found " ) ;

					Resource  newResource = new Resource();
					newResource.setName(resource.getName());
					newResource.setParent(resource.getParent());
					newResource.setParentTree(resource.getParentTree());
					newResource.setResourceMetaData(resource.getResourceMetaData());

					
					wasConfigReaderHelper.createNewResourceFromList(session,configService,newResource,attrList,"J2EEResourceProperty",cnt,referencedResources,deployInfo,"",resource.getResourceMetaData());
	//				ArrayList newAttributeResources =createNewResourceFromList(session,configService,newResource,attrList,"J2EEResourceProperty",cnt,referencedResources);
	//				Iterator newAttributeResourceIterator = newAttributeResources.iterator();
	//				while (newAttributeResourceIterator.hasNext()){

	//					resource.getParent().addInComingChild((Resource) newAttributeResourceIterator.next());
						
	//				}	

				}
			//	configService.setAttributes(session, resource.getConfigId(),attrList);
			}
			// comment 6
		}else{
			
			SDLog.log("		Created new Attribute " ) ;
		}
		
	}
		

	/**
	 * 
	 * @param configService
	 * @param session
	 * @param configObject
	 * @param referencedResources
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws DeployException
	 * @throws MalformedObjectNameException
	 * @throws AttributeNotFoundException
	 */
	public void getReferencedConfigObjectName(ConfigService configService,Session session, ObjectName configObject, Resource referencedResources,DeployInfo deployInfo )
		throws ConnectorException,ConfigServiceException,DeployException,MalformedObjectNameException,AttributeNotFoundException{
		//While processing attributes, if attribute value starts with ${, then getConfigObject(resource,resourceMetaData,Scope,variableName)

		//look up the definition of the object in ReferencedConfigObject xml
		//Call ConfigService.resolve(type, scope) where type is the name of the xml element and scope is the cluster under which object is definied
		//Loop through this list and attributes name and values pairs and for the matching one return.
		
		
		getMatchingResourceForConfigObject(configService, session, referencedResources, configObject, 0,referencedResources,null,deployInfo);
		
	}
	
}
