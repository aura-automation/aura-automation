/**	   Copyright 


**/
package org.aa.auraconfig.resources.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;

import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceConstants;
import org.aa.auraconfig.resources.ResourceHelper;
import org.aa.auraconfig.resources.metadata.ResourceMetaData;
import org.aa.auraconfig.resources.metadata.ResourceMetaDataHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class ResourceXMLParser {
	private static final Log logger  = LogFactory.getLog(ResourceXMLParser.class);	
	private Element originalResourcesElement = null; 
	
	
	/**
	 * Get JDom for the given resources.xml
	 * for each element call createResourceJavaObject
	 * @return
	 * @throws DeployException
	 */
	public Resource getResourcesFromXML (Element originalResourcesElement,String rulesXML,InputStream resourceXMLMetaDataFileInputStream,boolean validate,DeployInfo deployInfo,ConfigService configService)
		throws DeployException,IOException,JDOMException{

		//getResourcesXMLElements(resourceXMLFilePath,validate);
		return createResourceTree(originalResourcesElement, rulesXML, null,resourceXMLMetaDataFileInputStream,deployInfo,configService);

	}
	
	/**
	 * 
	 * @param resourceXMLFilePath
	 * @param validate
	 * @return
	 * @throws DeployException
	 */
	public Element getResourcesXMLElements(String resourceXMLFilePath,
			boolean validate) 
		throws DeployException{
		
		if (originalResourcesElement ==null){
			try{
				
				// Get JDom for resources.xml
		    	SAXBuilder builder = new SAXBuilder();
		    	if (validate){
		    		builder.setValidation(true);
		    		// this is not supported in the JDOM supplied with WAS 
		    		//builder.setFeature("http://apache.org/xml/features/validation/schema", true);
		    	}
		    	
//		    	Document doc = builder.build(new CharArrayReader("asdasd".toCharArray()));

				Document doc = builder.build(new File(resourceXMLFilePath));
		    	originalResourcesElement = doc.getRootElement(); 
				return originalResourcesElement;
		    	
		    	
				
				
			}catch(JDOMException e){
				e.printStackTrace();
				throw new DeployException(e);
			}
		}else{
			return originalResourcesElement;
		}
	}
	
	
	/**
	 * 
	 * @param resourceXMLFilePath
	 * @param validate
	 * @return
	 * @throws DeployException
	 */
	public Element getResourcesXMLElements(InputStream resourceXMLFile,
			boolean validate) 
		throws DeployException{
		
		if (originalResourcesElement ==null){
			try{
				
				// Get JDom for resources.xml
		    	SAXBuilder builder = new SAXBuilder();
		    	if (validate){
		    		builder.setValidation(true);
		    		// this is not supported in the JDOM supplied with WAS 
		    		//builder.setFeature("http://apache.org/xml/features/validation/schema", true);
		    	}
		    	
//		    	Document doc = builder.build(new CharArrayReader("asdasd".toCharArray()));

				Document doc = builder.build(resourceXMLFile);
		    	originalResourcesElement = doc.getRootElement(); 
				return originalResourcesElement;
		    	
		    	
				
				
			}catch(JDOMException e){
				e.printStackTrace();
				throw new DeployException(e);
			}
		}else{
			return originalResourcesElement;
		}
	}

	
	/**
	 * 
	 * @param referenceResourceXMLFileInputStream
	 * @param resourceXMLMetaDataFilePathInputStream
	 * @param validate
	 * @param deployInfo
	 * @return
	 * @throws DeployException
	 */
	public  Resource getReferenceResources(InputStream referenceResourceXMLFileInputStream,InputStream resourceXMLMetaDataFilePathInputStream,boolean validate,DeployInfo deployInfo) 
		throws DeployException{
	
		try{
			// Get JDom for resources.xml
	    	SAXBuilder builder = new SAXBuilder();
	    	if (validate){
	    		builder.setValidation(true);
	    		// not supported in WAS JDOM version
	    		//builder.setFeature("http://apache.org/xml/features/validation/schema", true);
	    	}
	    	
	    	Document doc = builder.build(referenceResourceXMLFileInputStream);
	    	return createResourceTree(doc.getRootElement(),null,referenceResourceXMLFileInputStream,resourceXMLMetaDataFilePathInputStream,deployInfo,null); 
			
		}catch(JDOMException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(IOException e){
			e.printStackTrace();
			throw new DeployException(e);
		}
	}

	
	
	
	private static Resource createResourceTree(Element rootResourcesElement, String rulesXML,InputStream resourceXMLFilePathInputStream,
				InputStream resourceXMLMetaDataFilePathInputStream,DeployInfo deployInfo,ConfigService configService)
		throws DeployException,IOException,JDOMException{
		try{
			Resource rootResourceJavaObject = new Resource();
			ResourceParserHelper resourceParserHelper = new ResourceParserHelper();
			ResourceMetaDataHelper resourceMetaDataHelper = new ResourceMetaDataHelper();
			ResourceMetaDataHelper resourceRulesHelper = new ResourceMetaDataHelper();

			ResourceMetaData resourceMetaData = resourceMetaDataHelper.parseMetaData(resourceXMLMetaDataFilePathInputStream);
			ResourceMetaData rulesMetaData = null;
			
			// get Document and then get root node of resources
	
			// get all the child nodes from resources
			// loop through first child and create a new Resources Object.
			//SDLog.log(rootResourcesNode.getNodeName());
			// if there are child nodes from root node then start processing
			if (rootResourcesElement.getChildren().size()>0){
				rootResourceJavaObject.setName(ResourceConstants.RESOURCES);
				if (deployInfo.getVersionInfo().getMajorNumber() == 2){
					HashMap<String, String> rootResourceAttrs  = new HashMap<String, String>();
					rootResourceAttrs.put("version", "2.0");
					rootResourceJavaObject.setAttributeList(rootResourceAttrs);
				}
				/**
				 * If InputStream is null then the parsing is for resource xml
				 * If InputStream is not null then the parsing is for reference resource xml
				 */
				
				resourceParserHelper.createResourceJavaObject(rootResourcesElement, rootResourceJavaObject,resourceXMLFilePathInputStream,deployInfo );
			}
			resourceParserHelper.setParentTreePath(rootResourceJavaObject);
			// Need to set this only once, I think to improve the performance.
			// ResourceMetaDataHelper.setParentTreePath(resourceMetaData);
			resourceParserHelper.setResourceMetaData(rootResourceJavaObject,resourceMetaData,configService,null);
			resourceParserHelper.setContainmentPath(rootResourceJavaObject);

			if (rulesXML !=null) {
				InputStream resourceRulesInputStream =   new FileInputStream( rulesXML );
				if (resourceRulesInputStream!=null){
					rulesMetaData = resourceRulesHelper.parseRulesMetaData(resourceRulesInputStream);
					resourceParserHelper.setRulesMetaData(rootResourceJavaObject,rulesMetaData);
	
				}
			}
			
			return rootResourceJavaObject;
		}catch(ConnectorException e){
			throw new DeployException(e);
		}catch (ConfigServiceException e){
			throw new DeployException(e);
		}
		//printResources(rootResourceJavaObject);
	}


	/**
	 * @return the originalResourcesElement
	 */
	public Element getOriginalResourcesElement() {
		return originalResourcesElement;
	}


	
}
