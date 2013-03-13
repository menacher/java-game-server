package org.menacheri.jetclient.handlers.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.menacheri.jetclient.NettyTCPClient;
import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.Event;

/**
 * A stateful handler whose job is to transmit messages coming on the Netty
 * {@link ChannelPipeline} to the session.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class DefaultToClientHandler extends SimpleChannelUpstreamHandler
{
	static final String NAME = "defaultHandler";
	private final Session session;

	public DefaultToClientHandler(Session session)
	{
		this.session = session;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		Event event = (Event) e.getMessage();
		session.onEvent(event);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception
	{
		NettyTCPClient.ALL_CHANNELS.add(e.getChannel());
		super.channelConnected(ctx, e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.err.println("Class:DefaultToClientHandler"
				+ " Exception occurred in tcp channel: " + e.getCause());
		Event event = Events.event(e, Events.EXCEPTION);
		session.onEvent(event);
	}

//	@Override
//	public void channelDisconnected(ChannelHandlerContext ctx,
//			ChannelStateEvent e) throws Exception
//	{
//		if (!session.isShuttingDown())
//		{
//			Event event = Events.event(e, Events.DISCONNECT);
//			session.onEvent(event);
//		}
//		else
//		{
//			System.err.println("Session is already shutting down. "
//					+ "Disconnect event will be discarded for channel {}"
//					+ e.getChannel().getId());
//		}
//
//	}

//	@Override
//	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
//			throws Exception
//	{
//		if (!session.isShuttingDown())
//		{
//			Event event = Events.event(e, Events.DISCONNECT);
//			session.onEvent(event);
//		}
//	}

	public static String getName()
	{
		return NAME;
	}
}
