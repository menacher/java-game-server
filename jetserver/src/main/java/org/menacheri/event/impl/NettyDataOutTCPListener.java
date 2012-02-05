package org.menacheri.event.impl;

import org.jboss.netty.channel.Channel;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.IEventHandler;


public class NettyDataOutTCPListener implements IEventHandler
{
	private final Channel channel;
	private static final int eventType = Events.SERVER_OUT_TCP;
	public NettyDataOutTCPListener(Channel channel)
	{
		this.channel = channel;
	}
	
	@Override
	public void onEvent(IEvent event)
	{
		channel.write(event.getSource());
	}

	@Override
	public int getEventType()
	{
		return eventType;
	}

}
