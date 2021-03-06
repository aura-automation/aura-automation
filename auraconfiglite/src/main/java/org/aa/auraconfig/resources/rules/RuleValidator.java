/**	   Copyright 


**/
package org.aa.auraconfig.resources.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aa.auraconfig.resources.AttributeRule;
import org.aa.auraconfig.resources.InvalidAttribute;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceStatsHelper;
import org.aa.auraconfig.resources.metadata.ResourceMetaDataConstants;
import org.aa.auraconfig.resources.metadata.ResourceMetaDataHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.log.SDLog;

public class RuleValidator {
	private static final Log logger  = LogFactory.getLog(RuleValidator.class);	

	/**
	 * 
	 * @param resource
	 * @param operation
	 * @return
	 */
	public boolean isResourceValid(Resource resource, String operation){
		/**
		 * Use stats helper to set the counter for the new. modified attributes
		 */
		ResourceStatsHelper resourceStatsHelper = new ResourceStatsHelper ();
		resourceStatsHelper.setResourceAttributeStats(resource);
		
		boolean valid = true;
		ResourceRulesMetaData rules = resource.getResourceMetaData().getResourceRulesMetaData();
		ArrayList<InvalidAttribute> invalidAttributes = new ArrayList<InvalidAttribute>();
		/**
		 * Rules Metadata is set only if individual attribute has different set of rule for e.g. if in a ServerCluster user is not allowed to change memberweight attribute.
		 * But if whole resource is not editable then rulesmetadata is not set instead editable attribute on resource metadata is used. 
		 */
		if ((rules!=null) || (resource.getResourceMetaData().getEditable()!=null)){
			//System.out.println("Rules Meta data is not null for " + resource.getContainmentPath());
			/**
			 * Check if the resource can be changed and if the resource is actually modified
			 */
			
			logger.trace(resource.getContainmentPath() +  " Checking if resource editable is set for None");
			logger.trace(resource.getContainmentPath() +  " in Resource meta data rule for editing is " + resource.getResourceMetaData().getEditable());
			
			if ((resource.getResourceMetaData().getEditable()!=null) && (resource.getResourceMetaData().getEditable().equalsIgnoreCase(ResourceMetaDataConstants.EDITABLE_NONE))){
				logger.trace("resource editable is set for None");
				if (resource.getModifiedAttributes().size() >0){
					logger.trace(resource.getContainmentPath() +   "resource is modified ");
					InvalidAttribute invalidAttribute = new InvalidAttribute();
					logger.trace("Setting invalid resource " + resource.getContainmentPath() +   " resource is not modified ");
					SDLog.log(" Resource '" + resource.getContainmentPath() + "' cannot be created or modified");
					invalidAttribute.setMessage("Resource cannot be created or modified");
					resource.setInvalidResource(invalidAttribute);
					valid = false;
				}else{
					logger.trace(resource.getContainmentPath() +   "resource is not modified ");
				}
			}

			/**
			 * Check if the resource can be changed and if the resource is created then error.
			 */
			logger.trace(resource.getContainmentPath() +  " Checking if resource editable is set for Update");
			if ((resource.getResourceMetaData().getEditable()!=null) &&  (resource.getResourceMetaData().getEditable().equalsIgnoreCase(ResourceMetaDataConstants.EDITABLE_UPDATE))){
				logger.trace("resource editable is set for Update");
				logger.trace("New Attr cnt is " + resource.getResourceStats().getNewAttributeCnt());
				logger.trace("Attr cnt is " + resource.getAttributeList().size() );
				if (resource.getResourceStats().getNewAttributeCnt()== resource.getAttributeList().size() ){
					logger.trace(resource.getContainmentPath() +  "resource editable is set for Update");
					InvalidAttribute invalidAttribute = new InvalidAttribute();
					logger.trace("Setting invalid resource " + resource.getContainmentPath() +   " resource is not created ");
					SDLog.log(" Resource '" + resource.getContainmentPath() + "' cannot be create, Only Modify is allowed");
					invalidAttribute.setMessage("Resource cannot be create, Only Modify is allowed");
					resource.setInvalidResource(invalidAttribute);
					valid = false;
				}
			}

			HashMap attrRules = null;
			if (rules!=null){	
				attrRules = rules.getAttributeRules();
			}
			if (attrRules!=null){
				
				String[] attrNames= (String[])attrRules.keySet().toArray(new String[0]);
				
				for (int i=0 ; i < attrNames.length; i++){
					String attrName= (String) attrNames[i];
					AttributeRule attrRule =  (AttributeRule)attrRules.get(attrName);
					String attrValue = null;
					//System.out.println("Checking " + attrName + " comply with rule");

					if (resource.getAttributeList().get(attrName)!=null){
						attrValue = resource.getAttributeList().get(attrName).toString();
					}
					InvalidAttribute invalidAttribute = isAttributeValid(attrName, attrRule, attrValue, operation);
					if (invalidAttribute !=null){
						invalidAttributes.add(invalidAttribute);
						valid = false;
						SDLog.log("Attribute name " + attrName +" with value " + attrValue + " does not comply with rule");
					}
				}
			}
		}else{
			logger.trace("Rule is null " + resource.getContainmentPath() + " of type " + resource.getResourceMetaData().getType());
		}
		resource.setInvalidAttributes(invalidAttributes);
		return valid;
	}
	
		
	/**
	 * if rule editable is set to none then no operation is permitted
	 * if rule editable is set to create then create and update operation is permitted
	 * if rule editable is set to upate then update operation is permitted
	 * 
	 * if min and max value is present then value should be in that range
	 * 
	 * if pattern is present then pattern should match
	 * 
	 * @param rule
	 * @param data
	 * @param operation
	 * @return
	 */
	 private InvalidAttribute isAttributeValid(String attributeName,AttributeRule rule, String data, String operation ){
		InvalidAttribute invalidAttribute = new InvalidAttribute();
		invalidAttribute.setName(attributeName);
		
		if  (!rule.isNillable() && data==null){
			invalidAttribute.setMessage(" Attribute cannot be null");
			return invalidAttribute;
			
		}
		
		if ((rule.getEditable()!= null)){
			if (rule.getEditable().equalsIgnoreCase("none")){
				invalidAttribute.setMessage(" Attribute cannot be changed");
				return invalidAttribute;
			}
			
/**			if (rule.getEditable().equalsIgnoreCase("create")){
				if (operation.equalsIgnoreCase("create") || (operation.equalsIgnoreCase("update"))){
					return true;
				}	
			}
	**/		
			if (rule.getEditable().equalsIgnoreCase("update")){
				if (operation.equalsIgnoreCase("create") ){
					invalidAttribute.setMessage(" Attribute cannot be created");
					return invalidAttribute;
				}	


			}

		} 
			
		//System.out.println("In validate rule max is " + rule.getMax());
		//System.out.println("In validate rule min is "  + rule.getMin());
		if ( (rule.getMax()>-1) || (rule.getMin()>-1)){
			//System.out.println("Checking if the rule is valid for min and max");
			if (!isValid(rule.getMin(), rule.getMax(), data)){
				invalidAttribute.setMessage(" Attribute values must be between " + rule.getMin() + " and "+ rule.getMax());
				return invalidAttribute;
			}

		}
		
		if ((rule.getPattern()!=null) && (rule.getPattern().length()>0)){
			if (!isValid(rule.getPattern(),data)){
				invalidAttribute.setMessage(" Attribute values must match pattern " + rule.getPattern());
				return invalidAttribute;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @param data
	 * @return
	 */
	private boolean isValid(int min, int max, String data){
		//System.out.println("Will validate data " + data);
		int intData = new Integer( data).intValue();
		boolean result =false;
		
		if ((intData >= min) && (intData <= max)){
			result = true;
		} 
		
		return result;
	}
	
	/**
	 * 
	 * @param pattern
	 * @param data
	 * @return
	 */
	private boolean isValid(String pattern, String data){
		 Pattern p = Pattern.compile(pattern);
		 Matcher m = p.matcher(data);
		 boolean result = m.lookingAt();
		 //System.out.println(" Match is " + result);
		 return result;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RuleValidator regExHelper = new RuleValidator();
		regExHelper.isValid("[2-9]", "256"); 
	}
	
}
