package org.aa.auradeploy.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import org.aa.auradeploy.Constants.DeployValues;
import org.aa.auradeploy.Constants.JMXApplication;
import org.aa.auradeploy.deploy.DeployInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import com.ibm.websphere.management.application.AppNotification;

/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */

public class Helper {

	public static void displayStats(Vector stats){
		SDLog.log(" ####################################");
		SDLog.log(" ####################################");
		
		for (int i=0; i< stats.size();i++){
			DeployStats deployStats = (DeployStats)stats.get(i);
			SDLog.log(" EarNumber " + deployStats.getEarNumber());
			if (deployStats.getEarName()!= null)
			SDLog.log(" EarName " + deployStats.getEarName());
			if (deployStats.getAction()!= null)
				SDLog.log(" Action " + deployStats.getAction());
			if (deployStats.getOperation()!= null)
				SDLog.log(" Operation " + deployStats.getOperation());
			if (deployStats.getStartTime()!= null)
				SDLog.log(" StartTime " + deployStats.getStartTime());
			if (deployStats.getEndTime()!= null)
				SDLog.log(" EndTime " + deployStats.getEndTime());
			if (deployStats.getElapsedTime()!= null)
				SDLog.log(" ElapsedTime " + deployStats.getElapsedTime());
	
			SDLog.log(" ####################################");
			SDLog.log(" ####################################");
			
		}
	}
	
	public static DeployStats recordTime(String earName,int earNumber,
				long startTimeInMillis, long endTimeInMillis, String action, 
				String operation){
		DeployStats deployStats = new DeployStats ();
		

		long elapsed = endTimeInMillis  - startTimeInMillis;
	
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		// get difference time
		cal.setTimeInMillis(elapsed);
		String elapsedTime = dateFormat.format(cal.getTime());
		// get start time
		cal.setTimeInMillis(startTimeInMillis);
		String startTime = dateFormat.format(cal.getTime());
		// get end time
		cal.setTimeInMillis(endTimeInMillis);
		String endTime = dateFormat.format(cal.getTime());

/**		SDLog.log(" +++++++++++++++++++++++++++++ ");
		SDLog.log(" +++++++++++++++++++++++++++++ ");
		System.out.println( "elapsed " + elapsed );
		System.out.println( "elapsed " + elapsedTime );
		
		SDLog.log("action " + action);
		SDLog.log("earName " + earName);
		SDLog.log("EarNumber " + earNumber);
		SDLog.log("Operation " + operation);
		SDLog.log("StartTime " + startTime);
		SDLog.log("endTime " + endTime);
		SDLog.log("action " + action);
		SDLog.log(" +++++++++++++++++++++++++++++ ");
		SDLog.log(" +++++++++++++++++++++++++++++ "); **/
//		DeployStats deployStats = new DeployStats ();
		deployStats.setAction(action);
		deployStats.setEarName(earName);
		deployStats.setEarNumber(earNumber);
		deployStats.setOperation(operation);
		deployStats.setStartTimeInMillis(startTimeInMillis);
		deployStats.setEndTimeInMillis(endTimeInMillis);
		deployStats.setStartTime(startTime);
		deployStats.setEndTime(endTime);

		deployStats.setElapsedTime(elapsedTime); 
		
		return deployStats;

	}
	
	public static boolean isDeployDataPresent(DeployInfo deployInfo){
			return new File( deployInfo.getDeployDataLocation()).exists();
	}

	
	public static String getModuleNameFromURI(String uri){
		String moduleName = uri;
		if ((uri !=null) && (uri.trim().length()>0)){
		
			moduleName = uri.substring(0,uri.indexOf(","));
			
		}
	
		return moduleName;
	}
	
	public static boolean getBooleanFromString(String strboolean){
		if ((strboolean !=null) && (strboolean.trim().length()>0)){
			return (new Boolean(strboolean)).booleanValue();
		}else{
			return false;
		}
	
	}

	
	public static String getServerOrClusterFullString(DeployInfo deployInfo ){
		StringBuffer sb = new StringBuffer();
		if ((deployInfo.getCluster() != null) && (deployInfo.getCluster().trim().length()>0)){
			sb.append("WebSphere:cell=");
			sb.append(deployInfo.getCell());
			sb.append(",cluster=");
			sb.append(deployInfo.getCluster());
		}else{
			sb.append("WebSphere:cell=");
			sb.append(deployInfo.getCell());
			sb.append(",node=");
			sb.append(deployInfo.getNode());
			sb.append(",server=");
			sb.append(deployInfo.getServer());
		}
	
		return sb.toString();
	}
	
	public static String getWebServerFullString(DeployInfo deployInfo ){
		if ((deployInfo.getWebserver()!= null) && (deployInfo.getWebserver().trim().length()>0 )){ 
			StringBuffer sb = new StringBuffer();
			sb.append("WebSphere:cell=");
			sb.append(deployInfo.getCell());
			sb.append(",node=");
			sb.append(deployInfo.getNode());
			sb.append(",server=");
			sb.append(deployInfo.getWebserver());
			return sb.toString();
		}else{
			return new String("");
		}
	
		
	}

	
	private  static String getData(String userData,String name) {
		
		String value = "";
		
		if (userData.indexOf(name) >= 0){
			value = userData.substring(userData.indexOf(name) + name.length() + 1,userData.indexOf(",",userData.indexOf(name)));
		}
		return value;
	}

	public static boolean getDistStatus(Properties props) {
		
//		System.out.println( "props = " + props );
		
		boolean value = true;
		Hashtable statusTable = getDistForAllNodes(props.getProperty("AppDistributionAll"));;
		Set set = statusTable.keySet();
		String[] keys =  (String[])set.toArray(new String[0]); 
		
//		System.out.println( "status= " + statusTable.toString() );
		
		for (int i=0;i<keys.length;i++){
//			System.out.println("Loop Number " + i);
//			System.out.println(" Value of default value is " + value );
//			System.out.println( "statusTable.get(keys[i]) "  +  i + " = " + statusTable.get(keys[i]));
//			System.out.println("Boolean value" + (String)statusTable.get(keys[i])+ " "+  ((String)statusTable.get(keys[i])).startsWith("true"));
			if(((String)statusTable.get(keys[i])).trim().toLowerCase().startsWith("true")){
//				System.out.println("++++++++++ In new condition setting dist to false");
				value = true;
			}else if((value ) && (!((String)statusTable.get(keys[i])).equalsIgnoreCase("true"))){
//				System.out.println("++++++++++ In Old condition setting dist to false");	
				value = false;	
			}

		}
		return value;
		
	}

	public static boolean getDistStatusForUninstall(Properties props) {
		
		if (props.get(JMXApplication.SUBTASK).toString().equalsIgnoreCase("Cleanup") && props.get(JMXApplication.SUBTASK).toString().equalsIgnoreCase("Completed")){
			return true;
		}else
			return false;
		
	}

	public static Hashtable getAppUserData(String userData){
	
		String appNotificationtask = JMXApplication.APPNOTIFICATIONTASK;
		String taskstatus= JMXApplication.TASKSTATUS;
		String subtask= JMXApplication.SUBTASK;
		String subtaskStatus= JMXApplication.SUBTASKSTATUS;
		String properties= JMXApplication.PROPERTIES;
		String newProperties= "properties" ;
		String message= "message" ;

		//		String message= Constants.MESSAGE;

		Hashtable data = new Hashtable();
		data.put(appNotificationtask, getData(userData,appNotificationtask));	
		data.put(taskstatus, getData(userData,taskstatus));	
		data.put(subtask, getData(userData,subtask));	
		data.put(subtaskStatus, getData(userData,subtaskStatus));	
//		data.put(properties, getData(userData,newProperties));

//		data.put(message, getData(userData,message).toString());	
		
		return data;
	}
	
	public static Hashtable getAppUserData(Object userData){
		
			String appNotificationtask = JMXApplication.APPNOTIFICATIONTASK;
			String taskstatus= JMXApplication.TASKSTATUS;
			String subtask= JMXApplication.SUBTASK;
			String subtaskStatus= JMXApplication.SUBTASKSTATUS;
			String properties= JMXApplication.PROPERTIES;
			String newProperties= "properties" ;
			String message= "message" ;

			//		String message= Constants.MESSAGE;


		 	AppNotification ev = (AppNotification) userData;
			Hashtable data = new Hashtable();
		 	
			data.put(appNotificationtask, ev.taskName);
			data.put(taskstatus, ev.taskStatus);
			data.put(subtask, ev.subtaskName);
			data.put(subtaskStatus, ev.subtaskStatus );
			data.put(newProperties, ev.props);
			
			data.put(message, ev.message);

		 	
			return data;
		}


	
	public static Hashtable getDistributionData(String userData){
		
			String distribution = AppNotification.DISTRIBUTION;
			String distributiondone = AppNotification.DISTRIBUTION_DONE;
			String distributionnotdone = AppNotification.DISTRIBUTION_NOT_DONE;
			String distributionstatuscomposite = AppNotification.DISTRIBUTION_STATUS_COMPOSITE;
			String distributionnode = AppNotification.DISTRIBUTION_STATUS_NODE;
			String distributionunknow = AppNotification.DISTRIBUTION_UNKNOWN;

			Hashtable data = new Hashtable();
			data.put(distribution , getData(userData,distribution));	
			data.put(distributiondone , getData(userData,distributiondone));	
			data.put(distributionnode , getData(userData,distributionnode));	
			data.put(distributionnotdone , getData(userData,distributionstatuscomposite));	
			data.put(distributionstatuscomposite , getData(userData,distributionstatuscomposite));	
			data.put(distributionunknow , getData(userData,distributionunknow));	
			
			return data;
		}

	
	public static boolean isValid (String value){
	
		if ((value == null) || (value.trim().length() ==0)){
			return false;
		}else{
			return true;
		}
	}

	public static boolean isValidClusterOperation(String value){
		if ((value == DeployValues.CLUSTER_OPERATION_START) || (value == DeployValues.CLUSTER_OPERATION_STOP) ||  (value == DeployValues.CLUSTER_OPERATION_RESTART ) ){
			return false;
		}else{
			return true;
		}
		
	}
	
	public static String isServerOrCluster(DeployInfo deployInfo ){
		StringBuffer sb = new StringBuffer();
		if ((deployInfo.getCluster() != null) && (deployInfo.getCluster().trim().length()>0)){
			return DeployValues.CLUSTER;	
		}else{
			return DeployValues.SERVER;	
		}
	}
	
	public static Hashtable getDistForAllNodes (String distStatusAll){
		String status ="";
		String nodeStatus="";
		String node="";
		Hashtable statusTable = new Hashtable();
//		if (distStatusAll.indexOf("+")>0 ){
			for (int i=0;i<distStatusAll.length();i++){
				
				 node =distStatusAll.substring(i, distStatusAll.indexOf(",distribution",i));

				if (distStatusAll.indexOf("+",i) > 0){
					nodeStatus = distStatusAll.substring(distStatusAll.indexOf(",distribution", i) + ",distribution".length() + 1,distStatusAll.indexOf("+",i));
					i = distStatusAll.indexOf("+",i);
				}else{
					nodeStatus = distStatusAll.substring(distStatusAll.indexOf(",distribution", i) + ",distribution".length() + 1);
					i = distStatusAll.length();
				}
				
				String nodeName = node.substring(node.indexOf(",node") + ",node".length()+1 );
				
				
				if (nodeName.toLowerCase().indexOf("web")<0){
					statusTable.put(node,nodeStatus); 		
				}else{
				}
			}	
	/**	}else{ 
			if (distStatusAll.indexOf(",distribution") >= 0){
				status = distStatusAll.substring(distStatusAll.indexOf(",distribution") + ",distribution".length() + 1);
			}
		
		}**/
		
		Set set = statusTable.keySet();
		String[] keys =  (String[])set.toArray(new String[0]); 
		for (int i=0;i<keys.length;i++){
			System.out.println(keys[i] + " " + statusTable.get(keys[i])); 
		}
		return statusTable;
	}

	public static Vector getAllNodes (Properties props){

		String distNodes = props.getProperty("nodes");
		String node="";
		Vector nodes= new Vector();
		if (distNodes.indexOf("+")>0 ){
			for (int i=0;i<distNodes.length();i++){
				

				if (distNodes.indexOf("+",i) > 0){
					node = distNodes.substring(distNodes.indexOf(",node", i) + ",node".length() + 1,distNodes.indexOf("+",i));
					i = distNodes.indexOf("+",i);
				}else{
					node = distNodes.substring(distNodes.indexOf(",node", i) + ",node".length()+1 );
					i = distNodes.length();
				}
				if (node.toLowerCase().indexOf("web")<0){	
					nodes.add(node); 					
				}
			}	
		}
		else{ 
			if (distNodes.indexOf(",node") >= 0){
				node = distNodes.substring(distNodes.indexOf(",node") + ",node".length() + 1);
				if (node.toLowerCase().indexOf("web")<0){
					nodes.add(node);
				}
			}
		
		}
		
/**		Set set = statusTable.keySet();
		String[] keys =  (String[])set.toArray(new String[0]); 
		for (int i=0;i<keys.length;i++){
			System.out.println(keys[i] + " " + statusTable.get(keys[i])); 
		} **/ 
		return nodes;
	}
		

	public static File[] scanDir(String newsourceDir, boolean filePattern){
		File sourceDir = new File(newsourceDir);

		if (filePattern == false){
			File[] files = sourceDir.listFiles();
			return files;	

		}else{
			FilenameFilter earFileNameFilter = new EARFileNameFilter(); 
			File[] files = sourceDir.listFiles(earFileNameFilter);
			return files;	
		}
		
	}

		
	public static File[] scanDir(String newsourceDir){
		return scanDir(newsourceDir,false);
	}
	
	public static Vector getNamedChildNode(Node newNode, String name ){
		Vector v = new Vector();
		
		NodeList childNodeList = newNode.getChildNodes();
		if (childNodeList !=null){
			for (int i=0; i < childNodeList.getLength();i++){
				
				if (((Node)childNodeList.item(i)).getNodeName().equalsIgnoreCase(name)){
					
					v.add((Node)childNodeList.item(i));
					
				}
				
			}
		}
		return v;
	}

	public static Node getNamedChildNode(Document dom, Vector names , boolean isRootNode)
		throws DeployException{
		Node newNode = null;
		
		NodeList childNodeList = dom.getChildNodes();
		
		for (int i=0; i < childNodeList.getLength();i++){
			for (int j=0; j < names.size(); j++ ){
				if (((Node)childNodeList.item(i)).getNodeName().equalsIgnoreCase(names.get(j).toString())){
					
					newNode =  ((Node)childNodeList.item(i));
					
				}
			}
			
		}
		if ((newNode == null) && isRootNode){
			System.out.println("Root Node must be deploy");
			Exception e = new Exception("Root Node must be deploy");
			throw new DeployException(e);
		}
		return newNode ;
		
	}

	public static Vector getRootNodeNames(){
	    
	    Vector v = new Vector();
	    v.add("deploy");
	    v.add("landg-deploy-config");
	    return v;
	    
	}
	
	public static boolean isApplicationNameCorrect(String message, String applicationName){
	    boolean isCorrect = true;
	    String newApplicationName = "";
	    if (message.startsWith("ADMA5106I") || message.startsWith("ADMA5013I")|| message.startsWith("ADMA5014E")){
	        if(message.toUpperCase().indexOf("APPLICATION") >0 ){
	            int indexOfstartOfApplicationName = message.toUpperCase().indexOf("APPLICATION") + 12;
	            int indexOfEndOfApplicationName = message.indexOf(" " ,indexOfstartOfApplicationName);
	            newApplicationName = message.substring(indexOfstartOfApplicationName,indexOfEndOfApplicationName);
	            if (!applicationName.equals(newApplicationName )){
	                isCorrect = false;
	            }
	        }
	    }
	    
	    if (!isCorrect){
	        System.out.println("Ignoring the message as application name is " +newApplicationName );
	    }

	    return isCorrect;
	    
	}

	public static void main(String[] args){
		
//		Helper.getAppUserData("AppNotification:task=UninstallApplication, taskStatus=InProgress, subtask=DeleteSIEntryTask, subtaskStatus=Completed, properties={appname=WasAdmin, nodes=WebSphere:cell=Avatar1Cell01,node=Avatar1Node01}, message=ADMA5104I: The server index entry for WebSphere:cell=Avatar1Cell01,node=Avatar1Node01 is updated successfully.");
//		Helper.getDistStatus("WebSphere:cell=Avatar1Cell01,node=Avatar1Node01,distribution=true");
		
//		Helper.getDistForAllNodes("WebSphere:cell=Avatar1Cell01,node=Avatar1Node02," +
//				"distribution=false+WebSphere:cell=Avatar1Cell01,node=Avatar1Node01," +
//				"distribution=true+WebSphere:cell=Avatar1Cell01,node=Avatar1Web03," +
//				"distribution=unknown"); 

//		Helper.getDistForAllNodes("WebSphere:cell=Avatar1Cell01,node=Avatar1Node02,distribution=false");
//		Properties props = new Properties();
		// props.put("nodes" , "WebSphere:cell=Ce53,node=No53-kwtwas53");
	//	Helper.getAllNodes(props);
	    
		Helper.isApplicationNameCorrect("ADMA5013I: Application Trade3 installed successfully.","Trade3");
		Helper.isApplicationNameCorrect("ADMA5106I: Application Trade3 uninstalled successfully.","Trade3");
		Helper.isApplicationNameCorrect("ADMA5014E: The installation of application Trade3 failed.","Trade3");
		
		 
	}
	


}
