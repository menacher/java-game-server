package org.menacheri.zombie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;

import java.net.SocketAddress;
import java.util.TimerTask;

import org.menacheri.jetserver.event.Events;
import org.menacheri.zombie.domain.IAM;
import org.menacheri.zombie.domain.ZombieCommands;


public class WriteByte extends TimerTask
{
	
	Channel channel;
	IAM iam;
	SocketAddress remoteAddress;
	
	public WriteByte(Channel channel,SocketAddress remoteAddress,IAM iam)
	{
		this.channel = channel;
		this.remoteAddress = remoteAddress;
		this.iam = iam;
	}
	
	@Override
	public void run()
	{
		int type = IAM.getInt(iam);
		int operation = 0;
		switch(iam)
		{
		case DEFENDER:
			operation = ZombieCommands.SHOT_GUN.getCommand();
			break;
		case ZOMBIE:
			operation = ZombieCommands.EAT_BRAINS.getCommand();
			break;
		}
		ByteBuf buf = null;
		if(null == remoteAddress){
			//TCP
			for(int i =0; i < 10;i++){
				buf = Unpooled.buffer(1 + 8);
				buf.writeByte(Events.SESSION_MESSAGE);
				buf.writeInt(type);
				buf.writeInt(operation);
				channel.write(buf);
			}
		}
		else
		{
			//UDP
			DatagramChannel udpChannel = (DatagramChannel)channel;
			buf = Unpooled.buffer(1 + 8);
			buf.writeByte(Events.SESSION_MESSAGE);
			buf.writeInt(type);
			buf.writeInt(operation);
			for(int i =0; i < 10;i++){
				udpChannel.write(buf);
			}
		}
		
	} 
}
