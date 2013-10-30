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
		installPlugin()
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
	
	boolean shouldPrompt(String fileDir,String fileName){
		println "checking $fileDir file $fileName"
		def shouldPrompt = true	
		if ((new File(fileDir)).exists()){
			((new File(fileDir))).eachFile{
				if (it.name =~ fileName)
					shouldPrompt = false
			}
		}
		shouldPrompt
	}
	
	void promptAdminClientJar(){
		def fileName = "com.ibm.ws.admin.client"
		if (shouldPrompt(libDir,fileName)){	
			Validator validator = new Validator()
			validator.addFileValidation(fileName)
			validator.hasString()
			
			def adminClientJar = common.prompt(null, "Enter the location of com.ibm.ws.admin.client jar e.g. c:/WebSphere/AppServer/runtimes/com.ibm.ws.admin.client_8.0.0.jar","",validator)
			
			copy (adminClientJar , libDir)
		}
	}
	
	void promptCryptoJar(){
		def fileName = "com.ibm.ws.security.crypto"
		if (shouldPrompt(libDir,fileName)){	

			Validator validator = new Validator()
			validator.addFileValidation(fileName)
			validator.hasString()
			
			def cryptoJar = common.prompt(null, "Enter the location of com.ibm.ws.security.crypto jar e.g. c:/WebSphere/AppServer/runtimes/security.crypto.jar","",validator)
			
			copy (cryptoJar , libDir)
		}
	}
	
	void promptSecProperties(){
		Validator validator = new Validator()
		validator.addPropsFileValidation()

		def fileName = "com.ibm.ws.security.crypto"
		if ( shouldPrompt(propsDir,"sas.client.props") 
			|| shouldPrompt(propsDir, "soap.client.props") 
			|| shouldPrompt(propsDir, "ssl.client.props")
			){	
		
			def secPropsDir = common.prompt(null, "Enter the location of dir containing sas, soap client props e.g. c:/WebSphere/AppServer/properties","",validator)
			
			copy (secPropsDir + File.separator + "sas.client.props" , propsDir) 
			copy (secPropsDir + File.separator + "soap.client.props" , propsDir) 
			copy (secPropsDir + File.separator + "ssl.client.props" , propsDir)
			// parse the ssl config file and change user root to plugins dir. This is to enable loading of certs
			ConfigurationManager confManager = new ConfigurationManager()
			confManager.setProperty(propsDir + File.separator + "ssl.client.props",
				,"user.root"
				,plugindir)
			}

	}
	
	void promptSSLStore(){
	if ( shouldPrompt(etcDir,"key") 
			|| shouldPrompt(etcDir, "trust") 
			){	
			Validator validator = new Validator()
			validator.addFileValidation()
	
			def keyStoreFile = common.prompt(null, "Enter the location of key store file,  e.g. c:/WebSphere/AppServer/etc/key.p12","",validator)	
			def trustStoreFile = common.prompt(null, "Enter the location of trust store file e.g. c:/WebSphere/AppServer/etc/trust.p12","",validator)
			
			copy (keyStoreFile, etcDir)
			copy (trustStoreFile, etcDir)
		}
			
	}
	
	boolean checkWASPlugin(){
		println("Check if plugin installed at the location " + auraRepo)
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
