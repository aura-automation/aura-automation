import org.apache.commons.configuration.*

public class ConfigurationManager{

	void setProperty(file,propertyName,propertyValue){
		println("Process configuration data")
		
		PropertiesConfiguration config = new PropertiesConfiguration(file)
		def currentValue =  (config.getProperty(propertyName))
		if (currentValue.equalsIgnoreCase(propertyValue)){
			println("value is correct")
		}else{
			println("value is not correct, will change to " + propertyValue)
			config.setProperty(propertyName,propertyValue)
			config.save()
		}
	}

}