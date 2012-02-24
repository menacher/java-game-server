package org.menacheri.zombie;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.handlers.netty.EventDecoder;


public class PipelineFactory implements ChannelPipelineFactory
{
	private static final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(2);
	private static final TimerCanceller CANCELLER = new TimerCanceller("Zombie",DefenderHandler.getService());
	private static final EventDecoder EVENT_DECODER = new EventDecoder();
	private static final StartEventCounter COUNTER = new StartEventCounter();
	private final ChannelHandler businessHandler;
	private static final AtomicInteger INTEGER = new AtomicInteger(0);
	public PipelineFactory(ChannelHandler handler)
	{
		this.businessHandler = handler;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(256, 0, 2,0,2));
		pipeline.addLast("lengthFieldPrepender", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("eventDecoder", EVENT_DECODER);
		pipeline.addLast("counter", COUNTER);
		pipeline.addLast("businessHandler",businessHandler);
		pipeline.addLast("canceller",CANCELLER);
		
		return pipeline;
	}

	public static class StartEventCounter extends OneToOneDecoder
	{
		private final AtomicInteger counter = new AtomicInteger(0);
		@Override
		protected Object decode(ChannelHandlerContext ctx, Channel channel,
				Object msg) throws Exception
		{
			IEvent event = (IEvent)msg;
			if(Events.START == event.getType())
			{
				int started = counter.incrementAndGet();
				System.out.println("Started: " + started);
			}
			else if(Events.SERVER_OUT_TCP == event.getType())
			{
				System.out.println("Client Recieved Data No: " + INTEGER.addAndGet(5000));
			}
			else
			{
				System.out.println("Recieved eventType: " + event.getType());
			}
			return msg;
		}
	}
}
