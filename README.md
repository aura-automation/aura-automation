This code code of aura-automation for WebSphere release automation. Using Aura you can autome 

- Application Deloyment with lots of EAR configurations
- Application operations like uninstall, install, stop, start, update, export
- WebSphere configuration automation
-   Create configuration 
-   Preview configuration changes
-   Monitor configurations for drift from know set
-   Extract configuration
-   Transform configuration to be environment augnostics
- Rule based configuration management
- ANT based hence easy to integrated with any existing scripts

Build Instruction
- Build is maven based
- WebSphere jars must be added to you local repo. Currect version is dependent on below IBM jars  
  <dependency>
		<groupId>com.ibm.websphere.thin.client</groupId>
		<artifactId>com.ibm.ws.admin.client</artifactId>
		<version>6.1.0</version>
	</dependency>
	<dependency>
		<groupId>com.ibm.websphere.thin.client</groupId>
		<artifactId>com.ibm.ws.security.crypto</artifactId>
		<version>6.1.0</version>
	</dependency>

