package org.menacheri.app.impl;

import org.jetlang.channels.MemoryChannel;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayer;
import org.menacheri.app.IPlayerSession;
import org.menacheri.event.impl.EventDispatcher;
import org.menacheri.protocols.IProtocol;


/**
 * This implementation of the {@link IPlayerSession} interface is used to both
 * receive and send messages to a particular player using the
 * {@link #onEvent(org.menacheri.event.IEvent)}. Broadcasts from the
 * {@link IGameRoom} are directly patched to the {@link EventDispatcher} which
 * listens on the room's {@link MemoryChannel} for events and in turn publishes
 * them to the listeners.
 * 
 * @author Abraham Menacherry
 * 
 */
public class PlayerSession extends Session implements IPlayerSession
{

	/**
	 * Each session belongs to a Player. This variable holds the reference.
	 */
	final protected IPlayer player;

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

	protected PlayerSession(PlayerSessionBuilder playerSessionBuilder)
	{
		super(playerSessionBuilder);
		this.player = playerSessionBuilder.player;
		this.parentGameRoom = playerSessionBuilder.parentGameRoom;
		this.protocol = playerSessionBuilder.protocol;
	}
	
	public static class PlayerSessionBuilder extends SessionBuilder
	{
		private IPlayer player = null;
		private IGameRoom parentGameRoom;
		private IProtocol protocol;

		public IPlayerSession build()
		{
			return new PlayerSession(this);
		}
		
		public PlayerSessionBuilder player(IPlayer player)
		{
			this.player = player;
			return this;
		}
		public PlayerSessionBuilder parentGameRoom(IGameRoom parentGameRoom)
		{
			if (null == parentGameRoom)
			{
				throw new IllegalStateException(
						"GameRoom instance is null, session will not be constructed");
			}
			this.parentGameRoom = parentGameRoom;
			return this;
		}
		public PlayerSessionBuilder protocol(IProtocol protocol)
		{
			this.protocol = protocol;
			return this;
		}
	}
	
	@Override
	public IPlayer getPlayer()
	{
		return player;
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

	@Override
	public String toString() {
		return "PlayerSession [id=" + id + "player=" + player + ", parentGameRoom="
				+ parentGameRoom + ", protocol=" + protocol  
				+ ", isShuttingDown=" + isShuttingDown + "]";
	}
}
