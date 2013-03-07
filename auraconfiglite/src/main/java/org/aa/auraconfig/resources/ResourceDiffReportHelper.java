/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.log.SDLog;

public class ResourceDiffReportHelper {
	private static final Log logger  = LogFactory.getLog(ResourceDiffReportHelper.class);

	public DiffAttribute getDiffAttribute(String attributeName, Object resourceAttributeValue, Object configAttributeValue){
		DiffAttribute diffAttribute = new DiffAttribute();
		diffAttribute.setName(attributeName);
		diffAttribute.setConfigValue(configAttributeValue);
		diffAttribute.setDaksValue(resourceAttributeValue);
		return diffAttribute;
	}

	/**
	 * Call this to return the diff Attribute objects for all the new attributes that will get created
	 * @param attributeList
	 * @return
	 */
	public ArrayList<DiffAttribute> getDiffAttributes(AttributeList attributeList ){
		Iterator attributeListIterator = attributeList.iterator();
		ArrayList<DiffAttribute> diffAttributes = new ArrayList<DiffAttribute>();
		while(attributeListIterator.hasNext()){
			Attribute attribute = (Attribute)attributeListIterator.next();
			DiffAttribute diffAttribute = new DiffAttribute();
			if (!attribute.getName().startsWith("_Websphere_")){
				diffAttribute.setChangeType("Added");
				diffAttribute.setName(attribute.getName());
				diffAttribute.setConfigValue("null");
				diffAttribute.setDaksValue(attribute.getValue());
				diffAttributes.add(diffAttribute);
			}
		}
		return diffAttributes;
	}

	/**
	 * 
	 * @param attributeList
	 * @return
	 */
	public ArrayList<DiffAttribute> getDiffAttributesForNew(HashMap<String, String> attributeList ){
		Iterator<String> attributeListIterator = attributeList.keySet().iterator();
		ArrayList<DiffAttribute> diffAttributes = new ArrayList<DiffAttribute>();
		while(attributeListIterator.hasNext()){
			String attributeKey = (String)attributeListIterator.next();
			DiffAttribute diffAttribute = new DiffAttribute();
			diffAttribute.setChangeType(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_NEW);
			diffAttribute.setName(attributeKey);
			diffAttribute.setConfigValue(attributeList.get(attributeKey));
			diffAttribute.setDaksValue(attributeList.get(attributeKey));
			diffAttributes.add(diffAttribute);
			//logger.trace("Adding Incoming attribute for the report " + attributeKey + " value " + attributeList.get(attributeKey));
		}
		return diffAttributes;
	}
	/**
	 * 
	 * @param attributeList
	 * @return
	 */
	public ArrayList<DiffAttribute> getDiffAttributesForIncoming(HashMap<String, String> attributeList ){
		Iterator<String> attributeListIterator = attributeList.keySet().iterator();
		ArrayList<DiffAttribute> diffAttributes = new ArrayList<DiffAttribute>();
		while(attributeListIterator.hasNext()){
			String attributeKey = (String)attributeListIterator.next();
			DiffAttribute diffAttribute = new DiffAttribute();
			diffAttribute.setChangeType("Incoming");
			diffAttribute.setName(attributeKey);
			diffAttribute.setConfigValue(attributeList.get(attributeKey));
			diffAttribute.setDaksValue(attributeList.get(attributeKey));
			diffAttributes.add(diffAttribute);
			//logger.trace("Adding Incoming attribute for the report " + attributeKey + " value " + attributeList.get(attributeKey));
		}
		return diffAttributes;
	}

	public void generateReport(Vector modifiedResources){
		
		for(int i =0 ; i < modifiedResources.size(); i++ ){
			
			Resource resource = (Resource)modifiedResources.get(i);
			ArrayList modifiedAttributes =resource.getModifiedAttributes();
			//System.out.println("Type: " + resource.getContainmentPath());

			if ((modifiedAttributes!=null) && (modifiedAttributes.size()>0)){
				//System.out.println("resource.getModifiedAttributes(): " + resource.getModifiedAttributes().size());

				SDLog.log("********************************************************");
				SDLog.log("Type: " + resource.getName());
	
	//			SDLog.log(resource.getResourceMetaData().getContainmentAttribute());
				SDLog.log("Name: " + resource.getAttributeList().get(resource.getResourceMetaData().getContainmentAttribute()));
	
				SDLog.log("Attribute Changes Count: " + modifiedAttributes.size());
				Iterator modifiedAttributesIterator = modifiedAttributes.iterator();
				while(modifiedAttributesIterator.hasNext() ){
					DiffAttribute diffAttribute =  (DiffAttribute)modifiedAttributesIterator.next();
					SDLog.log("Change Type:" + diffAttribute.getChangeType() +" AttributeName: " +  diffAttribute.getName() + " | Source Value: " + diffAttribute.getDaksValue() + " | Target Value:" + diffAttribute.getConfigValue());
				}
				SDLog.log("********************************************************");
			}
		}
	}
}
