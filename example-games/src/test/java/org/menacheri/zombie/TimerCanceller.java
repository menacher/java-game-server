package org.menacheri.zombie;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.MessageList;

import java.util.concurrent.ScheduledExecutorService;

import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.zombie.domain.ZombieCommands;


public class TimerCanceller extends ChannelInboundHandlerAdapter
{
	String type = null;
	ScheduledExecutorService service = null;
	public TimerCanceller(String type,ScheduledExecutorService service)
	{
		this.type = type;
		this.service = service;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx,
			MessageList<Object> msgs) throws Exception
	{
		MessageList<Event> events = msgs.cast();
		for(Event event: events){
			if(Events.NETWORK_MESSAGE == event.getType())
			{
				ByteBuf apocalypse = (ByteBuf) event.getSource();
				if(apocalypse.readableBytes()>=4)
				{
					int cmd = apocalypse.readInt();
					ZombieCommands command = ZombieCommands.CommandsEnum.fromInt(cmd);
					if(command == ZombieCommands.APOCALYPSE)
					{
						System.out.println("Cancelling " + type +  " timer due to apocalypse");
						service.shutdown();
						ctx.channel().close();
					}
				}
			}
		}
		msgs.releaseAll();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
	}
}
