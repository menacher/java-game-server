package org.menacheri.zombie.game;

import java.util.HashMap;
import java.util.Map;

import org.menacheri.jetserver.app.IGame;
import org.menacheri.jetserver.app.IGameRoom;
import org.menacheri.jetserver.app.impl.Game;
import org.menacheri.jetserver.app.impl.GameRoomSession.GameRoomSessionBuilder;
import org.menacheri.jetserver.protocols.IProtocol;
import org.menacheri.jetserver.service.ILookupService;
import org.menacheri.jetserver.service.impl.LookupService;
import org.menacheri.zombie.domain.Defender;
import org.menacheri.zombie.domain.World;
import org.menacheri.zombie.domain.Zombie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * This class contains the spring configuration for the jetserver library user.
 * The only bean that should compulsorily be declared is lookupService bean.
 * Otherwise the program will terminate with a context load error from spring
 * framework. The other beans declared can also be created using **new**
 * operator as per programmer convenience.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Configuration
@ImportResource("classpath:/beans/beans.xml")
public class ZombieSpringConfig
{
	@Autowired
	@Qualifier("messageBufferProtocol")
	private IProtocol messageBufferProtocol;
	
//	@Autowired
//	private ILookupService lookupService;
	
	public @Bean IGame zombieGame()
	{
		Game game = new Game(1,"Zombie");
		return game;
	}
	
	public @Bean IGameRoom zombieRoom1()
	{
		GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
		sessionBuilder.parentGame(zombieGame()).gameRoomName("Zombie_ROOM_1").protocol(messageBufferProtocol);
		ZombieRoom room = new ZombieRoom(sessionBuilder);
		room.setDefender(defender());
		room.setZombie(zombie());
		
		return room;
	}
	
	public @Bean World world()
	{
		World world = new World();
		world.setAlive(2000000000);
		world.setUndead(1);
		return world;
	}
	
	public @Bean Defender defender()
	{
		Defender defender = new Defender();
		defender.setWorld(world());
		return defender;
	}
	
	public @Bean Zombie zombie()
	{
		Zombie zombie = new Zombie();
		zombie.setWorld(world());
		return zombie;
	}
	
	
	public @Bean(name="lookupService") ILookupService lookupService()
	{
		Map<String,IGameRoom> refKeyGameRoomMap = new HashMap<String, IGameRoom>();
		refKeyGameRoomMap.put("Zombie_ROOM_1_REF_KEY_1", zombieRoom1());
		LookupService service = new LookupService(refKeyGameRoomMap);
		return service;
	}
}
