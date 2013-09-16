package io.nadron.example;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.impl.SimpleGame;
import io.nadron.app.impl.GameRoomSession.GameRoomSessionBuilder;
import io.nadron.example.lostdecade.LDRoom;
import io.nadron.example.zombie.domain.Defender;
import io.nadron.example.zombie.domain.World;
import io.nadron.example.zombie.domain.Zombie;
import io.nadron.example.zombie.game.ZombieRoom;
import io.nadron.handlers.netty.TextWebsocketEncoder;
import io.nadron.protocols.Protocol;
import io.nadron.protocols.impl.NettyObjectProtocol;
import io.nadron.service.LookupService;
import io.nadron.service.impl.SimpleLookupService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * This class contains the spring configuration for the nadron library user.
 * The only bean that is compulsory to be declared is lookupService bean.
 * Otherwise the program will terminate with a context load error from spring
 * framework. The other beans declared can also be created using **new**
 * operator as per programmer convenience.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Configuration
@ImportResource("classpath:/beans/beans.xml")
public class SpringConfig
{
	@Autowired
	@Qualifier("messageBufferProtocol")
	private Protocol messageBufferProtocol;

	@Autowired
	@Qualifier("webSocketProtocol")
	private Protocol webSocketProtocol;

	@Autowired
	@Qualifier("textWebsocketEncoder")
	private TextWebsocketEncoder textWebsocketEncoder;

	@Autowired
	@Qualifier("nettyObjectProtocol")
	private NettyObjectProtocol nettyObjectProtocol;
	
	public @Bean
	Game zombieGame()
	{
		Game game = new SimpleGame(1, "Zombie");
		return game;
	}

	public @Bean(name = "Zombie_Rooms")
	List<GameRoom> zombieRooms()
	{
		List<GameRoom> roomList = new ArrayList<GameRoom>(500);
		for (int i = 1; i <= 500; i++)
		{
			GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
			sessionBuilder.parentGame(zombieGame())
					.gameRoomName("Zombie_ROOM_" + i)
					.protocol(messageBufferProtocol);
			ZombieRoom room = new ZombieRoom(sessionBuilder);
			room.setDefender(defender());
			room.setZombie(zombie());
			roomList.add(room);
		}
		return roomList;
	}

	public @Bean(name = "Zombie_Room_Websocket")
	GameRoom zombieRoom2()
	{
		GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
		sessionBuilder.parentGame(zombieGame())
				.gameRoomName("Zombie_Room_Websocket")
				.protocol(webSocketProtocol);
		ZombieRoom room = new ZombieRoom(sessionBuilder);
		room.setDefender(defender());
		room.setZombie(zombie());

		return room;
	}

	public @Bean
	World world()
	{
		World world = new World();
		world.setAlive(2000000000);
		world.setUndead(1);
		return world;
	}

	public @Bean
	Defender defender()
	{
		Defender defender = new Defender();
		defender.setWorld(world());
		return defender;
	}

	public @Bean
	Zombie zombie()
	{
		Zombie zombie = new Zombie();
		zombie.setWorld(world());
		return zombie;
	}

	public @Bean(name = "LDGame")
	Game ldGame()
	{
		return new SimpleGame(2, "LDGame");
	}

	public @Bean(name = "LDGameRoom")
	GameRoom ldGameRoom()
	{
		GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
		sessionBuilder.parentGame(ldGame()).gameRoomName("LDGameRoom")
				.protocol(webSocketProtocol);
		LDRoom room = new LDRoom(sessionBuilder);
		return room;
	}

	public @Bean(name = "LDGameRoomForNettyClient")
	GameRoom ldGameRoomForNettyClient()
	{
		GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
		sessionBuilder.parentGame(ldGame()).gameRoomName("LDGameRoomForNettyClient")
				.protocol(nettyObjectProtocol);
		LDRoom room = new LDRoom(sessionBuilder);
		return room;
	}
	
	public @Bean(name = "lookupService")
	LookupService lookupService()
	{
		Map<String, GameRoom> refKeyGameRoomMap = new HashMap<String, GameRoom>();
		List<GameRoom> zombieRooms = zombieRooms();
		for (GameRoom room : zombieRooms)
		{
			refKeyGameRoomMap.put(room.getGameRoomName(), room);
		}
		refKeyGameRoomMap.put("Zombie_ROOM_1_REF_KEY_2", zombieRoom2());
		refKeyGameRoomMap.put("LDGameRoom", ldGameRoom());
		refKeyGameRoomMap.put("LDGameRoomForNettyClient", ldGameRoomForNettyClient());
		LookupService service = new SimpleLookupService(refKeyGameRoomMap);
		return service;
	}
}
