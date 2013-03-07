/**	   Copyright [2005] [www.apartech.com]


**/
package org.aa.auradeploy.ant;

import org.aa.auradeploy.application.Application;
import org.aa.auradeploy.deploy.DeployInfo;
import org.apache.tools.ant.BuildException;

import org.aa.common.Constants.LicenseConstants;
import org.aa.common.exception.DeployException;

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
