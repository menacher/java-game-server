package io.nadron.example.zombie.domain;


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
