package org.menacheri.jetclient.handlers.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetclient.NettyUDPClient;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;

/**
 * This upstream handler handles <b>ALL</b> UDP events. It will lookup the
 * appropriate session from {@link NettyUDPClient#CLIENTS} map and then transmit
 * the event to that {@link ISession}. <b>Note</b> If this class cannot find the
 * appropriate session to transmit this event to, then the event is
 * <b>silently</b> discarded.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class UDPUpstreamHandler extends SimpleChannelUpstreamHandler
{
	public UDPUpstreamHandler()
	{
		super();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		// Lookup the session from the local address.
		DatagramChannel datagramChannel = (DatagramChannel) e.getChannel();
		ISession session = NettyUDPClient.CLIENTS.get(datagramChannel
				.getLocalAddress());
		if (null != session)
		{
			IEvent event = (IEvent) e.getMessage();
			// Pass the event on to the session
			session.onEvent(event);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.err.println(e.getCause());
		DatagramChannel datagramChannel = (DatagramChannel) e.getChannel();
		ISession session = NettyUDPClient.CLIENTS.get(datagramChannel
				.getLocalAddress());
		if (null != session)
		{
			IEvent event = Events.event(e, Events.EXCEPTION);
			session.onEvent(event);
		}
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception
	{
		DatagramChannel datagramChannel = (DatagramChannel) e.getChannel();
		ISession session = NettyUDPClient.CLIENTS.get(datagramChannel
				.getLocalAddress());
		if ((null != session) && !session.isShuttingDown())
		{
			IEvent event = Events.event(e, Events.DISCONNECT);
			session.onEvent(event);
		}
		else if (null != session)
		{
			System.out.println("Session is already shutting down. "
					+ "Disconnect event will be discarded for channel {}"
					+ datagramChannel.getId());
		}

	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception
	{
		DatagramChannel datagramChannel = (DatagramChannel) e.getChannel();
		ISession session = NettyUDPClient.CLIENTS.get(datagramChannel
				.getLocalAddress());
		if ((null != session) && !session.isShuttingDown())
		{
			IEvent event = Events.event(e, Events.DISCONNECT);
			session.onEvent(event);
		}
	}

}
