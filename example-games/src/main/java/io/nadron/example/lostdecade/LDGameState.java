package io.nadron.example.lostdecade;

import io.nadron.app.GameRoom;

import java.io.Serializable;
import java.util.Set;



/**
 * The state of a game room is held in this object. Multiple remote client
 * connections to a {@link GameRoom} will share this state.
 * 
 * @author Abraham Menacherry
 * 
 */
public class LDGameState implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<Entity> entities;
	private Entity monster;
	private Entity hero;
	private boolean reset;
	
	public LDGameState()
	{
		
	}
	
	public LDGameState(Set<Entity> entities, Entity monster, Entity hero)
	{
		super();
		this.entities = entities;
		this.monster = monster;
		this.hero = hero;
	}
	
	public Entity getMonster()
	{
		return monster;
	}

	public void setMonster(Entity monster)
	{
		this.monster = monster;
	}

	public void addEntitiy(Entity hero)
	{
		// only the id will match, but other values maybe different.
		entities.remove(hero);
		entities.add(hero);
	}
	
	public Entity getHero()
	{
		return hero;
	}

	public void setHero(Entity hero)
	{
		this.hero = hero;
	}

	public boolean isReset()
	{
		return reset;
	}

	public void setReset(boolean reset)
	{
		this.reset = reset;
	}

	public Set<Entity> getEntities()
	{
		return entities;
	}
	
	public void setEntities(Set<Entity> entities)
	{
		this.entities = entities;
	}

}
 