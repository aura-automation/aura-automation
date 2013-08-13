package org.aa.common.encryption;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

public class Encrypt {
	
	public static void main(String[] args) 
		throws IOException, FileNotFoundException{
		Encrypt encrypt = new Encrypt();  
		System.out.println( encrypt.encryptString("wasadmin","jayst"));
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt"); // could be got from web, env variable...
		
		 /*
		  * Create our EncryptableProperties object and load it the usual way.
		  */
		 Properties props = new Properties();
		 props.load(new FileInputStream("/home/jatin/.aura_repo/environments/vm-04-encrypt"));
		 System.out.println("ENC(" + encryptor.encrypt("wasadmin") + ")");
		 
		 props.put("wasPassword-new", "ENC(" + encryptor.encrypt("wasadmin") + ")");
		 
		 props.store((new FileOutputStream("/home/jatin/.aura_repo/environments/vm-04-encrypt")),"encrupted");
		 System.out.println( props.getProperty("wasPassword"));
		 
	}

	public String encryptString(String value, String password){
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt"); // could be got from web, env variable...
		return "ENC(" + encryptor.encrypt(value) + ")";
	}
	
	public String getDecryptedProperty(String key, String propertiesFile, String password)
		throws IOException, FileNotFoundException{
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt"); // could be got from web, env variable...
		Properties props = new EncryptableProperties(encryptor);
		props.load(new FileInputStream(propertiesFile));

		return props.getProperty(key) ;
	}
	
}
