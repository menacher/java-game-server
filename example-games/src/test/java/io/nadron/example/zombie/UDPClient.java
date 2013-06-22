package io.nadron.example.zombie;

import io.nadron.event.Events;
import io.nadron.example.zombie.domain.IAM;
import io.nadron.handlers.netty.EventDecoder;
import io.nadron.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;



public class UDPClient
{
	private static final EventDecoder EVENT_DECODER = new EventDecoder();
	private final Bootstrap b;
	private final IAM iam;
	private final SocketAddress serverAddress;
	
	public UDPClient(
			final ChannelInboundHandlerAdapter businessHandler,
			IAM iam, String remoteHost, int remotePort, EventLoopGroup boss)
			throws UnknownHostException, InterruptedException
	{
		this.iam = iam;
		this.serverAddress = new InetSocketAddress(remoteHost,remotePort);
		// Create only one bootstrap per instance. But use it to make multiple udp channels.
		b = new Bootstrap();
		b.group(boss).channel(NioDatagramChannel.class)
		.option(ChannelOption.SO_BROADCAST, true)
		.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				p.addLast("eventDecoder", EVENT_DECODER);
				p.addLast("businessHandler", businessHandler);
			}
			
		});
		
	}
	
	/**
	 * Method used to create a datagram channel from the bootstrap instance.
	 * @return
	 * @throws UnknownHostException
	 */
	public DatagramChannel createDatagramChannel()  throws UnknownHostException, InterruptedException
	{
		String localHost = InetAddress.getLocalHost().getHostAddress();
		DatagramChannel c = (DatagramChannel) b.bind( new InetSocketAddress(localHost,0)).sync().channel();
		return c;
	}
	
	public InetSocketAddress getLocalAddress(DatagramChannel c)
	{
		InetSocketAddress add = (InetSocketAddress)c.localAddress();
		return add;
	}
	
	public void start(DatagramChannel c)
	{
		try{
			if(c.isActive())
			{
				// Write the connect statement. TODO repeat till we get start.
				System.out.println("Events.CONNECT: " + Events.CONNECT);
				ByteBuf buf = NettyUtils.createBufferForOpcode(Events.CONNECT);
				
				ChannelFuture future = c.write(buf);
				future.addListener(new ChannelFutureListener()
				{
					@Override
					public void operationComplete(ChannelFuture future) throws Exception
					{
						if(!future.isSuccess())
						{
							System.out.println("CONNECT_UDP write to server unsuccessful: " + future.cause().getMessage());
						}
					}
				});
				
				future.awaitUninterruptibly();
				WriteByte write = new WriteByte(c, serverAddress, iam);
				ZombieClient.SERVICE.scheduleAtFixedRate(write,10000l,5000l, TimeUnit.MILLISECONDS);
			}
			else
			{
				System.out.println("Error: datagram channel is not bound");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
