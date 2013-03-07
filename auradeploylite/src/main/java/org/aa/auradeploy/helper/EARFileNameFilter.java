/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.auradeploy.helper;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Jatin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EARFileNameFilter implements FilenameFilter{

	
		public boolean accept(File dir, String name){
			return (name.toLowerCase().endsWith(".ear"));
		}

}
