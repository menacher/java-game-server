package org.menacheri.server.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.menacheri.handlers.netty.HandshakeHandler;
import org.menacheri.handlers.netty.NulEncoder;
import org.springframework.beans.factory.annotation.Required;


/**
 * This important class is the starting point pipeline configuration agent. The
 * pipeline object created by this pipeline factory is provided to all incoming
 * connections at first. Later depending on the protocol and Game (Application)
 * these handlers would be reconfigured in the pipeline according to business
 * logic.
 * 
 * @author Abraham Menacherry
 * 
 */
public class HandshakePipelineFactory implements ChannelPipelineFactory
{
	private StringDecoder stringDecoder;
	private StringEncoder stringEncoder;
	private NulEncoder nulEncoder;
	private int frameSize;

	public HandshakePipelineFactory()
	{
		
	}
	
	public HandshakePipelineFactory(StringDecoder stringDecoder,
			StringEncoder stringEncoder, NulEncoder nulEncoder, int frameSize)
	{
		super();
		this.stringDecoder = stringDecoder;
		this.stringEncoder = stringEncoder;
		this.nulEncoder = nulEncoder;
		this.frameSize = frameSize;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();

		// Configure the necessary handlers on the pipeline.
		pipeline = addHandlers(pipeline);

		// Return the pipeline object. This method is called each time a
		// connection is made to server so each connection(i.e channel has its
		// own pipeline object)
		return pipeline;
	}

	/**
	 * Method used to add handlers to the created pipeline. Even though method
	 * is public, it would mostly be used by the getPipeline method of this
	 * class.
	 * 
	 * @param pipeline
	 *            The pipeline object to which these pre-configured handlers
	 *            need to be added.
	 * @return Returns the same pipeline object it received.
	 */
	public ChannelPipeline addHandlers(ChannelPipeline pipeline)
	{
		if (null == pipeline)
			return null;

		// Downstream handlers or encoders (i.e towards server) are added to
		// pipeline now.
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(frameSize,
				Delimiters.nulDelimiter()));
		pipeline.addLast("stringDecoder", stringDecoder);
		pipeline.addLast("handshakeHandler", createHandshakeHandler());

		// Upstream handlers (i.e towards client) are added to pipeline now.
		pipeline.addLast("nulEncoder", nulEncoder);
		pipeline.addLast("stringEncoder", stringEncoder);

		return pipeline;
	}

	/**
	 * This utility method could be considered like a cleanup method. It will
	 * remove all handlers that were added by this class initially to the
	 * pipeline. So a business class can pass its pipeline object to it and this
	 * will remove all the handshake handlers
	 * 
	 * @param pipeline
	 *            The pipeline object from which all handshake handlers need to
	 *            be removed.
	 * @return Returns the same pipeline object it received.
	 */
	public static ChannelPipeline removeHandlers(ChannelPipeline pipeline)
	{
		if (null == pipeline)
			return null;
		
		// Remove all the pipelines this class had initially added.
		pipeline.remove("framer");
		pipeline.remove("stringDecoder");
		pipeline.remove("handshakeHandler");
		pipeline.remove("nulEncoder");
		pipeline.remove("stringEncoder");
		
		return pipeline;
	}
	
	public HandshakeHandler createHandshakeHandler()
	{
		// This method will be overriden by spring using its lookup-method
		// feature
		return new HandshakeHandler();
	}
	
	public StringDecoder getStringDecoder()
	{
		return stringDecoder;
	}

	@Required
	public void setStringDecoder(StringDecoder stringDecoder)
	{
		this.stringDecoder = stringDecoder;
	}

	public StringEncoder getStringEncoder()
	{
		return stringEncoder;
	}

	@Required
	public void setStringEncoder(StringEncoder stringEncoder)
	{
		this.stringEncoder = stringEncoder;
	}

	public NulEncoder getNulEncoder()
	{
		return nulEncoder;
	}

	@Required
	public void setNulEncoder(NulEncoder nulEncoder)
	{
		this.nulEncoder = nulEncoder;
	}

	public int getFrameSize()
	{
		return frameSize;
	}

	@Required
	public void setFrameSize(int frameSize)
	{
		this.frameSize = frameSize;
	}
}
