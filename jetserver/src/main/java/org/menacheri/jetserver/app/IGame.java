package org.menacheri.jetserver.app;

import org.jboss.netty.channel.ChannelFuture;
import org.menacheri.jetserver.app.impl.Game;


/**
 * This interface abstracts a game domain object. Each game deployed in the
 * server should implement this interface. The class Game {@link Game}
 * implements this interface and can be used as a starting point implementation.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IGame
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
	 * @return The associated {@link IGameCommandInterpreter} instance.
	 */
	public IGameCommandInterpreter getGameCommandInterpreter();
	
	/**
	 * Set the interpreter associated with this game. This method will be used
	 * if the creation of the interpreter is outside of the implementing game
	 * room instance, say by a {@link IGame} instance or set by the spring
	 * container.
	 * 
	 * @param interpreter
	 *            The interpreter instance to set.
	 */
	public void setGameCommandInterpreter(IGameCommandInterpreter interpreter);
	
	/**
	 * Unloads the current game, by closing all sessions. This will delegate
	 * to {@link IGameRoom#close()}
	 * 
	 * @return In case of Netty Implementation it would return a collection of
	 *         {@link ChannelFuture} object.
	 */
	public Object unload();
}