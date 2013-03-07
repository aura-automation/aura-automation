/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.common.properties.helper;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class PropertyHelper {

	private static final Log logger  = LogFactory.getLog(PropertyHelper.class);
	/**
	 * This is used in the report generation when we need to display 
	 * both the values, in the alt
	 * 
	 * @param attributeValue
	 * @param deployInfo
	 * @param isSource
	 * @return
	 * @throws DeployException
	 */
	public static String replaceVariable(String attributeValue,DeployInfo deployInfo,boolean isSource )
		throws DeployException{
		
		if (deployInfo.getSourceDeployInfo()!=null){
	/**		
	
			if (deployInfo.getSourceDeployInfo().getEnvironmentId()!= -1){
				PropertiesContext propertiesContext =PropertiesContext.getInstance();
				properties= propertiesContext.getEnvironmentVariableProperties(deployInfo.getEnvironmentId());
				System.out.println("Getting properties for " + deployInfo.getEnvironmentId());
			}else{
				properties = PropertyLoader.loadProperties(deployInfo.getSourceDeployInfo().getEnvironmentProperties() );
				System.out.println("Getting properties for " + deployInfo.getEnvironmentProperties() );
			}
			**/
			EnvironmentProperties environmentProperties = new EnvironmentProperties ();
			Properties properties = environmentProperties.getProperties(deployInfo.getSourceDeployInfo()); 
			return replaceVariable(attributeValue,deployInfo,properties);
		}else{
			return attributeValue;
		}
	}

	/**
	 * This is used to get the variable for the target
	 * @param attributeValue
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 */
	//bhumika
	public static String replaceVariable(String attributeValue,DeployInfo deployInfo)
		throws DeployException{
		
		/**
		Properties properties;
		properties = PropertyLoader.loadProperties(deployInfo.getEnvironmentProperties() );
	
		if (deployInfo.getEnvironmentId()!= -1){
			PropertiesContext propertiesContext =PropertiesContext.getInstance();
			properties= propertiesContext.getEnvironmentVariableProperties(deployInfo.getEnvironmentId());
			System.out.println("Getting properties for " + deployInfo.getEnvironmentId());
		}else{
			properties = PropertyLoader.loadProperties(deployInfo.getEnvironmentProperties() );
			System.out.println("Getting properties for " + deployInfo.getEnvironmentProperties() );
		} **/ 
		EnvironmentProperties environmentProperties = new EnvironmentProperties ();
		Properties properties = environmentProperties.getProperties(deployInfo); 
	
		return replaceVariable(attributeValue,deployInfo,properties);
		
	}

	/**
		*  LOOP THROUGH CHR TILL LAST INDEX
		* SET CURRENT CHR NO
		* GET INDEX OF % AFTER CURRENT CHR NO
		* SET START VAR INX
		* GET INDEX OF END VAR AFTER START VAR INX
		* GET VAR NAME BETWEEN ABOVE 2
		* REPLACE VAR WITH VALUE
		* GET STRING FROM CURRENT CHR TILL START VAR INX
		* APPEND TO NEW STRG
		* APPEND TO REPLACE VAR TO NEW STR
		* SET CURRENT CHR TO END VAR INDEX
		* IF INDEX OF LAST END VAR    != LENGTH
		* APPEND LAST INX TO LENGTH
	 * @param attributeValue
	 * @return
	 * @throws DeployException
	 * TODO: If there are more then one variables in same string then this code will not work
	 */
	
		public static String replaceVariable(String attributeValue,DeployInfo deployInfo,Properties properties)
			throws DeployException{
			
			
			StringBuffer newAttributeValue = new StringBuffer();
			if((attributeValue.indexOf(PropertiesConstant.VARIABLE_PREFIX)>-1) && (attributeValue.indexOf(PropertiesConstant.RESOURCE_REFERENCE_PREFIX)== -1)){
	
			
				for (int i = 0 ; i <= attributeValue.lastIndexOf(PropertiesConstant.VARIABLE_PREFIX);  ){
					int prefixIndex =  attributeValue.indexOf(PropertiesConstant.VARIABLE_PREFIX, i);
					int suffixIndex =  attributeValue.indexOf(PropertiesConstant.VARIABLE_SUFFIX, prefixIndex);
					String variableName = 	getVariableName(attributeValue.substring(prefixIndex, suffixIndex + PropertiesConstant.VARIABLE_SUFFIX.length()));
					
					if (properties.get(variableName)==null) {
						logger.trace("Variable " + variableName + " is not defined");
						return null;	
					}
					String value =  properties.get(variableName).toString();
					
					/**
					 * If the value is absolute value like 'ME-DA' then trim the single quotes.
					 */
					if (value.startsWith("'")){
						value=value.trim();
						value = value.substring(1,value.length()-1);
					}
					newAttributeValue.append(attributeValue.substring(i,prefixIndex));
					newAttributeValue.append(value);
					
					i = suffixIndex + PropertiesConstant.VARIABLE_SUFFIX.length();
				}
				if (attributeValue.length()  != attributeValue.lastIndexOf(PropertiesConstant.VARIABLE_SUFFIX)){
					newAttributeValue.append(attributeValue.substring(attributeValue.lastIndexOf(PropertiesConstant.VARIABLE_SUFFIX) + PropertiesConstant.VARIABLE_SUFFIX.length() , attributeValue.length())); 
				}
			}else{
				newAttributeValue.append(attributeValue);
			}
			
			return newAttributeValue.toString();
			
		}

	/**
		 * Get all the properties value
		 * Loop through each property and check if the property value is in the attributeValue
		 * If above is true
		 * Get first occurance of the value
		 * Get 2 parts of the attributeValue, from start till first occurance 
		 * 			and from end the propertyValue till end
		 * Create new string made of 2 parts and propertyName.
		 * Again check if the propertyValue has any more occurance.
		 * If not then move to next popetry Value.
		 *     
		*  LOOP THROUGH CHR TILL LAST INDEX
		* SET CURRENT CHR NO
		* GET INDEX OF % AFTER CURRENT CHR NO
		* SET START VAR INX
		* GET INDEX OF END VAR AFTER START VAR INX
		* GET VAR NAME BETWEEN ABOVE 2
		* REPLACE VAR WITH VALUE
		* GET STRING FROM CURRENT CHR TILL START VAR INX
		* APPEND TO NEW STRG
		* APPEND TO REPLACE VAR TO NEW STR
		* SET CURRENT CHR TO END VAR INDEX
		* IF INDEX OF LAST END VAR    != LENGTH
		* APPEND LAST INX TO LENGTH
		* 
		* This method also checks if the value of the variable if between single quotes 
		* then replace full variable only
		* 
	 * @param attributeValue
	 * @return
	 * @throws DeployException
	 * TODO: If there are more then one variables in same string then this code will not work
	 */
	public static String replaceVariableValueWithVariable(String attributeKey,String attributeValue, 
			DeployInfo deployInfo,HashMap<String, String> attrMap)
	{
	
		
		
		/**
		Properties properties; 
	
		if (deployInfo.getEnvironmentId()!= -1){
			PropertiesContext propertiesContext =PropertiesContext.getInstance();
			properties= propertiesContext.getEnvironmentVariableProperties(deployInfo.getEnvironmentId());
			System.out.println("Getting properties for " + deployInfo.getEnvironmentId());
	
		}else{
			properties = PropertyLoader.loadProperties(deployInfo.getEnvironmentProperties());
			System.out.println("Getting properties for " + deployInfo.getEnvironmentProperties());
		} 
		**/
		
		/**
		if (!((attrMap==null) || (attrMap.get(attributeKey)==null))){
			String value = attrMap.get(attributeKey).toString();
			return value;
		}else{
		**/
		
			logger.trace("	Matching variable value " + attributeValue);
			EnvironmentProperties environmentProperties = new EnvironmentProperties ();
			Properties properties = environmentProperties.getProperties(deployInfo); 
		
			StringBuffer oldAttributeValue = new StringBuffer(attributeValue);
			Iterator keys = properties.keySet().iterator();
			
			boolean matchFound = false;
			while(keys.hasNext()){
				String propertyKey = keys.next().toString();
	
				String propertyValue = properties.get(propertyKey).toString().trim();
				
				if (propertyValue.trim().length()>0){
					/**
					 * if property value is blank then ignore this property
					 */
				
					if (propertyValue.startsWith("'")){
						int index = oldAttributeValue.indexOf(propertyValue.substring(1, propertyValue.length()-1));
						if ((index==0) && (oldAttributeValue.length()== (propertyValue.length()-2))){
							matchFound = true;
							StringBuffer newAttributeValue = new StringBuffer();
							newAttributeValue.append(PropertiesConstant.VARIABLE_PREFIX);
							newAttributeValue.append(propertyKey);
							newAttributeValue.append(PropertiesConstant.VARIABLE_SUFFIX);
							oldAttributeValue = newAttributeValue;
						}
					}else{
						int index = oldAttributeValue.indexOf(propertyValue);
						while((oldAttributeValue.indexOf(propertyValue)>-1) && (!((index  >= oldAttributeValue.indexOf(PropertiesConstant.VARIABLE_PREFIX) &&	(index  <= oldAttributeValue.indexOf(PropertiesConstant.VARIABLE_SUFFIX)))))){
							matchFound = true;
							int prefixIndex =  oldAttributeValue.indexOf(propertyValue);
							int suffixIndex =  prefixIndex  + propertyValue.length();
							
							StringBuffer newAttributeValue = new StringBuffer();
							newAttributeValue.append(PropertiesConstant.VARIABLE_PREFIX);
							newAttributeValue.append(propertyKey);
							newAttributeValue.append(PropertiesConstant.VARIABLE_SUFFIX);
							oldAttributeValue.replace(prefixIndex, suffixIndex, newAttributeValue.toString()) ;
							
							}
						
					}
				}
			}
			if (matchFound ){
				logger.trace("	Returning variable value " + oldAttributeValue.toString());
				return oldAttributeValue.toString();
			}else{
				logger.trace("	Returning variable value " + attributeValue);
				return attributeValue;
			}

	//	}		
		
		
	}

	/**
	 * 
	 * @param attributeValue
	 * @return
	 */
	public static ArrayList getArrayFromCommaSeperated(String attributeValue){
		ArrayList arrayList = new ArrayList();

		if ((attributeValue!=null) && attributeValue.length()>0){
			StringBuffer semicommaSeperatedString  = new StringBuffer(attributeValue);

			while(semicommaSeperatedString.indexOf(";")>-1){
				int i = semicommaSeperatedString.indexOf(";");
				String value = semicommaSeperatedString.substring( 0,i);
				arrayList.add(value);
				
				semicommaSeperatedString = new StringBuffer( semicommaSeperatedString.substring(i+1));
			}
			if (semicommaSeperatedString.indexOf(";")==-1){
				arrayList.add(semicommaSeperatedString.toString());
			}
		}
		return arrayList; 

		/**
		StringBuffer semicommaSeperatedString  = new StringBuffer(attributeValue);
		ArrayList arrayList = new ArrayList();

		while(semicommaSeperatedString.indexOf(";")>-1){
			int i = semicommaSeperatedString.indexOf(";");
			String value = semicommaSeperatedString.substring( 0,i);
			arrayList.add(value);
			
			semicommaSeperatedString = new StringBuffer( semicommaSeperatedString.substring(i+1));
		}
		
		return arrayList;
		**/ 
	} 
	public static String getVariableName(String attributeValue){
		return attributeValue.substring(attributeValue.indexOf(PropertiesConstant.VARIABLE_PREFIX)+PropertiesConstant.VARIABLE_PREFIX.length(),attributeValue.indexOf(PropertiesConstant.VARIABLE_SUFFIX ));
	}

}
