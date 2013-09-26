import static org.mortbay.jetty.Handler.DEFAULT
import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context
import org.mortbay.jetty.servlet.DefaultServlet
import org.mortbay.servlet.MultiPartFilter
import org.mortbay.jetty.handler.ResourceHandler
import org.mortbay.jetty.handler.HandlerList
import org.mortbay.jetty.handler.HandlerList
import org.mortbay.jetty.handler.DefaultHandler

import groovy.servlet.*


public class JettyServer{
	def auraHome = null
	def auraRepo = null
	def userHome = null
	def user = null
	def webserverPort = null
	
	def antEnvName = null
	def noPrompt = null
	def currentDir = null
	def ant = null

	void startServer(){

		ResourceHandler resource_handler = new ResourceHandler();
 
 
		HandlerList handlers = new HandlerList();
		 
		handlers.addHandler(resource_handler) 
		handlers.addHandler(new DefaultHandler() )
		
		new Server(webserverPort.toInteger()).with{
			
			
			def source = new File(auraHome + File.separator + "var" + File.separator + "scripts" )
			def destinationFolder = new File(auraRepo + File.separator + "web" )
			destinationFolder.delete()
			destinationFolder.mkdirs()
			def destination = new File(auraRepo + File.separator + "web" + File.separator + "Reports.groovy")
			
		//	source.withInputStream { is ->
		//		destination << is
		//}
			
			
			new Context(it, '/', Context.SESSIONS).with{
				//resourceBase = auraRepo + File.separator + "web"
				resourceBase = source 
				
				addFilter MultiPartFilter, '/*', DEFAULT
				
				addServlet(DefaultServlet, '*.json')

				addServlet(DefaultServlet, '*.html')

				addServlet(TemplateServlet, '/view/*').with {
					 setInitParameter 'resource.name.regex', '/view(.*)'
					 setInitParameter 'resource.name.replacement', 'gsp/$1.gsp'
				}
				
				addServlet(GroovyServlet, '*.groovy')
			} 

			
		//	server.setHandler(handlers);
			
			start()
			join()
		}
	}
	
	void initProperties(){
		auraHome = ant.project.properties.'AURA_HOME'
		auraRepo = ant.project.properties.'AURA_REPO'
		antEnvName = ant.project.properties.'env.name'
		webserverPort = ant.project.properties.'webserverPort'
		
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

