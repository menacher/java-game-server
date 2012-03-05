package org.menacheri.jetclient.app.impl;

import org.menacheri.jetclient.app.IPlayer;
import org.menacheri.jetclient.app.IPlayerSession;
import org.menacheri.jetclient.event.IEvent;

/**
 * This implementation of the {@link IPlayerSession} interface is used to both
 * receive and send messages to a particular player using the
 * {@link IEvent}{@link #onEvent(IEvent)}.
 * 
 * @author Abraham Menacherry
 * 
 */
public class PlayerSession extends Session implements IPlayerSession
{

	/**
	 * Each player session belongs to a Player. This variable holds the
	 * reference.
	 */
	final protected IPlayer player;

	protected PlayerSession(SessionBuilder sessionBuilder, IPlayer player)
	{
		super(sessionBuilder);
		this.player = player;
	}

	@Override
	public IPlayer getPlayer()
	{
		return player;
	}

}
