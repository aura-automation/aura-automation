import org.apache.commons.configuration.*
import org.apache.commons.lang.SystemUtils
public class AuraXML{

	def auraHome = null
	def auraRepo = null
	def userHome = null
	def user = null
	
	def antEnvName = null
	def noPrompt = null
	def currentDir = null
	def ant = null
	def date = null

	void loadAuraXML(){
		def auraXML = new File(currentDir + File.separator + "aura.xml")
		def resources = "resources"
		def properties = "properties"
		def earlocation = "ear"
		def deploydatalocation = "ear/deploydata"
		def unixuser = user
		def keyfile = userHome + File.separator + "id_rsa"
		def webserverPort = "8080"
		def outputFileBase=auraRepo + "/resources/" + date +"/"

		if (auraXML.exists()){
			XMLConfiguration config = new XMLConfiguration(auraXML)
			
			resources = config.getList("aura-was-config.resource-set.resource")?:resources
			properties = config.getProperty("aura-was-config.properties")?:properties

			earlocation = config.getProperty("aura-was-deploy.ear")?:earlocation
			deploydatalocation = config.getProperty("aura-was-deploy.deploydata")?:deploydatalocation
			unixuser = config.getProperty("aura-was-deploy.unix-user")?:unixuser
			keyfile = config.getProperty("aura-was-deploy.keyfile")?:keyfile
			webserverPort = config.getProperty("webserver-port")?:webserverPort
			outputFileBase = config.getProperty("aura-was-config.output-file-base-location")?currentDir + File.separator + config.getProperty("aura-was-config.output-file-base-location"):outputFileBase
			
							
		}else{
			println("using defaults, no xml found " + currentDir + File.separator + "aura.xml")
		}
		
		if (resources instanceof Collection){
			ant.project.setProperty('resource.location', resources.join(","))
		}else{
			ant.project.setProperty('resource.location', resources)
		}
		ant.project.setProperty('resource.property.location',  properties)
		ant.project.setProperty('ear.location',  earlocation)
		ant.project.setProperty('deploydata.location',  deploydatalocation)
		ant.project.setProperty('unix.user',  unixuser)
		ant.project.setProperty('keyfile',  keyfile)
		ant.project.setProperty('webserverPort',  webserverPort)
		ant.project.setProperty('outputFileBase',  outputFileBase)
		makeDirs(outputFileBase)
		
	}

	void makeDirs(dirname){
		new File(dirname).mkdirs()
	}

	void initProperties(){
		auraHome = ant.project.properties.'AURA_HOME'
		auraRepo = ant.project.properties.'AURA_REPO'
		antEnvName = ant.project.properties.'env.name'
		currentDir = ant.project.properties.'CURRENT_DIR'
		noPrompt = ant.project.properties.'noprompt'
		user = ant.project.properties.'USER'
		userHome = ant.project.properties.'USER_HOME'
		date = ant.project.properties.'date'
	
	}
	
	
	void setAntBuilder(antBuilder) {
		ant = new AntBuilder(antBuilder.project)
		antBuilder.project.copyInheritedProperties(ant.project)
		antBuilder.project.copyUserProperties(ant.project)
		
		initProperties()
	}
}
