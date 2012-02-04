package org.menacheri;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.menacheri.util.NettyUtils;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
	public void testNettyUtilStringWriteRead()
	{
		String msg = "Hello World!";
		ChannelBuffer stringBuffer = NettyUtils.writeString(msg);
		String reply = NettyUtils.readString(stringBuffer);
		System.out.println(reply);
		Assert.assertEquals(msg, reply);
	}
	
	public void testNettyUtilMultiStringWriteRead()
	{
		String hello = "Hello ";
		String world = "World!";
		ChannelBuffer stringBuffer1 = NettyUtils.writeString(hello);
		ChannelBuffer stringBuffer2 = NettyUtils.writeString(world);
		ChannelBuffer stringBuffer = ChannelBuffers.wrappedBuffer(stringBuffer1,stringBuffer2);
		String helloReply = NettyUtils.readString(stringBuffer);
		String worldReply = NettyUtils.readString(stringBuffer);
		System.out.println(helloReply + worldReply);
		Assert.assertEquals(hello, helloReply);
		Assert.assertEquals(worldReply, world);
	}
	
	public void testNettyUtilVarArgsStringWriteRead()
	{
		String hello = "Hello ";
		String world = "World!";
		ChannelBuffer stringBuffer = NettyUtils.writeStrings(hello,world);
		String helloReply = NettyUtils.readString(stringBuffer);
		String worldReply = NettyUtils.readString(stringBuffer);
		System.out.println(helloReply + worldReply);
		Assert.assertEquals(hello, helloReply);
		Assert.assertEquals(worldReply, world);
	}
}
