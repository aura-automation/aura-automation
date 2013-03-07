/*
 * Created on 23-Jun-2005
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
package org.aa.auradeploy.ant;

import java.io.IOException;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.deploy.DeployInfo;
import org.aa.auradeploy.deploy.EARProcessor;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.SAXException;

import org.aa.common.Constants.LicenseConstants;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.application.client.AppDeploymentException;
/**
 * @author Jatin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AntProcessEAR extends Task	
{

	private String mEARFileLocation;
	
	private String mApplicationName;

	private String mDeployDataLocation;

	private boolean misMultiEAR;

	private String mMultiEARLocation;

	private String mMultiEARDeployData;

	private String mVirtualHost;
	
	private boolean defaultValues = false;
	
	
	/**
	 * Returns the mApplicationName.
	 * @return String
	 */
	public String getApplicationName() {
		return mApplicationName;
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
	
	public void execute()
		throws BuildException{
		try{	
				DeployInfo deployInfo = new DeployInfo();
				SDLog.log("Aura Deploy Lite Version "+DeployValues.VERSION+", Centauros-Solutions Ltd, www.centauros-solutions.co.uk." );
		    	SDLog.log("Copyright Apartech Ltd ");
				SDLog.log("" );
		
				
				deployInfo.setApplicationName(mApplicationName);
				deployInfo.setDeployDataLocation(mDeployDataLocation);
				deployInfo.setEARFileLocation(mEARFileLocation );
				deployInfo.setisMultiEAR(misMultiEAR);
				deployInfo.setMultiEARDeployData(mMultiEARDeployData);
				deployInfo.setMultiEARLocation(mMultiEARLocation);
				deployInfo.setVirtualHost(mVirtualHost);
				deployInfo.setDefaultValues(defaultValues);
				
			
				AntValidator antValidator  = new AntValidator();
				boolean valid=  antValidator.validateForEARProcessor(deployInfo);
				if (!valid){
					throw new BuildException("Data not valid");
				}
	
				EARProcessor earProcessor = new EARProcessor();
				earProcessor.startwork(deployInfo);
		}catch(DeployException e){
			e.printStackTrace();
			throw new BuildException(e);
		}catch(IOException e){
			e.printStackTrace();
			throw new BuildException(e);
		}catch(SAXException e){
			e.printStackTrace();
			throw new BuildException(e);
		}catch(AppDeploymentException e){
			e.printStackTrace();
			throw new BuildException(e);
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
		}
		
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
