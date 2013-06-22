package io.nadron.example.zombie.domain;

import io.nadron.app.GameRoom;
import io.nadron.app.Task;
import io.nadron.communication.NettyMessageBuffer;
import io.nadron.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import io.nadron.event.Events;
import io.nadron.event.NetworkEvent;
import io.nadron.example.zombie.game.Messages;
import io.nadron.protocols.impl.WebSocketProtocol;


public class WorldMonitor implements Task
{
	private World world;
	private GameRoom room;

	private Object id;
	
	public WorldMonitor(World world, GameRoom room)
	{
		this.world = world;
		this.room = room;
	}
	
	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	@Override
	public Object getId()
	{
		return id;
	}

	@Override
	public void run()
	{
		if(world.apocalypse())
		{
			// Send it to all players
			System.out.println("Apocalypse is here");
			NetworkEvent networkEvent = Events.networkEvent(Messages.apocalypse());
			room.sendBroadcast(networkEvent);
		}
		else
		{
			NetworkEvent networkEvent = null;
			if(room.getProtocol() instanceof WebSocketProtocol)
			{
				networkEvent = Events.networkEvent(world.getAlive());
			}
			else
			{
				NettyMessageBuffer buffer = new NettyMessageBuffer();
				buffer.writeInt(world.getAlive());
				networkEvent = Events.networkEvent(buffer,DeliveryGuarantyOptions.FAST);
			}
			room.sendBroadcast(networkEvent);
		}
		
		world.report();
	}

	@Override
	public void setId(Object id)
	{
		this.id = id;
	}

	public GameRoom getRoom()
	{
		return room;
	}

	public void setRoom(GameRoom room)
	{
		this.room = room;
	}
	
}
