package org.menacheri.communication;


/**
 * This interface declares methods that are used to handle an incoming message.
 * It is not necessary to implement it, however implementing it could lead to a
 * standard mechanism for sending messages.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessageHandler
{
	/**
	 * Handle an incoming message.
	 * 
	 * @param message
	 *            The message that is received.
	 * @return If this is used to process a message, then the processed message
	 *         can be returned.
	 */
	public abstract Object handleMessage(Object message);

	
}
