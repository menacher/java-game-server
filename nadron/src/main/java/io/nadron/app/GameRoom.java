package io.nadron.app;

import io.nadron.app.impl.DefaultPlayer;
import io.nadron.event.Event;
import io.nadron.event.NetworkEvent;
import io.nadron.protocols.Protocol;
import io.nadron.service.GameStateManagerService;
import io.netty.channel.Channel;

import java.util.Set;



/**
 * This interface represents a game room and the related operations available on
 * it. The game room keeps the list of user sessions which are connected to it
 * and is responsible for sending broadcast messages. It can also considered to
 * be kind of a Factory pattern implementer, since it is responsible for
 * creating/initializing the game state, player sessions etc for the game.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface GameRoom
{

	/**
	 * Method used to create a player session object. Depending on the game, the
	 * right implementation class would be created by the game room. If there is
	 * some specific player session creation logic then this method can be
	 * considered like the factory method which should be overriden.
	 * 
	 * @return The game specific implementation of the player session.
	 */
	public PlayerSession createPlayerSession(Player player);
	
	/**
	 * Method called after the session is created. Can be used to add
	 * EventListeners to the session
	 */
	public void onLogin(PlayerSession playerSession);

	/**
	 * When a new user connects to the game, this method will be invoked to add
	 * the incoming session to the game room.
	 * 
	 * @param playerSession
	 *            The incoming user session. If we are using netty, it would be
	 *            a {@link Channel} object wrapped in a {@link PlayerSession}.
	 * @return true if session is connected successfully.
	 */
	public abstract boolean connectSession(PlayerSession playerSession);
	
	/**
	 * Method called after the session is created. Can be used to add
	 * EventListeners to the session
	 */
	public void afterSessionConnect(PlayerSession playerSession);

	/**
	 * Remove a session from the existing list of user sessions.
	 * 
	 * @param session
	 *            The session to be removed from the set.
	 * @return true if removal was successful and false if it was not.
	 */
	public abstract boolean disconnectSession(PlayerSession session);

	/**
	 * Returns a list of sessions that is held by the game room.
	 * 
	 * @return Returns the set of user sessions associated with game room.
	 */
	public abstract Set<PlayerSession> getSessions();

	/**
	 * @return Returns the name of the game room
	 */
	public abstract String getGameRoomName();

	/**
	 * Sets the name of the game room. Preferably unique.
	 * 
	 * @param gameRoomName
	 */
	public abstract void setGameRoomName(String gameRoomName);

	/**
	 * Each game room belongs to a game. This method can be used to retrieve
	 * that game object.
	 * 
	 * @return Returns the associated parent game object.
	 */
	public abstract Game getParentGame();

	/**
	 * Used to set the parent game object of the game room. By setting this we
	 * are able to identify which game room belongs to which game.
	 * 
	 * @param parentGame
	 *            The game to which this game room belongs.
	 */
	public abstract void setParentGame(Game parentGame);

	/**
	 * Every non trivial game will have some sort of state management service
	 * going on. This is done on a per game room basis where the latest state of
	 * the game room is effective for all the users connected to the game room.
	 * The state manager object encapsulates the state.
	 * 
	 * @return returns the state manager instance associated with the game room
	 *         or null if none are associated.
	 */
	public abstract GameStateManagerService getStateManager();

	/**
	 * Method used to set the state manager for a game room. Each game room will
	 * have its own instance of the state manager. If using spring, then the
	 * state manager bean needs to be of type "prototype" instead of singleton.
	 * Meaning that the state is not shared across multiple rooms.
	 * 
	 * @param stateManager
	 */
	public abstract void setStateManager(GameStateManagerService stateManager);

	/**
	 * Each game room has a protocol attached to it. This protocol object can be
	 * used to configure the Netty pipeline to add handlers or to add
	 * appropriate filter chains to the session.
	 * 
	 * @return Returns the associated protocol instance.
	 */
	public abstract Protocol getProtocol();
	
	/**
	 * Sets the protocol instance on the game room. This protocol object can be
	 * used to configure the Netty pipeline to add handlers or to add
	 * appropriate filter chains to the session.
	 * 
	 * @param protocol
	 *            The protocol instance to set.
	 */
	public abstract void setProtocol(Protocol protocol);

	/**
	 * Method used to set the set of user sessions to a particular game room.
	 * 
	 * @param sessions
	 *            The set of sessions to be set.
	 */
	public abstract void setSessions(Set<PlayerSession> sessions);

	/**
	 * Used to send an event to the GameRoom. The room can listen on this method
	 * for incoming events, do necessary business or game logic, transformations
	 * etc and then send it across to other {@link PlayerSession}s if required.
	 * Implementations are generally expected to be async, so default
	 * implementation would be to just patch incoming event to the
	 * {@link Session#onEvent(io.nadron.event.Event)} of the
	 * GameRoom's session where the actual business logic can be applied.
	 * 
	 * @param event The event to send to room
	 */
	public abstract void send(Event event);
	
	/**
	 * Method used to send a broadcast message to all sessions in the group. It
	 * is the easiest way to update the state of all connected {@link DefaultPlayer} s
	 * to the same state. This method will transmit messages using the delivery
	 * guaranty provided in the {@link NetworkEvent}.
	 * 
	 * @param networkEvent
	 *            The message that is to be broadcast to all user sessions of
	 *            this game room
	 */
	public abstract void sendBroadcast(NetworkEvent networkEvent);

	/**
	 * This method will close down the game room. It can be used to disconnect
	 * all users connected to a game room.
	 * 
	 */
	public abstract void close();

	public abstract void setFactory(SessionFactory factory);

	public abstract SessionFactory getFactory();

}