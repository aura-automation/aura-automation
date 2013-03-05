package com.apartech.auradeploy.deploy;

/*
 * Created on 19-Aug-2003
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.apartech.auradeploy.Constants.DeployData;
import com.apartech.auradeploy.helper.Helper;
import com.apartech.common.exception.DeployException;

/**
 * @author JATIN
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeployDataReader {
	private static final Log logger  = LogFactory.getLog(DeployDataReader.class);
	private Document dom;
	private String version ;
	private DeployDataReader10 deployDataReader10 ;
	private DeployDataReader20 deployDataReader20 ;
	private DeployDataReader11 deployDataReader11 ;
	
	public Document getDocument(){
		return dom;
	}

	public void setDocument(Document newDom){
		dom = newDom;
	}

	/**
	 * Contructor to pass the deploy info file. This will load the file 
	 * in memmory and create and DOM object.
	 * @param deployInfoFile
	 */
		DeployDataReader(String deploydata, DeployInfo deployInfo)
		throws IOException,SAXException,DeployException{
		try{
			DOMParser parser = new DOMParser();
			parser.parse(deploydata);
			dom = parser.getDocument();
			
			version = getVersion();
			if (version.equalsIgnoreCase(DeployData.VERSION20)){
				deployDataReader20 = new DeployDataReader20(dom);
			}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
				deployDataReader11 = new DeployDataReader11(dom,deployInfo);
			}else{
				deployDataReader10 = new DeployDataReader10(Helper.getNamedChildNode(dom,Helper.getRootNodeNames() ,true),deployInfo);
			}
		}
		catch(SAXException e){
			e.printStackTrace();
			throw new DeployException(e );
		}
		
		catch(IOException e){
			e.printStackTrace();
			throw new DeployException(e );
		}


	} 

	public String getVersion(){
		String version ="1.0.0";
		NodeList nl = dom.getElementsByTagName(DeployData.DEPLOYVERSION);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.DEPLOYVERSION)){
				NamedNodeMap namedNodeMap = nl.item(k).getAttributes();
				version = namedNodeMap.getNamedItem(DeployData.VALUE).getNodeValue();
			}
		}
		return version;
		
	}

	/** 
	 * to get the Security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getRolesToGroup()
		throws DeployException{
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getRolesToGroup();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getRolesToGroup();
		}else{
			return deployDataReader20.getRolesToGroup();
		}
	}

	public boolean getEnable(){
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEnable();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEnable();
		}else{
			// TODO:Change to version 2 
			return deployDataReader10.getEnable();
		}
	}
	
	/** 
	 * to get the Special security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getSpecialRolesMapping(){

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getSpecialRolesMapping();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getSpecialRolesMapping();

		}else {
			return deployDataReader20.getSpecialRolesMapping();
		}
	}

	/** 
	 * to get the Special security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getRunAsRolesMapping()
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getRunAsRolesMapping();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getRunAsRolesMapping();

		}else {
			// TODO:Convert to new version 
			return deployDataReader10.getRunAsRolesMapping();
		}
	} 


	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForNonMessaging(String ejbModuleName)
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBJNDIForNonMessaging(ejbModuleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBJNDIForNonMessaging(ejbModuleName);
		}else {
				return deployDataReader20.getEJBJNDIForNonMessaging(ejbModuleName);
		}

	}

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForMessaging(String ejbModuleName){

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBJNDIForMessaging(ejbModuleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBJNDIForMessaging(ejbModuleName);
		}else {
// TODO: Complete the deploydata version 2 for EJBJNDIForMessaging 			
			return deployDataReader10.getEJBJNDIForMessaging(ejbModuleName);
		}

	}
	
	/** 
	 * to get the ejb references part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBRefs(String moduleName){


		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBRefs(moduleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBRefs(moduleName);
		}else {
			return deployDataReader20.getEJBRefs(moduleName);
		}

	}

	/** 
	 * to get the Resources ref part of wscp command  
	 * @return String 
	 */
	public Hashtable getResRefs(String moduleName, String EJBName)
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getResRefs(moduleName, EJBName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getResRefs(moduleName, EJBName);
		}else {
			return deployDataReader20.getResRefs(moduleName,EJBName);
		}
	}

	/** 
	 * to get the Resources ref part of wscp command  
	 * @return String 
	 */
	public Hashtable getResEnvRefs(String moduleName)
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getResEnvRefs(moduleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
				return deployDataReader11.getResEnvRefs(moduleName);
		}else {
			return deployDataReader20.getResEnvRefs(moduleName);
		}
	}

	public Hashtable getWebModule()
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getWebModule();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getWebModule();
		}else {
			return deployDataReader20.getWebModule();
		}

		
	}


	public Hashtable getEJBCMP1Datasource(String ejbModuleName)
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBCMP1Datasource(ejbModuleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBCMP1Datasource(ejbModuleName);
		}else {
			// TODO: changed to deploydata version 2 
			return deployDataReader10.getEJBCMP1Datasource(ejbModuleName);
		}
	}
	
	
	public String getCMPDSForEJBModule(String ejbModuleName)
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getCMPDSForEJBModule(ejbModuleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getCMPDSForEJBModule(ejbModuleName);
		}else {
			// TODO: changed to deploydata version 2 
			return deployDataReader10.getCMPDSForEJBModule(ejbModuleName);
		}
	}
	
	public Hashtable getEJBCMP2Datasource(String ejbModuleName)
	throws DeployException{

		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBCMP2Datasource(ejbModuleName);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBCMP2Datasource(ejbModuleName);
		}else {
			// TODO: changed to deploydata version 2 
			return deployDataReader10.getEJBCMP2Datasource(ejbModuleName);
		}
	}
		

	public Vector getAllModules(){
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getAllModules();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getAllModules();
		}else {
			return deployDataReader20.getAllModules();
		}
	}

	public HashMap getWebModules(){
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getWebModules();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getWebModules();

		}else {
			return deployDataReader20.getWebModules();
		}
	}

	public Vector getEJBModules(){
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBModules();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBModules();
		}else {
			return deployDataReader20.getEJBModules();
		}
	}
	
	
	
	public Vector getAllModuleNames(){
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getAllModuleNames();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getAllModuleNames();
		}else {
			return deployDataReader20.getAllModules();
		}
	}

	
	public Vector getAllResources()
	throws DeployException{
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getAllResources();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getAllResources();
		}else {
			return deployDataReader20.getAllResources();
		}
		
	}

	public Vector getDataSources()
		throws DeployException{
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			System.err.println("Version "  + DeployData.VERSION10 + " does not support the resource creation" );
			return null;
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getDataSources();
		}else {
			System.err.println("Version "  + DeployData.VERSION20 + " does not support the resource creation" );

			return null;
		}
		
	}

	private Vector getFiles(String dir ){
		Vector xmlFile = new Vector();
		File file = new File(dir);
		File[] files  = file.listFiles(); 
		for (int i =0; i < files.length;i++ ){
			if (files[i].isDirectory()){
				scan(files[i],xmlFile);
			}else if (files[i].getName().endsWith("xml")){
				xmlFile.add(files[i]);
			}

		}
		

		return xmlFile ;
	}

	private Vector scan(File dir, Vector xmlFile){
		File[] files  = dir.listFiles(); 
		for (int i =0; i < files.length;i++ ){
			if (files[i].getName().endsWith("xml")){
				xmlFile.add(files[i]);
			}
		}
		
		
		return xmlFile;
	}	

	public static void main(String args[]) throws IOException, SAXException,DeployException{
		logger.trace("Starting deploydata");
		
		
		
//		DeployDataReader deployDataReader = new DeployDataReader("C:/ANT/SlickDeploy/deploydata/trade3-DeployDatav2.xml");
		
/**		DeployDataReader deployDataReader = new DeployDataReader("C:/ANT/SlickDeploy/deploydata/Clex-DeployData.xml");
		Hashtable hs =  deployDataReader.getResRefs ("CLEX_EJB.jar","UserController");

		String[] keys = (String[]) hs.keySet().toArray(new String[0]);
		for (int y = 0 ; y < keys.length; y ++){
			System.out.println ( keys[y] + " = " + hs.get(keys[y]));
		}
**/
/**		String[] keys = (String[]) hs.keySet().toArray(new String[0]);
		for (int y = 0 ; y < keys.length; y ++){
			System.out.println ( keys[y] + " = " + hs.get(keys[y]));
		}
		
		Vector libs =  deployDataReader.getLibraries();
		for (int i=0;i<libs.size();i++){
			System.out.println(libs.get(i));
		}
		System.out.println( " Starting Weight of the application is " + deployDataReader.getStartingWeight());
		deployDataReader.getAllModuleNames();
		deployDataReader.getAllResources();
		Vector v =  deployDataReader.getDataSources();
		System.out.println( v.size());
		Vector ejbModules =  deployDataReader.getAllModuleNames();
		for (int x=0;x< ejbModules.size();x++ ){
			System.out.println ( ejbModules.get(x).toString());

			Hashtable ht = deployDataReader.getEJBJNDIForNonMessaging(ejbModules.get(x).toString() );
			String[] keys = (String[]) ht.keySet().toArray(new String[0]);
			for (int y = 0 ; y < keys.length; y ++){
				System.out.println ( keys[y] + " = " + ht.get(keys[y]));
			}
//			 System.out.println(" EJB Module " + ejbModules.get(x).toString() + + deployDataReader.getCMPDSForEJBModule(ejbModules.get(x).toString());	
		}
		
		for (int i=0;i<libs.size();i++){
			System.out.println(libs.get(i));
		} **/

		/**		DeployDataReader deployDataReader = new DeployDataReader("C:/jatin/eclipse/easyDeploy/deploydata/deploydata.xml");
		Vector  v = deployDataReader.getFiles("C:/jatin/eclipse/easyDeploy/deploydata/landg");
		for (int i=0;i<v.size();i++){
			if (((File)v.get(i)).getName().equalsIgnoreCase("LEC4-ProtectionOnline-DeployData.xml")){
			System.out.println("*********************************");
			System.out.println("*********************************");
			System.out.println(((File)v.get(i)).getAbsoluteFile());
			System.out.println("*********************************");
			System.out.println("*********************************");

			System.out.println(((File)v.get(i)).getAbsoluteFile());
			DeployDataReader deployDataReader1 = new DeployDataReader(((File)v.get(i)).getAbsoluteFile().toString());
			
			deployDataReader1.getRolesToGroup();
			deployDataReader1.getSpecialRolesMapping();
			
			Vector modules =  deployDataReader1.getAllModuleNames();
			
			for (int x=0;x < modules.size();x++){
				
				deployDataReader1.getEJBCMPDatasource(modules.get(x).toString());
				deployDataReader1.getEJBJNDIForNonMessaging(modules.get(x).toString());
				deployDataReader1.getEJBRefs(modules.get(x).toString());
				deployDataReader1.getResRefs(modules.get(x).toString());
				deployDataReader1.getEJBCMPDatasource(modules.get(x).toString());
				 
			}
			deployDataReader1.getWebModule();
			deployDataReader1.getAllResources();
			}
			
		} **/
//		System.out.println(deployDataReader.getVersion());
		

						
	}	
	
	public Vector getLibraries()
	throws DeployException{
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getLibraries();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getLibraries();
		}else {
//			 TODO:Change to version 2
			return deployDataReader10.getLibraries();
		}

		
		
	}

	public Integer getStartingWeight()
	throws DeployException{
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getStartingWeight();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getStartingWeight();
		}else {
			// TODO:Change to version 2 
			return deployDataReader10.getStartingWeight();
		}
		
		
	}

	public boolean getGenerateEJBDeployCode(){
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getGenerateEJBDeployCode();
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getGenerateEJBDeployCode();
		}else {
			// TODO:Change to version 2 
			return deployDataReader10.getGenerateEJBDeployCode();
		}
	}

	public Hashtable getEJBWebServiceBinding(String ejbModuleName,String webservice)
	throws DeployException{
		if (version.equalsIgnoreCase(DeployData.VERSION10)){
			return deployDataReader10.getEJBWebServiceBinding(ejbModuleName,webservice);
		}else if (version.equalsIgnoreCase(DeployData.VERSION11)){
			return deployDataReader11.getEJBWebServiceBinding(ejbModuleName,webservice);
		}else {
			// TODO:Change to version 2 
			return deployDataReader10.getEJBWebServiceBinding(ejbModuleName,webservice);
		}
	}

}

