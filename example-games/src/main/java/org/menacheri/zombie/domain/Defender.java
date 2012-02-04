package org.menacheri.zombie.domain;

import org.menacheri.aspect.AppManaged;

@AppManaged
public class Defender
{
	private World world;

	public void shotgun() 
	{
		world.shotgun();
	}
	
	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}
	
}
