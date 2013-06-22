package io.nadron.service;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.PlayerSession;

/**
 * This interface defines methods that are of an Administrative nature. These
 * methods can be used to load and unload games and game rooms.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface GameAdminService
{
	/**
	 * Implementation method will internally add this game instance to a set.
	 * 
	 * @param game
	 *            The game instance to register with the admin service.
	 * @return True if addition is successful, false if a duplicate is present
	 *         in the set.
	 */
	public boolean registerGame(Game game);

	/**
	 * Loads a game based on the game id OR game name. The first preference is
	 * to id. The id will be ignored if it is a negative number and game name
	 * will be used to retrieve the game.
	 * 
	 * @param gameId
	 *            The unique number identifier for the game. Most probably a
	 *            database key.
	 * @param gameName
	 *            The unique name for a game.
	 * @return Optionally returns an object, if it is an asynchronous operation,
	 *         then it would return a Future object, otherwise it would return a
	 *         boolean suggesting success or failure.
	 */
	public Object loadGame(long gameId, String gameName);

	/**
	 * Unloads a game based on the game id OR game name. The first preference is
	 * to id. The id will be ignored if it is a negative number and game name
	 * will be used to retrieve the game. Side affect will be the closing of any
	 * resources, {@link GameRoom}s or {@link PlayerSession}s associated
	 * with this game.
	 * 
	 * @param gameId
	 *            The unique number identifier for the game. Most probably a
	 *            database key.
	 * @param gameName
	 *            The unique name for a game.
	 * @return Optionally returns an object, if it is an asynchronous operation,
	 *         then it would return a Future object, otherwise it would return a
	 *         boolean suggesting success(true) or failure(false).
	 */
	public Object unLoadGame(long gameId, String gameName);

	/**
	 * Unloads a game based on the game instance passed in. Since the instance
	 * will internally contain the unique id or String name, this method will
	 * delegate to the overloaded method which takes id and String. Side affect
	 * will be the closing of any resources, {@link GameRoom}s or
	 * {@link PlayerSession}s associated with this game.
	 * 
	 * @param game
	 *            The instance of the game to unload.
	 * @return Optionally returns an object, if it is an asynchronous operation,
	 *         then it would return a Future object, otherwise it would return a
	 *         boolean suggesting success(true) or failure(false).
	 */
	public Object unLoadGame(Game game);

	/**
	 * Loads a game room based on the game room id OR game name. The first
	 * preference is to id. The id will be ignored if it is a negative number
	 * and game room name will be used to retrieve the game room.
	 * 
	 * @param game
	 *            The game for which the game room need to be loaded.
	 * @param gameRoomId
	 *            The unique identifier for the game room. Most probably a
	 *            database id.
	 * @param gameRoomName
	 *            The name of the game room to load.
	 * @return Optionally returns an object, if it is an asynchronous operation,
	 *         then it would return a Future object, otherwise it would return a
	 *         boolean suggesting success(true) or failure(false).
	 */
	public Object loadGameRoom(Game game, long gameRoomId, String gameRoomName);

	/**
	 * Unloads a game room based on the game room instance passed in. Since the
	 * instance will internally contain the unique id or String name, this
	 * method will delegate to the overloaded method which takes id and String.
	 * Side affect will be the closing of any resources like
	 * {@link PlayerSession}s associated with this game room.
	 * 
	 * @param gameRoom
	 *            The game room instance which is to be unloaded.
	 */
	public void unloadGameRoom(GameRoom gameRoom);

	/**
	 * Unloads a game room of a game based on the instance of the game, the
	 * unique identifier of the game room or the string game room name. Side
	 * affect will be the closing of any resources like
	 * {@link PlayerSession}s associated with this game room.
	 * 
	 * @param game
	 *            The instance of the game for which the game room needs to be
	 *            removed.
	 * @param gameRoomId
	 *            The unique identifier for the game room. Most probably a
	 *            database id.
	 * @return Optionally returns an object, if it is an asynchronous operation,
	 *         then it would return a Future object, otherwise it would return a
	 *         boolean suggesting success(true) or failure(false).
	 */
	public Object unloadGameRoom(Game game, long gameRoomId);

	/**
	 * Unloads a game room of a game based on the instance of the game and the
	 * name of the game room. Side affect will be the closing of any resources
	 * like {@link PlayerSession}s associated with this game room.
	 * 
	 * @param game
	 *            The instance of the game for which the game room needs to be
	 *            removed
	 * @param gameRoomName
	 *            The name of the game room to unload.
	 * @return Optionally returns an object, if it is an asynchronous operation,
	 *         then it would return a Future object, otherwise it would return a
	 *         boolean suggesting success(true) or failure(false).
	 */
	public Object unloadGameRoom(Game game, String gameRoomName);

	/**
	 * Unloads all the games and game rooms that is stored in the admin services
	 * internal set.
	 */
	public void shutdown();
}
