/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.auraconfig.resources.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import com.apartech.auraconfig.resources.ResourceRulesMetaData;

public class ResourceMetaData {

	private String mType;
	
	private boolean mSyncFindModeContainmentPath = false;
	
	private String syncPreMatchAttribute;
//	private String configType;
	
	private String matchAttribute;
	
	private String attributeName;
	
	private String mContainmentPath;
	
	private String mContainmentAttribute;
	
	private String[] mAdditionalContainmentAttribute;
	
	private String mParentType; 
	
	private Vector parentTree;

	private ResourceMetaData parent;
	
	private boolean shouldCreate = true;
	
	private boolean isProperty = false;
	
	private boolean isArray = false;
	
	private String returnAttribute ="";
	
	private boolean findAndResolve = false;
	
	private String relation ;
	
	private Vector children;
	
	private HashMap linkAttribute;

	private boolean attributeNameInResourceXML = false;
	
	private boolean attributeCount0 = false;

	private boolean shouldIncludeAllChildren = false;
	
	private boolean isApplicationManaged = false;
	
	private boolean isCommandManaged = false;

	private String customCodeManaged;

	private ResourceRulesMetaData resourceRulesMetaData;
	
	private String editable;
	
	private CommandMetaData commandMetaData;  
	
	private String customCodeAttributes;
	
	/**
	 * 
	 * @return
	 */
	public String getCustomCodeAttributes() {
		return customCodeAttributes;
	}

	/**
	 * 
	 * @param customCodeAttributes
	 */
	public void setCustomCodeAttributes(String customCodeAttributes) {
		this.customCodeAttributes = customCodeAttributes;
	}

	/**
	 * 
	 * @return
	 */
	public String getCustomCodeManaged() {
		return customCodeManaged;
	}

	/**
	 * 
	 * @param customCodeManaged
	 */
	public void setCustomCodeManaged(String customCodeManaged) {
		this.customCodeManaged = customCodeManaged;
	}

	/**
	 * @return the commandMetaData
	 */
	public CommandMetaData getCommandMetaData() {
		return commandMetaData;
	}

	/**
	 * @param commandMetaData the commandMetaData to set
	 */
	public void setCommandMetaData(CommandMetaData commandMetaData) {
		this.commandMetaData = commandMetaData;
	}

	/**
	 * @return the isCommandManaged
	 */
	public boolean isCommandManaged() {
		return isCommandManaged;
	}

	/**
	 * @param isCommandManaged the isCommandManaged to set
	 */
	public void setCommandManaged(boolean isCommandManaged) {
		this.isCommandManaged = isCommandManaged;
	}


	/**
	 * @return the linkAttribute
	 */
	public HashMap getLinkAttribute() {
		return linkAttribute;
	}

	/**
	 * @param linkAttribute the linkAttribute to set
	 */
	public void setLinkAttribute(HashMap linkAttribute) {
		this.linkAttribute = linkAttribute;
	}

	/**
	 * @return the mType
	 */
	public String getType() {
		return mType;
	}

	/**
	 * @param type the mType to set
	 */
	public void setType(String type) {
		mType = type;
	}

	/**
	 * @return the mContainmentPath
	 */
	public String getContainmentPath() {
		return mContainmentPath;
	}

	/**
	 * @param containmentPath the mContainmentPath to set
	 */
	public void setContainmentPath(String containmentPath) {
		mContainmentPath = containmentPath;
	}

	/**
	 * @return the mParentType
	 */
	public String getParentType() {
		return mParentType;
	}

	/**
	 * @param parentType the mParentType to set
	 */
	public void setParentType(String parentType) {
		mParentType = parentType;
	}

	/**
	 * @return the shouldCreate
	 */
	public boolean isShouldCreate() {
		return shouldCreate;
	}

	/**
	 * @param shouldCreate the shouldCreate to set
	 */
	public void setShouldCreate(boolean shouldCreate) {
		this.shouldCreate = shouldCreate;
	}

	/**
	 * @return the isProperty
	 */
	public boolean getIsProperty() {
		return isProperty;
	}

	/**
	 * @param isProperty the isProperty to set
	 */
	public void setIsProperty(boolean isProperty) {
		this.isProperty = isProperty;
	}

	/**
	 * @return the returnAttribute
	 */
	public String getReturnAttribute() {
		return returnAttribute;
	}

	/**
	 * @param returnAttribute the returnAttribute to set
	 */
	public void setReturnAttribute(String returnAttribute) {
		this.returnAttribute = returnAttribute;
	}

	/**
	 * @return the findAndResolve
	 */
	public boolean isFindAndResolve() {
		return findAndResolve;
	}

	/**
	 * @param findAndResolve the findAndResolve to set
	 */
	public void setFindAndResolve(boolean findAndResolve) {
		this.findAndResolve = findAndResolve;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * @return the mContainmentAttribute
	 */
	public String getContainmentAttribute() {
		return mContainmentAttribute;
	}

	/**
	 * @param containmentAttribute the mContainmentAttribute to set
	 */
	public void setContainmentAttribute(String containmentAttribute) {
		mContainmentAttribute = containmentAttribute;
	}

	/**
	 * @return the isArray
	 */
	public boolean isArray() {
		return isArray;
	}

	/**
	 * @param isArray the isArray to set
	 */
	public void setIsArray(boolean isArray) {
		this.isArray = isArray;
	}

	/**
	 * @return the children
	 */
	public Vector getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Vector children) {
		this.children = children;
	}

	/**
	 * @return the parent
	 */
	public ResourceMetaData getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(ResourceMetaData parent) {
		this.parent = parent;
	}

	/**
	 * @return the parentTree
	 */
	public Vector getParentTree() {
		return parentTree;
	}

	/**
	 * @param parentTree the parentTree to set
	 */
	public void setParentTree(Vector parentTree) {
		this.parentTree = parentTree;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the mSyncFindModeContainmentPath
	 */
	public boolean isSyncFindModeContainmentPath() {
		return mSyncFindModeContainmentPath;
	}

	/**
	 * @param syncFindModeContainmentPath the mSyncFindModeContainmentPath to set
	 */
	public void setSyncFindModeContainmentPath(boolean syncFindModeContainmentPath) {
		mSyncFindModeContainmentPath = syncFindModeContainmentPath;
	}

	/**
	 * @return the matchAttribute
	 */
	public String getMatchAttribute() {
		return matchAttribute;
	}

	/**
	 * @param matchAttribute the matchAttribute to set
	 */
	public void setMatchAttribute(String matchAttribute) {
		this.matchAttribute = matchAttribute;
	}

	/**
	 * @return the syncPreMatchAttribute
	 */
	public String getSyncPreMatchAttribute() {
		return syncPreMatchAttribute;
	}

	/**
	 * @param syncPreMatchAttribute the syncPreMatchAttribute to set
	 */
	public void setSyncPreMatchAttribute(String syncPreMatchAttribute) {
		this.syncPreMatchAttribute = syncPreMatchAttribute;
	}

	/**
	 * @return the attributeNameInResourceXML
	 */
	public boolean isAttributeNameInResourceXML() {
		return attributeNameInResourceXML;
	}

	/**
	 * @param attributeNameInResourceXML the attributeNameInResourceXML to set
	 */
	public void setAttributeNameInResourceXML(boolean attributeNameInResourceXML) {
		this.attributeNameInResourceXML = attributeNameInResourceXML;
	}

	/**
	 * @return the isAttributeCount0
	 */
	public boolean isAttributeCount0() {
		return attributeCount0;
	}

	/**
	 * @param isAttributeCount0 the isAttributeCount0 to set
	 */
	public void setAttributeCount0(boolean attributeCount0) {
		this.attributeCount0 = attributeCount0;
	}

	/**
	 * @return the shouldIncludeAllChildren
	 */
	public boolean isShouldIncludeAllChildren() {
		return shouldIncludeAllChildren;
	}

	/**
	 * @param shouldIncludeAllChildren the shouldIncludeAllChildren to set
	 */
	public void setShouldIncludeAllChildren(boolean shouldIncludeAllChildren) {
		this.shouldIncludeAllChildren = shouldIncludeAllChildren;
	}

	/**
	 * @return the isApplicationManaged
	 */
	public boolean isApplicationManaged() {
		return isApplicationManaged;
	}

	/**
	 * @param isApplicationManaged the isApplicationManaged to set
	 */
	public void setApplicationManaged(boolean isApplicationManaged) {
		this.isApplicationManaged = isApplicationManaged;
	}

	/**
	 * @return the resourceRulesMetaData
	 */
	public ResourceRulesMetaData getResourceRulesMetaData() {
		return resourceRulesMetaData;
	}

	/**
	 * @param resourceRulesMetaData the resourceRulesMetaData to set
	 */
	public void setResourceRulesMetaData(ResourceRulesMetaData resourceRulesMetaData) {
		this.resourceRulesMetaData = resourceRulesMetaData;
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
	 * @return the mAdditionalContainmentAttribute
	 */
	public String[] getAdditionalContainmentAttribute() {
		return mAdditionalContainmentAttribute;
	}

	/**
	 * @param mAdditionalContainmentAttribute the mAdditionalContainmentAttribute to set
	 */
	public void setAdditionalContainmentAttribute(
			String[] mAdditionalContainmentAttribute) {
		this.mAdditionalContainmentAttribute = mAdditionalContainmentAttribute;
	}
	
	
}
