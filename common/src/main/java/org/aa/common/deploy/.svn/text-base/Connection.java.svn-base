/**	   Copyright [2009] [www.apartech.co.uk]


**/
package com.apartech.common.deploy;

import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import com.ibm.ws.management.configservice.ConfigServiceImpl;
import com.apartech.common.exception.DeployException;
import com.apartech.common.helper.ConnectionObjects;
import com.apartech.common.helper.SDAdminClientFactory;
import com.apartech.common.log.SDLog;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;

public class Connection {
    protected AdminClient adminClient;
    protected ObjectName nodeAgent;

    protected Session session ;
    protected String sessionID ;
    protected ConfigService configService ;
	final Log logger  = LogFactory.getLog(Connection.class);	

    protected void createAdminClient(DeployInfo deployInfo)
    	throws DeployException,AdminException{
    	
    	SDAdminClientFactory sdAdminClientFactory  = new SDAdminClientFactory (); 

    	SDLog.log("Server: " +  deployInfo.getHost() );
		SDLog.log("******************************");

    	ConnectionObjects connectionObjects = sdAdminClientFactory.getConnectionObjects(deployInfo);

		//createAdminClient(deployInfo);
    	configService = connectionObjects.getConfigService();
		adminClient = connectionObjects.getAdminClient();
    	configService = connectionObjects.getConfigService();
    	nodeAgent = connectionObjects.getNodeAgent(); 
    	session = connectionObjects.getSession();
    	sessionID = connectionObjects.getSessionID();
		
    }

    protected ObjectName getObjectName(String query)
    	throws MalformedObjectNameException,ConnectorException{
	    ObjectName queryName = new ObjectName (query);
	    ObjectName _objectName = null;
	    Set s = adminClient.queryNames(queryName, null);
	    
	    if (!s.isEmpty()){
	    	_objectName   = (ObjectName)s.iterator().next();
	    }
	    return _objectName ;
    }
}
