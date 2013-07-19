package io.nadron.example.zombie;

import io.nadron.event.Events;
import io.nadron.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ZombieClient
{
	public static final ScheduledExecutorService SERVICE;
	public static final Map<Channel, InetSocketAddress> CHANNEL_ID_ADDRESS_MAP;
	
	static {
		SERVICE = Executors.newSingleThreadScheduledExecutor();
		CHANNEL_ID_ADDRESS_MAP = new HashMap<Channel, InetSocketAddress>();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				SERVICE.shutdown();
			}
		});
	}
	
	public static void main(String[] args) throws UnknownHostException, InterruptedException
	{
		String host = "localhost";
		int port = 18090;
		final DefenderHandler defHandler = new DefenderHandler();
		PipelineFactory defFactory = new PipelineFactory(defHandler);
		final ZombieHandler zomHandler = new ZombieHandler();
		PipelineFactory zomFactory = new PipelineFactory(zomHandler);
		EventLoopGroup boss = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(boss).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true);
		for (int i = 1; i<=1;i++){
			final InetSocketAddress udpLocalAddress = null;
			if(i%2==0){
				bootstrap.handler(defFactory);
				//udpLocalAddress = defHandler.connectLocal();
			}else{
				bootstrap.handler(zomFactory);
				//udpLocalAddress = zomHandler.connectLocal();
			}
			InetSocketAddress remoteAddress = new InetSocketAddress(host,
					port);
			ChannelFuture future = bootstrap.connect(remoteAddress);
			
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future) throws Exception
				{
					if (future.isSuccess()) 
					{
						ByteBuf loginBuffer = getLoginBuffer("Zombie_ROOM_1",
								writeSocketAddressToBuffer(udpLocalAddress));
						Channel channel = future.channel();
						CHANNEL_ID_ADDRESS_MAP.put(channel,
								udpLocalAddress);
						ChannelFuture writeFuture = channel.write(loginBuffer);
						writeFuture.addListener(new ChannelFutureListener() 
						{
							@Override
							public void operationComplete(ChannelFuture future)
									throws Exception 
							{
								if (!future.isSuccess()) 
								{
									future.cause().printStackTrace();
								}
							}
						});
					} 
					else 
					{
						future.cause().printStackTrace();
					}
				}
			});
		}
	}
	
	public static ByteBuf getLoginBuffer(String refKey, ByteBuf udpAddress)
	{
		ByteBuf header = Unpooled.buffer(2);
		header.writeByte(Events.LOG_IN);
		header.writeByte(Events.PROTCOL_VERSION);
		String username = "user";
		String password = "pass";
		// write username,password and ref key.
		ByteBuf buffer = Unpooled.wrappedBuffer(header,
				NettyUtils.writeStrings(username,password,refKey),udpAddress);
		return buffer;
	}
	
	public static ByteBuf writeSocketAddressToBuffer(InetSocketAddress localAddress){
		ByteBuf hostName = NettyUtils.writeString(localAddress
				.getHostName());
		ByteBuf portNum = Unpooled.buffer(4);
		portNum.writeInt(localAddress.getPort());
		ByteBuf socketAddress = Unpooled.wrappedBuffer(hostName,
				portNum);
		return socketAddress;
	}
}
