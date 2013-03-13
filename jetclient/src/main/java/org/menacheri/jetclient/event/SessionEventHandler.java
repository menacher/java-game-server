package org.menacheri.jetclient.event;

import org.menacheri.jetclient.app.Session;

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
