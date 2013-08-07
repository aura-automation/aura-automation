import org.apache.commons.configuration.*
import org.apache.commons.lang.SystemUtils
public class AuraXML{

	def auraHome = null
	def auraRepo = null
	def antEnvName = null
	def noPrompt = null
	def currentDir = null
	def ant = null
	
	void loadAuraXML(){
		def auraXML = new File(currentDir + File.separator + "aura.xml")
		def resources = "resources"
		def properties = "properties"
		
		if (auraXML.exists()){
			XMLConfiguration config = new XMLConfiguration(auraXML)
			
			resources = config.getList("aura-was-config.resource-set.resource")?:resources
			properties = config.getProperty("aura-was-config.properties")?:properties
			
		}else{
			println("using defaults, no xml found " + currentDir + File.separator + "aura.xml")
		}
		
		if (resources instanceof Collection){
			ant.project.setProperty('resource.location', resources.join(","))
		}else{
			ant.project.setProperty('resource.location', resources)
		}
		ant.project.setProperty('resource.property.location',  properties)
	}

	void initProperties(){
		auraHome = ant.project.properties.'AURA_HOME'
		auraRepo = ant.project.properties.'AURA_REPO'
		antEnvName = ant.project.properties.'env.name'
		currentDir = ant.project.properties.'CURRENT_DIR'
		noPrompt = ant.project.properties.'noprompt'
		
	
	}
	
	
	void setAntBuilder(antBuilder) {
		ant = new AntBuilder(antBuilder.project)
		antBuilder.project.copyInheritedProperties(ant.project)
		antBuilder.project.copyUserProperties(ant.project)
		
		initProperties()
	}
}