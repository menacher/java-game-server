package io.nadron.client.app;

import io.netty.channel.ChannelFuture;

/**
 * This interface abstracts a game domain object. Each game deployed in the
 * server should implement this interface.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Game
{
	/**
	 * @return Returns the unique id associated with this game object.
	 */
	public Object getId();

	/**
	 * @param id
	 *            Sets the unique id for this game.
	 */
	public void setId(Object id);

	/**
	 * Get the name of the game. Preferably should be a unique name.
	 * 
	 * @return Returns the name of the game.
	 */
	public String getGameName();

	/**
	 * Set the name of the game. Preferably it should be a unique value.
	 * 
	 * @param gameName
	 *            Set the preferably unique game name.
	 */
	public void setGameName(String gameName);

	/**
	 * Unloads the current game, by closing all sessions.
	 * 
	 * @return In case of Netty Implementation it would return a collection of
	 *         {@link ChannelFuture} object.
	 */
	public Object unload();
}