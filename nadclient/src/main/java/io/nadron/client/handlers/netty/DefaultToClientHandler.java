package io.nadron.client.handlers.netty;

import io.nadron.client.NettyTCPClient;
import io.nadron.client.app.Session;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;


/**
 * A stateful handler whose job is to transmit messages coming on the Netty
 * {@link ChannelPipeline} to the session.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Sharable
public class DefaultToClientHandler extends SimpleChannelInboundHandler<Event>
{
	static final String NAME = "defaultHandler";
	private final Session session;

	public DefaultToClientHandler(Session session)
	{
		this.session = session;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx,
			Event event) throws Exception
	{
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
