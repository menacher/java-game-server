package org.menacheri.zombieclient;

import java.util.TimerTask;

import org.jboss.netty.buffer.ChannelBuffer;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.communication.IMessageBuffer;
import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.zombie.domain.IAM;
import org.menacheri.zombie.domain.ZombieCommands;

public class GamePlay extends TimerTask
{
	private final IAM iam;
	private final ISession session;
	
	public GamePlay(IAM iam, ISession session)
	{
		this.iam = iam;
		this.session = session;
	}
	
	@Override
	public void run()
	{
		int type = IAM.getInt(iam);
		int operation = 0;
		switch(iam)
		{
		case DEFENDER:
			operation = ZombieCommands.getInt(ZombieCommands.SHOT_GUN);
			break;
		case ZOMBIE:
			operation = ZombieCommands.getInt(ZombieCommands.EAT_BRAINS);
			break;
		}
		
		IMessageBuffer<ChannelBuffer> messageBuffer = new NettyMessageBuffer();
		messageBuffer.writeInt(type);
		messageBuffer.writeInt(operation);
		IEvent event = Events.clientOutUDP(messageBuffer);
		session.onEvent(event);
	}
}
