/**	   Copyright 


**/
package org.aa.auradeploy.deployentry;

import org.aa.auradeploy.deploy.Deploy;
import org.aa.auradeploy.deploy.DeployInfo;

import org.aa.common.exception.DeployException;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;

public class DeployEntry {

	String mCell = "Avatar2Cell01";
	String mCluster = "RemoteGPNWOMA-CL";
	String mConnectionType = "RMI";
	boolean mFailIfResourceAbsent  = false;
	boolean generateEJBDeployCode = false;
	String mHost = "Avatar2";
	boolean misMultiEAR = true;
	
	String multiEARDeployData = "C:\\jatin\\eclipse\\SlickDeploy-Test\\SlickDeploy\\deploydata\\soa";
	String multiEARLocation = "C:\\jatin\\eclipse\\SlickDeploy-Test\\SlickDeploy\\ear\\soa";
	String mNode = "Avatar2Node02";
	String mOperation = "uninstall";
	boolean parentLastClassLoaderMode = true;
	String mPassword = "jatin";
	String mPort = "9810";
	String remoteEARDirectory = "C:\\jatin\\eclipse\\SlickDeploy-Test\\SlickDeploy\\deploydata\\ear\\soa";
	boolean reStartClusterAfterDeploy = false;
	String mUserName = "jatin";

	public void startDeploy()
		throws DeployException{
		try{
			
			DeployInfo deployInfo = new DeployInfo();
			
			deployInfo.setCell(mCell);
			deployInfo.setCluster(mCluster);
			deployInfo.setConnectionType(mConnectionType);
			deployInfo.setFailIfResourceAbsent(mFailIfResourceAbsent);
			deployInfo.setGenerateEJBDeployCode(generateEJBDeployCode);
			deployInfo.setHost(mHost);
			deployInfo.setisMultiEAR(misMultiEAR);
			deployInfo.setMultiEARDeployData(multiEARDeployData);
			deployInfo.setMultiEARLocation(multiEARLocation);
			deployInfo.setNode(mNode);
			deployInfo.setOperation(mOperation);
			deployInfo.setParentLastClassLoaderMode(parentLastClassLoaderMode);
			deployInfo.setPassword(mPassword);
			deployInfo.setPort(mPort);
			deployInfo.setRemoteEARDirectory(remoteEARDirectory);
			deployInfo.setReStartClusterAfterDeploy(reStartClusterAfterDeploy);
			deployInfo.setUserName(mUserName);
			
			Deploy deploy = new Deploy();
			deploy.startWork(deployInfo);
			System.out.println(" control returned ");
		//	Runtime.getRuntime().exit(1);
			
			
			
		}catch(DeployException e){
			
			e.printStackTrace();
			throw new DeployException(e);
		}catch(ConnectorException e){
					
					e.printStackTrace();
					throw new DeployException(e);
					
		}catch(ConfigServiceException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(AdminException e){
			e.printStackTrace();
			throw new DeployException(e);
		}catch(TransferFailedException e){
			e.printStackTrace();
			throw new DeployException(e);
		}
	}

	public static void main(String[] args) {
		try{
		DeployEntry deployEntry = new DeployEntry();
		deployEntry.startDeploy();
		}catch(DeployException e){
			e.printStackTrace();
		}
	}
}
