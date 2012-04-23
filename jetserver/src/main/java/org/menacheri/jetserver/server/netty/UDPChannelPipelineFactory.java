package org.menacheri.jetserver.server.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.menacheri.jetserver.handlers.netty.MessageBufferEventDecoder;
import org.menacheri.jetserver.handlers.netty.MessageBufferEventEncoder;
import org.menacheri.jetserver.handlers.netty.UDPUpstreamHandler;


public class UDPChannelPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * This pipeline will be shared across all the channels. In Netty UDP
	 * implementation it does not make sense to have different pipelines for
	 * different channels as the protocol is essentials "connection-less"
	 */
	ChannelPipeline pipeline;
	/**
	 * The Message buffer event decoder and encoder for the pipeline.
	 */
	private MessageBufferEventDecoder messageBufferEventDecoder;
	private MessageBufferEventEncoder messageBufferEventEncoder;
	
	// Create a default pipeline implementation.
	private UDPUpstreamHandler upstream;

	public UDPChannelPipelineFactory()
	{

	}

	public UDPChannelPipelineFactory(UDPUpstreamHandler upstream)
	{
		this.upstream = upstream;
	}

	/**
	 * This method creates a single pipeline object that will be shared for all
	 * the channels.
	 */
	public void init()
	{
		pipeline = pipeline();
		
		pipeline.addLast("messageBufferEventDecoder", messageBufferEventDecoder);
		pipeline.addLast("upstream", upstream);
		
		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		pipeline.addLast("messageBufferEventEncoder",messageBufferEventEncoder);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		return pipeline;
	}

	public void setUpstream(UDPUpstreamHandler upstream)
	{
		this.upstream = upstream;
	}

	public MessageBufferEventDecoder getMessageBufferEventDecoder()
	{
		return messageBufferEventDecoder;
	}

	public void setMessageBufferEventDecoder(
			MessageBufferEventDecoder messageBufferEventDecoder)
	{
		this.messageBufferEventDecoder = messageBufferEventDecoder;
	}

	public MessageBufferEventEncoder getMessageBufferEventEncoder()
	{
		return messageBufferEventEncoder;
	}

	public void setMessageBufferEventEncoder(
			MessageBufferEventEncoder messageBufferEventEncoder)
	{
		this.messageBufferEventEncoder = messageBufferEventEncoder;
	}

	public UDPUpstreamHandler getUpstream()
	{
		return upstream;
	}

}
