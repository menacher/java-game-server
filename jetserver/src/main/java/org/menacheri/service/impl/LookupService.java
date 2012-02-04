package org.menacheri.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayer;
import org.menacheri.app.impl.Player;
import org.menacheri.service.ILookupService;
import org.menacheri.util.ICredentials;


/**
 * The lookup service abstracts away the implementation detail on getting the
 * game objects from the reference key provided by the client. This lookup is
 * now done from a hashmap but can be done from database or any other manner.
 * 
 * @author Abraham Menacherry
 * 
 */
public class LookupService implements ILookupService
{
	private Map<String, IGameRoom> refKeyGameRoomMap;

	public LookupService()
	{
		refKeyGameRoomMap = new HashMap<String, IGameRoom>();
	}

	public LookupService(Map<String, IGameRoom> refKeyGameRoomMap)
	{
		super();
		this.refKeyGameRoomMap = refKeyGameRoomMap;
	}

	@Override
	public IGame gameLookup(Object gameContextKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGameRoom gameRoomLookup(Object gameContextKey)
	{
		return refKeyGameRoomMap.get((String) gameContextKey);
	}

	@Override
	public IPlayer playerLookup(ICredentials loginDetail)
	{
		return new Player();
	}

	public Map<String, IGameRoom> getRefKeyGameRoomMap()
	{
		return refKeyGameRoomMap;
	}

	public void setRefKeyGameRoomMap(Map<String, IGameRoom> refKeyGameRoomMap)
	{
		this.refKeyGameRoomMap = refKeyGameRoomMap;
	}

}
