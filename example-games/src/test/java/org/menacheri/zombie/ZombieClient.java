package org.menacheri.zombie;


import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.menacheri.event.Events;
import org.menacheri.util.NettyUtils;


public class ZombieClient
{
	private static final ChannelBuffer LOGIN_BUFFER = getLoginBuffer("Zombie_ROOM_1_REF_KEY_1");
	
	public static void main(String[] args)
	{
		String host = "localhost";
		int port = 18090;
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool());
		DefenderHandler defHandler = new DefenderHandler();
		PipelineFactory defFactory = new PipelineFactory(defHandler);
		ZombieHandler zomHandler = new ZombieHandler();
		PipelineFactory zomFactory = new PipelineFactory(zomHandler);
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		// At client side option is tcpNoDelay and at server child.tcpNoDelay
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		for (int i = 1; i<=50;i++){
			if(i%2==0){
				bootstrap.setPipelineFactory(defFactory);
			}else{
				bootstrap.setPipelineFactory(zomFactory);
			}
			
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
					port));
			
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future) throws Exception
				{
					future.getChannel().write(LOGIN_BUFFER);
				}
			});
		}
	}
	
	public static ChannelBuffer getLoginBuffer(String refKey)
	{
		ChannelBuffer opCode = ChannelBuffers.buffer(1);
		opCode.writeByte(Events.LOG_IN);
		String username = "user";
		String password = "pass";
		// write username,password and ref key.
		ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(opCode,
				NettyUtils.writeStrings(username,password,refKey));
		return buffer;
	}
	
}
