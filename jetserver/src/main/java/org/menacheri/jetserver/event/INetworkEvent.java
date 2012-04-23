package org.menacheri.jetserver.event;

import org.menacheri.jetserver.communication.IDeliveryGuaranty;

/**
 * This interface is specifically used for events that will get transmitted to
 * remote machine/vm. It contains the {@link IDeliveryGuaranty} associated with
 * the event so that messages can be transmitted either using TCP or UDP
 * transports based on the guaranty defined. Implementations can use RELIABLE as
 * default.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface INetworkEvent extends IEvent
{
	IDeliveryGuaranty getDeliveryGuaranty();

	void setDeliveryGuaranty(IDeliveryGuaranty deliveryGuaranty);
}
