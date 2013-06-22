package io.nadron.client.event.impl;

import io.nadron.client.communication.DeliveryGuaranty;
import io.nadron.client.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.nadron.client.event.NetworkEvent;

/**
 * Default implementation of {@link NetworkEvent} interface. This class wraps a
 * message that needs to be transmitted to a remote Machine or VM.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultNetworkEvent extends DefaultEvent implements NetworkEvent
{
	DeliveryGuaranty guaranty;
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor which will set the {@link DeliveryGuaranty} to RELIABLE. It
	 * will also set the type of the event to {@link Events#NETWORK_MESSAGE}.
	 */
	public DefaultNetworkEvent()
	{
		super.setType(Events.NETWORK_MESSAGE);
		this.guaranty = DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE;
	}

	/**
	 * Copy constructor which will take values from the event and set it on this
	 * instance. It will disregard the type of the event and set it to
	 * {@link Events#NETWORK_MESSAGE}. {@link DeliveryGuarantyOptions} is set to
	 * RELIABLE.
	 * 
	 * @param event
	 *            The instance from which payload, create time etc will be
	 *            copied
	 */
	public DefaultNetworkEvent(Event event)
	{
		this(event, DeliveryGuarantyOptions.RELIABLE);
	}
	
	/**
	 * Copy constructor which will take values from the event and set it on this
	 * instance. It will disregard the type of the event and set it to
	 * {@link Events#NETWORK_MESSAGE}. {@link DeliveryGuarantyOptions} is set to the
	 * value passed in
	 * 
	 * @param event
	 *            The instance from which payload, create time etc will be
	 *            copied
	 * 
	 * @param deliveryGuaranty
	 */
	public DefaultNetworkEvent(Event event, DeliveryGuaranty deliveryGuaranty)
	{
		this.setSource(event.getSource());
		this.setTimeStamp(event.getTimeStamp());
		this.guaranty = DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE;
		super.setType(Events.NETWORK_MESSAGE);
	}
	
	@Override
	public DeliveryGuaranty getDeliveryGuaranty()
	{
		return guaranty;
	}

	@Override
	public void setDeliveryGuaranty(DeliveryGuaranty deliveryGuaranty)
	{
		this.guaranty = deliveryGuaranty;
	}

	@Override
	public void setType(int type)
	{
		throw new UnsupportedOperationException("Event type of this class is already set to NETWORK_MESSAGE. " +
				"It should not be reset.");
	}
}
