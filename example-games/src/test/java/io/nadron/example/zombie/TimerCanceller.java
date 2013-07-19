package io.nadron.example.zombie;

import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.example.zombie.domain.ZombieCommands;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ScheduledExecutorService;



public class TimerCanceller extends SimpleChannelInboundHandler<Event>
{
	String type = null;
	ScheduledExecutorService service = null;
	public TimerCanceller(String type,ScheduledExecutorService service)
	{
		this.type = type;
		this.service = service;
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx,
			Event event) throws Exception
	{
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
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
	}
}
