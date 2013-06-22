package io.nadron.app.impl;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.SessionFactory;
import io.nadron.concurrent.LaneStrategy;
import io.nadron.concurrent.LaneStrategy.LaneStrategies;
import io.nadron.event.Event;
import io.nadron.event.EventHandler;
import io.nadron.event.Events;
import io.nadron.event.NetworkEvent;
import io.nadron.event.impl.EventDispatchers;
import io.nadron.event.impl.NetworkEventListener;
import io.nadron.protocols.Protocol;
import io.nadron.service.GameStateManagerService;
import io.nadron.service.impl.GameStateManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class GameRoomSession extends DefaultSession implements GameRoom
{
	private static final Logger LOG = LoggerFactory.getLogger(GameRoomSession.class);
	
	/**
	 * The name of the game room, preferably unique across multiple games.
	 */
	protected String gameRoomName;
	/**
	 * The parent {@link SimpleGame} reference of this game room.
	 */
	protected Game parentGame;
	/**
	 * Each game room has separate state manager instances. This variable will
	 * manage the state for all the {@link DefaultPlayer}s connected to this game room.
	 */
	protected GameStateManagerService stateManager;

	/**
	 * The set of sessions in this object.
	 */
	protected Set<PlayerSession> sessions;
	
	/**
	 * Each game room has its own protocol for communication with client.
	 */
	protected Protocol protocol;
	
	protected SessionFactory sessionFactory;
	
	protected GameRoomSession(GameRoomSessionBuilder gameRoomSessionBuilder)
	{
		super(gameRoomSessionBuilder);
		this.sessions = gameRoomSessionBuilder.sessions;
		this.parentGame = gameRoomSessionBuilder.parentGame;
		this.gameRoomName = gameRoomSessionBuilder.gameRoomName;
		this.protocol = gameRoomSessionBuilder.protocol;
		this.stateManager = gameRoomSessionBuilder.stateManager;
		this.sessionFactory = gameRoomSessionBuilder.sessionFactory;
		
		if(null == gameRoomSessionBuilder.eventDispatcher)
		{
			this.eventDispatcher = EventDispatchers.newJetlangEventDispatcher(
					this, gameRoomSessionBuilder.laneStrategy);
		}
	}
	
	public static class GameRoomSessionBuilder extends SessionBuilder
	{
		protected Set<PlayerSession> sessions;
		protected Game parentGame;
		protected String gameRoomName;
		protected Protocol protocol;
		protected LaneStrategy<String, ExecutorService, GameRoom> laneStrategy;
		protected GameStateManagerService stateManager;
		protected SessionFactory sessionFactory;
		
		@Override
		protected void validateAndSetValues()
		{
			if (null == id)
			{
				id = String.valueOf(ID_GENERATOR_SERVICE.generateFor(GameRoomSession.class));
			}
			if(null == sessionAttributes)
			{
				sessionAttributes = new HashMap<String, Object>();
			}
			if (null == sessions)
			{
				sessions = new HashSet<PlayerSession>();
			}
			if (null == laneStrategy)
			{
				laneStrategy = LaneStrategies.GROUP_BY_ROOM;
			}
			if(null == stateManager)
			{
				stateManager = new GameStateManager();
			}
			if(null == sessionFactory)
			{
				sessionFactory = Sessions.INSTANCE;
			}
			creationTime = System.currentTimeMillis();
		}
		
		public GameRoomSessionBuilder sessions(Set<PlayerSession> sessions)
		{
			this.sessions = sessions;
			return this;
		}
		
		public GameRoomSessionBuilder parentGame(Game parentGame)
		{
			this.parentGame = parentGame;
			return this;
		}
		
		public GameRoomSessionBuilder gameRoomName(String gameRoomName)
		{
			this.gameRoomName = gameRoomName;
			return this;
		}
		
		public GameRoomSessionBuilder protocol(Protocol protocol)
		{
			this.protocol = protocol;
			return this;
		}
		
		public GameRoomSessionBuilder laneStrategy(
				LaneStrategy<String, ExecutorService, GameRoom> laneStrategy)
		{
			this.laneStrategy = laneStrategy;
			return this;
		}

		public GameRoomSessionBuilder stateManager(
				GameStateManagerService gameStateManagerService)
		{
			this.stateManager = gameStateManagerService;
			return this;
		}
		
		public GameRoomSessionBuilder sessionFactory(SessionFactory sessionFactory)
		{
			this.sessionFactory = sessionFactory;
			return this;
		}
	}
	
	@Override
	public PlayerSession createPlayerSession(Player player)
	{
		PlayerSession playerSession = getSessionInstance(player);
		return playerSession;
	}
	
	@Override
	public abstract void onLogin(PlayerSession playerSession);
	
	@Override
	public synchronized boolean connectSession(PlayerSession playerSession)
	{
		if (!isShuttingDown)
		{
			playerSession.setStatus(Session.Status.CONNECTING);
			sessions.add(playerSession);
			playerSession.setGameRoom(this);
			LOG.trace("Protocol to be applied is: {}",protocol.getClass().getName());
			protocol.applyProtocol(playerSession,true);
			createAndAddEventHandlers(playerSession);
			playerSession.setStatus(Session.Status.CONNECTED);
			afterSessionConnect(playerSession);
			return true;
			// TODO send event to all other sessions?
		}
		else
		{
			LOG.warn("Game Room is shutting down, playerSession {} {}",
					playerSession,"will not be connected!");
			return false;
		}
	}

	@Override
	public void afterSessionConnect(PlayerSession playerSession)
	{
		GameStateManagerService manager = getStateManager();
		if(null != manager){
			Object state = manager.getState();
			if(null != state){
				playerSession.onEvent(Events.networkEvent(state));
			}
		}
	}
	
	public synchronized boolean disconnectSession(PlayerSession playerSession)
	{
		final boolean removeHandlers = this.eventDispatcher.removeHandlersForSession(playerSession);
		//playerSession.getEventDispatcher().clear(); // remove network handlers of the session.
		return (removeHandlers && sessions.remove(playerSession));
	}

	@Override
	public void send(Event event) {
		onEvent(event);
	}
	
	@Override
	public void sendBroadcast(NetworkEvent networkEvent)
	{
		onEvent(networkEvent);
	}

	@Override
	public synchronized void close()
	{
		isShuttingDown = true;
		for(PlayerSession session: sessions)
		{
			session.close();
		}
	}
	
	public PlayerSession getSessionInstance(Player player)
	{
		PlayerSession playerSession = sessionFactory.newPlayerSession(this,player);
		return playerSession;
	}
	
	@Override
	public Set<PlayerSession> getSessions()
	{
		return sessions;
	}

	@Override
	public void setSessions(Set<PlayerSession> sessions)
	{
		this.sessions = sessions;
	}
	
	@Override
	public String getGameRoomName()
	{
		return gameRoomName;
	}

	@Override
	public void setGameRoomName(String gameRoomName)
	{
		this.gameRoomName = gameRoomName;
	}

	@Override
	public Game getParentGame()
	{
		return parentGame;
	}

	@Override
	public void setParentGame(Game parentGame)
	{
		this.parentGame = parentGame;
	}

	@Override
	public void setStateManager(GameStateManagerService stateManager)
	{
		this.stateManager = stateManager;
	}
	
	@Override
	public GameStateManagerService getStateManager()
	{
		return stateManager;
	}

	@Override
	public Protocol getProtocol()
	{
		return protocol;
	}

	@Override
	public void setProtocol(Protocol protocol)
	{
		this.protocol = protocol;
	}
	
	@Override
	public SessionFactory getFactory() 
	{
		return sessionFactory;
	}

	@Override
	public void setFactory(SessionFactory factory) 
	{
		this.sessionFactory = factory;
	}
	
	@Override
	public boolean isShuttingDown()
	{
		return isShuttingDown;
	}

	public void setShuttingDown(boolean isShuttingDown)
	{
		this.isShuttingDown = isShuttingDown;
	}

	/**
	 * Method which will create and add event handlers of the player session to
	 * the Game Room's EventDispatcher.
	 * 
	 * @param playerSession
	 *            The session for which the event handlers are created.
	 */
	protected void createAndAddEventHandlers(PlayerSession playerSession)
	{
		// Create a network event listener for the player session.
		EventHandler networkEventHandler = new NetworkEventListener(playerSession);
		// Add the handler to the game room's EventDispatcher so that it will
		// pass game room network events to player session session.
		this.eventDispatcher.addHandler(networkEventHandler);
		LOG.trace("Added Network handler to "
				+ "EventDispatcher of GameRoom {}, for session: {}", this,
				playerSession);
	}
}
