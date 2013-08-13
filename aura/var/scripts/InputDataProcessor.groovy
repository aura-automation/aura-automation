import org.apache.commons.lang.SystemUtils
import org.aa.common.encryption.Encrypt

public class InputDataProcessor {

    def nonInteractive = false
	boolean noPrompt = false
    def auraHome = null
	def auraRepo = null	
    def ant = null
    def shellExt = null
    def exeExt = null
	def Common = null
	def antEnvName = null
	def targetInvoked = null
	Encrypt encrypt = new Encrypt ();
	
	void process(){
		
		def envName
		if ((noPrompt == null) ||  (noPrompt  == "") || (!noPrompt)){
			listEnvs()
			envName = promptEnv()
		}else{
			envName = antEnvName
		}
		def envFileName =  auraRepo + File.separator + "environments" + File.separator + envName
		def envFile = new File(envFileName)
		boolean fileExists = checkIfEnvDataExists(envFile)
		
		// if noPrompt is true and env does not exists, error
		if((!envFile.exists()) && (noPrompt != null) && (noPrompt)){
			ant.fail("env data missing, cannot run in noprompt mode")
		}else if (!envFile.exists()){
			getEnvData(envFileName)
		}else{
			confirmEnvDataIsCorrect(envFileName)
		}

		ant.project.setProperty('wasPassword.uncoded',encrypt.getDecryptedProperty("wasPassword", envFileName, "jayst"))
		
		ant.project.setProperty('env.file',envFileName)
		ant.project.setProperty('env.name',envName)
	}

	def promptEnv() {
		def envName = common.prompt ("Enter environment name")
		return envName 
		
	}

	void listEnvs(){
		def envFileDir =  auraRepo + File.separator + "environments" 
		def dir = new File(envFileDir)
		dir.mkdirs()
		println('Below is comma seperated list of known environments')
		int idx = 0
		dir.eachFile {  
		    if (it.isFile()) {
			print it.name
			print ", "
		    }
		}
	}

	void listResources(){
		def scope = ant.project.properties.'scope'
		def resourceDir =  auraHome + '/resources/extractTemplates/' + scope
		def dir = new File(resourceDir)
		println('Below is list of resources that can extracted')
		dir.eachFile { 
			if (it.isFile()) {
				print it.name.trim().replace("Resource.xml","")
				print "    "
			}
		}
	}


	boolean checkIfEnvDataExists(envFile){
		return envFile.exists()
	}

	void confirmEnvDataIsCorrect(envFileName){
		def props = new Properties()
		new File(envFileName).withInputStream{
			stream -> props.load(stream)
		}
		println("---------------------------------")
		println("host name " + props["dmgrHostName"])
		println("host port " + props["dmgrPortNumber"])
		println("host conntype " + props["wasConnectionType"])
		println("host user " + props["wasUserName"])
		println("---------------------------------")
		if (!noPrompt){
			def action = common.prompt ("Do you want to modify[m], continue [c] or abort [o]")
			if (action.equalsIgnoreCase("m")){
				modify(envFileName)
			}
		}
	}

	void modify(envFileName){
		def props = new Properties()
		new File(envFileName).withInputStream{
			stream -> props.load(stream)
		}
		def hostname = common.prompt ("" , "Enter Host Name (" + props["dmgrHostName"] + ")" ,props["dmgrHostName"])
		println "New host name is " + hostname
		def hostport = common.prompt ("" , "Enter Host Port (" + props["dmgrPortNumber"] + ")", props["dmgrPortNumber"] )
		def connType = common.prompt ("" , "Enter conntype (" + props["wasConnectionType"] + ")" , props["wasConnectionType"] )
		def userName = common.prompt ("" , "Enter User Name (" + props["wasUserName"] + ")"  , props["wasUserName"] )
		def password = common.prompt ("Enter Password")
		
		println("---------------------------------")
		println("host name " + hostname)
		println("host port " + hostport)
		println("host conntype " + connType)
		println("host user " + userName)
		println("---------------------------------")
		def action = common.prompt ("Do you want to modify[m], save and continue [c] or abort [o]")
		if (action.equalsIgnoreCase("m")){
			modify(envFileName)
		}else if (action.equalsIgnoreCase("c")){	
			saveEnvData(envFileName,hostname,hostport,connType,userName,encrypt.encryptString(password,"jayst"))
		} else {
			println("No data")
		}
	}

	void getEnvData(envFileName){

		def hostname = common.prompt ("Enter Host Name")
		def hostport = common.prompt ("Enter Host Port")
		def connType = common.prompt ("Enter conntype")
		def userName = common.prompt ("Enter User Name")
		def password = common.prompt ("Enter Password")
		saveEnvData(envFileName,hostname,hostport,connType,userName,password)
	}

	void promptDeployData(){
		if (!noPrompt){
			def clusterName = ""
			def nodeName = ""
			def serverName = ""
			def webServer = ""
			
			clusterName = common.prompt ("Enter Cluster Name")
			if ((clusterName ==null) || (clusterName.trim().equals(""))){ 
				serverName = common.prompt ("Enter Server Name")
				if ((serverName ==null)|| (serverName.trim().equals(""))){
					promptDeployData()
				}else{
					nodeName = common.prompt ("Enter Node Name")
				}
			}
			webserverName = common.prompt ("Enter WebServer")
			ant.project.setProperty('node.name',nodeName)
			ant.project.setProperty('server.name',serverName)
			ant.project.setProperty('cluster.name',clusterName)
			ant.project.setProperty('webserver.name',webserverName)
		}
	}
	
	void getClusterServerName(){
		def clusterName = common.prompt ("Enter Cluster Name")
		def serverName = common.prompt ("Enter Server Name")
	}
		
	void saveEnvData(envFileName,hostname,hostport,connType,userName,password){
		File envFile = new File(envFileName)
		envFile.delete()
		envFile << ("dmgrHostName=" + hostname)
		envFile << (System.getProperty("line.separator"))
		envFile << ("dmgrPortNumber=" + hostport)
		envFile << (System.getProperty("line.separator"))
		envFile << ("wasConnectionType=" + connType)
		envFile << (System.getProperty("line.separator"))
		envFile << ("wasUserName=" + userName)
		envFile << (System.getProperty("line.separator"))
		envFile << ("wasPassword=" + encrypt.encryptString(password,"jayst"))
	}

	void setAntProperties(envFile){
		ant.project.setProperty('env.file',envFile)
		ant.project.setProperty('env.file',envFile)
	}
	
	void initProperties(){
		auraHome = ant.project.properties.'AURA_HOME' 
		auraRepo = ant.project.properties.'AURA_REPO'
		antEnvName = ant.project.properties.'env.name'
		noPrompt = ant.project.properties.'noprompt'
		common = new Common(ant)
		targetInvoked = ant.project.properties.'targetInvoked'
		
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
}
