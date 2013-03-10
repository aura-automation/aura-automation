package org.aa.common.log;

/**
 * @author Jatin
 *
 * Copyright (C) 

 */
public class Error {

	public static void log(String error){
		System.out.println(error);
	}

	public static void log(String error,Class errClass){
		System.out.println(errClass.getName() + " "+ error);
	}
	

}
