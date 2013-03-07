/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

public class LinkAttribute {

	String linkAttibuteName;
	String targetObject;
	String targetAttribute;
	String targetObjectMatchAttributeName;
	LinkAttribute linkAttribute;
	/**
	 * @return the linkAttibuteName
	 */
	public String getLinkAttibuteName() {
		return linkAttibuteName;
	}
	/**
	 * @param linkAttibuteName the linkAttibuteName to set
	 */
	public void setLinkAttibuteName(String linkAttibuteName) {
		this.linkAttibuteName = linkAttibuteName;
	}
	/**
	 * @return the targetObject
	 */
	public String getTargetObject() {
		return targetObject;
	}
	/**
	 * @param targetObject the targetObject to set
	 */
	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}
	/**
	 * @return the targetAttribute
	 */
	public String getTargetAttribute() {
		return targetAttribute;
	}
	/**
	 * @param targetAttribute the targetAttribute to set
	 */
	public void setTargetAttribute(String targetAttribute) {
		this.targetAttribute = targetAttribute;
	}
	/**
	 * @return the targetObjectMatchAttributeName
	 */
	public String getTargetObjectMatchAttributeName() {
		return targetObjectMatchAttributeName;
	}
	/**
	 * @param targetObjectMatchAttributeName the targetObjectMatchAttributeName to set
	 */
	public void setTargetObjectMatchAttributeName(
			String targetObjectMatchAttributeName) {
		this.targetObjectMatchAttributeName = targetObjectMatchAttributeName;
	}
	/**
	 * @return the linkAttribute
	 */
	public LinkAttribute getLinkAttribute() {
		return linkAttribute;
	}
	/**
	 * @param linkAttribute the linkAttribute to set
	 */
	public void setLinkAttribute(LinkAttribute linkAttribute) {
		this.linkAttribute = linkAttribute;
	}
}
