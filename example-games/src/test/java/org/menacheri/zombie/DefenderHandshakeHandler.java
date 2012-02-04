package org.menacheri.zombie;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.menacheri.protocols.ServerDataProtocols;
import org.menacheri.zombie.domain.IAM;


public class DefenderHandshakeHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * Utility handler provided by netty to add the length of the outgoing
	 * message to the message as a header.
	 */
	private LengthFieldPrepender lengthFieldPrepender;
	private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(15); 
	private static final TimerCanceller canceller = new TimerCanceller("Defender",service);
	public DefenderHandshakeHandler()
	{
		lengthFieldPrepender = new LengthFieldPrepender(2);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		String message = (String)e.getMessage();
		String replyAck = message.substring(1);
		String protocol = "" + ServerDataProtocols.getInt(ServerDataProtocols.CHANNEL_BUFFER_PROTOCOL);
		String ack = protocol + replyAck;
		ChannelFuture future = e.getChannel().write(ack);
		future.addListener(new ChannelFutureListener()
		{
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception
			{
				Channel channel = future.getChannel();
				ChannelPipeline pipeline = channel.getPipeline();
				pipeline.remove("framer");
				pipeline.remove("stringDecoder");
				pipeline.remove("stringEncoder");
				pipeline.remove("clientHandshakeHandler");
				pipeline.remove("nulEncoder");
				
				// Upstream handlers or encoders (i.e towards server) are added to
				// pipeline now.
				//pipeline.addLast("byteArrayToChannelBufferEncoder", new ByteArrayToChannelBufferEncoder());
				pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(4096, 0, 2,0,2));
				pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
				pipeline.addLast("canceller", canceller);
				ChannelBuffer buf = ChannelBuffers.buffer(8);
				buf.writeInt(2);// defender
				buf.writeInt(3);// select team as defender.
				Thread.sleep(500);
				channel.write(buf);
				WriteByte write = new WriteByte(channel, null,IAM.DEFENDER);
				
				service.scheduleAtFixedRate(write,1000l,100, TimeUnit.MILLISECONDS);
			}
		});
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.out.println("\nException caught in class DefenderHandshakeHandler");
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	public static ScheduledExecutorService getService()
	{
		return service;
	}
}
