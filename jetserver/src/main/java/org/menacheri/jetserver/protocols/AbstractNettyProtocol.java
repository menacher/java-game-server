package org.menacheri.jetserver.protocols;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.util.NettyUtils;

/**
 * This abstract class defines common methods across all protocols. Individual
 * protocol classes extend this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class AbstractNettyProtocol implements Protocol
{
	/**
	 * The name of the protocol. This is set by the child class to appropriate
	 * value while child class instance is created.
	 */
	final String protocolName;

	public AbstractNettyProtocol(String protocolName)
	{
		super();
		this.protocolName = protocolName;
	}

	public LengthFieldBasedFrameDecoder createLengthBasedFrameDecoder()
	{
		return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2);
	}

	@Override
	public String getProtocolName()
	{
		return protocolName;
	}

	@Override
	public void applyProtocol(PlayerSession playerSession,
			boolean clearExistingProtocolHandlers) 
	{
		if(clearExistingProtocolHandlers)
		{
			ChannelPipeline pipeline = NettyUtils
					.getPipeLineOfConnection(playerSession);
			NettyUtils.clearPipeline(pipeline);
		}
		applyProtocol(playerSession);
	}
}
