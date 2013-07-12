import org.apache.commons.lang.SystemUtils

public class Common {
	def nonInteractive = false
	def ant
	def systemIn = System.in.newReader()
	
	def Common(myant){
		ant = myant
	}
		
	private String prompt(promptText) {
		return prompt(null, promptText, null)
	}

	private String prompt(curValue, promptText) {
		return prompt(curValue, promptText, null)
	}

	private String prompt(curValue, promptText, defaultValue) {
		return prompt(curValue, promptText, defaultValue, null)
	}

	private String prompt(curValue, promptText, defaultValue, validator) {
		// use curValue if not null and not empty
		if (curValue != null && curValue.trim()) {
			return curValue
		}

		if (nonInteractive) {
			println(promptText)

			def warningMessage = 'Warning: Installer prompting for input in non-interactive mode.'
			if (defaultValue) {
				warningMessage += '  Returning default: ' + defaultValue
			}
			println(warningMessage)

		 /**   if (validator != null) {
				try {
					validator.validate(defaultValue)
				} catch (ValidationException ve) {
					throw new IllegalArgumentException(
							"Non-Interactive Mode: problem with default value of '${defaultValue}' " +
							"for '${promptText}' - " + ve.getValidationMessageArray().join(' '))
				}
			} **/
			return defaultValue
		}

		def userValue = null
		def valid = false
		while (!valid) {
			println(promptText)
			userValue = read(defaultValue)
		valid = true
		 /**   if (validator != null) {
				try {
					validator.validate(userValue)
					valid = true
				}
				catch (ValidationException ve) {
					for (message in ve.getValidationMessageArray()) {
						println(message)
					}
				}
			}
			else {
				valid = true
			} **/
	
		}

		return userValue
	}
		
	private String read(defaultValue) {
		def line = systemIn.readLine()?.trim()
		return line ?: defaultValue
	}

	private void println(displayText) {
			if (displayText != null) {
				ant.echo(displayText)
			}
	}

}