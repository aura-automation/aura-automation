/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.ant.base;


import org.apache.tools.ant.Task;

import org.aa.common.deploy.DeployInfo;

public class BaseAntXML extends Task{
	
	private String mOperationMode;

	private String mResourceXML1;
	
	private String mResourceXML2;

	private String mSyncResourceXML;
	
	private String environmentProperties1;

	private String environmentProperties2;

	private String mSyncReportLocation;
	
	private boolean mIncludeAllChildren = true;

	private String mRulesXML;

	
	/**
	 * @return the resourceXML
	 */
	public String getResourceXML1() {
		return mResourceXML1;
	}


	/**
	 * @param resourceXML the resourceXML to set
	 */
	public void setResourceXML2(String resourceXML) {
		this.mResourceXML2 = resourceXML;
	}

	public String getResourceXML2() {
		return mResourceXML2;
	}


	/**
	 * @param resourceXML the resourceXML to set
	 */
	public void setResourceXML1(String resourceXML) {
		this.mResourceXML1 = resourceXML;
	}
	
	public DeployInfo getPopluatedDeployInfo1(){
		
		DeployInfo deployInfo = new DeployInfo();
		deployInfo.setOperationMode(mOperationMode);


		deployInfo.setRulesXML(mRulesXML);

		deployInfo.setSyncResourceXML(mSyncResourceXML);

		deployInfo.setResourceXML(mResourceXML1);
		deployInfo.setEnvironmentProperties(environmentProperties1);
		deployInfo.setSyncReportLocation(mSyncReportLocation);
		deployInfo.setIncludeAllChildren(mIncludeAllChildren);
		
		return deployInfo;

		
	}
	
public DeployInfo getPopluatedDeployInfo2(){
		
		DeployInfo deployInfo = new DeployInfo();
		deployInfo.setOperationMode(mOperationMode);


		deployInfo.setRulesXML(mRulesXML);

		deployInfo.setResourceXML(mResourceXML2);
		deployInfo.setEnvironmentProperties(environmentProperties2);
		deployInfo.setSyncReportLocation(mSyncReportLocation);

		deployInfo.setIncludeAllChildren(mIncludeAllChildren);
		deployInfo.setSyncResourceXML(mSyncResourceXML);
		
		return deployInfo;

		
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
	 * @return the environmentProperties1
	 */
	public String getEnvironmentProperties1() {
		return environmentProperties1;
	}


	/**
	 * @param environmentProperties1 the environmentProperties1 to set
	 */
	public void setEnvironmentProperties1(String environmentProperties1) {
		this.environmentProperties1 = environmentProperties1;
	}


	/**
	 * @return the environmentProperties2
	 */
	public String getEnvironmentProperties2() {
		return environmentProperties2;
	}


	/**
	 * @param environmentProperties2 the environmentProperties2 to set
	 */
	public void setEnvironmentProperties2(String environmentProperties2) {
		this.environmentProperties2 = environmentProperties2;
	}

}


