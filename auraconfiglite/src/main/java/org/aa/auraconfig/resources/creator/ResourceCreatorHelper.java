package org.aa.auraconfig.resources.creator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.aa.auraconfig.resources.command.CommandManager;
import org.aa.auraconfig.resources.configreader.WASConfigReaderHelper;
import org.aa.auraconfig.resources.customcode.CustomCodeManager;
import org.aa.auraconfig.resources.finder.ResourceFinder;
import org.aa.auraconfig.resources.linkresources.LinkResourceHelper;
import org.aa.auraconfig.resources.metadata.CommandAttribute;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertyHelper;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.cmdframework.CommandException;
import com.ibm.websphere.management.cmdframework.CommandMgrInitException;
import com.ibm.websphere.management.cmdframework.CommandNotFoundException;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ResourceCreatorHelper {
	private static final Log logger  = LogFactory.getLog(ResourceCreatorHelper.class);	
	WASConfigReaderHelper wasConfigReaderHelper = new WASConfigReaderHelper();
	ResourceHelper resourceHelper = new ResourceHelper();

	ResourceFinder resourceFinder = new ResourceFinder();
	
	/**
	 * To modify config object
	 * Loop through the resource java object attribute list
	 * get the current attribute from configobject
	 * if value of current attribute in resource java object is different to value of config object then modify else print same will  not modify 
	 * @param resource
	 * @param resourceMetaData
	 * @param referencedResources
	 * @throws DeployException 
	 * @throws ConnectorException 
	 * @throws ConfigServiceException 
	 *
	 */
	
	public void  modifyConfigObject(Resource resource,Resource referenceResources,DeployInfo deployInfo, 
			ConfigService configService, AdminClient adminClient, Session session, ObjectName scope, 
			Vector<Resource> modifiedResources, Resource allResources)
		throws AttributeNotFoundException, DeployException, ConfigServiceException, ConnectorException, MalformedObjectNameException{
		logger.debug(">>> Object Exists " + resource.getContainmentPath() + " will call modify");

		if ((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (resource.getResourceMetaData().getCustomCodeManaged() !=null)){ 
			
		
			logger.debug(">>> ResourceCreator: Will modify using Custom code");

			CustomCodeManager customCodeManager = new CustomCodeManager();
			ArrayList<DiffAttribute> modifiedAttrs =  customCodeManager.modify(session, configService, resource,resource.getConfigId() , deployInfo, adminClient,scope, allResources,referenceResources) ;
			if (modifiedAttrs !=null  ){
				resource.setModifiedAttributes(modifiedAttrs);
			}
			logger.trace("Adding this resource to modified resource list " + resource.getContainmentPath());
			modifiedResources.add(resource);
		}
		else if((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (resource.getResourceMetaData().isCommandManaged()) &&
				resource.getResourceMetaData().getCommandMetaData().getModifyCommand() !=null ){ 
			logger.debug(" ResourceCreator: Will modify using Command code");
			CommandManager commandManager = new CommandManager();
			commandManager.modifyResource(resource, adminClient, session);

		} 
		// Has to be the after the custom code managed as any resource that is custom code will also be command managed.	
		//}else if (children[childCnt].getResourceMetaData().isCommandManaged()){
		//	modifyConfigObjectUsingCommand(children[childCnt],referenceResources,scope,deployInfo );
		else{
		
			// variables for difference report generation 	
			ArrayList<DiffAttribute> modifiedAttributes = new ArrayList<DiffAttribute>();
		
			// empty attribute list, modified attributes will be added to this attribute list.
			AttributeList changedAttrList = new AttributeList();
			// get java resource resource attr list
			HashMap<String, String> resourceAttributeMap = resource.getAttributeList();
	
			Iterator<String> attributeNameIterator = resourceAttributeMap.keySet().iterator();
			
			while (attributeNameIterator.hasNext()){
				
				// get 1st attribute's name and value
				String resourceAttributeName = (String )attributeNameIterator.next();
			
				modifyAttribute(resource,resource.getConfigId(),resourceAttributeName,modifiedAttributes,changedAttrList,configService,session,adminClient,scope,
						allResources,referenceResources,deployInfo);
	
			}

			logger.trace("Adding this resource to modified resource list " + resource.getContainmentPath());
			resource.setModifiedAttributes(modifiedAttributes);
			modifiedResources.add(resource);
			logger.debug( "<<<		Modified Attributes list size for resource " + resource.getContainmentPath() + " is " + changedAttrList.size() );
			configService.setAttributes(session, resource.getConfigId(), changedAttrList);
		}
		
	}

	/**
	 * 
	 * @param resource
	 * @param resourceAttributeName
	 * @param modifiedAttributes
	 * @param changedAttrList
	 * @param configService
	 * @param session
	 * @param adminClient
	 * @param scope
	 * @param allResources
	 * @param referenceResources
	 * @param deployInfo
	 * @throws AttributeNotFoundException
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws DeployException
	 */
	public void modifyAttribute(Resource resource,ObjectName resourceWasObject, String resourceAttributeName,ArrayList<DiffAttribute> modifiedAttributes, AttributeList changedAttrList , 
			ConfigService configService, Session session, AdminClient adminClient, ObjectName scope, Resource allResources, 
			Resource referenceResources, DeployInfo deployInfo)
			throws AttributeNotFoundException, ConfigServiceException, ConnectorException,DeployException,MalformedObjectNameException{

		HashMap<String, String> resourceAttributeMap = resource.getAttributeList();
		// not using resource type as in case on custom code resource and wasobject are not same, e.g with sibdatasource wasobject sibusmember resource is passed
		
		String resourceDataType= ConfigServiceHelper.getConfigDataType(resourceWasObject);
		
		AttributeList metaInfo =  configService.getAttributesMetaInfo(resourceDataType);
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper (); 

		boolean shouldModifyCurrentAttribute = false;

		if (!ResourceHelper.isOnIgnoreList(resourceAttributeName)  ){
			if (!(resourceAttributeName.equalsIgnoreCase(ResourceConstants.ATTRUBUTENAME) || resourceAttributeName.equalsIgnoreCase(ResourceConstants.TEMPLATE))){
				
				// check if the attribute is additonal and also if it is CommandLinkAttriute, 
				// If just additional and not link then do not modify, else modify using commandlink logic
				String commandAttributetype = "";
				CommandAttribute matchingCommandAttribute = resourceHelper.isThisExtraAttributeForCommand(resource, resourceAttributeName);
				if (matchingCommandAttribute !=null){
					commandAttributetype = matchingCommandAttribute.getType();
				}
				
				Object resourceAttributeValue = resourceAttributeMap.get(resourceAttributeName) ;
				logger.trace("      AttributeName:" + resourceAttributeName + " AttributeValue:" + resourceAttributeValue);

				// get config attribute value for this attribute, only if not additional
				Object configAttributeValue = null; 
				if (!commandAttributetype.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL)){
					configAttributeValue = configService.getAttribute(session, resourceWasObject, resourceAttributeName);
				}
				logger.trace("		 Config Resource Attribute:" + resourceAttributeName  + " In repository Config AttributeValue:" +  configAttributeValue);

				LinkAttribute linkAttribute = ResourceHelper.getLinkAttribute(resource,resourceAttributeName);
				logger.trace( "		Attribute is: " + resourceAttributeName + " linkAttribute is: "  + linkAttribute);
				
				// check the type of this attribute using config object attribute meta info
				boolean isReference = ResourceHelper.isAttributeReference(metaInfo,resourceAttributeName );
				logger.trace( "		Attribute is: " + resourceAttributeName+ " isReference is: "  + isReference);

				boolean isCollection = ResourceHelper.isCollection(metaInfo,resourceAttributeName);
				logger.trace( "		Attribute is: " + resourceAttributeName + " isCollection is: "  + isCollection);
	
				String type = ResourceHelper.getAttributeType(metaInfo,resourceAttributeName );
				
				if ( (deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (matchingCommandAttribute!=null) 
						&& (matchingCommandAttribute.getCommandLinkAttribute() !=null)){
					SDLog.log("Attribute cannot be modified "+ resourceAttributeName  );
					logger.trace( "		Is CommandlinkAttribute: " + resourceAttributeName  );
			/**		CommandManager  commandManager = new CommandManager();
					DiffAttribute diffAttribute  =  commandManager.modifyResourceLinkAttriute(resource, resourceAttributeName, matchingCommandAttribute.getCommandLinkAttribute(), 
							adminClient, session, configService);
					if (diffAttribute  !=null){
						modifiedAttributes.add(diffAttribute);
					} **/
					
				}else if (commandAttributetype.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL) && (deployInfo.getVersionInfo().getMajorNumber() >=2 )){
					logger.trace( "		Is COMMAND_ADDITIONAL hence do nothing: " + resourceAttributeName  );
				}else if ((linkAttribute != null) && isCollection){
					SDLog.log(" Manage by calling modifyArrayPropertyAttribute");
					
					modifyArrayPropertyAttribute(new ArrayList<ObjectName>(),resource,referenceResources,deployInfo,configService,session,allResources,scope);

				}else if (linkAttribute != null){
					// as this is link we will modify resourceAttrbuteVariable to new value.
					resourceAttributeValue = getLinkAttributeForMatchAttributeValue(resource,linkAttribute,resourceAttributeName,
							referenceResources,scope,deployInfo, allResources, configService, session);
					logger.trace( "		As is linkAttribute: " + resourceAttributeName + " getting matching reference " + configAttributeValue );

					if ((configAttributeValue==null) || (!configAttributeValue.toString().equalsIgnoreCase(resourceAttributeValue.toString()))){
						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
						shouldModifyCurrentAttribute = true;
					}

					
				}else if (isReference){
					logger.trace( "		As is reference : " + resourceAttributeName + " getting matching reference " );
	//				String variableName = ResourceHelper.getVariableName(value.toString());
	//				value  = getReferencedConfigObjectName(variableName, scope, referencedResources);
					logger.trace( "		As is reference : " + resourceAttributeName + " matching reference is " + configAttributeValue);
					
				}else if (type.equalsIgnoreCase("long")){
					configAttributeValue = new Long (configAttributeValue.toString());
					resourceAttributeValue = new Long (resourceAttributeValue.toString());
					
					if (new Long (configAttributeValue.toString()).longValue() != new Long (resourceAttributeValue.toString()).longValue()){
						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
						shouldModifyCurrentAttribute = true;
					}
					
				}else if (type.equalsIgnoreCase("int")){
					configAttributeValue = new Integer (configAttributeValue.toString());
					resourceAttributeValue = new Integer (resourceAttributeValue.toString());
	
					if (new Integer (configAttributeValue.toString()).intValue() != new Integer (resourceAttributeValue.toString()).intValue()){
						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
						shouldModifyCurrentAttribute = true;
					}

				}else if (type.equalsIgnoreCase("boolean")){
					configAttributeValue = new Boolean (configAttributeValue.toString());
					resourceAttributeValue = new Boolean (resourceAttributeValue.toString());
	
					if (new Boolean (configAttributeValue.toString()).booleanValue() != new Boolean(resourceAttributeValue.toString()).booleanValue()){
						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
						shouldModifyCurrentAttribute = true;
					}

				}else if (isReference){
					configAttributeValue = (ObjectName)configAttributeValue;
					
				}else if (configAttributeValue==null){
					modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
					shouldModifyCurrentAttribute = true;
					
				}else if ((new Boolean(isCollection)).booleanValue() && (type.equalsIgnoreCase("String"))){
					//ArrayList classpath = new ArrayList();
					// classpath.add(resourceAttributeValue.toString());
					ArrayList classpath =  PropertyHelper.getArrayFromCommaSeperated(resourceAttributeValue.toString());
					if (!resourceAttributeValue.toString().equals(ResourceHelper.getStringFromArrayList((ArrayList)configAttributeValue)))
					{
						logger.trace("*********************************************************");
						logger.trace(resourceAttributeValue.toString());
						logger.trace(ResourceHelper.getStringFromArrayList((ArrayList)configAttributeValue));
						logger.trace("*********************************************************");

						// String[] classpath = {resourceAttributeValue.toString()};
						// TODO: Handle ArrayList as arrayList rather then as String
						resourceAttributeValue = classpath ;
						shouldModifyCurrentAttribute = true;
						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, ResourceHelper.getStringFromArrayList((ArrayList)configAttributeValue)));
						logger.trace(" 	Will assign value as String Array:" + resourceAttributeName + " value " + resourceAttributeValue.toString());
					}

				}else  {
					configAttributeValue = configAttributeValue.toString();
					if (!configAttributeValue.toString().equals(resourceAttributeValue.toString())){
						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(resourceAttributeName, resourceAttributeValue, configAttributeValue));
						shouldModifyCurrentAttribute = true;
					}
				}
				if (shouldModifyCurrentAttribute){
					logger.debug( "		AttributeName:" + resourceAttributeName + "  oldValue: " + configAttributeValue + " new value:" + resourceAttributeValue);
					changedAttrList.add(new Attribute(resourceAttributeName,resourceAttributeValue));
				}
	//			newAttrList.add(new Attribute(key, value ));
			}
		}
		
		
	}
	


	


	/**
	 * 1: get configIds for the resource supplied
	 * 2: If configIds are less then 0 then return false that Config Object does not exists
	 * 3: Else check if FindAndResolve is true for this type of Resource from ReourceMetaData
	 * 			If true 
	 * 			Get the containment from ResourceMeta data this will give the attribute name that must be checked
	 * 			Loop through all configIds and using configservice.getAttribute get the containment attribute value for each ConfigObject
	 * 			Match this with the attribute that value of current resource being processed. 
	 * 			If same ObjectExists else does not Exists  
	 * 4: set the config id for the resource and return true
	 * 
	 * @param resource
	 * @param resourceMetaData
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public boolean checkIfConfigObjectExists(Resource resource,Resource referenceResource,DeployInfo deployInfo,
			ObjectName scope,ConfigService configService ,Session session, Resource allResources,boolean shouldLog)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException{
		
		logger.debug(">>> Start find resource " + resource.getContainmentPath());
		AttributeList metaInfo =  configService.getAttributesMetaInfo(resource.getName());
		HashMap map = ResourceHelper.getResourceAttributeMetaData(metaInfo);
		resource.setResourceAttrMetaInfo(map);
	
		ResourceFinder resourceFinder = new ResourceFinder();  
		ObjectName[] configIDs = resourceFinder.findAllResourcesInConfig(session, configService, resource, scope, false);
	
		int count = configIDs.length;
		// if number of objects returned are greater then 0 then the ConfigObject object exists
		// but if find and resolve is true then 
		// loop through all the objects and check if the attribute name mentioned in meta data and value in resources matches.
		String matchAttributeName = resource.getResourceMetaData().getContainmentPath();
		if (resource.getResourceMetaData().getMatchAttribute()!=null){ 
			matchAttributeName = resource.getResourceMetaData().getMatchAttribute();
		}
	
		if (configIDs.length >0){
			// if find and resolve is true then 
			if (resource.getResourceMetaData().isFindAndResolve()){
				count = 0;
				// loop through all the config objects
				// 		find the attribute value that matches and return that object.
				boolean configObjectForFindAndResolveExists = false;
				LinkAttribute linkAttribute =  ResourceHelper.getLinkAttribute(resource, matchAttributeName);
				String attributeValueOfResourceObject ;
				if (linkAttribute == null){
					attributeValueOfResourceObject =  resource.getAttributeList().get(matchAttributeName).toString();
				}else{
					attributeValueOfResourceObject = getLinkAttributeForMatchAttributeValue
					(resource, linkAttribute, matchAttributeName, referenceResource, scope,deployInfo , allResources, configService, session).toString() ; 
				}
	
				for (int configObjectCnt = 0;configObjectCnt< configIDs.length;configObjectCnt++){
					String attributeValueOfConfigObject = "null";
					if (configService.getAttribute(session, configIDs[configObjectCnt], matchAttributeName) !=null){
						attributeValueOfConfigObject = configService.getAttribute(session, configIDs[configObjectCnt], matchAttributeName).toString();
					}
					
					logger.trace(" Matching value of config type:(" + configObjectCnt +")"+ resource.getName() + " attribute: " + matchAttributeName + " config attr value " + attributeValueOfConfigObject + " attributeValueOfResourceObject: " + attributeValueOfResourceObject); 
					if(attributeValueOfConfigObject.equalsIgnoreCase(attributeValueOfResourceObject)){
						count ++;
						configObjectForFindAndResolveExists = true;
						resource.setConfigId(configIDs[configObjectCnt]);
						if (shouldLog)
							SDLog.log("		Object Exists, count: " + count);
						logger.debug("<<<		1 Object Exists, count: " + count + " " + configIDs[configObjectCnt]);
					}
					
				} 	
	
				if(configObjectForFindAndResolveExists){
					if (shouldLog)
						SDLog.log("		Object Exists, count: " + count );
					logger.debug("<<<		1 Object Exists, count: " + count );
					
					return true;	
				}else{
					if (shouldLog)
						SDLog.log("		Object does not exists");
					logger.debug("<<<		Object does not exists");
					return false;
				}
	
			}else{
				if (configIDs.length >1){
					ObjectName[] newConfigIDs = matchAdditionalContainmentAttr(configIDs,resource,referenceResource,deployInfo,scope,configService,session,allResources);
					if ( (newConfigIDs!=null) && newConfigIDs.length >0){
						if (shouldLog)
							SDLog.log("		Object Exists, count: " + newConfigIDs.length );
						logger.debug("<<<		2 Object Exists, count: " + newConfigIDs.length + " " + newConfigIDs[0]);
						resource.setConfigId(newConfigIDs[0]);
						return true;
					}else{
						if (shouldLog)
							SDLog.log("		Object does not exists.");
						logger.debug("		Object does not exists.");
						return false;
					}
				}else{
					if (shouldLog)
						SDLog.log("		Object Exists, count: " + configIDs.length );
					logger.debug("<<<		3 Object Exists, count: " + configIDs.length  + " " + configIDs[0]);
					resource.setConfigId(configIDs[0]);
					return true;
				}
				
				//	checkAllChildObjectsIfExists(resource,resourceMetaDataMap);	
			}
			
		}else{
			if (shouldLog)
				SDLog.log("		Object does not exists.");
			logger.debug("<<<		Object does not exists.");

			return false;
			//createConfigObject(resource,resourceMetaDataMap);
		}
	}
	
	
	/**
	 * Method to get the resource which is of linkAtributes Target Object type and matches the linkAttributes values.
	 * @param allResources
	 * @param linkAttribute
	 * @param matchContaintmentValue
	 * @return
	 */
	
	public Resource getMatchingResource(Resource allResources, LinkAttribute linkAttribute, 
			String sourceValue, Resource referencedResources, ObjectName scope)
			throws AttributeNotFoundException,DeployException,ConfigServiceException,ConnectorException{
		
		String[] targetObject = linkAttribute.getTargetObject().split("\\|");
		String[] targetObjectMatchAttributeNames  = linkAttribute.getTargetObjectMatchAttributeName().split("\\|");;
		String[] matchContaintmentValues = sourceValue.split("\\|");
		
		for (int targetObjectCnt = 0; targetObjectCnt < targetObject.length; targetObjectCnt++){ 
			logger.trace("Getting matching resource from all resources for link resource type:" + targetObject[targetObjectCnt]);
			if (allResources.getChildren() !=null){
				Vector children = allResources.getChildren();
				Iterator childrenIterator  = children.iterator();
				while (childrenIterator.hasNext()){
					Resource child = (Resource)childrenIterator.next();
					logger.trace("		matching resource:" +  child.getName() + " link resource type:" + targetObject[targetObjectCnt]);
		
					if (child.getName().equalsIgnoreCase(targetObject[targetObjectCnt])){
						//AttributeList attributeList = getConfigAttributeList(child, referencedResources, child.getResourceMetaData(), scope);
						//Object matchingResourceAttributeValue = ConfigServiceHelper.getAttributeValue(attributeList, targetObjectMatchAttributeName);
						boolean matchFound = true;
						for (int targetObjectMatchAttributeNameCnt = 0; targetObjectMatchAttributeNameCnt <  targetObjectMatchAttributeNames.length ; targetObjectMatchAttributeNameCnt ++) {
							if (matchFound){
								Object matchingResourceAttributeValue =null;
								if (targetObjectMatchAttributeNames[targetObjectMatchAttributeNameCnt].equalsIgnoreCase(ResourceConstants.RESOURCE_CONFIG_ID)){
									matchingResourceAttributeValue = child.getConfigId();
									System.out.println(" Since the match attribute is RESOURCE_CONFIG_ID, have got config id from resource which is: " + matchingResourceAttributeValue);
								}else{
									matchingResourceAttributeValue = child.getAttributeList().get(targetObjectMatchAttributeNames[targetObjectMatchAttributeNameCnt]);
								}
								
								logger.trace("			matching targetAttribute Name:" +  targetObjectMatchAttributeNames[targetObjectMatchAttributeNameCnt] + " for resource of type " + child.getName() + " source/xml value is '" + matchingResourceAttributeValue + "' to target/WAS repo config is '" + matchContaintmentValues[targetObjectMatchAttributeNameCnt]+ "'");
				
								if (matchingResourceAttributeValue.toString().equalsIgnoreCase(matchContaintmentValues[targetObjectMatchAttributeNameCnt])){
									logger.trace(" Matched Object is type '" + child.getName() + "' with containment path '" + child.getContainmentPath() + "'");
									matchFound = true;
								}else{
									matchFound = false;
								}
							}
						}
						if (matchFound){
							return child;
						}
					}
					if (child.getChildren()!=null){
						Resource matchResource = getMatchingResource(child,linkAttribute,sourceValue,referencedResources,scope);
						if (matchResource != null){
							logger.trace(" Matched Object is" + matchResource.getContainmentPath());
							return matchResource;
						}
					}	
				}
			}
		}
		logger.trace(" No Object was matched " );
		return null;
	}

	
	/**
	 * Applying changes to WebSphere when meta data of a resource/attribute is of type link, then get targetAttribute(in some cases is empty then get config ID) value of the targetObject
	 * In some case there can be multiple match attributes for KeyStore,keyManager.
	 * When creating a new object Resource, targetResource for the link attribute might not exists in WebSphere configuration, If so then this method will create that Target Config object as specified in the Resource XML
	 * 
	 * If targetObject is missing in Resource XML then method will error
	 * @param resource
	 * @param linkAttribute
	 * @param key
	 * @param referenceResources
	 * @param scope
	 * @param deployInfo
	 * @param allResources
	 * @param configService
	 * @param session
	 * @return
	 * @throws AttributeNotFoundException
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws DeployException
	 */
	public Object getLinkAttributeForMatchAttributeValue(Resource resource, LinkAttribute linkAttribute, 
			String key,Resource referenceResources, ObjectName scope,DeployInfo deployInfo, Resource allResources,
			ConfigService configService, Session session)
		throws AttributeNotFoundException,ConfigServiceException,ConnectorException,DeployException{
		
		// get value of the attribute as set, this will give value of link attr as set in source resource xml
		String matchContaintmentValue = (String)resource.getAttributeList().get(key);
		String targetAttribute = linkAttribute.getTargetAttribute();
		// get resource where value got above matches with value set in resource of target type
		Resource matchResource = getMatchingResource(allResources, linkAttribute,matchContaintmentValue,referenceResources,scope);
		
		if ((matchResource == null) && (!deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC))){
				SDLog.log("No matching resource found for link reference attibute name: '" + key + "' of resource '" + resource.getContainmentPath() + "'") ;
				throw new DeployException(new Exception("No matching resource found for link reference attibute name:" + key + " of resource " + resource.getContainmentPath())) ;
		}
		
		if (matchResource == null){
			return "-" ;
		}else{
			//set config Id for the resource
			 // When creating a new object Resource, targetResource for the link attribute might not exists in WebSphere configuration, If so then this method will create that Target Config object as specified in the Resource XML
			setConfigIDOrCreate(matchResource,referenceResources,deployInfo,scope, configService,session,allResources);
			
			LinkResourceHelper linkResourceHelper = new LinkResourceHelper(session,configService);
			Object linkAttributeValue = linkResourceHelper.getLinkAttributeForMatchAttributeValue(linkAttribute,matchResource);
			
			return linkAttributeValue  ;
		}
		
	}


	private void setConfigIDOrCreate(Resource matchResource,Resource  referenceResources,DeployInfo deployInfo,ObjectName scope, ConfigService configService,
				Session session,Resource allResources) throws ConfigServiceException, AttributeNotFoundException, ConnectorException, DeployException{
		checkIfConfigObjectExists(matchResource,referenceResources,deployInfo,scope,configService,session,allResources,false);

	}
	
	/**
	 * 
	 * @param configIDs
	 * @param resource
	 * @param referenceResource
	 * @param deployInfo
	 * @param scope
	 * @param configService
	 * @param session
	 * @param allResources
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws AttributeNotFoundException
	 * @throws DeployException
	 */
	private ObjectName[] matchAdditionalContainmentAttr(ObjectName[] configIDs, Resource resource,Resource referenceResource,DeployInfo deployInfo,
			ObjectName scope,ConfigService configService ,Session session, Resource allResources)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException{
		// check if the children on this objects exists.
		logger.trace("Will Check if additional containment attr must be matched");
		ArrayList<ObjectName> filteredConfigIDs = new ArrayList<ObjectName>();
		String[] addMatchAttributeNames = resource.getResourceMetaData().getAdditionalContainmentAttribute();
		if (addMatchAttributeNames!=null){ 
			logger.trace("Additional containment attr must be matched, number is " + addMatchAttributeNames.length);
			// loop through all the config objects found for a type
			for (int configObjectCnt = 0;configObjectCnt< configIDs.length;configObjectCnt++){
				boolean configObjectForAddMatchExists = true;
				// check that all the attributes values match, exit on 1st match fail 
				for (int addContainmentAttrCnt = 0;addContainmentAttrCnt < addMatchAttributeNames.length ;addContainmentAttrCnt++){
					String attributeValueOfConfigObject = "null";
					logger.trace("Matching containment attr :" +addMatchAttributeNames[addContainmentAttrCnt]);
					
					logger.trace("Check if link attr :" +addMatchAttributeNames[addContainmentAttrCnt]);
					LinkAttribute linkAttribute =  ResourceHelper.getLinkAttribute(resource, addMatchAttributeNames[addContainmentAttrCnt]);
					String attributeValueOfResourceObject ;
					if (linkAttribute == null){
						logger.trace("link attr false");
						attributeValueOfResourceObject =  resource.getAttributeList().get(addMatchAttributeNames[addContainmentAttrCnt]).toString();
					}else{
						logger.trace("link attr true");
						attributeValueOfResourceObject = getLinkAttributeForMatchAttributeValue
						(resource, linkAttribute, addMatchAttributeNames[addContainmentAttrCnt], referenceResource, scope,deployInfo , allResources, configService, session).toString() ; 
					}

					if (configService.getAttribute(session, configIDs[configObjectCnt], addMatchAttributeNames[addContainmentAttrCnt]) !=null){
						attributeValueOfConfigObject = configService.getAttribute(session, configIDs[configObjectCnt], addMatchAttributeNames[addContainmentAttrCnt]).toString();
					}
					logger.trace(" Matching value of config type:(" + configObjectCnt +")"+ resource.getName() + " attribute: " + addMatchAttributeNames[addContainmentAttrCnt] + " config attr value " + attributeValueOfConfigObject + " attributeValueOfResourceObject: " + attributeValueOfResourceObject); 

					if(!attributeValueOfConfigObject.equalsIgnoreCase(attributeValueOfResourceObject)){
						configObjectForAddMatchExists = false;
					}
				}
				if (configObjectForAddMatchExists){
					logger.trace(" Match found adding:(" + configIDs[configObjectCnt] +")"); 
					filteredConfigIDs.add(configIDs[configObjectCnt]);
				}
			}
			return filteredConfigIDs.toArray(new ObjectName[filteredConfigIDs.size()]);
		}else{
			logger.trace("No Additional containment attr must be matched");
			return configIDs;
		}
	}	


	/**
	 * For the array, get the each attributelist
	 * for this attribute list match that attribute name to resource attribute list
	 * @param arrayOfAttrList
	 * @param resource
	 * @param resourceMetaData
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws AttributeNotFoundException
	 */
	public void modifyArrayPropertyAttribute(ArrayList arrayOfAttrList,Resource resource,Resource referenceResources,
			DeployInfo deployInfo,ConfigService configService ,Session session, Resource allResources,ObjectName scope)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException{

		logger.trace("Modify array type " + resource.getName() + " " + resource.getContainmentPath());
		
		ResourceMetaData resourceMetaData = resource.getResourceMetaData();
		Iterator arrayOfAttrIterator = arrayOfAttrList.iterator();
		ArrayList modifiedAttributes = new ArrayList();
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper ();  

		String matchAttributeName; 
		if (resourceMetaData.getMatchAttribute()!=null){ 
			matchAttributeName = resourceMetaData.getMatchAttribute();
		}else{
			matchAttributeName = resourceMetaData.getContainmentPath();
		}

		while (arrayOfAttrIterator.hasNext()){
			
			AttributeList attrList = (AttributeList)arrayOfAttrIterator.next();

			LinkAttribute linkAttribute =  ResourceHelper.getLinkAttribute(resource, matchAttributeName );

			String configAttributeValue= ConfigServiceHelper.getAttributeValue(attrList, matchAttributeName ).toString();
			String resourceAttributeValue =  resource.getAttributeList().get(matchAttributeName).toString();
			
			//The match attribute can also be a link type and hence we are checking for that.
			if(linkAttribute != null){
				logger.trace("*************************** is it link attribute");
				resourceAttributeValue = getLinkAttributeForMatchAttributeValue(resource,linkAttribute,matchAttributeName .toString(),referenceResources,scope,deployInfo,allResources,configService,session).toString();
				logger.trace("*************************** resourceAttributeValue " + resourceAttributeValue);
				logger.trace("*************************** configAttributeValue " + configAttributeValue);
			}
	
			logger.trace("Matching value of attribute:" + matchAttributeName  + " attributeValueOfResourceObject  :" + resourceAttributeValue + " and configObjectAttrValue " + configAttributeValue);
			
			if (resourceAttributeValue.equalsIgnoreCase(configAttributeValue)){
				logger.trace("Match found for " + resource.getName() + " " + resourceAttributeValue);
				
				HashMap resourceAttrMap =  resource.getAttributeList();
				Iterator keysIterator = resourceAttrMap.keySet().iterator();
				AttributeList metaInfo =  configService.getAttributesMetaInfo(resource.getName());
				boolean atleastOneChanged = false;
				
				while (keysIterator.hasNext() ){
					String key = (String) keysIterator.next();
					logger.trace(" Processing " + key );
					// we have checked the containment key so not need to match it again
					if (!key.equalsIgnoreCase(matchAttributeName.toString() )){
					
						boolean isSame = false;
						Object resourceValue ; 
						Object configValue ;
						LinkAttribute attrLinkAttribute =  ResourceHelper.getLinkAttribute(resource, key);
	
						if((attrLinkAttribute!=null) &&  (ResourceHelper.isAttributeReference(metaInfo, key))){
							// SDLog.log(" ************ It is a Link and Reference********** " + key); 
							logger.trace(" ************ It is a Link and Reference ********** " + key); 

							configValue = (ObjectName)ConfigServiceHelper.getAttributeValue(attrList, key);
							resourceValue = new ObjectName(getLinkAttributeForMatchAttributeValue(resource,attrLinkAttribute,key,referenceResources,scope,deployInfo,allResources,configService,session).toString());

							//SDLog.log(" ************ It is a Link: resourceValue  ********** " + resourceValue ); 
							//SDLog.log(" ************ It is a Link: configValue ********** " + configValue); 

							logger.trace(" ************ It is a Link: resourceValue  ********** " + resourceValue ); 
							logger.trace(" ************ It is a Link: configValue ********** " + configValue); 
							
							if (configValue.equals(resourceValue)){
								SDLog.log("It is Link is same true"   );
								isSame = true;
							} else{
								SDLog.log("It is Link is same false"   );
							}
						}else if((attrLinkAttribute!=null) &&  (!ResourceHelper.isAttributeReference(metaInfo, key))){
							//SDLog.log(" ************ It is a Link ********** " + key); 
							logger.trace(" ************ It is a Link ********** " + key); 

							configValue = ConfigServiceHelper.getAttributeValue(attrList, key);
							resourceValue = getLinkAttributeForMatchAttributeValue(resource,attrLinkAttribute,key,referenceResources,scope,deployInfo,allResources,configService,session).toString();

							//SDLog.log(" ************ It is a Link: resourceValue  ********** " + resourceValue ); 
							//SDLog.log(" ************ It is a Link: configValue ********** " + configValue); 

							logger.trace(" ************ It is a Link: resourceValue  ********** " + resourceValue ); 
							logger.trace(" ************ It is a Link: configValue ********** " + configValue); 
							
							if (configValue.toString().equalsIgnoreCase(resourceValue.toString())){
								logger.trace("It is Link is same true"   );
								isSame = true;
							} else{
								logger.trace("It is Link is same false"   );
							}
							
						}else if (ResourceHelper.isBoolean(metaInfo, key)){
							resourceValue = new Boolean(resourceAttrMap.get(key).toString());
							configValue = (Boolean)ConfigServiceHelper.getAttributeValue(attrList, key);
							if (((Boolean)resourceValue).booleanValue() == ((Boolean)configValue).booleanValue()){
								isSame = true;
							} 
						}else if (ResourceHelper.isString(metaInfo, key)){
							resourceValue = (String)resourceAttrMap.get(key);
							configValue = (String)ConfigServiceHelper.getAttributeValue(attrList, key);
							if (((String)resourceValue).equalsIgnoreCase((String)configValue)){
								isSame = true;
							} 
							
						}else if (ResourceHelper.isInt(metaInfo, key)){
							resourceValue = new Integer(resourceAttrMap.get(key).toString());
							configValue = (Integer)ConfigServiceHelper.getAttributeValue(attrList, key);
							if (((Integer)resourceValue).intValue() == ((Integer)configValue).intValue()) {
								isSame = true;
							} 
							
						}else if (ResourceHelper.isLong(metaInfo, key)){
							resourceValue = new Long(resourceAttrMap.get(key).toString());
							configValue = (Long)ConfigServiceHelper.getAttributeValue(attrList, key);
							if (((Long)resourceValue).longValue() == ((Long)configValue).longValue()) {
								isSame = true;
							} 
						
	
						}else {
	
							resourceValue = (String)resourceAttrMap.get(key);
							configValue = (String)ConfigServiceHelper.getAttributeValue(attrList, key);
							if (((String)resourceValue).equalsIgnoreCase((String)configValue)){
								isSame = true;
							} 
						}
						if (!isSame ){
							atleastOneChanged = true;
							logger.trace("attrList  " + attrList);
							logger.trace("Adding modified attributes resource: " + " key:  "+ key + " resourceValue " +   resourceValue  + " configValue " + configValue );
							ConfigServiceHelper.setAttributeValue(attrList, key,resourceValue);
							

							//SDLog.log("Adding modified attributes resource: " + " key:  "+ key + " resourceValue " +   resourceValue  + " configValue " + configValue );
							modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute(key,resourceValue , configValue));
						}
					}
					if (atleastOneChanged){
						logger.trace("attrList  " + attrList);
						configService.setAttributes(session, resource.getConfigId(),attrList);
					}
				}
			}else{
				logger.trace("		Match NOT found for " + resource.getName() + " " + resourceAttributeValue);

			}
		}
		
		resource.setModifiedAttributes(modifiedAttributes);
	
	}


}
