/**	   Copyright 


**/
package org.aa.auraconfig.resources.metadata;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.aa.auraconfig.resources.AttributeRule;
import org.aa.auraconfig.resources.LinkAttribute;
import org.aa.auraconfig.resources.rules.ResourceRulesMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;

public class ResourceMetaDataHelper implements ResourceMetaDataConstants{
	private static final Log logger  = LogFactory.getLog(ResourceMetaDataHelper.class);	

	/**
	 * method to parse meta data
	 */
	private static ResourceMetaData fullResourceMetaData = null;
	
	public static ResourceMetaData  getFullResourceMetaData(){
		return fullResourceMetaData;
	}
	
	/**
	 * Used from UI to generate the tree.
	 * As we cannot return Resource Meta data we are creating a 
	 * new list of string array and sending that.
	 *  
	 * @return
	 */
/**	public static List parseMetaData()
		throws DeployException,IOException {
		InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
		try{
			ResourceMetaData metaData =  parseMetaData(resourceXMLMetaDataInputStream );
			ArrayList list = new ArrayList();
			String[] sibus = {"1","SIBUS","Cell:SIBUS"};
			list.add(sibus );
			return list;
		//	if (metaData.getChildren() !=null){
		//		convertMetaDatatoArray(metaData,)
		//	} 
		}catch(JDOMException e){
			throw new DeployException(e);
		}
	}
	**/
	private void convertMetaDatatoArray(ResourceMetaData metaData, ArrayList list){
		
	}
	
	/**
	 * This is used from the Resource Creator class
	 * @param resourceXMLMetaDataXMLFilePath
	 * @return
	 * @throws DeployException
	 * @throws IOException
	 * @throws JDOMException
	 */
	public ResourceMetaData parseMetaData(InputStream resourceXMLMetaDataXMLFilePath)
		throws DeployException,IOException,JDOMException {
		if (fullResourceMetaData ==null ){
	    	ResourceMetaData rootResourceMetaDataJavaObject = new ResourceMetaData();
			try{
				
		    	SAXBuilder builder = new SAXBuilder();
		    	Document doc = builder.build(resourceXMLMetaDataXMLFilePath);
	
		    	// Get the root element
		    	Element rootResourceMetaDataElement = doc.getRootElement();
		    	if (rootResourceMetaDataElement.getChildren().size()>0){
		    		rootResourceMetaDataElement.setName("Resources-MetaData");
					createResourceMetaDataJavaObject(rootResourceMetaDataElement, rootResourceMetaDataJavaObject,resourceXMLMetaDataXMLFilePath);
				}
		    	/** 
		    	 * moved this from resource parser to improve performance
		    	 */
		    	setParentTreePath(rootResourceMetaDataJavaObject);
		    	fullResourceMetaData = rootResourceMetaDataJavaObject;
	
			}catch(JDOMException e){
				e.printStackTrace();
				throw new DeployException(e);
			}catch(IOException e){
				e.printStackTrace();
				throw new DeployException(e);
			}
	    	return rootResourceMetaDataJavaObject;
		}else{
			
			return fullResourceMetaData;
		}
	}

	/**
	 * 
	 * @param resourceXMLMetaDataXMLFilePath
	 * @return
	 * @throws DeployException
	 * @throws IOException
	 * @throws JDOMException
	 */
	public ResourceMetaData parseRulesMetaData(InputStream resourceXMLMetaDataXMLFilePath)
		throws DeployException,IOException,JDOMException {
    	ResourceMetaData rootResourceMetaDataJavaObject = new ResourceMetaData();
		try{
			
	    	SAXBuilder builder = new SAXBuilder();
	    	Document doc = builder.build(resourceXMLMetaDataXMLFilePath);

	    	// Get the root element
	    	Element rootResourceMetaDataElement = doc.getRootElement();
	    	if (rootResourceMetaDataElement.getChildren().size()>0){
	    		rootResourceMetaDataElement.setName("Resources-MetaData");
				createResourceMetaDataJavaObject(rootResourceMetaDataElement, rootResourceMetaDataJavaObject,resourceXMLMetaDataXMLFilePath);
			}
	    	/** 
	    	 * moved this from resource parser to improve performance
	    	 */
	    	setParentTreePath(rootResourceMetaDataJavaObject);

		}catch(JDOMException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(IOException e){
			e.printStackTrace();
			throw new DeployException(e);
		}
    	return rootResourceMetaDataJavaObject;
	}
	
	/**
	 * 
	 * @param child
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 * @throws DeployException
	 */
	private static Element getImportedElement(Element child)
		throws JDOMException,IOException,DeployException{

		if (child.getAttribute(FILE)!=null){
			String filePath = child.getAttribute(FILE).getValue();
			InputStream resourceImportedXMLMetaData =  Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);

			if (resourceImportedXMLMetaData == null){
				SDLog.log(filePath + " file does not exists.");
				throw new DeployException(new Exception(filePath + " file does not exists."));
			
			}else{
				SAXBuilder builder = new SAXBuilder();
				Document importedDoc = builder.build(resourceImportedXMLMetaData);
				return importedDoc.getRootElement();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param node
	 * @param _resourceMetaData
	 * @param resourceMetaDataXMLFilePath
	 * @return
	 * @throws DeployException
	 * @throws IOException
	 * @throws JDOMException
	 */
	private static List createResourceMetaDataJavaObject(Element node, ResourceMetaData _resourceMetaData,InputStream resourceMetaDataXMLFilePath)
		throws DeployException,IOException,JDOMException{
		// get children of the node given to process
		List xmlChildren = node.getChildren();

		
		// create a empty Vector for Java children, will add it to the resource at end
		
		// set the name to the current resource from the current node as it is not done
		Vector children = new Vector();
		
		try{

				for (int i =0; i < xmlChildren.size(); i++){
					
				//	if (node.getAttribute(TYPE) != null){

				//		_resourceMetaData.setType(node.getAttribute(TYPE).getValue()) ;
				//		processAttributes(node,_resourceMetaData);					
				//		processContainmentAndLinkAttribute(node,_resourceMetaData);					
				//	}
					
					// create empty child Java Resource and the name of the current xml child
					ResourceMetaData childResourceMetaData = new ResourceMetaData();
					Element childNode = (Element)xmlChildren.get(i);
					if (childNode.getName().equalsIgnoreCase(IMPORT)){
						Element importedElement = getImportedElement(childNode);
						children.addAll (createResourceMetaDataJavaObject(importedElement,_resourceMetaData,resourceMetaDataXMLFilePath));
						

					}else{
						
						if (!(childNode.getAttribute(TYPE) == null)){
							childResourceMetaData.setType(childNode.getAttribute(TYPE).getValue()) ;
							logger.trace("   Setting parent to child " + childResourceMetaData.getType() + " is " + _resourceMetaData.getType());
							childResourceMetaData.setParent(_resourceMetaData);
							processAttributes((Element)xmlChildren.get(i),childResourceMetaData);					
							processContainmentAndLinkAttribute((Element)xmlChildren.get(i),childResourceMetaData);					
							processMappingAttribute((Element)xmlChildren.get(i),childResourceMetaData);
							if ((((Element)xmlChildren.get(i)).getChildren()!=null) && (((Element)xmlChildren.get(i)).getChildren().size() >0 )){
								createResourceMetaDataJavaObject((Element)xmlChildren.get(i),childResourceMetaData,resourceMetaDataXMLFilePath);
								logger.trace("   Setting parent to child " + childResourceMetaData.getType() + " is " + _resourceMetaData.getType());
								childResourceMetaData.setParent(_resourceMetaData);
							}
							children.add(childResourceMetaData);
							
						}
					}
					if ((node.getAttribute(TYPE) != null) || (node.isRootElement())){
						_resourceMetaData.setChildren(children);
					}
				}
				
				return children;
		}catch(DataConversionException e){
			e.printStackTrace();
			throw new DeployException(e);
		}
		
	}

	/**
	 * 
	 * @param node
	 * @param _resourceMetaData
	 * @throws DeployException
	 */
	private static void processContainmentAndLinkAttribute(Element node, ResourceMetaData _resourceMetaData)
		throws DeployException{
		
		String objectType = node.getAttribute(TYPE).getValue() ;
		Element containmentChild = node.getChild(CONTAINMENT);
		List attributeLinkChildren = node.getChildren(LINK_ATTRIBUTE);
		Element attributesChildren = node.getChild(ATTRIBUTES);
		

		if (containmentChild!=null){
    		Element attributeChild = containmentChild.getChild(ATTRIBUTE);

    		if (attributeChild != null){
    			_resourceMetaData.setContainmentAttribute(attributeChild.getAttribute("value").getValue());
    			logger.trace("In createMetaData for the Object: ContainmentPath for " + objectType + " is " + attributeChild.getAttribute("value").getValue());
    			_resourceMetaData.setContainmentPath(attributeChild.getAttribute("value").getValue());
    			Attribute returnAttributeChild = attributeChild.getAttribute("returnAttribute");
        		if (returnAttributeChild !=null){
        			logger.trace("In createMetaData for the Object: Return Attribute for " + objectType + " is " + returnAttributeChild.getValue());
    				_resourceMetaData.setReturnAttribute(returnAttributeChild.getValue());
    			}
    		}
    		
    		ArrayList<String> addtionalAttributes = new ArrayList<String>();
    		Element addtionalChild = containmentChild.getChild(ADDTIONAL);
    		if (addtionalChild  != null){
    			if (addtionalChild.getChildren("attribute")!=null){
    				Element[] addtionalChildren =  (Element[])addtionalChild.getChildren("attribute").toArray(new Element[0]);
    				for (int addtionalChildrenCnt =0 ; addtionalChildrenCnt < addtionalChildren.length ; addtionalChildrenCnt++ ){
    					if (((Element)addtionalChildren[addtionalChildrenCnt]).getAttribute("value").getValue()!=null){
    						addtionalAttributes.add(((Element)addtionalChildren[addtionalChildrenCnt]).getAttribute("value").getValue().toString());
    					}
    				}
    			}
    			_resourceMetaData.setAdditionalContainmentAttribute((String[])addtionalAttributes.toArray(new String[0]));
    		}
    	} 

		if (attributeLinkChildren!=null){
    			Iterator attributeLinkChildrenIterator = attributeLinkChildren.iterator();
    			HashMap linkAttributes = new HashMap();
    			while (attributeLinkChildrenIterator.hasNext()){
    				Element attributeLinkChild = (Element)attributeLinkChildrenIterator.next();
    				logger.trace("Object: link-attibute for " + objectType + " is " + attributeLinkChild.getAttribute(LINK_ATTRIBUTE_NAME).getValue());
    				LinkAttribute linkAttribute = processLinkAttribute(attributeLinkChild,null);
    				
    				List nestedAttributeLinkChildren = attributeLinkChild.getChildren(LINK_ATTRIBUTE);
	    			if (nestedAttributeLinkChildren !=null){
	    				Iterator it = attributeLinkChild.getChildren().iterator();
	    				while (it.hasNext()){
	    					Element nestedAttributeLinkChild = (Element)it.next();
	    						processLinkAttribute(nestedAttributeLinkChild,linkAttribute);
	    				}
	    			} 
	    			linkAttributes.put(linkAttribute.getLinkAttibuteName().toString(),linkAttribute);
    			}
    			_resourceMetaData.setLinkAttribute(linkAttributes);

		}
		
		if (attributesChildren!=null){
			logger.trace(node.getName() +  " Attributes is not null and will create rules meta data");
			// System.out.println("Setting data as " + _resourceMetaData.getType());
			_resourceMetaData.setResourceRulesMetaData( processRules(node,attributesChildren));
			
		}
		
	}
	
	/**
	 * 
	 * @param node
	 * @param rulesElement
	 * @return
	 * @throws DeployException
	 */
	private static ResourceRulesMetaData processRules(Element node, Element rulesElement)
		throws DeployException{
		ResourceRulesMetaData resourceRulesMetaData = new ResourceRulesMetaData ();
		HashMap attributeRules = new HashMap();
		
		if (node.getAttribute("editable")!=null){
			resourceRulesMetaData.setEditable(node.getAttribute("editable").getValue().toString());
		}
		List attributeChildren =  rulesElement.getChildren(ATTRIBUTE);
		
		for (int i=0; i < attributeChildren.size() ; i++){
			AttributeRule attributeRule = new AttributeRule();
			Element attributeChild =(Element)attributeChildren.get(i);
			
			if (attributeChild.getAttribute("name")!=null){
				attributeRule.setName( attributeChild.getAttribute("name").getValue().toString());
			}else{
				throw new DeployException(new Exception("Attribute Node must have a name"));
			}
		
			if (attributeChild.getAttribute("editable")!=null){
				attributeRule.setEditable( attributeChild.getAttribute("editable").getValue().toString());
			}
			
			if (attributeChild.getAttribute("pattern")!=null){
				attributeRule.setPattern( attributeChild.getAttribute("pattern").getValue().toString());
			}

			if (attributeChild.getAttribute("min")!=null){
			//	System.out.println("Adding min rule " + attributeChild.getAttribute("min").getValue().toString() );
				attributeRule.setMin( new Integer(attributeChild.getAttribute("min").getValue().toString()).intValue());
			}

			if (attributeChild.getAttribute("max")!=null){
			//	System.out.println("Adding max rule " + attributeChild.getAttribute("max").getValue().toString() );
				attributeRule.setMax( new Integer(attributeChild.getAttribute("max").getValue()).intValue());
			}

			attributeRules.put(attributeRule.getName(), attributeRule);
		}
		resourceRulesMetaData.setAttributeRules(attributeRules);
		return resourceRulesMetaData;
	}
	
	/**
	 * 
	 * @param attributeLinkChild
	 * @param parenLinkAttribute
	 * @return
	 */
	private static LinkAttribute processLinkAttribute(Element attributeLinkChild,LinkAttribute parenLinkAttribute){
		LinkAttribute linkAttribute = new LinkAttribute();
		linkAttribute.setLinkAttibuteName(attributeLinkChild.getAttribute(LINK_ATTRIBUTE_NAME).getValue().toString());
		if (attributeLinkChild.getAttribute(TARGET_ATTRIBUTE) != null){
			linkAttribute.setTargetAttribute(attributeLinkChild.getAttribute(TARGET_ATTRIBUTE).getValue().toString());
		}
		
		linkAttribute.setTargetObject(attributeLinkChild.getAttribute(TARGET_OBJECT).getValue().toString());
		linkAttribute.setTargetObjectMatchAttributeName(attributeLinkChild.getAttribute(TARGET_OBJECT_MATCH_ATTRIBUTE_NAME).getValue().toString());
		if (parenLinkAttribute==null){
			return linkAttribute;
		}else{
			parenLinkAttribute.setLinkAttribute(linkAttribute);
			return parenLinkAttribute;
		}
	}

	/**
	 * 
	 * @param node
	 * @param _resourceMetaData
	 */
	private static void  processMappingAttribute (Element node, ResourceMetaData _resourceMetaData){
		Element commandElement = node.getChild("command");
		CommandMetaData commandMetaData = new CommandMetaData();
		if (commandElement!=null){
			if (commandElement.getAttribute("createCommand")!= null)
				commandMetaData.setCreateCommand(commandElement.getAttribute("createCommand").getValue());
			if (commandElement.getAttribute("modifyCommand")!= null)
				commandMetaData.setModifyCommand(commandElement.getAttribute("modifyCommand").getValue());
			
			Element stepElement = commandElement.getChild("step");
			
			if (stepElement !=null){
				StepCommandMetaData stepCommandMetaData  = new StepCommandMetaData (); 
				stepCommandMetaData.setStepName(stepElement.getAttribute("name").getValue());
				List<Element> stepAttributeMapping = stepElement.getChildren("attribute-mapping");
				HashMap<String, CommandAttribute> stepAttributeMappings = new HashMap<String, CommandAttribute>();
				for (int i=0; i< stepAttributeMapping.size(); i++){
					Element attrElement = stepAttributeMapping.get(i);
					CommandAttribute stepCommandAttribute  = new CommandAttribute (); 
					stepCommandAttribute .setCommandAttribute( attrElement.getAttributeValue("commandAttribute"));
					stepCommandAttribute .setType( attrElement.getAttributeValue("type"));
					
					stepAttributeMappings.put( attrElement.getAttributeValue("configAttribute"),stepCommandAttribute);
				}
				stepCommandMetaData.setAttributeMappings(stepAttributeMappings);
				commandMetaData.setStepCommandMetaData(stepCommandMetaData);
			}
			
			List<Element> attributeMapping = commandElement.getChildren("attribute-mapping");
			HashMap<String, CommandAttribute> attributeMappings = new HashMap<String, CommandAttribute>();
			
			for (int i=0; i< attributeMapping.size(); i++){
				Element attrElement = attributeMapping.get(i);
				CommandAttribute commandAttribute  = new CommandAttribute (); 
				commandAttribute.setCommandAttribute( attrElement.getAttributeValue("commandAttribute"));
				commandAttribute.setType( attrElement.getAttributeValue("type"));
				commandAttribute.setConstantValue( attrElement.getAttributeValue("constantValue"));

				/**
				 * check if there is child that defines a link member
				 */
				if (attrElement.getChild("Commandlink-attribute")!=null){
					Element commandLinkElement = attrElement.getChild("Commandlink-attribute");
					CommandLinkAttribute commandLinkAttribute = new CommandLinkAttribute();


					commandLinkAttribute.setLinkAttibuteName(attrElement.getAttributeValue("configAttribute")) ;
					commandLinkAttribute.setTargetObjectRelation(commandLinkElement.getAttribute("targetObjectRelation").getValue().toString());
					commandLinkAttribute.setTargetAttribute (commandLinkElement.getAttribute("targetAttribute").getValue().toString());
					commandLinkAttribute.setTargetObjectAttribute(commandLinkElement.getAttribute("targetObjectAttribute").getValue().toString());
					commandLinkAttribute.setTargetObjectType(commandLinkElement.getAttribute("targetObjectType").getValue().toString());

					commandAttribute.setCommandLinkAttribute(commandLinkAttribute);
				}
				
				
				attributeMappings.put( attrElement.getAttributeValue("configAttribute"),commandAttribute);
				
			}
			commandMetaData.setAttributeMappings(attributeMappings);
			_resourceMetaData.setCommandMetaData(commandMetaData);
		}
	}
	
	/**
	 * 
	 * @param node
	 * @param Data
	 */
	private static void  processAttributes (Element node, ResourceMetaData _resourceMetaData)
		throws DataConversionException{
		if (node.getAttribute("attributeName")!=null){
			_resourceMetaData.setAttributeName( node.getAttribute("attributeName").getValue().toString());
		}

		if ((node.getAttribute("syncFindModeContainmentPath")!=null) && (node.getAttribute("syncFindModeContainmentPath").getValue().toString().equalsIgnoreCase("true") )){
			_resourceMetaData.setSyncFindModeContainmentPath( true);
		}

		if (node.getAttribute("matchAttribute")!=null){
			_resourceMetaData.setMatchAttribute(node.getAttribute("matchAttribute").getValue());
		}

		if (node.getAttribute("syncPreMatchAttribute")!=null){
			_resourceMetaData.setSyncPreMatchAttribute(node.getAttribute("syncPreMatchAttribute").getValue());
		}

		if (node.getAttribute("shouldCreate")!=null){
			
			_resourceMetaData.setShouldCreate( node.getAttribute("shouldCreate").getBooleanValue());
		}
		if (node.getAttribute("relation")!=null){
			_resourceMetaData.setRelation( node.getAttribute("relation").getValue().toString());
		}
		
		if (node.getAttribute("findAndResolve")!=null){
			_resourceMetaData.setFindAndResolve(true);
		}
		
		if (node.getAttribute("isProperty")!=null){
			if (node.getAttribute("isProperty").getBooleanValue())
				_resourceMetaData.setIsProperty(true);
		}
		
		if (node.getAttribute("isArray")!=null){
			if (node.getAttribute("isArray").getBooleanValue())
			_resourceMetaData.setIsArray(true);
		}
		
		if (node.getAttribute("isAttributeCount0")!=null){
			if (node.getAttribute("isAttributeCount0").getBooleanValue())
			_resourceMetaData.setAttributeCount0(true);
		}

		if (node.getAttribute("shouldIncludeAllChildren")!=null){
			if (node.getAttribute("shouldIncludeAllChildren").getBooleanValue())
			_resourceMetaData.setShouldIncludeAllChildren(true);
		}

		if (node.getAttribute(ResourceMetaDataConstants.ATTRIBUTE_NANE_IN_RESOURCEXML)!=null){
			if (node.getAttribute(ResourceMetaDataConstants.ATTRIBUTE_NANE_IN_RESOURCEXML).getBooleanValue())
			_resourceMetaData.setAttributeNameInResourceXML(true);
		}
		
		if (node.getAttribute(ResourceMetaDataConstants.APPLICATION_MANAGED)!=null){
			if (node.getAttribute(ResourceMetaDataConstants.APPLICATION_MANAGED).getBooleanValue())
			_resourceMetaData.setApplicationManaged(true);
		}

		if (node.getAttribute(ResourceMetaDataConstants.EDITABLE)!=null){
			_resourceMetaData.setEditable(node.getAttribute(ResourceMetaDataConstants.EDITABLE).getValue().toString());
		}

		if (node.getAttribute(ResourceMetaDataConstants.COMMAND_MANAGED)!=null){
			if (node.getAttribute(COMMAND_MANAGED).getBooleanValue())
				_resourceMetaData.setCommandManaged( true);
		}

		if (node.getAttribute(ResourceMetaDataConstants.CUSTOM_CODE_ATTRIBUTES)!=null){
				_resourceMetaData.setCustomCodeAttributes( node.getAttribute(ResourceMetaDataConstants.CUSTOM_CODE_ATTRIBUTES).getValue());
		}

		if (node.getAttribute(ResourceMetaDataConstants.CUSTOM_CODE_MANAGED)!=null){
		
				_resourceMetaData.setCustomCodeManaged((node.getAttribute(ResourceMetaDataConstants.CUSTOM_CODE_MANAGED).getValue().toString()));
		}
		
		
	}
	
	/**
	 * 
	 * @param resourceMetaData
	 * @throws DeployException
	 */
	public static void setParentTreePath(ResourceMetaData resourceMetaData )
		throws DeployException{
	
	
		if (resourceMetaData.getChildren().size()>0){
			ResourceMetaData[] children = (ResourceMetaData[]) resourceMetaData.getChildren().toArray(new ResourceMetaData[0]);
			
			for (int childCnt=0;childCnt < children.length;childCnt++){
				ResourceMetaData childResourceMetaData = children[childCnt];
				logger.trace("resourceMetaData: "+ childResourceMetaData.getType());
				
				Vector resourceMetaDataParentNames = new Vector();
				ResourceMetaData parentResourceMetaData = childResourceMetaData.getParent();
				while (parentResourceMetaData.getParent()!=null){
					logger.trace("		parent: "+ parentResourceMetaData.getType());
					resourceMetaDataParentNames.add(parentResourceMetaData.getType());
					parentResourceMetaData = parentResourceMetaData.getParent();
				}
				if ((children[childCnt].getChildren()!=null) && (children[childCnt].getChildren().size()>0)){
					setParentTreePath(children[childCnt]);
				}
				((ResourceMetaData)resourceMetaData.getChildren().get(childCnt)).setParentTree(resourceMetaDataParentNames);
			}
		}
		
	}	
	
	public ResourceMetaData findMetaDataByType(String type)
		throws DeployException,IOException, JDOMException{
		
		if (fullResourceMetaData==null){
			ResourceMetaDataHelper helper = new ResourceMetaDataHelper();
			InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
			parseMetaData(resourceXMLMetaDataInputStream);
		}
		return findMetaData(type,fullResourceMetaData);
	}
	
	private static ResourceMetaData findMetaData(String type,ResourceMetaData metadata){
		Vector children = metadata.getChildren();
		if (children!=null){ 
			Iterator it = children.iterator();
			while(it.hasNext()){
				ResourceMetaData childMetaData = (ResourceMetaData)it.next();
				if (childMetaData.getType().equalsIgnoreCase(type)){
					return childMetaData;
				}else{
					ResourceMetaData matchedMetaData =findMetaData(type, childMetaData);
					if (matchedMetaData !=null){
						return matchedMetaData ;
					}
				}
			}
		}	
		return null;
	}
}
