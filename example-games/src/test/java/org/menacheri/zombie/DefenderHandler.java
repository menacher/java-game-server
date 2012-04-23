package org.menacheri.zombie;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.menacheri.zombie.domain.IAM;

public class DefenderHandler extends SimpleChannelUpstreamHandler
{
	private static final IAM I_AM = IAM.DEFENDER;
	private static final Map<InetSocketAddress, DatagramChannel> CLIENTS = new HashMap<InetSocketAddress, DatagramChannel>();
	private final UDPClient udpClient;

	public DefenderHandler()
	{
		this.udpClient = new UDPClient(this, I_AM, "255.255.255.255", 18090,
				Executors.newCachedThreadPool());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		Object message = e.getMessage();
		if (message instanceof Event)
		{
			Event event = (Event) message;
			if (Events.START == event.getType())
			{
				// TCP write to server
				WriteByte write = new WriteByte(e.getChannel(), null,
						IAM.DEFENDER);
				ZombieClient.SERVICE.scheduleAtFixedRate(write, 10000l, 500,
						TimeUnit.MILLISECONDS);
				// For UDP write to server
				connectUDP(e.getChannel());
			}
			else if (Events.NETWORK_MESSAGE == event.getType())
			{
				ChannelBuffer buffer = (ChannelBuffer) event.getSource();
				if (buffer.readableBytes() >= 4)
				{
					System.out
							.println("UDP event from server in DefenderHandler: "
									+ buffer.readInt());
				}
				else
				{
					System.out
							.println("UDP Event does not have expected data in DefenderHandler");
				}
			}
			else
			{
				super.messageReceived(ctx, e);
			}
		}
	}

	public InetSocketAddress connectLocal() throws UnknownHostException
	{
		DatagramChannel c = udpClient.createDatagramChannel();
		InetSocketAddress localAddress = udpClient.getLocalAddress(c);
		CLIENTS.put(localAddress, c);
		return localAddress;
	}

	public void connectUDP(Channel channel)
	{
		InetSocketAddress address = ZombieClient.CHANNEL_ID_ADDRESS_MAP.get(channel.getId());
		System.out.println("UDP address for connect UDP: " + address);
		final DatagramChannel c = CLIENTS.get(address);
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
			ZombieClient.SERVICE.submit(runnable);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.out.println("\nException caught in class DefenderHandler");
		e.getCause().printStackTrace();
		e.getChannel().close();
		ZombieClient.SERVICE.shutdown();
	}

}
