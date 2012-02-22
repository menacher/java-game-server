package org.menacheri.zombie;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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



public class DefenderHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * Utility handler provided by netty to add the length of the outgoing
	 * message to the message as a header.
	 */
	private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(15); 
	private static final IAM iam = IAM.DEFENDER;
	private static final Map<InetSocketAddress, DatagramChannel> clients = new HashMap<InetSocketAddress, DatagramChannel>();
	public UDPClient udpClient;
	public DefenderHandler()
	{
		this.udpClient = new UDPClient(this,iam,"255.255.255.255",18090,service);
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
				Channel channel = e.getChannel();
				loginUDP(channel);
				ChannelBuffer buf = ChannelBuffers.buffer(1 + 8);
				buf.writeByte(Events.SESSION_MESSAGE);
				buf.writeInt(2);// defender
				buf.writeInt(3);// select team as defender.
				Thread.sleep(500);
				channel.write(buf);
				WriteByte write = new WriteByte(channel, null,IAM.DEFENDER);
				
				service.scheduleAtFixedRate(write,10000l,500, TimeUnit.MILLISECONDS);
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
					System.out.println("UDP event from server in DefenderHandler: " + buffer.readInt());
				}
				else
				{
					System.out.println("UDP Event does not have expected data in DefenderHandler");
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
					System.out.println("Sending UDP login from DefenderHandler was a failure.");
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
		if ((udpClient != null) && (c != null))
		{
			System.out.println("Going to connect UDP in DefenderHandler");
			// Connect the UDP
			Runnable runnable = new Runnable()
			{
				@Override
				public void run()
				{
					udpClient.start(c);
				}
			};
			service.submit(runnable);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.out.println("\nException caught in class DefenderHandler");
		e.getCause().printStackTrace();
		e.getChannel().close();
		service.shutdown();
	}

	public static ScheduledExecutorService getService()
	{
		return service;
	}
}
