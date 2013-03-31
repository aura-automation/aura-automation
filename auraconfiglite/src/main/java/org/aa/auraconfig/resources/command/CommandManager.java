/**	   Copyright 


**/
package org.aa.auraconfig.resources.command;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.ObjectName;

import org.aa.auraconfig.resources.DiffAttribute;
import org.aa.auraconfig.resources.Resource;
import org.aa.auraconfig.resources.ResourceConstants;
import org.aa.auraconfig.resources.ResourceDiffReportHelper;
import org.aa.auraconfig.resources.ResourceHelper;
import org.aa.auraconfig.resources.metadata.CommandAttribute;
import org.aa.auraconfig.resources.metadata.CommandLinkAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.jsp.core.internal.Logger;

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.command.Command;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.cmdframework.AdminCommand;
import com.ibm.websphere.management.cmdframework.CommandException;
import com.ibm.websphere.management.cmdframework.CommandMgr;
import com.ibm.websphere.management.cmdframework.CommandMgrInitException;
import com.ibm.websphere.management.cmdframework.CommandNotFoundException;
import com.ibm.websphere.management.cmdframework.CommandResult;
import com.ibm.websphere.management.cmdframework.CommandStep;
import com.ibm.websphere.management.cmdframework.TaskCommand;
import com.ibm.websphere.management.cmdframework.commandmetadata.CommandMetadata;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class CommandManager {
	private static final Log logger  = LogFactory.getLog(CommandManager.class);	

	public void processCommand(AdminClient adminClient, Session session )
		throws CommandMgrInitException,CommandException,CommandNotFoundException,ConnectorException{
		
	//	showResource(adminClient, session);
		
	//	createResource(adminClient, session);
		
	}
	

	public ObjectName createResource(Resource resource, AdminClient adminClient, Session session )
		throws CommandMgrInitException,CommandException,CommandNotFoundException,ConnectorException{
		
		CommandMgr cmdMgr = CommandMgr.getClientCommandMgr(adminClient);

		
		//AdminCommand command = cmdMgr.createCommand("createCluster");
		
		//AdminCommand command = cmdMgr.createCommand("createSIBDestination");
		logger.trace("Command that will run " + resource.getResourceMetaData().getCommandMetaData().getCreateCommand());
		AdminCommand  command = cmdMgr.createCommand(resource.getResourceMetaData().getCommandMetaData().getCreateCommand());
		command.setConfigSession(session);

		HashMap<String, String> attributeListFromXML = resource.getAttributeList();
		HashMap<String, String> attributeListFromXMLForStep = null;
		HashMap<String, CommandAttribute> attributeMappingsMetaDataForStep =  null;

		
		if (resource.getResourceMetaData().getCommandMetaData().getStepCommandMetaData()!=null ){
			
			TaskCommand taskCommand = (TaskCommand)command;
			logger.trace( " taskCommand.listCommandSteps " + taskCommand.listCommandSteps().length+ " " + taskCommand.listCommandSteps().toString());
			for (int k = 0 ; k < taskCommand.listCommandSteps().length ; k++ ){
				logger.trace( " taskCommand.listCommandSteps -" + k + " " + taskCommand.listCommandSteps()[k]);
			}
	//		while(taskCommand.hasNextStep()){
	//			logger.trace(" Steps are " + taskCommand.nextStep().getName());
	//		}
			logger.trace(" getting memberConfig from " + taskCommand.getName() );
			CommandStep step = taskCommand.getCommandStep("memberConfig");
			//CommandStep step = taskCommand.gotoStep(resource.getResourceMetaData().getCommandMetaData().getStepCommandMetaData().getStepName());
			attributeListFromXMLForStep = resource.getAttributeList();
			attributeMappingsMetaDataForStep =  resource.getResourceMetaData().getCommandMetaData().getStepCommandMetaData().getAttributeMappings();

			String[] attributeListFromXMLKeysForStep =  (String[])attributeListFromXML.keySet().toArray(new String[0]);
			
			for (int i=0 ; i < attributeListFromXMLKeysForStep.length ; i++){
				logger.trace("		Processing attribute from XML for Step " +  attributeListFromXMLKeysForStep[i]);
				if (attributeMappingsMetaDataForStep.get(attributeListFromXMLKeysForStep[i]) != null){
					logger.trace("		attributeMappingsMetaData is not null"  );
					CommandAttribute  commandAttribute  = attributeMappingsMetaDataForStep.get(attributeListFromXMLKeysForStep[i]);
					String type = commandAttribute.getType();
					if (type.equalsIgnoreCase(ResourceConstants.COMMAND_MAPPING)){
						logger.trace("	Attribute mapping COMMAND_MAPPING for Step " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
						step.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
						
					}else if  (type.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL)){
						logger.trace("	Attribute mapping COMMAND_ADDITIONAL for Step " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
						step.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
					}
				}else{
					logger.trace("		attributeMappingsMetaData is null"  );
					logger.trace("	Attribute mapping for Step " + attributeListFromXMLKeysForStep[i] + " - " + attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
					step.setParameter(attributeListFromXMLKeysForStep[i],attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
				}
			}

		
		}

		

		HashMap<String, CommandAttribute> attributeMappingsMetaData =  resource.getResourceMetaData().getCommandMetaData().getAttributeMappings();
		String[] attributeListFromXMLKeys =  (String[])attributeListFromXML.keySet().toArray(new String[0]);
		
		for (int i=0 ; i < attributeListFromXMLKeys.length ; i++){
			logger.trace("		Processing attribute from XML " +  attributeListFromXMLKeys[i]);
			/**
			 * Check so that attribute that we are processing in not step/task attribute, if it is do not add it to 
			 * Command as it is already added to Step, plus it will fail if added to command   
			 */
			if ((attributeMappingsMetaDataForStep == null) || attributeMappingsMetaDataForStep.get(attributeListFromXMLKeys[i]) == null){ 
				if (attributeMappingsMetaData.get(attributeListFromXMLKeys[i]) != null){
					CommandAttribute  commandAttribute  = attributeMappingsMetaData.get(attributeListFromXMLKeys[i]);
					String type = commandAttribute.getType();
					logger.trace("		attributeMappingsMetaData for command is not null of type " + type   );

					if (type.equalsIgnoreCase(ResourceConstants.COMMAND_MAPPING)){
						logger.trace("	Attribute mapping COMMAND_MAPPING " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXML.get(attributeListFromXMLKeys[i]));
						command.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXML.get(attributeListFromXMLKeys[i]));
						
					}else if  (type.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL)){
						logger.trace("	Attribute mapping COMMAND_ADDITIONAL " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXML.get(attributeListFromXMLKeys[i]));
						command.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXML.get(attributeListFromXMLKeys[i]));
					}	
				}else{
					logger.trace("		attributeMappingsMetaData is null"  );
					logger.trace("	Attribute mapping " + attributeListFromXMLKeys[i] + " - " + attributeListFromXML.get(attributeListFromXMLKeys[i]));
					command.setParameter(attributeListFromXMLKeys[i],attributeListFromXML.get(attributeListFromXMLKeys[i]));
				}
			}
		}
		
		/**
		 * Now process the attriutes that will not be in the resource XMLs. Like contants and mappings
		 */
		String[] attributeMappingsMetaDataKeys =  (String[])attributeMappingsMetaData.keySet().toArray(new String[0]);
		
		for (int i=0 ; i < attributeMappingsMetaDataKeys.length; i++){
			CommandAttribute  commandAttribute  = attributeMappingsMetaData.get(attributeMappingsMetaDataKeys[i]);
			String type = commandAttribute.getType();

			if (type.equalsIgnoreCase(ResourceConstants.COMMAND_PARENT)){
				logger.trace("	Attribute mapping " + commandAttribute.getCommandAttribute()  + " - " + ResourceHelper.getResourceIdentifierName(resource.getParent()));
				command.setParameter(commandAttribute.getCommandAttribute() , 
						ResourceHelper.getResourceIdentifierName(resource.getParent()));
			}else if  (type.equalsIgnoreCase(ResourceConstants.COMMAND_CONSTANT)){
				logger.trace("	Attribute mapping COMMAND_CONSTANT  " + commandAttribute.getCommandAttribute()  + " - " + commandAttribute.getConstantValue());
				command.setParameter(
						commandAttribute.getCommandAttribute() ,
						commandAttribute.getConstantValue());
			}	
			
		}

		
		
		//	command.setParameter("bus", ResourceHelper.getResourceIdentifierName(resource.getParent()) );
	//	command.setParameter("type","QUEUE" );
//		command.setParameter("name", resource.getAttributeList().get("identifier"));
//		command.setParameter("cluster", resource.getAttributeList().get("cluster"));


//		AdminCommand command = cmdMgr.createCommand("showSIBus");
//		command.setParameter("bus","SIBus" );



//		TaskCommand taskCommand = (TaskCommand) command;
//		CommandStep step = taskCommand.getCommandStep("clusterConfig");
		
//		step.setParameter("clusterName", "cluster1");
//		SDLog.log("clusterName" +  step.getParameter("clusterName").toString());

//		step.setParameter("preferLocal", "true" );
		
//		SDLog.log( step.getParameter("clusterName").toString());
		CommandResult result = null;
		try{
			command.execute();
			result =  command.getCommandResult();
			SDLog.log("Result is " + result.isSuccessful() );
			if (!result.isSuccessful()){
				
				logger.trace("Looping throw results")	;
				ArrayList results = ((ArrayList)command.getResult());
				for (int i=0; i < results.size();i++ ){
					((Exception)results.get(i)).printStackTrace();
				}

			}
			logger.trace( (ObjectName)result.getResult());
			return (ObjectName)result.getResult();
	//		ArrayList results = ((ArrayList)result.getResult());
	//		for (int i=0; i < results.size();i++ ){
	//			logger.trace( " i " + (String)results.get(i));
	//		}

	//		SDLog.log(result.getMessages().toString());
	//		return null;
		}catch(Exception e){
			e.printStackTrace();
			if (!result.isSuccessful()){
				SDLog.log(result.getMessages().toString() );
			} else{
				ArrayList results = ((ArrayList)result.getResult());
				for (int i=0; i < results.size();i++ ){
					logger.trace( " i " + (String)results.get(i));
				}
			}
		}

		return null;
	}

	public DiffAttribute modifyResourceLinkAttriute(Resource resource,String attribute, CommandLinkAttribute commandLinkAttribute,
			AdminClient adminClient, Session session, ConfigService configService )
			throws ConfigServiceException,ConnectorException,AttributeNotFoundException{
		logger.trace(">> ");
		DiffAttribute diffAttribute  = null;
		AttributeList newAttrList = new AttributeList();
		logger.trace("Will attempt to modify resource " + resource.getContainmentPath() + "attribute " + attribute);

		if (commandLinkAttribute.getTargetObjectRelation().equalsIgnoreCase("child")){
			logger.trace("Command Link is child type " );
			logger.trace("Getting value of attribute " + commandLinkAttribute.getTargetAttribute());
			AttributeList targetObject = (AttributeList) configService.getAttribute(session, resource.getConfigId() , commandLinkAttribute.getTargetAttribute());
			String  targetObjectAttributeValue = null;
			logger.trace("Getting value of attribute " + commandLinkAttribute.getTargetObjectAttribute() );

			if (ConfigServiceHelper.getAttributeValue(targetObject, commandLinkAttribute.getTargetObjectAttribute())!=null){
				targetObjectAttributeValue =  ConfigServiceHelper.getAttributeValue(targetObject, commandLinkAttribute.getTargetObjectAttribute()).toString();
			}
		//	String  targetObjectAttributeValue = (String)configService.getAttribute(session, targetObject , commandLinkAttribute.getTargetObjectAttribute());
			ObjectName attrObjectName = ConfigServiceHelper.createObjectName((AttributeList)targetObject);

			String  resourceAttributeValue = resource.getAttributeList().get(attribute).toString();
			if (!resourceAttributeValue.equalsIgnoreCase(targetObjectAttributeValue)){
				logger.trace("Will modify xml value is " + resourceAttributeValue + " config value is " + targetObjectAttributeValue );
				newAttrList.add(new Attribute(attribute,resourceAttributeValue));
				configService.setAttributes(session,attrObjectName, newAttrList);
				ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
				diffAttribute = resourceDiffReportHelper.getDiffAttribute(attribute, resourceAttributeValue, targetObjectAttributeValue);

			}else{
				logger.trace("Will not modify as values are same " );

			}
		}else{
			logger.trace("Command Link is not child type " );
		}
		logger.trace("<< ");
		return diffAttribute;
	}


	public void getAllResourceUsingCommand(Resource resource,
				AdminClient adminClient, Session session){
		
		
		
	}
}
