package com.apartech.auradeploy.deploy;

import java.io.File;

/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
public class DeployInfo extends com.apartech.common.deploy.DeployInfo{
	
	
	private String mContextRoot;
	
	private File mWorkArea ;
	
	private boolean defaultValues;
	
	private boolean releaseMI;
	
	private String EARExportLocation;

	private boolean nowait;
	

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


