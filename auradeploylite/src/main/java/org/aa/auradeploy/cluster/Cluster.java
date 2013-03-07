/*
 * Created on 12-Apr-2005
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
package org.aa.auradeploy.cluster;

import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.deploy.DeployInfo;
import org.aa.auradeploy.deploy.DeployNotificationListener;
import org.aa.auradeploy.deploy.DeployState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;


/**
 * @author Jatin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cluster extends org.aa.common.deploy.Connection{
	
	public DeployNotificationListener deployNotificationListener = new DeployNotificationListener();
	private static final Log logger  = LogFactory.getLog(Cluster.class);
	private DeployInfo mdeployInfo;
	ObjectName cluster = null;
	boolean nodeExist=true;
	private Cluster(){

	}

	public Cluster(DeployInfo deployInfo)
	throws DeployException{
		mdeployInfo = deployInfo;
		try{
			SDLog.log("Aura Deploy Lite Version "+DeployValues.VERSION+", Apartech Ltd, www.apartech.co.uk." );
	    	SDLog.log("Copyright Apartech Ltd ");
			SDLog.log("" );

			createAdminClient(deployInfo);

			String query = "WebSphere:type=Cluster,name="+deployInfo.getCluster() +",*";
			cluster = getObjectName(query);
			if (cluster == null){
				throw new DeployException(new Exception("Cluster does " + deployInfo.getCluster() + " not exists "));
			}
		}catch(MalformedObjectNameException e){
			throw new DeployException(e);
		}catch(ConnectorException e){
			throw new DeployException(e);

		}catch(AdminException e){
				throw new DeployException(e);

		}
	}

	public void doWork() 
	throws DeployException{
		SDLog.log("Cluster Operation is " + mdeployInfo.getOperation());
		if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.CLUSTER_OPERATION_START)){
			startCluster();
		}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.CLUSTER_OPERATION_STOP)){
			stopCluster();
		}else if (mdeployInfo.getOperation().equalsIgnoreCase(DeployValues.CLUSTER_OPERATION_RESTART)){
			restartCluster();
		}

	}
	public boolean checkIfClusterRunning(){
		boolean hasStarted = false;
		try{
			logger.trace("Get the state attribute");	

			String state= adminClient.getAttribute(cluster, "state").toString();
			logger.trace("Value of the state attribute is " + state);	

			if (state.equalsIgnoreCase(DeployValues.CLUSTER_RUNNING)){
				hasStarted = true;
			}else{
				hasStarted = false;
			}

		}catch(ConnectorException e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}catch(ReflectionException e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}catch(InstanceNotFoundException e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}catch(AttributeNotFoundException e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}catch(MBeanException e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return hasStarted;
	}


	public void startCluster()
	throws DeployException{
		DeployState.websphere_process_starting = false;
		DeployState.websphere_process_running = false;


		try {
			registerClusterNotificationListener();
			boolean noExit = true;

			boolean isRunning = checkIfClusterRunning();

			if (!isRunning){	

				String opName ="start";
				String signature[] = {};
				String params[] = { };
				adminClient.invoke(cluster , opName, params, signature);
				SDLog.log(" Cluster Starting# " + mdeployInfo.getCluster());

				while (noExit )
				{
					Thread.sleep(9000);

					logger.trace( "websphere_process_stopped" + DeployState.websphere_process_stopped);
					logger.trace("websphere_process_stopping" + DeployState.websphere_process_stopping);
					logger.trace("websphere_process_running" + DeployState.websphere_process_running);
					logger.trace("websphere_process_starting" + DeployState.websphere_process_starting);

					if (DeployState.websphere_process_starting && mdeployInfo.isNowait()) {
						noExit = false;
					}
					else if(DeployState.websphere_process_running)
					{
						noExit=false;
					}

				}


			}else{

				DeployState.websphere_process_starting = true;
				DeployState.websphere_process_running = true;
				SDLog.log(" Cluster "+ mdeployInfo.getCluster() + " already running ");

			}

		}catch (InstanceNotFoundException e) {
			e.printStackTrace();
			throw new DeployException(e);
		}catch (ConnectorException e) {
			e.printStackTrace();
			throw new DeployException(e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DeployException(e);
		}

	}

	public void stopCluster()
	throws DeployException{
		DeployState.websphere_process_stopping = false;
		DeployState.websphere_process_stopped = false;	

		try {
			registerClusterNotificationListener();
			boolean noExit = true;

			boolean isRunning = checkIfClusterRunning();

			if (isRunning){	
				String opName ="stop";
				String signature[] = {};
				String params[] = { };
				adminClient.invoke(cluster , opName, params, signature);
				SDLog.log(" Cluster Stopping = " + mdeployInfo.getCluster());
				while (noExit )
				{
					Thread.sleep(9000);

					logger.trace( "websphere_process_stopped" + DeployState.websphere_process_stopped);
					logger.trace("websphere_process_stopping" + DeployState.websphere_process_stopping);
					logger.trace("websphere_process_running" + DeployState.websphere_process_running);
					logger.trace("websphere_process_starting" + DeployState.websphere_process_starting);

					if (DeployState.websphere_process_stopped) {
						noExit = false;
					}
				}
			}else{
				DeployState.websphere_process_stopped = true;
				DeployState.websphere_process_stopping = true;
				SDLog.log(" Cluster "+ mdeployInfo.getCluster() + " already stopped ");
			}
		}catch (InstanceNotFoundException e) {
			e.printStackTrace();
			throw new DeployException(e);
		}catch (ConnectorException e) {
			e.printStackTrace();
			throw new DeployException(e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DeployException(e);
		}

	}

	public void restartCluster() throws DeployException{

		DeployState.websphere_process_starting = false;
		DeployState.websphere_process_running = false;
		DeployState.websphere_process_stopping = false;
		DeployState.websphere_process_stopped = false;
		
		registerClusterNotificationListener();
	
		boolean isRunningBeforeRestart = checkIfClusterRunning();
		if (!isRunningBeforeRestart ){

			DeployState.websphere_process_stopping = true;
			DeployState.websphere_process_stopped = true;
		}
		boolean noExit = true;
		try
		{	
			if(nodeExist==false)
			{
				noExit=false;
			}
			while (noExit )
			{
				Thread.sleep(9000);

				logger.trace( "websphere_process_stopped" + DeployState.websphere_process_stopped);
				logger.trace("websphere_process_stopping" + DeployState.websphere_process_stopping);
				logger.trace("websphere_process_running" + DeployState.websphere_process_running);
				logger.trace("websphere_process_starting" + DeployState.websphere_process_starting);
				// if restart is required 
				// and server is stopped
				// and is not already starting

				if ((DeployState.websphere_process_stopped) && (!DeployState.websphere_process_starting )){
					startCluster( );
				}
				// if restart is required 
				// and cluster was running before restart
				// and server is not stopped
				// and is not already starting

				if (isRunningBeforeRestart  && (!DeployState.websphere_process_stopping)&& (!(DeployState.websphere_process_stopped ))){
					stopCluster();
				}
				if(DeployState.websphere_process_stopped && DeployState.websphere_process_stopping && DeployState.websphere_process_starting && mdeployInfo.isNowait())
				{
					noExit = false;	
				}
				if (((DeployState.websphere_process_stopped && DeployState.websphere_process_stopping && DeployState.websphere_process_starting && DeployState.websphere_process_running))){
					noExit = false;	
				}

			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}	


	}

	private void registerClusterNotificationListener () throws DeployException
	{
		logger.trace("Entered the register notification method.");

		try
		{
			logger.trace("Getting reference to AppManagement MBean.");

			//            String query = "WebSphere:type=Cluster,*";
			String query = "WebSphere:*,type=NodeAgent";

			ObjectName ClusterManager = null;
			ObjectName queryName = new ObjectName (query);
			Set s = adminClient.queryNames(queryName, null);
			logger.trace(" Got reference to set of AppManagement MBean.");


			if (!s.isEmpty()){
				ClusterManager = (ObjectName)s.iterator().next();
				logger.trace(" AppManagement MBean found.");
			

			}else
			{
				if(mdeployInfo.isFailOnError())
				{
					throw new DeployException(new Exception("Nodegent is unavailable and hence restart cannot be processed."));
				}
				else
				{
					nodeExist=false;
					SDLog.log("Nodegent is unavailable and hence restart cannot be processed.");	
				}
			}

			if(nodeExist)
			{
			logger.trace("Adding Notification to AppManagemeng MBean.");
			adminClient.addNotificationListener(ClusterManager , deployNotificationListener, null, null);
			logger.trace("Added Notification to AppManagemeng MBean sucessfully.");
			}

		}catch (InstanceNotFoundException e){
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
}
