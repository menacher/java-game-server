package io.nadron.service;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.Player;
import io.nadron.util.Credentials;


public interface LookupService
{
	/**
	 * Get a game room based on a reference key. This allows the connection
	 * service to connect to a game room of a game.
	 * 
	 * @param gameContextKey
	 *            This is the key the flex front end passes to handshake
	 *            service.
	 * @return Returns the game room associated with this key or null if no such
	 *         game room exists.
	 */
	public abstract GameRoom gameRoomLookup(Object gameContextKey);

	/**
	 * Get a game based on a reference key. This allows the player session to
	 * get its associated game.
	 * 
	 * @param gameContextKey
	 * @return Returns the game instance based on the key provided. Null if
	 *         invalid key is provided.
	 */
	public abstract Game gameLookup(Object gameContextKey);

	/**
	 * Lookup a gamer based on a context key.
	 * 
	 * @param loginDetail Contains the username and password.
	 * @return Returns the gamer instance based on the key provided. Null if
	 *         invalid key is provided.
	 */
	public abstract Player playerLookup(Credentials loginDetail);

}
