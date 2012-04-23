package org.menacheri.jetclient.app.impl;

import org.menacheri.jetclient.app.Player;
import org.menacheri.jetclient.app.PlayerSession;
import org.menacheri.jetclient.event.Event;

/**
 * This implementation of the {@link PlayerSession} interface is used to both
 * receive and send messages to a particular player using the
 * {@link Event}{@link #onEvent(Event)}.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultPlayerSession extends DefaultSession implements PlayerSession
{

	/**
	 * Each player session belongs to a Player. This variable holds the
	 * reference.
	 */
	final protected Player player;

	protected DefaultPlayerSession(SessionBuilder sessionBuilder, Player player)
	{
		super(sessionBuilder);
		this.player = player;
	}

	@Override
	public Player getPlayer()
	{
		return player;
	}

}
