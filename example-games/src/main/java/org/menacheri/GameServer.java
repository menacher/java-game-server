package org.menacheri;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.ITask;
import org.menacheri.context.AppContext;
import org.menacheri.server.IServerManager;
import org.menacheri.service.ITaskManagerService;
import org.menacheri.zombie.domain.World;
import org.menacheri.zombie.domain.WorldMonitor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class GameServer
{
	
	public static void main(String[] args)
	{
		PropertyConfigurator.configure(System
				.getProperty("log4j.configuration"));
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(
		"/beans/beans.xml");
		
		// For the destroy method to work.
		context.registerShutdownHook();
		
		// Start the main game server
		IServerManager serverManager = (IServerManager)AppContext.getBean(AppContext.SERVER_MANAGER);
		//serverManager.startServers(8090,843,8081);
		serverManager.startServers(8090,843,8090);
		System.out.println("Started servers");
		startGames();
	}
	
	public static void startGames()
	{
		World world = (World) AppContext.getBean("world");
		world.setAlive(2000000000);
		IGameRoom room = (IGameRoom)AppContext.getBean("Zombie_ROOM_1");
		ITask monitor = new WorldMonitor(world,room);
		ITaskManagerService taskManager = (ITaskManagerService)AppContext.getBean(AppContext.TASK_MANAGER_SERVICE);
		taskManager.scheduleWithFixedDelay(monitor, 500, 5000, TimeUnit.MILLISECONDS);
	}
	
}
