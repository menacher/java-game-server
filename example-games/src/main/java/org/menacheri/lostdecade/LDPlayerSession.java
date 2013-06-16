package org.menacheri.lostdecade;

import org.menacheri.jetserver.app.impl.DefaultPlayerSession;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.impl.DefaultEventContext;
import org.menacheri.jetserver.event.impl.DefaultSessionEventHandler;

public class LDPlayerSession extends DefaultPlayerSession
{
	private Entity entity;
	DefaultEventContext context;
	
	protected LDPlayerSession(PlayerSessionBuilder playerSessionBuilder)
	{
		super(playerSessionBuilder);
		context = new DefaultEventContext(this, null);
		// Just pass on incoming session messages to the game room.
		this.addHandler(new DefaultSessionEventHandler(this)
		{
			@Override
			protected void onDataIn(Event event)
			{
				if (null != event.getSource())
				{
					// Pass the player session in the event context so that the
					// game room knows which player session send the message.
					event.setEventContext(context);
					LDPlayerSession.this.getGameRoom().send(event);
				}
			}
			
		});
	}

	public Entity getEntity()
	{
		return entity;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

}
