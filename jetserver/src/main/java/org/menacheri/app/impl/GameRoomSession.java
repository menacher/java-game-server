package org.menacheri.app.impl;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.event.IEventHandler;
import org.menacheri.event.INetworkEvent;
import org.menacheri.event.impl.NetworkEventListener;
import org.menacheri.protocols.IProtocol;
import org.menacheri.service.IGameStateManagerService;
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
			// Create tcp and udp event handlers and add it to the game rooms event dispatcher.
			createAndAddEventHandlers(playerSession);
			playerSession.setStatus(ISession.Status.CONNECTED);
			afterSessionConnect(playerSession);
			return true;
			// TODO send event to all other sessions.
		}
		else
		{
			LOG.warn("Game Room is shutting down, playerSession {} {}",
					playerSession,"will not be connected!");
			return false;
		}
	}

	@Override
	public boolean connectSession(IPlayerSession session, Object protocolKey,
			Object nativeConnection)
	{
		return false;
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
	
	public Set<IPlayerSession> getSessions()
	{
		return sessions;
	}

	@Override
	public void setSessions(Set<IPlayerSession> sessions)
	{
		this.sessions = sessions;
	}
	
	public String getGameRoomName()
	{
		return gameRoomName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.menacheri.app.IGameRoom#setGameRoomName(java.lang.String)
	 */
	public void setGameRoomName(String gameRoomName)
	{
		this.gameRoomName = gameRoomName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.menacheri.app.IGameRoom#getParentGame()
	 */
	public IGame getParentGame()
	{
		return parentGame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.menacheri.app.IGameRoom#setParentGame(org.menacheri.app.IGame)
	 */
	public void setParentGame(IGame parentGame)
	{
		this.parentGame = parentGame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.menacher.game.IGameRoom#setStateManager(org.menacheri.app.
	 * IGameStateManagerService)
	 */
	public void setStateManager(IGameStateManagerService stateManager)
	{
		this.stateManager = stateManager;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.menacheri.app.IGameRoom#getStateManager()
	 */
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
	
	public boolean isShuttingDown()
	{
		return isShuttingDown;
	}

	public void setShuttingDown(boolean isShuttingDown)
	{
		this.isShuttingDown = isShuttingDown;
	}

	/**
	 * Method which will create and add event handlers to the player session as
	 * well as the EventDispatcher.
	 * 
	 * @param playerSession
	 *            The session for which the event handlers are created.
	 */
	public void createAndAddEventHandlers(IPlayerSession playerSession)
	{
		//TODO only add the UDP handler if the session can handle it. For e.g flash can't.
		IEventHandler networkEventHandler = new NetworkEventListener(playerSession);
		// Add a listener to the game room which will in turn pass game room events to session.
		this.eventDispatcher.addHandler(networkEventHandler);
		LOG.trace("Added Network handler to "
				+ "EventDispatcher of GameRoom {} for session: {}", this,
				playerSession);
	}
}
