/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

import com.ibm.websphere.management.application.client.AppDeploymentTask;

public class ApplicationManagerHelper {
	
	public int getDataColumnNumber(AppDeploymentTask  deplTask ,String matchColumnName){
		String[] colName = deplTask.getColumnNames();
		String[][] data = deplTask.getTaskData();
		int matchColumnNo = -1;
		int dataRows = data.length;
		int columnLength = colName.length;

	    for (int dataRowCnt=0 ; dataRowCnt < dataRows; dataRowCnt++){
	    	for (int columnCnt=0 ; columnCnt< columnLength; columnCnt++){
	    		if (dataRowCnt==0){
	    			if (data[dataRowCnt][columnCnt].equalsIgnoreCase(matchColumnName)){
	    				matchColumnNo = columnCnt;
	    			}  
	    		}
	    	}
	    }
		return matchColumnNo;
	}

}
