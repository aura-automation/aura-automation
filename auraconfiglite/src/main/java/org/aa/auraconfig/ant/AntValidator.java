/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */

package org.aa.auraconfig.ant;

import java.io.File;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.log.Error;


public class AntValidator {
	
	

	public boolean validate(DeployInfo deployInfo){
		
		boolean isValid = true;
	
		if (!AntTaskValidatorHelper.isValid(deployInfo.getApplicationName())){
			isValid = false;
			Error.log("Please add " + DeployValues.APPLICATION_NAME + "  attribute to the ant task");
		}
	
/**		if (!AntTaskValidatorHelper.isValid(deployInfo.getNode())){
			isValid = false;
			Error.log("Please add " + DeployValues.NODE + "  attribute to the ant task");
		} 
**/
		if (!AntTaskValidatorHelper.isValid(deployInfo.getEnvironmentProperties())){
			isValid = false;
			Error.log("Please add " + DeployValues.ENVIRONMENT_PROPERTIES + "  attribute to the ant task");
		} 

		if ((!fileExits(deployInfo.getEnvironmentProperties()))){
			isValid = false;
			Error.log("Environment Properties does not exits at this path " + deployInfo.getEnvironmentProperties());
		}

		if ((!fileExits(deployInfo.getResourceXML()))){
			isValid = false;
			Error.log("Resource XML does not exits at this path " + deployInfo.getResourceXML());
		}

		if (deployInfo.getRulesXML()!=null){	
			if ((!fileExits(deployInfo.getRulesXML()))){
				isValid = false;
				Error.log("Resource XML does not exits at this path " + deployInfo.getRulesXML());
			}
		}

		if (!AntTaskValidatorHelper.isValid(deployInfo.getSyncReportLocation())){
			isValid = false;
			Error.log("Please add " + DeployValues.SYNC_REPORT_LOCATION + "  attribute to the ant task");
		}
		
		if (!isConnectionModeValid(deployInfo.getConnectionMode())){
			isValid = false;
		}else{
			if (deployInfo.getConnectionMode().equalsIgnoreCase(DeployValues.CONNECTION_MODE_REMOTE)){
				if (!AntTaskValidatorHelper.isValid(deployInfo.getUserName())){
					isValid = false;
					Error.log("Please add " + DeployValues.USER + "  attribute to the ant task");
				}
				if (!AntTaskValidatorHelper.isValid(deployInfo.getPassword())){
					isValid = false;
					Error.log("Please add " + DeployValues.PASSWORD + "  attribute to the ant task");
				}
				if (!AntTaskValidatorHelper.isValid(deployInfo.getPort())){
					isValid = false;
					Error.log("Please add " + DeployValues.PORT + "  attribute to the ant task");
				}
				if (!AntTaskValidatorHelper.isValid(deployInfo.getHost())){
					isValid = false;
					Error.log("Please add " + DeployValues.HOST + "  attribute to the ant task");
				}
			}else{
				if (!AntTaskValidatorHelper.isValid(deployInfo.getWasRespositoryRoot())){
					isValid = false;
					Error.log("Please add " + DeployValues.WAS_REPOSITORY_ROOT + "  attribute to the ant task");
				}
				
			}
		}
		
		

		
		if (!isOperationModeValid (deployInfo.getOperationMode())){
			isValid = false;
		}

		if (!isConnectionTypeValid(deployInfo.getConnectionType())){
			isValid = false;
		}

		if (deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC)){

			if  (!AntTaskValidatorHelper.isValid(deployInfo.getSyncResourceXML())) {
				isValid = false;
				Error.log("Sync Resource XML file does not exits at this path " + deployInfo.getSyncResourceXML());
			}
		}

		// for application management if this is empty then default to reinstall
		if (!AntTaskValidatorHelper.isValid(deployInfo.getOperationMode())){
			deployInfo.setOperation(DeployValues.OPERATION_MODE_REPORTONLY);	
		 
		}
	
		return isValid ;

	}

	private boolean fileExits(String value){
	
		File file = new File(value);
		return file.exists(); 
	}

	private boolean isConnectionTypeValid(String value){
		boolean isValid = true;
		if ((value ==null) || (value.trim().length()==0)) {
			isValid = false;
			Error.log("Please add " + DeployValues.CONNECTIONTYTPE + "  attribute to the ant task");
		}else if(!((value.equalsIgnoreCase("RMI")) || (value.equalsIgnoreCase("SOAP")))){
			isValid = false;
			Error.log("Value of " + DeployValues.CONNECTIONTYTPE + " in the attribute of ant task must be RMI or SOAP and not " + value);
		
		}
		return isValid ;
	}

	private boolean isConnectionModeValid(String value){
		boolean isValid = true;
		if ((value ==null) || (value.trim().length()==0)) {
			isValid = false;
			Error.log("Please add " + DeployValues.CONNECTION_MODE + "  attribute to the ant task");
		}else if(!((value.equalsIgnoreCase(DeployValues.CONNECTION_MODE_LOCAL )) || (value.equalsIgnoreCase(DeployValues.CONNECTION_MODE_REMOTE )))){
			isValid = false;
			Error.log("Value of " + DeployValues.CONNECTION_MODE+ " in the attribute of ant task must be " + DeployValues.CONNECTION_MODE_REMOTE + " or " + DeployValues.CONNECTION_MODE_LOCAL + " and not " + value);
		}
		return isValid ;
	}


	private boolean isOperationModeValid(String value){
		boolean isValid = true;
		if ((value ==null) || (value.trim().length()==0)) {
			isValid = false;
			Error.log("Please add " + DeployValues.OPERATION_MODE + "  attribute to the ant task");
		}else if(!((value.equalsIgnoreCase(DeployValues.OPERATION_MODE_NORMAL)) || (value.equalsIgnoreCase( DeployValues.OPERATION_MODE_REPORTONLY)|| (value.equalsIgnoreCase( DeployValues.OPERATION_MODE_SYNC))))){
			isValid = false;
			Error.log("Value of " + DeployValues.OPERATION_MODE + " in the attribute of ant task must be " + DeployValues.OPERATION_MODE_NORMAL + " or " + DeployValues.OPERATION_MODE_REPORTONLY + " or " + DeployValues.OPERATION_MODE_SYNC + " not " + value);
		
		}
		return isValid ;
	}

}
