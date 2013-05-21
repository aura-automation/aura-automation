import org.apache.commons.lang.SystemUtils

public class WASInstaller {

    def classpath = null
    def repoDirectoryName = null
    def IMDirectoryName = null
    def wasInstalllocation = null
    def systemIn = System.in.newReader()
    def dmgrName = null
    def dmgrHost = null
    def nodeName = null
    def cellName = null
    def startingNodePortNumber = null
    def startingDmgrPortNumber = null
    def ant = null
    def shellExt = null
    def exeExt = null

	WASInstaller(classpath) {
		this.classpath = classpath
	}

	void initProperties(){
	//	repoDirectoryName = ant.project.properties.'repoDirectoryName'
		repoDirectoryName= ant.project.properties.'repoDirectoryName'
		IMDirectoryName= ant.project.properties.'IMDirectoryName'
		wasInstalllocation= ant.project.properties.'wasInstalllocation'
		nodeName= ant.project.properties.'nodeName'
		dmgrName= ant.project.properties.'dmgrName'
		dmgrHost= ant.project.properties.'dmgrHost'
		startingDmgrPortNumber= ant.project.properties.'startingDmgrPortNumber'
		startingNodePortNumber= ant.project.properties.'startingNodePortNumber'
		cellName= ant.project.properties.'cellName'

		println " SystemUtils.IS_OS_MAC " + SystemUtils.IS_OS_MAC
		println " SystemUtils.IS_OS_WINDOWS " +  SystemUtils.IS_OS_WINDOWS
		println " SystemUtils.IS_OS_UNIX " +  SystemUtils.IS_OS_UNIX
		if (SystemUtils.IS_OS_UNIX){
			shellExt = '.sh'
			exeExt = ""
		}else if (SystemUtils.IS_OS_WINDOWS){
			shellExt = '.bat'
			exeExt = ".exe"
		}

	}	

	void setAntBuilder(antBuilder) {
		ant = new AntBuilder(antBuilder.project)
		antBuilder.project.copyInheritedProperties(ant.project)
		antBuilder.project.copyUserProperties(ant.project)
		
		initProperties()
	}

	void executeProcess(cmd){
		def proc = cmd.execute()
		def sout = new StringBuffer(), serr = new StringBuffer()
		proc.consumeProcessOutput(sout, serr)
		proc.waitFor()
		println "out> $sout err> $serr"
	//	println "return code: ${ proc.exitValue()}"
	//	println "stderr: ${proc.err.text}"
	//	println "stdout: ${proc.in.text}"


	}

	void listProfiles() {
		def cmd=wasInstalllocation + "//bin//manageprofiles.bat -listProfiles"
		executeProcess(cmd) 
	}

	void deleteAllProfiles() {
		println("Deleting All Profiles")
		def cmd=wasInstalllocation + "//bin//manageprofiles"+ shellExt +" -deleteAll"
		executeProcess(cmd) 
	}

	void deleteDmgrProfile() {
		println("Deleting Dmgr Profile")
		def cmd=wasInstalllocation + "//bin//manageprofiles"+ shellExt +" -deleteAll"
		executeProcess(cmd) 
	}

	void deleteNodeProfile() {
		println("Deleting Node Profile")
		def cmd=wasInstalllocation + "//bin//manageprofiles"+ shellExt +" -deleteAll"
		executeProcess(cmd) 
	}

	void createDmgrProfile() {
		println("Create Dmgr Profile")
		def cmd=wasInstalllocation + "//bin//manageprofiles"+ shellExt +" -create -cellName " + cellName + "  -profileName " + dmgrName + " -profilePath " +  wasInstalllocation + "//profiles//" + dmgrName + " -templatePath " + wasInstalllocation + "//profileTemplates//management -serverType DEPLOYMENT_MANAGER -startingPort " + startingDmgrPortNumber
		executeProcess(cmd) 
	}

	void createNodeProfile() {
		println("Create Node Profile")
		def dmgrPortNumber = new Integer(startingDmgrPortNumber).intValue() + 3 
		def cmd=wasInstalllocation + "//bin//manageprofiles"+ shellExt +" -create -profileName " + nodeName + " -profilePath " +  wasInstalllocation + "//profiles//" + nodeName + " -templatePath " + wasInstalllocation + "//profileTemplates//managed -startingPort " + startingNodePortNumber + " -dmgrHost "  + dmgrHost  + " -dmgrPort " + dmgrPortNumber
		println(cmd)
		executeProcess(cmd) 
	}
	
	void prereqCheck(){
		new AntBuilder().delete(dir: wasInstalllocation)
	}

	void wasInstall(responseFile) {
		prereqCheck()	
		def cmd=IMDirectoryName + "//imcl"+ exeExt +" input " + responseFile + " -acceptLicense"
		println("Installing WAS:" + cmd)
		executeProcess(cmd) 
	}

	void wasUnInstall(responseFile) {
		println("UnInstalling WAS")
		def cmd=IMDirectoryName + "//imcl"+ exeExt +" input " + responseFile + "  -acceptLicense"
		executeProcess(cmd) 
	}

	void uninstall(responseFile) {
		stopNode()
		stopManager()
		deleteAllProfiles()
		wasUnInstall(responseFile)
	}

	void installAll(responseFile) {

		wasInstall(responseFile)
		createDmgrProfile()
		startManager()
		createNodeProfile()
		startNode()
		
	}

	void stopNode() {
		println("Stop Node")
		def cmd=wasInstalllocation + "/profiles/" + nodeName + "/bin/stopNode"+ shellExt 
		executeProcess(cmd) 
	}

	void stopManager() {
		println("Stop Manager")
		def cmd=wasInstalllocation + "//profiles//" + dmgrName + "//bin//stopManager"+ shellExt
		executeProcess(cmd) 
	}

	void startNode() {
		def cmd=wasInstalllocation + "/profiles/" + nodeName + "/bin/startNode"+ shellExt
		println("Start Node: " + cmd)
		executeProcess(cmd) 
	}

	void startManager() {
		println("Start Manager")
		def cmd=wasInstalllocation + "//profiles//" + dmgrName + "//bin//startManager"+ shellExt
		executeProcess(cmd) 
	}

	private String prompt(promptText) {
		return prompt(null, promptText, null)
	}


	private String prompt(curValue, promptText, defaultValue) {
	        // use curValue if not null and not empty
	        if (curValue != null && curValue.trim()) {
	            return curValue
	        }
	
	       
	
	        def userValue = null
	        def valid = false
	        while (!valid) {
	            println(promptText)
	            userValue = read(defaultValue)
		    valid = true
	          
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
