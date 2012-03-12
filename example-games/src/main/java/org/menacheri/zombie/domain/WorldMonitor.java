package org.menacheri.zombie.domain;

import org.menacheri.app.IGameRoom;
import org.menacheri.app.ITask;
import org.menacheri.communication.IDeliveryGuaranty.DeliveryGuaranty;
import org.menacheri.communication.NettyMessageBuffer;
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
			room.sendBroadcast(Messages.apocalypse());
		}
		else
		{
			NettyMessageBuffer buffer = new NettyMessageBuffer();
			buffer.writeInt(world.getAlive());
			room.sendBroadcast(buffer,DeliveryGuaranty.FAST);
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
