package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.SessionEventHandler;

public class ExceptionEventHandler implements SessionEventHandler
{
	private final Session session;
	
	public ExceptionEventHandler(Session session){
		this.session = session;
	}
	
	@Override
	public void onEvent(Event event) {
		
	}

	@Override
	public int getEventType() {
		return Events.EXCEPTION;
	}

	@Override
	public Session getSession() {
		return null;
	}

	@Override
	public void setSession(Session session)
			throws UnsupportedOperationException {
		
	}


}
