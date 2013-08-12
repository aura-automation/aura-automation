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
	
	void loadAuraXML(){
		def auraXML = new File(currentDir + File.separator + "aura.xml")
		def resources = "resources"
		def properties = "properties"
		def earlocation = "ear"
		def deploydatalocation = "ear/deploydata"
		def unixuser = user
		def keyfile = userHome + File.separator + "id_rsa"

		if (auraXML.exists()){
			XMLConfiguration config = new XMLConfiguration(auraXML)
			
			resources = config.getList("aura-was-config.resource-set.resource")?:resources
			properties = config.getProperty("aura-was-config.properties")?:properties

			earlocation = config.getProperty("aura-was-deploy.ear")?:earlocation
			deploydatalocation = config.getProperty("aura-was-deploy.deploydata")?:deploydatalocation
			unixuser = config.getProperty("aura-was-deploy.unix-user")?:unixuser
			keyfile = config.getProperty("aura-was-deploy.keyfile")?:keyfile
							
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
	}

	void initProperties(){
		auraHome = ant.project.properties.'AURA_HOME'
		auraRepo = ant.project.properties.'AURA_REPO'
		antEnvName = ant.project.properties.'env.name'
		currentDir = ant.project.properties.'CURRENT_DIR'
		noPrompt = ant.project.properties.'noprompt'
		user = ant.project.properties.'USER'
		userHome = ant.project.properties.'USER_HOME'
	
	}
	
	
	void setAntBuilder(antBuilder) {
		ant = new AntBuilder(antBuilder.project)
		antBuilder.project.copyInheritedProperties(ant.project)
		antBuilder.project.copyUserProperties(ant.project)
		
		initProperties()
	}
}