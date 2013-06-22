package io.nadron.protocols;

import io.nadron.app.PlayerSession;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


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

	/**
	 * Name of the idle state check handlers which will be removed by protocol
	 * manually if required from pipeline.
	 */
	public static final String IDLE_STATE_CHECK_HANDLER = "idleStateCheck";
	
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
