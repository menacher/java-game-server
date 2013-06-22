package io.nadron.example.zombie.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class World
{
	private static final Logger LOG = LoggerFactory.getLogger(World.class);
	private volatile int alive;
	private volatile int undead;

	public boolean apocalypse() {
		if(alive <= 0)
		{
			return true;
		}
		return false;
	}
	
	public void report() {
		if(alive > 0) {
			LOG.trace("alive= {} undead= {}",alive, undead);
		}
	}

	public int getAlive()
	{
		return alive;
	}

	public void setAlive(int alive)
	{
		this.alive = alive;
	}

	public int getUndead()
	{
		return undead;
	}

	public void setUndead(int undead)
	{
		this.undead = undead;
	}

	public void shotgun() 
	{
		int newUndead = undead - 1;
		LOG.trace("Defender update, undead = " + undead + " new undead: " + newUndead);
		undead = newUndead;
	}
	
	public void eatBrains() 
	{
		LOG.trace("In eatBrains Alive: {} Undead: {}",alive,undead);
		alive--;
		undead += 2	;
		LOG.trace("New Alive: {} Undead: {}",alive,undead);
	}
	
}
