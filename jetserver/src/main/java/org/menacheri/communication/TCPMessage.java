package org.menacheri.communication;


public class TCPMessage implements IMessage
{
	private final Object message;
	private static final int DELIVERY_GUARANTY = DeliveryGuaranty.RELIABLE;

	public TCPMessage(final Object message)
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
		return DELIVERY_GUARANTY;
	}

}
