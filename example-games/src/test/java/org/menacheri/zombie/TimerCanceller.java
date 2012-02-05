package org.menacheri.zombie;

import java.util.concurrent.ScheduledExecutorService;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.zombie.domain.ZombieCommands;


public class TimerCanceller extends SimpleChannelUpstreamHandler
{
	String type = null;
	ScheduledExecutorService service = null;
	public TimerCanceller(String type,ScheduledExecutorService service)
	{
		this.type = type;
		this.service = service;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		Object message = e.getMessage();
		if(message instanceof IEvent)
		{
			IEvent event = (IEvent)message;
			if(Events.SERVER_OUT_TCP == event.getType() || Events.SERVER_OUT_UDP == event.getType())
			{
				ChannelBuffer apocalypse = (ChannelBuffer) event.getSource();
				if(apocalypse.readableBytes()>=4)
				{
					int cmd = apocalypse.readInt();
					ZombieCommands command = ZombieCommands.getCommand(cmd);
					if(command == ZombieCommands.APOCALYPSE)
					{
						System.out.println("Cancelling " + type +  " timer due to apocalypse");
						service.shutdown();
						e.getChannel().close();
					}
				}
			}
		}
		if(message instanceof ChannelBuffer)
		{
			ChannelBuffer apocalypse = (ChannelBuffer) message;
			if(apocalypse.readableBytes()>=4)
			{
				int cmd = apocalypse.readInt();
				ZombieCommands command = ZombieCommands.getCommand(cmd);
				if(command == ZombieCommands.APOCALYPSE)
				{
					System.out.println("Cancelling " + type +  " timer");
					service.shutdown();
					e.getChannel().close();
				}
			}
		}
		super.messageReceived(ctx, e);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		System.out.println("Going to close channel in timer canceller handler");
		e.getChannel().close();
	}
}
