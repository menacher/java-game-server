package org.menacheri.app.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.app.impl.PlayerSession.PlayerSessionBuilder;
import org.menacheri.app.impl.Session.SessionBuilder;


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
		SessionBuilder sessionBuilder = new SessionBuilder();
		return sessionBuilder.build();
	}
	
	public static IPlayerSession newPlayerSession(IGameRoom gameRoom)
	{
		// TODO the player has to be set here after doing lookup.
		PlayerSessionBuilder builder = new PlayerSessionBuilder();
		builder.parentGameRoom(gameRoom).id(String.valueOf(sessionId.incrementAndGet()));
		return builder.build();
	}

}
