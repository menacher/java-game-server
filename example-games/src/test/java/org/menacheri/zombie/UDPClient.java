package org.menacheri.zombie;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.menacheri.event.Events;
import org.menacheri.handlers.netty.EventDecoder;
import org.menacheri.util.NettyUtils;
import org.menacheri.zombie.domain.IAM;


public class UDPClient
{
	private static final EventDecoder EVENT_DECODER = new EventDecoder();
	private final ConnectionlessBootstrap b;
	private final IAM iam;
	private final SocketAddress serverAddress;
	
	public UDPClient(ChannelHandler businessHandler, IAM iam,String remoteHost, int remotePort,ExecutorService executorService)
	{
		this.iam = iam;
		this.serverAddress = new InetSocketAddress(remoteHost,remotePort);
		DatagramChannelFactory f = new NioDatagramChannelFactory(executorService);
		// Create only one bootstrap per instance. But use it to make multiple udp channels.
		b = new ConnectionlessBootstrap(f);
		ChannelPipeline p = b.getPipeline();
		p.addLast("eventDecoder",EVENT_DECODER);
		p.addLast("businessHandler", businessHandler);
		b.setOption("broadcast", "true");
	}
	
	public ConnectionlessBootstrap getConnectionlessBootstrap()
	{
		return b;
	}
	
	/**
	 * Method used to create a datagram channel from the bootstrap instance.
	 * @return
	 * @throws UnknownHostException
	 */
	public DatagramChannel createDatagramChannel()  throws UnknownHostException
	{
		String localHost = InetAddress.getLocalHost().getHostAddress();
		DatagramChannel c = (DatagramChannel) b.bind( new InetSocketAddress(localHost,0));
		return c;
	}
	
	public InetSocketAddress getLocalAddress(DatagramChannel c)
	{
		InetSocketAddress add = (InetSocketAddress)c.getLocalAddress();
		return add;
	}
	
	public void start(DatagramChannel c)
	{
		if(c.isBound())
		{
			// Write the connect statement. TODO repeat till we get start.
			System.out.println("Events.CONNECT_UDP: " + Events.CONNECT_UDP);
			ChannelBuffer buf = NettyUtils.createBufferForOpcode(Events.CONNECT_UDP);
			
			ChannelFuture future = c.write(buf, serverAddress);
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future) throws Exception
				{
					if(!future.isSuccess())
					{
						System.out.println("CONNECT_UDP write to server unsuccessful: " + future.getCause().getMessage());
					}
				}
			});
			
			future.awaitUninterruptibly();
			WriteByte write = new WriteByte(c, serverAddress,iam);
			DefenderHandler.getService().scheduleAtFixedRate(write,10000l,1000l, TimeUnit.MILLISECONDS);
		}
		else
		{
			System.out.println("Error: datagram channel is not bound");
		}
	}
}
