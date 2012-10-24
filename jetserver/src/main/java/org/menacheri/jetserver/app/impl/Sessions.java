package org.menacheri.jetserver.app.impl;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.Player;
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

	public static Session newSession()
	{
		return new SessionBuilder().build();
	}
	
	public static PlayerSession newPlayerSession(GameRoom gameRoom, Player player)
	{
		// TODO the player has to be set here after doing lookup.
		return new PlayerSessionBuilder().parentGameRoom(gameRoom).player(player).build();
	}

}
