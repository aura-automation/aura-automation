package com.apartech.auradeploy.deploy;

import java.util.Hashtable;

import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigService;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.exception.ConfigServiceException;
import com.ibm.websphere.management.exception.ConnectorException;

/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
public class Server {
	
	public boolean ClusterOrServerExists(DeployInfo deploInfo,AdminClient adminClient,ConfigService configService,Session session ){
		boolean exists = false;	



		try{
			
             ObjectName ApplicationManager = null;
	         ObjectName server1 = ConfigServiceHelper.createObjectName(null, "ApplicationServer", "server1");
	         ObjectName[] matches = configService.queryConfigObjects(session, null, server1, null);
//	         server1 = matches[0];   // use the first server found 

	         ObjectName Cluster = ConfigServiceHelper.createObjectName(null, "ServerCluster", "Test-CL");
	         ObjectName[] Clusters  = configService.queryConfigObjects(session, null, Cluster, null);

	         ObjectName dataSource = ConfigServiceHelper.createObjectName(null, "DataSource" );
	         ObjectName[] dataSources = configService.queryConfigObjects(session, null, dataSource, null);
			
			Hashtable hs = dataSources[1].getKeyPropertyList();
			String[] keys = (String [])hs.keySet().toArray(new String[0]) ;
			for (int i=0 ; i < keys.length  ; i++){
				System.out.println( keys[i].toString() );
			}

			
			System.out.println(" Number if Clusters found " + Clusters.length);
			System.out.println(" Number if servers found " + matches.length);
			System.out.println(" Number if datasource found " + dataSources.length);

		}catch(ConnectorException e){
			e.printStackTrace();
		}catch(ConfigServiceException e){		
			e.printStackTrace();
		}
		return exists;
	} 

}

