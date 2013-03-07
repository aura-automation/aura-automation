/**	   Copyright [2009] [www.apartech.com]


**/
package org.aa.common.properties.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jst.jsp.core.internal.Logger;

public abstract class PropertyLoader{
    private static final boolean THROW_ON_LOAD_FAILURE = true;
    private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;
    private static final String SUFFIX = ".properties";
    
      
    /**
	     * Looks up a resource named 'name' in the classpath. The resource must map
	     * to a file with .properties extension. The name is assumed to be absolute
	     * and can use either "/" or "." for package segment separation with an
	     * optional leading "/" and optional ".properties" suffix. Thus, the
	     * following names refer to the same resource:
	     * <pre>
	     * some.pkg.Resource
	     * some.pkg.Resource.properties
	     * some/pkg/Resource
	     * some/pkg/Resource.properties
	     * /some/pkg/Resource
	     * /some/pkg/Resource.properties
	     * </pre>
	     * 
	     * @param name classpath resource name [may not be null]
	     * @param loader classloader through which to load the resource [null
	     * is equivalent to the application loader]
	     * 
	     * @return resource converted to java.util.Properties [may be null if the
	     * resource was not found and THROW_ON_LOAD_FAILURE is false]
	     * @throws IllegalArgumentException if the resource was not found and
	     * THROW_ON_LOAD_FAILURE is true
	     */
    
       public static Properties loadPropertiesFromClassPath (String name, ClassLoader loader)
	    {
	        if (name == null)
	            throw new IllegalArgumentException ("null input: name");
	        
	        if (name.startsWith ("/"))
	            name = name.substring (1);
	            
	        if (name.endsWith (SUFFIX))
	            name = name.substring (0, name.length () - SUFFIX.length ());
	        
	        Properties result = null;
	        
	        InputStream in = null;
	        try
	        {
	            if (loader == null) loader = ClassLoader.getSystemClassLoader ();
	            
	            if (LOAD_AS_RESOURCE_BUNDLE)
	            {    
	                name = name.replace ('/', '.');
	                // Throws MissingResourceException on lookup failures:
	                final ResourceBundle rb = ResourceBundle.getBundle (name,
	                    Locale.getDefault (), loader);
	                
	                result = new Properties ();
	                for (Enumeration keys = rb.getKeys (); keys.hasMoreElements ();)
	                {
	                    final String key = (String) keys.nextElement ();
	                    final String value = rb.getString (key);
	                    
	                    result.put (key, value);
	                } 
	            }
	            else
	            {
	                name = name.replace ('.', '/');
	                
	                if (! name.endsWith (SUFFIX))
	                    name = name.concat (SUFFIX);
	                                
	                // Returns null on lookup failures:
	                in = loader.getResourceAsStream (name);
	                if (in != null)
	                {
	                    result = new Properties ();
	                    result.load (in); // Can throw IOException
	                }
	            }
	        }
	        catch (Exception e)
	        {
	            result = null;
	        }
	        finally
	        {
	            if (in != null) try { in.close (); } catch (Throwable ignore) {}
	        }
	        
	        if (THROW_ON_LOAD_FAILURE && (result == null))
	        {
	            throw new IllegalArgumentException ("could not load [" + name + "]"+
	                " as " + (LOAD_AS_RESOURCE_BUNDLE
	                ? "a resource bundle"
	                : "a classloader resource"));
	        }
	        
	        return result;
	    }
	    
	    /**
	     * A convenience overload of {@link #loadProperties(String, ClassLoader)}
	     * that uses the current thread's context classloader.
	     */
	    public static Properties loadProperties (final String name)
	    {
	    	try{
	        	File file = new File(name);
	        	Properties properties = new Properties();
	        	if (file.exists()) {
	        		properties.load(new FileInputStream(file));
	        		if (properties.get(PropertiesConstant.IMPORT)!=null){
	        			String propertyValue = properties.get(PropertiesConstant.IMPORT).toString();
	        			ArrayList importFiles =  PropertyHelper.getArrayFromCommaSeperated(propertyValue);
	        			for (int i=0 ; i < importFiles.size();i++ ){
		        			File importFile = new File(file.getParent() + File.separatorChar + importFiles.get(i));
		        			properties.load(new FileInputStream(importFile));
	        			}
	        		}
	        	}
	        	properties.remove(PropertiesConstant.IMPORT);
	        	return properties;
	    	}catch(FileNotFoundException e){
	    		throw new IllegalArgumentException ("could not load [" + name + "]");
	    	}catch(IOException e){
	            throw new IllegalArgumentException ("could not load [" + name + "]");
	    	}
	    }
	        
	    public static void main(String[] args) {
			Properties props = loadProperties("C:\\jatin\\eclipse\\Aura-Config-Test-V61\\resources\\properties\\AvatarProcServer.properties");
			System.out.println("System out " + props);
		}
	} // End of class