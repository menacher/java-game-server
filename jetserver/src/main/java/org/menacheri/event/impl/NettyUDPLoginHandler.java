package org.menacheri.event.impl;

import java.net.InetSocketAddress;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.menacheri.app.ISession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.IEventHandler;
import org.menacheri.util.NettyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyUDPLoginHandler implements IEventHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(NettyUDPLoginHandler.class);
	private static final int eventType = Events.LOG_IN_UDP;
	private final Map<InetSocketAddress,ISession> updSessionsMap;
	private final ISession session;
	
	public NettyUDPLoginHandler(Map<InetSocketAddress,ISession> updSessionsMap,ISession session)
	{
		this.updSessionsMap = updSessionsMap;
		this.session = session;
	}
	
	@Override
	public int getEventType()
	{
		return eventType;
	}

	@Override
	public void onEvent(IEvent event)
	{
		ChannelBuffer buffer = (ChannelBuffer)event;
		String hostname = NettyUtils.readString(buffer);
		String port = NettyUtils.readString(buffer);
		InetSocketAddress remoteAddress = new InetSocketAddress(hostname, Integer.valueOf(port));
		if(null == updSessionsMap.get(remoteAddress))
		{
			updSessionsMap.put(remoteAddress, session);
		}
		else
		{
			LOG.error("Session already added for remoteAddress {}.",remoteAddress);
			NettyUtils
			.createBufferForOpcode(Events.LOG_IN_FAILURE_UDP);
		}
	}
}
