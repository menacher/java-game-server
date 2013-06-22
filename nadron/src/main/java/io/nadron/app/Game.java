package io.nadron.app;

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
	 * Each game requires a different set of game commands. This method will set
	 * the interpreter which will convert these commands to method calls.
	 * 
	 * @return The associated {@link GameCommandInterpreter} instance.
	 */
	public GameCommandInterpreter getGameCommandInterpreter();
	
	/**
	 * Set the interpreter associated with this game. This method will be used
	 * if the creation of the interpreter is outside of the implementing game
	 * room instance, say by a {@link Game} instance or set by the spring
	 * container.
	 * 
	 * @param interpreter
	 *            The interpreter instance to set.
	 */
	public void setGameCommandInterpreter(GameCommandInterpreter interpreter);
	
	/**
	 * Unloads the current game, by closing all sessions. This will delegate
	 * to {@link GameRoom#close()}
	 * 
	 * @return In case of Netty Implementation it would return a collection of
	 *         {@link ChannelFuture} object.
	 */
	public Object unload();
}