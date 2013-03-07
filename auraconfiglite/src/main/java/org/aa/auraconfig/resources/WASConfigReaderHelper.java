package org.aa.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.customcode.CustomCodeManager;
import org.aa.auraconfig.resources.metadata.CommandLinkAttribute;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.aa.auraconfig.resources.parser.ResourceParserHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertiesConstant;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class WASConfigReaderHelper {
	private static final Log logger = LogFactory.getLog(WASConfigReaderHelper.class);

	/**
	 * 
	 * @param session
	 * @param configService
	 * @param childResource
	 * @param configObject
	 * @param referencedResources
	 * @param deployInfo
	 * @param count
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws MalformedObjectNameException
	 * @throws AttributeNotFoundException
	 * @throws DeployException
	 */
	public  void checkIfAnyAttributeIsIncoming (Session session, ConfigService configService, 
			Resource childResource, ObjectName configObject,Resource referencedResources,DeployInfo deployInfo, int count)
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{

		AttributeList attrMetaInfoList = configService.getAttributesMetaInfo( childResource.getName());
		Iterator attrMetaInfoListIterator = attrMetaInfoList.iterator();

		
		HashMap incomingAttributeList = new HashMap();
		while (attrMetaInfoListIterator.hasNext()){
			
			Attribute configObjectAttributeMetaInfo = (Attribute)attrMetaInfoListIterator.next();
			String attributeName = configObjectAttributeMetaInfo.getName();
			// this is used when an already existing resource needs to be checked for incoming changes.
			// if we are checking an existing resource in the
			logger.trace(" >>> Start Checking attribute for existing resource " + attributeName );
			
			if (childResource.getAttributeList().get(attributeName)==null){
				if (!ResourceHelper.isOnIgnoreList(attributeName) ){
					//logger.trace(" >>> Start Checking attribute for existing resource " + attributeName );
					Object attributeValue = configService.getAttribute(session,configObject,attributeName);
					logger.trace(" Checking attribute " + attributeName + " with value " + attributeValue);
					addConfigAttributeToResourceAttributeMap(session, configService, attrMetaInfoList, attributeName,
							incomingAttributeList , childResource, attributeValue, referencedResources,deployInfo, count);

					
				}
			}
			logger.trace(" <<< Finished Checking attribute for existing resource " + attributeName );
		}
		
		HashMap attributeList = childResource.getAttributeList();

		/**
		 * Get extra attributes for custom code
		 */
		if ((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (childResource.getResourceMetaData().getCustomCodeManaged() != null)){
			logger.trace( " Now run custom code to get extra attributes that are required for AdminTask ");

			CustomCodeManager customCodeManager = new CustomCodeManager();
			customCodeManager.process(session,configService,childResource,configObject,deployInfo,incomingAttributeList);
		}
		
		attributeList.putAll(incomingAttributeList ); 

		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
		ArrayList IncomingAttrList = resourceDiffReportHelper.getDiffAttributesForIncoming(incomingAttributeList);
		if (childResource.getModifiedAttributes()==null)
			childResource.setModifiedAttributes(IncomingAttrList);
		else
			childResource.getModifiedAttributes().addAll(IncomingAttrList);

	}


	/**	private boolean existsOnIgnoreList(String attributeName){
		boolean exists = false;
		String[] ignoreList = ResourceConstants.SYNC_IGNORE_ATTRIBUTE_LIST;
		for (int i=0; i<ignoreList.length;i++){
			if (ignoreList[i].toString().equalsIgnoreCase(attributeName)){
				logger.error("Attribute name " + attributeName + " is on ignore list");
				return true;
			}
		}
		return exists; 
	}
	**/
	
	/**
	 * 
	 */
	public void addConfigAttributeToResourceAttributeMap(Session session, ConfigService configService,AttributeList configObjectAttributeMetaInfoList,
				String attributeName, HashMap attributeList ,Resource newResource, Object attributeValue,Resource referencedResources,DeployInfo deployInfo, int count)
			throws ConnectorException,AttributeNotFoundException,ConfigServiceException,DeployException,MalformedObjectNameException{
		
		ResourceHelper resourceHelper = new ResourceHelper();
		ResourceFinder resourceFinder = new ResourceFinder();
		ResourceMetaData defaultResourceMetaData =  new ResourceMetaData();
		
		if (!ResourceHelper.isOnIgnoreList(attributeName) && (newResource.getResourceMetaData()!=null)){
			
			String attributeType = ResourceHelper.getAttributeType(configObjectAttributeMetaInfoList,attributeName);
			Object defaultAttributeValue =  ResourceHelper.getDefaultAttributeValue(configObjectAttributeMetaInfoList,attributeName);
			boolean isReference = ResourceHelper.isAttributeReference(configObjectAttributeMetaInfoList,attributeName);
			boolean isCollection = ResourceHelper.isCollection(configObjectAttributeMetaInfoList,attributeName);
			LinkAttribute linkAttribute = ResourceHelper.getLinkAttribute(newResource, attributeName);
			
			defaultResourceMetaData.setType(attributeType);
			defaultResourceMetaData.setIsArray(isCollection);
			defaultResourceMetaData.setParent(newResource.getResourceMetaData());
			defaultResourceMetaData.setParentTree(newResource.getParentTree());
			// TODO: This has to be intelligent. Change this
			defaultResourceMetaData.setContainmentAttribute("null");
			defaultResourceMetaData.setContainmentPath("null");
			//defaultResourceMetaData.setFindAndResolve(true);
			logger.trace(" resource type " + newResource.getResourceMetaData().getType() + " should include all children from resource metadata "  + newResource.getResourceMetaData().isShouldIncludeAllChildren());
			logger.trace( " attributeName " + attributeName +" isCollection  " + isCollection  );
			logger.trace( " attributeName " + attributeName +" isReference  " + isReference  );
			if (linkAttribute != null){
				logger.trace( " attributeName " + attributeName +" linkAttribute True"  );
			}else{
				logger.trace( " attributeName " + attributeName +" linkAttribute False"  );
			}
			
			if ((linkAttribute != null) && attributeValue!=null){
				// When we have a case where value of link attribute is an array
				if ((isCollection) && (((ArrayList)attributeValue).size() > 0)){
					
					logger.trace("In type " + newResource.getName() + " " + attributeName + " is a Link attribute with value " + attributeValue);
					attributeValue = getLinkAttributeValue(session,configService,linkAttribute,attributeValue.toString());
					
					if (!((defaultAttributeValue!=null) && defaultAttributeValue.toString().equalsIgnoreCase(attributeValue.toString()))){
						attributeList.put(attributeName, attributeValue);
					}
				}
			/**
			 * This is a check for the types like chain and customeservice attributes for JVM where 
			 * the attribute is a reference and collection.
			 */	
			}else if (isReference && !isCollection){
	
				logger.trace("********************************* It is a reference " + newResource.getName() );
				logger.trace("********************************* Call Matching reference resource " + newResource.getName() );
	
				//SDLog.log( "" + attributeValue);
				
				Resource referenceMatchResource= resourceFinder.getMatchingResourceReferenceForConfigObject
						(configService,session,referencedResources,(ObjectName)attributeValue,attributeType);
				if (referenceMatchResource != null){
					attributeList.put(attributeName, PropertiesConstant.VARIABLE_PREFIX + referenceMatchResource.getName() + PropertiesConstant.VARIABLE_SUFFIX);
				}else{
					logger.trace("WARNING: " + " attributeName [ " + attributeName  + " ] of type " + newResource.getContainmentPath() + " not supported");
					SDLog.log("WARNING: " + " attributeName [ " + attributeName + " ] of type " + newResource.getContainmentPath() + " not supported");
				}
	
		//		value  = getReferencedConfigObjectName(variableName, scope, referencedResources);
	
		//		String variableName = ResourceHelper.getVariableName(value.toString());
				
			}else if (attributeType.equalsIgnoreCase("long")){
				
				if (!((defaultAttributeValue!=null) && ( new Long (defaultAttributeValue.toString()).longValue() == (new Long (attributeValue.toString())).longValue()))){
					logger.trace("Adding long attribute value " + attributeValue + " to attribute " + attributeName + " type long");
					attributeValue  = new Long (attributeValue.toString());
					attributeList.put(attributeName, attributeValue);
				}
			
			}else if (attributeType.equalsIgnoreCase("boolean")){
	
				if (!((defaultAttributeValue!=null) && ( new Boolean(defaultAttributeValue.toString()).booleanValue() == (new Boolean (attributeValue.toString())).booleanValue()))){
					logger.trace("Adding boolean attribute value " + attributeValue + " to attribute " + attributeName + " type boolean");
					attributeValue  = new Boolean (attributeValue.toString());
					attributeList.put(attributeName, attributeValue);
				}
	
			}else if (attributeType.equalsIgnoreCase("int")){
	
				if (!((defaultAttributeValue!=null) && ( new Integer(defaultAttributeValue.toString()).intValue() == (new Integer (attributeValue.toString())).intValue()))){
					logger.trace("Adding init attribute value " + attributeValue + " to attribute " + attributeName + " type int");
					attributeValue  = new Integer (attributeValue.toString());
					attributeList.put(attributeName, attributeValue);
				}
			}else if (isReference && !isCollection){
				logger.trace("Adding Reference (not collection) attribute value " + attributeValue + " to attribute " + attributeName + " type reference");
				attributeValue  = (ObjectName)attributeValue;
				attributeList.put(attributeName, attributeValue);
				
			}else if ((new Boolean(isCollection)).booleanValue() && (attributeType.equalsIgnoreCase("String"))){
				logger.trace("Adding String attribute value " + attributeValue + " to attribute " + attributeName + " type string collection");
				attributeValue = ResourceHelper.getStringFromArrayList((ArrayList)attributeValue);
				attributeList.put(attributeName, attributeValue);
	
			}else if (attributeType.equalsIgnoreCase("String")){
				if (attributeValue!=null){
					attributeValue  = attributeValue.toString();
					if (!((defaultAttributeValue!=null) && ( defaultAttributeValue.toString().equalsIgnoreCase(attributeValue.toString())))){
						logger.trace("Adding attribute value " + attributeValue + " to attribute " + attributeName + " type string ");
						attributeList.put(attributeName, attributeValue.toString());
					}
	
				}
	
			}else if (attributeType.equalsIgnoreCase("ENUM")){
				//TODO ENUM is not taken care of.
				if ((defaultAttributeValue!=null) && !( defaultAttributeValue.toString().equalsIgnoreCase(attributeValue.toString()))){
					logger.trace("Adding ENUM attribute value " + attributeValue + " to attribute " + attributeName + " type enum");
					attributeValue  = attributeValue.toString();
					attributeList.put(attributeName, attributeValue);
				}
				
			}else if (attributeType == null){
				//TODO Type is null
					SDLog.log(" 							TODO Type is null. ");
			}else if (isCollection &&  !isReference && // if is collection but not a reference
						(newResource.getResourceMetaData().isShouldIncludeAllChildren() || deployInfo.isIncludeAllChildren() || (resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName) != null))){
				logger.trace("Adding collection attribute value " + attributeValue + " to attribute " + attributeName + " type collection and not reference");
				ArrayList attributeArrayList = (ArrayList)attributeValue;
				Iterator attributeIterator = attributeArrayList.iterator();
				logger.trace("Got " + attributeArrayList.size() + " resource of type "+ attributeType + ", Its a collection ");
				int cnt = 0;
				while(attributeIterator.hasNext()){
					
					AttributeList attributeConfigObject = (AttributeList)attributeIterator.next();
					
					if (attributeConfigObject!=null){
						
						
						
						String newResourceType = ConfigServiceHelper.getAttributeValue(attributeConfigObject , "_Websphere_Config_Data_Type").toString();
						// we will check if the resources that we are dealing with already has an child resource of the 
						// type that we are going to create, if yes then don't create else create
						
						// and also check if the resource of this is there in the source so as to avoid sending all the child resource 
						// that are not required
						
						// in addition if resource is of need by command manager then send it.
						
						if ((getMatchingChildResourceForConfigObject(configService, session, newResource, 
								ConfigServiceHelper.createObjectName(attributeConfigObject), 0, referencedResources, 
								attributeName,newResourceType,deployInfo, count)==null)
								
								&& (ResourceHelper.doesResourceHaveAnyChildOfType(attributeType,attributeName,newResource,deployInfo))
								|| (resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName) != null)
								){
							
								createNewResourceFromList(session,configService,newResource,attributeConfigObject,newResourceType,cnt,referencedResources,deployInfo,attributeName,defaultResourceMetaData);
								cnt ++;
								
						}
						
					}
				}
			}else if (isCollection && isReference && (deployInfo.isIncludeAllChildren() || (resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName) !=null))){
				logger.trace("Adding collection and reference attribute value " + attributeValue + " to attribute " + attributeName + " type collection and reference");
				ArrayList attributeArrayList = (ArrayList)attributeValue;
				Iterator attributeIterator = attributeArrayList.iterator();
				logger.trace("Got " + attributeArrayList.size() + " resource of type "+ attributeType + ", Its a collection ");
				int cnt = 0;
				while(attributeIterator.hasNext()){
					
					ObjectName attributeConfigObject = (ObjectName)attributeIterator.next();
					
					if (attributeConfigObject != null){
						
						if ((ResourceHelper.doesResourceHaveAChildOfType(attributeType,attributeName,newResource)==null)&& (ResourceHelper.doesResourceHaveAnyChildOfType(attributeType,attributeName,newResource,deployInfo))){
							//createNewResource(session,configService,newResource,attributeConfigObject,referencedResources,deployInfo,attributeName);
							logger.trace(" Getting the type of the resource by fetching attribute _Websphere_Config_Data_Type " + attributeConfigObject.getCanonicalName() );
							
							String newResourceType = configService.getAttribute(session, attributeConfigObject , "_Websphere_Config_Data_Type").toString();
							logger.trace(" Got resource the type of the resource by fetching attribute _Websphere_Config_Data_Type " + attributeConfigObject.getCanonicalName() + " is " + newResourceType );
							resourceHelper.createSkeletonResource(newResourceType , newResource, null);
								
						}
					}
				}
			}else if (attributeType.equalsIgnoreCase("J2EEResourcePropertySet")){
				
				if (attributeValue != null){
					Resource j2eeResourcePropertySetResource = null; 
					if (getMatchingChildResourceForConfigObject(configService, session, newResource, ConfigServiceHelper.createObjectName((AttributeList)attributeValue), 0, referencedResources, attributeName,"J2EEResourcePropertySet",deployInfo, count)!=null){
						SDLog.log("Getting J2EEPropertySet from resources");
						j2eeResourcePropertySetResource = getMatchingChildResourceForConfigObject(configService, session, newResource, ConfigServiceHelper.createObjectName((AttributeList)attributeValue), 0, referencedResources, attributeName,"J2EEResourcePropertySet",deployInfo, count);
						
					}else{
	
						j2eeResourcePropertySetResource = resourceHelper.createSkeletonResource("J2EEResourcePropertySet", newResource,defaultResourceMetaData);
	
					}
	
					ArrayList arrayList = (ArrayList) ConfigServiceHelper.getAttributeValue((AttributeList)attributeValue, "resourceProperties"); 
					Iterator arrayListIterator = arrayList.iterator();
					int cnt = 0;
					
					while (arrayListIterator.hasNext() ){
						AttributeList  propertyAttributeList = (AttributeList)arrayListIterator.next();
						ObjectName propertyObjectName = ConfigServiceHelper.createObjectName(propertyAttributeList);
						
						if (getMatchingChildResourceForConfigObject(configService, session, j2eeResourcePropertySetResource, 
								propertyObjectName,cnt, referencedResources, 
								attributeName,"J2EEResourceProperty",deployInfo,count)==null){
							cnt ++;
							
							createNewResourceFromList(session,configService,j2eeResourcePropertySetResource, 
									propertyAttributeList, "J2EEResourceProperty",cnt,referencedResources,deployInfo,"",defaultResourceMetaData);
						}
						//j2eeResourcePropertySetResource.addInComingChildren(j2eePropertyResources);
						
					}
					
					
					
					
				}
				
			}else if (deployInfo.isIncludeAllChildren() || (resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName) !=null)){
				logger.trace("Adding attribute value " + attributeValue + " to attribute " + attributeName + " type arraylist or config object");	
				
				try{
					ObjectName attributeConfigObject = (ObjectName)attributeValue;
					if (attributeConfigObject != null){
						
						if ((ResourceHelper.doesResourceHaveAChildOfType(attributeType,attributeName,newResource)==null)&& (ResourceHelper.doesResourceHaveAnyChildOfType(attributeType,attributeName,newResource,deployInfo))){
							createNewResource(session,configService,newResource,attributeConfigObject,referencedResources,deployInfo,attributeName,count);
							//newResource.addInComingChild(newResourceForAttribute);	
						}
					}
	
				}catch(ClassCastException e){
					logger.debug(" ************** Beware it is Attribute List for attribute . " + attributeName);
					
					if (attributeValue != null){
				//		SDLog.log("Creating a resource for " + attributeType+ " with value " + attributeValue);
	
						Resource newAttributeResource = new Resource();
	
						String newAttributeResourceType =  ConfigServiceHelper.getAttributeValue((AttributeList)attributeValue , "_Websphere_Config_Data_Type").toString();
						newAttributeResource.setName(newAttributeResourceType);
	
						if ((ResourceHelper.doesResourceHaveAChildOfType(newAttributeResourceType,attributeName,newResource)==null)
								&& (ResourceHelper.doesResourceHaveAnyChildOfType(attributeType,attributeName,newResource,deployInfo))
								|| (resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName) !=null))
							
							createNewResourceFromList(session,configService,newResource,(AttributeList)attributeValue , newAttributeResourceType,0,referencedResources,deployInfo,attributeName,defaultResourceMetaData);
						//j2eeResourcePropertySetResource.addInComingChildren(j2eePropertyResources);
							
						//newResource.addInComingChild(j2eeResourcePropertySetResource);
					}					
	
				}
			}
		}else{
			logger.warn("Attribute Name " + attributeName + "  for "+ newResource.getName() + " with containment path " +  newResource.getContainmentPath() + " not supported or on exclude list."  );
		}
	}


	/**
		 * Creating a new resource which is of same type as peer resource passed in.
		 * So the meta data for both resources is same.
		 * @param session
		 * @param configService
		 * @param peerResource
		 * @param configObject
		 * @param referecedResources
		 * @param deployInfo
		 * @param resourceAttributeName
		 * @return
		 * @throws ConfigServiceException
		 * @throws ConnectorException
		 * @throws AttributeNotFoundException
		 * @throws DeployException
		 * @throws MalformedObjectNameException
		 */
		public  Resource createNewResource(Session session, ConfigService configService, Resource peerResource, 
				ObjectName configObject,Resource referecedResources,DeployInfo deployInfo,String resourceAttributeName, int count)
			throws ConfigServiceException, ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException{
	
	//		System.out.println("************** Will create a new resource as the resource from WAS does not exists in the XML, also will check if the resource to be created is CommandManaged");
	//		System.out.println("************** Is Resource Command Managed " + peerResource.getResourceMetaData().isCommandManaged());
			ResourceHelper resourceHelper = new ResourceHelper();
			Resource  newResource = new Resource();
			newResource.setName(peerResource.getName());
			newResource.setIncoming(true);
			newResource.setParent(peerResource.getParent());
			newResource.setParentTree(peerResource.getParentTree());
			newResource.setResourceMetaData(peerResource.getResourceMetaData());
			
			newResource.setContainmentPath(peerResource.getContainmentPath());
			AttributeList metaInfo =  configService.getAttributesMetaInfo(newResource.getName());
			HashMap map = ResourceHelper.getResourceAttributeMetaData(metaInfo);
			newResource.setResourceAttrMetaInfo(map);
	
			/**
			 * If the containment path required a attribute value to be appended then do this here
			 */
			
			if (!newResource.getResourceMetaData().getContainmentAttribute().equalsIgnoreCase("null")){
				if (newResource.getContainmentPath().endsWith("=")){
					newResource.setContainmentPath(newResource.getContainmentPath()+ configService.getAttribute(session, configObject, newResource.getResourceMetaData().getContainmentAttribute()) );
				}else{
					newResource.setContainmentPath(newResource.getContainmentPath()+ "="+configService.getAttribute(session, configObject, newResource.getResourceMetaData().getContainmentAttribute()) );
				}
				ResourceParserHelper resourceParserHelper = new ResourceParserHelper();
				resourceParserHelper.setContainmentPath(newResource);
				
			}
			logger.trace(">>> Creating new resource " + newResource.getName()  + " for parent " + peerResource.getParent().getContainmentPath());
	
			/**
			 * Get attributes names and values from config object for the new resource
			 */	
			AttributeList configObjectAttributeMetaInfoList = configService.getAttributesMetaInfo(peerResource.getName());
			Iterator configObjectAttributeMetaInfoIterator = configObjectAttributeMetaInfoList.iterator();
			
			HashMap<String, String> inComingAttributeList = new HashMap<String, String>();
			
			while (configObjectAttributeMetaInfoIterator.hasNext()){
				Attribute configObjectAttributeMetaInfo = (Attribute)configObjectAttributeMetaInfoIterator.next();
	
				String attributeName = configObjectAttributeMetaInfo.getName();
				if (!ResourceHelper.isOnIgnoreList(attributeName) ){
				
					Object attributeValue = configService.getAttribute(session,configObject,attributeName);
					
					addConfigAttributeToResourceAttributeMap(session,configService,configObjectAttributeMetaInfoList,attributeName,
							inComingAttributeList,newResource,attributeValue,referecedResources,deployInfo,count);
				
				
					// check if this attribute is used by command link. If it is used then get it populated.
					if ((deployInfo.getVersionInfo().getMajorNumber() >=2)){
						CommandLinkAttribute commandLinkAttribute = resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName);
						if (commandLinkAttribute != null ){
							
							java.util.Vector<Resource> children  = newResource.getInComingChildren();
							if (children!=null){
								for (int i=0 ; i < children.size(); i++){
									Resource child = (Resource)children.get(i);
									if (child.getResourceMetaData().getType().equalsIgnoreCase(commandLinkAttribute.getTargetObjectType())){
										String value = child.getAttributeList().get(commandLinkAttribute.getTargetObjectAttribute()).toString();
										logger.trace(" +++++++++++ Will add " + commandLinkAttribute.getLinkAttibuteName() + " value " + value);
										inComingAttributeList.put(commandLinkAttribute.getLinkAttibuteName(), value);
									}
								}
							}
						}
					}
				}
			}
			
			/**
			 * Get extra attributes for custom code
			 */
			if ((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (newResource.getResourceMetaData().getCustomCodeManaged() != null)){
				logger.trace( " Now run custom code to get extra attributes that are required for AdminTask ");
				CustomCodeManager customCodeManager = new CustomCodeManager();
				customCodeManager.process(session,configService,newResource,configObject,deployInfo,inComingAttributeList);
			}
			
			logger.trace("Setting attributeList");
			newResource.setAttributeList(inComingAttributeList);
	
			
			
	//		SDLog.log(newResource.getContainmentPath());
			
			if (newResource.getResourceMetaData().isAttributeNameInResourceXML()){
				newResource.getAttributeList().put(ResourceConstants.ATTRUBUTENAME, resourceAttributeName);
			}
	
			logger.trace("<<< Created new resource " + newResource.getAttributeList().get(newResource.getResourceMetaData().getContainmentAttribute()));
			return newResource;	
			
		}


	/**
	 * 
	 * @param session
	 * @param configService
	 * @param parentResource
	 * @param configAttributeList
	 * @param configObjectType
	 * @param cnt
	 * @param referencedResources
	 * @param deployInfo
	 * @param resourceAttributeName
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws AttributeNotFoundException
	 * @throws DeployException
	 * @throws MalformedObjectNameException
	 */
	public void createNewResourceFromList(Session session,ConfigService configService,Resource parentResource,
			AttributeList configAttributeList,String configObjectType,int cnt,
			Resource referencedResources,DeployInfo deployInfo,String resourceAttributeName,ResourceMetaData defaultResourceMetaData)
		throws ConfigServiceException,ConnectorException,AttributeNotFoundException,DeployException,MalformedObjectNameException{
		
	//	ArrayList newResourceArrayList = new ArrayList();
		logger.trace(">>> " + (cnt+1) + " Creating new resource from List " + configObjectType  + " for parent " + parentResource.getContainmentPath());
		ResourceHelper resourceHelper = new ResourceHelper();
	
		Resource  newResource = resourceHelper.createSkeletonResource(configObjectType,parentResource,defaultResourceMetaData);
		
		if (newResource !=null){
			AttributeList metaInfo =  configService.getAttributesMetaInfo(newResource.getName());
			HashMap map = ResourceHelper.getResourceAttributeMetaData(metaInfo);
			newResource.setResourceAttrMetaInfo(map);
	
			
			logger.trace("Getting Metadata info for type " + configObjectType);
			AttributeList configObjectAttributeMetaInfoList = configService.getAttributesMetaInfo(configObjectType);
			Iterator attributeMetaInfoIterator = configObjectAttributeMetaInfoList.iterator();
	
			/**
			 * Add the containment path first before processing other attributes, because if any other attribute's value is
			 * is resource then it will parent containment path and this path is not complete then child's containment path 
			 * will also be not complete.
			 * 
			 */
			if (!newResource.getResourceMetaData().getContainmentAttribute().equalsIgnoreCase("null")){
				if (newResource.getContainmentPath().endsWith("=")){
					newResource.setContainmentPath(newResource.getContainmentPath()+ ConfigServiceHelper.getAttributeValue(configAttributeList,newResource.getResourceMetaData().getContainmentAttribute()));
				}else{
					newResource.setContainmentPath(newResource.getContainmentPath()+"="+ConfigServiceHelper.getAttributeValue(configAttributeList,newResource.getResourceMetaData().getContainmentAttribute()));
				}
				ResourceParserHelper resourceParserHelper = new ResourceParserHelper();
				resourceParserHelper.setContainmentPath(newResource);
	
			}
	
			
			HashMap inComingAttributeList = new HashMap();
	
			while (attributeMetaInfoIterator.hasNext()){
				Attribute attributeMetaInfo = (Attribute)attributeMetaInfoIterator.next();
				
				String attributeName = attributeMetaInfo.getName();
				logger.trace("Getting Config Value for type " + configObjectType + " attribute name " + attributeMetaInfo.getName());
				if (!ResourceHelper.isOnIgnoreList(attributeName) ){
					Object attributeValue = ConfigServiceHelper.getAttributeValue(configAttributeList,attributeMetaInfo.getName());
					logger.trace("Got Config Value for type " + configObjectType + " attribute name " + attributeMetaInfo.getName() +" value is " + attributeValue );
	
					if (attributeValue !=null){
						addConfigAttributeToResourceAttributeMap(session,configService,configObjectAttributeMetaInfoList,attributeName,
								inComingAttributeList,newResource,attributeValue,referencedResources,deployInfo,cnt);
					}else{
						/**
						 * It is possible for J2EEResourceProperty's attributes to have a 
						 * null value so do not warn.
						 */
						if (!newResource.getName().equalsIgnoreCase("J2EEResourceProperty")){
						logger.warn("WARNING: Config Value for type " + configObjectType + " attribute name " + attributeMetaInfo.getName() + " is null");
						//SDLog.log("WARNING: Config Value for type " + configObjectType + " attribute name " + attributeMetaInfo.getName() + " is null");
						}
					}
					
					// check if this attribute is used by command link. If it is used then get it populated.
					CommandLinkAttribute commandLinkAttribute = resourceHelper.isThisLinkAttributeForCommand(newResource, attributeName);
					if (commandLinkAttribute != null ){
						
							Vector<Resource> children  = newResource.getInComingChildren();
							for (int i=0 ; i < children.size(); i++){
								Resource child = (Resource)children.get(i);
								
								logger.trace(" Matching " + child.getResourceMetaData().getType() + " to "  + commandLinkAttribute.getTargetObjectType());
								
								if (child.getResourceMetaData().getType().equalsIgnoreCase(commandLinkAttribute.getTargetObjectType())){
									String value = child.getAttributeList().get(commandLinkAttribute.getTargetObjectAttribute()).toString();
									inComingAttributeList.put(commandLinkAttribute.getLinkAttibuteName(), value);
									logger.trace(" +++++++++++ Will add " + commandLinkAttribute.getLinkAttibuteName() + " value " + value);
								}
							}
					}
	
				}
			}
	
			
			/**
			 * Get extra attributes for custom code
			 */
			if((deployInfo.getVersionInfo().getMajorNumber() >=2 ) && (newResource.getResourceMetaData().getCustomCodeManaged() != null)){
				logger.trace( " Now run custom code to get extra attributes that are required for AdminTask ");
				CustomCodeManager customCodeManager = new CustomCodeManager();
				customCodeManager.process(session,configService,newResource,null,deployInfo,inComingAttributeList);
			}
			
			newResource.setAttributeList(inComingAttributeList);
			
	
			if (newResource.getResourceMetaData().isAttributeNameInResourceXML()){
				
				newResource.getAttributeList().put(ResourceConstants.ATTRUBUTENAME, resourceAttributeName);
			}
	
		//	newResourceArrayList.add(newResource);
			if (newResource.getResourceMetaData()!=null){
				logger.trace("<<< " + (cnt +1) + " Created new resource from List " + configObjectType  + " "+ newResource.getAttributeList().get(newResource.getResourceMetaData().getContainmentAttribute()));
			}
		}
		
	
		//return newResourceArrayList;
	}


	/**
	 * When data is read from the config repository and AURA comes across a link attribute value
	 * then this method is used to find the resources name to link back to AURA value.
	 * 
	 * @param session
	 * @param configService
	 * @param linkAttribute
	 * @param attributeValue
	 * @return
	 * @throws AttributeNotFoundException
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws DeployException
	 * @throws MalformedObjectNameException
	 */
	public Object getLinkAttributeValue(Session session, ConfigService configService, LinkAttribute linkAttribute, 
			String attributeValue )
		throws AttributeNotFoundException,ConfigServiceException,ConnectorException,DeployException,MalformedObjectNameException{
		
		logger.trace("Will try to match the attribute value " + attributeValue + " to target Link object");
		String linkAttributeValue = "Missing"; 
		String targetAttribute = linkAttribute.getTargetAttribute();
		String targetObject = linkAttribute.getTargetObject();
		String targetMatchAttribute = linkAttribute.getTargetObjectMatchAttributeName();
		
		ResourceHelper resourceHelper = new ResourceHelper();
		logger.trace("Getting object from config for target object type " + targetObject);
		ObjectName[] configIDs= resourceHelper.getObjectNames(session, configService, targetObject);
		if (configIDs!=null){
			logger.trace("Got object from config for target object type size " + configIDs.length);
			for (int i = 0; i< configIDs.length; i++){
				if (targetAttribute == null){
					if (configIDs[i].getCanonicalName().equalsIgnoreCase((new ObjectName(attributeValue)).getCanonicalName())){
						
						linkAttributeValue = configService.getAttribute(session, configIDs[i], targetMatchAttribute).toString();
						logger.trace("Link Attribute value is " + linkAttributeValue);
	
						if (linkAttribute.getLinkAttribute()!=null){
							return getLinkAttributeValue(session, configService, linkAttribute.getLinkAttribute(), linkAttributeValue);
						}
						return linkAttributeValue ;
					} 
				}else{
					String configTargetAttributeValue =  configService.getAttribute(session, configIDs[i], targetAttribute).toString();
					logger.trace("Matching configTargetAttributeValue:attributeValue " + configTargetAttributeValue + ":" + attributeValue);
					if (configTargetAttributeValue.equalsIgnoreCase(attributeValue)){
						linkAttributeValue = configService.getAttribute(session, configIDs[i], targetMatchAttribute).toString();
						logger.trace("Link Attribute value is " + linkAttributeValue);
						return linkAttributeValue ;
					} 
				}
			}
		}else{
			logger.trace("Did not get any object from config for target object type " + targetObject);
		}
		
		logger.trace("Link Attribute value is " + linkAttributeValue);
		return linkAttributeValue  ;
		
	}


	/**
	 * Method to get the matching peer resource for the given config Type.
	 * 
	 * This method will try and match the child resources for the config object using 
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
	private Resource getMatchingChildResourceForConfigObject(ConfigService configService,Session session, Resource resource,ObjectName configObject,
			int configObjectCnt,Resource referencedResources,String attributeName,String type,DeployInfo deployInfo, int count )
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{
		
		logger.trace("Will check if child of type " + type + " exists in " + resource.getName() + " " + resource.getContainmentPath() );
		Vector combinedListOfResource = new Vector();
		
		//List resourceChildren = resource.getChildren();
		//configObject
		if (resource.getChildren()!=null) 
			combinedListOfResource.addAll(resource.getChildren());
		
		if (resource.getInComingChildren()!=null)
			combinedListOfResource.addAll(resource.getInComingChildren());
	
		for (int i = 0; i < combinedListOfResource.size(); i++) {
			//SDLog.log( resource.getContainmentPath() +  " " + resource.getChildren().size());
			Resource childResource = (Resource) combinedListOfResource.get(i);
			logger.trace( "(" + i + " ) Matching " + childResource.getName() + " to " + type ); 
					
			if(childResource.getName().equalsIgnoreCase(type) && (childResource.getResourceMetaData()!=null)){
			
				logger.trace ("Getting attribute " + childResource.getResourceMetaData().getContainmentPath().toString() + " from config object " + configObject);
				String matchAttribute = childResource.getResourceMetaData().getContainmentPath().toString();
				
				logger.trace("matchAttribute " + matchAttribute);
				logger.trace("childResource.getResourceMetaData().getMatchAttribute() " + childResource.getResourceMetaData().getMatchAttribute());
				
				if (childResource.getResourceMetaData().getMatchAttribute()!=null){
					matchAttribute = childResource.getResourceMetaData().getMatchAttribute();
				}
				
				logger.trace("matchAttribute " + matchAttribute);
				String attributeValueOfConfigObject = "null";
				// TODO: Add more intelligent code here.
				
				if ((matchAttribute!=null)&& (!matchAttribute.equalsIgnoreCase("null")) && (configService.getAttribute(session,configObject,matchAttribute)!=null)){
					
					attributeValueOfConfigObject = configService.getAttribute(session,configObject,matchAttribute).toString();
				}
				
				LinkAttribute  linkAttribute = ResourceHelper.getLinkAttribute(resource, matchAttribute); 
				if (linkAttribute != null){
					attributeValueOfConfigObject = getLinkAttributeValue (session, configService, linkAttribute, attributeValueOfConfigObject).toString() ;
				}
	
	
				logger.trace("attributeValueOfConfigObject " + attributeValueOfConfigObject);
				logger.trace("childResource.getName() " + childResource.getName());
				logger.trace("matchAttribute " + matchAttribute);
				
				
				if (childResource.getName().equalsIgnoreCase(type)){
					if ((!matchAttribute.equalsIgnoreCase("null")) && (!ResourceHelper.isResourceDummy(childResource, childResource.getResourceMetaData()))){
						String attributeValueOfMatchAttributeInResource = childResource.getAttributeList().get(matchAttribute).toString();
						logger.trace( "(" + (configObjectCnt +1) +  ")	Matching Resource in AURA " + attributeValueOfMatchAttributeInResource + " to " + attributeValueOfConfigObject ); 
						
						if ((!(attributeValueOfConfigObject.equalsIgnoreCase("null")) && (attributeValueOfConfigObject.equalsIgnoreCase(attributeValueOfMatchAttributeInResource)))) {
							count++;
							logger.trace( "                 Matching found Resource in AURA " + attributeValueOfMatchAttributeInResource + " to " + attributeValueOfConfigObject  );
							if (childResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME)!=null){
								logger.trace( "                 Need to match attribute name as well " +childResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME));
								if (childResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString().equalsIgnoreCase(attributeName)){
									logger.trace("There is a child resource of type " + childResource.getName() + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
									i = combinedListOfResource.size();
									checkIfAnyAttributeIsIncoming (session,configService,childResource,configObject,referencedResources,deployInfo,count);
									return childResource;
		
								}else{
									logger.trace( "                 Match attribute failed name as well " +childResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME) + " to " + attributeName);
								}
							}else{
								logger.trace("There is a child resource of type " + childResource.getName() + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
								i = combinedListOfResource.size();
								checkIfAnyAttributeIsIncoming (session,configService,childResource,configObject,referencedResources,deployInfo,count);
								return childResource;
								
							}
						}
					}else{
						logger.trace("There is a child resource of type " + childResource.getName() + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
						i = combinedListOfResource.size();
						checkIfAnyAttributeIsIncoming (session,configService,childResource,configObject,referencedResources,deployInfo,count);
						return childResource;
					}
				}
			}
		}
		//logger.info("	Match Not found: " + resource.getName() + " identifier " + attributeValueOfConfigObject);
		// SDLog.log("	Match Not found: " + resource.getName() + " identifier " + attributeValueOfConfigObject);
		logger.trace("There is no child resource of type " + type + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
		return null;
	
	
		
		
		//return getMatchingResourceFromThisListOfResourceForTheConfigObject(configService,session, resource,configObject,				configObjectCnt,referencedResources,attributeName,resourceChildren );
	}


	/**
	 * 
	 * @param objectType
	 * @param attributeName
	 * @param attributeValue
	 * @param configService
	 * @param session
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public ArrayList<ObjectName> getMatchingWASObjects(String objectType, String attributeName, String attributeValue,
			ConfigService configService, Session session)
			throws ConfigServiceException,ConnectorException{
		
		ArrayList<ObjectName> matchingObjectNameList = new ArrayList<ObjectName>();
		ObjectName[] configIDs = configService.resolve(session, objectType);
		
		for (int i=0 ; i < configIDs.length; i++){
			ObjectName currentObject = (ObjectName) configIDs[i];
			String wasAttributeValue = configService.getAttribute(session, currentObject, attributeName ).toString();
			
			if (attributeValue.equalsIgnoreCase(wasAttributeValue)){
				matchingObjectNameList.add(currentObject);
			}
		}
		return 	matchingObjectNameList;	
	}

	
}
