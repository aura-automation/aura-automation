import static org.mortbay.jetty.Handler.DEFAULT
import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context
import org.mortbay.jetty.servlet.DefaultServlet
import org.mortbay.servlet.MultiPartFilter
import groovy.servlet.*


public class JettyServer{


	void startServer(){

		new Server(8080).with{
			new Context(it, '/groovy', Context.SESSIONS).with{
				resourceBase = '/home/jatin/.aura/'
				addFilter MultiPartFilter, '/*', DEFAULT

			addServlet(GroovyServlet, '*.groovy')
	
			} 

			start()
			join()
		}
	}
}

