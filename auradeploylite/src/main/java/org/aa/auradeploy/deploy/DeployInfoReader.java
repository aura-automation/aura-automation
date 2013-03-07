/*
 * Created on 25-Aug-2005
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
package org.aa.auradeploy.deploy;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.helper.Helper;
import org.aa.auradeploy.helper.Validator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.log.SDLog;

/**
 * @author Jatin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeployInfoReader {
	
	private static final Log logger  = LogFactory.getLog(DeployInfoReader.class); 
    	
	public DeployInfo populateDeployInfoForEARProcesser(){
		DeployInfo deployInfo = new DeployInfo();
		logger.trace("Create the instance of Validator.");
		SDLog.log("    [Deploy]	Validating data");
		Validator validator = new Validator();
		logger.trace("Created the instance of Validator sucessfully.");

		if (!validator.validateforEARProcessor()){
			SDLog.log("    [Deploy]	Data validation failed and hence deploy will stop");
			logger.error("Validation failed.");
			logger.error("Validation failed due to above reason.");
			System.exit(-1);
		}

		logger.trace("Populate the deployInfo with Application name.");
		deployInfo.setApplicationName(System.getProperty(DeployValues.APPLICATION_NAME)); 

		logger.trace("Populate the deployInfo with EAR Location.");
		deployInfo.setEARFileLocation(System.getProperty(DeployValues.EAR_LOCATION)); 

		logger.trace("Populate the deployInfo with Depoy Location.");
		deployInfo.setDeployDataLocation(System.getProperty(DeployValues.DEPLOYDATA_LOCATION));

		logger.trace("Populate the deployInfo with isMultiEAR.");
		deployInfo.setisMultiEAR(Helper.getBooleanFromString(System.getProperty(DeployValues.isMultiEAR)));

		logger.trace("Populate the deployInfo with MultiEAR deploydata.");
		deployInfo.setMultiEARDeployData(System.getProperty(DeployValues.MultiEARDeployData));

		logger.trace("Populate the deployInfo with MultiEar location.");
		deployInfo.setMultiEARLocation(System.getProperty(DeployValues.MultiEARLocation));

		return deployInfo; 
	}

	
	
	public DeployInfo populateDeployInfo(){
		DeployInfo deployInfo = populateDeployInfoForEARProcesser();
		logger.trace("Create the instance of Validator.");
		SDLog.log("    [Deploy]	Validating data");
		Validator validator = new Validator();
		logger.trace("Created the instance of Validator sucessfully.");

		if (!validator.validate()){
			SDLog.log("    [Deploy]	Data validation failed and hence deploy will stop");
			logger.error("Validation failed.");
			logger.error("Validation failed due to above reason.");
			System.exit(-1);
		}

		logger.trace("Populate the deployInfo with Cell Name .");
		deployInfo.setCell(System.getProperty(DeployValues.CELL_NAME)); 

		logger.trace("Populate the deployInfo with Cluster Name.");
		deployInfo.setCluster(System.getProperty(DeployValues.CLUSTER_NAME)); 

		logger.trace("Populate the deployInfo with User.");
		deployInfo.setUserName(System.getProperty(DeployValues.USER));

		logger.trace("Populate the deployInfo with Password.");
		deployInfo.setPassword(System.getProperty(DeployValues.PASSWORD));

		logger.trace("Populate the deployInfo with Port.");
		deployInfo.setPort(System.getProperty(DeployValues.PORT));

		logger.trace("Populate the deployInfo with Node.");
		deployInfo.setNode(System.getProperty(DeployValues.NODE));

		logger.trace("Populate the deployInfo with Host.");
		deployInfo.setHost(System.getProperty(DeployValues.HOST));

		logger.trace("Populate the deployInfo with Operation.");
		deployInfo.setOperation(System.getProperty(DeployValues.OPERATION));

		logger.trace("Populate the deployInfo with COnnection Type to " + System.getProperty(DeployValues.CONNECTIONTYTPE));
		deployInfo.setConnectionType(System.getProperty(DeployValues.CONNECTIONTYTPE));


		logger.trace("Populate the deployInfo with Webserver " + System.getProperty(DeployValues.WEBSERVER));
		deployInfo.setWebserver(System.getProperty(DeployValues.WEBSERVER));
		
		logger.trace("Populate the deployInfo with JNDIPort.");
		deployInfo.setJNDIPort(System.getProperty(DeployValues.JNDIPORT));

		logger.trace("Populate the deployInfo with JNDIUrl.");
		deployInfo.setJNDIUrl(System.getProperty(DeployValues.JNDIURL));

		logger.trace("Populate the deployInfo with isFailOnResource.");
		deployInfo.setFailIfResourceAbsent(Helper.getBooleanFromString(System.getProperty(DeployValues.FAILIFRESOURCEABSENT)));

		
		return deployInfo; 
	}
	
}
