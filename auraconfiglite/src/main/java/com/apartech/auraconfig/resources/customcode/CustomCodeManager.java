package com.apartech.auraconfig.resources.customcode;

import java.util.ArrayList;
import java.util.HashMap;

import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.apartech.auraconfig.resources.DiffAttribute;
import com.apartech.auraconfig.resources.Resource;
import com.apartech.auraconfig.resources.metadata.ResourceMetaData;
import com.apartech.auraconfig.resources.metadata.ResourceMetaDataConstants;
import com.apartech.common.deploy.DeployInfo;
import com.apartech.common.exception.DeployException;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class CustomCodeManager {
	private static final Log logger = LogFactory.getLog(CustomCodeManager.class);
	
	/**
	 * 
	 * @param session
	 * @param configService
	 * @param resource
	 * @param configObject
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 * @throws MalformedObjectNameException
	 */
	public void process(Session session,ConfigService configService,
			Resource resource,ObjectName configObject, DeployInfo deployInfo, HashMap<String, String> incomingAttributeList) throws DeployException,MalformedObjectNameException,AttributeNotFoundException{
	
			ResourceMetaData resourceMetaData =  resource.getResourceMetaData();
			logger.trace("CustomCodeManager:-  resourceMetaData.getCustomCodeManaged() " + resourceMetaData.getCustomCodeManaged());
			
			if (resourceMetaData.getCustomCodeManaged().equalsIgnoreCase(ResourceMetaDataConstants.CUSTOM_CODE_SIBUSMEMBER)){
				SIBusMemberCustomCodeImpl sibusMemberCustomCodeImpl = new SIBusMemberCustomCodeImpl();
				sibusMemberCustomCodeImpl.extract(session, configService,  resource, configObject, deployInfo,incomingAttributeList);
				
			}else if (resourceMetaData.getCustomCodeManaged().equalsIgnoreCase(ResourceMetaDataConstants.CUSTOM_CODE_SIBFOREIGNBUS)){
				SIForeignBusCustomCodeImpl siForeignBusCustomCodeImpl = new SIForeignBusCustomCodeImpl();
				siForeignBusCustomCodeImpl.extract(session, configService,  resource, configObject, deployInfo,incomingAttributeList);
			}else if (resourceMetaData.getCustomCodeManaged().equalsIgnoreCase(ResourceMetaDataConstants.CUSTOM_CODE_SIBMQLINK )){
				SIBMQLinkCustomCodeImpl sibMQLinkCustomCodeImpl = new SIBMQLinkCustomCodeImpl();
				sibMQLinkCustomCodeImpl.extract(session, configService,  resource, configObject, deployInfo,incomingAttributeList);
			}
	
		
	}


	
	public ArrayList<DiffAttribute> modify(Session session,ConfigService configService,
			Resource resource,ObjectName configObject, DeployInfo deployInfo,AdminClient adminClient,ObjectName scope, 
			Resource allResources,Resource referenceResources) throws DeployException{
	
			ResourceMetaData resourceMetaData =  resource.getResourceMetaData();
			logger.trace("CustomCodeManager:-  resourceMetaData.getCustomCodeManaged() " + resourceMetaData.getCustomCodeManaged());
			
			if (resourceMetaData.getCustomCodeManaged().equalsIgnoreCase(ResourceMetaDataConstants.CUSTOM_CODE_SIBUSMEMBER)){
				SIBusMemberCustomCodeImpl sibusMemberCustomCodeImpl = new SIBusMemberCustomCodeImpl();
				return sibusMemberCustomCodeImpl.modify(session, configService,  resource, configObject, deployInfo,adminClient,scope,
						allResources,referenceResources);
			}else if (resourceMetaData.getCustomCodeManaged().equalsIgnoreCase(ResourceMetaDataConstants.CUSTOM_CODE_SIBFOREIGNBUS)){
				SIForeignBusCustomCodeImpl siForeignBusCustomCodeImpl = new SIForeignBusCustomCodeImpl();
					return siForeignBusCustomCodeImpl.modify(session, configService,  resource, configObject, deployInfo,adminClient,scope,
							allResources,referenceResources);
			}else if (resourceMetaData.getCustomCodeManaged().equalsIgnoreCase(ResourceMetaDataConstants.CUSTOM_CODE_SIBMQLINK)){
				SIBMQLinkCustomCodeImpl sibMQLinkCustomCodeImpl = new SIBMQLinkCustomCodeImpl();
				return sibMQLinkCustomCodeImpl.modify(session, configService,  resource, configObject, deployInfo,adminClient,scope,
						allResources,referenceResources);
			}else{
				return null;
			}
	
		
	}

}
