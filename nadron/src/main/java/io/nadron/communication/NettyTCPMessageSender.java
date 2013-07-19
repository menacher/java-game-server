package io.nadron.communication;

import io.nadron.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.communication.MessageSender.Reliable;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that transmits messages reliably to remote machines/vm's. Internally
 * this class uses Netty tcp {@link Channel} to transmit the message.
 * 
 * @author Abraham Menacherry
 * 
 */
public class NettyTCPMessageSender implements Reliable
{
	private final Channel channel;
	private static final DeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuarantyOptions.RELIABLE;
	private static final Logger LOG = LoggerFactory
			.getLogger(NettyTCPMessageSender.class);

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

	/**
	 * Writes an the {@link Events#DISCONNECT} to the client, flushes
	 * all the pending writes and closes the channel.
	 * 
	 */
	@Override
	public void close()
	{
		LOG.debug("Going to close tcp connection in class: {}", this
				.getClass().getName());
		Event event = Events.event(null, Events.DISCONNECT);
		if (channel.isActive())
		{
			channel.write(event).addListener(ChannelFutureListener.CLOSE);
		}
		else
		{
			channel.close();
			LOG.trace("Unable to write the Event {} with type {} to socket",
					event, event.getType());
		}
	}

	@Override
	public String toString()
	{
		String channelId = "TCP channel: ";
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
