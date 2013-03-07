/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.common.helper;

import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;

public class ConnectionObjects {

    private AdminClient adminClient;
    private ObjectName nodeAgent;

    Session session ;
	String sessionID ;
	ConfigService configService ;
	/**
	 * @return the adminClient
	 */
	public AdminClient getAdminClient() {
		return adminClient;
	}
	/**
	 * @param adminClient the adminClient to set
	 */
	public void setAdminClient(AdminClient adminClient) {
		this.adminClient = adminClient;
	}
	/**
	 * @return the nodeAgent
	 */
	public ObjectName getNodeAgent() {
		return nodeAgent;
	}
	/**
	 * @param nodeAgent the nodeAgent to set
	 */
	public void setNodeAgent(ObjectName nodeAgent) {
		this.nodeAgent = nodeAgent;
	}
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	/**
	 * @return the configService
	 */
	public ConfigService getConfigService() {
		return configService;
	}
	/**
	 * @param configService the configService to set
	 */
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

}
