/**	   Copyright [2009] [www.apartech.co.uk]


**/
package com.apartech.auradeploy.helper;

import java.util.Properties;

import javax.management.InstanceNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.apartech.auradeploy.deploy.DeployInfo;
import com.apartech.common.exception.DeployException;
import com.apartech.common.log.SDLog;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.ConnectorException;

public class SDAdminClientFactory {
	
//    private AdminClient adminClient;
    
    private static final Log logger  = LogFactory.getLog(SDAdminClientFactory.class);

    private ConnectionObjects createAdminClient(DeployInfo deployInfo)
	throws DeployException{
    	ConnectionObjects connectionObjects = new ConnectionObjects();
		SDLog.log("Creating connection to target server on host :" + deployInfo.getHost());
		SDLog.log("Creating connection to target server on port :" + deployInfo.getPort());
		SDLog.log("Creating connection to target server using :" + deployInfo.getConnectionType());
		SDLog.log("    ");
	
		logger.trace("Create the Properties object.");
	    Properties connectProps = new Properties();
		logger.debug(deployInfo.getConnectionType());
		logger.debug("Setting AdminClient.CONNECTOR_HOST to " + deployInfo.getHost());
	    connectProps.setProperty(AdminClient.CONNECTOR_HOST, deployInfo.getHost());
	
		logger.debug("Setting AdminClient.CONNECTOR_PORT "+ deployInfo.getPort());
	    connectProps.setProperty(AdminClient.CONNECTOR_PORT, deployInfo.getPort());
	
		logger.debug("Setting AdminClient.USERNAME " + deployInfo.getUserName());
			
	    connectProps.setProperty(AdminClient.USERNAME, deployInfo.getUserName());
	    connectProps.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
	
	//	logger.debug("Setting AdminClient.PASSWORD " + deployInfo.getPassword());
	    connectProps.setProperty(AdminClient.PASSWORD, deployInfo.getPassword());

		
		if ((deployInfo.getJAACSecurityConfig()!=null) && (deployInfo.getJAACSecurityConfig().length()>0))
			System.setProperty("java.security.auth.login.config", deployInfo.getJAACSecurityConfig());
			//System.setProperty("java.security.auth.login.config", "file:C:\\IBM\\WebSphere61\\profiles\\Dmgr01\\properties\\wsjaas_client.conf");
		
		if ((deployInfo.getSASSecurityConfig()!=null) && (deployInfo.getSASSecurityConfig().length()>0))
			System.setProperty("com.ibm.CORBA.ConfigURL", deployInfo.getSASSecurityConfig());
			//System.setProperty("com.ibm.CORBA.ConfigURL","file:C:\\IBM\\WebSphere61\\profiles\\Dmgr01\\properties\\sas.client.props");
		
		if ((deployInfo.getSSLSecurityConfig()!=null) && (deployInfo.getSSLSecurityConfig().length()>0))
			System.setProperty("com.ibm.SSL.ConfigURL" ,deployInfo.getSSLSecurityConfig());
			//System.setProperty("com.ibm.SSL.ConfigURL" ,"file:C:\\IBM\\WebSphere61\\profiles\\Dmgr01\\properties\\ssl.client.props");
		
		if ((deployInfo.getSOAPSecurityConfig()!=null) && (deployInfo.getSOAPSecurityConfig().length()>0))
			System.setProperty("com.ibm.SOAP.ConfigURL" ,deployInfo.getSOAPSecurityConfig());
		

		if (deployInfo.getConnectionType().equalsIgnoreCase("RMI")){
			logger.debug(deployInfo.getConnectionType() + "Setting AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI");
			
	   		logger.debug("Setting AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI");
			connectProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI);
			// connectProps.setProperty("com.ibm.CORBA.ConfigURL", "C:/IBM/WebSphere6/AppServer/profiles/Dmgr01/properties/sas.client.properites");
		}else{
	   		logger.debug("Setting AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP");
			connectProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		}
	
	
		System.out.println( "com.ibm.CORBA.ConfigURL" + System.getProperty("com.ibm.CORBA.ConfigURL"));
		

	
	    try
	    {
	   		logger.trace("Creating AdminClient." );
	   		AdminClient adminClient =  AdminClientFactory.createAdminClient(connectProps);
	   		connectionObjects.setAdminClient(adminClient);
			SDLog.log("Created AdminClient connection to target server sucessfully");
			SDLog.log("    ");
			
	
	        logger.trace("Created AdminClient successfully." );
	
	        logger.trace("Creating configService." );
	
	        connectionObjects.setConfigService(new ConfigServiceProxy(adminClient));
	        logger.trace("Creating configService successfully." );
	
	        logger.trace("Creating session." );
	        Session session = new Session("A",false);
	        connectionObjects.setSession(session);
	        String sessionID = session.getSessionId();
	        connectionObjects.setSessionID(sessionID);
	        SDLog.log("Created session with SESSION ID " + sessionID );
			SDLog.log("    ");
	
	
	        logger.trace("Created SessionId is " + session.getSessionId() );
	
	    }catch (ConnectorException e){
	    	logger.error("Exception creating admin client, "+e.getMessage(),e);
	        e.printStackTrace();
	        throw new DeployException(e);
		}catch (InstanceNotFoundException e){
	    	logger.error("Exception creating admin client, "+e.getMessage(),e);
			e.printStackTrace();
			throw new DeployException(e);
		}				
	    logger.debug("Connected to AdminClient successfully.");
	    return connectionObjects;
    }

    public ConnectionObjects getConnectionObjects(DeployInfo deployInfo)
    	throws DeployException{

    	return createAdminClient(deployInfo);
    }

}
