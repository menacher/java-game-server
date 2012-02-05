package org.menacheri.event.impl;

import org.menacheri.app.ISession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.ISessionEventHandler;


public class DataOutUDPListener implements ISessionEventHandler
{
	private static final int eventType = Events.SERVER_OUT_UDP;
	private final ISession session;
	
	public DataOutUDPListener(ISession session)
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
