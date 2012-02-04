package org.menacheri.service;

import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;

/**
 * This interface declares the method used to connect and incoming user
 * connection to a {@link IGame}. This connection service will lookup the game
 * room, game etc based on the reference key passed in. It is also responsible
 * for configuring game specific handlers to the user session's pipeline.
 * Since this is an initial connection mechanism, the latest state of the
 * {@link IGame} or {@link IGameRoom} would be send to the incoming connection.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IGameConnectionService
{
	/**
	 * Method used to connect a player to a game. "connect" the player to the
	 * game, this may be done by setting object references on the player
	 * session object or by explicitly adding the player session to a
	 * {@link IGameRoom}.
	 * 
	 * @param playerSession
	 *            the session object instance of the incoming player.
	 * @param gameProtocolKey
	 *            this key is used to lookup information like the {@link IGame},
	 *            {@link IGameRoom} etc in order to actually "connect" a player
	 *            to the game.
	 * @return This will return a true after the session is setup.
	 */
	public abstract boolean connectToGame(IPlayerSession playerSession,
			Object gameProtocolKey, Object nativeConnection);//TODO make it send back an error code to client. 

}
