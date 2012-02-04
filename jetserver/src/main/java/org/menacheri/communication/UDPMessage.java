package org.menacheri.communication;

public class UDPMessage implements IMessage
{
	private final Object message;
	private static final int deliveryGuaranty = DeliveryGuaranty.FAST;
	
	public UDPMessage(Object message)
	{
		this.message = message;
	}
	
	@Override
	public int getDeliveryGuaranty()
	{
		return deliveryGuaranty;
	}

	@Override
	public Object getMessage()
	{
		return message;
	}

}
