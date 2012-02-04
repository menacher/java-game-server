package org.menacheri.app.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetlang.channels.MemoryChannel;
import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.communication.DeliveryGuaranty;
import org.menacheri.communication.IMessageListener;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.protocols.IProtocol;
import org.menacheri.service.IGameStateManagerService;
import org.menacheri.util.NettyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameRoom implements IGameRoom, IMessageListener
{
	private static final Logger LOG = LoggerFactory.getLogger(GameRoom.class);
	/**
	 * This variable could be used as a database key.
	 */
	private long id;

	private String uniqueId;
	/**
	 * The name of the game room, preferably unique across multiple games.
	 */
	private String gameRoomName;
	/**
	 * The parent {@link Game} reference of this game room.
	 */
	private IGame parentGame;
	/**
	 * Each game room has separate state manager instances. This variable will
	 * manage the state for all the {@link Player}s connected to this game room.
	 */
	private IGameStateManagerService stateManager;

	/**
	 * Any incoming message from a player session that is to be broadcast to
	 * other sessions will be published to this channel. Note: The session which
	 * submits the message will also get the broadcast.
	 */
	private org.jetlang.channels.Channel<IEvent> gameRoomChannel;
	
	/**
	 * Used to set a unique id on the incoming sessions to this room.
	 */
	private AtomicInteger sessionId = null;
	
	/**
	 * The set of sessions in this object.
	 */
	private Set<IPlayerSession> sessions;
	
	/**
	 * Life cycle variable to check if the room is shutting down. If it is, then no
	 * more connections will be accepted.
	 */
	volatile boolean isShuttingDown;
	
	/**
	 * Each game room has its own protocol for communication with client.
	 */
	private IProtocol protocol;
	
	public GameRoom()
	{
	}
	
	public void initialize()
	{
		isShuttingDown = false;
		gameRoomChannel = new MemoryChannel<IEvent>();
		sessionId = new AtomicInteger(0);
		sessions = new HashSet<IPlayerSession>();
	}
	
	/**
	 * No gameRoomChannelGroup creation, can be used by overriding classes if
	 * the default constructor is too restrictive.
	 * 
	 * @param ignore
	 *            This parameter is ignored.
	 */
	public GameRoom(boolean ignore)
	{
		sessionId = new AtomicInteger(0);
		sessions = Collections
				.synchronizedSet(new HashSet<IPlayerSession>());
	}
	
	@Override
	public void sendBroadcast(Object message)
	{
		sendBroadcast(message,DeliveryGuaranty.RELIABLE);
	}

	@Override
	public void sendBroadcast(Object message, int deliveryGuaranty)
	{
		// Create a udp or tcp message based on the deliveryGuaranty.
		IEvent event = null;
		switch (deliveryGuaranty)
		{
		case DeliveryGuaranty.FAST:
			event = Events.dataOutUdpEvent(message);
		case DeliveryGuaranty.RELIABLE:
		default:
			event = Events.dataOutTcpEvent(message);
			break;
		}
		
		if(null != event)
		{
			// publish it on the channel, it will be picked up by the player
			// sessions because their fibers are subscribed to this channel
			gameRoomChannel.publish(event);
		}
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
	
	public synchronized boolean connectSession(IPlayerSession session,
			Object protocolKey, Object nativeConnection)
	{
		if (!isShuttingDown)
		{
			// If connection attempt to game was unsuccessful then disconnect
			// this connection
			session.setStatus(ISession.Status.CONNECTING);
			sessions.add(session);
			// TODO move this to onEvent method.
			session.setConnectParameter(NettyUtils.NETTY_CHANNEL,
					nativeConnection);
			
			// Apply communication protocol.
			LOG.trace("Protocol to be applied is: {}",protocol.getClass().getName());
			protocol.applyProtocol(session);
			session.onEvent(Events.event(nativeConnection, Events.CONNECT_TCP));
			session.subscribeToGameChannel(gameRoomChannel);
			session.setStatus(ISession.Status.CONNECTED);
			afterSessionConnect(session);
			return true;
			// TODO send event to all other sessions.
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean connectSession(IPlayerSession session)
	{
		return false;
	}
	
	@Override
	public void afterSessionConnect(IPlayerSession playerSession)
	{
			
	}
	
	public synchronized boolean disconnectSession(IPlayerSession session)
	{
		return sessions.remove(session);
	}

	@Override
	public IPlayerSession createPlayerSession()
	{
		IPlayerSession playerSession = getSessionInstance();
		playerSession.setGameRoom(this);
		playerSession.setId(sessionId.incrementAndGet());
		onLogin(playerSession);
		return playerSession;
	}
	
	@Override
	public void onLogin(IPlayerSession playerSession)
	{
		
	}
	
	public IPlayerSession getSessionInstance()
	{
		PlayerSession playerSession = new PlayerSession();
		playerSession.initialize();
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
	
	public String getUniqueId()
	{
		return String.valueOf(id);
	}

	public void setUniqueId(String uniqueId)
	{
		this.uniqueId = uniqueId;
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
	public void receiveMessage(Object message)
	{
		sendBroadcast(message);
	}

	public AtomicInteger getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(AtomicInteger sessionId)
	{
		this.sessionId = sessionId;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((uniqueId == null) ? 0 : uniqueId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameRoom other = (GameRoom) obj;
		if (id != other.id)
			return false;
		if (uniqueId == null)
		{
			if (other.uniqueId != null)
				return false;
		}
		else if (!uniqueId.equals(other.uniqueId))
			return false;
		return true;
	}


	public long getId()
	{
		return id;
	}


	public void setId(long id)
	{
		this.id = id;
	}

	public org.jetlang.channels.Channel<IEvent> getGameRoomChannel()
	{
		return gameRoomChannel;
	}

	public void setGameRoomChannel(
			org.jetlang.channels.Channel<IEvent> gameRoomChannel)
	{
		this.gameRoomChannel = gameRoomChannel;
	}

	public boolean isShuttingDown()
	{
		return isShuttingDown;
	}

	public void setShuttingDown(boolean isShuttingDown)
	{
		this.isShuttingDown = isShuttingDown;
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

}
