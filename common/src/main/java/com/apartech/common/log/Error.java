package com.apartech.common.log;

/**
 * @author Jatin
 *
 * Copyright (C) 2006  Apartech Ltd. Jatin Bhadra

 */
public class Error {

	public static void log(String error){
		System.out.println(error);
	}

	public static void log(String error,Class errClass){
		System.out.println(errClass.getName() + " "+ error);
	}
	

}
