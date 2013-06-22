package io.nadron.example.zombieclient;

import io.nadron.client.app.Session;
import io.nadron.client.communication.MessageBuffer;
import io.nadron.client.communication.NettyMessageBuffer;
import io.nadron.client.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.client.event.Event;
import io.nadron.client.event.Events;
import io.nadron.example.zombie.domain.IAM;
import io.nadron.example.zombie.domain.ZombieCommands;
import io.netty.buffer.ByteBuf;


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
		
		for(int i = 1; i<10; i++){
			MessageBuffer<ByteBuf> messageBuffer = new NettyMessageBuffer();
			messageBuffer.writeInt(type);
			messageBuffer.writeInt(operation);
			Event event = Events.networkEvent(messageBuffer, DeliveryGuarantyOptions.FAST);
			session.onEvent(event);
		}
	}
}
