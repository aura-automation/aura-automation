/**	   Copyright 


**/
package org.aa.auraconfig.ant;

import java.io.InputStream;

import org.aa.auraconfig.ant.AntValidator;
import org.aa.auraconfig.ant.base.BaseAntXML;
import org.aa.auraconfig.resources.creator.ResourceCreator;
import org.aa.auraconfig.resources.xmlcompartor.ResourceXMLOnlyComparator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.aa.common.Constants.LicenseConstants;
import org.aa.common.ant.AntTask;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;

public class AntXML extends BaseAntXML{

	public void execute()
		throws BuildException{
		try{	
		//	LicenseInfo licenseInfo  = ExpiryCheck.checkIfExpired(LicenseConstants.CONFIG_LITE);
		//	if (licenseInfo.isValid()){

		
				DeployInfo deployInfo1 = getPopluatedDeployInfo1();
				DeployInfo deployInfo2 = getPopluatedDeployInfo2();
		//		deployInfo1.setLicenseInfo(licenseInfo);
		//		deployInfo2.setLicenseInfo(licenseInfo);
				
				ResourceCreator resourceCreator = new ResourceCreator();
		//		String resourceXMLMetaData = Thread.currentThread().getContextClassLoader().getResource("/resource-metadata.xml").getFile();
		//		String referencedResourceXML = Thread.currentThread().getContextClassLoader().getResource("/Reference-ResourceObjects.xml").getFile();
		
				InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
				InputStream referencedResourceXML =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
				
				if (resourceXMLMetaDataInputStream==null){
					SDLog.log("Error: Data missing");
				}
				if (referencedResourceXML == null){
					SDLog.log("Error: Reference file missing");
				}
				
			//	AntValidator validator = new AntValidator();
				ResourceXMLOnlyComparator resourceXMLOnlyComparator = new ResourceXMLOnlyComparator();
				resourceXMLOnlyComparator.compare(deployInfo1, deployInfo2);
		//	}
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

}
