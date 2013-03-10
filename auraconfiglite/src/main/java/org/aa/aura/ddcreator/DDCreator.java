package org.aa.aura.ddcreator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.aa.auraconfig.resources.ApplicationReadManager;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceDiffReportHelper;
import org.aa.auraconfig.resources.ResourceFinder;
import org.aa.auraconfig.resources.parser.ResourceXMLParser;
import org.aa.auraconfig.resources.parser.ResourceXMLWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.application.client.AppDeploymentException;

public class DDCreator {
	private static final Log logger  = LogFactory.getLog(DDCreator.class);	

	public Resource createDD(DeployInfo deployInfo1, String earName)
		throws DeployException,JDOMException,IOException,Exception{
		
		//deployInfo1.setResourceXML("C://jatin//eclipse//Aura-Config-Test-V61//resources//resourceEARApplication.xml");
		InputStream resourceApplicationXMLInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("application/resourceEARApplication.xml");
//		deployInfo1.setResourceXML(applicationXML);
		
		InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
		InputStream referencedResourceXML =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
		
		if (resourceXMLMetaDataInputStream==null){
			SDLog.log("Error: AuraConfig Data missing");
		}
	
		if (referencedResourceXML == null){
			SDLog.log("Error: AuraConfig Reference file missing");
		}
		
		ResourceXMLParser resourceXMLParser = new ResourceXMLParser(); 
	
		Element rootNode = resourceXMLParser.getResourcesXMLElements(resourceApplicationXMLInputStream ,false);
	
		Resource resources = resourceXMLParser.getResourcesFromXML(rootNode,null,resourceXMLMetaDataInputStream,false,deployInfo1,null);
		Resource referencedResources = resourceXMLParser.getReferenceResources(referencedResourceXML,resourceXMLMetaDataInputStream,false,deployInfo1);
		
		Resource earResource = resources.getChildren().get(0).getChildren().get(0);
		System.out.println("Got the application resource " + earResource.getName()); 
		
		ApplicationReadManager applicationReadManager = new ApplicationReadManager(earResource, deployInfo1);
		logger.trace("Call applicationReadManager.processApplicationEAR(earName) " + earName);
		applicationReadManager.processApplicationEAR(earName);
		
		logger.trace("Create XML  at " + deployInfo1.getSyncResourceXML());

		ResourceXMLWriter resourceXMLWriter  = new ResourceXMLWriter();
		resourceXMLWriter.createResourceXMLFile(resources,deployInfo1);
	
		return resources;
	}


	public static void main(String[] args) {
		try {
			DeployInfo deployInfo = new DeployInfo();
			deployInfo.setEnvironmentProperties("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\properties\\AvatarWAS61.properties");
			deployInfo.setSyncResourceXML("C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployAppDeployData.xml");
			deployInfo.setSyncReportLocation("C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployAppDeployData.xml.html");

			DDCreator ddCreator  = new DDCreator ();
			ddCreator.createDD(deployInfo , "C:\\jatin\\eclipse\\AuraDeployLiteTest\\workfiles\\ear\\easydeploy\\easyDeployApp.ear");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
