/**	   Copyright 


**/
package org.aa.auraconfig.ant;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.aa.auraconfig.ant.base.AntResourceCompareTask;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceCreator;
import org.apache.tools.ant.BuildException;

import org.aa.common.Constants.LicenseConstants;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;

public class AntResourcesCompare extends AntResourceCompareTask{

	public void execute()
		throws BuildException{
		try{	
		//	LicenseInfo licenseInfo  = ExpiryCheck.checkIfExpired(LicenseConstants.CONFIG_LITE);
		//	if (licenseInfo.isValid()){

			
				ResourceCreator resourceCreator = new ResourceCreator();
				ResourceCreator targetResourceCreator = new ResourceCreator();
		//		String resourceXMLMetaData = Thread.currentThread().getContextClassLoader().getResource("/resource-metadata.xml").getFile();
		//		String referencedResourceXML = Thread.currentThread().getContextClassLoader().getResource("/Reference-ResourceObjects.xml").getFile();
		
				InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
				InputStream referencedResourceXML =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
				
				if (resourceXMLMetaDataInputStream==null){
					SDLog.log("Error: AuraConfig Data missing");
				}
				if (referencedResourceXML == null){
					SDLog.log("Error: AuraConfig Reference file missing");
				}
				
				DeployInfo sourceDeployInfo = getSourcePopluatedDeployInfo();
				Resource sourceResources = resourceCreator.start(sourceDeployInfo.getResourceXML(), null,referencedResourceXML, resourceXMLMetaDataInputStream,sourceDeployInfo);
				List <String> missingAttributeList= sourceResources.getMissingAttributeList();
	
				for(int i=0;i<missingAttributeList.size();i++)
				{
					System.out.println("Attribute:"+missingAttributeList.get(i).toString());
				}
				
				
				if (missingAttributeList.size()>0){
					throw new BuildException(new Exception("Above Variables are missing in " +  sourceDeployInfo.getEnvironmentProperties()));
					
				}
				InputStream referencedResourceXMLForTarget =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
				
				DeployInfo targetDeployInfo = getTargetPopluatedDeployInfo();
				targetDeployInfo.setSourceDeployInfo(sourceDeployInfo);
				Resource targetResources = targetResourceCreator.start(targetDeployInfo.getResourceXML(),null ,referencedResourceXMLForTarget, resourceXMLMetaDataInputStream,targetDeployInfo);
				
				List <String> targetMissingAttributeList= sourceResources.getMissingAttributeList();
	
				for(int i=0;i<targetMissingAttributeList.size();i++)
				{
					System.out.println("Attribute:"+targetMissingAttributeList.get(i).toString());
				}
				
				
				if (targetMissingAttributeList.size()>0){
					throw new BuildException(new Exception("Above Variables are missing in " +  targetDeployInfo.getEnvironmentProperties()));
					
				}
			
		//	}
		}catch(Exception e){
			e.printStackTrace();
			throw new BuildException(e);
		}
	}


}
