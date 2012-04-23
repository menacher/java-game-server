package org.menacheri;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.Task;
import org.menacheri.jetserver.server.ServerManager;
import org.menacheri.jetserver.service.TaskManagerService;
import org.menacheri.zombie.domain.World;
import org.menacheri.zombie.domain.WorldMonitor;
import org.menacheri.zombie.game.ZombieRoom;
import org.menacheri.zombie.game.ZombieSpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;


public class GameServer
{
	private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
	
	public static void main(String[] args)
	{
		PropertyConfigurator.configure(System
				.getProperty("log4j.configuration"));
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(ZombieSpringConfig.class);
		// For the destroy method to work.
		ctx.registerShutdownHook();
		
		// Start the main game server
		ServerManager serverManager = ctx.getBean(ServerManager.class);
		//serverManager.startServers(18090,843,8081);
		try
		{
			serverManager.startServers();
		}
		catch (Exception e)
		{
			LOG.error("Unable to start servers cleanly: {}",e);
		}
		System.out.println("Started servers");
		startGames(ctx);
	}
	
	public static void startGames(AbstractApplicationContext ctx)
	{
		World world = ctx.getBean(World.class);
		GameRoom room = ctx.getBean(ZombieRoom.class);
		Task monitor = new WorldMonitor(world,room);
		TaskManagerService taskManager = ctx.getBean(TaskManagerService.class);
		taskManager.scheduleWithFixedDelay(monitor, 500, 5000, TimeUnit.MILLISECONDS);
	}
	
}
