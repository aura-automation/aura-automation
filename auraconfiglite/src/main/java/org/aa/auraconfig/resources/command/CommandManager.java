/**	   Copyright 


**/
package org.aa.auraconfig.resources.command;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

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

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.eclipse.emf.common.util.EList;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.cmdframework.AdminCommand;
import com.ibm.websphere.management.cmdframework.CommandException;
import com.ibm.websphere.management.cmdframework.CommandMgr;
import com.ibm.websphere.management.cmdframework.CommandMgrInitException;
import com.ibm.websphere.management.cmdframework.CommandNotFoundException;
import com.ibm.websphere.management.cmdframework.CommandResult;
import com.ibm.websphere.management.cmdframework.CommandStep;
import com.ibm.websphere.management.cmdframework.InvalidParameterNameException;
import com.ibm.websphere.management.cmdframework.InvalidParameterValueException;
import com.ibm.websphere.management.cmdframework.TaskCommand;
import com.ibm.websphere.management.cmdframework.commandmetadata.CommandMetadata;
import com.ibm.websphere.management.cmdframework.commandmetadata.ParameterMetadata;
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
	
	
	private void displayCommandMetadata(CommandMgr cmdMgr, String command) throws CommandException,ConnectorException{
		CommandMetadata commandMetadata = cmdMgr.getCommandMetadata(command);
		System.out.println(" ****************"  + commandMetadata.getName());
		EList elist = commandMetadata.getParameters();
		
		Iterator<ParameterMetadata> elistIt =  elist.iterator();
		while (elistIt.hasNext()){
			ParameterMetadata parameterMetadata = (ParameterMetadata)elistIt.next();
			System.out.println(" **************** getName "  + parameterMetadata.getName());
			System.out.println(" 	**************** getType "  + parameterMetadata.getType());
			System.out.println(" 	**************** getDefaultValue "  + parameterMetadata.getDefaultValue());
			System.out.println(" 	**************** getCustom "  + parameterMetadata.getCustom());
			System.out.println(" 	**************** isConfigAttribute "  + parameterMetadata.isConfigAttribute());
			System.out.println(" 	**************** isKeyField "  + parameterMetadata.isKeyField() );
			System.out.println(" 	**************** isRequired "  + parameterMetadata.isRequired()  );
			System.out.println(" 	**************** isSetDefaultValue "  + parameterMetadata.isSetDefaultValue() );
			System.out.println(" 	**************** isSetReadonly "  + parameterMetadata.isSetReadonly());
			 
		}
	}

	private String getParamaterType(AdminCommand command,String commandParameterName) throws DeployException{
		String type=null ;
		boolean typeFound = false;
		CommandMetadata commandMetadata = command.getCommandMetadata();
		EList elist = commandMetadata.getParameters();
		Iterator<ParameterMetadata> elistIt =  elist.iterator();

		while (elistIt.hasNext() && !typeFound){
			ParameterMetadata parameterMetadata = (ParameterMetadata)elistIt.next();
			if (parameterMetadata.getName().equalsIgnoreCase(commandParameterName)){
				type = parameterMetadata.getType();
				typeFound = true;
			} 
		}
		if (!typeFound){
			throw new DeployException(new Exception("type not defined for commandParameterName " + commandParameterName + " for Command " + commandMetadata.getName()));
		}
		return type;
	}
	
	/**
	 * Set command attribute after casting the value type 
	 * @param command
	 * @param commandParameterName
	 * @param commandParamaterValue
	 * @throws InvalidParameterValueException
	 * @throws InvalidParameterNameException
	 * @throws DeployException
	 */
	private void setCommandAttribute(AdminCommand command, String commandParameterName,  String commandParameterValue) 
			throws InvalidParameterValueException, InvalidParameterNameException, DeployException{
		
		String type = getParamaterType(command,commandParameterName);
		if (type.equalsIgnoreCase("Integer")){
			command.setParameter(commandParameterName,new Integer(commandParameterValue).intValue());
		}else if (type.equalsIgnoreCase("Boolean")){
			command.setParameter(commandParameterName,new Boolean(commandParameterValue).booleanValue());
		}else if (type.equalsIgnoreCase("String")){
			command.setParameter(commandParameterName,(String) commandParameterValue);	
		}else{
			command.setParameter(commandParameterName,commandParameterValue);
		}
		
		
	}
	
	/**
	 * Create a resource using AdminTask Command
	 * @param resource
	 * @param adminClient
	 * @param session
	 * @return
	 * @throws CommandMgrInitException
	 * @throws CommandException
	 * @throws CommandNotFoundException
	 * @throws ConnectorException
	 * @throws DeployException
	 */
	public ObjectName createResource(Resource resource, AdminClient adminClient, Session session )
		throws CommandMgrInitException,CommandException,CommandNotFoundException,ConnectorException, DeployException{
		
		CommandMgr cmdMgr = CommandMgr.getClientCommandMgr(adminClient);
		
		//AdminCommand command = cmdMgr.createCommand("createCluster");
		//AdminCommand command = cmdMgr.createCommand("createSIBDestination");

		logger.trace(">>>> Command that will run " + resource.getResourceMetaData().getCommandMetaData().getCreateCommand());
	//	displayCommandMetadata(cmdMgr, resource.getResourceMetaData().getCommandMetaData().getCreateCommand());

		AdminCommand  command = cmdMgr.createCommand(resource.getResourceMetaData().getCommandMetaData().getCreateCommand());
		
		command.setConfigSession(session);
		HashMap<String, String> attributeListFromXML = resource.getAttributeList();
		HashMap<String, String> attributeListFromXMLForStep = null;
		HashMap<String, CommandAttribute> attributeMappingsMetaDataForStep =  null;

		
		if (resource.getResourceMetaData().getCommandMetaData().getStepCommandMetaData()!=null ){
			
			TaskCommand taskCommand = (TaskCommand)command;
			logger.trace( " 	Step: taskCommand.listCommandSteps " + taskCommand.listCommandSteps().length+ " " + taskCommand.listCommandSteps().toString());
	//		for (int k = 0 ; k < taskCommand.listCommandSteps().length ; k++ ){
	//			logger.trace( " taskCommand.listCommandSteps -" + k + " " + taskCommand.listCommandSteps()[k]);
	//		}
	//		while(taskCommand.hasNextStep()){
	//			logger.trace(" Steps are " + taskCommand.nextStep().getName());
	//		}
	//		logger.trace(" getting memberConfig from " + taskCommand.getName() );
			CommandStep step = taskCommand.getCommandStep("memberConfig");
			//CommandStep step = taskCommand.gotoStep(resource.getResourceMetaData().getCommandMetaData().getStepCommandMetaData().getStepName());
			attributeListFromXMLForStep = resource.getAttributeList();
			attributeMappingsMetaDataForStep =  resource.getResourceMetaData().getCommandMetaData().getStepCommandMetaData().getAttributeMappings();

			String[] attributeListFromXMLKeysForStep =  (String[])attributeListFromXML.keySet().toArray(new String[0]);
			
			for (int i=0 ; i < attributeListFromXMLKeysForStep.length ; i++){
				logger.trace("			Step: Processing attribute from XML for Step " +  attributeListFromXMLKeysForStep[i]);
				if (attributeMappingsMetaDataForStep.get(attributeListFromXMLKeysForStep[i]) != null){
					logger.trace("		Step: attributeMappingsMetaData is not null"  );
					CommandAttribute  commandAttribute  = attributeMappingsMetaDataForStep.get(attributeListFromXMLKeysForStep[i]);
					String type = commandAttribute.getType();
					if (type.equalsIgnoreCase(ResourceConstants.COMMAND_MAPPING)){
						logger.trace("		Step: Attribute mapping COMMAND_MAPPING for Step " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
						step.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
						
					}else if  (type.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL)){
						logger.trace("		Step: Attribute mapping COMMAND_ADDITIONAL for Step " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
						step.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
					}
				}else{
					logger.trace("		Step: attributeMappingsMetaData is null"  );
					logger.trace("		Step: Attribute mapping for Step " + attributeListFromXMLKeysForStep[i] + " - " + attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
					step.setParameter(attributeListFromXMLKeysForStep[i],attributeListFromXMLForStep.get(attributeListFromXMLKeysForStep[i]));
				}
			}

		
		}

		

		HashMap<String, CommandAttribute> attributeMappingsMetaData =  resource.getResourceMetaData().getCommandMetaData().getAttributeMappings();
		String[] attributeListFromXMLKeys =  (String[])attributeListFromXML.keySet().toArray(new String[0]);
		
		for (int i=0 ; i < attributeListFromXMLKeys.length ; i++){
			logger.trace("			Command: Processing attribute from XML " +  attributeListFromXMLKeys[i]);
			/**
			 * Check so that attribute that we are processing in not step/task attribute, if it is do not add it to 
			 * Command as it is already added to Step, plus it will fail if added to command   
			 */
			if ((attributeMappingsMetaDataForStep == null) || attributeMappingsMetaDataForStep.get(attributeListFromXMLKeys[i]) == null){ 
				if (attributeMappingsMetaData.get(attributeListFromXMLKeys[i]) != null){
					CommandAttribute  commandAttribute  = attributeMappingsMetaData.get(attributeListFromXMLKeys[i]);
					String type = commandAttribute.getType();

					if (type.equalsIgnoreCase(ResourceConstants.COMMAND_MAPPING)){
						logger.trace("			Command:Setting value for COMMAND_MAPPING " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXML.get(attributeListFromXMLKeys[i]));
						command.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXML.get(attributeListFromXMLKeys[i]));
						
					}else if  (type.equalsIgnoreCase(ResourceConstants.COMMAND_ADDITIONAL)){
						logger.trace("			Command:Setting value for mapping COMMAND_ADDITIONAL " + commandAttribute.getCommandAttribute()  + " - " + attributeListFromXML.get(attributeListFromXMLKeys[i]));
						command.setParameter(
								commandAttribute.getCommandAttribute() ,
								attributeListFromXML.get(attributeListFromXMLKeys[i]));
					}	
				}else{
					logger.trace("			Command:Setting value for " + attributeListFromXMLKeys[i] + " - " + attributeListFromXML.get(attributeListFromXMLKeys[i]));
					setCommandAttribute(command,attributeListFromXMLKeys[i], attributeListFromXML.get(attributeListFromXMLKeys[i]));
					//command.setParameter(attributeListFromXMLKeys[i],attributeListFromXML.get(attributeListFromXMLKeys[i]));
				}
			}
		}
		
		/**
		 * Now process the attributes that will not be in the resource XMLs. Like constants and mappings
		 */
		String[] attributeMappingsMetaDataKeys =  (String[])attributeMappingsMetaData.keySet().toArray(new String[0]);
		
		for (int i=0 ; i < attributeMappingsMetaDataKeys.length; i++){
			CommandAttribute  commandAttribute  = attributeMappingsMetaData.get(attributeMappingsMetaDataKeys[i]);
			String type = commandAttribute.getType();

			if (type.equalsIgnoreCase(ResourceConstants.COMMAND_PARENT)){
				logger.trace("			Command:COMMAND_PARENT Setting value for " + commandAttribute.getCommandAttribute()  + " - " + ResourceHelper.getResourceIdentifierName(resource.getParent()));
				command.setParameter(commandAttribute.getCommandAttribute() , 
						ResourceHelper.getResourceIdentifierName(resource.getParent()));
			}else if  (type.equalsIgnoreCase(ResourceConstants.COMMAND_CONSTANT)){
				logger.trace("			Command:COMMAND_CONSTANT Setting value for " + commandAttribute.getCommandAttribute()  + " - " + commandAttribute.getConstantValue());
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
		logger.trace( "		Running command now ");
		CommandResult result = null;
		try{
			command.execute();
			result =  command.getCommandResult();
			SDLog.log("		Result is " + result.isSuccessful() );
			if (!result.isSuccessful()){
				ArrayList results = ((ArrayList)command.getResult());
				for (int i=0; i < results.size();i++ ){
					((Exception)results.get(i)).printStackTrace();
				}

			}
			logger.trace( "<<<< Result of command is " + (ObjectName)result.getResult());
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

	/**
	 * Used to modify an attribute 
	 * @param resource
	 * @param attribute
	 * @param commandLinkAttribute
	 * @param adminClient
	 * @param session
	 * @param configService
	 * @return
	 * @throws ConfigServiceException
	 * @throws ConnectorException
	 * @throws AttributeNotFoundException
	 */
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
	
	public void modifyResource(Resource resource, AdminClient adminClient, Session session )
			throws DeployException{
			
		try {
			CommandMgr cmdMgr = CommandMgr.getClientCommandMgr(adminClient);
			

			logger.trace(">>>> Command that will run " + resource.getResourceMetaData().getCommandMetaData().getModifyCommand());
			displayCommandMetadata(cmdMgr, resource.getResourceMetaData().getCommandMetaData().getModifyCommand());
		} catch (CommandException e) {
			throw new DeployException(e);
		} catch (ConnectorException e) {
			throw new DeployException(e);
		}

		//	AdminCommand  command = cmdMgr.createCommand(resource.getResourceMetaData().getCommandMetaData().getCreateCommand());
			
		//	command.setConfigSession(session);
	}
}
