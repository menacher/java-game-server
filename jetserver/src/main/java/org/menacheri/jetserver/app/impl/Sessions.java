package org.menacheri.jetserver.app.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.app.impl.DefaultPlayerSession.PlayerSessionBuilder;
import org.menacheri.jetserver.app.impl.DefaultSession.SessionBuilder;


/**
 * Factory class used to create a {@link PlayerSession} instance. It will
 * create a new instance, initialize it and set the {@link GameRoom} reference
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
	private static final AtomicInteger SESSION_ID = new AtomicInteger(0);

	public static Session newSession()
	{
		SessionBuilder sessionBuilder = new SessionBuilder();
		return sessionBuilder.build();
	}
	
	public static PlayerSession newPlayerSession(GameRoom gameRoom)
	{
		// TODO the player has to be set here after doing lookup.
		PlayerSessionBuilder builder = new PlayerSessionBuilder();
		builder.parentGameRoom(gameRoom).id(String.valueOf(SESSION_ID.incrementAndGet()));
		return builder.build();
	}

}
