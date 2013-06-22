package io.nadron.client.communication;


/**
 * This interface declares method for sending a message to client. Different
 * implementations would be used by the server for sending based on the delivery
 * guaranty that is required.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface MessageSender
{
	/**
	 * This method delegates to the underlying native session object to send a
	 * message to the client.
	 * 
	 * @param message
	 *            The message to be sent to client.
	 * @return The boolean or future associated with this operation if
	 *         synchronous or asynchronous implementation respectively.
	 */
	Object sendMessage(Object message);

	/**
	 * Returns the delivery guaranty of the implementation. Currently only
	 * RELIABLE and FAST are supported, their respective integer values are 0
	 * and 1.
	 * 
	 * @return The guaranty instance associated with the implementation.
	 */
	DeliveryGuaranty getDeliveryGuaranty();

	/**
	 * Cleanup hook which can be called when a session is disconnected or
	 * closed.
	 */
	void close();

	public interface Reliable extends MessageSender{}
	
	public interface Fast extends MessageSender{}
}
