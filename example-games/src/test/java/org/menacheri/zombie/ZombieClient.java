package org.menacheri.zombie;


import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.menacheri.event.Events;
import org.menacheri.util.NettyUtils;


public class ZombieClient
{
	public static final ScheduledExecutorService SERVICE;
	public static final Map<Integer, InetSocketAddress> CHANNEL_ID_ADDRESS_MAP;
	
	static {
		SERVICE = Executors.newScheduledThreadPool(15);
		CHANNEL_ID_ADDRESS_MAP = new HashMap<Integer, InetSocketAddress>();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				SERVICE.shutdown();
			}
		});
	}
	
	public static void main(String[] args) throws UnknownHostException
	{
		String host = "localhost";
		int port = 18090;
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool());
		final DefenderHandler defHandler = new DefenderHandler();
		PipelineFactory defFactory = new PipelineFactory(defHandler);
		final ZombieHandler zomHandler = new ZombieHandler();
		PipelineFactory zomFactory = new PipelineFactory(zomHandler);
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		// At client side option is tcpNoDelay and at server child.tcpNoDelay
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		for (int i = 1; i<=50;i++){
			final InetSocketAddress udpLocalAddress;
			if(i%2==0){
				bootstrap.setPipelineFactory(defFactory);
				udpLocalAddress = defHandler.connectLocal();
			}else{
				bootstrap.setPipelineFactory(zomFactory);
				udpLocalAddress = zomHandler.connectLocal();
			}
			
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
					port));
			
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future) throws Exception
				{
					ChannelBuffer loginBuffer = getLoginBuffer("Zombie_ROOM_1_REF_KEY_1",writeSocketAddressToBuffer(udpLocalAddress));
					Channel channel = future.getChannel();
					CHANNEL_ID_ADDRESS_MAP.put(channel.getId(), udpLocalAddress);
					channel.write(loginBuffer);
				}
			});
		}
	}
	
	public static ChannelBuffer getLoginBuffer(String refKey, ChannelBuffer udpAddress)
	{
		ChannelBuffer opCode = ChannelBuffers.buffer(1);
		opCode.writeByte(Events.LOG_IN);
		String username = "user";
		String password = "pass";
		// write username,password and ref key.
		ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(opCode,
				NettyUtils.writeStrings(username,password,refKey),udpAddress);
		return buffer;
	}
	
	public static ChannelBuffer writeSocketAddressToBuffer(InetSocketAddress localAddress){
		ChannelBuffer hostName = NettyUtils.writeString(localAddress
				.getHostName());
		ChannelBuffer portNum = ChannelBuffers.buffer(4);
		portNum.writeInt(localAddress.getPort());
		ChannelBuffer socketAddress = ChannelBuffers.wrappedBuffer(hostName,
				portNum);
		return socketAddress;
	}
}
