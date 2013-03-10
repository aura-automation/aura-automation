/**	   Copyright 


**/
package org.aa.auraconfig.resources.metadata;

import java.util.HashMap;

public class StepCommandMetaData {

	private String stepName;
	
	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}

	/**
	 * @param stepName the stepName to set
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	/**
	 * @return the attributeMappings
	 */
	public HashMap<String, CommandAttribute> getAttributeMappings() {
		return attributeMappings;
	}

	/**
	 * @param attributeMappings the attributeMappings to set
	 */
	public void setAttributeMappings(
			HashMap<String, CommandAttribute> attributeMappings) {
		this.attributeMappings = attributeMappings;
	}

	private HashMap<String, CommandAttribute> attributeMappings;

}
