package org.menacheri.communication;

/**
 * This interface is used as a wrapper for an incoming(to server) or outgoing(to
 * client) message. This also has a delivery guaranty associated with it, so
 * when it is sent to client the appropriate message sender would be used. TODO
 * should generics be used and this interface parameterized instead of sending
 * object?
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessage
{
	/**
	 * @return Returns the actual object message.
	 */
	Object getMessage();

	/**
	 * @return By default the guaranty would be {@link IDeliveryGuaranty.DeliveryGuaranty#RELIABLE}
	 *         
	 */
	IDeliveryGuaranty getDeliveryGuaranty();
}
