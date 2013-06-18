package org.menacheri.jetserver.event.impl;

import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.SessionEventHandler;

/**
 * This abstract helper class can be used to quickly create a listener which
 * listens for SESSION_MESSAGE events. Child classes need to override the
 * onEvent to plugin the logic.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class SessionMessageHandler implements SessionEventHandler {

	private final Session session;
	
	public SessionMessageHandler(Session session)
	{
		this.session = session;
	}
	
	@Override
	public int getEventType() {
		return Events.SESSION_MESSAGE;
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Session instance is final and cannot be reset on this handler");
	}

}
