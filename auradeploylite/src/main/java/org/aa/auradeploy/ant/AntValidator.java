/**
 * @author Jatin
 *
 * Copyright (C) 

 */

package org.aa.auradeploy.ant;

import java.io.File;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.helper.Helper;
import org.aa.auradeploy.log.Error;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.log.SDLog;



public class AntValidator {
	
	
	public boolean validateForEARProcessor(DeployInfo deployInfo){
			
		boolean isValid = true;
		
		if ( deployInfo.isMultiEAR()){ 
			if (!Helper.isValid( deployInfo.getMultiEARDeployData())){
				isValid = false;
				Error.log("Please add " + DeployValues.MultiEARDeployData + " attribute to the ant task.");
			}else if (!fileExits(deployInfo.getMultiEARDeployData())){
				isValid = false;
				Error.log("Deploydata Directory for Multi EAR does not exits at this path " + deployInfo.getMultiEARDeployData());
			}

				
			if (!Helper.isValid(deployInfo.getMultiEARLocation())){
				isValid = false;
				Error.log("Please add " + DeployValues.MultiEARLocation + "  attribute to the ant task");
			}else if (!fileExits(deployInfo.getMultiEARLocation())){
				isValid = false;
				Error.log("EAR for Multi EAR does not exits at this path " + System.getProperty(DeployValues.EAR_LOCATION));
			}
		}

		if (! deployInfo.isMultiEAR()){ 
		
			if (!Helper.isValid(deployInfo.getApplicationName())){
				isValid = false;
				Error.log("Please add " + DeployValues.APPLICATION_NAME + "  attribute to the ant task");
			}
			if (!Helper.isValid(deployInfo.getEARFileLocation())){
				isValid = false;
				Error.log("Please add " + DeployValues.EAR_LOCATION + "  attribute to the ant task");
			}
	/**		if (!Helper.isValid(deployInfo.getDeployDataLocation())){
				isValid = false;
				Error.log("Please add " + DeployValues.DEPLOYDATA_LOCATION + "  attribute to the ant task");
			} **/
			if ((!fileExits(deployInfo.getDeployDataLocation()))){
				isValid = false;
				Error.log("Deploy data file does not exits at this path " + deployInfo.getDeployDataLocation());
			}
			if ((!fileExits(deployInfo.getEARFileLocation()))){
				isValid = false;
				Error.log("EAR file does not exits at this path " + deployInfo.getEARFileLocation());
			}
		}
		
/**		if (!Helper.isValid(deployInfo.getFailIfResourceAbsent())){
			isValid = false;
			Error.log("Please add " + DeployValues.FAILIFRESOURCEABSENT + "  attribute to the ant task");
		} **/
		return isValid ;
	
	}

	public boolean validate(DeployInfo deployInfo){
		
		boolean isValid = true;
	
		if (!Helper.isValid(deployInfo.getApplicationName())){
			isValid = false;
			Error.log("Please add " + DeployValues.APPLICATION_NAME + "  attribute to the ant task");
		}
	
/**		if (!Helper.isValid(deployInfo.getCell())){
			isValid = false;
			Error.log("Please add " + DeployValues.CELL_NAME + "  attribute to the ant task");
		} 
	**/	
		if ( (!Helper.isValid(deployInfo.getCluster())) && (!Helper.isValid(deployInfo.getServer()))){
			isValid = false;
			Error.log("Please add " + DeployValues.CLUSTER_NAME + " or " + DeployValues.SERVER + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getUserName())){
			isValid = false;
			Error.log("Please add " + DeployValues.USER + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getPassword())){
			isValid = false;
			Error.log("Please add " + DeployValues.PASSWORD + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getPort())){
			isValid = false;
			Error.log("Please add " + DeployValues.PORT + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getHost())){
			isValid = false;
			Error.log("Please add " + DeployValues.HOST + "  attribute to the ant task");
		}
/**
		if (!Helper.isValid(deployInfo.getNode())){
			isValid = false;
			Error.log("Please add " + DeployValues.NODE + "  attribute to the ant task");
		}
		**/ 
		if (!isConnectionValid(deployInfo.getConnectionType())){
			isValid = false;
		}
	
		if ( (deployInfo.isMultiEAR() ) && (!Helper.isValid(deployInfo.getMultiEARLocation()))){
			isValid = false;
			Error.log("Please add " + DeployValues.MultiEARLocation  + " attribute to the ANT task");
		}
	
		if ( (deployInfo.isMultiEAR()) && (!Helper.isValid(deployInfo.getMultiEARDeployData()))){
			isValid = false;
			Error.log("Please add " + DeployValues.MultiEARDeployData  + " attribute to the ANT task");
		}
		
		if (deployInfo.getTargetOS()!=null){
			if (!(deployInfo.getTargetOS().equalsIgnoreCase("Windows") || deployInfo.getTargetOS().equalsIgnoreCase("Unix") || deployInfo.getTargetOS().equalsIgnoreCase("Linux"))){
				isValid = false;
				Error.log("TargetOS must be either Windows, Unix or Linux.");
				
			}
		}
		
		// for application management if this is empty then default to reinstall
		if (!Helper.isValid(deployInfo.getOperation())){
			SDLog.log(" Will default the operation to reinstall.");
			deployInfo.setOperation(DeployValues.APPLICATION_OPERATION_REINSTALL);	
		 
		}
	
		if ((deployInfo.getStartingWeight() == null) ){
			isValid = false;
			Error.log("Please add " + DeployValues.STARTINGWEIGHT + "  attribute to the ant task, Current value is " + deployInfo.getStartingWeight());
		}
		
	/**		if (!Helper.isValid(deployInfo.getFailIfResourceAbsent())){
			isValid = false;
			Error.log("Please add " + DeployValues.FAILIFRESOURCEABSENT + "  attribute to the ant task");
		} **/
		return isValid ;

	}

	public boolean validateForCluster(DeployInfo deployInfo){
		
		boolean isValid = true;

		if (!Helper.isValid(deployInfo.getCell())){
			isValid = false;
			Error.log("Please add " + DeployValues.CELL_NAME + "  attribute to the ant task");
		} 
		
		if ( (!Helper.isValid(deployInfo.getCluster())) && (!Helper.isValid(deployInfo.getServer()))){
			isValid = false;
			Error.log("Please add " + DeployValues.CLUSTER_NAME + " or " + DeployValues.SERVER + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getUserName())){
			isValid = false;
			Error.log("Please add " + DeployValues.USER + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getPassword())){
			isValid = false;
			Error.log("Please add " + DeployValues.PASSWORD + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getPort())){
			isValid = false;
			Error.log("Please add " + DeployValues.PORT + "  attribute to the ant task");
		}
		if (!Helper.isValid(deployInfo.getHost())){
			isValid = false;
			Error.log("Please add " + DeployValues.HOST + "  attribute to the ant task");
		}
		if (!isConnectionValid(deployInfo.getConnectionType())){
			isValid = false;
		}

		if (!Helper.isValid(deployInfo.getOperation())){
			isValid = false;
			Error.log("Please add " + DeployValues.OPERATION+ "  attribute to the ant task");
		}
	
		if (!Helper.isValidClusterOperation(deployInfo.getOperation())){
			isValid = false;
			Error.log("Operation should be either of " + DeployValues.CLUSTER_OPERATION_RESTART + " or " + DeployValues.CLUSTER_OPERATION_START + " or " + DeployValues.CLUSTER_OPERATION_STOP );
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
			Error.log("Please add " + DeployValues.CONNECTIONTYTPE + "  attribute to the ant task");
		}else if(!((value.equalsIgnoreCase("RMI")) || (value.equalsIgnoreCase("SOAP")))){
			isValid = false;
			Error.log("Value of " + DeployValues.CONNECTIONTYTPE + " in the attribute of ant task must be RMI or SOAP and not " + value);
		
		}
		return isValid ;

	}

}
