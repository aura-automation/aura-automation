/**	   Copyright [2009] [www.apartech.co.uk]


**/
package com.apartech.auraconfig.ant.base;

import org.apache.tools.ant.Task;

import com.apartech.common.deploy.DeployInfo;
public class AntResourceCompareTask extends Task{

	private String sourceHost;
	
	private String sourceConnectionType;

	private String sourceConnectionMode;
	
	private String sourcePort;
	
	private String sourceUserName;
	
	private String sourcePassword;

	private String sourceOperation;

	private String sourceUserInstallRoot;
	
	private String sourceWasRespositoryRoot;
	
	private String sourceEnvironmentProperties;

	private String targetHost;
	
	private String targetConnectionType;

	private String targetConnectionMode;
	
	private String targetPort;
	
	private String targetUserName;
	
	private String targetPassword;

	private String targetOperation;

	private String targetUserInstallRoot;
	
	private String targetWasRespositoryRoot;
	
	private String targetEnvironmentProperties;

	private String mApplicationName;

	private String mOperationMode;

	private String mResourceXML;

	private String mSyncResourceXML;
	
	private String mSyncReportLocation;
	
	private boolean mIncludeAllChildren = true;

	private String workingArea ;
	
	private String SOAPSecurityConfig;
	
	private String SASSecurityConfig;
	
	private String JAACSecurityConfig;
	
	private String SSLSecurityConfig;

	/**
	 * @return the sourceHost
	 */
	public String getSourceHost() {
		return sourceHost;
	}

	/**
	 * @param sourceHost the sourceHost to set
	 */
	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}

	/**
	 * @return the sourceConnectionType
	 */
	public String getSourceConnectionType() {
		return sourceConnectionType;
	}

	/**
	 * @param sourceConnectionType the sourceConnectionType to set
	 */
	public void setSourceConnectionType(String sourceConnectionType) {
		this.sourceConnectionType = sourceConnectionType;
	}

	/**
	 * @return the sourceConnectionMode
	 */
	public String getSourceConnectionMode() {
		return sourceConnectionMode;
	}

	/**
	 * @param sourceConnectionMode the sourceConnectionMode to set
	 */
	public void setSourceConnectionMode(String sourceConnectionMode) {
		this.sourceConnectionMode = sourceConnectionMode;
	}

	/**
	 * @return the sourcePort
	 */
	public String getSourcePort() {
		return sourcePort;
	}

	/**
	 * @param sourcePort the sourcePort to set
	 */
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	/**
	 * @return the sourceUserName
	 */
	public String getSourceUserName() {
		return sourceUserName;
	}

	/**
	 * @param sourceUserName the sourceUserName to set
	 */
	public void setSourceUserName(String sourceUserName) {
		this.sourceUserName = sourceUserName;
	}

	/**
	 * @return the sourcePassword
	 */
	public String getSourcePassword() {
		return sourcePassword;
	}

	/**
	 * @param sourcePassword the sourcePassword to set
	 */
	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	/**
	 * @return the sourceOperation
	 */
	public String getSourceOperation() {
		return sourceOperation;
	}

	/**
	 * @param sourceOperation the sourceOperation to set
	 */
	public void setSourceOperation(String sourceOperation) {
		this.sourceOperation = sourceOperation;
	}

	/**
	 * @return the sourceUserInstallRoot
	 */
	public String getSourceUserInstallRoot() {
		return sourceUserInstallRoot;
	}

	/**
	 * @param sourceUserInstallRoot the sourceUserInstallRoot to set
	 */
	public void setSourceUserInstallRoot(String sourceUserInstallRoot) {
		this.sourceUserInstallRoot = sourceUserInstallRoot;
	}

	/**
	 * @return the sourceWasRespositoryRoot
	 */
	public String getSourceWasRespositoryRoot() {
		return sourceWasRespositoryRoot;
	}

	/**
	 * @param sourceWasRespositoryRoot the sourceWasRespositoryRoot to set
	 */
	public void setSourceWasRespositoryRoot(String sourceWasRespositoryRoot) {
		this.sourceWasRespositoryRoot = sourceWasRespositoryRoot;
	}

	/**
	 * @return the sourceEnvironmentProperties
	 */
	public String getSourceEnvironmentProperties() {
		return sourceEnvironmentProperties;
	}

	/**
	 * @param sourceEnvironmentProperties the sourceEnvironmentProperties to set
	 */
	public void setSourceEnvironmentProperties(String sourceEnvironmentProperties) {
		this.sourceEnvironmentProperties = sourceEnvironmentProperties;
	}

	/**
	 * @return the targetHost
	 */
	public String getTargetHost() {
		return targetHost;
	}

	/**
	 * @param targetHost the targetHost to set
	 */
	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	/**
	 * @return the targetConnectionType
	 */
	public String getTargetConnectionType() {
		return targetConnectionType;
	}

	/**
	 * @param targetConnectionType the targetConnectionType to set
	 */
	public void setTargetConnectionType(String targetConnectionType) {
		this.targetConnectionType = targetConnectionType;
	}

	/**
	 * @return the targetConnectionMode
	 */
	public String getTargetConnectionMode() {
		return targetConnectionMode;
	}

	/**
	 * @param targetConnectionMode the targetConnectionMode to set
	 */
	public void setTargetConnectionMode(String targetConnectionMode) {
		this.targetConnectionMode = targetConnectionMode;
	}

	/**
	 * @return the targetPort
	 */
	public String getTargetPort() {
		return targetPort;
	}

	/**
	 * @param targetPort the targetPort to set
	 */
	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}

	/**
	 * @return the targetUserName
	 */
	public String getTargetUserName() {
		return targetUserName;
	}

	/**
	 * @param targetUserName the targetUserName to set
	 */
	public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}

	/**
	 * @return the targetPassword
	 */
	public String getTargetPassword() {
		return targetPassword;
	}

	/**
	 * @param targetPassword the targetPassword to set
	 */
	public void setTargetPassword(String targetPassword) {
		this.targetPassword = targetPassword;
	}

	/**
	 * @return the targetOperation
	 */
	public String getTargetOperation() {
		return targetOperation;
	}

	/**
	 * @param targetOperation the targetOperation to set
	 */
	public void setTargetOperation(String targetOperation) {
		this.targetOperation = targetOperation;
	}

	/**
	 * @return the targetUserInstallRoot
	 */
	public String getTargetUserInstallRoot() {
		return targetUserInstallRoot;
	}

	/**
	 * @param targetUserInstallRoot the targetUserInstallRoot to set
	 */
	public void setTargetUserInstallRoot(String targetUserInstallRoot) {
		this.targetUserInstallRoot = targetUserInstallRoot;
	}

	/**
	 * @return the targetWasRespositoryRoot
	 */
	public String getTargetWasRespositoryRoot() {
		return targetWasRespositoryRoot;
	}

	/**
	 * @param targetWasRespositoryRoot the targetWasRespositoryRoot to set
	 */
	public void setTargetWasRespositoryRoot(String targetWasRespositoryRoot) {
		this.targetWasRespositoryRoot = targetWasRespositoryRoot;
	}

	/**
	 * @return the targetEnvironmentProperties
	 */
	public String getTargetEnvironmentProperties() {
		return targetEnvironmentProperties;
	}

	/**
	 * @param targetEnvironmentProperties the targetEnvironmentProperties to set
	 */
	public void setTargetEnvironmentProperties(String targetEnvironmentProperties) {
		this.targetEnvironmentProperties = targetEnvironmentProperties;
	}

	/**
	 * @return the mApplicationName
	 */
	public String getApplicationName() {
		return mApplicationName;
	}

	/**
	 * @param applicationName the mApplicationName to set
	 */
	public void setApplicationName(String applicationName) {
		mApplicationName = applicationName;
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
	public boolean isIncludeAllChildren() {
		return mIncludeAllChildren;
	}

	/**
	 * @param includeAllChildren the mIncludeAllChildren to set
	 */
	public void setIncludeAllChildren(boolean includeAllChildren) {
		mIncludeAllChildren = includeAllChildren;
	}

	public DeployInfo getSourcePopluatedDeployInfo(){
		
		DeployInfo deployInfo = new DeployInfo();
		deployInfo.setApplicationName(mApplicationName);
		deployInfo.setHost(sourceHost);
		deployInfo.setUserName(sourceUserName);
		deployInfo.setPassword(sourcePassword);
		deployInfo.setPort(sourcePort);
		deployInfo.setConnectionMode(sourceConnectionMode);
		deployInfo.setConnectionType(sourceConnectionType);
		//deployInfo.setResourceXML(mResourceXML);
		deployInfo.setResourceXML(mResourceXML);
		
		deployInfo.setEnvironmentProperties(sourceEnvironmentProperties);
		deployInfo.setUserInstallRoot(sourceUserInstallRoot);
		deployInfo.setWasRespositoryRoot(sourceWasRespositoryRoot);

		deployInfo.setSyncResourceXML(workingArea + "/" + sourceHost + ".xml" );
		deployInfo.setSyncReportLocation(workingArea + "/" + sourceHost + ".html");
		deployInfo.setIncludeAllChildren(mIncludeAllChildren);
		deployInfo.setOperationMode(mOperationMode);
		deployInfo.setSASSecurityConfig(SASSecurityConfig);
		deployInfo.setSOAPSecurityConfig(SOAPSecurityConfig);
		deployInfo.setSSLSecurityConfig(SSLSecurityConfig);
		deployInfo.setJAACSecurityConfig(JAACSecurityConfig);
		return deployInfo;

		
	}

	public DeployInfo getTargetPopluatedDeployInfo(){
		
		DeployInfo deployInfo = new DeployInfo();
		deployInfo.setApplicationName(mApplicationName);
		deployInfo.setHost(targetHost);
		deployInfo.setUserName(targetUserName);
		deployInfo.setPassword(targetPassword);
		deployInfo.setPort(targetPort);
		deployInfo.setConnectionMode(targetConnectionMode);
		deployInfo.setConnectionType(targetConnectionType);
		deployInfo.setEnvironmentProperties(targetEnvironmentProperties);
		deployInfo.setUserInstallRoot(targetUserInstallRoot);
		deployInfo.setWasRespositoryRoot(targetWasRespositoryRoot);


		deployInfo.setResourceXML(workingArea + "/" + sourceHost + ".xml" );
		deployInfo.setSyncResourceXML(mSyncResourceXML);
		deployInfo.setSyncReportLocation(mSyncReportLocation);
		deployInfo.setIncludeAllChildren(mIncludeAllChildren);
		deployInfo.setOperationMode(mOperationMode);
		deployInfo.setSASSecurityConfig(SASSecurityConfig);
		deployInfo.setSOAPSecurityConfig(SOAPSecurityConfig);
		deployInfo.setSSLSecurityConfig(SSLSecurityConfig);
		deployInfo.setJAACSecurityConfig(JAACSecurityConfig);

		return deployInfo;

		
	}

	/**
	 * @return the workingArea
	 */
	public String getWorkingArea() {
		return workingArea;
	}

	/**
	 * @param workingArea the workingArea to set
	 */
	public void setWorkingArea(String workingArea) {
		this.workingArea = workingArea;
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


}
