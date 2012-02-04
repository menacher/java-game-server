package org.menacheri.app.impl;

import org.jetlang.channels.MemoryChannel;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayer;
import org.menacheri.app.IPlayerSession;
import org.menacheri.event.impl.EventDispatcher;
import org.menacheri.event.impl.EventDispatchers;
import org.menacheri.protocols.IProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This implementation of the {@link IPlayerSession} interface is used to both
 * receive and send messages to a particular player using the
 * {@link #onEvent(org.menacheri.event.IEvent)}. Broadcasts from the
 * {@link GameRoom} are directly patched to the {@link EventDispatcher} which
 * listens on the room's {@link MemoryChannel} for events and in turn publishes
 * them to the listeners.
 * 
 * @author Abraham Menacherry
 * 
 */
public class PlayerSession extends Session implements IPlayerSession
{
	private static final Logger LOG = LoggerFactory
			.getLogger(PlayerSession.class);

	/**
	 * Each session belongs to a Player. This variable holds the reference.
	 */
	IPlayer player;

	/**
	 * Each incoming connection is made to a game room. This reference holds the
	 * association to the game room.
	 */
	IGameRoom parentGameRoom;
	/**
	 * This variable holds information about the type of binary communication
	 * protocol to be used with this session.
	 */
	IProtocol protocol;

	public void initialize()
	{
		super.initialize();
//		EventDispatcher dispatcher = new EventDispatcher();
//		dispatcher.initialize();
//		this.eventDispatcher = dispatcher;
		this.eventDispatcher = EventDispatchers.newJetlangEventDispatcher();
	}

	@Override
	public Object subscribeToGameChannel(Object nativeGameChannel)
	{
		EventDispatcher dispatcher = (EventDispatcher) eventDispatcher;
		Object object = dispatcher.subscribeToGameChannel(nativeGameChannel);
		LOG.trace("Session {} subscribed to game room {}", this.getId(),
				this.getGameRoom().getUniqueId());
		return object;
	}

	@Override
	public IPlayer getPlayer()
	{
		return player;
	}

	@Override
	public void setPlayer(IPlayer player)
	{
		this.player = player;
	}

	public IGameRoom getGameRoom()
	{
		return parentGameRoom;
	}

	public void setGameRoom(IGameRoom gameRoom)
	{
		this.parentGameRoom = gameRoom;
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
	public void close()
	{
		if(!isShuttingDown)
		{
			super.close();
			parentGameRoom.disconnectSession(this);
		}
	}

}
