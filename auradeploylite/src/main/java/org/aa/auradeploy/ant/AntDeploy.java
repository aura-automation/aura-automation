/*
 * Created on 23-Jun-2005
 *
 * Copyright (C) 

 */
package org.aa.auradeploy.ant;

import org.aa.auradeploy.deploy.Deploy;
import org.aa.auradeploy.deploy.DeployInfo;
import org.apache.tools.ant.Task;

import java.io.File;

import org.apache.tools.ant.BuildException;

import org.aa.common.Constants.LicenseConstants;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.client.TransferFailedException;
/**
 * @author Jatin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AntDeploy extends AntApplicationTask
{

	
	private String mDeployDataLocation;

	private boolean mFailIfResourceAbsent;

	private String mMultiEARDeployData;

	private String mWebserver;

	private Integer  mStartingWeight;
	
	private boolean  mShouldStart = false;

	private String mRemoteEARDirectory;

	private int mSleepTimeForSyncRequest = 50000;

	private boolean mGenerateEJBDeployCode = false;
	
	private boolean mParentLastClassLoaderMode = true;

	private boolean startClusterBeforeDeploy = false;

	private boolean reStartClusterAfterDeploy = false;
	
	private boolean mZeroBinaryCopy = false;

	private boolean mFailOnError = false;

	private boolean mEnable = true;

	private String mContextRoot ;
	
	private File mWorkArea ;

	private boolean defaultValues = false;

	private boolean releaseMI = false;
	
	private String targetOS;
	
	/**
	 * 
	 * @return
	 */
	public String getTargetOS() {
		return targetOS;
	}

	/**
	 * 
	 * @param targetOS
	 */
	public void setTargetOS(String targetOS) {
		this.targetOS = targetOS;
	}

	/**
	 * @return the releaseMI
	 */
	public boolean isReleaseMI() {
		return releaseMI;
	}

	/**
	 * @param releaseMI the releaseMI to set
	 */
	public void setReleaseMI(boolean releaseMI) {
		this.releaseMI = releaseMI;
	}

	/**
	 * Returns the mDeployDataLocation.
	 * @return String
	 */
	public String getDeployDataLocation() {
		return mDeployDataLocation;
	}

	public void setDeployDataLocation(String mDeployDataLocation) {
		this.mDeployDataLocation = mDeployDataLocation;
	}

	public boolean getFailIfResourceAbsent() {
		return mFailIfResourceAbsent;
	}

	public void setFailIfResourceAbsent(boolean mFailIfResourceAbsent) {
		this.mFailIfResourceAbsent = mFailIfResourceAbsent;
	}
	

	/**
	 * @return Returns the mMultiEARDeployData.
	 */
	public String getMultiEARDeployData() {
		return mMultiEARDeployData;
	}
	/**
	 * @param multiEARDeployData The mMultiEARDeployData to set.
	 */
	public void setMultiEARDeployData(String multiEARDeployData) {
		mMultiEARDeployData = multiEARDeployData;
	}
	/**
	 * @return Returns the mWebserver.
	 */
	public String getWebserver() {
		return mWebserver;
	}
	/**
	 * @param webserver The mWebserver to set.
	 */
	public void setWebserver(String webserver) {
		mWebserver = webserver;
	}


	public void execute()
		throws BuildException{
		try{	
	
				DeployInfo deployInfo = getPopluatedDeployInfo();
	
				deployInfo.setDeployDataLocation(mDeployDataLocation);
				deployInfo.setFailIfResourceAbsent(mFailIfResourceAbsent);
				deployInfo.setMultiEARDeployData(mMultiEARDeployData);
				deployInfo.setWebserver(mWebserver);
				deployInfo.setStartingWeight(mStartingWeight);
				deployInfo.setShouldStart(mShouldStart);
		
				deployInfo.setRemoteEARDirectory(mRemoteEARDirectory);
				deployInfo.setParentLastClassLoaderMode(mParentLastClassLoaderMode);
				deployInfo.setSleepTimeForSyncRequest(mSleepTimeForSyncRequest);
				deployInfo.setGenerateEJBDeployCode(mGenerateEJBDeployCode);
		
				deployInfo.setStartClusterBeforeDeploy(startClusterBeforeDeploy);
				deployInfo.setReStartClusterAfterDeploy(reStartClusterAfterDeploy);
				deployInfo.setZeroBinaryCopy(mZeroBinaryCopy);
				deployInfo.setFailOnError(mFailOnError);
				deployInfo.setEnable(mEnable);
				deployInfo.setContextRoot(mContextRoot);
				deployInfo.setWorkArea(mWorkArea);
				deployInfo.setDefaultValues(defaultValues);
				deployInfo.setReleaseMI(releaseMI);
	
				deployInfo.setTargetOS(targetOS);
	
				Deploy deploy = new Deploy();
				AntValidator antValidator  = new AntValidator();
				boolean valid=  antValidator.validate(deployInfo);
				
				if (!valid){
					throw new BuildException("Data not valid");
				}
				String antNodesVariable = deploy.startWork(deployInfo);
				String _nodes ="";
				if (antNodesVariable.trim().length()>0){
					_nodes = antNodesVariable.substring(0,antNodesVariable.length()-1);
					getProject().setNewProperty("_Nodes",_nodes);
				}
		}catch(DeployException e){
			e.printStackTrace();
			throw new BuildException(e);
		}catch(ConnectorException e){
			e.printStackTrace();
			throw new BuildException(e);
			
		}catch(ConfigServiceException e){
			e.printStackTrace();
			throw new BuildException(e);
			
		}catch(AdminException e){
			e.printStackTrace();
			throw new BuildException(e);
			
		}catch(TransferFailedException e){
			e.printStackTrace();
			throw new BuildException(e);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
			
		}		
	}
    /**
     * @return Returns the mStartingWeight.
     */
    public Integer  getStartingWeight() {
        return mStartingWeight;
    }
    /**
     * @param startingWeight The mStartingWeight to set.
     */
    public void setStartingWeight(Integer startingWeight) {
        mStartingWeight = startingWeight;
    }
    /**
     * @return Returns the mShouldStart.
     */
    public boolean getShouldStart() {
        return mShouldStart;
    }
    /**
     * @param shouldStart The mShouldStart to set.
     */
    public void setShouldStart(boolean shouldStart) {
        mShouldStart = shouldStart;
    }
	/**
	 * @return Returns the mParentFirstClassLoaderMode.
	 */
	public boolean isParentLastClassLoaderMode() {
		return mParentLastClassLoaderMode;
	}
	/**
	 * @param parentFirstClassLoaderMode The mParentFirstClassLoaderMode to set.
	 */
	public void setParentLastClassLoaderMode(
			boolean parentFirstClassLoaderMode) {
		mParentLastClassLoaderMode = parentFirstClassLoaderMode;
	}
	/**
	 * @return Returns the mRemoteEARDirectory.
	 */
	public String getRemoteEARDirectory() {
		return mRemoteEARDirectory;
	}
	/**
	 * @param remoteEARDirectory The mRemoteEARDirectory to set.
	 */
	public void setRemoteEARDirectory(String remoteEARDirectory) {
		mRemoteEARDirectory = remoteEARDirectory;
	}
	/**
	 * @return Returns the mSleepTimeForSyncRequest.
	 */
	public int getSleepTimeForSyncRequest() {
		return mSleepTimeForSyncRequest;
	}
	/**
	 * @param sleepTimeForSyncRequest The mSleepTimeForSyncRequest to set.
	 */
	public void setSleepTimeForSyncRequest(int sleepTimeForSyncRequest) {
		mSleepTimeForSyncRequest = sleepTimeForSyncRequest;
	}
	/**
	 * @return Returns the mGenerateEJBDeployCode.
	 */
	public boolean isGenerateEJBDeployCode() {
		return mGenerateEJBDeployCode;
	}
	/**
	 * @param generateEJBDeployCode The mGenerateEJBDeployCode to set.
	 */
	public void setGenerateEJBDeployCode(boolean generateEJBDeployCode) {
		mGenerateEJBDeployCode = generateEJBDeployCode;
	}
	/**
	 * @return Returns the reStartClusterAfterDeploy.
	 */
	public boolean isReStartClusterAfterDeploy() {
		return reStartClusterAfterDeploy;
	}
	/**
	 * @param reStartClusterAfterDeploy The reStartClusterAfterDeploy to set.
	 */
	public void setReStartClusterAfterDeploy(boolean reStartClusterAfterDeploy) {
		this.reStartClusterAfterDeploy = reStartClusterAfterDeploy;
	}
	/**
	 * @return Returns the startClusterBeforeDeploy.
	 */
	public boolean isStartClusterBeforeDeploy() {
		return startClusterBeforeDeploy;
	}
	/**
	 * @param startClusterBeforeDeploy The startClusterBeforeDeploy to set.
	 */
	public void setStartClusterBeforeDeploy(boolean startClusterBeforeDeploy) {
		this.startClusterBeforeDeploy = startClusterBeforeDeploy;
	}

	/**
	 * @return the mZeroBinaryCopy
	 */
	public boolean isZeroBinaryCopy() {
		return mZeroBinaryCopy;
	}

	/**
	 * @param zeroBinaryCopy the mZeroBinaryCopy to set
	 */
	public void setZeroBinaryCopy(boolean zeroBinaryCopy) {
		mZeroBinaryCopy = zeroBinaryCopy;
	}

	/**
	 * @return the mFailOnError
	 */
	public boolean isFailOnError() {
		return mFailOnError;
	}

	/**
	 * @param failOnError the mFailOnError to set
	 */
	public void setFailOnError(boolean failOnError) {
		mFailOnError = failOnError;
	}

	/**
	 * @return the mEnable
	 */
	public boolean isEnable() {
		return mEnable;
	}

	/**
	 * @param enable the mEnable to set
	 */
	public void setEnable(boolean enable) {
		mEnable = enable;
	}

	/**
	 * @return the mContextRoot
	 */
	public String getContextRoot() {
		return mContextRoot;
	}

	/**
	 * @param contextRoot the mContextRoot to set
	 */
	public void setContextRoot(String contextRoot) {
		mContextRoot = contextRoot;
	}

	/**
	 * @return the mWorkArea
	 */
	public File getWorkArea() {
		return mWorkArea;
	}

	/**
	 * @param workArea the mWorkArea to set
	 */
	public void setWorkArea(File workArea) {
		mWorkArea = workArea;
	}

	/**
	 * @return the defaultValues
	 */
	public boolean isDefaultValues() {
		return defaultValues;
	}

	/**
	 * @param defaultValues the defaultValues to set
	 */
	public void setDefaultValues(boolean defaultValues) {
		this.defaultValues = defaultValues;
	}
}
