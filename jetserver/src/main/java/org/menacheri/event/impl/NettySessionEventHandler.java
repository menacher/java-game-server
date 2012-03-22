package org.menacheri.event.impl;

import org.menacheri.communication.NettyMessageBuffer;
import org.menacheri.communication.IMessageSender.IReliable;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;


public class NettySessionEventHandler extends AbstractSessionEventHandler
{
	@Override
	public void onConnect(IEvent event)
	{
		super.onConnect(event);

		// Create and send a start event to client.
		// TODO this code is NOT going to work if there are some other encoders
		// defined in the pipeline. Design to fix this would require event
		// future implementation
		Object source = event.getSource();
		if (source instanceof IReliable)
		{
			NettyMessageBuffer messageBuffer = new NettyMessageBuffer();
			messageBuffer.writeByte(Events.START);
			IEvent startEvent = Events.event(messageBuffer, Events.START);
			onStart(startEvent);
		}
	}


}
