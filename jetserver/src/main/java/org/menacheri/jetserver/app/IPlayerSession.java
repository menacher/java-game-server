package org.menacheri.jetserver.app;

import org.menacheri.jetserver.protocols.IProtocol;


/**
 * This interface model's a human player's session to jetserver. It declares
 * methods to get and set the {@link IPlayer}, the {@link IGameRoom} to which
 * this session will connect and the network {@link IProtocol} that will be used
 * for communication.
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

	/**
	 * Each user session is attached to a game room. This method is used to retrieve that
	 * game room object.
	 * 
	 * @return Returns the associated game room object or null if none is
	 *         associated.
	 */
	public abstract IGameRoom getGameRoom();

	/**
	 * Method used to set the game room for a particular session.
	 * 
	 * @param gameRoom
	 *            The gameRoom object to set.
	 */
	public abstract void setGameRoom(IGameRoom gameRoom);

	/**
	 * Get the {@link IProtocol} associated with this session.
	 * 
	 * @return Returns the associated protocol instance.
	 */
	public IProtocol getProtocol();

	/**
	 * Set the network protocol on the user session.
	 * 
	 * @param protocol
	 *            The {@link IProtocol} to set.
	 */
	public void setProtocol(IProtocol protocol);
}
