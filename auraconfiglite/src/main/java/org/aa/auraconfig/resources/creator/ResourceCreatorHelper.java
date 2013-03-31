package org.aa.auraconfig.resources.creator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
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
import org.aa.auraconfig.resources.metadata.CommandAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertyHelper;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
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
	 */
	
	public void  modifyConfigObject(Resource resource,Resource referenceResources,DeployInfo deployInfo, 
			ConfigService configService, AdminClient adminClient, Session session, ObjectName scope, 
			Vector<Resource> modifiedResources, Resource allResources)
		throws AttributeNotFoundException, ConfigServiceException, ConnectorException,DeployException{
		
		if ((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (resource.getResourceMetaData().getCustomCodeManaged() !=null)){ 
			
		
			logger.trace(" ResourceCreator: Will modify using Custom code");

			CustomCodeManager customCodeManager = new CustomCodeManager();
			ArrayList<DiffAttribute> modifiedAttrs =  customCodeManager.modify(session, configService, resource,resource.getConfigId() , deployInfo, adminClient,scope, allResources,referenceResources) ;
			if (modifiedAttrs !=null  ){
				resource.setModifiedAttributes(modifiedAttrs);
			}
			logger.trace("Adding this resource to modified resource list " + resource.getContainmentPath());
			modifiedResources.add(resource);
		}else if((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (resource.getResourceMetaData().isCommandManaged()) &&
				resource.getResourceMetaData().getCommandMetaData().getModifyCommand() !=null ){ 
		
				logger.trace(" ResourceCreator: Will modify using Command code");

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
			logger.info( "		Modified Attributes list size for resource " + resource.getContainmentPath() + " is " + changedAttrList.size() );
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
			throws AttributeNotFoundException, ConfigServiceException, ConnectorException,DeployException{

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
				logger.trace("Modifying Resource:" + resource.getContainmentPath() + " AttributeName:" + resourceAttributeName + " AttributeValue:" + resourceAttributeValue);

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
				
				if ( (deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (matchingCommandAttribute!=null) && (matchingCommandAttribute.getCommandLinkAttribute() !=null)){
					logger.trace( "		Is CommandlinkAttribute: " + resourceAttributeName  );
					CommandManager  commandManager = new CommandManager();
					DiffAttribute diffAttribute  =  commandManager.modifyResourceLinkAttriute(resource, resourceAttributeName, matchingCommandAttribute.getCommandLinkAttribute(), 
							adminClient, session, configService);
					if (diffAttribute  !=null){
						modifiedAttributes.add(diffAttribute);
					}
					
				}else if (commandAttributetype.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL) && (deployInfo.getVersionInfo().getMajorNumber() >=2 )){
					logger.trace( "		Is COMMAND_ADDITIONAL hence do nothing: " + resourceAttributeName  );

				}else if (linkAttribute != null){
					// as this is link we will modify resourceAttrbuteVariable to new value.
					resourceAttributeValue = getLinkAttributeValue(resource,linkAttribute,resourceAttributeName,
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
					logger.info( "		AttributeName:" + resourceAttributeName + "  oldValue: " + configAttributeValue + " new value:" + resourceAttributeValue);
					changedAttrList.add(new Attribute(resourceAttributeName,resourceAttributeValue));
				}
	//			newAttrList.add(new Attribute(key, value ));
			}
		}
		
		
	}
	
	/**
	 * 
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
	public Object getLinkAttributeValue(Resource resource, LinkAttribute linkAttribute, 
			String key,Resource referenceResources, ObjectName scope,DeployInfo deployInfo, Resource allResources,
			ConfigService configService, Session session)
		throws AttributeNotFoundException,ConfigServiceException,ConnectorException,DeployException{
		
		String matchContaintmentValue = (String)resource.getAttributeList().get(key);
		String targetAttribute = linkAttribute.getTargetAttribute();
		Resource matchResource = getMatchingResource(allResources, linkAttribute,matchContaintmentValue,referenceResources,scope);
		
		if ((matchResource == null) && (!deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC))){
				SDLog.log("No matching resource found for link reference attibute name: '" + key + "' of resource '" + resource.getContainmentPath() + "'") ;
				throw new DeployException(new Exception("No matching resource found for link reference attibute name:" + key + " of resource " + resource.getContainmentPath())) ;
		}
		
		if (matchResource == null){
			return "-" ;
		}else{
			checkIfConfigObjectExists( matchResource,referenceResources,deployInfo,scope,configService,session,allResources);
			
			Object linkAttributeValue;
			logger.trace(" Check if targetAttribute: " + linkAttribute.getTargetAttribute() + " for targetObject:" + linkAttribute.getTargetObject() + " is null, if null then return ObjectName");
			if (linkAttribute.getTargetAttribute()!=null){
				logger.trace(" Matching Config Objects found for link resource " + linkAttribute.getTargetObject() + " name: "  + linkAttribute.getTargetObjectMatchAttributeName() + " " + matchResource.getConfigId() );
				linkAttributeValue  = configService.getAttribute(session, matchResource.getConfigId() , targetAttribute);
				logger.trace(" Matching Config Objects found for link resource " + linkAttribute.getTargetObject() + " name: "  + linkAttribute.getTargetObjectMatchAttributeName() + " attribute name: " + targetAttribute.toString() + " linkAttributeValue " + linkAttributeValue);
			}else{
				logger.trace(" targetAttribute: " + linkAttribute.getTargetAttribute() + " for targetObject:" + linkAttribute.getTargetObject() + " is null, hence return " + matchResource.getConfigId());
				linkAttributeValue  = matchResource.getConfigId();
			}
			return linkAttributeValue  ;
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
			String matchContaintmentValue, Resource referencedResources, ObjectName scope)
			throws AttributeNotFoundException,DeployException,ConfigServiceException,ConnectorException{
		
		String targetObject = linkAttribute.getTargetObject();
		String targetObjectMatchAttributeName  = linkAttribute.getTargetObjectMatchAttributeName();
		
		logger.trace("Getting matching resource from all resources for link resource type:" + targetObject);
		if (allResources.getChildren() !=null){
			Vector children = allResources.getChildren();
			Iterator childrenIterator  = children.iterator();
			while (childrenIterator.hasNext()){
				Resource child = (Resource)childrenIterator.next();
				logger.trace("		matching resource:" +  child.getName() + " link resource type:" + targetObject);
	
				if (child.getName().equalsIgnoreCase(targetObject)){
					//AttributeList attributeList = getConfigAttributeList(child, referencedResources, child.getResourceMetaData(), scope);
					//Object matchingResourceAttributeValue = ConfigServiceHelper.getAttributeValue(attributeList, targetObjectMatchAttributeName);	
					Object matchingResourceAttributeValue = child.getAttributeList().get(targetObjectMatchAttributeName);
					logger.trace("			matching targetAttribute Name:" +  targetObjectMatchAttributeName + " for resource of type " + child.getName() + " source/xml value is '" + matchingResourceAttributeValue + "' to target/WAS repo config is '" + matchContaintmentValue + "'");
	
					if (matchingResourceAttributeValue.toString().equalsIgnoreCase(matchContaintmentValue)){
						logger.trace(" Matched Object is type '" + child.getName() + "' with containment path '" + child.getContainmentPath() + "'");
						return child;
					}
				}
				if (child.getChildren()!=null){
					Resource matchResource = getMatchingResource(child,linkAttribute,matchContaintmentValue,referencedResources,scope);
					if (matchResource != null){
						logger.trace(" Matched Object is" + matchResource.getContainmentPath());
						return matchResource;
					}
				}	
			}
		}
		logger.trace(" No Object was matched " );
		return null;
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
			ObjectName scope,ConfigService configService ,Session session, Resource allResources)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException{
		
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
					attributeValueOfResourceObject = getLinkAttributeValue
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
					}
					
				} 	
	
				if(configObjectForFindAndResolveExists){
					SDLog.log("		Object Exists, count: " + count);
					
					return true;	
				}else{
					SDLog.log("		Object does not exists");
					return false;
				}
	
			}else{
				
				// check if the children on this objects exists.
				SDLog.log("		Object Exists, count: " + configIDs.length );
				resource.setConfigId(configIDs[0]);
				return true;
				//	checkAllChildObjectsIfExists(resource,resourceMetaDataMap);	
			}
			
		}else{
			SDLog.log("		Object does not exists.");
			return false;
			//createConfigObject(resource,resourceMetaDataMap);
		}
	}

}
