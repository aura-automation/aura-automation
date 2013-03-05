/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.auraconfig.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.apartech.auraconfig.resources.metadata.ResourceMetaData;
import com.apartech.common.log.SDLog;

public class Resource implements Cloneable{

	private static final Log logger = LogFactory.getLog(Resource.class);
	
	String name;
	
	HashMap attributeList;
	
	List<String> missingAttributeList=new ArrayList<String>();
	
	HashMap unresolvedAttributeList;
	
	// Vector of Resource
	Vector children;
	
	Vector parentTree;
	
	ResourceMetaData resourceMetaData;
	
	ArrayList attributeArray;

	Resource parent;
	
	String containmentPath;
	
	ObjectName configId;
	
	ObjectName scope;
	
	ArrayList<DiffAttribute> modifiedAttributes;
	
	ArrayList<InvalidAttribute> invalidAttributes;
	
	Vector<Resource> inComingChildren;
	
	int differentChildCount;
	
	boolean incoming = false;
	
	boolean dummy = false;

	static ResourceStats resourceStats = new ResourceStats();
	
	HashMap resourceAttrMetaInfo;
	
	InvalidAttribute invalidResource;
	
	boolean hasAnyChange = false;
	
    /**
	 * @return the hasAnyChange
	 */
	public boolean isHasAnyChange() {
		return hasAnyChange;
	}

	/**
	 * @param hasAnyChange the hasAnyChange to set
	 */
	public void setHasAnyChange(boolean hasAnyChange) {
		this.hasAnyChange = hasAnyChange;
	}

	public Object clone(){
        try{
            return super.clone();
        }catch( CloneNotSupportedException e ){
            return null;
        }
    } 

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
	 * @return the attributeList
	 */
	public HashMap getAttributeList() {
		return attributeList;
	}

	/**
	 * @param attributeList the attributeList to set
	 */
	public void setAttributeList(HashMap attributeList) {
		this.attributeList = attributeList;
	}

	/**
	 * @return the children
	 */
	public Vector<Resource> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Vector children) {
		this.children = children;
	}

	/**
	 * @return the attributeArray
	 */
	public ArrayList getAttributeArray() {
		return attributeArray;
	}

	/**
	 * @param attributeArray the attributeArray to set
	 */
	public void setAttributeArray(ArrayList attributeArray) {
		this.attributeArray = attributeArray;
	}

	/**
	 * @return the parent
	 */
	public Resource getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Resource parent) {
		this.parent = parent;
	}

	/**
	 * @return the containmentPath
	 */
	public String getContainmentPath() {
		return containmentPath;
	}

	/**
	 * @param containmentPath the containmentPath to set
	 */
	public void setContainmentPath(String containmentPath) {
		this.containmentPath = containmentPath;
	}

	/**
	 * @return the configId
	 */
	public ObjectName getConfigId() {
		return configId;
	}

	/**
	 * @param configId the configId to set
	 */
	public void setConfigId(ObjectName configId) {
		this.configId = configId;
	}

	/**
	 * @return the scope
	 */
	public ObjectName getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(ObjectName scope) {
		this.scope = scope;
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
	 * @return the resourceMetaData
	 */
	public ResourceMetaData getResourceMetaData() {
		return resourceMetaData;
	}

	/**
	 * @param resourceMetaData the resourceMetaData to set
	 */
	public void setResourceMetaData(ResourceMetaData resourceMetaData) {
		this.resourceMetaData = resourceMetaData;
	}

	/**
	 * @return the modifiedAttributes
	 */
	public ArrayList<DiffAttribute> getModifiedAttributes() {
		return modifiedAttributes;
	}

	/**
	 * @param modifiedAttributes the modifiedAttributes to set
	 */
	public void setModifiedAttributes(ArrayList<DiffAttribute> modifiedAttributes) {
		this.modifiedAttributes = modifiedAttributes;
	}
	
	
	public void addChild(Resource childResource) {
		if (children==null){
			children = new Vector();
		}
		this.children.add(childResource) ;
	}

	public void removeChildOfType(Resource childResource) {
		if (children!=null){
			Iterator it = children.iterator();
			while(it.hasNext()){
				if (((Resource)it.next()).getName().equalsIgnoreCase(childResource.getName())){
					logger.warn(" Remove object " + childResource.getName());
					it.remove();
				}
			}
			
		}
		
	}

	public void addInComingChild(Resource childResource) {
		if (inComingChildren == null){
			inComingChildren  = new Vector();
		}
		this.inComingChildren.add(childResource) ;
	}

	public void addInComingChildren(List childResources) {
		if (inComingChildren == null){
			inComingChildren  = new Vector();
		}
		this.inComingChildren.addAll(childResources) ;
	}

	/**
	 * @return the inComingChildren
	 */
	public Vector getInComingChildren() {
		return inComingChildren;
	}

	/**
	 * @param inComingChildren the inComingChildren to set
	 */
	public void setInComingChildren(Vector inComingChildren) {
		this.inComingChildren = inComingChildren;
	}

	/**
	 * @return the resourceStats
	 */
	public ResourceStats getResourceStats() {
		return resourceStats;
	}

	/**
	 * @return the incoming
	 */
	public boolean isIncoming() {
		return incoming;
	}

	/**
	 * @param incoming the incoming to set
	 */
	public void setIncoming(boolean incoming) {
		this.incoming = incoming;
	}

	/**
	 * @return the resourceAttrMetaInfo
	 */
	public HashMap getResourceAttrMetaInfo() {
		return resourceAttrMetaInfo;
	}

	/**
	 * @param resourceAttrMetaInfo the resourceAttrMetaInfo to set
	 */
	public void setResourceAttrMetaInfo(HashMap resourceAttrMetaInfo) {
		this.resourceAttrMetaInfo = resourceAttrMetaInfo;
	}

	/**
	 * @return the differentChildCount
	 */
	public int getDifferentChildCount() {
		return differentChildCount;
	}

	/**
	 * @param differentChildCount the differentChildCount to set
	 */
	public void setDifferentChildCount(int differentChildCount) {
		this.differentChildCount = differentChildCount;
	}

	/**
	 * @param differentChildCount the differentChildCount to set
	 */
	public void addDifferentChildCount() {
		this.differentChildCount ++;
		if (parent!=null)
			parent.addDifferentChildCount();
	}

	/**
	 * @return the invalidAttributes
	 */
	public ArrayList<InvalidAttribute> getInvalidAttributes() {
		return invalidAttributes;
	}

	/**
	 * @param invalidAttributes the invalidAttributes to set
	 */
	public void setInvalidAttributes(ArrayList<InvalidAttribute> invalidAttributes) {
		this.invalidAttributes = invalidAttributes;
	}

	/**
	 * @return the invalidResource
	 */
	public InvalidAttribute getInvalidResource() {
		return invalidResource;
	}

	/**
	 * @param invalidResource the invalidResource to set
	 */
	public void setInvalidResource(InvalidAttribute invalidResource) {
		this.invalidResource = invalidResource;
	}

	public HashMap getUnresolvedAttributeList() {
		return unresolvedAttributeList;
	}

	public void setUnresolvedAttributeList(HashMap unresolvedAttributeList) {
		this.unresolvedAttributeList = unresolvedAttributeList;
	}

	public List<String> getMissingAttributeList() {
		return missingAttributeList;
	}

	public void setMissingAttributeList(List<String> missingAttributeList) {
		this.missingAttributeList = missingAttributeList;
	}

	/**
	 * @return the dummy
	 */
	public boolean isDummy() {
		return dummy;
	}

	/**
	 * @param dummy the dummy to set
	 */
	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}



	

}
