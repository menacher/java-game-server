package org.menacheri.zombie;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.util.NettyUtils;
import org.menacheri.zombie.domain.IAM;


public class ZombieHandler extends SimpleChannelUpstreamHandler
{
	private static final IAM iam = IAM.ZOMBIE;
	private static final Map<InetSocketAddress, DatagramChannel> clients = new HashMap<InetSocketAddress, DatagramChannel>();
	private UDPClient udpClient;
	
	public ZombieHandler()
	{
		udpClient = new UDPClient(this,iam,"255.255.255.255",8090,DefenderHandler.getService());
	}
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		Object message = e.getMessage();
		if(message instanceof IEvent)
		{
			IEvent event = (IEvent)message;
			if(Events.START == event.getType())
			{
				loginUDP(e.getChannel());
				WriteByte write = new WriteByte(e.getChannel(),null, IAM.ZOMBIE);
				DefenderHandler.getService().scheduleAtFixedRate(write,2000l,500l,TimeUnit.MILLISECONDS);
			}
			else if(Events.LOG_IN_SUCCESS == event.getType())
			{
				connectUDP(event);
			}
			else if(Events.SERVER_OUT_UDP == event.getType())
			{
				ChannelBuffer buffer = (ChannelBuffer)event.getSource();
				if(buffer.readableBytes() >= 4)
				{
					System.out.println("UDP event from server in ZombieHandler: " + buffer.readInt());
				}
				else
				{
					System.out.println("UDP Event does not have expected data in ZombieHandler");
				}
			}
			else
			{
				super.messageReceived(ctx, e);
			}
		}
		
	}
	
	public void loginUDP(Channel channel) throws UnknownHostException
	{
		ChannelBuffer opCode = NettyUtils.createBufferForOpcode(Events.LOG_IN_UDP);
		final DatagramChannel c = udpClient.createDatagramChannel();
		final InetSocketAddress localAddress = udpClient.getLocalAddress(c);
		ChannelBuffer hostName = NettyUtils.writeString(localAddress.getHostName());
		ChannelBuffer portNum = ChannelBuffers.buffer(4);
		portNum.writeInt(localAddress.getPort());
		ChannelBuffer login = ChannelBuffers.wrappedBuffer(opCode,hostName,portNum);
		ChannelFuture loginFuture = channel.write(login);
		loginFuture.addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future) throws Exception
			{
				if(future.isSuccess())
				{
					clients.put(localAddress, c);
				}
				else
				{
					System.out.println("Sending UDP login from ZombieHandler was a failure.");
				}
			}
		});
		loginFuture.awaitUninterruptibly();
	}
	
	public void connectUDP(IEvent event)
	{
		ChannelBuffer buffer = (ChannelBuffer)event.getSource();
		InetSocketAddress address = NettyUtils.readSocketAddress(buffer);
		final DatagramChannel c = clients.get(address);
		if(null != udpClient)
		{
			// Connect the UDP
			System.out.println("Going to connect UDP in ZombieHandler");
			Runnable runnable = new Runnable()
			{
				@Override
				public void run()
				{
					udpClient.start(c);
				}
			};
			DefenderHandler.getService().submit(runnable);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.out.println("\nException caught in class ZombieHandler");
		System.out.println(e.getCause());
		e.getChannel().close();
	}
}
