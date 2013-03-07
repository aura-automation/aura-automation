/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class ResourceStatsHelper {
    private static final Log logger  = LogFactory.getLog(ResourceStatsHelper.class);

	public void setResourceAttributeStats(Resource resource){

		ArrayList modifiedAttributes = resource.getModifiedAttributes();
		
		if (modifiedAttributes!=null){
			Iterator it = modifiedAttributes.iterator();
			int incomingCnt = 0;
			int newCnt = 0;
			int modifiedCnt = 0;
			
			while (it.hasNext() ){
				DiffAttribute diffAttribute =  (DiffAttribute)it.next();
				if (diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_INCOMING)){
					incomingCnt++;
				} else if (diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_NEW)){
					newCnt++;
				}else if (diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_MODIFIED)){
					modifiedCnt++;
				}
			}
			// to get rid of data type attribute from the count number
			//if (newCnt>0)
				//newCnt--;

			
			resource.getResourceStats().setIncomingAttributeCnt(incomingCnt);
			
			resource.getResourceStats().setNewAttributeCnt(newCnt);
			resource.getResourceStats().setModifiedAttributeCnt(modifiedCnt);
		}
	}
	
	public void setChildrenCount(Resource resource){
		Vector allChildren = new Vector();
		if (resource.getChildren()!=null){
			
		}
	}
}
