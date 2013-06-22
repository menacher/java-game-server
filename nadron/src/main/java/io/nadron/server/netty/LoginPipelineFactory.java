package io.nadron.server.netty;

import io.nadron.handlers.netty.EventDecoder;
import io.nadron.handlers.netty.LoginHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Timer;


public class LoginPipelineFactory extends ChannelInitializer<SocketChannel> 
{
	/**
	 * TODO make this configurable
	 */
	private static final int MAX_IDLE_SECONDS = 60;
	private int frameSize;
	private Timer timer;
	private EventDecoder eventDecoder;
	private LoginHandler loginHandler;
	private LengthFieldPrepender lengthFieldPrepender;

	@Override
	protected void initChannel(SocketChannel ch) throws Exception 
	{
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("framer", createLengthBasedFrameDecoder());
		pipeline.addLast("idleStateCheck", new IdleStateHandler(
				MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, MAX_IDLE_SECONDS));
		pipeline.addLast("eventDecoder", eventDecoder);
		pipeline.addLast("loginHandler", loginHandler);
		pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
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

	public void setLengthFieldPrepender(
			LengthFieldPrepender lengthFieldPrepender) 
	{
		this.lengthFieldPrepender = lengthFieldPrepender;
	}

}
