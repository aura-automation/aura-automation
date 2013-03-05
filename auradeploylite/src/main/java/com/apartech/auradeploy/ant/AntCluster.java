/**	   Copyright [2009] [www.apartech.co.uk]


**/
package com.apartech.auradeploy.ant;

import org.apache.tools.ant.BuildException;


import com.apartech.auradeploy.cluster.Cluster;
import com.apartech.auradeploy.deploy.DeployInfo;
import com.apartech.common.Constants.LicenseConstants;
import com.apartech.common.exception.DeployException;

public class AntCluster extends AntTask{
	
	public void execute()
		throws BuildException{
		
		try{	
				DeployInfo deployInfo = getPopluatedDeployInfo();
	
				Cluster cluster = new Cluster(deployInfo);
				cluster.doWork();
		}catch(Exception e){
			throw new BuildException(e);
		}
	}

}
