package org.menacheri;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.ITask;
import org.menacheri.server.IServerManager;
import org.menacheri.service.ITaskManagerService;
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
		IServerManager serverManager = ctx.getBean(IServerManager.class);
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
		IGameRoom room = ctx.getBean(ZombieRoom.class);
		ITask monitor = new WorldMonitor(world,room);
		ITaskManagerService taskManager = ctx.getBean(ITaskManagerService.class);
		taskManager.scheduleWithFixedDelay(monitor, 500, 5000, TimeUnit.MILLISECONDS);
	}
	
}
