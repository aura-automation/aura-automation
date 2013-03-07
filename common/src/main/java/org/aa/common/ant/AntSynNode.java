/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.common.ant;

import org.aa.common.deploy.DeployInfo;
import org.aa.common.sync.SyncNode;
import org.apache.tools.ant.BuildException;


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
