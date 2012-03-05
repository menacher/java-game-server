package org.menacheri.jetclient.app;

/**
 * This interface abstracts a user session to a {@link IGame}.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IPlayerSession extends ISession
{
	/**
	 * Each session is associated with a {@link IPlayer}. This is the actual
	 * human or machine using this session.
	 * 
	 * @return Returns that associated Player object or null if it is not
	 *         associated yet.
	 */
	public abstract IPlayer getPlayer();

}
