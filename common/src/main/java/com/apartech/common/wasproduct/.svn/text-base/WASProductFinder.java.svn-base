/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.common.wasproduct;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import com.apartech.common.Constants.WASProductConstants;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.exception.AdminException;

public class WASProductFinder {
	
	public WASProduct getProduct(AdminClient adminClient, String sessionId)
		throws AdminException,Exception{

		// default to WAS , if the product is something else it will be set in the loop below
		WASProduct wasProduct = new WASProduct();
		wasProduct.setWASOnly(true);
		wasProduct.setWASProduct(WASProductConstants.WAS);

		Hashtable<String, Object> props = new Hashtable<String, Object>();
		AppManagement  appManagement = AppManagementProxy.getJMXProxyForClient(adminClient);
		props.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
		
		Vector<String> applications = appManagement.listApplications(props, sessionId);
		

		for (int i=0 ; i < applications.size(); i++){
			
			if (applications.get(i).toString().startsWith(WASProductConstants.BPEContainer)){
				wasProduct.setWASOnly(false);
				wasProduct.setWASProduct(WASProductConstants.WAS_PROCESS_SERVER);
				return wasProduct;

			}else if (applications.get(i).toString().equalsIgnoreCase(WASProductConstants.SCA_SIB_MEDIATION)){
				wasProduct.setWASOnly(false);
				wasProduct.setWASProduct(WASProductConstants.WAS_ESB);
				return wasProduct;
				
			}else if (applications.get(i).toString().equalsIgnoreCase(WASProductConstants.WPS)){
				wasProduct.setWASOnly(false);
				wasProduct.setWASProduct(WASProductConstants.WAS_PORTAL_SERVER);
				return wasProduct;
				
			}
		}
		
		return wasProduct; 
	}

}
