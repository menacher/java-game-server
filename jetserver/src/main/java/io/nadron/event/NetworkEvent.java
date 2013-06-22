package io.nadron.event;

import io.nadron.communication.DeliveryGuaranty;

/**
 * This interface is specifically used for events that will get transmitted to
 * remote machine/vm. It contains the {@link DeliveryGuaranty} associated with
 * the event so that messages can be transmitted either using TCP or UDP
 * transports based on the guaranty defined. Implementations can use RELIABLE as
 * default.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface NetworkEvent extends Event
{
	DeliveryGuaranty getDeliveryGuaranty();

	void setDeliveryGuaranty(DeliveryGuaranty deliveryGuaranty);
}
