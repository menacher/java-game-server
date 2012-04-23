package org.menacheri.jetclient.communication;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.menacheri.jetclient.communication.MessageSender.IReliable;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;

/**
 * A class that transmits messages reliably to remote machines/vm's. Internally
 * this class uses Netty tcp {@link Channel} to transmit the message.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyTCPMessageSender implements IReliable
{
	private final Channel channel;
	private static final DeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuaranty.DeliveryGuarantyOptions.RELIABLE;

	public NettyTCPMessageSender(Channel channel)
	{
		super();
		this.channel = channel;
	}

	@Override
	public Object sendMessage(Object message)
	{
		return channel.write(message);
	}

	@Override
	public DeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void close()
	{
		channel.close();
	}

	/**
	 * Writes an event mostly the {@link Events}.CLOSE to the client, flushes
	 * all the pending writes and closes the channel.
	 * 
	 * @param closeEvent
	 */
	public void close(Event closeEvent)
	{
		closeAfterFlushingPendingWrites(channel, closeEvent);
	}

	/**
	 * This method will write an event to the channel and then add a close
	 * listener which will close it after the write has completed.
	 * 
	 * @param channel
	 * @param event
	 */
	public void closeAfterFlushingPendingWrites(Channel channel, Event event)
	{
		if (channel.isConnected())
		{
			channel.write(event).addListener(ChannelFutureListener.CLOSE);
		}
		else
		{
			System.err.println("Unable to write the Event :" + event
					+ " to socket as channel is ot connected");
		}
	}

	@Override
	public String toString()
	{
		String channelId = "TCP channel with Id: ";
		if (null != channel)
		{
			channelId += channel.getId().toString();
		}
		else
		{
			channelId += "0";
		}
		String sender = "Netty " + channelId;
		return sender;
	}
}
