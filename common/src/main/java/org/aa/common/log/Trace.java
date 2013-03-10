package org.aa.common.log;

/**
 * @author Jatin
 *
 * Copyright (C) 

 */
public class Trace {
	
	public static void log (String logString,Class sourceClass){
		
		System.out.println( sourceClass.getName() + " " + logString);
	
	}

}
