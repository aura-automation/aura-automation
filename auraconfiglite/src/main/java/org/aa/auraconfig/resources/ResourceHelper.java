/**	   Copyright 


**/
package org.aa.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.metadata.CommandAttribute;
import org.aa.auraconfig.resources.metadata.CommandLinkAttribute;
import org.aa.auraconfig.resources.metadata.ResourceAttributeMetaData;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.aa.auraconfig.resources.metadata.ResourceMetaDataHelper;
import org.aa.auraconfig.resources.parser.ResourceParserHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertiesConstant;
import org.aa.common.properties.helper.PropertyHelper;
import org.aa.common.properties.helper.PropertyLoader;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.cmdframework.CommandException;
import com.ibm.websphere.management.cmdframework.CommandMgrInitException;
import com.ibm.websphere.management.cmdframework.CommandNotFoundException;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.SystemAttributes;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ResourceHelper {

	private static final Log logger  = LogFactory.getLog(ResourceHelper.class);	

	public String  getFullContainmentPathForResource(Resource resource)
		throws DeployException{
		//get the type of resource
		// set containmentPath as empty string
		// call to getContaimentPathForResource and set the containmentPath
		// Loop through the parents of the resource till no parent found
		// call to getContaimentPathForResource for each parent and prefix the containmentPath
		// return full containment path

		String fullContainmentPath = getContainmentPathForResource(resource);
		Resource nextParentResource = resource.getParent();
		while (!nextParentResource.getName().equalsIgnoreCase(ResourceConstants.RESOURCES)) {
			fullContainmentPath = getContainmentPathForResource(nextParentResource) + ":" + fullContainmentPath;
			nextParentResource = nextParentResource.getParent();
		}
		return fullContainmentPath ;
	}

	public String getContainmentPathForResource(Resource resource)
		throws DeployException{
		
		// get the type of the resource
		String type = resource.getName();
		if (resource.getResourceMetaData()==null){
			SDLog.log("Meta Data missing for resource type " + type );
			throw new DeployException(new Exception("Meta Data missing for resource type " + type ));
		} 
		// get MetaData of this resource Type
		ResourceMetaData resourceMetaData = resource.getResourceMetaData();
		// If Object type is property then no need to get containment path
		if 	(!resourceMetaData.getIsProperty()){
			String containmentPath ;
			
			if (resourceMetaData.getReturnAttribute()!=""){
				// get the return attribute for this resource type from MetaData 
				containmentPath = resourceMetaData.getReturnAttribute();
			}else{
				// get the containmentPath for this resource type from MetaData if 
				containmentPath = resourceMetaData.getContainmentPath();
			}
			// get the value of attribute resolved from the ContainmentPath.
			StringBuffer contaimentPathForResource = new StringBuffer(type);
			if (containmentPath.equalsIgnoreCase("null")){
				return contaimentPathForResource.toString() ;
			}else{
				/**
				 * containment should not be null but will be when this is 
				 * dummy resource. We just specify the resource and no attributes
				 */
				String value = "";
				if (resource.getAttributeList().get(containmentPath) !=null){
					value = resource.getAttributeList().get(containmentPath).toString() ;
				}
			// if not findandresolve
				if (!resourceMetaData.isFindAndResolve()){
					// Form the String where containmentPath=Value
					contaimentPathForResource.append("=");
					contaimentPathForResource.append(value);
					
				}
				return contaimentPathForResource.toString();
			}
		}else{
			return "";
		}
	}

	public static String getAttributeType(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{

		return getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_TYPE);
	}

	public static boolean isCollection(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{

		return (new Boolean(getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_IS_COLLECTION))).booleanValue();
	}

	public static boolean isAttributeReference(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{

		return (new Boolean(getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_IS_REFERENCE))).booleanValue();
	}

	public static boolean isInt(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{
		if ("int".equalsIgnoreCase( getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_TYPE))){
			return true;
		}else{
			return false; 
		}
	}
	public static boolean isOnIgnoreList(String attributeName){
		for (int i = 0 ; i < ResourceConstants.ATTRIBUTE_IGNORE_LIST.length ; i++){
			if (ResourceConstants.ATTRIBUTE_IGNORE_LIST[i].equalsIgnoreCase(attributeName)){
				logger.error("Attribute name " + attributeName + " is on ignore list");
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLong(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{
		if ("long".equalsIgnoreCase( getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_TYPE))){
			return true;
		}else{
			return false; 
		}
	}

	public static boolean isBoolean(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{
		SDLog.log( getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_TYPE));
		if ("boolean".equalsIgnoreCase( getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_TYPE))){
			return true;
		}else{
			return false; 
		}
	}

	public static boolean isString(AttributeList metaInfo, String attributeName)
		throws ConfigServiceException,ConnectorException{
		if ("string".equalsIgnoreCase( getAttributeDetail(metaInfo,attributeName,SystemAttributes._ATTRIBUTE_METAINFO_TYPE))){
			return true;
		}else{
			return false; 
		}
	}
	
	public static AttributeList getDefaultAttributeList(AttributeList metaInfo)
		throws ConfigServiceException,ConnectorException{

	AttributeList newAttrList = new AttributeList();
		for (int metaInfoCnt=0; metaInfoCnt< metaInfo.size();metaInfoCnt++){
			String name = ((Attribute)metaInfo.get(metaInfoCnt)).getName();

			Object value = null;
			AttributeList attrList = (AttributeList)((Attribute)metaInfo.get(metaInfoCnt)).getValue();
			for (int attrListCnt=0;attrListCnt < attrList.size();attrListCnt++ ){
				if (((Attribute)attrList.get(attrListCnt)).getName().equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_DEFAULT_VALUE)){
					if (((Attribute)attrList.get(attrListCnt)).getValue() !=null){
						value = (((Attribute)attrList.get(attrListCnt)).getValue().toString());
					}else{
						value = null;
					}
				}
			} 
			logger.trace("Add new Attribute to default attribute list, name:" + name + " value:" + value);
			newAttrList.add(new Attribute(name,value));
		}
		return newAttrList;
		
	}

	public static Object getDefaultAttributeValue(AttributeList metaInfo,String key)
		throws ConfigServiceException,ConnectorException{

		for (int metaInfoCnt=0; metaInfoCnt< metaInfo.size();metaInfoCnt++){
			String name = ((Attribute)metaInfo.get(metaInfoCnt)).getName();
			if (name.equalsIgnoreCase(key)){ 
		
				AttributeList attrList = (AttributeList)((Attribute)metaInfo.get(metaInfoCnt)).getValue();
				for (int attrListCnt=0;attrListCnt < attrList.size();attrListCnt++ ){
					if (((Attribute)attrList.get(attrListCnt)).getName().equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_DEFAULT_VALUE)){
						logger.trace("Get default Attribute Value of " + key + " is " +  ((Attribute)attrList.get(attrListCnt)).getValue());
						return (((Attribute)attrList.get(attrListCnt)).getValue());
					}
				} 
			}
		}
		return null;
	}

	public static HashMap getResourceAttributeMetaData(AttributeList metaInfo)
		throws ConfigServiceException,ConnectorException{
		
		HashMap map	= new HashMap();
		
		for (int metaInfoCnt=0; metaInfoCnt< metaInfo.size();metaInfoCnt++){
			AttributeList attrList = (AttributeList)((Attribute)metaInfo.get(metaInfoCnt)).getValue();
			ResourceAttributeMetaData resourceAttributeMetaData = new ResourceAttributeMetaData();
			
			for (int attrListCnt=0;attrListCnt < attrList.size();attrListCnt++ ){
				Attribute currentAttribute = (Attribute)attrList.get(attrListCnt);
				String attributeName = currentAttribute.getName();
				
				if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_DEFAULT_VALUE)){
					if (currentAttribute.getValue()!=null){
						resourceAttributeMetaData.setDefaultValue(currentAttribute.getValue().toString());
					}
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_DEPRECATED)){
					resourceAttributeMetaData.setDeprecated(((Boolean)currentAttribute.getValue()).booleanValue());
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_ENUM_INFO)){
					logger.trace("Enum info " + currentAttribute.getValue());
					
					resourceAttributeMetaData.setEnumInfo(convertStringArrayFromAttributeList((ArrayList)currentAttribute.getValue()));
					
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_ENUM_TYPE)){
					resourceAttributeMetaData.setEnumType(((String)currentAttribute.getValue()));
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_IS_COLLECTION)){
					resourceAttributeMetaData.setCollection(((Boolean)currentAttribute.getValue()).booleanValue());
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_IS_OBJECT)){
					resourceAttributeMetaData.setObject(((Boolean)currentAttribute.getValue()).booleanValue());
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_IS_REFERENCE)){
					resourceAttributeMetaData.setReference(((Boolean)currentAttribute.getValue()).booleanValue());
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_NAME)){
					resourceAttributeMetaData.setName(((String)currentAttribute.getValue()));
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_REMOVED)){
					resourceAttributeMetaData.setRemoved(((Boolean)currentAttribute.getValue()).booleanValue());
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_TYPE)){
					resourceAttributeMetaData.setType(((String)currentAttribute.getValue()));
				}else if (attributeName.equalsIgnoreCase(SystemAttributes._ATTRIBUTE_METAINFO_SUBTYPES)){
					logger.trace("SUBTYPES " + currentAttribute.getValue());
					//resourceAttributeMetaData.setSubType(((String)currentAttribute.getValue()));
	
				}
			} 
		map.put(resourceAttributeMetaData.getName(), resourceAttributeMetaData);
		}
	return map;
	}
	
	private static ArrayList convertStringArrayFromAttributeList(ArrayList array){
		ArrayList arrayList = new ArrayList();
		for (int i=0 , n= array.size(); i < n ; i++){
			Attribute attr = (Attribute)array.get(i);
			arrayList.add(attr.getName());
		} 
		return arrayList;
	}
	
	/**
	 * 
	 * @param metaInfo
	 * @param attributeName
	 * @param subAttrName
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private static String getAttributeDetail(AttributeList metaInfo, String attributeName,String subAttrName)
		throws ConfigServiceException,ConnectorException{

		for (int metaInfoCnt=0; metaInfoCnt< metaInfo.size();metaInfoCnt++){
			
			if 	(((Attribute)metaInfo.get(metaInfoCnt)).getName().equalsIgnoreCase(attributeName)){
				
				AttributeList attrList = (AttributeList)((Attribute)metaInfo.get(metaInfoCnt)).getValue();
				for (int attrListCnt=0;attrListCnt < attrList.size();attrListCnt++ ){
					if (((Attribute)attrList.get(attrListCnt)).getName().equalsIgnoreCase(subAttrName)){
						return (((Attribute)attrList.get(attrListCnt)).getValue().toString());
					}
				} 
			}
		}
		return "";
	}
	
	/**
	 * Check if the given attribute for this resource is a link to another resource's attribute
	 * Get metadata for this resource
	 * get Link Attribute from meta data.
	 * match given attribute to linkAttributes variable names.
	 * @param resource
	 * @param key
	 * @return
	 */
	public static LinkAttribute getLinkAttribute(Resource resource, String key){
		
		ResourceMetaData metaData =  resource.getResourceMetaData();
		HashMap linkAttributes =  metaData.getLinkAttribute();
		if (linkAttributes !=null){
			Iterator keyIterator =  linkAttributes.keySet().iterator();
			while(keyIterator.hasNext()){
				String linkAttributeName =  keyIterator.next().toString();
				if (linkAttributeName.equalsIgnoreCase(key)){
					logger.trace(" Link Attribute type " + resource.getName() + " attribute + " + key + " is link type" );
					return (LinkAttribute)linkAttributes.get(key) ;
				}
			}
		}
		return null;
	}
	
	public static String getStringFromArrayList(ArrayList myArrayList){
		StringBuffer myStringBuffer = new StringBuffer();
		Iterator arrayIterator = myArrayList.iterator();

		while (arrayIterator.hasNext()){
			String arrayString = (String )arrayIterator.next();
			myStringBuffer.append(arrayString );
			myStringBuffer.append(";");
		}
		if (myStringBuffer.length()>0){
			return myStringBuffer.toString().substring(0, myStringBuffer.length() - 1);
		}else{
			return myStringBuffer.toString();
		}
	}
	
	public static String getResourceIdentifierName(Resource resource){
		String containmentAttr = resource.getResourceMetaData().getContainmentAttribute();
	
		Object identifierName = resource.getAttributeList().get( containmentAttr);
		if (identifierName !=null){
			return identifierName.toString();
		}
		else{
			return null;
		}
		
	}
	
	/**
	 * get ObjectName for the given Containment Path
	 * @param containmentpath
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public ObjectName[] getObjectNames(Session session, ConfigService configService, String containmentpath)
		throws ConfigServiceException,ConnectorException{
		logger.trace("Check if containment path exists :" + containmentpath);
		return configService.resolve(session, containmentpath);
	}
	
	/**
	 * get ObjectName for the given Containment Path in given scope.
	 * @param scope
	 * @param containmentpath
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	public ObjectName[] getObjectNames(Session session, ConfigService configService,String containmentpath,ObjectName scope)
		throws ConfigServiceException,ConnectorException{
		logger.trace("Check if containment path exists :" + containmentpath);
			
		return configService.resolve(session, scope ,containmentpath);
	}

	

	
	public static Resource doesResourceHaveAChildOfType(String type,String attributeName, Resource resource,String matchAttribute, String matchAttributeValue){
		
		if (resource.getChildren()!=null){
			Iterator childrenIterator = resource.getChildren().iterator();
			while (childrenIterator.hasNext()){
				Resource myChildResource = (Resource)childrenIterator.next();
				if (myChildResource.getName().equalsIgnoreCase(type)){
					if (myChildResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME)!=null){
							if (myChildResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString().equalsIgnoreCase(attributeName)){
								logger.trace("There is a child resource of type " + type + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
								return myChildResource;

							}
					}else{
						logger.trace("There is a child resource of type " + type + " in resource " + resource.getName());
						return myChildResource;
					}
				}
			} 
		}
		logger.trace("There is no child resource of type " + type + " in resource " + resource.getName());
		return null;
	}
	
	/**
	 * to check of the resource tree has an entry of dummy or valid resource of the type that we are dealing.
	 * 
	 * This is used to avoid sending child resource if it is not required.
	 * @param type
	 * @param attributeName
	 * @param resource
	 * @param deployInfo
	 * @return
	 */
	public static boolean doesResourceHaveAnyChildOfType(String type,String attributeName, Resource resource,DeployInfo deployInfo){
		if (deployInfo.isIncludeAllChildren() || resource.getResourceMetaData().isShouldIncludeAllChildren() ){
			return true;
		}
		
		if (resource.getParent()!=null){
			Resource parentResource = resource.getParent();
			logger.trace("Parent of " + resource.getName() + " is " + parentResource.getName());
			if (parentResource.getChildren()!=null){
				Iterator childrenIterator = parentResource.getChildren().iterator();
				while (childrenIterator.hasNext()){
					Resource checkChildResource = (Resource)childrenIterator.next();
					logger.trace("Checking child name of " + resource.getName() + " is " + parentResource.getName());
					if (checkChildResource.getName().equalsIgnoreCase(resource.getName())){
						Resource matchResource = doesResourceHaveAChildOfType(type ,null, checkChildResource);
							if( matchResource  !=null){
								return true ;
							}	
						}
					}
				}
			}			
		logger.trace("There is no child resource of type " + type + " in resource " + resource.getName());
		return false;
	}

	public static Resource doesResourceHaveAChildOfType(String type,String attributeName, Resource resource){
		
		if (resource.getChildren()!=null){
			Iterator childrenIterator = resource.getChildren().iterator();
			while (childrenIterator.hasNext()){
				Resource myChildResource = (Resource)childrenIterator.next();
				if (myChildResource.getName().equalsIgnoreCase(type)){
					if (myChildResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME)!=null){
							if (myChildResource.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString().equalsIgnoreCase(attributeName)){
								logger.trace("There is a child resource of type " + type + " in resource " + resource.getName() + " with matching attribute name " + attributeName);
								return myChildResource;

							}
					}else{
						logger.trace("There is a child resource of type " + type + " in resource " + resource.getName());
						return myChildResource;
					}
				}
			} 
		}
		logger.trace("There is no child resource of type " + type + " in resource " + resource.getName());
		return null;
	}
	
	public DiffAttribute isAttributeModified(Resource resource, String attributeName){
		DiffAttribute diffAttribute = null;
		
		ArrayList diffArrayList = resource.getModifiedAttributes();
		for (int i=0; (diffArrayList!=null) && i < diffArrayList.size();i++ ){
			if (((DiffAttribute)diffArrayList.get(i)).getName().equalsIgnoreCase(attributeName)){
				return ((DiffAttribute)diffArrayList.get(i));
			}
		}
		
		return diffAttribute;
	}
	
	/**
	 * 
	 * @param resource
	 * @param attributeName
	 * @return
	 */
	public InvalidAttribute isAttributeValid(Resource resource, String attributeName){
		InvalidAttribute invalidAttribute = null;
		
		ArrayList<InvalidAttribute> invalidArrayList = resource.getInvalidAttributes();
		for (int i=0; (invalidArrayList !=null) && i < invalidArrayList .size();i++ ){
			if (((InvalidAttribute)invalidArrayList.get(i)).getName().equalsIgnoreCase(attributeName)){
				return ((InvalidAttribute)invalidArrayList.get(i));
			}
		}
		
		return invalidAttribute;
	}
	
	
/**	public static String getCommaSeperatedFromArray(ArrayList attributeValue){
		
		StringBuffer semicommaSeperatedString = new StringBuffer();
		Iterator newAttributeValue = ((ArrayList) attributeValue).iterator();
		while(newAttributeValue.hasNext()){
			semicommaSeperatedString.append( newAttributeValue.next());
			semicommaSeperatedString.append( ";");
		}
		if (semicommaSeperatedString.length()>0){
			return semicommaSeperatedString.substring(0, semicommaSeperatedString.length()-1);
		}else{
			return semicommaSeperatedString.toString();
		}
	} 
**/


	public static void initialiseProperites(){
		Properties properties = PropertyLoader.loadPropertiesFromClassPath("Constants.properties",Thread.currentThread().getContextClassLoader());
		if (properties !=null){
		
			ResourceConstants.SYNC_IGNORE_TYPE_LIST =  (String[]) PropertyHelper.getArrayFromCommaSeperated( properties.get("SYNC_IGNORE_TYPE_LIST").toString()).toArray(new String[0]);
			ResourceConstants.ATTRIBUTE_IGNORE_LIST =  (String[]) PropertyHelper.getArrayFromCommaSeperated( properties.get("ATTRIBUTE_IGNORE_LIST").toString()).toArray(new String[0]);
			
		}

	}
	
	public static boolean isResourceDummy(Resource resource, ResourceMetaData resourceMetaData){
		
		/**
		 * if there is only one attribute and the values of that 
		 * is a reference that will be used for pre match then it is 
		 * dummy
		 */
		if (resource.getAttributeList().size() ==1){
			String value = "";
			Iterator it = resource.getAttributeList().keySet().iterator();
			while(it.hasNext()){
				value = resource.getAttributeList().get(it.next()).toString(); 
			}
			if (value.startsWith(PropertiesConstant.RESOURCE_REFERENCE_PREFIX, 0)){
				resource.setDummy(true);
				return true;
			} 
		}

		/**
		 * If arritbutes is 0 and the object is isAttrbuteCount0 false 
		 * then it is a dummy attribute
		 */
		
		if ( (resourceMetaData!=null) && (resource.getAttributeList().size()==0) && (!resourceMetaData.isAttributeCount0())){
			resource.setDummy(true);
			return true;
		}
		resource.setDummy(false);
		return false;
	}
	
	public static boolean isResourceDummyInSource(Resource resource, DeployInfo deployInfo){
		String[] currentSelectedResource =  deployInfo.getCurrentSelectedResources();
		if (currentSelectedResource!=null){
			for (int i=0, n = currentSelectedResource.length; i<n;i++  ){
				if (currentSelectedResource[i].equalsIgnoreCase(resource.getName())){
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Create object w/o attributes set for supported Type.
	 * If type not supported then return null 
	 * 
	 * This will be called from 2 places.
	 * Creating a new resource
	 * Add a new attribute,where value of that attribute is a new resource.
	 *  
	 * @param type
	 * @param parentResource
	 * @return
	 * @throws DeployException
	 */
	public Resource createSkeletonResource(String type,Resource parentResource,ResourceMetaData defaultResourceMetaData)
		throws DeployException{
		try{
			/**
			 * Creating new resource and setting containment path and resource type
			 */
			Resource resource = new Resource();
			resource.setName(type);
			resource.setIncoming(true);
			logger.trace("Setting containment path for new resource  type " + type + " " +  parentResource.getContainmentPath() + ":" + type);
			resource.setContainmentPath(parentResource.getContainmentPath() + ":" + type);
			/**
			 * Create empty attribute list
			 */
	
			resource.setAttributeList(new HashMap());
			
			/**
			 * Create clone of the parent and children of this resource
			 */
			
	//		Resource cloneParentResource =  (Resource)parentResource.clone();
	//		resource.setParent(cloneParentResource);
			
			resource.setParent(parentResource);
	
	//		Vector children = cloneParentResource.getChildren();
			Vector children = parentResource.getChildren();
			
			Vector childrenBeforeAddingNewChild = new Vector();
			
			if (children !=null){
				childrenBeforeAddingNewChild = (Vector)children.clone();
			}
			
			/**
			 * Add this new resource as child of clone parent because 
			 * if the 
			 */
			//cloneParentResource.addChild(resource);
			parentResource.addChild(resource);
			ResourceParserHelper resourceParserHelper = new ResourceParserHelper();
			//resourceParserHelper.setParentTreePath(cloneParentResource);
			resourceParserHelper.setParentTreePath(parentResource);

			//resourceParserHelper.setResourceMetaData(resource,ResourceMetaDataHelper.getFullResourceMetaData(),null,defaultResourceMetaData);
			resourceParserHelper.setResourceMetaData(resource,ResourceMetaDataHelper.getFullResourceMetaData(),null,defaultResourceMetaData);
			
			if (resource.getResourceMetaData()==null){
				logger.info("Resource type " + parentResource.getContainmentPath() + " " + resource.getName() + " not supported."  );
				SDLog.log("Resource type " +  parentResource.getContainmentPath()+ " " +resource.getName() + " not supported."  );
				resource.setResourceMetaData(defaultResourceMetaData);
	
				//parentResource.removeChildOfType(resource);
			
		//	}else{
		//		resource.setParent(parentResource);
		//		parentResource.addInComingChild(resource);
			}
			
			resource.setParent(parentResource);
			parentResource.addInComingChild(resource);
			parentResource.addDifferentChildCount();

			parentResource.setChildren(childrenBeforeAddingNewChild);
			return resource;
		}catch(ConnectorException e){
			throw new DeployException(e);
		}catch (ConfigServiceException e){
			throw new DeployException(e);
		}
	}	


	/**	
	 * Will check if the attribute passed is traget attribute in any of the command link for this resource.
	 * 
	 * @param newResource
	 * @param currentResourceAttribute
	 * @return
	 */
	
	public CommandLinkAttribute isThisLinkAttributeForCommand(Resource newResource, String currentResourceAttribute){
		
		boolean isThisExtraAttributeForCommand = false;
		
		if (newResource.getResourceMetaData().isCommandManaged()){
			logger.trace(" Resource " + newResource.getResourceMetaData().getType() + " isCommandManaged true ");			
			HashMap<String, CommandAttribute> commandAttributeMapping =  newResource.getResourceMetaData().getCommandMetaData().getAttributeMappings();
			String[] keys = (String [])commandAttributeMapping.keySet().toArray(new String[0]);
			for (int i =0 ; i < commandAttributeMapping.size();i++){
				CommandAttribute commandAttribute =  (CommandAttribute)commandAttributeMapping.get(keys[i]);

				logger.trace("Checking if command link attribute is " + keys[i] + " is null ");
				if (commandAttribute.getCommandLinkAttribute()!=null){
					logger.trace("Checking if not null for " + keys[i] );

					CommandLinkAttribute  commandLinkAttribute = commandAttribute.getCommandLinkAttribute();
	
					logger.trace("Checking if - " + currentResourceAttribute + " is same the Target Attribute in Command Link - " + commandLinkAttribute.getTargetAttribute());
					if (commandLinkAttribute.getTargetAttribute().equalsIgnoreCase(currentResourceAttribute)){
						logger.trace("Attribute needs to extracted from the WAS Config " + currentResourceAttribute); 
						return commandLinkAttribute;
					}
	
				}
			}
			
			
		}
		return null;
	}

	/**	
	 * 
	 * @param newResource
	 * @param currentResourceAttribute
	 * @return
	 */
	
	public CommandAttribute isThisExtraAttributeForCommand(Resource newResource, String currentResourceAttribute){
		
		boolean isThisExtraAttributeForCommand = false;
		
		if (newResource.getResourceMetaData().isCommandManaged()){
			logger.trace(" Resource " + newResource.getResourceMetaData().getType() + " isCommandManaged true ");			
			HashMap<String, CommandAttribute> commandAttributeMapping =  newResource.getResourceMetaData().getCommandMetaData().getAttributeMappings();
			//String[] keys = (String [])commandAttributeMapping.keySet().toArray(new String[0]);
			CommandAttribute matchingCommandAttribute =  commandAttributeMapping.get(currentResourceAttribute);
			if ((matchingCommandAttribute!=null ) && (matchingCommandAttribute .getType().equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL)) ){
				return matchingCommandAttribute;	
			}
		}
		return null;
	}


}
