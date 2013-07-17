import org.apache.commons.lang.SystemUtils

public class PluginInstaller{
	
	def nonInteractive = false
	def noPrompt = false
	def auraHome = null
	def auraRepo = null
	def ant = null
	def Common = null
	
	def plugindir 
	def etcDir  
	def libDir 
	def propsDir 
	
	void process(){
		boolean pluginInstalled = checkWASPlugin()
		
		if (!pluginInstalled ){
			installPlugin()
		}
	}
	
	void installPlugin(){
		println("WAS Plugin is not installed")
		
		promptAdminClientJar()
		promptCryptoJar()
		promptSecProperties()
		promptSSLStore()
	}
	
	void copy(src,dest){
		def source = new File(src)
		new File(dest).mkdirs()
		def destination = new File(dest +File.separator + source.name)
		
		source.withInputStream { is ->
			destination << is
		}
		
		destination.lastModified = source.lastModified()
	}
	
	void promptAdminClientJar(){
		Validator validator = new Validator()
		validator.addFileValidation()
		validator.hasString("com.ibm.ws.admin.client")
		
		def adminClientJar = common.prompt(null, "Enter the location of com.ibm.ws.admin.client jar e.g. c:/WebSphere/AppServer/runtimes/com.ibm.ws.admin.client_8.0.0.jar","",validator)
		
		copy (adminClientJar , libDir)
	}
	
	void promptCryptoJar(){
		Validator validator = new Validator()
		validator.addFileValidation()
		validator.hasString("com.ibm.ws.security.crypto")
		
		def cryptoJar = common.prompt(null, "Enter the location of com.ibm.ws.security.crypto jar e.g. c:/WebSphere/AppServer/runtimes/security.crypto.jar","",validator)
		
		copy (cryptoJar , libDir)

	}
	
	void promptSecProperties(){
		Validator validator = new Validator()
		validator.addPropsFileValidation()

		def secPropsDir = common.prompt(null, "Enter the location of dir containing sas, soap client props e.g. c:/WebSphere/AppServer/properties","",validator)
		
		copy (secPropsDir + File.separator + "sas.client.props" , propsDir) 
		copy (secPropsDir + File.separator + "soap.client.props" , propsDir) 
		copy (secPropsDir + File.separator + "ssl.client.props" , propsDir) 
	}
	
	void promptSSLStore(){
		Validator validator = new Validator()
		validator.addFileValidation()

		def keyStoreFile = common.prompt(null, "Enter the location of key store file","",validator)	
		def trustStoreFile = common.prompt(null, "Enter the location of trust store file","",validator)
		
		copy (keyStoreFile, etcDir)
		copy (trustStoreFile, etcDir)
			
	}
	
	boolean checkWASPlugin(){
		println("Check if plugin installed in location " + auraRepo)
		boolean pluginInstalled = true
		if (!(new File(etcDir)).exists()){
			pluginInstalled = false
		}	
		if (!(new File(libDir)).exists()){
			pluginInstalled = false
		}	
		if (!(new File(propsDir)).exists()){
			pluginInstalled = false
		}
		return	pluginInstalled 
	}
	
	void initProperties(){
		auraHome = ant.project.properties.'AURA_HOME'
		auraRepo = ant.project.properties.'AURA_REPO'
		noPrompt = ant.project.properties.'noprompt'

		plugindir = auraRepo + File.separator + "plugins" + File.separator + "was-remote"
		etcDir = plugindir + File.separator + "etc" 
		libDir = plugindir + File.separator + "lib"
		propsDir = plugindir + File.separator + "properties"

		common = new Common(ant)	
	}
	
	
	void setAntBuilder(antBuilder) {
		ant = new AntBuilder(antBuilder.project)
		antBuilder.project.copyInheritedProperties(ant.project)
		antBuilder.project.copyUserProperties(ant.project)
		
		initProperties()
	}


	
}