package io.nadron.app;

import io.nadron.event.Event;
import io.nadron.protocols.Protocol;


/**
 * This interface model's a human player's session to nadron. It declares
 * methods to get and set the {@link Player}, the {@link GameRoom} to which
 * this session will connect and the network {@link Protocol} that will be used
 * for communication.
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

	/**
	 * Each user session is attached to a game room. This method is used to retrieve that
	 * game room object.
	 * 
	 * @return Returns the associated game room object or null if none is
	 *         associated.
	 */
	public abstract GameRoom getGameRoom();

	/**
	 * Method used to set the game room for a particular session.
	 * 
	 * @param gameRoom
	 *            The gameRoom object to set.
	 */
	public abstract void setGameRoom(GameRoom gameRoom);

	/**
	 * Get the {@link Protocol} associated with this session.
	 * 
	 * @return Returns the associated protocol instance.
	 */
	public Protocol getProtocol();

	/**
	 * Set the network protocol on the user session.
	 * 
	 * @param protocol
	 *            The {@link Protocol} to set.
	 */
	public void setProtocol(Protocol protocol);
	
	/**
	 * The event to be send to the {@link GameRoom} to which the PlayerSession
	 * belongs. Behavior is unspecified if message is sent when a room change is
	 * taking place.
	 * 
	 * @param event The event to send to the {@link GameRoom}
	 */
	public void sendToGameRoom(Event event);
}
