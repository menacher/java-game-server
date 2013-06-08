package org.menacheri.zombieclient;

import io.netty.buffer.ByteBuf;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import org.menacheri.jetclient.communication.MessageBuffer;
import org.menacheri.jetclient.communication.NettyMessageBuffer;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.Events;
import org.menacheri.zombie.domain.IAM;
import org.menacheri.zombie.domain.ZombieCommands;

public class GamePlay implements Runnable
{
	private final IAM iam;
	private final Session session;
	
	public GamePlay(IAM iam, Session session)
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
		
		for(int i = 1; i<2; i++){
			MessageBuffer<ByteBuf> messageBuffer = new NettyMessageBuffer();
			messageBuffer.writeInt(type);
			messageBuffer.writeInt(operation);
			Event event = Events.networkEvent(messageBuffer, DeliveryGuarantyOptions.FAST);
			session.onEvent(event);
		}
	}
}
