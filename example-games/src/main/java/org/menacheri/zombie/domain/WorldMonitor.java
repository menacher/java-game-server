package org.menacheri.zombie.domain;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.Task;
import org.menacheri.jetserver.communication.DeliveryGuaranty.DeliveryGuarantyOptions;
import org.menacheri.jetserver.communication.NettyMessageBuffer;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.NetworkEvent;
import org.menacheri.jetserver.protocols.impl.WebSocketProtocol;
import org.menacheri.zombie.game.Messages;

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
				networkEvent = Events.networkEvent(buffer);
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
