package io.nadron.client.communication;

import io.nadron.client.communication.MessageSender.Reliable;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


/**
 * A class that transmits messages reliably to remote machines/vm's. Internally
 * this class uses Netty tcp {@link Channel} to transmit the message.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyTCPMessageSender implements Reliable
{
	private boolean isClosed = false;
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
		return channel.writeAndFlush(message);
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

	public synchronized void close()
	{
		if (isClosed)
			return;
		ChannelFuture closeFuture = channel.close();
		closeFuture.awaitUninterruptibly();
		if (!closeFuture.isSuccess())
		{
			System.err.println("TCP channel " + channel
					+ " did not close successfully");
		}
		isClosed = true;
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
		if (channel.isActive())
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
			channelId += channel.toString();
		}
		else
		{
			channelId += "0";
		}
		String sender = "Netty " + channelId;
		return sender;
	}
}
