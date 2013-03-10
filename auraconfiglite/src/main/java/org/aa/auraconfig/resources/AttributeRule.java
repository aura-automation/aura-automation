/**	   Copyright 


**/
package org.aa.auraconfig.resources;

public class AttributeRule {
	
	private String name;
	
	private String editable;
	
	private String pattern;

	private int min = -1;
	
	private int max = -1;
	
	boolean nillable = false;
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
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * @return the nillable
	 */
	public boolean isNillable() {
		return nillable;
	}

	/**
	 * @param nillable the nillable to set
	 */
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}

}
