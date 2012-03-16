package org.menacheri.zombieclient;

import org.jboss.netty.buffer.ChannelBuffer;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.communication.IDeliveryGuaranty;
import org.menacheri.jetclient.communication.IMessageBuffer;
import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.event.Events;
import org.menacheri.jetclient.event.IEvent;
import org.menacheri.zombie.domain.IAM;
import org.menacheri.zombie.domain.ZombieCommands;

public class GamePlay implements Runnable
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
			operation = ZombieCommands.SHOT_GUN.getCommand();
			break;
		case ZOMBIE:
			operation = ZombieCommands.EAT_BRAINS.getCommand();
			break;
		}
		
		IMessageBuffer<ChannelBuffer> messageBuffer = new NettyMessageBuffer();
		messageBuffer.writeInt(type);
		messageBuffer.writeInt(operation);
		IEvent event = Events.networkEvent(messageBuffer,IDeliveryGuaranty.DeliveryGuaranty.FAST);
		session.onEvent(event);
	}
}
