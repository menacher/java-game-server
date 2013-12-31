package io.nadron.client.util;

import static org.junit.Assert.assertEquals;
import io.nadron.client.util.LoginHelper;
import io.nadron.client.util.NettyUtils;
import io.nadron.client.util.LoginHelper.LoginBuilder;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

import org.junit.Test;

public class LoginHelperTest {

	@Test
	public void writeAndReadCredsTest() throws Exception
	{
		LoginBuilder builder = new LoginBuilder().username("user")
				.password("pass").connectionKey("Zombie_ROOM_1")
				.nadronTcpHostName("localhost").tcpPort(18090)
				.nadronUdpHostName("255.255.255.255").udpPort(18090);
		LoginHelper helper = builder.build();
		ByteBuf byteBuf = helper.getLoginBuffer(new InetSocketAddress(18090)).getNativeBuffer();
		assertEquals("user" , NettyUtils.readString(byteBuf));
		assertEquals("pass" , NettyUtils.readString(byteBuf));
		assertEquals("Zombie_ROOM_1" , NettyUtils.readString(byteBuf));
		assertEquals(new InetSocketAddress(18090) , NettyUtils.readSocketAddress(byteBuf));
	}
	
	@Test
	public void doManyLogins() throws Exception
	{
		for(int i = 1; i <1000; i++){
			writeAndReadCredsTest();
		}
	}
}
