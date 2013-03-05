package com.apartech.auradeploy.deploy;

/*
 * Created on 19-Aug-2003
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.apartech.auradeploy.Constants.DeployData;
import com.apartech.common.exception.DeployException;

/**
 * @author JATIN
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeployDataReader20 {
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
	DeployDataReader20(Document newDom)
		throws IOException,SAXException{
			dom = newDom;
	} 
	

	/** 
	 * to get the Security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getRolesToGroup(){
		
		Hashtable mapRoleToUser = new Hashtable();
		NodeList nl = dom.getElementsByTagName(DeployData.ROLE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ROLE)){
				Node node = nl.item(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("Role : " + roleNamedNodeMap.getNamedItem(DeployData.ROLE_NAME).getNodeValue());

				if (node.getChildNodes().getLength()>0){
					NodeList childNls = node.getChildNodes();
					StringBuffer sb = new StringBuffer();
					for (int i =0; i< childNls.getLength(); i++){
						if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.GROUP)){;
						NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
						logger.trace("Group : " + namedNodeMap.getNamedItem(DeployData.GROUP_NAME).getNodeValue());
						sb.append(namedNodeMap.getNamedItem(DeployData.GROUP_NAME).getNodeValue()); 
						sb.append("|"); 
						} 
					}
					if (sb.length()>2){
						sb = new StringBuffer( sb.toString().substring(0,((sb.toString().length())-1)));
					}
					mapRoleToUser.put("'" + roleNamedNodeMap.getNamedItem(DeployData.ROLE_NAME).getNodeValue() + "'", sb.toString());

				}
			}
		}
		return mapRoleToUser;
	}
	

	/** 
	 * to get the Special security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getSpecialRolesMapping(){
		Hashtable specialRolesMapping = new Hashtable();
		NodeList nl = dom.getElementsByTagName(DeployData.SPECIAL_ROLE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.SPECIAL_ROLE)){
				Node node = nl.item(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("Role : " + roleNamedNodeMap.getNamedItem(DeployData.SPECIAL_ROLE_NAME).getNodeValue());
				StringBuffer sb = new StringBuffer();
				if (node.getChildNodes().getLength()>0){
					NodeList childNls = node.getChildNodes();
					for (int i =0; i< childNls.getLength(); i++){
						if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.GROUP)){;
						NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
						sb.append(namedNodeMap.getNamedItem(DeployData.GROUP_NAME).getNodeValue());
						sb.append("|");
						logger.trace("    Group : " + namedNodeMap.getNamedItem(DeployData.GROUP_NAME).getNodeValue());
						} 
					}
				if (sb.length()>2){
					sb = new StringBuffer( sb.toString().substring(0,((sb.toString().length())-1)));
				}
				specialRolesMapping.put("'" + roleNamedNodeMap.getNamedItem(DeployData.ROLE_NAME).getNodeValue() + "'",sb.toString()) ;
				}
			}

		}
		return specialRolesMapping;
	}
	

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForNonMessaging(String ejbModuleName){

		Hashtable EJBJNDIForNonMessaging = new Hashtable();		
		NodeList nl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = nl.item(k);
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue());
				if (ejbNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue().equalsIgnoreCase(ejbModuleName)){
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN)){;
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue());
								logger.trace("    JNDI Name : " + namedNodeMap.getNamedItem(DeployData.JNDI_NAME).getNodeValue());
								EJBJNDIForNonMessaging.put(namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue(),namedNodeMap.getNamedItem(DeployData.JNDI_NAME).getNodeValue());		
							} 
						}
					}
				}
			}

		}

		return EJBJNDIForNonMessaging;
	}

	/** 
	 * to get the ejb references part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBRefs(String moduleName){

		NodeList nl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		Hashtable EJBReftoEJB = new Hashtable();
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = nl.item(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue().equalsIgnoreCase(moduleName)){								
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN)){;
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue());
								NodeList EJBRefsChild = childNls.item(i).getChildNodes();
								for(int j=0 ;j < EJBRefsChild.getLength();j++){
									if (EJBRefsChild.item(j).getNodeName().equalsIgnoreCase("ejb-ref")){
										NamedNodeMap EJBRefNamedNodeMap = EJBRefsChild.item(j).getAttributes();
										logger.trace("        EJB REF Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue());
										logger.trace("        EJB EXT JNDI Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.EJB_EXT_JNDI_NAME).getNodeValue());
										EJBReftoEJB.put(EJBRefNamedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue(),EJBRefNamedNodeMap.getNamedItem(DeployData.EJB_EXT_JNDI_NAME).getNodeValue());
									}
									
								}
							} 
						}
					}
				}
			}

		}









		NodeList webNl = dom.getElementsByTagName(DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.getLength(); a++ ){
			if (webNl.item(a).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = webNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue().equalsIgnoreCase(moduleName)){				
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int b =0; b< childNls.getLength(); b++){
							if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.EJB_REF)){;
								NamedNodeMap namedNodeMap = childNls.item(b).getAttributes();
								logger.trace("    EJB REF Name : " + namedNodeMap.getNamedItem(DeployData.EJB_REF_NAME).getNodeValue());
								logger.trace("    EXT JNDI Name : " + namedNodeMap.getNamedItem(DeployData.EJB_EXT_JNDI_NAME).getNodeValue());
								EJBReftoEJB.put(namedNodeMap.getNamedItem(DeployData.EJB_REF_NAME).getNodeValue(),namedNodeMap.getNamedItem(DeployData.EJB_EXT_JNDI_NAME).getNodeValue())	;
	
	
							} 
						}
					}
				}
			}
		}




		return EJBReftoEJB;
	}

	/** 
	 * to get the Resources ref part of wscp command  
	 * @return String 
	 */
	public Hashtable getResRefs(String moduleName,String ejbName){
		Hashtable mapResReftoEJB = new Hashtable();

		NodeList nl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = nl.item(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();

				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue().equalsIgnoreCase(moduleName)){				
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN )){;
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue());
								if (namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue().equalsIgnoreCase(ejbName)){
									NodeList EJBRefsChild = childNls.item(i).getChildNodes();
									for(int j=0 ;j < EJBRefsChild.getLength();j++){
										if (EJBRefsChild.item(j).getNodeName().equalsIgnoreCase(DeployData.RES_REF)){
											NamedNodeMap EJBRefNamedNodeMap = EJBRefsChild.item(j).getAttributes();
											logger.trace("        RES REF Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.RES_REF_NAME).getNodeValue());
											logger.trace("        RES EXT JNDI Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue()); 					
											mapResReftoEJB.put(EJBRefNamedNodeMap.getNamedItem(DeployData.RES_REF_NAME).getNodeValue(),EJBRefNamedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue());
											}
										
										}
									}
								}
							}
						}
					}
				}
			}


			NodeList webNl = dom.getElementsByTagName(DeployData.WEB_MODULE);
			for (int a=0 ; a< webNl.getLength(); a++ ){
				if (webNl.item(a).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
					Node node = webNl.item(a);
					NamedNodeMap webNamedNodeMap = node.getAttributes();
	
					logger.trace("Web Module : " + webNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue());
	
					if (((String)webNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue()).equalsIgnoreCase(moduleName)){				
						if (node.getChildNodes().getLength()>0){
							NodeList childNls = node.getChildNodes();
							for (int b =0; b< childNls.getLength(); b++){
								if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.RES_REF)){;
									NamedNodeMap namedNodeMap = childNls.item(b).getAttributes();
									logger.trace("    RES REF Name : " + namedNodeMap.getNamedItem(DeployData.RES_REF_NAME).getNodeValue());
									logger.trace("    RES JNDI Name : " + namedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue());
									mapResReftoEJB.put(namedNodeMap.getNamedItem(DeployData.RES_REF_NAME).getNodeValue(),namedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue());
		
								} 
							}
						}
					}
				}
			}

		return mapResReftoEJB;
	}

	
	/** 
	 * to get the Resources env refs part of wscp command  
	 * @return String 
	 */
	public Hashtable getResEnvRefs(String moduleName){
		Hashtable mapResReftoEJB = new Hashtable();

// TODO: Complete this method		
		return mapResReftoEJB;
	}
	
	
	public Hashtable getWebModule(){
		
/**		Hashtable mapWebModToVHTask = new Hashtable();
		NodeList webNl = dom.getElementsByTagName("web-moule");
		for (int a=0 ; a< webNl.getLength(); a++ ){
			if (webNl.item(a).getNodeName().equalsIgnoreCase("web-moule")){
				Node node = webNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem("war").getNodeValue());
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem("virtual-host").getNodeValue());
				mapWebModToVHTask.put(roleNamedNodeMap.getNamedItem("war").getNodeValue()+",WEB-INF/web.xml",roleNamedNodeMap.getNamedItem("virtual-host").getNodeValue()) ;
			}
		}
		return mapWebModToVHTask;		**/

		
		Hashtable mapWebModToVHTask = new Hashtable();
		NodeList webNl = dom.getElementsByTagName(DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.getLength(); a++ ){
			if (webNl.item(a).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = webNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue());
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.VIRTUAL_HOST).getNodeValue());
				mapWebModToVHTask.put(roleNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue()+",WEB-INF/web.xml",roleNamedNodeMap.getNamedItem(DeployData.VIRTUAL_HOST).getNodeValue()) ;
			}
		}
		return mapWebModToVHTask;		
	}


	public String getEJBCMP2Datasource(String ejbModuleName){
		String datasource = null;
		Hashtable EJBCMPDatasource = new Hashtable();		
		NodeList nl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = nl.item(k);
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue());
				if (ejbNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue().equalsIgnoreCase(ejbModuleName)){
					
					Node moduleNode = ejbNamedNodeMap.getNamedItem(DeployData.JAR);
					if  (moduleNode.getAttributes().getNamedItem(DeployData.DEFAULT_DATASOURCE) != null){
						logger.trace("Default Datasource: " + moduleNode.getAttributes().getNamedItem(DeployData.DEFAULT_DATASOURCE).getNodeValue()) ;

						datasource  = moduleNode.getAttributes().getNamedItem(DeployData.DEFAULT_DATASOURCE).getNodeValue() ;
					
					}
				}
			}

		}

		return datasource ;
	}

	public String getEJBCMP1Datasource(String ejbModuleName){
		String datasource = null;
		Hashtable EJBCMPDatasource = new Hashtable();		
		NodeList nl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = nl.item(k);
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue());
				if (ejbNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue().equalsIgnoreCase(ejbModuleName)){
					
					Node moduleNode = ejbNamedNodeMap.getNamedItem(DeployData.JAR);
					if  (moduleNode.getAttributes().getNamedItem(DeployData.DEFAULT_DATASOURCE) != null){
						logger.trace("Default Datasource: " + moduleNode.getAttributes().getNamedItem(DeployData.DEFAULT_DATASOURCE).getNodeValue()) ;

						datasource  = moduleNode.getAttributes().getNamedItem(DeployData.DEFAULT_DATASOURCE).getNodeValue() ;
					
					}
				}
			}

		}

		return datasource ;
	}
		

	public Vector getAllModules(){
		Vector modules = new Vector();
		
		NodeList webNl = dom.getElementsByTagName(DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.getLength(); a++ ){
			if (webNl.item(a).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = webNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue()+"+WEB-INF/web.xml") ;
			}
		}
		
		NodeList ejbNl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int a=0 ; a< ejbNl.getLength(); a++ ){
			if (ejbNl.item(a).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = ejbNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue()+"+META-INF/ejb-jar.xml") ;
			}
		}		
		return modules;		
	}

	public HashMap getWebModules(){
		HashMap modules = new HashMap();
		
		NodeList webNl = dom.getElementsByTagName(DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.getLength(); a++ ){
			if (webNl.item(a).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = webNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
			
				String webserver = "";
				if (roleNamedNodeMap.getNamedItem(DeployData.WEBSERVER).getNodeValue()!= null){
					webserver = roleNamedNodeMap.getNamedItem(DeployData.WEBSERVER).getNodeValue();
				}
						
				modules.put(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+"+WEB-INF/web.xml",webserver) ;
			}
		}
				return modules;		
	}

	public Vector getEJBModules(){
		Vector modules = new Vector();
		
		NodeList ejbNl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int a=0 ; a< ejbNl.getLength(); a++ ){
			if (ejbNl.item(a).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = ejbNl.item(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue()+"+META-INF/ejb-jar.xml") ;
			}
		}		
		return modules;		
	}


	
	public Vector getAllResources(){
		Vector resource = new Vector();

		NodeList nl = dom.getElementsByTagName(DeployData.ENTERPRISE_MODULE);
		for (int k=0 ; k< nl.getLength(); k++ ){
			if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_MODULE)){
				Node node = nl.item(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();

				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.JAR).getNodeValue());
				if (node.getChildNodes().getLength()>0){
					NodeList childNls = node.getChildNodes();
					for (int i =0; i< childNls.getLength(); i++){
						if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN )){;
							NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
							NodeList EJBRefsChild = childNls.item(i).getChildNodes();
							for(int j=0 ;j < EJBRefsChild.getLength();j++){
								if (EJBRefsChild.item(j).getNodeName().equalsIgnoreCase(DeployData.RES_REF)){
									NamedNodeMap EJBRefNamedNodeMap = EJBRefsChild.item(j).getAttributes();
									logger.trace("        RES EXT JNDI Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue()); 					
									resource.add(EJBRefNamedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue());
									}
								
								}
							} 
						}
					}
				}
			}


			NodeList webNl = dom.getElementsByTagName(DeployData.WEB_MODULE);
			for (int a=0 ; a< webNl.getLength(); a++ ){
				if (webNl.item(a).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
					Node node = webNl.item(a);
					NamedNodeMap webNamedNodeMap = node.getAttributes();
	
					logger.trace("Web Module : " + webNamedNodeMap.getNamedItem(DeployData.WAR).getNodeValue());
	
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int b =0; b< childNls.getLength(); b++){
							if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.RES_REF)){;
								NamedNodeMap namedNodeMap = childNls.item(b).getAttributes();
								logger.trace("    RES JNDI Name : " + namedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue());
								resource.add(namedNodeMap.getNamedItem(DeployData.RES_EXT_JNDI_NAME).getNodeValue());
	
							} 
						}
					}
				}
			}

		
		return resource; 
	}



	public static void main(String args[]) throws IOException, SAXException,DeployException{
	/**	logger.trace("Starting deploydata");
		DeployDataReader deployDataReader = new DeployDataReader("C:/IBM/WSAD/Workspace/easyDeploy/deploydata/deploydata.xml");
		Vector resources =  deployDataReader.getAllResources(); 
		for (int i=0;i<resources.size();i++){
			System.out.println((String)resources.get(i));
		}		
**/
//		logger.trace(deployDataReader.getRoles());
//		logger.trace(deployDataReader.getSpecialRoles());
//		logger.trace(deployDataReader.getEJBJNDICmd());
//		logger.trace(deployDataReader.getEJBRefs());
//.		logger.trace(deployDataReader.getResRefs());

						
	}	
	
}


