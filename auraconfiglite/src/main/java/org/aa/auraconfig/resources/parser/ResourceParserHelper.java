/**	   Copyright [2009] [www.apartech.com]


 **/
package org.aa.auraconfig.resources.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceConstants;
import org.aa.auraconfig.resources.ResourceHelper;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertiesConstant;
import org.aa.common.properties.helper.PropertyHelper;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ResourceParserHelper {
	private static final Log logger  = LogFactory.getLog(ResourceParserHelper.class);	
	List<String> missingVariableList=new ArrayList<String>();
	/**
	 * loop through children in the XML node
	 * process all the attributes for this node and create HashMap of attributes and assign to the Java Resource
	 * for each children set the parent, type and attibutes.
	 * 
	 * @param node
	 * @param _resource
	 */
	public Vector createResourceJavaObject(Element node, Resource _resource,InputStream resourceXMLFilePathInputStream,DeployInfo deployInfo ) 
	throws DeployException,IOException,JDOMException{

		List xmlChildren;

		if (node.getName().equalsIgnoreCase(ResourceConstants.IMPORT)){
			xmlChildren = getImportedElement(node, deployInfo.getResourceXML(),resourceXMLFilePathInputStream);
			// get children of the node given to process
		}else{
			xmlChildren= node.getChildren();

			// create a empty Vector for Java children, will add it to the resource at end
			// set the name to the current resource from the current node as it is not done
			_resource.setName(node.getName());
			processAtrributes (node, _resource,deployInfo);
		}
		Vector children = new Vector();

		for (int i =0; i < xmlChildren.size(); i++){

			if (((Element)xmlChildren.get(i)).getName().equalsIgnoreCase(ResourceConstants.IMPORT)){
				children.addAll(createResourceJavaObject((Element)xmlChildren.get(i),_resource,resourceXMLFilePathInputStream,deployInfo));


			}else{

				logger.trace("   Parsing resource.xml, Resource Type:" + ((Element)xmlChildren.get(i)).getName());
				// create empty child Java Resource and the name of the current xml child
				Resource childResource = new Resource();
				childResource.setName(((Element)xmlChildren.get(i)).getName());
				logger.trace("   Setting parent to child " + childResource.getName() + " is " + _resource.getName());
				childResource.setParent(_resource);
				processAtrributes((Element)xmlChildren.get(i),childResource,deployInfo);


				// If current child has further children then call createResource again



				if (((((Element)xmlChildren.get(i)).getChildren()!=null) && (((Element)xmlChildren.get(i)).getChildren().size() >0 ))){
					createResourceJavaObject((Element)xmlChildren.get(i),childResource,resourceXMLFilePathInputStream,deployInfo);
					logger.trace("   Setting parent to child " + childResource.getName() + " is " + _resource.getName());
					childResource.setParent(_resource);
				}
				children.add(childResource);
			}
		}
		_resource.setChildren(children);
		return children;
	}

	/**
	 * 
	 * @param _resource
	 * @throws DeployException
	 */
	public void setParentTreePath(Resource _resource)
	throws DeployException{

		Resource resource = _resource;
		if ((resource.getChildren()!=null) && (resource.getChildren().size()>0)){
			Resource[] children = (Resource[]) resource.getChildren().toArray(new Resource[0]);

			for (int childCnt=0;childCnt < children.length;childCnt++){
				Resource childResource = children[childCnt];

				logger.trace("Setting resource parent tree for " + childResource.getName());

				Vector resourceParentNames = new Vector();
				Resource parentResource = childResource.getParent();
				while (parentResource.getParent()!=null){

					resourceParentNames.add(parentResource.getName());
					logger.trace("	Parent " + parentResource.getName());
					parentResource = parentResource.getParent();
				}
				if ((childResource.getChildren()!=null) && (childResource.getChildren().size()>0)){
					setParentTreePath(childResource);
				}
				childResource.setParentTree(resourceParentNames);




				/**				String containmentPath = resourceHelper.getFullContainmentPathForResource(resourceMetaDataMap, children[childCnt]);
				logger.trace(" Containment Path for " + type  + " = " + containmentPath  );
				children[childCnt].setContainmentPath(containmentPath);
				if (children[childCnt].getChildren()!=null){
					if (children[childCnt].getChildren().size()>0){
						setContainmentPath(children[childCnt], resourceMetaDataMap);
					}
				}
			}**/
			}
		}

	}
	/**
	 * This is done, so that when a call is made from was config reader for a resource that does 
	 * exist in meta data then assign defaultmetadata created in was config reader.
	 * 
	 * if meta exists then this method will not be called

	 * search for meta data in meta data xml
	 * or get from config service
	 * @param resources
	 * @param fullResourceMetaData
	 * @param configService
	 * @throws DeployException
	 */
	public void setResourceMetaData(Resource resources , ResourceMetaData fullResourceMetaData,ConfigService configService,
			ResourceMetaData defaultResourceMetaData)
	throws DeployException,ConnectorException,ConfigServiceException{
		
		boolean matchFound = false;
		//	Vector matchingResource = new Vector(); 
		Vector matchingResourceMetaData = getResourceMetaData(resources.getName(),fullResourceMetaData);
		/**
		 * We are creating a default metadata from config object
		 */

		logger.trace("Found Resource Meta data for resource, "+ resources.getName() + " count "+  matchingResourceMetaData.size());

		for (int i=0;i < matchingResourceMetaData.size();i++){
			ResourceMetaData  resourceMetaData = (ResourceMetaData)matchingResourceMetaData.get(i);
			logger.trace(" Matching resourceMetaData " + resourceMetaData.getType() + " to " + resources.getName() );
			logger.trace(" resourceMetaData tree is " + resourceMetaData.getParentTree() + " and resource parent tree is " + resources.getParentTree() );

			if (matchResourceTree(resourceMetaData.getParentTree(),resources.getParentTree())){
				if(resources.getAttributeList().get(ResourceConstants.ATTRUBUTENAME) ==null){ 
					logger.trace(" Trees matched and Setting MetaData for " + resources.getName());

					resources.setResourceMetaData(resourceMetaData);
					matchFound = true;
				}else{
					if  (resources.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString().equalsIgnoreCase(resourceMetaData.getAttributeName())){
						logger.trace(" There are 2 attributes of same type but different name for " + resources.getName() );
						logger.trace(" Trees matched for " + resources.getName() );
						logger.trace(" Setting MetaData for " + resources.getName());
						resources.setResourceMetaData(resourceMetaData); 
						matchFound = true;
					}
				}
			}else{
				logger.trace(" Parent Tree did not match for " + resources.getName() );
			}
		}
		/**
		 * This is done, so that when a call is made from was config reader for a resource that does 
		 * exist in meta data then assign defaultmetadata created in was config reader.
		 * 
		 * if meta exists then this method will not be called
		 **/
		if (!matchFound) {
			if (resources.getParent()!=null){
				SDLog.log("************* Resource type " + resources.getName() + "[" + resources.getParent().getContainmentPath()  + "] not supported. ********* "  );
			}else{
				if (!resources.getName().equalsIgnoreCase("resources"))
					SDLog.log("************* Resource type " + resources.getName() + " not supported. ********* "  );
			}
			//ResourceMetaData parentMetaData =  resources.getParent().getResourceMetaData();
			if (defaultResourceMetaData==null){
				logger.trace("Call Create default resource meta data");
				createDefaultResourceMetaData(resources,configService);
			}else{
				logger.trace("Set default resource meta data");
				resources.setResourceMetaData(defaultResourceMetaData);
			} 
		}
		
		if (resources.getChildren()!=null){
			Iterator childrenIterator = resources.getChildren().iterator();
			while (childrenIterator.hasNext()){
				Resource child = (Resource)childrenIterator.next();
				setResourceMetaData(child, fullResourceMetaData,configService,null);
			} 
		}
	}


	public void setRulesMetaData(Resource resources , ResourceMetaData fullRulesMetaData)
		throws DeployException,ConnectorException,ConfigServiceException{
		boolean matchFound = false;
		//	Vector matchingResource = new Vector(); 
		Vector matchingRulesMetaData = getResourceMetaData(resources.getName(),fullRulesMetaData);
		/**
		 * We are creating a default metadata from config object
		 */
		logger.trace("Found Rules Meta data for resource, "+ resources.getName() + " count "+  matchingRulesMetaData.size());

		for (int i=0;i < matchingRulesMetaData.size();i++){
			ResourceMetaData  rulesMetaData = (ResourceMetaData)matchingRulesMetaData.get(i);

			if (matchResourceTree(rulesMetaData.getParentTree(),resources.getParentTree())){
				if(resources.getAttributeList().get(ResourceConstants.ATTRUBUTENAME) ==null){ 
					logger.trace("Setting Rules Metadata in resource meta data from rules meta data for " + resources.getContainmentPath() + " as " + rulesMetaData.getResourceRulesMetaData());
					resources.getResourceMetaData().setResourceRulesMetaData(rulesMetaData.getResourceRulesMetaData()) ;
					logger.trace("Setting editable in resource  " + resources.getContainmentPath() + " meta data from rules meta data " + rulesMetaData.getEditable());
					resources.getResourceMetaData().setEditable(rulesMetaData.getEditable()) ;
					matchFound = true;
				}else{
					if  (resources.getAttributeList().get(ResourceConstants.ATTRUBUTENAME).toString().equalsIgnoreCase(resources.getResourceMetaData().getAttributeName())){
						logger.trace(" There are 2 attributes of same type but different name for " + resources.getName() );
						logger.trace("Setting Rules Metadata in resource meta data from rules meta data for " + resources.getContainmentPath() + " as " + rulesMetaData.getResourceRulesMetaData());

						resources.getResourceMetaData().setResourceRulesMetaData(rulesMetaData.getResourceRulesMetaData()) ;
						logger.trace("Setting editable in resource " + resources.getContainmentPath() +" meta data from rules meta data " + rulesMetaData.getEditable());
						resources.getResourceMetaData().setEditable(rulesMetaData.getEditable()) ;
						matchFound = true;
					}
				}
			}else{
				logger.trace(" Parent Tree did not match for " + resources.getName() );
			}
		}


		if (resources.getChildren()!=null){
			Iterator childrenIterator = resources.getChildren().iterator();
			while (childrenIterator.hasNext()){
				Resource child = (Resource)childrenIterator.next();
				setRulesMetaData(child, fullRulesMetaData);
			} 
		}
	}


	/**
	 * create meta data from config service
	 * @param resource
	 * @param configService
	 */
	private void  createDefaultResourceMetaData(Resource resource,ConfigService configService)
	throws DeployException, ConnectorException,ConfigServiceException{
		if (!(resource.getName()== null  || resource.getName().equalsIgnoreCase("resources") )){
			logger.trace( "*************** Getting relationships for " +  resource.getName());
			//logger.trace( configService.getRelationshipsMetaInfo(resource.getName()));
			logger.trace( "*************** ");

			logger.trace( "*************** Getting attributes metainfo for " +  resource.getName());
			//	logger.trace( configService.getAttributesMetaInfo(resource.getName()));
			logger.trace( "*************** ");
			ResourceMetaData metadata = new ResourceMetaData();
			ResourceMetaData parentmetadata = resource.getParent().getResourceMetaData();
			metadata.setParent(resource.getParent().getResourceMetaData());
			metadata.setType(resource.getName());
			metadata.setContainmentAttribute("null");
			metadata.setContainmentPath("null");
			setParentTreePath(resource);
			//setContainmentPath(resource.getParent());

			resource.setResourceMetaData(metadata);
		}
	}

	/**
	 * for each resource
	 *  get full containmentpath
	 *  and set it
	 *  Do this for all children
	 * @param _resource
	 * @param resourceMetaDataMap
	 * @throws DeployException
	 */
	/**
	 *  Loop through resource.
	 *  Get 1st resource (e.g. Cell)
	 *  Get parent (e.g. resources(TOP))
	 *  
	 *  Loop through resourceMetadata (get Type matching)
	 *  Get Parent. check if it matches
	 */
	public void setContainmentPath(Resource _resource)
	throws DeployException{
		ResourceHelper	resourceHelper = new ResourceHelper();

		if (_resource.getChildren() != null){
			Iterator children =	_resource.getChildren().iterator();
			while(children.hasNext()){
				Resource child = (Resource)children.next();
				logger.trace(" Getting Containment Path for " + child.getName() );

				String containmentPath = resourceHelper.getFullContainmentPathForResource(child);
				logger.trace(" Containment Path for " + child.getName() + " = " + containmentPath  );

				child.setContainmentPath(containmentPath);
				if (child.getChildren()!=null){
					setContainmentPath(child);
				}

			}
		}

	}
	/**
	 * 
	 * @param resourceTree
	 * @param resourceMetaDataTree
	 * @return
	 */
	private boolean matchResourceTree(Vector resourceTree,Vector resourceMetaDataTree){
		boolean match = true;
		for (int i = 0; i < resourceTree.size() && match;i++){
			if ((resourceMetaDataTree.size() < i) || (!resourceTree.get(i).toString().equalsIgnoreCase(resourceMetaDataTree.get(i).toString()))){
				match = false;
			}
		}
		return match;
	}

	/**
	 * 
	 * @param resourceName
	 * @param resourceMetaData
	 * @return
	 */
	private Vector getResourceMetaData(String resourceName, ResourceMetaData resourceMetaData){
		Vector matchingResourceMetaData = new Vector(); 
		/**	if ((resourceMetaData.getType()!= null) && resourceMetaData.getType().equalsIgnoreCase(resourceName)){
			matchingResourceMetaData.add(resourceMetaData); 
		} **/
		if ((resourceMetaData.getChildren()!= null) && resourceMetaData.getChildren().size()>0){
			Vector children = resourceMetaData.getChildren();
			for (int i=0; i<children.size();i++){
				ResourceMetaData child = (ResourceMetaData )children.get(i);
				if ((child.getType()!= null) && child.getType().equalsIgnoreCase(resourceName)){
					matchingResourceMetaData.add(child); 
				}
				if (child.getChildren()!=null){
					if (getResourceMetaData(resourceName,child).size()>0)
						matchingResourceMetaData.addAll(getResourceMetaData(resourceName,child) );
				}
			}
		}
		return matchingResourceMetaData;
	}


	/**
	 * Method to add attributes to the new Resource Java object.
	 * @author Jatin Bhadra
	 * @param node
	 * @param _resource
	 */
	private void processAtrributes(Element node, Resource _resource,DeployInfo deployInfo )
	throws DeployException{
		boolean check=false;
		String attribute="";
		HashMap attributeMap = new HashMap();
		HashMap unresolvedMap=new HashMap();

		List attributes =  node.getAttributes();
		// This logic is implemented to pick up the data that is not name value attribute.
	//	List contents = node.getTextTrim();
		ArrayList<String> missingVariables;

		//if (_resource.getResourceMetaData().getContainmentAttribute().equalsIgnoreCase(ResourceConstants.DEFAULT_ATTR)){
	//		for (int i =0; i< contents.size(); i++){
		if ((node.getTextTrim()!= null) && node.getTextTrim().length()>0){ 
		//	System.out.println(" adding attribute _defaultAttr " +  node.getTextTrim());
			attributeMap.put("_defaultAttr", node.getTextTrim());
			unresolvedMap.put("_defaultAttr", node.getTextTrim());
		}
				// attributeMap.put("_defaultAttr", contents.get(i).toString() );
	//		}
		//}
			
		/**
		if ((attributes.size() == 0 ) && (contents.size() >0)){
			for (int i =0; i< contents.size(); i++){
				attributeMap.put("_defaultAttr", contents.get(i).toString() );
			}
		}else{
		**/	
			for (int i =0; i< attributes.size(); i++){
				check=false;
				logger.trace("Parsing resource XML Attribute Name:" + ((org.jdom.Attribute)attributes.get(i)).getName());

				String variableValue = PropertyHelper.replaceVariable(((org.jdom.Attribute)attributes.get(i)).getValue(),deployInfo);
				//	if (variableValue == null){
				//		missingVariables.add( (org.jdom.Attribute)attributes.get(i)).getValue());
				//	}
				logger.trace("Parsing resource XML Attribute Value:" + variableValue );


				if(variableValue!=null)
				{
					unresolvedMap.put(((org.jdom.Attribute)attributes.get(i)).getName(), ((org.jdom.Attribute)attributes.get(i)).getValue());
					attributeMap.put(((org.jdom.Attribute)attributes.get(i)).getName(), variableValue );
				}
				else
				{
					attribute=((org.jdom.Attribute)attributes.get(i)).getValue();
					for (int j = 0 ; j<=attribute.lastIndexOf(PropertiesConstant.VARIABLE_PREFIX);  ){
						int prefixIndex =  attribute.indexOf(PropertiesConstant.VARIABLE_PREFIX, j);
						int suffixIndex =  attribute.indexOf(PropertiesConstant.VARIABLE_SUFFIX, j);
						String variableName = 	getVariableName(attribute.substring(prefixIndex, suffixIndex + PropertiesConstant.VARIABLE_SUFFIX.length()));
						if(variableName!=null)
						{
							variableName=variableName.replace(PropertiesConstant.VARIABLE_PREFIX, "");
							variableName=variableName.replace(PropertiesConstant.VARIABLE_SUFFIX, "");

							for(int index=0;index<missingVariableList.size();index++)
							{
								if(missingVariableList.get(index).equals(variableName))
								{
									check=true;
									break;
								}
							}
							if(check==false)
								missingVariableList.add(variableName);
						}
						j = suffixIndex + PropertiesConstant.VARIABLE_SUFFIX.length();

					}
				}
			}
		/** } **/
		_resource.setUnresolvedAttributeList(unresolvedMap);
		_resource.setAttributeList(attributeMap);
		_resource.setMissingAttributeList(missingVariableList);
	}

	private List getImportedElement(Element child , String resourceXMLFilePath,InputStream resourceXMLFilePathInputStream)
	throws JDOMException,IOException,DeployException{

		if (child.getAttribute(ResourceConstants.FILE)!=null){
			String filePath = child.getAttribute(ResourceConstants.FILE).getValue();

			if (resourceXMLFilePathInputStream!=null){
				InputStream referencedResourceXML =  Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
				SAXBuilder builder = new SAXBuilder();
				Document importedDoc = builder.build(referencedResourceXML);
				return importedDoc.getRootElement().getChildren();
			}else{
				File importedFile = new File(filePath);
				if (!importedFile.isAbsolute()){
					File resourceXMLFile = new File(resourceXMLFilePath);
					String resourceXMLParent = resourceXMLFile.getParent();
					filePath =  resourceXMLParent + File.separator + filePath ; 
				}
				importedFile = new File(filePath); 
				if (!importedFile.exists()){
					SDLog.log(filePath + " file does not exists.");
					throw new DeployException(new Exception(filePath + " file does not exists."));

				}else{
					SAXBuilder builder = new SAXBuilder();
					Document importedDoc = builder.build(importedFile);
					return importedDoc.getRootElement().getChildren();
				}
			}
		}
		return null;
	}
	/**
	public void addImportedFileToDOM (Element currentElement, Element originalElement,String resourceXMLFilePath)
		throws DeployException,JDOMException,IOException{

		Iterator rootResourcesIterator =  currentElement.getChildren().iterator();
		String parentName = currentElement.getName();

		while (rootResourcesIterator.hasNext()){
			Element child = (Element)rootResourcesIterator.next();
			Element originalChild = originalElement.getChild(child.getName() );

			if (child.getName().equalsIgnoreCase(IMPORT)){

						Iterator importedResourcesChildren= importedDoc.getRootElement().getChildren().iterator();
						while(importedResourcesChildren.hasNext()){
							Element importedChild = (Element)importedResourcesChildren.next();
							importedResourcesChildren.remove();
							// List childList = importedResourcesElement.cloneContent();
//							rootResourcesIterator.
							originalChild.getParentElement().addContent(importedChild);
						}
					}

				}else{
					SDLog.log("Import must have a attribute file.");
					throw new DeployException(new Exception("Import must have a attribute file."));
				}
				rootResourcesIterator.remove();
			} 


			//rootResourcesElement.removeChild(IMPORT);
			if (child.getChildren().size()>0){
				addImportedFileToDOM(child, originalChild, resourceXMLFilePath);
			}
		}

	}

/**	public void removeImportedElement(Element parent,String resourceXMLFilePath,String resourceXMLMetaDataFilePath,boolean validate)
	throws DeployException,JDOMException,IOException{
		if(parent==null){
			parent = ResourceXMLParser.getResourcesXMLElements(resourceXMLFilePath, resourceXMLMetaDataFilePath, validate);
		}
		Iterator children = parent.getChildren().iterator();
		while (children.hasNext() ){
			Element child = (Element)children.next();
			if (child.getName().equalsIgnoreCase(IMPORT)){
				//SDLog.log( "" + parent.detach()removeContent(child));
				SDLog.log( "" + child.detach());
				//child.removeContent();
			}

			//rootResourcesElement.removeChild(IMPORT);
			if (child.getChildren().size()>0){
				removeImportedElement(child,resourceXMLFilePath, resourceXMLMetaDataFilePath, validate);
			}
		}

	}
	 **/
	public static String getVariableName(String attributeValue){
		return attributeValue.substring(attributeValue.indexOf(PropertiesConstant.VARIABLE_PREFIX)+PropertiesConstant.VARIABLE_PREFIX.length(),attributeValue.indexOf(PropertiesConstant.VARIABLE_SUFFIX ));
	}
}
