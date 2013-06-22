package io.nadron.app.impl;

import io.nadron.app.GameRoom;
import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.SessionFactory;
import io.nadron.app.impl.DefaultPlayerSession.PlayerSessionBuilder;
import io.nadron.app.impl.DefaultSession.SessionBuilder;


/**
 * Factory class used to create a {@link PlayerSession} instance. It will
 * create a new instance, initialize it and set the {@link GameRoom} reference
 * if necessary.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Sessions implements SessionFactory
{

	public static final SessionFactory INSTANCE = new Sessions();
	
	@Override
	public Session newSession()
	{
		return new SessionBuilder().build();
	}
	
	@Override
	public PlayerSession newPlayerSession(GameRoom gameRoom, Player player)
	{
		return new PlayerSessionBuilder().parentGameRoom(gameRoom).player(player).build();
	}

}
