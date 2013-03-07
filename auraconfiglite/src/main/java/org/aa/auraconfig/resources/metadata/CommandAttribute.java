/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.auraconfig.resources.metadata;


public class CommandAttribute {
	
	private String commandAttribute;

	private String type;
	
	private CommandLinkAttribute commandLinkAttribute;
	
	/**
	 * This is value set in metadata when the attribute type is constant, like SIBQueue has type as Queue
	 */
	
	private String constantValue;

	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}

	public CommandLinkAttribute getCommandLinkAttribute() {
		return commandLinkAttribute;
	}
	
	public void setCommandLinkAttribute(CommandLinkAttribute commandLinkAttribute) {
		this.commandLinkAttribute = commandLinkAttribute;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the commandAttribute
	 */
	public String getCommandAttribute() {
		return commandAttribute;
	}
	/**
	 * @param commandAttribute the commandAttribute to set
	 */
	public void setCommandAttribute(String commandAttribute) {
		this.commandAttribute = commandAttribute;
	}


}
