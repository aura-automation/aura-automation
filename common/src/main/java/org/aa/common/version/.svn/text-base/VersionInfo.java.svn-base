/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.common.version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class VersionInfo {
	private static final Log logger = LogFactory.getLog(VersionInfo.class);
	
	private String versionNumber;
	
	private int majorNumber;
	
	private int minorNumber;
	
	/**
	 * @return the majorNumber
	 */
	public int getMajorNumber() {
		return majorNumber;
	}

	/**
	 * @param majorNumber the majorNumber to set
	 */
	public void setMajorNumber(int majorNumber) {
		this.majorNumber = majorNumber;
	}

	/**
	 * @return the minorNumber
	 */
	public int getMinorNumber() {
		return minorNumber;
	}

	/**
	 * @param minorNumber the minorNumber to set
	 */
	public void setMinorNumber(int minorNumber) {
		this.minorNumber = minorNumber;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @param versionNumber the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
		parseVersionNumber();
	}
	
	private void parseVersionNumber(){
		if (versionNumber == null){
			majorNumber = 1;
			minorNumber = 0;
		}else{
			majorNumber =  new Integer(versionNumber.substring(0, versionNumber.indexOf('.'))).intValue();
			logger.trace("Major Version number is " + majorNumber);
			versionNumber = versionNumber.substring(versionNumber.indexOf('.')+1);
			minorNumber =  new Integer(versionNumber).intValue();
			logger.trace("Minor Version number is " + minorNumber);
		}
	}
	
	public static void main(String[] args) {
		String versionNumber = "2.0";
		VersionInfo versionInfo = new VersionInfo();
		versionInfo.setVersionNumber(versionNumber);
	
		 
	}

}
