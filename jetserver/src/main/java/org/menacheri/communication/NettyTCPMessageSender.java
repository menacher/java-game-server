package org.menacheri.communication;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NettyTCPMessageSender implements IMessageSender
{
	private Channel channel;
	private static final IDeliveryGuaranty DELIVERY_GUARANTY = new DeliveryGuaranty(DeliveryGuaranty.RELIABLE);
	private static final Logger LOG = LoggerFactory.getLogger(NettyTCPMessageSender.class);
	
	public NettyTCPMessageSender()
	{
		
	}
	
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

	public IDeliveryGuaranty getDeliveryGuaranty()
	{
		return DELIVERY_GUARANTY;
	}
	
	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel = channel;
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
