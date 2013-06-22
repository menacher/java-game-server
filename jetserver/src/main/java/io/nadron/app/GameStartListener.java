package io.nadron.app;

import java.util.Properties;

/**
 * Defines the starting point for a game instance. This method will be called by
 * the container one time for each startup. Subsequent startups for the same
 * game instance which was persisted would have the isInitialized set to true.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface GameStartListener
{
	/**
	 * The "public static void main" for each game instance( a game instance
	 * would actually take place in a game room). This method can be used to
	 * load all the initial state of the {@link Game} or {@link GameRoom},
	 * start up scheduled {@link Task}s and do any other initialization logic
	 * necessary for a particular game.
	 * 
	 * @param isInitialized
	 *            If this is a persisted instance, then restarts would be called
	 *            with isInitialized set to true. TODO current version does not
	 *            support persistence and hence this parameter will always be
	 *            false.
	 * @param properties
	 *            The environment properties which would be a combination of JVM
	 *            flags and properties files would be passed into this method by
	 *            the container
	 */
	public void start(boolean isInitialized, Properties properties);
	
	/**
	 * @return Returns the associated game room instance.
	 */
	public GameRoom getGameRoom();

	/**
	 * @param gameRoom
	 *            The game room instance to set on the implementation.
	 */
	public void setGameRoom(GameRoom gameRoom);
}
