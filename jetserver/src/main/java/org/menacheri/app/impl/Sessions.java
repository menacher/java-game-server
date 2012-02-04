package org.menacheri.app.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;


/**
 * Factory class used to create a {@link IPlayerSession} instance. It will
 * create a new instance, initialize it and set the {@link IGameRoom} reference
 * if necessary.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Sessions
{
	/**
	 * Used to set a unique id on the incoming sessions to this room.
	 */
	private static final AtomicInteger sessionId = new AtomicInteger(0);

	public static ISession newSession()
	{
		Session session = new Session();
		session.initialize();
		return session;
	}
	
	public static IPlayerSession newPlayerSession(IGameRoom gameRoom)
	{
		if (null == gameRoom)
		{
			throw new IllegalStateException(
					"GameRoom instance is null, session will not be constructed");
		}
		PlayerSession playerSession = new PlayerSession();
		playerSession.initialize();
		playerSession.setGameRoom(gameRoom);
		playerSession.setId(sessionId.incrementAndGet());
		return playerSession;
	}

}
