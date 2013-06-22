package io.nadron.event;

import io.nadron.app.Session;

/**
 * This interface is implemented by event handlers which are listening on
 * messages published to a {@link Session}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface SessionEventHandler extends EventHandler
{
	public Session getSession();

	/**
	 * Sets the session instance on this handler. The default implementation
	 * will throw an exception since the session field is final and is passed in
	 * via a constructor.
	 * 
	 * @param session The session instance to set.
	 * @throws UnsupportedOperationException
	 */
	public void setSession(Session session) throws UnsupportedOperationException;
}
