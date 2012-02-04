package org.menacheri.app.impl;

import org.menacheri.app.IGame;
import org.menacheri.app.IGameCommandInterpreter;

/**
 * Domain object representing a game. This is a convenience implementation of
 * the IGame interface so that simple games need not implement their own.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Game implements IGame
{
	/**
	 * This variable could be used as a database key.
	 */
	private long id;
	/**
	 * A string version of id is also provided.
	 */
	private String uniqueId;
	
	/**
	 * The name of the game.
	 */
	private String gameName;
	/**
	 * Each game has its own specific commands. This instance will be used to
	 * transform those commands(most probably in the form of bytes) to actual
	 * java method calls.
	 */
	private IGameCommandInterpreter gameCommandInterpreter;
	
	public Game()
	{

	}

	public Game(long id, String uniqueId, String gameName,
			IGameCommandInterpreter gameCommandInterpreter)
	{
		super();
		this.id = id;
		this.uniqueId = uniqueId;
		this.gameName = gameName;
		this.gameCommandInterpreter = gameCommandInterpreter;
	}

	/**
	 * Meant as a database access key.
	 * 
	 * @return The unique identifier of this Game.
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Meant as a database access key.
	 * 
	 * @param id
	 *            Set the unique identifier for this game.
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.menacheri.app.IGame#getGameName()
	 */
	public String getGameName()
	{
		return gameName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.menacheri.app.IGame#setGameName(java.lang.String)
	 */
	public void setGameName(String gameName)
	{
		this.gameName = gameName;
	}

	@Override
	public IGameCommandInterpreter getGameCommandInterpreter()
	{
		return gameCommandInterpreter;
	}
	
	@Override
	public void setGameCommandInterpreter(IGameCommandInterpreter interpreter)
	{
		this.gameCommandInterpreter = interpreter;
	}
	
	@Override
	public synchronized Object unload()
	{
		return null;
	}

	@Override
	public String getUniqueId()
	{
		return uniqueId;
	}

	@Override
	public void setUniqueId(String uniqueId)
	{
		this.uniqueId = uniqueId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((uniqueId == null) ? 0 : uniqueId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (id != other.id)
			return false;
		if (uniqueId == null)
		{
			if (other.uniqueId != null)
				return false;
		}
		else if (!uniqueId.equals(other.uniqueId))
			return false;
		return true;
	}

}
