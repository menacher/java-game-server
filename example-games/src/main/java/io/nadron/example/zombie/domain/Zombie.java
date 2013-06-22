package io.nadron.example.zombie.domain;


public class Zombie
{
	private World world;
	
	public void eatBrains() 
	{
		world.eatBrains();
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
