package org.menacheri.handlers;

import org.jboss.netty.channel.Channel;
import org.menacheri.app.IGame;
import org.menacheri.app.IPlayerSession;
import org.menacheri.handlers.netty.HandshakeHandler;
import org.menacheri.protocols.UnknownProtocolException;
import org.menacheri.service.IGameConnectionService;
import org.menacheri.service.IHandshakeService;


/**
 * This interface has the methods by which handshake is done between server and
 * client. This handler is by default the initial handler setup in the
 * communication pipeline. Once the handshake is done with help from
 * {@link IHandshakeService} then this handler will use the
 * {@link IGameConnectionService} to connect the incoming session to the
 * game.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IHandshakeHandler
{
	/**
	 * @return Returns the handshake service instance associated with the
	 *         implementing class. Null if no service instance is attached.
	 */
	public IHandshakeService getHandshakeSerivce();

	/**
	 * @param handshakeSerivce
	 *            Sets the handshake service instance. The handshake handler
	 *            will delegate to the handshake service all the handshake
	 *            related tasks.
	 */
	public void setHandshakeSerivce(IHandshakeService handshakeSerivce);

	/**
	 * This method manages the session creation attempt by a client. It validates that
	 * the reference key passed in (message) is valid and stores it locally for
	 * use after the acknowledgment also has been validated. If the reference
	 * key is invalidated then the session will be terminated.
	 * 
	 * @param nativeConnection
	 *            Usually the <a href="http://www.jboss.org/netty">Netty</a>
	 *            {@link Channel} instance.
	 * @param message
	 *            This message is a reference key which client passes to server.
	 *            The server validates this and stores it. This will later be
	 *            used to connect to the game, once the acknowledgment has also
	 *            been validated.
	 */
	public void manageIncomingSessionFromClient(
			Object nativeConnection, String message);

	/**
	 * This method is used to manage acknowledgment from the client once the
	 * session has been validated. It checks if the acknowledgment it sent to
	 * client was echoed back by client. The default implementation,
	 * {@link HandshakeHandler} also uses this method to set the protocol based
	 * on the first 2 characters of the incoming acknowledgment from client.
	 * 
	 * @param playerSession
	 *            The user session instance for which acknowledgment is to be
	 *            managed.
	 * @param message
	 *            The incoming message from client. This is the acknowledgment
	 *            echoed back by client.
	 */
	public void manageAckFromClient(IPlayerSession playerSession,
			String message) throws UnknownProtocolException;

	/**
	 * Once the incoming session and the acknowledgment has been validated.
	 * The communication pipeline need to cleared off all handshake related
	 * handlers. This utility method is responsible for the cleanup.
	 * 
	 * @param playerSession
	 *            This user session instance is used to retrieve the
	 *            communication pipeline which need to be cleared.
	 */
	public void removeHandshakeHandlers(IPlayerSession playerSession);

	/**
	 * Once validation, acknowledgment and communication pipeline cleanup has
	 * occurred, this method will be utilized to connect to the game. The method
	 * in turn utilizes the reference key passed in to the handler while
	 * validating the session to connect to the game. It also uses the
	 * acknowledgment from client to configure communication handlers based on
	 * the protocol selected by the client.
	 * 
	 * @param playerSession
	 *            The session that needs to be attached to a {@link IGame}.
	 * @param gameProtocolKey
	 *            The acknowledgment message sent by the client, it maybe
	 *            modified for setting the protocol for communication.
	 */
	public void connectToGame(IPlayerSession playerSession, String gameProtocolKey);

	/**
	 * If session reference key or the acknowledgment is wrong or invalid,
	 * then this method will be used to disconnect the session. It will send
	 * an error message string to client and disconnect.
	 * 
	 * @param playerSession The session to be disconnected to be used to send this message.
	 * @param message
	 */
	public void sendInvalidHandshakeMessage(IPlayerSession playerSession,
			String message);
}
