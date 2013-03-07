/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.common.ant;

import org.aa.common.deploy.DeployInfo;
import org.apache.tools.ant.Task;


public class AntTask extends Task{

	private String mCluster;

	private String mCell;
	
	private String mApplicationName;

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

	private String mConnectionMode;
	
	private String mOperationMode;

	private String mResourceXML;

	private String mSyncResourceXML;
	
	private String mEnvironmentProperties;

	private String mSyncReportLocation;
	
	private boolean mIncludeAllChildren = true;

	private String mUserInstallRoot;
	
	private String mWasRespositoryRoot;

	private String SOAPSecurityConfig;
	
	private String SASSecurityConfig;
	
	private String JAACSecurityConfig;
	
	private String SSLSecurityConfig;
	
	private String mRulesXML;

	private int mSleepTimeForSyncRequest = 50000;


	
	/**
	 * @return the resourceXML
	 */
	public String getResourceXML() {
		return mResourceXML;
	}


	/**
	 * @param resourceXML the resourceXML to set
	 */
	public void setResourceXML(String resourceXML) {
		this.mResourceXML = resourceXML;
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

	/**
	 * Returns the mApplicationName.
	 * @return String
	 */
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
	
	
	public DeployInfo getPopluatedDeployInfo(){
		
		DeployInfo deployInfo = new DeployInfo();
		deployInfo.setApplicationName(mApplicationName);
		deployInfo.setCell(mCell);
		deployInfo.setCluster(mCluster);
		deployInfo.setConnectionType(mConnectionType);
		deployInfo.setHost(mHost);
		deployInfo.setJNDIPort(mJNDIPort);
		deployInfo.setJNDIUrl(mJNDIUrl);
		deployInfo.setNode(mNode);
		deployInfo.setOperation(mOperation);
		deployInfo.setPassword(mPassword);
		deployInfo.setPort(mPort);
		deployInfo.setServer(mServer);
		deployInfo.setUserName(mUserName);
		deployInfo.setConnectionMode(mConnectionMode);
		deployInfo.setOperationMode(mOperationMode);
		deployInfo.setResourceXML(mResourceXML);
		deployInfo.setRulesXML(mRulesXML);

		deployInfo.setSyncResourceXML(mSyncResourceXML);
		deployInfo.setEnvironmentProperties(mEnvironmentProperties);
		deployInfo.setSyncReportLocation(mSyncReportLocation);
		deployInfo.setIncludeAllChildren(mIncludeAllChildren);
		deployInfo.setUserInstallRoot(mUserInstallRoot);
		deployInfo.setWasRespositoryRoot(mWasRespositoryRoot);

		deployInfo.setSASSecurityConfig(SASSecurityConfig);
		deployInfo.setSOAPSecurityConfig(SOAPSecurityConfig);
		deployInfo.setJAACSecurityConfig(JAACSecurityConfig);
		deployInfo.setSSLSecurityConfig(SSLSecurityConfig);
		deployInfo.setSleepTimeForSyncRequest(mSleepTimeForSyncRequest);

		
		return deployInfo;

		
	}

	/**
	 * @return the mConnectionMode
	 */
	public String getConnectionMode() {
		return mConnectionMode;
	}

	/**
	 * @param connectionMode the mConnectionMode to set
	 */
	public void setConnectionMode(String connectionMode) {
		mConnectionMode = connectionMode;
	}

	/**
	 * @return the mOperationMode
	 */
	public String getOperationMode() {
		return mOperationMode;
	}

	/**
	 * @param operationMode the mOperationMode to set
	 */
	public void setOperationMode(String operationMode) {
		mOperationMode = operationMode;
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
	 * @return the mIncludeAllChildren
	 */
	public boolean getIncludeAllChildren() {
		return mIncludeAllChildren;
	}


	/**
	 * @param includeAllChildren the mIncludeAllChildren to set
	 */
	public void setIncludeAllChildren(boolean includeAllChildren) {
		mIncludeAllChildren = includeAllChildren;
	}


	/**
	 * @return the mUserInstallRoot
	 */
	public String getUserInstallRoot() {
		return mUserInstallRoot;
	}


	/**
	 * @param userInstallRoot the mUserInstallRoot to set
	 */
	public void setUserInstallRoot(String userInstallRoot) {
		mUserInstallRoot = userInstallRoot;
	}


	/**
	 * @return the mWasRespositoryRoot
	 */
	public String getWasRespositoryRoot() {
		return mWasRespositoryRoot;
	}


	/**
	 * @param wasRespositoryRoot the mWasRespositoryRoot to set
	 */
	public void setWasRespositoryRoot(String wasRespositoryRoot) {
		mWasRespositoryRoot = wasRespositoryRoot;
	}

	/**
	 * @return the sOAPSecurityConfig
	 */
	public String getSoapSecurityConfig() {
		return SOAPSecurityConfig;
	}

	/**
	 * @param securityConfig the sOAPSecurityConfig to set
	 */
	public void setSoapSecurityConfig(String securityConfig) {
		SOAPSecurityConfig = securityConfig;
	}

	/**
	 * @return the sASSecurityConfig
	 */
	public String getSasSecurityConfig() {
		return SASSecurityConfig;
	}

	/**
	 * @param securityConfig the sASSecurityConfig to set
	 */
	public void setSasSecurityConfig(String securityConfig) {
		SASSecurityConfig = securityConfig;
	}

	/**
	 * @return the jAASSecurityConfig
	 */
	public String getJaacSecurityConfig() {
		return JAACSecurityConfig;
	}

	/**
	 * @param securityConfig the jAASSecurityConfig to set
	 */
	public void setJaacSecurityConfig(String securityConfig) {
		JAACSecurityConfig = securityConfig;
	}

	/**
	 * @return the sSLSecurityConfig
	 */
	public String getSslSecurityConfig() {
		return SSLSecurityConfig;
	}

	/**
	 * @param securityConfig the sSLSecurityConfig to set
	 */
	public void setSslSecurityConfig(String securityConfig) {
		SSLSecurityConfig = securityConfig;
	}


	/**
	 * @return the rulesXML
	 */
	public String getRulesXML() {
		return mRulesXML;
	}


	/**
	 * @param rulesXML the rulesXML to set
	 */
	public void setRulesXML(String rulesXML) {
		this.mRulesXML = rulesXML;
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

}
