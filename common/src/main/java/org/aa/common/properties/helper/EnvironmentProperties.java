/**	   Copyright 


**/
package org.aa.common.properties.helper;

import java.util.Hashtable;
import java.util.Properties;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.properties.helper.PropertyLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class EnvironmentProperties {
	private static final Log logger  = LogFactory.getLog(EnvironmentProperties.class);
	private static Hashtable<String, Properties> propertiesTable = new Hashtable<String, Properties>();
	
	public Properties getProperties(DeployInfo deployInfo){
		Properties properties = new Properties();
			if (deployInfo.getEnvironmentProperties()!=null){
				if (propertiesTable.get(deployInfo.getEnvironmentProperties())!=null){
					logger.trace("Getting properties for " + deployInfo.getEnvironmentProperties() + " from cache");
					properties = propertiesTable.get(deployInfo.getEnvironmentProperties());
				}else{
					logger.trace("Getting properties for " + deployInfo.getEnvironmentProperties() + " from properties " );
					properties = PropertyLoader.loadProperties(deployInfo.getEnvironmentProperties() );
					propertiesTable.put(deployInfo.getEnvironmentProperties(),properties);
	
				}
			}
		properties.put("CellName", deployInfo.getCell());	
		return properties;
		
	}
	
	/**
	 * Called when connection is made to the server 
	 */
	public void clearCache(){
		System.out.println("Clearing Variables cache.");
		propertiesTable = new Hashtable<String, Properties>();
	}
}
