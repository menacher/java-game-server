package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.menacheri.jetserver.app.GameEvent;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class will handle on the {@link GameEvent}s by forwarding message
 * events to the associated session instance.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultToServerHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultToServerHandler.class);
	
	/**
	 * The player session associated with this stateful business handler.
	 */
	private final PlayerSession playerSession;

	public DefaultToServerHandler(PlayerSession playerSession)
	{
		super();
		this.playerSession = playerSession;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		playerSession.onEvent((Event) e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		LOG.error("Exception in DefaultToServerHandler class: {}.",e);
		Event event = Events.event(e,Events.EXCEPTION);
		playerSession.onEvent(event);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception
	{
		if (!playerSession.isShuttingDown())
		{
			LOG.debug("Channel {} is disconnected, "
					+ "raising the event to session", e.getChannel().getId());
			Event event = Events.event(e, Events.DISCONNECT);
			playerSession.onEvent(event);
		}
		else
		{
			LOG.debug("Session is already shutting down. "
					+ "Disconnect event will be discarded for channel {}", 
					e.getChannel().getId());
		}
		
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception
	{
		LOG.debug("Channel {} is closed and resources released", e.getChannel().getId());
		if (!playerSession.isShuttingDown())
		{
			Event event = Events.event(e, Events.DISCONNECT);
			playerSession.onEvent(event);
		}
	}
	
	public PlayerSession getPlayerSession()
	{
		return playerSession;
	}

}
