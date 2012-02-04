package org.menacheri.event.impl;

import org.menacheri.app.IPlayerSession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.IEventContext;
import org.menacheri.event.IEventHandler;


public abstract class LogInEventHandler implements IEventHandler 
{
	private static final int EVENT_TYPE = Events.LOG_IN;
	
	@Override
	public void onEvent(IEvent event)
	{
		IPlayerSession session = null;
		Object lookupKey = null;
		IEventContext context = event.getEventContext();
		if (null != context)
		{
			session = (IPlayerSession) context.getSession();
			lookupKey = event.getSource();
		}
		else
		{
			session = (IPlayerSession) event.getSource();
		}
		login(session,lookupKey);
	}
	
	@Override
	public int getEventType()
	{
		return EVENT_TYPE;
	}
	
	public abstract void login(IPlayerSession playerSession, Object lookupKey);
}
