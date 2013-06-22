package io.nadron.service.impl;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.service.GameAdminService;
import io.netty.channel.group.ChannelGroupFuture;

import java.util.Collection;



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
