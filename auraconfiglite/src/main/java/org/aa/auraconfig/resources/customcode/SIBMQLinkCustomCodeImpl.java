package org.aa.auraconfig.resources.customcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.DiffAttribute;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceDiffReportHelper;
import org.aa.auraconfig.resources.ResourceHelper;
import org.aa.auraconfig.resources.configreader.WASConfigReader;
import org.aa.auraconfig.resources.configreader.WASConfigReaderHelper;
import org.aa.auraconfig.resources.creator.ResourceCreatorHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

public class SIBMQLinkCustomCodeImpl {
	private static final Log logger = LogFactory.getLog(SIBMQLinkCustomCodeImpl.class);
	WASConfigReaderHelper wasConfigReaderHelper = new WASConfigReaderHelper();


	/**
	 * Description: Create a new WebSphere MQ link.

		Target object:   None
		
		Arguments:
		  *bus - The name of the bus.
		  *messagingEngine - The name of the messaging engine.
		  *name - The name of the WebSphere MQ link.
		  *foreignBusName - The name of the foreign bus.
		  *queueManagerName - The name of the queue manager.
		  *senderChannelTransportChain - The name of the sender channel transport chain {OutboundBasicMQLink
		 | OutboundSecureMQLink}.
		  description - The description of the SIB WebSphere MQ link.
		  batchSize - Batch size {1 - 9,999} (default is "50").
		  maxMsgSize - The maximum message size for the WebSphere MQ link {0 - 104,857,600} (default is "4,1
		94,304").
		  heartBeat - Heartbeat {0 - 999,999} (default is "300").
		  sequenceWrap - The sequence wrap value {100 - 999,999,999} (default is "999,999,999").
		  nonPersistentMessageSpeed - Non-persistent message speed {Fast | Normal} (default is "Fast").
		  adoptable - Adoptable {True | False} (default is "True").
		  initialState - The initial state of the WebSphere MQ link {Started | Stopped} (default is "Started
		").
		  senderChannelName - The name of the sender channel.
		  hostName - Host name.
		  port - Port number {0 - 2,147,483,647} (default is "1414").
		  discInterval - Disconnect interval {0 - 999,999} (default is "900").
		  shortRetryCount - Short retry count {0 - 999,999,999} (default is "10").
		  shortRetryInterval - Short retry interval {0 - 999,999,999} (default is "60").
		  longRetryCount - Long retry count {0 - 999,999,999} (default is "999,999,999").
		  longRetryInterval - Long retry interval {0 - 999,999,999} (default is "1200").
		  senderChannelInitialState - The initial state of the sender channel {Started | Stopped} (default i
		s "Started").
		  receiverChannelName - The name of the receiver channel.
		  inboundNonPersistentReliability - Inbound Non-persistent reliability {BEST_EFFORT | EXPRESS | RELI
		ABLE} (default is "Reliable").
		  inboundPersistentReliability - Inbound persistent reliability {Reliable | Assured} (default is "As
		sured").
		  receiverChannelInitialState - The initial state of the receiver channel {Started | Stopped} (defau
		lt is "Started").
		
		Steps:
		  None
	 */
	
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
	public void extract (Session session,ConfigService configService,
			Resource resource,ObjectName configObject,DeployInfo deployInfo ,HashMap<String, String> incomingAttributeList) 
			throws DeployException,MalformedObjectNameException,AttributeNotFoundException{

		try {
			logger.trace( " Get SIBMQLink "  + configObject.getCanonicalName() );

			AttributeList attributeValue = (AttributeList)configService.getAttribute(session,configObject,"senderChannel");
			if (attributeValue !=null){
				ObjectName attributeValueObject = ConfigServiceHelper.createObjectName(attributeValue); 
				System.out.println(" ************ " +  ConfigServiceHelper.getConfigDataType(attributeValueObject));
			
					checkInCommingAttribute(attributeValueObject,incomingAttributeList, resource, 
							configService, session ,deployInfo, 0,"SIBMQLinkSenderChannel" );
			}	
						
			
			
			
			attributeValue = (AttributeList)configService.getAttribute(session,configObject,"receiverChannel");
			if (attributeValue !=null){
				ObjectName attributeValueObject = ConfigServiceHelper.createObjectName(attributeValue); 
				System.out.println(" ************ " +  ConfigServiceHelper.getConfigDataType(attributeValueObject));
			
					checkInCommingAttribute(attributeValueObject,incomingAttributeList, resource, 
							configService, session ,deployInfo, 0,"SIBMQLinkReceiverChannel" );
			}	
			
			
			
			/**
			 * get targetUid
			 * Match with SIBVirtualMQLink uuid
			 * Get the parent foreign bus 
			 */
			String targetUuid = configService.getAttribute(session,configObject,"targetUuid").toString();
			String foreignBusName = null;
			String busName = null;
			
			ObjectName[] busConfigIDs = configService.resolve(session, "SIBus");
			ObjectName[] cellIDs = configService.resolve(session, "Cell");
			String cellName = configService.getAttribute(session, cellIDs[0] , "name" ).toString();

			for (int j=0 ; j < busConfigIDs.length; j++){
				
				ObjectName currentBus = busConfigIDs[j];
				String currentBusName = configService.getAttribute(session, currentBus, "name" ).toString();
				ObjectName[] configIDs = configService.resolve(session, "Cell="+ cellName +":SIBus="+ currentBusName +":SIBForeignBus");
				
				for (int i=0 ; i < configIDs.length; i++){
					ObjectName currentObject = (ObjectName) configIDs[i];
					
					AttributeList wasvirtualLink = (AttributeList)configService.getAttribute(session, currentObject, "virtualLink" );
					if (wasvirtualLink!=null){
						String uuid = ConfigServiceHelper.getAttributeValue(wasvirtualLink, "uuid").toString();
		
						if (targetUuid.equalsIgnoreCase(uuid)){
							foreignBusName = configService.getAttribute(session, currentObject,"name").toString();
							busName = currentBusName;

						}
					}
				}
			}			
			incomingAttributeList.put("foreignBusName", foreignBusName);
			incomingAttributeList.put("bus", busName);
						
			checkInCommingAttribute(configObject,incomingAttributeList, resource, 
					configService, session ,deployInfo, 0,"SIBMQLink" );
			
			
		}catch(ConfigServiceException e){
			throw new DeployException(e);
		}catch(ConnectorException e){
			throw new DeployException(e);
		}
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
		logger.trace(">>");
		logger.trace("	Will modify " + resource.getContainmentPath());

		ArrayList<DiffAttribute> modifiedAttributes = new ArrayList<DiffAttribute>();
		try {

			// Start - Modify Attributes of SIBMQLinkSenderChannel
			AttributeList senderChannelAttributeList =  (AttributeList )configService.getAttribute(session, configObject, "senderChannel");
			ObjectName attrObjectName = ConfigServiceHelper.createObjectName((AttributeList)senderChannelAttributeList);
		
			modifyObjectName("SIBMQLinkSenderChannel", attrObjectName, modifiedAttributes, resource, configService, session, adminClient, 
					scope, allResources, referenceResources, deployInfo);
			// End - Modify Attributes of SIBMQLinkSenderChannel

			
			//Start - Modify Attributes of SIBMQLinkReceiverChannel
			AttributeList receiverChannelAttributeList =  (AttributeList )configService.getAttribute(session, configObject, "receiverChannel");
			attrObjectName = ConfigServiceHelper.createObjectName((AttributeList)receiverChannelAttributeList);

			modifyObjectName("SIBMQLinkReceiverChannel", attrObjectName, modifiedAttributes, resource, configService, session, adminClient, 
					scope, allResources, referenceResources, deployInfo);
			//End - Modify Attributes of SIBMQLinkReceiverChannel
			
			// Start - Modify Attributes if MQLink itself
			modifyObjectName("SIBMQLink", configObject, modifiedAttributes, resource, configService, session, adminClient, 
					scope, allResources, referenceResources, deployInfo);
			// End - Modify Attributes if MQLink itself

			
			/**
			 * get targetUid
			 * Match with SIBVirtualMQLink uuid
			 * Get the parent foreign bus 
			 */
			
			if (resource.getAttributeList().get("foreignBusName") != null){
				String resourceForeignBusNameValue =  resource.getAttributeList().get("foreignBusName").toString();
				
				String wasForeignBusIdValue  =   configService.getAttribute(session,configObject, "targetUuid").toString();
				String resourceForeignBusIdValue = getVirtalLinkUuIdForForeignBus(resourceForeignBusNameValue, configService, session);
				
				logger.trace("Getting Foreign bus and Bus details for resource foreign bus " + resourceForeignBusNameValue + " id " + resourceForeignBusIdValue );
				HashMap<String, String> resourceForeignBusDetails =  getForeignBusForVirtalLinkUuId(resourceForeignBusIdValue, configService, session) ;
				logger.trace("Getting Foreign bus and Bus details for WAS foreign bus id " + wasForeignBusIdValue );
				HashMap<String, String> wasForeignBusDetails =  getForeignBusForVirtalLinkUuId(wasForeignBusIdValue, configService, session) ;
				
				String resourceBusNameValue =  resourceForeignBusDetails.get("busName").toString();
				
				logger.trace("Checking if wasForeignBusIdValue " + wasForeignBusIdValue + " is same as resourceForeignBusIdValue " + resourceForeignBusIdValue ); 
				if (!wasForeignBusIdValue.equalsIgnoreCase(resourceForeignBusIdValue)){
					logger.trace("Above value are different hence setting targetUuid to " + resourceForeignBusIdValue ); 

					AttributeList changedAttrList = new AttributeList ();
					changedAttrList.add(new Attribute("targetUuid", resourceForeignBusIdValue));
					configService.setAttributes(session, configObject, changedAttrList);
					
					// add foreign bus to diff attribute
					ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
					modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute("foreignBusName", resourceBusNameValue, wasForeignBusDetails.get("foreignBusName")) );
					
					// if bus is different then add that as well
					if (!wasForeignBusDetails.get("busName").equalsIgnoreCase(resourceBusNameValue)){
						System.out.println("Bus name is also different " + wasForeignBusDetails.get("busName") + " resource value is " + resourceBusNameValue); 

						modifiedAttributes.add(resourceDiffReportHelper.getDiffAttribute("busName", resourceBusNameValue, wasForeignBusDetails.get("busName")) );
					}
				}
			}else{
				SDLog.log("	WARNING: 	ForeignBusName is null in the xml");
			}
			logger.trace("<<");
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
	 * @param foreignBusName
	 * @param configService
	 * @param session
	 * @return
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws AttributeNotFoundException
	 */
	private HashMap<String, String> getForeignBusForVirtalLinkUuId(String virtualLinkUuId, ConfigService configService, Session session)
		throws ConnectorException,ConfigServiceException,AttributeNotFoundException{
	
		ObjectName[] busConfigIDs = configService.resolve(session, "SIBus");
		ObjectName[] cellIDs = configService.resolve(session, "Cell");
		String cellName = configService.getAttribute(session, cellIDs[0] , "name" ).toString();
		
		String foreignBusName = null;
		String  busName = null;
		
		HashMap<String, String> foreignBusDetails = new HashMap<String, String>();
		for (int j=0 ; j < busConfigIDs.length; j++){
			
			ObjectName currentBus = busConfigIDs[j];
			String currentBusName = configService.getAttribute(session, currentBus, "name" ).toString();
			logger.trace("	Checking for matching virtual link " + virtualLinkUuId  + " is in bus " + currentBusName);
			ObjectName[] configIDs = configService.resolve(session, "Cell="+ cellName +":SIBus="+ currentBusName +":SIBForeignBus");
			
			for (int i=0 ; i < configIDs.length; i++){
				ObjectName currentObject = (ObjectName) configIDs[i];
				
				AttributeList wasvirtualLink = (AttributeList)configService.getAttribute(session, currentObject, "virtualLink" );
				if (wasvirtualLink!=null){
					String uuid = ConfigServiceHelper.getAttributeValue(wasvirtualLink, "uuid").toString();
					String currentForeignBusName = configService.getAttribute(session, currentObject,"name").toString();
					
					logger.trace("	Checking for matching virtual link  " + virtualLinkUuId  + " is in bus " + currentBusName + " foreign bus " + currentForeignBusName );
					if (virtualLinkUuId.equalsIgnoreCase(uuid)){
						logger.trace("	Match found virtual link  " + virtualLinkUuId  + " is in bus " + currentBusName + " foreign bus " + currentForeignBusName );
						foreignBusName = currentForeignBusName ;
						busName = currentBusName;

					}
				}
			}
		}
		foreignBusDetails.put("foreignBusName", foreignBusName);
		foreignBusDetails.put("busName", busName);
		return foreignBusDetails;
	}

	
	/**
	 * 
	 * @param foreignBusName
	 * @param configService
	 * @param session
	 * @return
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws AttributeNotFoundException
	 */
	private String getVirtalLinkUuIdForForeignBus(String foreignBusName, ConfigService configService, Session session)
		throws ConnectorException,ConfigServiceException,AttributeNotFoundException{
	
		ObjectName[] busConfigIDs = configService.resolve(session, "SIBus");
		ObjectName[] cellIDs = configService.resolve(session, "Cell");
		String cellName = configService.getAttribute(session, cellIDs[0] , "name" ).toString();
		String uuid = null;
		for (int j=0 ; j < busConfigIDs.length; j++){
			
			ObjectName currentBus = busConfigIDs[j];
			String currentBusName = configService.getAttribute(session, currentBus, "name" ).toString();
			ObjectName[] configIDs = configService.resolve(session, "Cell="+ cellName +":SIBus="+ currentBusName +":SIBForeignBus=" + foreignBusName);
			
			for (int i=0 ; i < configIDs.length; i++){
				ObjectName currentObject = (ObjectName) configIDs[i];
				
				AttributeList wasvirtualLink = (AttributeList)configService.getAttribute(session, currentObject, "virtualLink" );
				if (wasvirtualLink!=null){
					uuid = ConfigServiceHelper.getAttributeValue(wasvirtualLink, "uuid").toString();
	
				
				}
			}
		}
		return uuid;
	}
	
	/**
	 * 
	 * @param objectType
	 * @param resourceWasObject
	 * @param modifiedAttributes
	 * @param resource
	 * @param configService
	 * @param session
	 * @param adminClient
	 * @param scope
	 * @param allResources
	 * @param referenceResources
	 * @param deployInfo
	 * @throws AttributeNotFoundException
	 * @throws ConnectorException
	 * @throws ConfigServiceException
	 * @throws DeployException
	 */
	private void modifyObjectName(String objectType, ObjectName resourceWasObject, 
			ArrayList<DiffAttribute> modifiedAttributes,Resource resource,
			ConfigService configService, Session session, AdminClient adminClient,ObjectName scope, 
			Resource allResources,Resource referenceResources,DeployInfo deployInfo )
			throws AttributeNotFoundException,ConnectorException,ConfigServiceException,DeployException{
		
		AttributeList changedAttrList = new AttributeList ();
		AttributeList attributeMetaInfo =  configService.getAttributesMetaInfo(objectType);
		Iterator attrMetaInfoListIterator = attributeMetaInfo.iterator();
		
		while (attrMetaInfoListIterator.hasNext()){
			Attribute configObjectAttributeMetaInfo = (Attribute)attrMetaInfoListIterator.next();
			String resourceAttributeName = configObjectAttributeMetaInfo.getName();
			logger.trace( " Checking if attribute needs to be modified " + resourceAttributeName  );

			modifyAttribute(resourceWasObject, resourceAttributeName,modifiedAttributes,resource,
					configService, changedAttrList,session, adminClient,scope, 
					allResources,referenceResources,deployInfo);
			
		}
		configService.setAttributes(session, resourceWasObject, changedAttrList);
		
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
			}else{
				logger.trace("		Value in XML is null or equal so no need to modify");
			}
				
		}else{
			logger.trace("		Attribute in WAS is not valid so no need to modify");
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
			Resource resource,ConfigService configService,Session session, DeployInfo deployInfo ,int count,String ObjectName)
	
		throws ConfigServiceException,ConnectorException,MalformedObjectNameException,AttributeNotFoundException,DeployException{
		
			
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
					if (!ResourceHelper.isOnIgnoreList(attributeName) && !attributeName.equalsIgnoreCase("sendStream") ){
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
