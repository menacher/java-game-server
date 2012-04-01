package org.menacheri.app.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.menacheri.app.IGame;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.app.ISession;
import org.menacheri.app.impl.GameRoomSession.GameRoomSessionBuilder;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.INetworkEvent;
import org.menacheri.event.impl.AbstractSessionEventHandler;
import org.menacheri.protocols.IProtocol;
import org.menacheri.protocols.impl.DummyProtocol;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

public class PlayerSessionTest
{
	private static final IProtocol DUMMY_PROTOCOL = new DummyProtocol();
	private static final AtomicLong COUNTER = new AtomicLong(0l);
	private static final int NUM_OF_GAME_ROOMS = 1000;
	private static final int SESSIONS_PER_GAME_ROOM = 25;
	private static final int EVENTS_PER_SESSION = 10;
	private static final int LATCH_COUNT = ((NUM_OF_GAME_ROOMS * SESSIONS_PER_GAME_ROOM) * (EVENTS_PER_SESSION * SESSIONS_PER_GAME_ROOM))
			+ (EVENTS_PER_SESSION * SESSIONS_PER_GAME_ROOM * NUM_OF_GAME_ROOMS);
	private static final CountDownLatch LATCH = new CountDownLatch(LATCH_COUNT);
	private IGame game;
	private List<IGameRoom> gameRoomList;
	private List<ISession> sessionList;

	@Before
	public void setUp()
	{
		game = new Game(1, "Test");
		gameRoomList = new ArrayList<IGameRoom>(NUM_OF_GAME_ROOMS);
		sessionList = new ArrayList<ISession>(NUM_OF_GAME_ROOMS
				* SESSIONS_PER_GAME_ROOM);
		for (int i = 1; i <= NUM_OF_GAME_ROOMS; i++)
		{
			GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
			sessionBuilder.parentGame(game).gameRoomName("Zombie_ROOM_" + i)
					.protocol(DUMMY_PROTOCOL);
			ISession gameRoomSession = new TestGameRoom(sessionBuilder);
			gameRoomSession.addHandler(new GameRoomSessionHandler(
					gameRoomSession));
			gameRoomList.add((IGameRoom) gameRoomSession);
		}
		for (IGameRoom gameRoom : gameRoomList)
		{
			for (int j = 1; j <= SESSIONS_PER_GAME_ROOM; j++)
			{
				IPlayerSession playerSession = gameRoom.createPlayerSession();
				gameRoom.connectSession(playerSession);
				playerSession.addHandler(new SessionHandler(playerSession));
				sessionList.add(playerSession);
			}
		}
	}

	@Test
	public void eventHandlingPerformance() throws InterruptedException
	{
		long start = System.nanoTime();
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				for (ISession session : sessionList)
				{
					for (int i = 1; i <= EVENTS_PER_SESSION; i++)
					{
						IEvent event = Events.event(null,
								Events.SESSION_MESSAGE);
						session.onEvent(event);
					}
				}
			}
		});
		t.start();

		assertTrue(LATCH.await(20, TimeUnit.SECONDS));
		long time = System.nanoTime() - start;
		System.out.printf(
				"Took  %.3f seconds to pass %d messages between sessions\n",
				time / 1e9, COUNTER.get());
		System.out.printf("Message passing rate was %.3f million messages/sec",
				COUNTER.get() / ((time / 1e9) * 1000000));
	}

	private static class TestGameRoom extends GameRoomSession
	{
		protected TestGameRoom(GameRoomSessionBuilder gameRoomSessionBuilder)
		{
			super(gameRoomSessionBuilder);
		}

		@Override
		public void onLogin(IPlayerSession playerSession)
		{
			SessionHandler handler = new SessionHandler(playerSession);
			playerSession.addHandler(handler);
		}
	}

	private static class GameRoomSessionHandler extends
			AbstractSessionEventHandler
	{
		public GameRoomSessionHandler(ISession session)
		{
			super(session);
		}

		@Override
		public void onNetworkMessage(INetworkEvent event)
		{
			COUNTER.incrementAndGet();
			LATCH.countDown();
		}
	}

	private static class SessionHandler extends AbstractSessionEventHandler
	{
		public SessionHandler(ISession session)
		{
			super(session);
		}

		@Override
		public void onNetworkMessage(INetworkEvent event)
		{
			COUNTER.incrementAndGet();
			LATCH.countDown();
		}
	}
}
