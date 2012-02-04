package org.menacheri;

import org.apache.log4j.PropertyConfigurator;
import org.menacheri.server.IServerManager;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class starts the server running at the designated port on the localhost.
 * It defaults to port 8090 but can use any port specified at command prompt.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SpringNettyServer
{
	public static void main(String[] args)
	{
		PropertyConfigurator.configure(System
				.getProperty("log4j.configuration"));
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(
		"server-beans.xml");
		// For the destroy method to work.
		context.registerShutdownHook();
		
		// Start tcp and flash servers
		IServerManager manager = (IServerManager)context.getBean("serverManager");
		manager.startServers(8090,843,8081);
	}
	
	
}

