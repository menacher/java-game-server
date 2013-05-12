package org.menacheri.jetclient.handlers.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import org.menacheri.jetclient.NettyTCPClient;
import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.Events;

/**
 * A stateful handler whose job is to transmit messages coming on the Netty
 * {@link ChannelPipeline} to the session.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class DefaultToClientHandler extends ChannelInboundMessageHandlerAdapter<Event>
{
	static final String NAME = "defaultHandler";
	private final Session session;

	public DefaultToClientHandler(Session session)
	{
		this.session = session;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Event event)
			throws Exception {
		session.onEvent(event);
	}
	
	// TODO check what other methods need to be caught.
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NettyTCPClient.ALL_CHANNELS.add(ctx.channel());
		super.channelActive(ctx);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception
	{
		System.err.println("Class:DefaultToClientHandler"
				+ " Exception occurred in tcp channel: " + cause);
		Event event = Events.event(cause, Events.EXCEPTION);
		session.onEvent(event);
	}

	// TODO see if this causes reconnection failure
	@Override
	public void channelInactive(ChannelHandlerContext ctx)
			throws Exception
	{
		if (!session.isShuttingDown())
		{
			// Should not send close to session, since reconnection/other
			// business logic might be in place.
			Event event = Events.event(null, Events.DISCONNECT);
			session.onEvent(event);
		}
	}
	
	public static String getName()
	{
		return NAME;
	}
	
}
