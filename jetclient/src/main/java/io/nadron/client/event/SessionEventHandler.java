package io.nadron.client.event;

import io.nadron.client.app.Session;

/**
 * In addition to handling events this handler will also have a reference to the
 * session.
 * 
 * @author Abraham Menacherry.
 * 
 */
public interface SessionEventHandler extends EventHandler
{
	Session getSession();
	void setSession(Session session);
}
