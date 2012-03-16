package org.menacheri.communication;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.communication.IMessageSender.IReliable;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final IDeliveryGuaranty DELIVERY_GUARANTY = DeliveryGuaranty.RELIABLE;
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
		return channel.write(message);
	}

	@Override
	public IDeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public ChannelFuture close()
	{
		return channel.close();
	}

	/**
	 * Writes an event mostly the {@link Events}.CLOSE to the client, flushes
	 * all the pending writes and closes the channel.
	 * 
	 * @param closeEvent
	 */
	public void close(IEvent closeEvent)
	{
		closeAfterFlushingPendingWrites(channel, closeEvent);
	}

	/**
	 * This method will write an event to the channel and then add a close
	 * listener which will close it after the write has completed.
	 * 
	 * @param ch
	 * @param event
	 */
	public void closeAfterFlushingPendingWrites(Channel ch, IEvent event)
	{
		if (ch.isConnected())
		{
			ch.write(event).addListener(ChannelFutureListener.CLOSE);
		}
		else
		{
			LOG.warn("Unable to write the Event {} with type {} to socket",
					event, event.getType());
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
