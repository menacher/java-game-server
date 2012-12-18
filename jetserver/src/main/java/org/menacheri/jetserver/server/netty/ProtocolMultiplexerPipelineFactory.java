package org.menacheri.jetserver.server.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.menacheri.jetserver.handlers.netty.LoginProtocol;
import org.menacheri.jetserver.handlers.netty.ProtocolMultiplexerDecoder;

public class ProtocolMultiplexerPipelineFactory implements
		ChannelPipelineFactory
{
	private static final int MAX_IDLE_SECONDS = 60;
	private Timer timer;
	private IdleStateAwareChannelHandler idleCheckHandler;
	private int bytesForProtocolCheck;
	private LoginProtocol loginProtocol;
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("idleStateCheck", new IdleStateHandler(timer, 0, 0,
				MAX_IDLE_SECONDS));
		pipeline.addLast("idleCheckHandler", idleCheckHandler);
		pipeline.addLast("multiplexer", createProtcolMultiplexerDecoder());
		return pipeline;
	}
	
	protected ChannelHandler createProtcolMultiplexerDecoder()
	{
		return new ProtocolMultiplexerDecoder(bytesForProtocolCheck,loginProtocol);
	}

	public Timer getTimer()
	{
		return timer;
	}

	public void setTimer(Timer timer)
	{
		this.timer = timer;
	}

	public IdleStateAwareChannelHandler getIdleCheckHandler()
	{
		return idleCheckHandler;
	}

	public void setIdleCheckHandler(IdleStateAwareChannelHandler idleCheckHandler)
	{
		this.idleCheckHandler = idleCheckHandler;
	}

	public int getBytesForProtocolCheck()
	{
		return bytesForProtocolCheck;
	}

	public void setBytesForProtocolCheck(int bytesForProtocolCheck)
	{
		this.bytesForProtocolCheck = bytesForProtocolCheck;
	}

	public LoginProtocol getLoginProtocol()
	{
		return loginProtocol;
	}

	public void setLoginProtocol(LoginProtocol loginProtocol)
	{
		this.loginProtocol = loginProtocol;
	}
}
