package org.aa.auraconfig.resources.linkresources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.LinkAttribute;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceConstants;
import org.aa.auraconfig.resources.ResourceHelper;
import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class LinkResourceHelper {

	private static final Log logger = LogFactory.getLog(LinkResourceHelper.class);

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
	
	private Session mSession; 
	private ConfigService mConfigService;
	
	private LinkResourceHelper(){
		
	}
	
	public LinkResourceHelper(Session session,ConfigService configService){
		mSession = session;
		mConfigService = configService;
	}
	
	/**
	 * When getting configuration from WebSphere when meta for an attribute is link type then convert the value from WebSphere to targetMatch objects, match attribute value.
	 * @param linkAttribute
	 * @param attributeValue
	 * @return
	 * @throws AttributeNotFoundException
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws DeployException
	 * @throws MalformedObjectNameException
	 */
	
	public Object getMatchAttributeValueForLinkAttribute(LinkAttribute linkAttribute, String attributeValue )
		throws AttributeNotFoundException,ConfigServiceException,ConnectorException,DeployException,MalformedObjectNameException{
		
		logger.trace("Will try to match the attribute value " + attributeValue + " to target Link object");
		String linkAttributeValue = "Missing"; 
		String targetAttribute = linkAttribute.getTargetAttribute();
		String targetObject = linkAttribute.getTargetObject();

		if (attributeValue.trim().length() ==0 ){
			return "";
		}
		
		/**
		 * Target Match can be multiple objects in case like value of keyStore,trustStore,keyManager,trustManager on SecureSocketLayer config type
		 */
		String targetMatchAttribute = linkAttribute.getTargetObjectMatchAttributeName();
		
		ResourceHelper resourceHelper = new ResourceHelper();
		logger.trace("Getting object from config for target object type " + targetObject);
		String[] targetObjectTypes =  targetObject.split("\\|");
		ArrayList<ObjectName>  aggregatedConfigIDs = new ArrayList<ObjectName>();
		// get ObjectNames for all the types, used for authMechanism, to get LDAP, SPENGO
		// note this is no longer user but the code has not been reverted as might me required for some other config 
		for (int targetObjectTypeCnt=0; targetObjectTypeCnt < targetObjectTypes.length; targetObjectTypeCnt++){ 
			logger.trace("Getting object from config for target object type " + targetObjectTypes[targetObjectTypeCnt]);

			ObjectName[] _configIDs = resourceHelper.getObjectNames(mSession, mConfigService, targetObjectTypes[targetObjectTypeCnt]);
			Collections.addAll(aggregatedConfigIDs, _configIDs); 
		}
		
		logger.trace("Got object from config for target object type size " + aggregatedConfigIDs.size());
		for (int i = 0; i< aggregatedConfigIDs.size(); i++){
			if (targetAttribute == null){
				String attrValueCanonicalName = (new ObjectName(attributeValue)).getCanonicalName().replace("[", "").replace("]", "");
				//System.out.println( (new ObjectName(attributeValue)).getKeyPropertyList());
				logger.trace("Since the target targetAttribute is null matching from WAS " + aggregatedConfigIDs.get(i).getCanonicalName() + " to " + attrValueCanonicalName) ;

				if (aggregatedConfigIDs.get(i).getCanonicalName().equalsIgnoreCase(attrValueCanonicalName)){
						/**
						 * In some case the target match may just be an ID rather then value of any attribute, Although this should happen
						 * as the ID cannot be environment agnostic	
						 */
						if (targetMatchAttribute.equalsIgnoreCase(ResourceConstants.RESOURCE_CONFIG_ID)){
							linkAttributeValue =  ConfigServiceHelper.getConfigDataId( aggregatedConfigIDs.get(i)).toString();
						}
						else{
							String[] targetMatchAttributeArray = linkAttribute.getTargetObjectMatchAttributeName().split("\\|");
							ArrayList<String> linkAttributeValueArray = new ArrayList<String>();
							for (int targetMatchAttributeCnt = 0; targetMatchAttributeCnt< targetMatchAttributeArray.length ; targetMatchAttributeCnt++){
								String thisLinkAttributeValue = mConfigService.getAttribute(mSession, aggregatedConfigIDs.get(i), targetMatchAttributeArray[targetMatchAttributeCnt] ).toString();
								logger.trace("Link Attribute value is " + linkAttributeValue);
								
								/**
								 * If link attribute has a nested link attribute like managementScope in case of keyStrore, trustStore, trustManager attribute on SecureSocketLayer. 
								 */
								logger.trace("Checking if " + targetMatchAttributeArray[targetMatchAttributeCnt] + " is nested link attribute");	
								if ((linkAttribute.getLinkAttribute()!=null) && linkAttribute.getLinkAttribute().getLinkAttibuteName().equalsIgnoreCase(targetMatchAttributeArray[targetMatchAttributeCnt])) {
									logger.trace("				Processing Nested Link Attribute: WASCOnfigReaderHelper for " + linkAttribute.getLinkAttribute().getLinkAttibuteName());
									linkAttributeValueArray.add( getMatchAttributeValueForLinkAttribute(linkAttribute.getLinkAttribute(), thisLinkAttributeValue ).toString());
								}else{
									linkAttributeValueArray.add( thisLinkAttributeValue);
								}
							}
							
							linkAttributeValue = getPipeSeperatedFromArray(linkAttributeValueArray);
							
							
						}
						logger.trace("Link Attribute value is " + linkAttributeValue);

						
					
					
					return linkAttributeValue ;
				} 
			}else{
				String configTargetAttributeValue =  mConfigService.getAttribute(mSession, aggregatedConfigIDs.get(i), targetAttribute).toString();
				logger.trace("Matching configTargetAttributeValue:attributeValue " + configTargetAttributeValue + ":" + attributeValue);
               /**
                * Target Match can be multiple objects incase like value of keyStore,trustStore,keyManager,trustManager on SecureSocketLayer config type
                */
				String[] targetMatchAttributeArray = linkAttribute.getTargetObjectMatchAttributeName().split("\\|");
				ArrayList<String> linkAttributeValueArray = new ArrayList<String>();
				if (configTargetAttributeValue.equalsIgnoreCase(attributeValue)){
					for (int targetMatchAttributeCnt = 0; targetMatchAttributeCnt< targetMatchAttributeArray.length ; targetMatchAttributeCnt++){
						linkAttributeValueArray.add(  mConfigService.getAttribute(mSession, aggregatedConfigIDs.get(i), targetMatchAttributeArray[targetMatchAttributeCnt]).toString());
						logger.trace("Link Attribute value is " + linkAttributeValue);
					}
					return getPipeSeperatedFromArray(linkAttributeValueArray) ;
				} 
			}
		}
		
		
		logger.trace("Link Attribute value is " + linkAttributeValue);
		return linkAttributeValue  ;
		
	}
	
	
	

	/**
	 * Method to convert arraylist to pipe sepearted string	
	 * @param stringList
	 * @return
	 */
	private String getPipeSeperatedFromArray(ArrayList<String> stringList){
		StringBuffer sb = new StringBuffer();
		for (int i=0 ; i < stringList.size();i++){
			sb.append(stringList.get(i).toString());
			sb.append("|");
		}
		String pipeString = sb.toString(); 
		if (pipeString.endsWith("|")){
			return pipeString.substring(0, pipeString.length()-1); 
		}else if (pipeString.length()==0){
			return "Missing";
		} else{
			return pipeString;
		}
			
		
	}
	

	
	public Object getLinkAttributeForMatchAttributeValue(LinkAttribute linkAttribute, Resource matchResource) throws ConfigServiceException, ConnectorException{
		logger.trace(" Check if targetAttribute: " + linkAttribute.getTargetAttribute() + " for targetObject:" + linkAttribute.getTargetObject() + " is null, if null then return ObjectName");
	
		Object linkAttributeValue;
		String targetAttribute = linkAttribute.getTargetAttribute(); 
				
		if (linkAttribute.getTargetAttribute()!=null){
			logger.trace(" Matching Config Objects found for link resource " + linkAttribute.getTargetObject() + " name: "  + linkAttribute.getTargetObjectMatchAttributeName() + " " + matchResource.getConfigId() );
			linkAttributeValue  = mConfigService.getAttribute(mSession, matchResource.getConfigId() , targetAttribute);
			logger.trace(" Matching Config Objects found for link resource " + linkAttribute.getTargetObject() + " name: "  + linkAttribute.getTargetObjectMatchAttributeName() + " attribute name: " + targetAttribute.toString() + " linkAttributeValue " + linkAttributeValue);
		}else{
			logger.trace(" targetAttribute: " + linkAttribute.getTargetAttribute() + " for targetObject:" + linkAttribute.getTargetObject() + " is null, hence return " + matchResource.getConfigId());
			linkAttributeValue  = matchResource.getConfigId();
		}
		return linkAttributeValue  ;
	}
}
