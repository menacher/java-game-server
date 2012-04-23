package org.menacheri.jetserver.app.impl;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.jetserver.app.IGame;
import org.menacheri.jetserver.app.IGameRoom;
import org.menacheri.jetserver.app.IPlayerSession;
import org.menacheri.jetserver.app.ISession;
import org.menacheri.jetserver.event.IEventHandler;
import org.menacheri.jetserver.event.INetworkEvent;
import org.menacheri.jetserver.event.impl.NetworkEventListener;
import org.menacheri.jetserver.protocols.IProtocol;
import org.menacheri.jetserver.service.IGameStateManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class GameRoomSession extends Session implements IGameRoom
{
	private static final Logger LOG = LoggerFactory.getLogger(GameRoomSession.class);
	
	/**
	 * The name of the game room, preferably unique across multiple games.
	 */
	protected String gameRoomName;
	/**
	 * The parent {@link Game} reference of this game room.
	 */
	protected IGame parentGame;
	/**
	 * Each game room has separate state manager instances. This variable will
	 * manage the state for all the {@link Player}s connected to this game room.
	 */
	protected IGameStateManagerService stateManager;

	/**
	 * The set of sessions in this object.
	 */
	protected Set<IPlayerSession> sessions;
	
	/**
	 * Each game room has its own protocol for communication with client.
	 */
	protected IProtocol protocol;
	
	protected GameRoomSession(GameRoomSessionBuilder gameRoomSessionBuilder)
	{
		super(gameRoomSessionBuilder);
		this.sessions = gameRoomSessionBuilder.sessions;
		this.parentGame = gameRoomSessionBuilder.parentGame;
		this.gameRoomName = gameRoomSessionBuilder.gameRoomName;
		this.protocol = gameRoomSessionBuilder.protocol;
	}
	
	public static class GameRoomSessionBuilder extends SessionBuilder
	{
		private Set<IPlayerSession> sessions;
		private IGame parentGame;
		private String gameRoomName;
		private IProtocol protocol;
		
		@Override
		protected void validateAndSetValues()
		{
			super.validateAndSetValues();// Mandatory call
			if(null == sessions){
				sessions = new HashSet<IPlayerSession>();
			}
		}
		
		public GameRoomSessionBuilder sessions(Set<IPlayerSession> sessions)
		{
			this.sessions = sessions;
			return this;
		}
		
		public GameRoomSessionBuilder parentGame(IGame parentGame)
		{
			this.parentGame = parentGame;
			return this;
		}
		
		public GameRoomSessionBuilder gameRoomName(String gameRoomName)
		{
			this.gameRoomName = gameRoomName;
			return this;
		}
		
		public GameRoomSessionBuilder protocol(IProtocol protocol)
		{
			this.protocol = protocol;
			return this;
		}
	}
	
	@Override
	public IPlayerSession createPlayerSession()
	{
		IPlayerSession playerSession = getSessionInstance();
		return playerSession;
	}
	
	@Override
	public abstract void onLogin(IPlayerSession playerSession);
	
	@Override
	public synchronized boolean connectSession(IPlayerSession playerSession)
	{
		if (!isShuttingDown)
		{
			playerSession.setStatus(ISession.Status.CONNECTING);
			sessions.add(playerSession);
			LOG.trace("Protocol to be applied is: {}",protocol.getClass().getName());
			protocol.applyProtocol(playerSession);
			createAndAddEventHandlers(playerSession);
			playerSession.setStatus(ISession.Status.CONNECTED);
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
	public void afterSessionConnect(IPlayerSession playerSession)
	{
			
	}
	
	public synchronized boolean disconnectSession(IPlayerSession playerSession)
	{
		final boolean removeHandlers = this.eventDispatcher.removeHandlersForSession(playerSession);
		return (removeHandlers && sessions.remove(playerSession));
	}

	@Override
	public void sendBroadcast(INetworkEvent networkEvent)
	{
		onEvent(networkEvent);
	}

	@Override
	public synchronized void close()
	{
		isShuttingDown = true;
		for(IPlayerSession session: sessions)
		{
			session.close();
		}
	}
	
	public IPlayerSession getSessionInstance()
	{
		IPlayerSession playerSession = Sessions.newPlayerSession(this);
		return playerSession;
	}
	
	@Override
	public Set<IPlayerSession> getSessions()
	{
		return sessions;
	}

	@Override
	public void setSessions(Set<IPlayerSession> sessions)
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
	public IGame getParentGame()
	{
		return parentGame;
	}

	@Override
	public void setParentGame(IGame parentGame)
	{
		this.parentGame = parentGame;
	}

	@Override
	public void setStateManager(IGameStateManagerService stateManager)
	{
		this.stateManager = stateManager;
	}
	
	@Override
	public IGameStateManagerService getStateManager()
	{
		return stateManager;
	}

	@Override
	public IProtocol getProtocol()
	{
		return protocol;
	}

	@Override
	public void setProtocol(IProtocol protocol)
	{
		this.protocol = protocol;
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
	protected void createAndAddEventHandlers(IPlayerSession playerSession)
	{
		// Create a network event listener for the player session.
		IEventHandler networkEventHandler = new NetworkEventListener(playerSession);
		// Add the handler to the game room's EventDispatcher so that it will
		// pass game room network events to player session session.
		this.eventDispatcher.addHandler(networkEventHandler);
		LOG.trace("Added Network handler to "
				+ "EventDispatcher of GameRoom {}, for session: {}", this,
				playerSession);
	}
}
