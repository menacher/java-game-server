package org.menacheri.event.impl;

import org.menacheri.communication.IDeliveryGuaranty;
import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.INetworkEvent;

/**
 * Default implementation of {@link INetworkEvent} interface. This class wraps a
 * message that needs to be transmitted to a remote Machine or VM.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NetworkEvent extends Event implements INetworkEvent
{
	IDeliveryGuaranty guaranty;
	private static final long serialVersionUID = 6486454029499527617L;

	/**
	 * Default constructor which will set the IDeliveryGuaranty to RELIABLE. It
	 * will also set the type of the event to {@link Events#NETWORK_MESSAGE}.
	 */
	public NetworkEvent()
	{
		super.setType(Events.NETWORK_MESSAGE);
		this.guaranty = IDeliveryGuaranty.DeliveryGuaranty.RELIABLE;
	}

	/**
	 * Copy constructor which will take values from the event and set it on this
	 * instance. It will disregard the type of the event and set it to
	 * {@link Events#NETWORK_MESSAGE}. {@link DeliveryGuaranty} is set to
	 * RELIABLE.
	 * 
	 * @param event
	 *            The instance from which payload, create time etc will be
	 *            copied
	 */
	public NetworkEvent(IEvent event)
	{
		this(event, IDeliveryGuaranty.DeliveryGuaranty.RELIABLE);
	}

	/**
	 * Copy constructor which will take values from the event and set it on this
	 * instance. It will disregard the type of the event and set it to
	 * {@link Events#NETWORK_MESSAGE}. {@link DeliveryGuaranty} is set to the
	 * value passed in
	 * 
	 * @param event
	 *            The instance from which payload, create time etc will be
	 *            copied
	 * 
	 * @param deliveryGuaranty
	 */
	public NetworkEvent(IEvent event, IDeliveryGuaranty deliveryGuaranty)
	{
		this.setSource(event.getSource());
		this.setEventContext(event.getEventContext());
		this.setTimeStamp(event.getTimeStamp());
		this.guaranty = IDeliveryGuaranty.DeliveryGuaranty.RELIABLE;
		super.setType(Events.NETWORK_MESSAGE);
	}

	@Override
	public IDeliveryGuaranty getDeliveryGuaranty()
	{
		return guaranty;
	}

	@Override
	public void setDeliveryGuaranty(IDeliveryGuaranty deliveryGuaranty)
	{
		this.guaranty = deliveryGuaranty;
	}

	@Override
	public void setType(int type)
	{
		throw new IllegalArgumentException(
				"Event type of this class is already set to NETWORK_MESSAGE. "
						+ "It should not be reset.");
	}
}
