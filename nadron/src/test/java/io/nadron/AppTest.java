package io.nadron;

import static org.junit.Assert.assertEquals;
import io.nadron.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest
{

    @Test
	public void nettyUtilStringWriteRead()
	{
		String msg = "Hello World!";
		ByteBuf stringBuffer = NettyUtils.writeString(msg);
		String reply = NettyUtils.readString(stringBuffer);
		assertEquals(msg, reply);
	}
	
    @Test
	public void nettyUtilMultiStringWriteRead()
	{
		String hello = "Hello ";
		String world = "World!";
		ByteBuf stringBuffer1 = NettyUtils.writeString(hello);
		ByteBuf stringBuffer2 = NettyUtils.writeString(world);
		ByteBuf stringBuffer = Unpooled.wrappedBuffer(stringBuffer1,stringBuffer2);
		String helloReply = NettyUtils.readString(stringBuffer);
		String worldReply = NettyUtils.readString(stringBuffer);
		assertEquals(hello, helloReply);
		assertEquals(worldReply, world);
	}
	
    @Test
	public void nettyUtilVarArgsStringWriteRead()
	{
		String hello = "Hello ";
		String world = "World!";
		ByteBuf stringBuffer = NettyUtils.writeStrings(hello,world);
		String helloReply = NettyUtils.readString(stringBuffer);
		String worldReply = NettyUtils.readString(stringBuffer);
		assertEquals(hello, helloReply);
		assertEquals(worldReply, world);
	}
    
    @Test
    public void readWriteSocketAddress()
    {
    	InetSocketAddress socketAddress = new InetSocketAddress("localhost", 18090);
    	ByteBuf buffer = NettyUtils.writeSocketAddress(socketAddress);
    	InetSocketAddress readAddress = NettyUtils.readSocketAddress(buffer);
    	assertEquals(socketAddress,readAddress);
    }
    
    @Test
    public void readWriteEmptyString()
    {
    	String hello = "Hello";
		String world = "World!";
		String empty = "";
    	String space = " ";
		ByteBuf buffer = NettyUtils.writeStrings(hello, empty, space, world);
    	String[] readStrings = NettyUtils.readStrings(buffer, 4);
    	assertEquals(readStrings.length, 4);
    	assertEquals(readStrings[0], hello);
    	assertEquals(readStrings[1], empty);
    	assertEquals(readStrings[2], space);
    	assertEquals(readStrings[3], world);
    }
}
