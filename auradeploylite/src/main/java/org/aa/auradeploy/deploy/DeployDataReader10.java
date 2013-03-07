package org.aa.auradeploy.deploy;

/*
 * Created on 19-Aug-2003
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.aa.auradeploy.Constants.DeployData;
import org.aa.auradeploy.helper.Helper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;
import org.aa.common.properties.helper.PropertyHelper;

/**
 * @author JATIN
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */




public class DeployDataReader10 {
	private static final Log logger  = LogFactory.getLog(DeployDataReader.class);
	private Node appNode;
	private DeployInfo deployInfo;	
	public Node getAppNodeList(){
		return appNode;
	}

	public void setDocument(Node newDom){
		appNode =newDom;
	}

	/**
	 * Contructor to pass the deploy info file. This will load the file 
	 * in memmory and create and DOM object.
	 * @param deployInfoFile
	 */
	DeployDataReader10(Node newNode,DeployInfo myDeployInfo) 
		throws IOException,SAXException{
			deployInfo	= myDeployInfo;
			appNode = newNode;

	} 
	
	public String getNoInstall(){
		String noInstall ="";
		Vector noInstallNodes =  Helper.getNamedChildNode(appNode,DeployData.NOINSTALL);
//		NodeList nl = appNode.getElementsByTagName(DeployData.NOINSTALL);
		
		for (int k=0 ; k< noInstallNodes.size(); k++ ){
			if (((Node)noInstallNodes.get(k)).getNodeName().equalsIgnoreCase(DeployData.DEPLOYVERSION)){
				NamedNodeMap namedNodeMap = ((Node)noInstallNodes.get(k)).getAttributes();
				noInstall= namedNodeMap.getNamedItem(DeployData.VALUE).getNodeValue();
			}
		}
		return noInstall;
		
	}


	public Hashtable getRunAsRolesMapping()
		throws DeployException{

		Hashtable mapRunAsRoleToUser = new Hashtable();
		
		// start with ROLE_BINDING
		Vector roleBnd = Helper.getNamedChildNode(appNode , DeployData.RUN_AS_ROLE_BINDING);
		for (int m =0;m<roleBnd.size();m++){
			
			NodeList nl = ((Node)roleBnd.get(m)).getChildNodes();
			
			for (int k=0 ; k< nl.getLength(); k++ ){
				// continue with the role node
				if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ROLE)){
					Node node = nl.item(k);
					NamedNodeMap roleNamedNodeMap = node.getAttributes();
					
					String userName = PropertyHelper.replaceVariable( roleNamedNodeMap.getNamedItem(DeployData.USERNAME).getNodeValue(), deployInfo);
					String password = PropertyHelper.replaceVariable( roleNamedNodeMap.getNamedItem(DeployData.USERPASSWORD).getNodeValue() , deployInfo);
					
					logger.trace("Role : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
					logger.trace("Role : " + userName);
					logger.trace("Role : " + password );
					Hashtable userPassword = new Hashtable();
					userPassword.put(DeployData.USERNAME, userName);
					userPassword.put(DeployData.USERPASSWORD, password  );
					
					mapRunAsRoleToUser.put(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(), userPassword );
	
					}
				}
			}

		return mapRunAsRoleToUser ;

	}

	
	
	/** 
	 * to get the Security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getRolesToGroup()
		throws DeployException{
		
		Hashtable mapRoleToUser = new Hashtable();
		
		// start with ROLE_BINDING
		Vector roleBnd = Helper.getNamedChildNode(appNode , DeployData.ROLE_BINDING);
		for (int m =0;m<roleBnd.size();m++){
			
			NodeList nl = ((Node)roleBnd.get(m)).getChildNodes();
			
			for (int k=0 ; k< nl.getLength(); k++ ){
				// continue with the role node
				if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ROLE)){
					Node node = nl.item(k);
					NamedNodeMap roleNamedNodeMap = node.getAttributes();
					
					logger.trace("Role : " + PropertyHelper.replaceVariable(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo));
	
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						StringBuffer sb = new StringBuffer();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.GROUP)){;
							NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
							String groupName = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.GROUP_NAME).getNodeValue(),deployInfo);
							logger.trace("Group : " + groupName );
							sb.append(groupName ); 
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
		}
		return mapRoleToUser;
	}
	

	/** 
	 * to get the Special security roles part of wscp command  
	 * @return String 
	 */
	public Hashtable getSpecialRolesMapping(){
		Hashtable specialRolesMapping = new Hashtable();
		Vector splBndNl = Helper.getNamedChildNode(appNode, DeployData.SPECIAL_BINDING);
		for (int m=0 ; m < splBndNl.size();m++ ){
			NodeList nl = ((Node)splBndNl.get(m)).getChildNodes();
			for (int k=0 ; k< nl.getLength(); k++ ){
				if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.ROLE)){
					Node node = nl.item(k);
					NamedNodeMap roleNamedNodeMap = node.getAttributes();
					logger.trace("Role : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
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
		}
		return specialRolesMapping;
	}
	

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForNonMessaging(String ejbModuleName)
		throws DeployException{

		Hashtable EJBJNDIForNonMessaging = new Hashtable();
		// get EJB module
		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				// get Name of EJB module
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbModuleName)){
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						//get Name of EJB Name
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING)){;
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								if (namedNodeMap.getNamedItem(DeployData.JNDI_NAME)!=null){
									String jndiName = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.JNDI_NAME).getNodeValue(), deployInfo);
									logger.trace("    JNDI Name : " + jndiName );
									EJBJNDIForNonMessaging.put(namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(),jndiName );
								}
							} 
						}
					}
				}
			}
		}
		return EJBJNDIForNonMessaging;
	}

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public String getCMPDSForEJBModule(String ejbModuleName)
	throws DeployException{

		String dsForModuleMap = null;
		// get EJB module
		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				// get Name of EJB module
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbModuleName)){
					if (ejbNamedNodeMap.getNamedItem(DeployData.MODULE_DS_BND).getNodeValue()!=null){
						dsForModuleMap = PropertyHelper.replaceVariable(ejbNamedNodeMap.getNamedItem(DeployData.MODULE_DS_BND).getNodeValue(),deployInfo);
					}	
				}
				
			}
		}
	return dsForModuleMap ;	
	}

	
	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBJNDIForMessaging(String msgModuleName){

		Hashtable EJBJNDIForMessaging = new Hashtable();
		// get EJB module
		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				// get Name of EJB module
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(msgModuleName)){
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						//get Name of EJB Name
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.MESSAGE_BEAN_BINDING)){;
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								String messageListner = namedNodeMap.getNamedItem(DeployData.MESSAGELISTNER).getNodeValue();
								logger.trace("    JNDI Name : " + messageListner );
								EJBJNDIForMessaging.put(namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(),messageListner );		
							} 
						}
					}
				}
			}
		}

		return EJBJNDIForMessaging;
	}

	
	
	/** 
	 * to get the ejb references part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBRefs(String moduleName){

		
		Hashtable EJBReftoEJB = new Hashtable();
		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);		
		
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE) ){
				Node node = (Node)nl.get(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(moduleName)){								
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING)||  childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.MESSAGE_BEAN_BINDING)){
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList EJBRefsChild = childNls.item(i).getChildNodes();
								for(int j=0 ;j < EJBRefsChild.getLength();j++){
									if (EJBRefsChild.item(j).getNodeName().equalsIgnoreCase(DeployData.EJB_REF_BINDING)){
										NamedNodeMap EJBRefNamedNodeMap = EJBRefsChild.item(j).getAttributes();
										logger.trace("        EJB REF Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
										NodeList ejbRefNode =  EJBRefsChild.item(j).getChildNodes();
										for (int n = 0; n<ejbRefNode.getLength();n++){
											if (ejbRefNode.item(n).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
												NamedNodeMap ejbRefAttrs =   ejbRefNode.item(n).getAttributes();
												String ejbRefJNDI = ejbRefAttrs.getNamedItem(DeployData.NAME).getNodeValue();
												logger.trace("        EJB REF Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
												EJBReftoEJB.put(EJBRefNamedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue(),ejbRefJNDI);
											}
										}
									}
								}
							} 
						}
					}
				}
			}
		}
		
		Vector webNl = Helper.getNamedChildNode(appNode,DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				// get web module 
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(moduleName)){				
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int b =0; b< childNls.getLength(); b++){
							if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.EJB_REF_BINDING)){;
								NamedNodeMap namedNodeMap = childNls.item(b).getAttributes();
//								// get EJB ref name
								logger.trace("    EJB REF Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList webEjbRefNl = childNls.item(b).getChildNodes();
								if  (webEjbRefNl.getLength()>0){
									for (int c =0 ; c < webEjbRefNl.getLength(); c++){
										
										if (webEjbRefNl.item(c).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK) ){
										// get EJB ref name
											NamedNodeMap webEjbRefJNDILink = webEjbRefNl.item(c).getAttributes();
											String webEJBRefJDNI = webEjbRefJNDILink.getNamedItem(DeployData.NAME).getNodeValue();
											logger.trace("    EXT JNDI Name : " + webEjbRefJNDILink.getNamedItem(DeployData.NAME).getNodeValue());
											EJBReftoEJB.put(namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(),webEJBRefJDNI );
										}
									}
								}
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
	public Hashtable getResRefs(String moduleName,String ejbName)
	throws DeployException{
		Hashtable mapResReftoEJB = new Hashtable();

		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());


				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(moduleName)){								
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if(childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING) ||  childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.MESSAGE_BEAN_BINDING)){
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								if (namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbName)){		
									if (namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbName)){	
										NodeList EJBRefsChild = childNls.item(i).getChildNodes();
										for(int j=0 ;j < EJBRefsChild.getLength();j++){
											if (EJBRefsChild.item(j).getNodeName().equalsIgnoreCase(DeployData.RESOURCE_REF_BINDING)){
												NamedNodeMap EJBRefNamedNodeMap = EJBRefsChild.item(j).getAttributes();
												logger.trace("        Res REF Name : " + EJBRefNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
												NodeList ejbRefNode =  EJBRefsChild.item(j).getChildNodes();
												for (int n = 0; n<ejbRefNode.getLength();n++){
													if (ejbRefNode.item(n).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
														NamedNodeMap ejbRefAttrs =   ejbRefNode.item(n).getAttributes();
														String ejbRefJNDI = PropertyHelper.replaceVariable( ejbRefAttrs.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
														logger.trace("        EJB EXT JNDI Name : " + ejbRefAttrs.getNamedItem(DeployData.NAME).getNodeValue());
														mapResReftoEJB.put(EJBRefNamedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue(),ejbRefJNDI);
													}
												}
											}
										}
									}
								}
							} 
						}
					}
				}
			}
		}

		

		
		

		Vector webNl = Helper.getNamedChildNode (appNode,DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				// get web module 
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(moduleName)){				
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int b =0; b< childNls.getLength(); b++){
							if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.RESOURCE_REF_BINDING)){;
								NamedNodeMap namedNodeMap = childNls.item(b).getAttributes();
//								// get EJB ref name
								logger.trace("    EJB REF Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList webEjbRefNl = childNls.item(b).getChildNodes();
								if  (webEjbRefNl.getLength()>0){
									for (int c =0 ; c < webEjbRefNl.getLength(); c++){
										
										if (webEjbRefNl.item(c).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK) ){
										// get EJB ref name
											NamedNodeMap webEjbRefJNDILink = webEjbRefNl.item(c).getAttributes();
											String webEjbRefJNDI = PropertyHelper.replaceVariable(webEjbRefJNDILink.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
											logger.trace("    EXT JNDI Name : " + webEjbRefJNDI);
											mapResReftoEJB.put(namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(),webEjbRefJNDI)	;
										}
									}
								}
							} 
						}
					}
				}
			}
		}

	return mapResReftoEJB;
	}

	/** 
	 * to get the Resources ref part of wscp command  
	 * @return String 
	 */
	public Hashtable getResEnvRefs(String moduleName)
	throws DeployException{
		Hashtable mapResEnvToRes = new Hashtable();


		
		Vector nl = Helper.getNamedChildNode(appNode ,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());


				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(moduleName)){								
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING)){
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList ResEnvChild = childNls.item(i).getChildNodes();
								for(int j=0 ;j < ResEnvChild.getLength();j++){
									if (ResEnvChild.item(j).getNodeName().equalsIgnoreCase(DeployData.RESOURCE_ENV_BINDING)){
										NamedNodeMap resEnvNamedNodeMap = ResEnvChild.item(j).getAttributes();
										logger.trace("        Res REF Name : " + resEnvNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
										NodeList resRefNode =  ResEnvChild.item(j).getChildNodes();
										for (int n = 0; n<resRefNode.getLength();n++){
											if (resRefNode.item(n).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
												NamedNodeMap resEnvAttrs =   resRefNode.item(n).getAttributes();
												String resEnvJNDI = PropertyHelper.replaceVariable(resEnvAttrs.getNamedItem(DeployData.NAME).getNodeValue(),deployInfo);
												logger.trace("        Res EXT JNDI Name : " + resEnvJNDI);
												mapResEnvToRes.put(resEnvNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(),resEnvJNDI);
											}
										}
									}
								}
							} 
						}
					}
				}
			}
		}


		Vector webNl = Helper.getNamedChildNode(appNode,DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				// get web module 
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(moduleName)){				
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int b =0; b< childNls.getLength(); b++){
							if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.RESOURCE_ENV_BINDING)){;
								NamedNodeMap namedNodeMap = childNls.item(b).getAttributes();
//								// get res ref name
								logger.trace("    RES ENV Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList webRefEnvNl = childNls.item(b).getChildNodes();
								if  (webRefEnvNl.getLength()>0){
									for (int c =0 ; c < webRefEnvNl.getLength(); c++){
										
										if (webRefEnvNl.item(c).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK) ){
										// get EJB ref name
											NamedNodeMap webRefEnvJNDILink = webRefEnvNl.item(c).getAttributes();
											String webRefEnvJNDI = PropertyHelper.replaceVariable(webRefEnvJNDILink .getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
											logger.trace("    RES ENV JNDI Name : " + webRefEnvJNDI );
											mapResEnvToRes.put(namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue(),webRefEnvJNDI );
										}
									}
								}
							} 
						}
					}
				}
			}
		}

		return mapResEnvToRes;
	}

	
	public Hashtable getWebModule()
	throws DeployException{
		
		
		Hashtable mapWebModToVHTask = new Hashtable();
		Vector webNl = Helper.getNamedChildNode(appNode,DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				String virtualHost = PropertyHelper.replaceVariable(roleNamedNodeMap.getNamedItem(DeployData.VIRTUALHOST).getNodeValue(), deployInfo); 
				logger.trace("Web Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				logger.trace("Web Module : " + virtualHost);
				mapWebModToVHTask.put(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+",WEB-INF/web.xml",virtualHost) ;
			}
		}
		return mapWebModToVHTask;		
	}

	
//TODO Add a method to get default CMP datadource for the module.
//TODO Change this method to return the EJB to CMP datasource mapping.	
	public Hashtable getEJBCMP2Datasource(String ejbModuleName)
	throws DeployException{

		Hashtable mapEJBCMPtoJNDI = new Hashtable();

		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());

				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbModuleName)){								
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING)){
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList EJBCMPChild = childNls.item(i).getChildNodes();
								for(int j=0 ;j < EJBCMPChild .getLength();j++){
									if (EJBCMPChild.item(j).getNodeName().equalsIgnoreCase(DeployData.CMP2_DS_BINDING)){
										NodeList ejbcmpJNDINode =  EJBCMPChild.item(j).getChildNodes();
										for (int n = 0; n<ejbcmpJNDINode.getLength();n++){
											if (ejbcmpJNDINode.item(n).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
												NamedNodeMap ejbcmpJNDINodeAttrs =   ejbcmpJNDINode.item(n).getAttributes();
												String ejbcmpJNDI = PropertyHelper.replaceVariable(ejbcmpJNDINodeAttrs.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo); 
												logger.trace("        EJB CMP JNDI Name : " + ejbcmpJNDI);
												mapEJBCMPtoJNDI.put(namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue(),ejbcmpJNDI);
											}
										}
									}
								}
							} 
						}
					}
				}
			}
		}
		
		return mapEJBCMPtoJNDI;
	
	}

	public Hashtable getEJBCMP1Datasource(String ejbModuleName)
	throws DeployException{

		Hashtable mapEJBCMPtoJNDI = new Hashtable();

		Vector nl = Helper.getNamedChildNode(appNode , DeployData.EJB_MODULE);
		
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());

				if (roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbModuleName)){								
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int i =0; i< childNls.getLength(); i++){
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING)){
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("    EJB Name : " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
								NodeList EJBCMPChild = childNls.item(i).getChildNodes();
								for(int j=0 ;j < EJBCMPChild .getLength();j++){
									if (EJBCMPChild.item(j).getNodeName().equalsIgnoreCase(DeployData.CMP1_DS_BINDING)){
										NodeList ejbcmpJNDINode =  EJBCMPChild.item(j).getChildNodes();
										for (int n = 0; n<ejbcmpJNDINode.getLength();n++){
											if (ejbcmpJNDINode.item(n).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
												NamedNodeMap ejbcmpJNDINodeAttrs =   ejbcmpJNDINode.item(n).getAttributes();
												String ejbcmpJNDI = PropertyHelper.replaceVariable(ejbcmpJNDINodeAttrs.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
												logger.trace("        EJB CMP JNDI Name : " + ejbcmpJNDI );
												mapEJBCMPtoJNDI.put(namedNodeMap.getNamedItem(DeployData.ENTERPRISE_BEAN_NAME).getNodeValue(),ejbcmpJNDI );
											}
										}
									}
								}
							} 
						}
					}
				}
			}
		}
		
		return mapEJBCMPtoJNDI;
	
	}

		

	public Vector getAllModules(){
		Vector modules = new Vector();
		
		Vector webNl = Helper.getNamedChildNode(appNode, DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+"+WEB-INF/web.xml") ;
			}
		}
		
		Vector ejbNl = Helper.getNamedChildNode(appNode , DeployData.EJB_MODULE);
		for (int a=0 ; a< ejbNl.size(); a++ ){
			if (((Node)ejbNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)ejbNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+"+META-INF/ejb-jar.xml") ;
			}
		}		
		return modules;		
	}

	public HashMap getWebModules(){
		HashMap modules = new HashMap();
		
		Vector webNl = Helper.getNamedChildNode(appNode,DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				String webserver = "";
				if (roleNamedNodeMap.getNamedItem(DeployData.WEBSERVER)!= null){
					webserver = roleNamedNodeMap.getNamedItem(DeployData.WEBSERVER).getNodeValue();
				}
				logger.trace(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+"+WEB-INF/web.xml" + " webserver is " + webserver);		
				modules.put(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+"+WEB-INF/web.xml",webserver) ;
			}
		}
		
		return modules;		
	}

	public Vector getEJBModules(){
		Vector modules = new Vector();
		
		Vector ejbNl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int a=0 ; a< ejbNl.size(); a++ ){
			if (((Node)ejbNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)ejbNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()+"+META-INF/ejb-jar.xml") ;
			}
		}		
		return modules;		
	}
	
	
	public Vector getAllResources()
	throws DeployException{
		Vector resource = new Vector();

		Vector nl = Helper.getNamedChildNode(appNode, DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();

				logger.trace("EJB Module : " + roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				if (node.getChildNodes().getLength()>0){
					NodeList childNls = node.getChildNodes();
					for (int i =0; i< childNls.getLength(); i++){
						if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.ENTERPRISE_BEAN_BINDING )){;
							NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
							NodeList EJBRefsChild = childNls.item(i).getChildNodes();
							for(int j=0 ;j < EJBRefsChild.getLength();j++){
								if (EJBRefsChild.item(j).getNodeName().equalsIgnoreCase(DeployData.RESOURCE_REF_BINDING)){
									NodeList EJBRefJNDILink = EJBRefsChild.item(j).getChildNodes();
									for (int a=0;a< EJBRefJNDILink.getLength();a++){
										if (EJBRefJNDILink.item(a).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
											NamedNodeMap EJBRefJNDILinkAtrs = EJBRefJNDILink.item(a).getAttributes();
											String  ejbRefJNDI = PropertyHelper.replaceVariable(EJBRefJNDILinkAtrs.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
											logger.trace("        RES EXT JNDI Name : " + ejbRefJNDI ); 					
											resource.add(ejbRefJNDI );
										}
									}
								}	
								}
							} 
						}
					}
				}
			}


			Vector webNl = Helper.getNamedChildNode(appNode, DeployData.WEB_MODULE);
			for (int a=0 ; a< webNl.size(); a++ ){
				if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
					Node node = (Node)webNl.get(a);
					NamedNodeMap webNamedNodeMap = node.getAttributes();
	
					logger.trace("Web Module : " + webNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
	
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						for (int b =0; b< childNls.getLength(); b++){
							if( childNls.item(b).getNodeName().equalsIgnoreCase(DeployData.RESOURCE_REF_BINDING)){
								NodeList EJBRefJNDILink = childNls.item(b).getChildNodes();
								for (int m=0;m<EJBRefJNDILink.getLength();m++){
									if (EJBRefJNDILink.item(m).getNodeName().equalsIgnoreCase(DeployData.JNDI_NAME_LINK)){
										NamedNodeMap EJBRefJNDILinkAtrs = EJBRefJNDILink.item(m).getAttributes();
										String ejbRefJNDI = PropertyHelper.replaceVariable( EJBRefJNDILinkAtrs.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
										logger.trace("        RES EXT JNDI Name : " + ejbRefJNDI ); 					
										resource.add(ejbRefJNDI );
									}
								}
							}
						}
					}
				}
			}

		
		return resource; 
	}



	public static void main(String args[]) throws IOException, SAXException,DeployException{
/**
		logger.trace("Starting deploydata");
		DeployDataReader deployDataReader = new DeployDataReader("C:/jatin/eclipse/easyDeploy/deploydata/DeployData_template.xml");
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
		Vector modules = new Vector();
		
		Vector webNl = Helper.getNamedChildNode(appNode, DeployData.WEB_MODULE);
		for (int a=0 ; a< webNl.size(); a++ ){
			if (((Node)webNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.WEB_MODULE)){
				Node node = (Node)webNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()) ;
			}
		}
		
		Vector ejbNl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int a=0 ; a< ejbNl.size(); a++ ){
			if (((Node)ejbNl.get(a)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)ejbNl.get(a);
				NamedNodeMap roleNamedNodeMap = node.getAttributes();
				modules.add(roleNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue()) ;
			}
		}		
		return modules;		
	}
	


	public Vector getLibraries()
	throws DeployException{
		
		Vector sharedlibName = new Vector();
		Hashtable Libraries = new Hashtable();
		
		// start with ROLE_BINDING
		Vector libraries = Helper.getNamedChildNode(appNode,DeployData.LIBRARIES);
		if (libraries !=null){
			for (int m =0;m<libraries.size();m++){
				NodeList nl = ((Node)libraries.get(m)).getChildNodes();
				for (int k=0 ; k< nl.getLength(); k++ ){
					// continue with the role node
					if (nl.item(k).getNodeName().equalsIgnoreCase(DeployData.SHAREDLIBNAME)){
						Node node = nl.item(k);
						NamedNodeMap sharedLibNodeMap = node.getAttributes();
						String sharedLib = PropertyHelper.replaceVariable( sharedLibNodeMap.getNamedItem(DeployData.NAME).getNodeValue(), deployInfo);
						logger.trace("Role : " + sharedLib );
						sharedlibName.add(sharedLib );
		
					}
				}
			}
		}
		return sharedlibName;
	}

	public Integer getStartingWeight()
	throws DeployException{
		Integer weight = new Integer (0);
		
		
		Vector startingWeight = Helper.getNamedChildNode(appNode, DeployData.STARTINGWEIGHT);
		if ((startingWeight !=null) && (startingWeight.size()>0)) {
			
			NamedNodeMap attrs =  ((Node)startingWeight.get(0)).getAttributes();
			Node valueNode = attrs.getNamedItem("value"); 
			String newWeight = PropertyHelper.replaceVariable( valueNode.getNodeValue(), deployInfo);
			weight =  (new Integer( newWeight)) ;
		}
		return weight;

	}

	public boolean getEnable(){
		boolean isEnable = true;
		
		
		Vector enable = Helper.getNamedChildNode(appNode, DeployData.ENABLE);
		if ((enable !=null) && (enable.size()>0)) {
			
			NamedNodeMap attrs =  ((Node)enable.get(0)).getAttributes();
			Node valueNode = attrs.getNamedItem("value"); 			
			isEnable =  (new Boolean( valueNode.getNodeValue())).booleanValue() ;
		}
		return isEnable ;

	}

	public boolean getGenerateEJBDeployCode(){

		boolean shouldGenerate = false; 
		Vector generateEJBDeployCode = Helper.getNamedChildNode(appNode, DeployData.GENERATEEJBDEPLOYCODE);
		if ((generateEJBDeployCode !=null) && (generateEJBDeployCode.size()>0)) {
			
			NamedNodeMap attrs =  ((Node)generateEJBDeployCode.get(0)).getAttributes();
			Node valueNode = attrs.getNamedItem("value"); 			
			shouldGenerate =  (new Boolean( valueNode.getNodeValue())).booleanValue() ;
		}
		logger.trace("Generate EJB Deploy code from Deploydata" + shouldGenerate);
		return shouldGenerate;
		

	}

	/** 
	 * to get the JNDI name part of wscp command  
	 * @return String 
	 */
	public Hashtable getEJBWebServiceBinding(String ejbModuleName,String webServiceName)
	throws DeployException{

		Hashtable EJBWebServiceBinding = new Hashtable();
		// get EJB module
		Vector nl = Helper.getNamedChildNode(appNode,DeployData.EJB_MODULE);
		for (int k=0 ; k< nl.size(); k++ ){
			if (((Node)nl.get(k)).getNodeName().equalsIgnoreCase(DeployData.EJB_MODULE)){
				Node node = (Node)nl.get(k);
				
				NamedNodeMap ejbNamedNodeMap = node.getAttributes();
				// get Name of EJB module
				logger.trace("EJB Module : " + ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue());
				logger.trace("Checking module in deploy data " + ejbNamedNodeMap.getNamedItem(DeployData.NAME) + " equals " + ejbModuleName);
				if (ejbNamedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(ejbModuleName)){
					logger.trace("Match found Checking module in deploy data " + ejbNamedNodeMap.getNamedItem(DeployData.NAME) + " equals " + ejbModuleName);
					logger.trace("Child count for node " + ejbNamedNodeMap.getNamedItem(DeployData.NAME) + " is " + node.getChildNodes());
					if (node.getChildNodes().getLength()>0){
						NodeList childNls = node.getChildNodes();
						
						//get Name of EJB Name
						for (int i =0; i< childNls.getLength(); i++){
							logger.trace("Child node :" + i + " name is " + childNls.item(i).getNodeName());
							if( childNls.item(i).getNodeName().equalsIgnoreCase(DeployData.WEB_SERVICE)){;
								NamedNodeMap namedNodeMap = childNls.item(i).getAttributes();
								logger.trace("Matching " + namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue() + " to " + webServiceName);
								if (namedNodeMap.getNamedItem(DeployData.NAME).getNodeValue().equalsIgnoreCase(webServiceName)){
									
									if (namedNodeMap.getNamedItem(DeployData.PORT)!=null){
										String port = PropertyHelper.replaceVariable( namedNodeMap.getNamedItem(DeployData.PORT).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.PORT + " value " + port );
										EJBWebServiceBinding.put(DeployData.PORT, port);
									}
									
									if (namedNodeMap.getNamedItem(DeployData.TIMEOUT)!=null){
										String timeout = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.TIMEOUT).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.TIMEOUT+ " value " + timeout);
										EJBWebServiceBinding.put(DeployData.TIMEOUT, timeout);
									}
									
									if (namedNodeMap.getNamedItem(DeployData.BASICAUTHID )!=null){
										String basicAuth = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.BASICAUTHID ).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.BASICAUTHID+ " value " + basicAuth);
										EJBWebServiceBinding.put(DeployData.BASICAUTHID, basicAuth);
									}
									
									if (namedNodeMap.getNamedItem(DeployData.BASICAUTHPASSWORD)!=null){
										String basicpassword = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.BASICAUTHPASSWORD).getNodeValue(), deployInfo);
										EJBWebServiceBinding.put(DeployData.BASICAUTHPASSWORD, basicpassword );
									}
									
									if (namedNodeMap.getNamedItem(DeployData.SSLCONFIG)!=null){
										String basicSSL = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.SSLCONFIG).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.SSLCONFIG + " value " + basicSSL);
										EJBWebServiceBinding.put(DeployData.SSLCONFIG, basicSSL);
									}

									if (namedNodeMap.getNamedItem(DeployData.OVERRIDDENENDPOINT)!=null){	
										String overRiddenEndPoint = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.OVERRIDDENENDPOINT).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.OVERRIDDENENDPOINT + " value " + overRiddenEndPoint);
										EJBWebServiceBinding.put(DeployData.OVERRIDDENENDPOINT, overRiddenEndPoint);
									}

									if (namedNodeMap.getNamedItem(DeployData.PORTTYPE)!=null){
										String portType = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.PORTTYPE).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.PORTTYPE + " value " + portType);
										EJBWebServiceBinding.put(DeployData.PORTTYPE, portType);
									}
									
									if (namedNodeMap.getNamedItem(DeployData.PREFFEREDPORT)!=null){
										String preferedPort = PropertyHelper.replaceVariable(namedNodeMap.getNamedItem(DeployData.PREFFEREDPORT).getNodeValue(), deployInfo);
										logger.trace("adding " + DeployData.PREFFEREDPORT + " value " + preferedPort);	
										EJBWebServiceBinding.put(DeployData.PREFFEREDPORT, preferedPort);
									}
								}
							} 
						}
					}
				}else{
					logger.trace("Match not found Checking module in deploy data " + ejbNamedNodeMap.getNamedItem(DeployData.NAME) + " equals " + ejbModuleName);
				}
			}
		}
		return EJBWebServiceBinding;
	}


}


