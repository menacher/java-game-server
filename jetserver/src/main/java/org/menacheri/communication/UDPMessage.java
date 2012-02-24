package org.menacheri.communication;

public class UDPMessage implements IMessage
{
	private final Object message;
	private static final int DELIVERY_GUARANTY = DeliveryGuaranty.FAST;
	
	public UDPMessage(final Object message)
	{
		this.message = message;
	}
	
	@Override
	public int getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

	@Override
	public Object getMessage()
	{
		return message;
	}

}
