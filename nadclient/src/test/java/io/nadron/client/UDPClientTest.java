package io.nadron.client;

import static org.junit.Assert.assertEquals;
import io.nadron.client.handlers.netty.UDPPipelineFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;

import org.junit.Test;

public class UDPClientTest {

	@Test
	public void multiChannelCreationTest() {
		NioEventLoopGroup boss = new NioEventLoopGroup();
		DefaultChannelGroup defaultChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		for (int i = 0; i < 100; i++) {
			Bootstrap udpBootstrap = new Bootstrap();
			udpBootstrap
					.group(boss)
					.channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(
							UDPPipelineFactory
									.getInstance(new InetSocketAddress(18090)));
			DatagramChannel datagramChannel = (DatagramChannel) udpBootstrap
					.bind(new InetSocketAddress(0)).syncUninterruptibly()
					.channel();
			defaultChannelGroup.add(datagramChannel);
		}
		assertEquals(defaultChannelGroup.size(), 100);
	}
}
