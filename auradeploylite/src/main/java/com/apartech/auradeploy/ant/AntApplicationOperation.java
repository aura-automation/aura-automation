/**	   Copyright [2005] [www.apartech.com]


**/
package com.apartech.auradeploy.ant;

import org.apache.tools.ant.BuildException;

import com.apartech.auradeploy.application.Application;
import com.apartech.auradeploy.deploy.DeployInfo;
import com.apartech.common.Constants.LicenseConstants;
import com.apartech.common.exception.DeployException;

public class AntApplicationOperation extends AntApplicationTask{
	
	public void execute() throws BuildException {
		try{	

			DeployInfo  deployInfo = getPopluatedDeployInfo();
		// get connection to the server and get application object
			Application application = new Application(deployInfo);
			application.doWork();
		}catch(Exception e){
			throw new BuildException(e);
		}
	
	}
}
