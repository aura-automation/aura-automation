/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.auradeploy.ant;

import org.aa.auradeploy.deploy.DeployInfo;
import org.apache.tools.ant.BuildException;




public class AntApplicationTask extends AntTask {
	
	private String mEARFileLocation;

	private boolean misMultiEAR = false;

	private String mMultiEARLocation;

	private String mEARExportLocation;
	
	/**
	 * @return the mEARBackUpLocation
	 */
	public String getEARExportLocation() {
		return mEARExportLocation;
	}


	/**
	 * @param mEARBackUpLocation the mEARBackUpLocation to set
	 */
	public void setEARExportLocation(String mEARExportLocation) {
		this.mEARExportLocation = mEARExportLocation;
	}


	public DeployInfo getPopluatedDeployInfo() throws BuildException {
		DeployInfo  deployInfo = super.getPopluatedDeployInfo();
		deployInfo.setEARFileLocation(mEARFileLocation );
		deployInfo.setisMultiEAR(misMultiEAR);
		deployInfo.setMultiEARLocation(mMultiEARLocation);
		deployInfo.setEARExportLocation(mEARExportLocation);
		
		return deployInfo;
	}


	/**
	 * Returns the mEARFileLocation.
	 * @return String
	 */
	public String getEARFileLocation() {
		return mEARFileLocation;
	}


	/**
	 * Sets the mEARFileLocation.
	 * @param mEARFileLocation The mEARFileLocation to set
	 */
	public void setEARFileLocation(String mEARFileLocation) {
		this.mEARFileLocation = mEARFileLocation;
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

	

}
