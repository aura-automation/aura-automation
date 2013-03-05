package com.apartech.auraconfig.resources.customcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.apartech.auraconfig.resources.DiffAttribute;
import com.apartech.auraconfig.resources.Resource;
import com.apartech.auraconfig.resources.ResourceCreatorHelper;
import com.apartech.auraconfig.resources.ResourceDiffReportHelper;
import com.apartech.auraconfig.resources.ResourceFinder;
import com.apartech.auraconfig.resources.ResourceHelper;
import com.apartech.auraconfig.resources.WASConfigReader;
import com.apartech.auraconfig.resources.WASConfigReaderHelper;
import com.apartech.common.deploy.DeployInfo;
import com.apartech.common.exception.DeployException;
import com.apartech.common.properties.helper.PropertyHelper;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class SIBusMemberCustomCodeImpl {
	private static final Log logger = LogFactory.getLog(SIBusMemberCustomCodeImpl.class);
	WASConfigReaderHelper wasConfigReaderHelper = new WASConfigReaderHelper();

	/**
	 * 
	 * @param session
	 * @param configService
	 * @param resource
	 * @param configObject
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 */
	public HashMap extract (Session session,ConfigService configService,
			Resource resource,ObjectName configObject,DeployInfo deployInfo, HashMap<String, String> incomingAttributeList) throws DeployException,MalformedObjectNameException{
	//	attributes =  PropertyHelper.getArrayFromCommaSeperated(resource.getResourceMetaData().getCustomCodeAttributes() );
		try {
			logger.trace( " Get SIBusMemberTarget, i.e. child of this resource "  );
			Object attributeValue = configService.getAttribute(session,configObject,"target");
			
			ArrayList attributeArrayList = (ArrayList)attributeValue;
			Iterator attributeIterator = attributeArrayList.iterator();
			logger.trace("Got " + attributeArrayList.size() + " , Its a collection ");
			int cnt = 0;
			String engineId = null;
			while(attributeIterator.hasNext()){
				
				AttributeList attributeConfigObject = (AttributeList)attributeIterator.next();
				engineId = ConfigServiceHelper.getAttributeValue(attributeConfigObject, "engineUuid").toString();

				logger.trace( " Get SIBMessagingEngine, that matches the ID with engineuid "  );
				ArrayList<ObjectName> configIDs = wasConfigReaderHelper.getMatchingWASObjects("SIBMessagingEngine" , "uuid" , engineId,configService, session); 
				 
				
				for (int i=0 ; i < configIDs.size(); i++){
						logger.trace( " Get SIBDatastore, child of SIBMessagingEngine "  );
						AttributeList dataStore =  (AttributeList )configService.getAttribute(session, configIDs.get(i) , "dataStore");
						ObjectName dataStoreConfigObject = ConfigServiceHelper.createObjectName((AttributeList)dataStore);
					
							checkInCommingAttribute(dataStoreConfigObject,incomingAttributeList, resource,configService,session,deployInfo,0,"SIBDatastore" );
							
				}

				configIDs = wasConfigReaderHelper.getMatchingWASObjects("SIBMQServerBusMember" , "uuid" , engineId,configService, session); 

				logger.trace( " Get SIBMQServerBusMember, that matches the ID with engineuid "  );

				for (int i=0 ; i < configIDs.size(); i++){
						logger.trace( " Get SIBDatastore, child of SIBMessagingEngine "  );
						
						checkInCommingAttribute(configIDs.get(i) ,incomingAttributeList, resource, 
								configService, session ,deployInfo, 0,"SIBMQServerBusMember" );
							
					}
			}
			
		
		}catch(ConfigServiceException e){
			throw new DeployException(e);
		}catch(ConnectorException e){
			throw new DeployException(e);
		}catch(AttributeNotFoundException e){
			throw new DeployException(e);
		}
		return incomingAttributeList;
	}
	
	
	/**
	 * 
	 * @param session
	 * @param configService
	 * @param resource
	 * @param configObject
	 * @param deployInfo
	 * @throws DeployException
	 */
	public ArrayList<DiffAttribute> modify(Session session,ConfigService configService,
			Resource resource,ObjectName configObject,DeployInfo deployInfo,
			 AdminClient adminClient,ObjectName scope, Resource allResources,Resource referenceResources) throws DeployException{
	//	attributes =  PropertyHelper.getArrayFromCommaSeperated(resource.getResourceMetaData().getCustomCodeAttributes() );
		System.out.println(" Get the attriutes");
		
		
		ArrayList modifiedAttributes = new ArrayList();
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
		try {
			logger.trace( " Get SIBusMemberTarget, i.e. child of this resource ");
			Object attributeValue = configService.getAttribute(session,configObject,"target");
			
			ArrayList attributeArrayList = (ArrayList)attributeValue;
			Iterator attributeIterator = attributeArrayList.iterator();
			logger.trace("Got " + attributeArrayList.size() + " , Its a collection ");
			int cnt = 0;
			String engineId = null;
			while(attributeIterator.hasNext()){
				
				AttributeList attributeConfigObject = (AttributeList)attributeIterator.next();
				engineId = ConfigServiceHelper.getAttributeValue(attributeConfigObject, "engineUuid").toString();

				logger.trace( " Get SIBMessagingEngine, that matches the ID with engineuid ");
				ArrayList<ObjectName> configIDs = wasConfigReaderHelper.getMatchingWASObjects("SIBMessagingEngine" , "uuid" , engineId,configService, session); 

				for (int i=0 ; i < configIDs.size(); i++){
						logger.trace( " Get SIBDatastore, child of SIBMessagingEngine ");
						AttributeList dataStore =  (AttributeList )configService.getAttribute(session, configIDs.get(i), "dataStore");
						logger.trace( " Got SIBDatastore, child of SIBMessagingEngine ");

						ObjectName attrObjectName = ConfigServiceHelper.createObjectName((AttributeList)dataStore);
						AttributeList changedAttrList = new AttributeList ();
				/**
				 * Logic here is to get the metadata for the attributes for Object like dataStore, MQEngine
				 * 
				 * Then loop through each and see if the value is different in the xml or new from xml. if so process it 		
				 */
						AttributeList attributeMetaInfo =  configService.getAttributesMetaInfo("SIBDatastore");
						Iterator attrMetaInfoListIterator = attributeMetaInfo.iterator();
						while (attrMetaInfoListIterator.hasNext()){
							
							Attribute configObjectAttributeMetaInfo = (Attribute)attrMetaInfoListIterator.next();
							String resourceAttributeName = configObjectAttributeMetaInfo.getName();
							logger.trace( " Checing if attribute needs to be modified " + resourceAttributeName  );

							//modifyAttribute(dataStore,attributeName,modifiedAttributes, resource);
							
							modifyAttribute(attrObjectName, resourceAttributeName,modifiedAttributes,resource,
									configService, changedAttrList,session, adminClient,scope, 
									allResources,referenceResources,deployInfo);
							
						}

				//		for (int k = 0; k < dataStoreAttributes.size(); k++ ){
				//			System.out.println(" Modify attributes for " + dataStoreAttributes.get(k) );
				//			modifyAttribute(dataStore,dataStoreAttributes.get(k),modifiedAttributes, resource);
				//		}
						configService.setAttributes(session, attrObjectName, changedAttrList);

					}

			}
		}catch(ConfigServiceException e){
			throw new DeployException(e);
		}catch(ConnectorException e){
			throw new DeployException(e);
		}catch(AttributeNotFoundException e){
			throw new DeployException(e);
		}
		return modifiedAttributes;
	}

	/**
	 * 
	 * @param configObject
	 * @param atributeName
	 * @param modifiedAttributes
	 * @param resource
	 * @throws AttributeNotFoundException
	 */
	private void modifyAttribute(ObjectName resourceWasObject, String resourceAttributeName,ArrayList<DiffAttribute> modifiedAttributes,Resource resource,
			ConfigService configService, AttributeList changedAttrList,Session session, AdminClient adminClient,ObjectName scope, 
			Resource allResources,Resource referenceResources,DeployInfo deployInfo)
		throws AttributeNotFoundException,ConnectorException,ConfigServiceException,DeployException{
		
		HashMap<String, String> resourceAttributeList = resource.getAttributeList(); 
		ResourceCreatorHelper resourceCreatorHelper = new ResourceCreatorHelper();
		if (configService.getAttribute(session, resourceWasObject, resourceAttributeName) !=null){

			String configAttributeValue = configService.getAttribute(session, resourceWasObject, resourceAttributeName).toString();
			String resourceAttributeValue =  resourceAttributeList.get(resourceAttributeName );
		
			if (((configAttributeValue==null) || (!configAttributeValue.equals(resourceAttributeValue))) && (resourceAttributeValue!=null)) {
				System.out.println(resourceAttributeName + " different resourceAttributeValue:" + resourceAttributeValue + " configAttributeValue:"+ configAttributeValue);
				
				resourceCreatorHelper.modifyAttribute(resource, resourceWasObject, resourceAttributeName, modifiedAttributes, changedAttrList,
						configService, session, adminClient, scope, allResources, referenceResources, deployInfo); 
			}
				
		}

	}

	
	/**
	 * 
	 * @param configObject
	 * @param atributeName
	 * @param incomingAttributeList
	 * @param resource
	 * @param configService
	 * @param session
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 */
	private void checkInCommingAttribute(ObjectName configObject, HashMap<String, String> incomingAttributeList ,
			Resource resource,ConfigService configService,Session session, DeployInfo deployInfo ,int count,String ObjectName )
	
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{
		
	/**	HashMap<String, String> resourceAttributeList = resource.getAttributeList(); 
		if ((resourceAttributeList == null) ||(resourceAttributeList.get(atributeName ) ==null)){
			String configAttributeValue = null;
			if (configService.getAttribute(session,configObject, atributeName )!=null){
				configAttributeValue = configService.getAttribute(session,configObject, atributeName ).toString();
				System.out.println(atributeName + " is incoming : configAttributeValue:"+ configAttributeValue);
				incomingAttributeList.put(atributeName,configAttributeValue);
			}
			
		
		}
		**/
		
		AttributeList attrMetaInfoList = configService.getAttributesMetaInfo(ObjectName);
		Iterator attrMetaInfoListIterator = attrMetaInfoList.iterator();

		
		while (attrMetaInfoListIterator.hasNext()){
			
			Attribute configObjectAttributeMetaInfo = (Attribute)attrMetaInfoListIterator.next();
			String attributeName = configObjectAttributeMetaInfo.getName();
			// this is used when an already existing resource needs to be checked for incoming changes.
			// if we are checking an existing resource in the
			logger.trace(" >>> Start Checking attribute for existing resource " + attributeName );
			
			if (incomingAttributeList.get(attributeName) ==null){
				if ((resource.getAttributeList()==null) || (resource.getAttributeList().get(attributeName)==null)){
					if (!ResourceHelper.isOnIgnoreList(attributeName) ){
						//logger.trace(" >>> Start Checking attribute for existing resource " + attributeName );
						Object attributeValue = configService.getAttribute(session,configObject,attributeName);
						logger.trace(" Checking attribute " + attributeName + " with value " + attributeValue);
						wasConfigReaderHelper.addConfigAttributeToResourceAttributeMap(session, configService, attrMetaInfoList, attributeName,
								incomingAttributeList , resource, attributeValue, null,deployInfo, count);
	
						
					}
				}
			}
			logger.trace(" <<< Finished Checking attribute for existing resource " + attributeName );
		}
	}
	
	

}
