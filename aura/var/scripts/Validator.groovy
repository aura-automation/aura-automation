import org.apache.commons.lang.SystemUtils

public class Validator{
	
	boolean fileValidation = false
	boolean propsFileValidation = false
	boolean hasString = false
	
	def subString
	boolean valid = true
	
	
	void addFileValidation(){
		fileValidation = true
	} 
	
	void hasString(message){
		hasString = true
		subString = message
	}
	
	void addPropsFileValidation(){
		propsFileValidation = true
	}
	
	boolean validate(String message){
		
		if (hasString){
			if (message.indexOf(subString) == -1 ){
				println("Incorrect file location, must have " + subString)
				valid = false
			}
		}
		
		if (fileValidation){
			if (! (new File(message)).exists()){
				println("File " + message + " location missing")
				valid = false
			}
		}
		
		if (propsFileValidation){
			if (! (new File(message + File.separator + "soap.client.props")).exists()){
				println("soap.client.props " + message + " does not exists")
				valid = false
			}
			if (! (new File(message + File.separator + "sas.client.props")).exists()){
				println("sas.client.props " + message + " does not exists")
				valid = false
			}
			if (! (new File(message + File.separator + "ssl.client.props")).exists()){
				println("ssl.client.props " + message + " does not exists")
				valid = false
			}

		}
		return valid
	}
}