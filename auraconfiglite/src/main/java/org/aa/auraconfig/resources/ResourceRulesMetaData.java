/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

import java.util.HashMap;

public class ResourceRulesMetaData {
	
	private String editable;
	
	private HashMap attributeRules;

	/**
	 * @return the editable
	 */
	public String getEditable() {
		return editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(String editable) {
		this.editable = editable;
	}

	/**
	 * @return the attributeRules
	 */
	public HashMap getAttributeRules() {
		return attributeRules;
	}

	/**
	 * @param attributeRules the attributeRules to set
	 */
	public void setAttributeRules(HashMap attributeRules) {
		this.attributeRules = attributeRules;
	}

}
