package org.menacheri.jetserver.server.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.menacheri.jetserver.handlers.netty.EventDecoder;
import org.menacheri.jetserver.handlers.netty.LoginHandler;


public class LoginPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * TODO make this configurable
	 */
	private static final int MAX_IDLE_SECONDS = 60;
	private int frameSize;
	private Timer timer;
	private IdleStateAwareChannelHandler idleCheckHandler;
	private EventDecoder eventDecoder;
	private LoginHandler loginHandler;
	private LengthFieldPrepender lengthFieldPrepender;
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		addHandlers(pipeline);
		return pipeline;
	}

	public ChannelPipeline addHandlers(ChannelPipeline pipeline)
	{
		if (null == pipeline)
			return null;
		pipeline.addLast("framer",createLengthBasedFrameDecoder());
		pipeline.addLast("idleStateCheck", new IdleStateHandler(timer, 0, 0,
				MAX_IDLE_SECONDS));
		pipeline.addLast("idleCheckHandler", idleCheckHandler);
		pipeline.addLast("eventDecoder", eventDecoder);
		pipeline.addLast("loginHandler", loginHandler);
		pipeline.addLast("lengthFieldPrepender",lengthFieldPrepender);
		return pipeline;
	}
	
	public ChannelHandler createLengthBasedFrameDecoder()
	{
		return new LengthFieldBasedFrameDecoder(frameSize, 0, 2, 0, 2);
	}
	
	public int getFrameSize()
	{
		return frameSize;
	}

	public void setFrameSize(int frameSize)
	{
		this.frameSize = frameSize;
	}

	public EventDecoder getEventDecoder()
	{
		return eventDecoder;
	}

	public void setEventDecoder(EventDecoder eventDecoder)
	{
		this.eventDecoder = eventDecoder;
	}

	public LoginHandler getLoginHandler()
	{
		return loginHandler;
	}

	public void setLoginHandler(LoginHandler loginHandler)
	{
		this.loginHandler = loginHandler;
	}

	public IdleStateAwareChannelHandler getIdleCheckHandler()
	{
		return idleCheckHandler;
	}

	public void setIdleCheckHandler(IdleStateAwareChannelHandler idleCheckHandler)
	{
		this.idleCheckHandler = idleCheckHandler;
	}

	public Timer getTimer()
	{
		return timer;
	}

	public void setTimer(Timer timer)
	{
		this.timer = timer;
	}

	public LengthFieldPrepender getLengthFieldPrepender()
	{
		return lengthFieldPrepender;
	}

	public void setLengthFieldPrepender(LengthFieldPrepender lengthFieldPrepender)
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

}
