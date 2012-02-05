package org.menacheri.event.impl;

import org.menacheri.app.ISession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.ISessionEventHandler;


/**
 * This class listens for tcp data out events from one {@link ISession} and
 * passes it on to another {@link ISession}. In short it acts as a conduit
 * between sessions.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DataOutTcpListener implements ISessionEventHandler
{
	private static final int eventType = Events.SERVER_OUT_TCP;
	private final ISession session;
	
	public DataOutTcpListener(ISession session)
	{
		this.session = session;
	}
	
	@Override
	public void onEvent(IEvent event)
	{
		session.onEvent(event);
	}

	@Override
	public int getEventType()
	{
		return eventType;
	}

	@Override
	public ISession getSession()
	{
		return session;
	}
	
	@Override
	public void setSession(ISession session)
	{
		throw new UnsupportedOperationException("Session is a final field in this class. It cannot be set");
	}

}