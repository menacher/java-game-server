package org.menacheri.service;

import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayer;
import org.menacheri.util.ICredentials;


public interface ILookupService
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
	public abstract IGameRoom gameRoomLookup(Object gameContextKey);

	/**
	 * Get a game based on a reference key. This allows the player session to
	 * get its associated game.
	 * 
	 * @param gameContextKey
	 * @return Returns the game instance based on the key provided. Null if
	 *         invalid key is provided.
	 */
	public abstract IGame gameLookup(Object gameContextKey);

	/**
	 * Lookup a gamer based on a context key.
	 * 
	 * @param loginDetail Contains the username and password.
	 * @return Returns the gamer instance based on the key provided. Null if
	 *         invalid key is provided.
	 */
	public abstract IPlayer playerLookup(ICredentials loginDetail);

}
