package org.menacheri.handlers;

import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.service.IGameStateManagerService;


/**
 * This interface deals with sending a message back to the client. Not necessary
 * to implement, but could lead to standardization if implemented.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessageSender
{
	/**
	 * Once a handler makes a decision to broadcast the message to all the
	 * players connected to a {@link IGameRoom} then this is the method that
	 * should be invoked.
	 * 
	 * @param gameRoom
	 *            The reference to the game room object to which this message is
	 *            to be broadcast.
	 * @param message
	 *            The message that is to be broadcast to all connected players.
	 */
	public abstract void broadcastMessage(IGameRoom gameRoom, Object message);

	/**
	 * This is a convenience method that can be used to broadcast the latest
	 * state of the game to all connected players. It is very useful in small
	 * games where the state is defined in a light weight object.
	 * 
	 * @param gameRoom
	 *            The reference to the game room object to which this latest
	 *            state is to be broadcast.
	 * @param stateManager
	 *            This contains the latest state.
	 */
	public abstract void broadcastMessage(IGameRoom gameRoom,
			IGameStateManagerService stateManager);

	/**
	 * This method is used to send a message to a single player. This can be
	 * used to update the state of the player to the latest. Basically used in
	 * non-broadcast scenarios.
	 * 
	 * @param playerSession
	 *            The reference to the players session, on which the message
	 *            is to be sent.
	 * @param message
	 *            The message that is to be sent.
	 */
	public abstract void sendMessage(IPlayerSession playerSession,
			Object message);

	/**
	 * This is a convenience method that can be used to send the latest state of
	 * the game to a player. Mostly useful when it is found that a players state
	 * is very out of sync.
	 * 
	 * @param playerSession
	 *            The reference to the players session, on which the latest
	 *            state can be sent.
	 * @param stateManager
	 *            This contains the latest state.
	 */
	public abstract void sendMessage(IPlayerSession playerSession,
			IGameStateManagerService stateManager);
}
