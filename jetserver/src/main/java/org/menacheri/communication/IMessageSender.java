package org.menacheri.communication;

/**
 * This interface declares method for sending a message to client. Different
 * implementations would be used by the server for sending based on the delivery
 * guaranty that is required.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessageSender
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
	public Object sendMessage(Object message);

	/**
	 * Returns the delivery guaranty of the implementation. Currently only
	 * RELIABLE and FAST are supported, their respective integer values are 0
	 * and 1.
	 * 
	 * @return The guaranty instance  associated with the implementation.
	 */
	public IDeliveryGuaranty getDeliveryGuaranty();
	
	/**
	 * An interface whose implementations would transmit messages reliably to
	 * the remote machine/vm. The transport for instance could be TCP.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public interface IReliable extends IMessageSender{}
	
	/**
	 * An interface whose implementations would transmit messages fast but
	 * <b>unreliably</b> to the remote machine/vm. The transport for instance
	 * could be UDP.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public interface IFast extends IMessageSender{}

}
