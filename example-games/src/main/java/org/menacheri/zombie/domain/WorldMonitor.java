package org.menacheri.zombie.domain;

import org.menacheri.jetserver.app.IGameRoom;
import org.menacheri.jetserver.app.ITask;
import org.menacheri.jetserver.communication.IDeliveryGuaranty;
import org.menacheri.jetserver.communication.NettyMessageBuffer;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.INetworkEvent;
import org.menacheri.zombie.game.Messages;

public class WorldMonitor implements ITask
{
	private World world;
	private IGameRoom room;

	private Object id;
	
	public WorldMonitor(World world, IGameRoom room)
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
			INetworkEvent networkEvent = Events.networkEvent(Messages.apocalypse());
			room.sendBroadcast(networkEvent);
		}
		else
		{
			NettyMessageBuffer buffer = new NettyMessageBuffer();
			buffer.writeInt(world.getAlive());
			INetworkEvent networkEvent = Events.networkEvent(buffer,IDeliveryGuaranty.DeliveryGuaranty.FAST);
			room.sendBroadcast(networkEvent);
		}
		
		world.report();
	}

	@Override
	public void setId(Object id)
	{
		this.id = id;
	}

	public IGameRoom getRoom()
	{
		return room;
	}

	public void setRoom(IGameRoom room)
	{
		this.room = room;
	}
	
}
