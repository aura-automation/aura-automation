/**	   Copyright [2009] [www.apartech.co.uk]


**/
package com.apartech.auradeploy.ant;

import org.apache.tools.ant.Task;

import com.apartech.auradeploy.deploy.DeployInfo;
import com.apartech.common.log.SDLog;


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
	
	private String SOAPSecurityConfig;
	
	private String SASSecurityConfig;
	
	private String JAACSecurityConfig;
	
	private String SSLSecurityConfig;

	private String mEnvironmentProperties;

	private boolean nowait; 
	
	private boolean failOnError=false;
	
	public boolean getNoWait()
	{
		return nowait;
	}
	public void setNoWait(boolean mnowait)
	{
		this.nowait=mnowait;
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


	AntTask(){
		
	//	SDLog.log("Aura Deploy Lite Version 1.0");
	//	SDLog.log("Copyright Apartech Ltd ");
		
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

	public boolean isFailOnError() {
		return failOnError;
	}
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}
	
	DeployInfo getPopluatedDeployInfo(){
		
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
		deployInfo.setEnvironmentProperties(mEnvironmentProperties);

		deployInfo.setSASSecurityConfig(SASSecurityConfig);
		deployInfo.setSOAPSecurityConfig(SOAPSecurityConfig);
		deployInfo.setJAACSecurityConfig(JAACSecurityConfig);
		deployInfo.setSSLSecurityConfig(SSLSecurityConfig);
		deployInfo.setNowait(nowait);
		deployInfo.setFailOnError(failOnError);
		
		return deployInfo;

		
	}




}
