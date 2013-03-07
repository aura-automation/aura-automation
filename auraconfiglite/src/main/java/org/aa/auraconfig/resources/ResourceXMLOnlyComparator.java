/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.aa.auraconfig.resources.parser.ResourceXMLParser;
import org.aa.auraconfig.resources.parser.ResourceXMLWriter;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.aa.common.Constants.DeployValues;
import org.aa.common.deploy.DeployInfo;
import org.aa.common.exception.DeployException;
import org.aa.common.log.SDLog;

public class ResourceXMLOnlyComparator{ 

	Vector<Resource> modifiedResources = new Vector<Resource>();  	
	
	public Resource compare(DeployInfo deployInfo1, DeployInfo deployInfo2)
		throws DeployException,JDOMException,IOException{

	
		deployInfo1.setOperationMode(DeployValues.OPERATION_MODE_SYNC );
		

		InputStream resourceXMLMetaDataInputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("resources-metadata.xml");
		InputStream referencedResourceXML =  Thread.currentThread().getContextClassLoader().getResourceAsStream("Reference-ResourceObjects.xml");
		
		if (resourceXMLMetaDataInputStream==null){
			SDLog.log("Error: AuraConfig Data missing");
		}

		if (referencedResourceXML == null){
			SDLog.log("Error: AuraConfig Reference file missing");
		}
		
		ResourceXMLParser resourceXMLParser = new ResourceXMLParser(); 
		ResourceXMLParser resourceXMLParser2 = new ResourceXMLParser(); 

		/**
		 * Load the resources from the 1st XML files
		 */
		Element rootNode = resourceXMLParser.getResourcesXMLElements(deployInfo1.getResourceXML() ,false);
		Resource resources = resourceXMLParser.getResourcesFromXML(rootNode,null,resourceXMLMetaDataInputStream,false,deployInfo1,null);
		Resource referencedResources = resourceXMLParser.getReferenceResources(referencedResourceXML,resourceXMLMetaDataInputStream,false,deployInfo1);

		// 0nly if the second XML is provided need to run a compare else just produce a report
		if (deployInfo2 !=null){
			deployInfo2.setSourceDeployInfo(deployInfo1);

			deployInfo2.setOperationMode(DeployValues.OPERATION_MODE_SYNC );

			Element rootNode2 = resourceXMLParser2.getResourcesXMLElements(deployInfo2.getResourceXML(),false);
			Resource resources2 = resourceXMLParser2.getResourcesFromXML(rootNode2,null,resourceXMLMetaDataInputStream,false,deployInfo2,null);
	
		
			checkDifference(resources,resources2,deployInfo2,resources);
		
		//	checkDifference(resources2, resources,deployInfo2,true);
		
			ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
			resourceDiffReportHelper.generateReport(modifiedResources); 

			ResourceXMLWriter resourceXMLWriter  = new ResourceXMLWriter();
			resourceXMLWriter.createResourceXMLFile(resources,deployInfo2);

		}else{
		
			ResourceXMLWriter resourceXMLWriter  = new ResourceXMLWriter();
			resourceXMLWriter.createResourceXMLFile(resources,deployInfo1);
		}
		return resources;
	}
	
	
	/**
	 * 
	 * @param resources
	 * @param resources2
	 */
	private void checkDifference(Resource resources,Resource resources2,
				DeployInfo deployInfo,Resource allResources)
		throws DeployException{
		ResourceFinder resourceFinder = new ResourceFinder();

		Vector<Resource> children = resources.getChildren();
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper (); 

		
		/**
		 * Check if 2nd xml has any extract resources.
		 * 
		 * For the given resource type check for all the resources in the second xml for same type. For e.g. ServerCluster 
		 * For each resource resource found in the target type if the entry in source does not exists the add it as incoming
		 */
		
		if (!resources.getName().equalsIgnoreCase(ResourceConstants.RESOURCES)){
			Resource resource2ParentOfCurrentChild = resourceFinder.matchResource(resources2, resources ); 

		//	Resource resource2ParentOfCurrentChild = matchedResource.getParent();
			if (resource2ParentOfCurrentChild!=null){
				//System.out.println(resource2ParentOfCurrentChild.getContainmentPath());
				Vector<Resource> resource2AllSiblings =  resource2ParentOfCurrentChild.getChildren();
				// If there are not resources at all in second xml then no need to check for incoming.
				if (resource2AllSiblings !=null){
					for (int j =0; j < resource2AllSiblings.size(); j++ ){
						Resource resource2PotentialIncomingSibling = (Resource)resource2AllSiblings.get(j);
						Resource resource1PotentialIncomingSibling = resourceFinder.matchResource(resources, resource2PotentialIncomingSibling);
						if (resource1PotentialIncomingSibling== null){
							//Resource alreadyAddToResource1Incoming = resourceFinder.matchResource(, resource2PotentialIncomingSibling);
							resource2PotentialIncomingSibling.setIncoming(true);
							markAllChildrenIncoming(resource2PotentialIncomingSibling);
							resources.addInComingChild(resource2PotentialIncomingSibling);
							resource2PotentialIncomingSibling.setParent(resources);
						}
					}
				}
			}
		}
		
		
		
		
		/**
		 * Check for the any resources that exists in both and for any resources that missing in target
		 */
		for (int i = 0 , n = children.size(); i < n ; i++){
			Resource child = children.get(i);

			
			Resource matchedResource = resourceFinder.matchResource(resources2, child); 
			
			if((matchedResource!=null) ){
				compareProperties(child, matchedResource);
					
				if (deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC)){
					incomingProperties(child, matchedResource);
				}
			}else{
				child.setModifiedAttributes(resourceDiffReportHelper.getDiffAttributesForNew(child.getAttributeList()));
			}
			
				
				
			if ((child.getModifiedAttributes() !=null) && (child.getModifiedAttributes().size()>0)){
				child.getParent().addDifferentChildCount();
				modifiedResources.add(child);
			}
			
			
			/**	else{
					Resource parent = child.getParent();
					child.setIncoming(true);
					child.setModifiedAttributes(resourceDiffReportHelper.getDiffAttributesForIncoming(child.getAttributeList()));

					if (parent.getResourceMetaData()!= null){
						Resource parentMatch =  resourceFinder.matchResource(resources2, parent );
						if (parentMatch != null){
							child.setParent(parentMatch);
							parentMatch.addInComingChild(child);
							
						}
					}
				} **/
				

			
			
			if (child.getChildren()!=null){
				checkDifference(child, resources2,deployInfo,allResources);
			}
		}
	}
	
	private void compareProperties(Resource resource1, Resource resource2){
		HashMap<String, String> attributes1 = resource1.getUnresolvedAttributeList();
		HashMap<String, String> attributes2 =  resource2.getUnresolvedAttributeList();
		
		String[] keys1 =(String[])attributes1.keySet().toArray(new String[0]);
		ArrayList<DiffAttribute> diffAttrs= resource1.getModifiedAttributes();
		if (diffAttrs ==null){
			diffAttrs = new ArrayList<DiffAttribute>();
		}
		
		for (int i =0, n=keys1.length; i<n; i++){
			String value1 = attributes1.get(keys1[i]);
			String value2 = attributes2.get(keys1[i]);
			if (!value1.equals(value2)){
				
				DiffAttribute diffAttribute = new DiffAttribute();
				if ((value2==null) || value2.equalsIgnoreCase("null")){
					diffAttribute.setChangeType(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_NEW);
				} else{
					diffAttribute.setChangeType(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_MODIFIED);
				}
				diffAttribute.setName(keys1[i]);
				diffAttribute.setDaksValue(value1);
				diffAttribute.setConfigValue(value2);
				diffAttrs.add(diffAttribute);
				
			}
			
		}
		resource1.setModifiedAttributes(diffAttrs);
		
	}
	
	
	private void incomingProperties(Resource resource1, Resource resource2){
		HashMap<String, String> attributes1 = resource1.getAttributeList();
		HashMap<String, String> attributes2 =  resource2.getAttributeList();
    	HashMap<String, String> incomingAttributeList = new HashMap<String, String>();

		String[] keys2 =(String[])attributes2.keySet().toArray(new String[0]);
		
		for (int i =0, n=keys2.length; i<n; i++){
			String value1 = attributes1.get(keys2[i]);
			String value2 = attributes2.get(keys2[i]);
			if (value1 == null){
				incomingAttributeList.put(keys2[i], value2);
			}
		}
		
		ResourceDiffReportHelper resourceDiffReportHelper = new ResourceDiffReportHelper();
		ArrayList<DiffAttribute> incomingArrayList = resourceDiffReportHelper.getDiffAttributesForIncoming(incomingAttributeList);
		resource1.getAttributeList().putAll(incomingAttributeList) ;

		if (resource1.getModifiedAttributes()==null)
			resource1.setModifiedAttributes(incomingArrayList);
		else
			resource1.getModifiedAttributes().addAll(incomingArrayList);

		
	}
	
	private void markAllChildrenIncoming(Resource resource2PotentialIncomingSibling){
		Vector<Resource> children = resource2PotentialIncomingSibling.getChildren();
		if (children!=null){
			for (int i=0 ; i < children.size(); i ++){
				((Resource)children.get(i)).setIncoming(true);
				markAllChildrenIncoming((Resource)children.get(i));
			}
			resource2PotentialIncomingSibling.setInComingChildren(resource2PotentialIncomingSibling.getChildren());
			resource2PotentialIncomingSibling.setChildren(null);
		}
	}
	
	public static void main(String[] args) {
		
		//	String file1 = "C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\output\\resourceServerCluster-AvatarWAS61.properties-extract.xml";
			String file1 = "C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\Portal\\ExportPortal_PRIMARY_OUTPUT.xml";
		//	"C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\output\\resourceEARApplication-AvatarProcServer.properties-extract.xml"
		//	"C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\output\\resourceEARApplication-AvatarWAS61.properties-extract.xml"
			DeployInfo deployInfo1 = new DeployInfo();
			deployInfo1.setResourceXML(file1);
			deployInfo1.setEnvironmentProperties("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\environmentProperties\\AvatarWAS61.properties");
			deployInfo1.setEnvironmentId(-1);
			deployInfo1.setSyncResourceXML("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\output\\XMLOnlyResource.xml");
			deployInfo1.setSyncReportLocation("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\report\\XMLOnlyResource.html");
			
		//	String file2 = "C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\output\\resourceServerCluster-AvatarProcServer.properties-extract.xml";
			String file2 = "C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\Portal\\ExportPortal_PRIMARY_OUTPUT1.xml";
			DeployInfo deployInfo2 = new DeployInfo();
			deployInfo2.setResourceXML(file2);
			deployInfo2.setEnvironmentProperties("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\environmentProperties\\AvatarProcServer.properties");
			deployInfo2.setEnvironmentId(-1);
			
			deployInfo2.setSyncResourceXML("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\output\\XMLOnlyResource.xml");
			deployInfo2.setSyncReportLocation("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\report\\XMLOnlyResource.html");

			try {
				ResourceXMLOnlyComparator resourceXMLOnlyComparator  = new ResourceXMLOnlyComparator ();
				resourceXMLOnlyComparator.compare(deployInfo1,deployInfo2); 
			}catch(DeployException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}catch(JDOMException e){
				e.printStackTrace();
			}
	}

	
}
