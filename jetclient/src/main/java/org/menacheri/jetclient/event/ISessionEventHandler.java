package org.menacheri.jetclient.event;

import org.menacheri.jetclient.app.ISession;

/**
 * In addition to handling events this handler will also have a reference to the
 * session.
 * 
 * @author Abraham Menacherry.
 * 
 */
public interface ISessionEventHandler extends IEventHandler
{
	ISession getSession();
}
