/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.auradeploy.ant;

import org.aa.auradeploy.cluster.Cluster;
import org.aa.auradeploy.deploy.DeployInfo;
import org.apache.tools.ant.BuildException;


import org.aa.common.Constants.LicenseConstants;
import org.aa.common.exception.DeployException;

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
