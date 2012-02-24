package org.menacheri.protocols;

import org.jboss.netty.channel.ChannelHandler;

/**
 * This abstract class defines common methods across all protocols. Individual
 * protocol classes extend this class.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class AbstractNettyProtocol implements IProtocol
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

	public ChannelHandler createLengthBasedFrameDecoder()
	{
		// This will be overriden by spring.
		//return new LengthFieldBasedFrameDecoder(4096, 0, 2, 0, 2);
		return null;
	}

	@Override
	public String getProtocolName()
	{
		return protocolName;
	}

}
