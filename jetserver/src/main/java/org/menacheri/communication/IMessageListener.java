package org.menacheri.communication;


/**
 * Defines methods for receiving a message from the client. The implementations
 * of this interface would likely be event driven.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessageListener
{
	/**
	 * An event driven method, which will be invoked by an upstream handler when
	 * there is message from the client coming to the server. Invoked when the
	 * Player(client) sends a message to the server.
	 * 
	 * @param message
	 *            The message sent by client.
	 */
	public abstract void receiveMessage(Object message);
}
