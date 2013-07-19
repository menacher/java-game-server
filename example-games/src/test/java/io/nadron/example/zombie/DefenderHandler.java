package io.nadron.example.zombie;

import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.example.zombie.domain.IAM;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DefenderHandler extends SimpleChannelInboundHandler<Event>
{
	private static final IAM I_AM = IAM.DEFENDER;
	private static final Map<InetSocketAddress, DatagramChannel> CLIENTS = new HashMap<InetSocketAddress, DatagramChannel>();
	private final UDPClient udpClient;

	@Override
	public void channelRead0(ChannelHandlerContext ctx,
			Event event) throws Exception
	{
		if (Events.START == event.getType())
		{
			// TCP write to server
			WriteByte write = new WriteByte(ctx.channel(), null,
					IAM.DEFENDER);
			ZombieClient.SERVICE.scheduleAtFixedRate(write, 10000l, 500,
					TimeUnit.MILLISECONDS);
			// For UDP write to server
			connectUDP(ctx.channel());
		}
		else if (Events.NETWORK_MESSAGE == event.getType())
		{
			ByteBuf buffer = (ByteBuf) event.getSource();
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
	}
	
	public DefenderHandler() throws UnknownHostException, InterruptedException
	{
		this.udpClient = new UDPClient(this, I_AM, "255.255.255.255", 18090,
				new NioEventLoopGroup());
	}

	public InetSocketAddress connectLocal() throws UnknownHostException, InterruptedException
	{
		DatagramChannel c = udpClient.createDatagramChannel();
		InetSocketAddress localAddress = udpClient.getLocalAddress(c);
		CLIENTS.put(localAddress, c);
		return localAddress;
	}

	public void connectUDP(Channel channel)
	{
		InetSocketAddress address = ZombieClient.CHANNEL_ID_ADDRESS_MAP.get(channel);
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
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("\nException caught in class DefenderHandler");
		cause.printStackTrace();
		ctx.channel().close();
		ZombieClient.SERVICE.shutdown();
	}

}
