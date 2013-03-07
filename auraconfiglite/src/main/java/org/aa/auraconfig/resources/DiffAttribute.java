/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

public class DiffAttribute {
	
	private String name;
	
	private Object configValue;
	
	private Object daksValue;
	
	private String  changeType = "Modified";

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the configValue
	 */
	public Object getConfigValue() {
		return configValue;
	}

	/**
	 * @param configValue the configValue to set
	 */
	public void setConfigValue(Object configValue) {
		this.configValue = configValue;
	}

	/**
	 * @return the daksValue
	 */
	public Object getDaksValue() {
		return daksValue;
	}

	/**
	 * @param daksValue the daksValue to set
	 */
	public void setDaksValue(Object daksValue) {
		this.daksValue = daksValue;
	}

	/**
	 * @return the changeType
	 */
	public String getChangeType() {
		return changeType;
	}

	/**
	 * @param changeType the changeType to set
	 */
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	

}
