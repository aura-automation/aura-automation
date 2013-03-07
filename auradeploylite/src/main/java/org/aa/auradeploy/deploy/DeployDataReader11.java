/**	   

 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */

package org.aa.auradeploy.deploy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.aa.auradeploy.Constants.DeployData;
import org.aa.auradeploy.helper.Helper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.aa.common.exception.DeployException;

/**
 * @author Jatin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeployDataReader11 extends DeployDataReader10{
	private static final Log logger  = LogFactory.getLog(DeployDataReader.class);
	private Document dom;
	
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
	DeployDataReader11(Document newDom, DeployInfo deployInfo)
		throws IOException,SAXException,DeployException{
						
			super(getEntAppNode(newDom), deployInfo);
			dom = newDom;
	} 
	
	private static Node getEntAppNode(Document newDom)throws DeployException{
	    
		Node node = null;
		Node rootNode  = Helper.getNamedChildNode(newDom,Helper.getRootNodeNames(),false);
		Vector v = Helper.getNamedChildNode(rootNode,DeployData.ENTERPRISE_APPLICATION);
		if (v!=null){
		node = (Node)v.get(0);
		}
		return node;
	}
	
	public String getNoInstall(){
		String noInstall ="";
		NodeList nl = dom.getElementsByTagName(DeployData.NOINSTALL);
		
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.DEPLOYVERSION)){
				NamedNodeMap namedNodeMap = nl.item(k).getAttributes();
				noInstall= namedNodeMap.getNamedItem(DeployData.VALUE).getNodeValue();
			}
		}
		return noInstall;
		
	}


	/** 
	 * to get the Security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getRunAsRolesMapping()
	throws DeployException{
		
		return super.getRunAsRolesMapping() ;
	}
	

	
	/** 
	 * to get the Security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getRolesToGroup()
		throws DeployException{
		
		return super.getRolesToGroup() ;
	}
	

	/** 
	 * to get the Special security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getSpecialRolesMapping(){
		return super.getSpecialRolesMapping();
	}
	

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForNonMessaging(String ejbModuleName)
	throws DeployException{

		return super.getEJBJNDIForNonMessaging(ejbModuleName);
	}

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public String getCMPDSForEJBModule(String ejbModuleName)
	throws DeployException{

		return super.getCMPDSForEJBModule(ejbModuleName);

	}

	
	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForMessaging(String msgModuleName){

		return super.getEJBJNDIForMessaging(msgModuleName);
	}

	
	
	/** 
	 * to get the ejb references part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBRefs(String moduleName){

		return super.getEJBRefs(moduleName);
	}

	/** 
	 * to get the Resources ref part of wscp command  
	 * @return String 
	 */
	public Hashtable getResRefs(String moduleName,String ejbName)
	throws DeployException{
		
		return super.getResRefs(moduleName,ejbName);
		

	}

	/** 
	 * to get the Resources ref part of wscp command  
	 * @return String 
	 */
	public Hashtable getResEnvRefs(String moduleName)
	throws DeployException{
		
		return super.getResEnvRefs(moduleName);	
		
	}

	
	public Hashtable getWebModule()
	throws DeployException{
		
		return super.getWebModule();

	}

//TODO Add a method to get default CMP datadource for the module.
//TODO Change this method to return the EJB to CMP datasource mapping.	
	public Hashtable getEJBCMP2Datasource(String ejbModuleName)
	throws DeployException{

		return super.getEJBCMP2Datasource(ejbModuleName);
	
	}

	public Hashtable getEJBCMP1Datasource(String ejbModuleName)
	throws DeployException{

		return super.getEJBCMP1Datasource(ejbModuleName);
	
	}

		

	public Vector getAllModules(){

		return super.getAllModules();


	}

	public HashMap getWebModules(){
		
		return super.getWebModules();
		
	}

	public Vector getEJBModules(){

			return super.getEJBModules();
	}
	
	
	public Vector getAllResources()
	throws DeployException{

		return super.getAllResources();
			
	}

	public Vector getDataSources()
		throws DeployException{
	
		Vector resource = new Vector();
		Node rootNode  = Helper.getNamedChildNode(dom, Helper.getRootNodeNames() ,true); 
		Vector jdbcNl = Helper.getNamedChildNode(rootNode,DeployData.JDBCPROVIDER);
		
		for (int k=0 ; k< jdbcNl.size(); k++ ){
			if (((Node)jdbcNl.get(k)).getNodeName().equalsIgnoreCase(DeployData.JDBCPROVIDER)){
				Node jdbcNode = (Node)jdbcNl.get(k);
				NamedNodeMap jdbcNamedNodeMap = jdbcNode .getAttributes();

				logger.trace("JDBC Provider : " + jdbcNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (jdbcNode.getChildNodes().getLength()>0){
					NodeList datasourceNls = jdbcNode.getChildNodes();
					
					for (int i =0; i< datasourceNls.getLength(); i++){
						Hashtable hs = new Hashtable();

						if( datasourceNls.item(i).getNodeName().equalsIgnoreCase(DeployData.DATASOURCE)){
							NamedNodeMap datasourceNodeMap = datasourceNls.item(i).getAttributes();
							for (int x=0 ; x< datasourceNodeMap.getLength();x++){
								System.out.println(datasourceNodeMap.item(x).getNodeName() + " = " + datasourceNodeMap.item(x).getNodeValue());
								hs.put(datasourceNodeMap.item(x).getNodeName(), datasourceNodeMap.item(x).getNodeValue());
							}
							resource.add(hs);
						}
					}
				}
			}
		}

		
		return resource; 

	}

	public static void main(String args[]) throws IOException, SAXException,DeployException{
		logger.trace("Starting deploydata");
/**		DeployDataReader deployDataReader = new DeployDataReader("C:/jatin/eclipse/easyDeploy/deploydata/DeployData_template.xml");
//		DeployDataReader deployDataReader = new DeployDataReader("C:/eclipse/workspace/easyDeploy/deploydata/DeployData_template.xml");
		System.out.println(deployDataReader.getVersion());
		
//		Hashtable roles =  deployDataReader.getRolesToGroup();
//		Hashtable roles =  deployDataReader.getSpecialRolesMapping();
//		Hashtable roles =  deployDataReader.getEJBJNDIForNonMessaging("CLTestEJB.jar");
//		Hashtable roles =  deployDataReader.getEJBRefs("CLTestWeb.war");
//		Hashtable roles =  deployDataReader.getResRefs("CLTestWeb.war");
//		Hashtable roles =  deployDataReader.getResRefs("CLTestEJB.jar");
		Hashtable roles = deployDataReader.getWebModule();
		Vector modules = deployDataReader.getAllModules();
		Vector resources = deployDataReader.getAllResources();

		for (int i=0 ; i<resources .size();i++){
			System.out.println( resources .get(i));
		} 

		
		for (int i=0 ; i<modules .size();i++){
			System.out.println( modules.get(i));
		} 
		String[] keys =  (String[]) roles.keySet().toArray(new String[0]) ;
		
		for (int i=0; i<roles.size();i++){
			
			System.out.print( keys[i] + " ");
			System.out.println( roles.get(keys[i]));
			
		}

		//		deployDataReader.getSpecialRolesMapping();
		//deployDataReader.getAllModules();
		//deployDataReader.getAllResources();
**/
						
	}	
	
	public Vector getAllModuleNames(){
		
		return super.getAllModuleNames();
		
	}
	


	public Vector getLibraries()
	throws DeployException{
		
		return super.getLibraries(); 
	}

	public Integer getStartingWeight()
	throws DeployException{
		Integer weight = new Integer (1);
		
		
		return super.getStartingWeight();

	}
	
	public boolean getGenerateEJBDeployCode(){

		
		return super.getGenerateEJBDeployCode();

	}

	public Hashtable getEJBWebServiceBinding(String ejbModule,String webservice)
	throws DeployException{

		
		return super.getEJBWebServiceBinding(ejbModule,webservice);

	}

}


