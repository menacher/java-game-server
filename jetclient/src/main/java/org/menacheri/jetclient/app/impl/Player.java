package org.menacheri.jetclient.app.impl;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.jetclient.app.IPlayer;
import org.menacheri.jetclient.app.IPlayerSession;

/**
 * A class used to represent a human player who connects to jetserver.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Player implements IPlayer
{
	/**
	 * This variable could be used as a database key.
	 */
	private Object id;

	/**
	 * The name of the gamer.
	 */
	private String name;
	/**
	 * Email id of the gamer.
	 */
	private String emailId;

	/**
	 * One player can be connected to multiple games at the same time. Each
	 * session in this set defines a connection to a game. TODO, each player
	 * should not have multiple sessions to the same game.
	 */
	private Set<IPlayerSession> playerSessions;

	public Player()
	{
		playerSessions = new HashSet<IPlayerSession>();
	}

	public Player(Object id, String name, String emailId)
	{
		super();
		this.id = id;
		this.name = name;
		this.emailId = emailId;
		playerSessions = new HashSet<IPlayerSession>();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Player other = (Player) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public Object getId()
	{
		return id;
	}

	@Override
	public void setId(Object id)
	{
		this.id = id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getEmailId()
	{
		return emailId;
	}

	@Override
	public void setEmailId(String emailId)
	{
		this.emailId = emailId;
	}

	@Override
	public synchronized boolean addSession(IPlayerSession session)
	{
		return playerSessions.add(session);
	}

	@Override
	public synchronized boolean removeSession(IPlayerSession session)
	{
		boolean remove = playerSessions.remove(session);
		if (playerSessions.size() == 0)
		{
			logout(session);
		}
		return remove;
	}

	@Override
	public synchronized void logout(IPlayerSession session)
	{
		session.close();
		if (null != playerSessions)
		{
			playerSessions.remove(session);
		}
	}

	public Set<IPlayerSession> getPlayerSessions()
	{
		return playerSessions;
	}

	public void setPlayerSessions(Set<IPlayerSession> playerSessions)
	{
		this.playerSessions = playerSessions;
	}

}
