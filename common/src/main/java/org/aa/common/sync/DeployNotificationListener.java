/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.common.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.aa.common.Constants.JMXApplication;
import org.aa.common.helper.Helper;
import org.aa.common.log.SDLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.ibm.websphere.management.application.AppNotification;


public class DeployNotificationListener implements  AppNotification.Listener, NotificationListener{

	private static final Log logger  = LogFactory.getLog(DeployNotificationListener.class);

	List<String> initiateNodeList=new  ArrayList<String>();
	List<String> completedNodeList=new  ArrayList<String>(); 
	public void handleNotification(Notification ntfyObj, Object handback){
	        // Each notification that the NodeAgent MBean generates will result in
	        // this method being called
//	       System.out.println("* Notification received at " + new Date().toString());
//	       System.out.println("* type      = " + ntfyObj.getType());
//	       System.out.println("* message   = " + ntfyObj.getMessage());
//	       System.out.println("* source    = " + ntfyObj.getSource());
//	       System.out.println("* seqNum    = " + Long.toString(ntfyObj.getSequenceNumber()));
//	       System.out.println("* timeStamp = " + new Date(ntfyObj.getTimeStamp()));

			DeployState.ntfyCount++;
	
			
	        String ntfyObjType = ntfyObj.getType();
	      

	        
	        
	        if (ntfyObjType.startsWith("websphere.nodesync") ){
	        	
	        	handleNodeSyncNotification(ntfyObj, handback);
	        }	        
	        logger.trace("? Notification received at " + ntfyObjType);
	        logger.trace("? Notification received at " + new Date().toString());
	        logger.trace("? type      = " + ntfyObj.getType());
	        if (ntfyObj.getMessage()!=null)
	        	logger.trace("? message   = " + ntfyObj.getMessage());
	        logger.trace("? source    = " + ntfyObj.getSource());
	        logger.trace("? seqNum    = " + Long.toString(ntfyObj.getSequenceNumber()));
	        logger.trace("? timeStamp = " + new Date(ntfyObj.getTimeStamp()));

			
	    }

	public void appEventReceived(AppNotification data){

		SDLog.log("################################");
		SDLog.log( "Data Subtask = " + data.subtaskName);
		SDLog.log("Data Message = " + data.message);
		SDLog.log("################################");
	}

	private void handleNodeSyncNotification(Notification ntfyObj, Object handback)
	{
		boolean isExist=false;
		String ntfyObjType=ntfyObj.getType();
		String ntfyObjSource=ntfyObj.getSource().toString();
		String nodeName=ntfyObjSource.substring(ntfyObjSource.indexOf("node=")+5,ntfyObjSource.length());
		if(ntfyObjType.equals("websphere.nodesync.initiated"))
		{
			nodeName=nodeName.substring(0,nodeName.indexOf(","));
			SDLog.log("		Sync Initiated on Node " + nodeName );
			for(int i=0;i<SyncNode.syncIssuedNodes.size();i++)
			{
				if(SyncNode.syncIssuedNodes.get(i).equals(nodeName))
				{
					for(int j=0;j<initiateNodeList.size();j++)
					{
						if(initiateNodeList.get(j).equals(nodeName))
						{
							isExist=true;
							break;
						}
					}
					if(isExist==false)
					{
						initiateNodeList.add(nodeName);
						isExist=true;
					}
				}
			}
			if(SyncNode.syncIssuedNodes.size()==initiateNodeList.size())
			{DeployState.websphere_nodesync_initiated=true;}
		}
		if(ntfyObjType.equals("websphere.nodesync.complete"))
		{
			nodeName=nodeName.substring(0,nodeName.indexOf(","));
			
			SDLog.log("		Sync Complete on Node " + nodeName);
			for(int i=0;i<SyncNode.syncIssuedNodes.size();i++)
			{
				if(SyncNode.syncIssuedNodes.get(i).equals(nodeName))
				{
					for(int j=0;j<completedNodeList.size();j++)
					{
						if(completedNodeList.get(j).equals(nodeName))
						{
							isExist=true;
							break;
						}
					}
					if(isExist==false)
					{
						completedNodeList.add(nodeName);
						isExist=true;
					}
				}
			}
			if(SyncNode.syncIssuedNodes.size()==completedNodeList.size())
			{DeployState.websphere_nodesync_complete=true;}
		}
	}

	private void handleApplicationNotification(Notification ntfyObj, Object handback){
		logger.trace(">> Entry handleApplicationNotification");

		logger.trace(ntfyObj.getUserData());

		Hashtable data =  Helper.getAppUserData(ntfyObj.getUserData());
        
        logger.trace( "User Data  " + JMXApplication.APPNOTIFICATIONTASK  + " = "+ data.get(JMXApplication.APPNOTIFICATIONTASK).toString());
        logger.trace( "User Data  " +  JMXApplication.SUBTASK + " = " + data.get(JMXApplication.SUBTASK).toString());
        logger.trace( "User Data  " +  JMXApplication.SUBTASKSTATUS + " = " + data.get(JMXApplication.SUBTASKSTATUS).toString());
        logger.trace( "User Data  " +  JMXApplication.TASKSTATUS + " = " + data.get(JMXApplication.TASKSTATUS).toString()); 
        logger.trace( " ++++++++ User Data  " +  JMXApplication.TASKSTATUS + " = " + data.get("properties").toString() + " ++++++++");
        SDLog.log(new Date(ntfyObj.getTimeStamp()) + " " + data.get("message").toString());
        SDLog.log("");
//        Hashtable data =  Helper.getAppUserData(ntfyObj.getUserData());
    
   // System.out.println( "User Data  " + JMXApplication.APPNOTIFICATIONTASK  + " = "+ data.get(JMXApplication.APPNOTIFICATIONTASK).toString());
   // System.out.println(  "User Data  " +  JMXApplication.SUBTASK + " = " + data.get(JMXApplication.SUBTASK).toString());
   // System.out.println(  "User Data  " +  JMXApplication.SUBTASKSTATUS + " = " + data.get(JMXApplication.SUBTASKSTATUS).toString());
   // System.out.println(  "User Data  " +  JMXApplication.TASKSTATUS + " = " + data.get(JMXApplication.TASKSTATUS).toString());  

    logger.trace( "Uninstall Status is " + DeployState.uninstallStatus);
    logger.trace( "Install Status is " + DeployState.installStatus); 

    	
	if (data.get(JMXApplication.APPNOTIFICATIONTASK).toString().equalsIgnoreCase(JMXApplication.NOTIFICATION_UNINSTALLAPP)){
		if (data.get(JMXApplication.TASKSTATUS).toString().equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED)){
		    if (Helper.isApplicationNameCorrect (data.get("message").toString() ,DeployState.deployInfo.getApplicationName())){
		        logger.warn("Got Application Notification that uninstall is complete hence set local uninstall status to complete.");
		        DeployState.uninstallStatus = JMXApplication.NOTIFICATION_COMPLETED;		

		    }
		}else if(data.get(JMXApplication.TASKSTATUS).toString().equalsIgnoreCase(JMXApplication.NOTIFICATION_INPROGRESS)){
	        logger.warn("Got Application Notification that uninstall is InProgress hence set the uninstall status to InProgress.");
	        DeployState.uninstallStatus = JMXApplication.NOTIFICATION_INPROGRESS;		
			DeployState.nodes =  Helper.getAllNodes((Properties)data.get("properties"));

		}else{
		    if (Helper.isApplicationNameCorrect (data.get("message").toString() ,DeployState.deployInfo.getApplicationName())){
			    logger.error("Got Application Notification that Uninstall has failed hence set the uninstall status to failed.");
			    DeployState.uninstallStatus = JMXApplication.NOTIFICATION_FAILED;
		    }
		}

	}

		if (data.get(JMXApplication.APPNOTIFICATIONTASK).toString().equalsIgnoreCase(JMXApplication.NOTIFICATION_INSTALLAPP)){
			if (data.get(JMXApplication.TASKSTATUS).toString().equalsIgnoreCase(JMXApplication.NOTIFICATION_COMPLETED)){
			    if (Helper.isApplicationNameCorrect (data.get("message").toString() ,DeployState.deployInfo.getApplicationName())){
					logger.warn("Got Application Notification that Install status is complete hence set the install status to complete.");
					DeployState.installStatus = JMXApplication.NOTIFICATION_COMPLETED;
			    }
			}else if(data.get(JMXApplication.TASKSTATUS).toString().equalsIgnoreCase(JMXApplication.NOTIFICATION_INPROGRESS)){
				if (DeployState.nodes.size()<=0){
					DeployState.nodes =  Helper.getAllNodes((Properties)data.get("properties"));
				}
				logger.warn("Got Application Notification that Install status is InProgress hence set the install status to InProgress.");
				DeployState.installStatus = JMXApplication.NOTIFICATION_INPROGRESS;		
			}else{
			    if (Helper.isApplicationNameCorrect (data.get("message").toString() ,DeployState.deployInfo.getApplicationName())){
					logger.warn("Got Application Notification that Install status is failed hence set the install status to failed.");
					DeployState.installStatus = JMXApplication.NOTIFICATION_FAILED;		
			    }
			}
		
		}
		
		
		DeployState.distStatus =  Helper.getDistStatus((Properties)data.get("properties"));
		
		DeployState.distStatusForUninstall =  Helper.getDistStatusForUninstall((Properties)data.get("properties"));

		
		//		System.out.println( "distStatus is " + distStatus );
		
		if (((Properties)data.get("properties")).getProperty("AppDistributionAll").trim().length()>0 ){
			if (DeployState.syncCount >0)
				DeployState.syncCount --;
		}

		
	}
		
}
