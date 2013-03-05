/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.auraconfig.ant;

import java.io.InputStream;
import java.util.List;

import org.apache.tools.ant.BuildException;

import com.apartech.auraconfig.resources.Resource;
import com.apartech.auraconfig.resources.ResourceCreator;
import com.apartech.common.Constants.LicenseConstants;
import com.apartech.common.ant.AntTask;
import com.apartech.common.deploy.DeployInfo;
import com.apartech.common.exception.DeployException;
import com.apartech.common.log.SDLog;

public class AntResources extends AntTask{
	
	public void execute()
		throws BuildException{
		try{	
	//		LicenseInfo licenseInfo  = ExpiryCheck.checkIfExpired(LicenseConstants.CONFIG_LITE);
	//		if (licenseInfo.isValid()){
	
				
					DeployInfo deployInfo = getPopluatedDeployInfo();
	//				deployInfo.setLicenseInfo(licenseInfo);
					ResourceCreator resourceCreator = new ResourceCreator();
		//			String resourceXMLMetaData = Thread.currentThread().getContextClassLoader().getResource("/resource-metadata.xml").getFile();
		//			String referencedResourceXML = Thread.currentThread().getContextClassLoader().getResource("/Reference-ResourceObjects.xml").getFile();
		
					InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
					InputStream referencedResourceXML =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
					
					if (resourceXMLMetaDataInputStream==null){
						SDLog.log("Error: DAKS Data missing");
					}
					if (referencedResourceXML == null){
						SDLog.log("Error: DAKS Reference file missing");
					}
					
					AntValidator validator = new AntValidator();
					if (!validator.validate(deployInfo)){
						throw new DeployException(new Exception("Data Not Valid"));
					}
		
					Resource resources = resourceCreator.start(deployInfo.getResourceXML(),deployInfo.getRulesXML(), referencedResourceXML, resourceXMLMetaDataInputStream,deployInfo);
					List <String> missingAttributeList= resources.getMissingAttributeList();
					
					if (resources.isHasAnyChange()){
						getProject().setProperty("hasAnyChange", "true");
					}else{
						getProject().setProperty("hasAnyChange", "false");
					}
					
					for(int i=0;i<missingAttributeList.size();i++)
					{
						System.out.println("Attribute:"+missingAttributeList.get(i).toString());
					}
					
					
					if (missingAttributeList.size()>0){
						throw new BuildException(new Exception("Above Variables are missing"));
						
					}
					
				//	resourceCreator.startGettingValidChildren(deployInfo.getResourceXML(), referencedResourceXML, resourceXMLMetaDataInputStream,deployInfo);
	//		}			
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
		}

	}



}
