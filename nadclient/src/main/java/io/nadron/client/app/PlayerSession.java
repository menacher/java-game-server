package io.nadron.client.app;

/**
 * This interface abstracts a user session to a {@link Game}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface PlayerSession extends Session
{
	/**
	 * Each session is associated with a {@link Player}. This is the actual
	 * human or machine using this session.
	 * 
	 * @return Returns that associated Player object or null if it is not
	 *         associated yet.
	 */
	public abstract Player getPlayer();

}
