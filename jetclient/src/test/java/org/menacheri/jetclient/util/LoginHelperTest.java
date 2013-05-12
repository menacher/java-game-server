package org.menacheri.jetclient.util;

import static org.junit.Assert.assertEquals;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

import org.junit.Test;
import org.menacheri.jetclient.util.LoginHelper.LoginBuilder;

public class LoginHelperTest {

	@Test
	public void writeAndReadCredsTest() throws Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1")
				.jetserverTcpHostName("localhost").tcpPort(18090)
				.jetserverUdpHostName("255.255.255.255").udpPort(18090);
		LoginHelper helper = builder.build();
		ByteBuf byteBuf = helper.getLoginBuffer(new InetSocketAddress(18090)).getNativeBuffer();
		assertEquals("user" , NettyUtils.readString(byteBuf));
		assertEquals("pass" , NettyUtils.readString(byteBuf));
		assertEquals("Zombie_ROOM_1" , NettyUtils.readString(byteBuf));
		assertEquals(new InetSocketAddress(18090) , NettyUtils.readSocketAddress(byteBuf));
	}
}
