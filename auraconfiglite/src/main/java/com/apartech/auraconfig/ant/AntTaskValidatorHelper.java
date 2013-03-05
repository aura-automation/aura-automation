/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.auraconfig.ant;

import com.apartech.common.Constants.DeployValues;

public class AntTaskValidatorHelper {

	
	public static boolean isValid (String value){
	
		if ((value == null) || (value.trim().length() ==0)){
			return false;
		}else{
			return true;
		}
	}

	public static boolean isValidClusterOperation(String value){
		if ((value == DeployValues.CLUSTER_OPERATION_START) || (value == DeployValues.CLUSTER_OPERATION_STOP) ||  (value == DeployValues.CLUSTER_OPERATION_RESTART ) ){
			return false;
		}else{
			return true;
		}
		
	}

}
