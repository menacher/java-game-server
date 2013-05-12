package org.menacheri.zombie;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.concurrent.atomic.AtomicInteger;

import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.handlers.netty.EventDecoder;
import org.menacheri.jetserver.handlers.netty.MessageBufferEventEncoder;


public class PipelineFactory extends ChannelInitializer<SocketChannel>
{
	private static final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(2);
	private static final TimerCanceller CANCELLER = new TimerCanceller("Zombie",ZombieClient.SERVICE);
	private static final EventDecoder EVENT_DECODER = new EventDecoder();
	private static final MessageBufferEventEncoder EVENT_ENCODER= new MessageBufferEventEncoder();
	private static final StartEventCounter COUNTER = new StartEventCounter();
	private final ChannelHandler businessHandler;
	private static final AtomicInteger INTEGER = new AtomicInteger(0);
	public PipelineFactory(ChannelHandler handler)
	{
		this.businessHandler = handler;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(256, 0, 2,0,2));
		pipeline.addLast("lengthFieldPrepender", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("eventDecoder", EVENT_DECODER);
		//pipeline.addLast("eventEncoder" , EVENT_ENCODER);
		pipeline.addLast("counter", COUNTER);
		pipeline.addLast("businessHandler",businessHandler);
		pipeline.addLast("canceller",CANCELLER);
	}
	
	public static class StartEventCounter extends MessageToMessageDecoder<Event>
	{
		private final AtomicInteger counter = new AtomicInteger(0);
		@Override
		protected Object decode(ChannelHandlerContext ctx, Event event)
				throws Exception {
			if(Events.START == event.getType())
			{
				int started = counter.incrementAndGet();
				System.out.println("Started: " + started);
			}
			else if(Events.NETWORK_MESSAGE == event.getType())
			{
				System.out.println("Client Recieved Data No: " + INTEGER.addAndGet(5000));
			}
			else
			{
				System.out.println("Recieved eventType: " + event.getType());
			}
			return event;
		}
	}
	
}
