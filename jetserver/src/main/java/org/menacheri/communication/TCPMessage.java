package org.menacheri.communication;


public class TCPMessage implements IMessage
{
	private final Object message;
	private static final int deliveryGuaranty = DeliveryGuaranty.RELIABLE;

	public TCPMessage(Object message)
	{
		this.message = message;
	}
	
	@Override
	public Object getMessage()
	{
		return message;
	}

	@Override
	public int getDeliveryGuaranty()
	{
		return deliveryGuaranty;
	}

}
