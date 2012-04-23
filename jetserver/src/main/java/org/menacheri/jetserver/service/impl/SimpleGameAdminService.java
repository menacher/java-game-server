package org.menacheri.jetserver.service.impl;

import java.util.Collection;

import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.menacheri.jetserver.app.Game;
import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.service.GameAdminService;


public class SimpleGameAdminService implements GameAdminService
{
	private Collection<Game> games;
	
	@Override
	public boolean registerGame(Game game)
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
	public Object loadGameRoom(Game game, long gameRoomId, String gameRoomName)
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
	public Object unLoadGame(Game game)
	{
		if(null != game){
			ChannelGroupFuture groupFuture = (ChannelGroupFuture)game.unload();
			return groupFuture;
		}
		return null;
	}

	@Override
	public void unloadGameRoom(GameRoom gameRoom)
	{
		if(null != gameRoom){
			gameRoom.close();
		}
	}

	@Override
	public Object unloadGameRoom(Game game, long gameRoomId)
	{
		return null;
	}

	@Override
	public Object unloadGameRoom(Game game, String gameRoomId)
	{
		return null;
	}

	@Override
	public synchronized void shutdown()
	{
		if(null != games)
		{
			for (Game game: games)
			{
				unLoadGame(game);
			}
		}
	}
	
	public Collection<Game> getGames()
	{
		return games;
	}

	public void setGames(Collection<Game> games)
	{
		this.games = games;
	}

}
