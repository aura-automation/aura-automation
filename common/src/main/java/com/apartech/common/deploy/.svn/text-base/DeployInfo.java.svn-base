package com.apartech.common.deploy;

import com.apartech.common.Constants.DeployValues;
import com.apartech.common.version.VersionInfo;

/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
public class DeployInfo {

	/**
	 * Used from UI to identify if we need to compare all the resources.
	 * 
	 */
	
	
	private String[] currentSelectedResources;
	
	private DeployInfo sourceDeployInfo;
	
	private String userInstallRoot;
	
	private String wasRespositoryRoot;

	private boolean includeAllChildren;
	
	private String mSyncReportLocation;
	
	private String operationMode = DeployValues.OPERATION_MODE_REPORTONLY;

	private String connectionMode = DeployValues.CONNECTION_MODE_REMOTE;
	
	private String 	mResourceXML;
	
	private String mEnvironmentProperties;

	private String 	mSyncResourceXML;

	private String mEARFileLocation;
	
	private String mCluster;

	private String mCell;
	
	private String mApplicationName;

	private int applicationNumber;
	
	private String mDeployDataLocation;

	private String mServer;

	private String mNode;
	
	private String mPort;
	
	private String mUserName;
	
	private String mPassword;

	private String mHost;

	private String mOperation;

	private String mConnectionType;

	private String mJNDIPort;

	private String mJNDIUrl;
	
	private boolean mFailIfResourceAbsent;
	
	private boolean misMultiEAR;

	private String mMultiEARLocation;

	private String mMultiEARDeployData;

	private String mWebserver;

	private String mVirtualHost;

	private Integer mStartingWeight;

	private boolean mShouldStart;
	
	private boolean mShouldGenerateEJBCode;

	private String mRemoteEARDirectory;

	private int mSleepTimeForSyncRequest ;

	private boolean mParentLastClassLoaderMode ;
	
	private boolean  mGenerateEJBDeployCode ;

	private boolean startClusterBeforeDeploy ;

	private boolean reStartClusterAfterDeploy;

	private boolean mZeroBinaryCopy;
	
	private boolean mFailOnError;
	
	private boolean mEnable;
	
	private String rulesXML;

	private VersionInfo versionInfo;

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
	 * @return the versionInfo
	 */
	public VersionInfo getVersionInfo() {
		return versionInfo;
	}

	/**
	 * @param versionInfo the versionInfo to set
	 */
	public void setVersionInfo(VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}

	/** this is used only from UI where user selects an environment from 
		the drop down for source and target. In code this is used to get the 
		variable list.
	**/
	private int environmentId =-1;

	/**
	 * Returns the mApplicationName.
	 * @return String
	 */
	

	private String SOAPSecurityConfig;
	
	private String SASSecurityConfig;
	
	private String JAACSecurityConfig;
	
	private String SSLSecurityConfig;

	public String getApplicationName() {
		return mApplicationName;
	}

	/**
	 * Returns the mCell.
	 * @return String
	 */
	public String getCell() {
		return mCell;
	}

	/**
	 * Returns the mCluster.
	 * @return String
	 */
	public String getCluster() {
		return mCluster;
	}

	/**
	 * Returns the mEARFileLocation.
	 * @return String
	 */
	public String getEARFileLocation() {
		return mEARFileLocation;
	}

	/**
	 * Sets the mApplicationName.
	 * @param mApplicationName The mApplicationName to set
	 */
	public void setApplicationName(String mApplicationName) {
		this.mApplicationName = mApplicationName;
	}

	/**
	 * Sets the mCell.
	 * @param mCell The mCell to set
	 */
	public void setCell(String mCell) {
		this.mCell = mCell;
	}

	/**
	 * Sets the mCluster.
	 * @param mCluster The mCluster to set
	 */
	public void setCluster(String mCluster) {
		this.mCluster = mCluster;
	}

	/**
	 * Sets the mEARFileLocation.
	 * @param mEARFileLocation The mEARFileLocation to set
	 */
	public void setEARFileLocation(String mEARFileLocation) {
		this.mEARFileLocation = mEARFileLocation;
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

	public String getNode() {
		return mNode;
	}

	public String getServer() {
		return mServer;
	}

	public void setNode(String mNode) {
		this.mNode = mNode;
	}

	public void setServer(String mServer) {
		this.mServer = mServer;
	}

	public String getPassword() {
		return mPassword;
	}

	public String getPort() {
		return mPort;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public void setPort(String mPort) {
		this.mPort = mPort;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}


	public String getHost() {
		return mHost;
	}

	public void setHost(String mHost) {
		this.mHost = mHost;
	}

	public String getOperation() {
		return mOperation;
	}

	public void setOperation(String mOperation) {
		this.mOperation = mOperation;
	}

	public String getConnectionType() {
		return mConnectionType;
	}

	public void setConnectionType(String mConnectionType) {
		this.mConnectionType = mConnectionType;
	}

	public String getJNDIPort() {
		return mJNDIPort;
	}

	public void setJNDIPort(String mJNDIPort) {
		this.mJNDIPort = mJNDIPort;
	}

	public String getJNDIUrl() {
		return mJNDIUrl;
	}

	public void setJNDIUrl(String mJNDIUrl) {
		this.mJNDIUrl = mJNDIUrl;
	}

	public boolean getFailIfResourceAbsent() {
		return mFailIfResourceAbsent;
	}

	public void setFailIfResourceAbsent(boolean mFailIfResourceAbsent) {
		this.mFailIfResourceAbsent = mFailIfResourceAbsent;
	}
	

	/**
	 * @return Returns the misMultiEAR.
	 */
	public boolean isMultiEAR() {
		return misMultiEAR;
	}
	/**
	 * @param misMultiEAR The misMultiEAR to set.
	 */
	public void setisMultiEAR(boolean misMultiEAR) {
		this.misMultiEAR = misMultiEAR;
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
	 * @return Returns the mMultiEARLocation.
	 */
	public String getMultiEARLocation() {
		return mMultiEARLocation;
	}
	/**
	 * @param multiEARLocation The mMultiEARLocation to set.
	 */
	public void setMultiEARLocation(String multiEARLocation) {
		mMultiEARLocation = multiEARLocation;
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
	/**
	 * @return Returns the mVirtualHost.
	 */
	public String getVirtualHost() {
		return mVirtualHost;
	}
	/**
	 * @param virtualHost The mVirtualHost to set.
	 */
	public void setVirtualHost(String virtualHost) {
		mVirtualHost = virtualHost;
	}
    /**
     * @return Returns the mStartingWeight.
     */
    public Integer getStartingWeight() {
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
    public boolean isShouldStart() {
        return mShouldStart;
    }
    /**
     * @param shouldStart The mShouldStart to set.
     */
    public void setShouldStart(boolean shouldStart) {
        mShouldStart = shouldStart;
    }
	/**
	 * @return Returns the mShouldGenerateEJBCode.
	 */
	public boolean isShouldGenerateEJBCode() {
		return mShouldGenerateEJBCode;
	}
	/**
	 * @param shouldGenerateEJBCode The mShouldGenerateEJBCode to set.
	 */
	public void setShouldGenerateEJBCode(boolean shouldGenerateEJBCode) {
		mShouldGenerateEJBCode = shouldGenerateEJBCode;
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
			boolean parentLastClassLoaderMode) {
		mParentLastClassLoaderMode = parentLastClassLoaderMode;
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
	 * @return the applicationNumber
	 */
	public int getApplicationNumber() {
		return applicationNumber;
	}

	/**
	 * @param applicationNumber the applicationNumber to set
	 */
	public void setApplicationNumber(int applicationNumber) {
		this.applicationNumber = applicationNumber;
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
	 * @return the connectionMode
	 */
	public String getConnectionMode() {
		return connectionMode;
	}

	/**
	 * @param connectionMode the connectionMode to set
	 */
	public void setConnectionMode(String connectionMode) {
		this.connectionMode = connectionMode;
	}

	/**
	 * @return the operationMode
	 */
	public String getOperationMode() {
		return operationMode;
	}

	/**
	 * @param operationMode the operationMode to set
	 */
	public void setOperationMode(String operationMode) {
		this.operationMode = operationMode;
	}

	/**
	 * @return the mResourceXML
	 */
	public String getResourceXML() {
		return mResourceXML;
	}

	/**
	 * @param resourceXML the mResourceXML to set
	 */
	public void setResourceXML(String resourceXML) {
		mResourceXML = resourceXML;
	}

	/**
	 * @return the mSyncResourceXML
	 */
	public String getSyncResourceXML() {
		return mSyncResourceXML;
	}

	/**
	 * @param syncResourceXML the mSyncResourceXML to set
	 */
	public void setSyncResourceXML(String syncResourceXML) {
		mSyncResourceXML = syncResourceXML;
	}

	/**
	 * @return the mEnvironmentProperties
	 */
	public String getEnvironmentProperties() {
		return mEnvironmentProperties;
	}

	/**
	 * @param environmentProperties the mEnvironmentProperties to set
	 */
	public void setEnvironmentProperties(String environmentProperties) {
		mEnvironmentProperties = environmentProperties;
	}

	/**
	 * @return the mSyncReportLocation
	 */
	public String getSyncReportLocation() {
		return mSyncReportLocation;
	}

	/**
	 * @param syncReportLocation the mSyncReportLocation to set
	 */
	public void setSyncReportLocation(String syncReportLocation) {
		mSyncReportLocation = syncReportLocation;
	}

	/**
	 * @return the includeAllChildren
	 */
	public boolean isIncludeAllChildren() {
		return includeAllChildren;
	}

	/**
	 * @param includeAllChildren the includeAllChildren to set
	 */
	public void setIncludeAllChildren(boolean includeAllChildren) {
		this.includeAllChildren = includeAllChildren;
	}

	/**
	 * @return the userInstallRoot
	 */
	public String getUserInstallRoot() {
		return userInstallRoot;
	}

	/**
	 * @param userInstallRoot the userInstallRoot to set
	 */
	public void setUserInstallRoot(String userInstallRoot) {
		this.userInstallRoot = userInstallRoot;
	}

	/**
	 * @return the wasRespositoryRoot
	 */
	public String getWasRespositoryRoot() {
		return wasRespositoryRoot;
	}

	/**
	 * @param wasRespositoryRoot the wasRespositoryRoot to set
	 */
	public void setWasRespositoryRoot(String wasRespositoryRoot) {
		this.wasRespositoryRoot = wasRespositoryRoot;
	}

	/**
	 * @return the sourceDeployInfo
	 */
	public DeployInfo getSourceDeployInfo() {
		return sourceDeployInfo;
	}

	/**
	 * @param sourceDeployInfo the sourceDeployInfo to set
	 */
	public void setSourceDeployInfo(DeployInfo sourceDeployInfo) {
		this.sourceDeployInfo = sourceDeployInfo;
	}

	/**
	 * @return the currentSelectedResources
	 */
	public String[] getCurrentSelectedResources() {
		return currentSelectedResources;
	}

	/**
	 * @param currentSelectedResources the currentSelectedResources to set
	 */
	public void setCurrentSelectedResources(String[] currentSelectedResources) {
		this.currentSelectedResources = currentSelectedResources;
	}

	/**
	 * @return the environmentId
	 */
	public int getEnvironmentId() {
		return environmentId;
	}

	/**
	 * @param environmentId the environmentId to set
	 */
	public void setEnvironmentId(int environmentId) {
		this.environmentId = environmentId;
	}

	/**
	 * @return the sOAPSecurityConfig
	 */
	public String getSOAPSecurityConfig() {
		return SOAPSecurityConfig;
	}

	/**
	 * @param securityConfig the sOAPSecurityConfig to set
	 */
	public void setSOAPSecurityConfig(String securityConfig) {
		SOAPSecurityConfig = securityConfig;
	}

	/**
	 * @return the sASSecurityConfig
	 */
	public String getSASSecurityConfig() {
		return SASSecurityConfig;
	}

	/**
	 * @param securityConfig the sASSecurityConfig to set
	 */
	public void setSASSecurityConfig(String securityConfig) {
		SASSecurityConfig = securityConfig;
	}

	/**
	 * @return the jAASSecurityConfig
	 */
	public String getJAACSecurityConfig() {
		return JAACSecurityConfig;
	}

	/**
	 * @param securityConfig the jAASSecurityConfig to set
	 */
	public void setJAACSecurityConfig(String securityConfig) {
		JAACSecurityConfig = securityConfig;
	}

	/**
	 * @return the sSLSecurityConfig
	 */
	public String getSSLSecurityConfig() {
		return SSLSecurityConfig;
	}

	/**
	 * @param securityConfig the sSLSecurityConfig to set
	 */
	public void setSSLSecurityConfig(String securityConfig) {
		SSLSecurityConfig = securityConfig;
	}

	/**
	 * @return the rulesXML
	 */
	public String getRulesXML() {
		return rulesXML;
	}

	/**
	 * @param rulesXML the rulesXML to set
	 */
	public void setRulesXML(String rulesXML) {
		this.rulesXML = rulesXML;
	}

	
	
}


