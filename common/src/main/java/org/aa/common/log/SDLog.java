package org.aa.common.log;



/**
 * @author Jatin
 *
 * Copyright (C) 

 */
public class SDLog {
	
	private static boolean writeLog = true;
	
	public static void log(String messsage){
		if (writeLog){ 
			System.out.println(messsage);
			System.out.flush();

		}
	}

	public static void log(String messsage, int level ){
		level = level*2;  
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level ; i++ ){
			sb.append(" "); 
		}
		
		sb.append(messsage);
		if (writeLog ){ 
			System.out.println(sb.toString());
			System.out.flush();

		}
	}

	public static void log(String messsage,Class srcClass){
		if (writeLog ){ 
			System.out.println(srcClass.getName() + " "+ messsage);
			System.out.flush();

		}
		
	}

}
