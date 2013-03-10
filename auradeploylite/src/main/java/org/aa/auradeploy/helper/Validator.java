package org.aa.auradeploy.helper;

import java.io.File;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.log.Error;



/**
 * @author Jatin
 *
 * Copyright (C) 

 */
public class Validator {
	
	
	public boolean validate(){
		boolean isValid = true;
		if (!validateforEARProcessor()){
			isValid = false;
		}
		if (!Helper.isValid(System.getProperty(DeployValues.CELL_NAME))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.CELL_NAME + " in the bat/sh file that invokes SlickDeploy.");
		} 
		if (!Helper.isValid(System.getProperty(DeployValues.CLUSTER_NAME))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.CLUSTER_NAME + " in the bat/sh file that invokes SlickDeploy.");
		}
		if (!Helper.isValid(System.getProperty(DeployValues.USER))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.USER + " in the bat/sh file that invokes SlickDeploy.");
		}
		if (!Helper.isValid(System.getProperty(DeployValues.PASSWORD))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.PASSWORD + " in the bat/sh file that invokes SlickDeploy.");
		}
		if (!Helper.isValid(System.getProperty(DeployValues.PORT))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.PORT + " in the bat/sh file that invokes SlickDeploy.");
		}
		if (!Helper.isValid(System.getProperty(DeployValues.HOST ))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.HOST + " in the bat/sh file that invokes SlickDeploy.");
		}
		if (!Helper.isValid(System.getProperty(DeployValues.NODE))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.NODE + " in the bat/sh file that invokes SlickDeploy.");
		} 
		if (!Helper.isValid(System.getProperty(DeployValues.OPERATION ))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.OPERATION + " in the bat/sh file that invokes SlickDeploy.");
		}
		if (!isConnectionValid(System.getProperty(DeployValues.CONNECTIONTYTPE))){
			isValid = false;
		}
		if (!Helper.isValid(System.getProperty(DeployValues.FAILIFRESOURCEABSENT))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.FAILIFRESOURCEABSENT + " in the bat/sh file that invokes SlickDeploy.");
		}
		return isValid ;
	
	}


	public boolean validateforEARProcessor(){
		
	boolean isValid = true;
	if (Helper.getBooleanFromString( (System.getProperty(DeployValues.isMultiEAR)))){ 
		if (!Helper.isValid(System.getProperty(DeployValues.MultiEARDeployData))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.MultiEARDeployData + " in the bat/sh file that invokes SlickDeploy.");
		}else if (!fileExits(System.getProperty(DeployValues.MultiEARDeployData))){
			isValid = false;
			Error.log("Deploydata Directory does not exits at this path " + System.getProperty(DeployValues.EAR_LOCATION));
		}

			
		if (!Helper.isValid(System.getProperty(DeployValues.MultiEARLocation))){
			isValid = false;
			Error.log("Please add -D" + DeployValues.MultiEARLocation + " in the bat/sh file that invokes SlickDeploy.");
		}else if (!fileExits(System.getProperty(DeployValues.MultiEARLocation))){
			isValid = false;
			Error.log("EAR file does not exits at this path " + System.getProperty(DeployValues.EAR_LOCATION));
		}
	}
	
	if (!Helper.isValid(System.getProperty(DeployValues.APPLICATION_NAME))){
		isValid = false;
		Error.log("Please add -D" + DeployValues.APPLICATION_NAME + " in the bat/sh file that invokes SlickDeploy.");
	}
	if (!Helper.isValid(System.getProperty(DeployValues.EAR_LOCATION))){
		isValid = false;
		Error.log("Please add -D" + DeployValues.EAR_LOCATION + " in the bat/sh file that invokes SlickDeploy.");
	}
	if (!Helper.isValid(System.getProperty(DeployValues.DEPLOYDATA_LOCATION))){
		isValid = false;
		Error.log("Please add -D" + DeployValues.DEPLOYDATA_LOCATION + " in the bat/sh file that invokes SlickDeploy.");
	}
	if ((!Helper.getBooleanFromString( (System.getProperty(DeployValues.isMultiEAR)))) && (!fileExits(System.getProperty(DeployValues.DEPLOYDATA_LOCATION )))){
		isValid = false;
		Error.log("Deploy data file does not exits at this path " + System.getProperty(DeployValues.DEPLOYDATA_LOCATION ));
	}

	if ((!Helper.getBooleanFromString( (System.getProperty(DeployValues.isMultiEAR)))) && (!fileExits(System.getProperty(DeployValues.EAR_LOCATION)))){
		isValid = false;
		Error.log("EAR file does not exits at this path " + System.getProperty(DeployValues.EAR_LOCATION));
	}
	return isValid ;

}

	private boolean fileExits(String value){
	
		File file = new File(value);
		return file.exists(); 
	}

	private boolean isConnectionValid(String value){
		boolean isValid = true;
		if ((value ==null) || (value.trim().length()==0)) {
			isValid = false;
			Error.log("Please add -D" + DeployValues.CONNECTIONTYTPE + " in the bat/sh file that invokes SlickDeploy.");
		}else if(!((value.equalsIgnoreCase("RMI")) || (value.equalsIgnoreCase("SOAP")))){
			isValid = false;
			Error.log("Value of -D" + DeployValues.CONNECTIONTYTPE + " in the bat/sh file that invokes SlickDeploy must be RMI or SOAP and not " + value);
		
		}
		return isValid ;

	}

}
