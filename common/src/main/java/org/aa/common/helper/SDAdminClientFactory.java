/**	   Copyright 


**/
package org.aa.common.helper;

import java.util.Properties;

import javax.management.InstanceNotFoundException;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceFactory;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;

public class SDAdminClientFactory {
	
//    private AdminClient adminClient;
    
    private static final Log logger  = LogFactory.getLog(SDAdminClientFactory.class);

    private ConnectionObjects createAdminClient(DeployInfo deployInfo)
		throws DeployException{
    	
    	ConnectionObjects connectionObjects = new ConnectionObjects();
		SDLog.log("Creating connection to target server on host :" + deployInfo.getHost());
	
		logger.trace("Create the Properties object.");
	    Properties connectProps = new Properties();
		logger.debug(deployInfo.getConnectionType());
		
		 try{
			if (deployInfo.getConnectionMode().equalsIgnoreCase(DeployValues.CONNECTION_MODE_LOCAL)){
				SDLog.log("Creating connection to target server using local mode: " + deployInfo.getWasRespositoryRoot());
				SDLog.log("    ");
				
				System.setProperty("was.repository.root",deployInfo.getWasRespositoryRoot());
				System.setProperty("user.install.root",deployInfo.getUserInstallRoot());
				
		
				logger.trace( "was.repository.root JVM property is  " + System.getProperty("was.repository.root"));
				/**
				 * 
				 * in local mode only config service can be created in the life time of 
				 * the JVM hence this check to make sure that create config is not called 
				 * more then once
				 */
				ConfigService configService = ConfigServiceFactory.getConfigService();
				
				SDLog.log("******************************");			
				if (configService == null) {
					Properties prop = new Properties(); 
					prop.setProperty("location", "local");
							      
					configService = ConfigServiceFactory.createConfigService(true, prop);
				
				}
				
				connectionObjects.setConfigService( configService);
			   
			}else{
				SDLog.log("Creating connection to target server on port :" + deployInfo.getPort());
				SDLog.log("Creating connection to target server using :" + deployInfo.getConnectionType());
				SDLog.log("    ");

				if ((deployInfo.getJAACSecurityConfig()!=null) && (deployInfo.getJAACSecurityConfig().length()>0)){
					System.setProperty("java.security.auth.login.config","file://"+  deployInfo.getJAACSecurityConfig());
					logger.trace ("java.security.auth.login.config" + "file://"+  deployInfo.getJAACSecurityConfig());
				}
				if ((deployInfo.getSASSecurityConfig()!=null) && (deployInfo.getSASSecurityConfig().length()>0)){
					System.setProperty("com.ibm.CORBA.ConfigURL",  "file://"+  deployInfo.getSASSecurityConfig());
					logger.trace("com.ibm.CORBA.ConfigURL" + "file://"+  deployInfo.getSASSecurityConfig());
				}
				if ((deployInfo.getSSLSecurityConfig()!=null) && (deployInfo.getSSLSecurityConfig().length()>0)){
					System.setProperty("com.ibm.SSL.ConfigURL" ,"file://"+  deployInfo.getSSLSecurityConfig());
					logger.trace("com.ibm.SSL.ConfigURL" + "file://"+  deployInfo.getSSLSecurityConfig());
				}
				if ((deployInfo.getSOAPSecurityConfig()!=null) && (deployInfo.getSOAPSecurityConfig().length()>0)){
					System.setProperty("com.ibm.SOAP.ConfigURL" , "file://"+ deployInfo.getSOAPSecurityConfig());
					logger.trace("com.ibm.SOAP.ConfigURL"  + "file://"+  deployInfo.getSOAPSecurityConfig());
				}
				
				if (deployInfo.getConnectionType().equalsIgnoreCase("RMI")){
					logger.debug(deployInfo.getConnectionType() + "Setting AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI");
					
			   		logger.debug("Setting AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI");
					connectProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_RMI);
					
				}else{
			   		logger.debug("Setting AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP");
					connectProps.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
				}
			
				logger.debug("Setting AdminClient.CONNECTOR_HOST to " + deployInfo.getHost());
			    connectProps.setProperty(AdminClient.CONNECTOR_HOST, deployInfo.getHost());
				logger.debug("Setting AdminClient.CONNECTOR_PORT "+ deployInfo.getPort());
			    connectProps.setProperty(AdminClient.CONNECTOR_PORT, deployInfo.getPort());
				logger.debug("Setting AdminClient.USERNAME " + deployInfo.getUserName());
			    connectProps.setProperty(AdminClient.USERNAME, deployInfo.getUserName());
			    connectProps.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
			    connectProps.setProperty(AdminClient.PASSWORD, deployInfo.getPassword());

			    logger.trace("Creating AdminClient." );
		   		AdminClient adminClient =  AdminClientFactory.createAdminClient(connectProps);
		   		connectionObjects.setAdminClient(adminClient);
				SDLog.log("Created AdminClient connection to target server sucessfully");
				SDLog.log("    ");
		
		        logger.trace("Created AdminClient successfully." );
		        logger.trace("Creating configService." );
		
		        connectionObjects.setConfigService(new ConfigServiceProxy(adminClient));
		        logger.trace("Creating configService successfully." );
		    
		        logger.debug("Connected to AdminClient successfully.");
			}
	        logger.trace("Creating session." );
	        Session session = new Session("A",false);
	        connectionObjects.setSession(session);
	        String sessionID = session.getSessionId();
	        connectionObjects.setSessionID(sessionID);
	        SDLog.log("Created session with SESSION ID " + sessionID );
			SDLog.log("    ");
	        logger.trace("Created SessionId is " + session.getSessionId() );


		 }catch (ConnectorException e){
	    	logger.error("Exception creating admin client, "+e.getStackTrace(),e);
	        e.printStackTrace();
	        throw new DeployException(e);
		}catch (InstanceNotFoundException e){
	    	logger.error("Exception creating admin client, "+e.getStackTrace(),e);
			e.printStackTrace();
			throw new DeployException(e);
		}catch (AdminException e) {
	    	logger.error("Exception creating admin client, "+ e.getStackTrace(),e);
			e.printStackTrace();
			throw new DeployException(e);
		}				
		return connectionObjects;
    }

    public ConnectionObjects getConnectionObjects(DeployInfo deployInfo)
    	throws DeployException{

    	return createAdminClient(deployInfo);
    }

}
