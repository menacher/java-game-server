package org.menacheri.communication;

import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;

public class UDPMessage implements IMessage
{
	private final Object message;
	private static final IDeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuaranty.FAST;
	
	public UDPMessage(final Object message)
	{
		this.message = message;
	}
	
	@Override
	public Object getMessage()
	{
		return message;
	}
	
	@Override
	public IDeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

}
