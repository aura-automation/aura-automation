/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.ant;

import org.aa.aura.ddcreator.DDCreator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.aa.common.Constants.DeployValues;
import org.aa.common.Constants.LicenseConstants;
import org.aa.common.deploy.DeployInfo;

public class AntDDCreator extends Task{
	private String earFile;
	
	private String deployDataFile;
	
	private String environmentProperties;
	
	@Override
	public void execute() throws BuildException {
		// TODO Auto-generated method stub
		try{
		//	LicenseInfo licenseInfo  = ExpiryCheck.checkIfExpired(LicenseConstants.CONFIG_LITE);
		//	if (licenseInfo.isValid()){
				super.execute();
				
				DeployInfo deployInfo = new DeployInfo();
		//		deployInfo.setLicenseInfo(licenseInfo);
				deployInfo.setOperationMode(DeployValues.OPERATION_MODE_SYNC);
				deployInfo.setEnvironmentProperties(environmentProperties);
				deployInfo.setSyncResourceXML(deployDataFile + ".xml");
				deployInfo.setSyncReportLocation(deployDataFile + ".html");
				
					DDCreator ddCreator  = new DDCreator();
					ddCreator.createDD(deployInfo , earFile);
			
		//	}
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

	/**
	 * @return the earFile
	 */
	public String getEarFile() {
		return earFile;
	}

	/**
	 * @param earFile the earFile to set
	 */
	public void setEarFile(String earFile) {
		this.earFile = earFile;
	}

	/**
	 * @return the deployDataFile
	 */
	public String getDeployDataFile() {
		return deployDataFile;
	}

	/**
	 * @param deployDataFile the deployDataFile to set
	 */
	public void setDeployDataFile(String deployDataFile) {
		this.deployDataFile = deployDataFile;
	}

	/**
	 * @return the environmentProperties
	 */
	public String getEnvironmentProperties() {
		return environmentProperties;
	}

	/**
	 * @param environmentProperties the environmentProperties to set
	 */
	public void setEnvironmentProperties(String environmentProperties) {
		this.environmentProperties = environmentProperties;
	}

}
