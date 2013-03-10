/**	   Copyright 


**/
package org.aa.auraconfig.resources;

public class ResourceConstants {
	
	
	public static final String RESOURCES = "resources";
	public static final String REQUEST = "request";
	public static final String FILE = "file";

	public static final String IMPORT= "import";
	
	public static final String ATTRUBUTENAME = "__attributeName";
	public static final String TEMPLATE = "__template";
	
	public static String[] SYNC_IGNORE_ATTRIBUTE_LIST = {"uuid"};
	public static String[] SYNC_IGNORE_TYPE_LIST = {};

	public static String[] ATTRIBUTE_IGNORE_LIST = {"providerType","adjustPort","uniqueId","busUuid","mapping","EJBTimer","securityTagCompatibilityMode","bootstrapRepositoryLocation","byteArray"};
	
	public static final String ATTRIBUTE_CHANGE_TYPE_NEW = "Added";
	public static final String ATTRIBUTE_CHANGE_TYPE_INCOMING = "Incoming";
	public static final String ATTRIBUTE_CHANGE_TYPE_MODIFIED = "Modified";
	public static final String ATTRIBUTE_CHANGE_TYPE_UNMODIFIED = "UnModified";

	public static final String DEFAULT_ATTR = "_defaultAttr";
	
	public static final String COMMAND_ADDITIONAL = "additional";
	public static final String COMMAND_PARENT = "parent";
	public static final String COMMAND_MAPPING = "mapping";
	public static final String COMMAND_CONSTANT = "constant";
	
	

}
