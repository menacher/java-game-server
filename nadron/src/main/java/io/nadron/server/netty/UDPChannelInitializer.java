package io.nadron.server.netty;

import io.nadron.handlers.netty.UDPEventEncoder;
import io.nadron.handlers.netty.UDPUpstreamHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;



public class UDPChannelInitializer extends ChannelInitializer<DatagramChannel>
{
	/**
	 * This pipeline will be shared across all the channels. In Netty UDP
	 * implementation it does not make sense to have different pipelines for
	 * different channels as the protocol is essentially "connection-less"
	 */
	ChannelPipeline pipeline;
	private UDPEventEncoder udpEventEncoder;
	
	// Create a default pipeline implementation.
	private UDPUpstreamHandler upstream;

	public UDPChannelInitializer()
	{

	}

	public UDPChannelInitializer(UDPUpstreamHandler upstream)
	{
		this.upstream = upstream;
	}

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		// pipeline is shared across all channels.
		pipeline = ch.pipeline();
		pipeline.addLast("upstream", upstream);
		
		// Downstream handlers - Filter for data which flows from server to
		// client. Note that the last handler added is actually the first
		// handler for outgoing data.
		// TODO since this is not handling datagram packet will it work out of box?
		pipeline.addLast("udpEventEncoder", udpEventEncoder);
		
	}

	public void setUpstream(UDPUpstreamHandler upstream)
	{
		this.upstream = upstream;
	}
	
	public UDPUpstreamHandler getUpstream()
	{
		return upstream;
	}

	public UDPEventEncoder getUdpEventEncoder() 
	{
		return udpEventEncoder;
	}

	public void setUdpEventEncoder(UDPEventEncoder udpEventEncoder) 
	{
		this.udpEventEncoder = udpEventEncoder;
	}
	
	
}
