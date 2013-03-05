/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.apartech.auraconfig.resources.parser.ResourceParserHelper;
import com.apartech.auraconfig.resources.parser.ResourceXMLParser;
import com.apartech.common.exception.DeployException;
import com.apartech.common.properties.helper.PropertiesConstant;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ResourceFinder {
	private static final Log logger  = LogFactory.getLog(ResourceFinder.class);	

	/**
	 *  Method to get all the config objects that match resource given
	 *  This method check if the find should be done by containment path or find and resolve
	 *  or if it should be done by attribute name
	 *  
	 *  
	 * @param session
	 * @param configService
	 * @param resource
	 * @param scope
	 * @param inSyncMethod
	 * @return
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws AttributeNotFoundException
	 * @throws MalformedObjectNameException
	 */	
	public ObjectName[] findAllResourcesInConfig(Session session,ConfigService configService,Resource resource,
			ObjectName scope, boolean inSyncMethod )
		throws ConnectorException, ConfigServiceException,AttributeNotFoundException {
			
		ResourceHelper resourceHelper = new ResourceHelper();
		
	
		ObjectName[] configIDs = null;
		String attributeNameForDuplicateObjectTypeChild = null;
		if (resource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME)!=null){
			attributeNameForDuplicateObjectTypeChild  = resource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString();
		} 
		/**
		 * SyncFindModeContainmentPath is used for objects like SIBusMemberTarget
		 * where there can be only one bus SIBusMemberTarget under SIBusMember, so that we display only that.
		 * 
		 * But in the find mode we use the logic of 
		 */
		if (resource.getResourceMetaData().isSyncFindModeContainmentPath()){
			logger.trace(">>> Get all ConfigObjects for the containment path " + resource.getContainmentPath() +  " ] " ); 
			
			configIDs = configService.resolve(session, resource.getContainmentPath());
			//configIDs = resourceHelper.getObjectNames(session,configService, resource.getContainmentPath()); 
		
		}else if ((!inSyncMethod)&& (!resource.getResourceMetaData().isFindAndResolve())){
			configIDs = resourceHelper.getObjectNames(session,configService, resource.getContainmentPath()); 
		}else if (scope == null){
			logger.trace(">>> Get all ConfigObjects for the type " + resource.getName() + " to find " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) ); 
			configIDs = configService.resolve(session, resource.getName());
		}else if (attributeNameForDuplicateObjectTypeChild  != null){
			logger.trace(">>> Get all ConfigObjects by getting  value of the attribute " + resource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME) + " for resource " + resource.getName() + " to find " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) );
			ObjectName[] newConfigObject = {ConfigServiceHelper.createObjectName((AttributeList)configService.getAttribute(session, resource.getParent().getConfigId() , attributeNameForDuplicateObjectTypeChild ))};
			configIDs = newConfigObject;
		}
		else{
			logger.trace(">>> Get ConfigObjects for the type " + resource.getName() + " in scope " + configService.getAttribute(session, scope,"name") + " to find " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()) ); 
			configIDs = configService.resolve(session, scope, resource.getName());
		}
		return configIDs;
	}
	
	
	/**
	 * Now check if there a syncPreMatchAttribute, If the value for the attribute exists 
	 * Check if the attribute is of reference type. If it is reference type then for the value of 
	 * this attribute from WAS config get the resource reference value.
	 * If resource reference value is not found then mark this object as prematch fail 
	 * If resource reference value is found then match the resource reference name 
	 * to the value of in the resource object. This is to ensure that type of object reterived is 
	 * correct.
	 * 
	 * This is useful for object like ActivationSpec, QCF and AdminObject which have connectiondefination
	 * and adminobject attribute which defines what type of object it is
	 * 
	 * By match these attributes we ensure that in sync operation that we do not fetch the objects that 
	 * are not required. 
	 */
	public boolean doesConfigObjectPreMatch(Session session,ConfigService configService,
			Resource resource,ObjectName configObject,Resource referencedResources)
		throws ConnectorException, ConfigServiceException,AttributeNotFoundException,MalformedObjectNameException,AttributeNotFoundException,DeployException {
			
		boolean syncPreMatch = true;
	
		if ((resource.getResourceMetaData().getSyncPreMatchAttribute()!=null) && (resource.getResourceMetaData().getSyncPreMatchAttribute().trim().length() > 0 )){
			String preMatchAttributeName = resource.getResourceMetaData().getSyncPreMatchAttribute();
			String preMatchAttributeValue = resource.getAttributeList().get(preMatchAttributeName).toString();
			
			if (configService.getAttribute(session, configObject,  preMatchAttributeName)!=null){
				Object preMatchAttributeConfigValue =  configService.getAttribute(session, configObject,  preMatchAttributeName).toString();					
			
				boolean isReference = ResourceHelper.isAttributeReference(configService.getAttributesMetaInfo(resource.getName()),preMatchAttributeName);
				String attributeType = ResourceHelper.getAttributeType(configService.getAttributesMetaInfo(resource.getName()),preMatchAttributeName);
				if (isReference ){
					Resource referenceMatchResource= getMatchingResourceReferenceForConfigObject(configService,session,referencedResources, new ObjectName(preMatchAttributeConfigValue.toString()),attributeType);
					if(referenceMatchResource == null){
						logger.warn(attributeType + " not supported");
						syncPreMatch = false;
					}else{	
						if (!(PropertiesConstant.VARIABLE_PREFIX + referenceMatchResource.getName() + PropertiesConstant.VARIABLE_SUFFIX).equalsIgnoreCase(preMatchAttributeValue) ){
							logger.trace("Prematch false Config:" + PropertiesConstant.VARIABLE_PREFIX + referenceMatchResource.getName() + PropertiesConstant.VARIABLE_SUFFIX  + " and DAKS:" + preMatchAttributeValue);
							syncPreMatch = false;
						}
					}
				}else{
					if (!(preMatchAttributeValue.equals(preMatchAttributeConfigValue.toString()))) {
						logger.trace("Prematch false Config:" + preMatchAttributeConfigValue + " and DAKS:" + preMatchAttributeValue );
						syncPreMatch = false;
						
					}
					
				}
			}else{
				logger.trace("Prematch false Config value is null for " + preMatchAttributeName + " and DAKS:" + preMatchAttributeValue );
				syncPreMatch = false;
			}
		}
		return syncPreMatch;
	}
	
	public Resource getMatchingResourceReferenceForConfigObject(ConfigService configService,
			Session session, Resource referencedResources,ObjectName configObjectName,String configObjectType )
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{
		
		logger.trace("In Match reference resource for config Object " + configObjectName );		
		
		if ((configObjectName!=null) && (referencedResources.getChildren()!=null)){
			// get root reference XML element's children
			Iterator rootResourceReference = ((Resource)referencedResources.getChildren().get(0)).getChildren().iterator();
			while (rootResourceReference.hasNext()){
				// check if name of current child is same as the object that we are looking for e.g. __ConnectionDefinition_JMS_ConnectionFactory
				Resource childRefVariableResource = (Resource)rootResourceReference.next();
				
				Resource childRefResource = ((Resource)(childRefVariableResource.getChildren().get(0)));
				logger.trace("Matching resource " +  childRefResource.getName() + " to " + configObjectType);

				if (childRefResource.getName().equalsIgnoreCase(configObjectType)){
					
						
						boolean matchingObject = true;
						logger.trace("Getting Attribute List from Java Object ");
						HashMap attributes = childRefResource.getAttributeList();
						Iterator attributeKeys = attributes.keySet().iterator();
						if (attributes.size()>0){
							while(attributeKeys.hasNext() && matchingObject){
								String key = attributeKeys.next().toString();
								logger.trace("Getting Attribute " + key + " from Config Object ");
								String configAttrValue = configService.getAttribute(session, configObjectName,key).toString();
								logger.trace("Checking if " + configAttrValue + " equals " + (attributes.get(key).toString()));
								if (matchingObject && (configAttrValue.equalsIgnoreCase(attributes.get(key).toString()))){
									matchingObject = true;
								}else{
									matchingObject = false;
								}
							}
							if (matchingObject){
								logger.info(" Match for the referenced found " + childRefVariableResource.getName());
								return childRefVariableResource;
							}else{
								
								logger.info(" No Match for the referenced found " );
							}
						}else{
							
							logger.info(" No Match for the referenced found " );
						}
					}
				}
			}
		return null;
	}

	/**
	 * 
	 * @param allResource
	 * @param resourceToMatch
	 * @return
	 */
	public Resource matchResource(Resource allResource ,Resource resourceToMatch ){
		if (allResource.getChildren()!=null){
			
			return matchChildrenResource(allResource.getChildren(),resourceToMatch );
		}
		
		return null;
	}
	
	/**
	 * Match the resource in the resource tree provided
	 * @param children
	 * @param resourceToMatchString
	 * @return
	 */
	private Resource matchChildrenResource(Vector<Resource> children,Resource resourceToMatchString  ){
		
		String resourceType = resourceToMatchString.getName();  
		
		String matchAttributeValue = null;
		
		String matchAttributeName = resourceToMatchString.getResourceMetaData().getMatchAttribute();
		if (matchAttributeName==null){
			matchAttributeName = resourceToMatchString.getResourceMetaData().getContainmentAttribute();
		}
		//System.out.println("Match Attr is " + matchAttributeName );
		 
		if (resourceToMatchString.getAttributeList().get(matchAttributeName) != null){
			matchAttributeValue =resourceToMatchString.getUnresolvedAttributeList().get(matchAttributeName).toString(); 
		}

		
		Resource matchResource = null;
		
		for (int i=0; i < children.size();i++){
			Resource childResource = (Resource)children.get(i);
			if (childResource.getName().equalsIgnoreCase(resourceType)){
				if (matchAttributeName == null || matchAttributeName.equalsIgnoreCase("null") ){
					matchResource = childResource;
				}else if (childResource.getUnresolvedAttributeList().get(matchAttributeName) !=null){
					if (childResource.getUnresolvedAttributeList().get(matchAttributeName).toString().equalsIgnoreCase(matchAttributeValue)){
						// Check if there is additional match attribute, e.g. in case of EARApplication each resource can be have more then one attribute as
						// match attribute
						if (resourceToMatchString.getResourceMetaData().getAdditionalContainmentAttribute()!=null){
							String [] matchAttributes = resourceToMatchString.getResourceMetaData().getAdditionalContainmentAttribute();
							boolean match = true;
							for (int x=0 ; (x < matchAttributes.length && match); x++){
								String addMatchVal = null;
								String addMatchVal1 = null;
								if (resourceToMatchString.getUnresolvedAttributeList().get(matchAttributes[x])!=null){
									addMatchVal = resourceToMatchString.getUnresolvedAttributeList().get(matchAttributes[x]).toString();
								}
								if (childResource.getUnresolvedAttributeList().get(matchAttributes[x])!=null){
									addMatchVal1 = childResource.getUnresolvedAttributeList().get(matchAttributes[x]).toString();
								}
								if (addMatchVal!=null){
									if (!addMatchVal.equalsIgnoreCase(addMatchVal1)){
										match = false;
									}
								}
							}
							if (match){
								matchResource = childResource;
							}
						}else{
							boolean parentMatch = false;
							// check that parent tree of the matched resources is same as parent tree of resourcetobematched
							
							Resource resourceToMatchParent = resourceToMatchString.getParent();
							Resource sourceParentResource = childResource.getParent();
							
							while (!resourceToMatchParent.getName().equalsIgnoreCase(ResourceConstants.REQUEST)){
								if (matchResource(sourceParentResource.getParent(), resourceToMatchParent )!=null){
									parentMatch = true;
								} else{
									parentMatch = false;
									break;
								}
								resourceToMatchParent = resourceToMatchParent.getParent();
								sourceParentResource = sourceParentResource.getParent();
								System.out.println( "passing parent as " + sourceParentResource.getContainmentPath());
								System.out.println( " resourceToMatchParent passing parent as " + resourceToMatchParent.getContainmentPath());
								
							}
							if (parentMatch){
								matchResource = childResource;
								break;
							}
						}
					}
				}
			}else{
				if (childResource.getChildren()!=null){
					matchResource =  matchChildrenResource(childResource.getChildren(), resourceToMatchString );
					if (matchResource !=null){
						return matchResource ;
					}
				}
			} 
		} 

		return matchResource;	
	}

	
}
