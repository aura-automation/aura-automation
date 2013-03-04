/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.common.ant;

import org.apache.tools.ant.BuildException;

import com.apartech.common.deploy.DeployInfo;
import com.apartech.common.sync.SyncNode;

public class AntSynNode extends AntTask{
	
	public void execute()
		throws BuildException{
		
		try{	
			DeployInfo deployInfo = getPopluatedDeployInfo();
			SyncNode synNode = new SyncNode();
			synNode.syncAllNodes(deployInfo);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
		}
			
	}
	
}
