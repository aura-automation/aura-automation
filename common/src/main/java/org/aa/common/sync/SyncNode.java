/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.common.sync;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.aa.common.deploy.Connection;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

public class SyncNode extends Connection {
    boolean distNotComplete = true;
	private static final Log logger  = LogFactory.getLog(SyncNode.class);
	public DeployNotificationListener deployNotificationListener = new DeployNotificationListener();
	boolean nodeExist=true;
	public static List<String> syncIssuedNodes=new ArrayList<String>();
	boolean isSync=false;
	private boolean registerNotificationListener(AdminClient adminClient, String nodeName ) throws DeployException
	{
		
		try
		{
		
			logger.trace("Getting reference to AppManagement MBean.");
			
			//            String query = "WebSphere:type=Cluster,*";
			//String query = "WebSphere:*,type=NodeAgent";
			logger.trace("Entered the register notification method." + "WebSphere:*,type=NodeSync");
			//String query = "WebSphere:*,node=" + nodeName +",type=NodeSync";
		
			String query = "WebSphere:*,node=" + nodeName +",type=NodeSync";
			
			ObjectName NodeSync = null;
			
			ObjectName queryName = new ObjectName (query);
			Set s = adminClient.queryNames(queryName, null);
			logger.trace(" Got reference to set of AppManagement MBean.");
			
			if (s.size() > 0){
				NodeSync = (ObjectName)s.iterator().next();
				if(adminClient.invoke(NodeSync, "isNodeSynchronized", null, null).toString()=="true")
				{
					SDLog.log("		Node already syncronised, hence will not issue sync for node "+nodeName);
					isSync=true;
					return true;
				}
				else
				{
					logger.trace(" AppManagement MBean found.");
					logger.trace("Adding Notification to AppManagemeng MBean.");
					syncIssuedNodes.add(nodeName);
					adminClient.addNotificationListener(NodeSync , deployNotificationListener, null, null);
					SDLog.log("		Will call synchronise for  "+nodeName);

					logger.trace("		Added Notification to AppManagemeng MBean sucessfully.");
					return true;
				}
				
			}else{
				SDLog.log("		Nodegent is unmanaged or unavailable, sync is not issued for node " + nodeName);	
				return false;
			}
	
		
		}catch (MBeanException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ReflectionException e) {
        	e.printStackTrace();
        	throw new DeployException(e);
		}
		catch (InstanceNotFoundException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (MalformedObjectNameException e){
			e.printStackTrace();
			throw new DeployException(e);
		}
	}

	    public void unRegisterNotificationListener (AdminClient adminClient, String nodeName  ) throws DeployException
	    {
	   		logger.trace("Entered the register notification method.");

	        try
	        {
				logger.trace("Getting reference to AppManagement MBean.");
		
				String query = "WebSphere:*,node=" + nodeName +",type=NodeSync";
	   			ObjectName ApplicationManager = null;
	            ObjectName queryName = new ObjectName (query);
	            Set s = adminClient.queryNames(queryName, null);
	       		
				logger.trace("Got reference to set of AppManagement MBean.");
	        
	            if (!s.isEmpty()){
					ApplicationManager = (ObjectName)s.iterator().next();
					logger.trace("AppManagement MBean found.");

	            }
				logger.trace("Adding Notification to AppManagemeng MBean.");
				adminClient.removeNotificationListener(ApplicationManager , deployNotificationListener);

				logger.trace("Added Notification to AppManagemeng MBean sucessfully.");

	        }catch (InstanceNotFoundException e){
	            e.printStackTrace();
	        	throw new DeployException(e);
	        }catch (ConnectorException e){
	            e.printStackTrace();
	        	throw new DeployException(e);
	        }catch (MalformedObjectNameException e){
	        	e.printStackTrace();
	        	throw new DeployException(e);
	        }catch (ListenerNotFoundException e){
	        	e.printStackTrace();
	        	throw new DeployException(e);
	        }
	    }

	
	public void syncNode (String nodeName,AdminClient adminClient,DeployInfo deployInfo) throws DeployException
    {   
		
		isSync=false;
   		logger.trace("Entered Node Sync.");
   		
   		
        try
        {
			logger.trace("Getting reference to CellSync MBean.");
	
            String query = "WebSphere:type=CellSync,*";
   			ObjectName CellSync = null;
            ObjectName queryName = new ObjectName (query);
            Set s = adminClient.queryNames(queryName, null);
			logger.trace("Got reference to set of CellSync MBean.");
     
           			
			if(registerNotificationListener(adminClient,nodeName)) 
			{
	            if (!s.isEmpty()){
					CellSync = (ObjectName)s.iterator().next();
					logger.trace("CellSync MBean found.");
					
	        		DeployState.syncCount ++;
					logger.trace( " Status of Sync is "+ (adminClient.invoke(CellSync, "syncNode", new String[]{nodeName}, new String[]{String.class.getName()})).toString());
					//SDLog.log("Sync called for Node " + nodeName);
	
	           }
           }
    		
//            unRegisterNotificationListener(adminClient);
        }catch (MBeanException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (InstanceNotFoundException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ReflectionException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (ConnectorException e){
            e.printStackTrace();
        	throw new DeployException(e);
        }catch (MalformedObjectNameException e){
        	e.printStackTrace();
        	throw new DeployException(e);
        }catch (Exception e) {
			e.printStackTrace();
			throw new DeployException(e);
		}
    }
 	
	public void syncAllNodes(DeployInfo deployInfo)
		throws DeployException,ConfigServiceException,ConnectorException,AdminException{
		
		int waitcount = 0; 	
		createAdminClient(deployInfo);
		if (deployInfo.getConnectionMode().equalsIgnoreCase("Remote")){
			SyncNode syncNode = new SyncNode();
			ObjectName[] configIDs =  configService.resolve(session, "Node");
			for (int i=0; i < configIDs.length;i++){
				String nodeName = configService.getAttribute(session,configIDs[i], "name").toString();
				SDLog.log("");
				SDLog.log("Sync Node: " + nodeName );
				syncNode.syncNode(nodeName, adminClient,deployInfo);
			}
		}
		try{
	        while (distNotComplete)
	        {
	  
	        	Thread.sleep(deployInfo.getSleepTimeForSyncRequest());

	        	if (DeployState.websphere_nodesync_complete){
	        		SDLog.log("All Node syncronised");
	        		distNotComplete = false;
//	        		for(int i=0;i<syncIssuedNodes.size();i++)
//	        		{
//	        		unRegisterNotificationListener(adminClient,syncIssuedNodes.get(i));
//	        		}
	        		
	        	}else{

		        	if (DeployState.syncCount <=0)
		        		DeployState.syncCount = 0;
		        	
		        	if ((DeployState.syncCount <=0) || (waitcount >= 2)){
		        		waitcount = 0;
		        		DeployState.syncCount ++;
		        		SDLog.log("Requesting the status of the syncronisation." );
		        	}else{
		        		waitcount ++;	
		        		SDLog.log("Waiting for the response of the syncronisation status" + DeployState.syncCount);
		        	}
		          	if(syncIssuedNodes.size()==0)
					{
		          		distNotComplete = false;
					}
	        	}
	        }
		}catch(InterruptedException e){
	        	throw new DeployException(e);
	    }

	}
}
