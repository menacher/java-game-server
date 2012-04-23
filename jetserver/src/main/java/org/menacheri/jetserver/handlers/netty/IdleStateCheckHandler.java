package org.menacheri.jetserver.handlers.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class IdleStateCheckHandler extends IdleStateAwareChannelHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(IdleStateCheckHandler.class);
	
	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
			throws Exception
	{
		if(e.getState() == IdleState.ALL_IDLE){
			LOG.warn("Channel {} has been idle, it will be disconnected now: ",e.getChannel());
			e.getChannel().close();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		LOG.warn("Channel {} has thrown exception {}",e.getChannel(),e);
		e.getChannel().close();
		super.exceptionCaught(ctx, e);
	}
}
