package org.menacheri.service;

import org.menacheri.app.IPlayerSession;

/**
 * This service is used to manage the handshake facility to server. Handshake is
 * done in 3 parts. First the client sends the server a reference key which is
 * available in the database. If reference key is valid then the handshake
 * service will add the incoming connection to the validated sessions set.
 * For the second part an ack message is sent to client from the server, this
 * ack is prepended by a single character protocol identifier string which can
 * be modified by client to signal a different protocol. The third part is the
 * receipt of the same ack from client. Now handshake is considered to be
 * complete.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IHandshakeService
{
	/**
	 * Method will check if the incoming reference key is valid. If the
	 * reference key is valid it will create a session using the. It will return
	 * the created session object. If it is an invalid reference key method will
	 * return null
	 * 
	 * @param refKey
	 *            The reference key coming from client to server.
	 * @return Returns a player session instance if the refKey is valid, null
	 *         otherwise.
	 */
	public abstract IPlayerSession validateCredentialsAndCreateSession(
			Object refKey);

	/**
	 * This method is used to generate an ack message to send to the client. It
	 * will generate a random message, prepend the default protocol information
	 * to this message and send it to client. The client can change the protocol
	 * in the reply ack.
	 * 
	 * @param playerSession
	 *            The incoming connction object wrapped in a player session
	 *            instance. object.
	 * @return Returns the ack message that was generated. This can be used to
	 *         send the ack to client and later be used to validate the ack.
	 */
	public abstract String generateAck(IPlayerSession playerSession);

	/**
	 * This method will validate whether an incoming ack message is a valid one
	 * or not. Only first character of the incoming message can be different,
	 * since it signifies a different protocol. Other characters need to be the
	 * same.
	 * 
	 * @param playerSession
	 *            The incoming session object wrapped in a player session instance
	 *            object.
	 * @param incomingAck
	 *            The incoming ack message from client.
	 * @param expectedAck
	 *            The expected ack message that was earlier saved when it was
	 *            sent.
	 * @return Returns true if the ack send by client is a valid one. False
	 *         otherwise.
	 */
	public abstract boolean validateAck(IPlayerSession playerSession,
			String incomingAck, String expectedAck);
}
