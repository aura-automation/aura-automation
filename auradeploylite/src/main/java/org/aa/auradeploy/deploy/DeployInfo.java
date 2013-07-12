package org.aa.auradeploy.deploy;

import java.io.File;
import java.util.Properties;

/**
 * @author Jatin
 *
 * Copyright (C) 

 */
public class DeployInfo extends org.aa.common.deploy.DeployInfo{
	
	
	private String mContextRoot;
	
	private File mWorkArea ;
	
	private boolean defaultValues;
	
	private boolean releaseMI;
	
	private String EARExportLocation;

	private boolean nowait;
	
	private Properties properties;

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the eARExportLocation
	 */
	public String getEARExportLocation() {
		return EARExportLocation;
	}

	/**
	 * @param eARExportLocation the eARExportLocation to set
	 */
	public void setEARExportLocation(String eARExportLocation) {
		EARExportLocation = eARExportLocation;
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

	public boolean isNowait() {
		return nowait;
	}

	public void setNowait(boolean nowait) {
		this.nowait = nowait;
	}

	
}


