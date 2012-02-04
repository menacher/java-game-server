package org.menacheri.service.impl;

import java.util.Collection;

import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.service.IGameAdminService;


public class GameAdminService implements IGameAdminService
{
	private Collection<IGame> games;
	
	@Override
	public boolean registerGame(IGame game)
	{
		return games.add(game);
	}
	
	@Override
	public Object loadGame(long gameId, String gameName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object loadGameRoom(IGame game, long gameRoomId, String gameRoomName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object unLoadGame(long gameId, String gameName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object unLoadGame(IGame game)
	{
		if(null != game){
			ChannelGroupFuture groupFuture = (ChannelGroupFuture)game.unload();
			return groupFuture;
		}
		return null;
	}

	@Override
	public void unloadGameRoom(IGameRoom gameRoom)
	{
		if(null != gameRoom){
			gameRoom.close();
		}
	}

	@Override
	public Object unloadGameRoom(IGame game, long gameRoomId)
	{
		return null;
	}

	@Override
	public Object unloadGameRoom(IGame game, String gameRoomId)
	{
		return null;
	}

	@Override
	public synchronized void shutdown()
	{
		if(null != games)
		{
			for (IGame game: games)
			{
				unLoadGame(game);
			}
		}
	}
	
	public Collection<IGame> getGames()
	{
		return games;
	}

	public void setGames(Collection<IGame> games)
	{
		this.games = games;
	}

}
