package io.nadron.app.impl;

import static org.junit.Assert.assertTrue;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.GameRoomSession;
import io.nadron.app.impl.SimpleGame;
import io.nadron.app.impl.GameRoomSession.GameRoomSessionBuilder;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.event.NetworkEvent;
import io.nadron.event.impl.DefaultSessionEventHandler;
import io.nadron.event.impl.ExecutorEventDispatcher;
import io.nadron.junitcategories.Performance;
import io.nadron.protocols.Protocol;
import io.nadron.protocols.impl.DummyProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

public class PlayerSessionWithEventDispatcherTest
{
	private static final Protocol DUMMY_PROTOCOL = new DummyProtocol();
	private static final AtomicLong COUNTER = new AtomicLong(0l);
	private static final int NUM_OF_GAME_ROOMS = 1000;
	private static final int SESSIONS_PER_GAME_ROOM = 50;
	private static final int EVENTS_PER_SESSION = 1;
	private static final int LATCH_COUNT = ((NUM_OF_GAME_ROOMS * SESSIONS_PER_GAME_ROOM) * (EVENTS_PER_SESSION * SESSIONS_PER_GAME_ROOM))
			+ (EVENTS_PER_SESSION * SESSIONS_PER_GAME_ROOM * NUM_OF_GAME_ROOMS);
	private static final CountDownLatch LATCH = new CountDownLatch(LATCH_COUNT);
	private Game game;
	private List<GameRoom> gameRoomList;
	private List<Session> sessionList;

	@Before
	public void setUp()
	{
		game = new SimpleGame(1, "Test");
		gameRoomList = new ArrayList<GameRoom>(NUM_OF_GAME_ROOMS);
		sessionList = new ArrayList<Session>(NUM_OF_GAME_ROOMS
				* SESSIONS_PER_GAME_ROOM);
		for (int i = 1; i <= NUM_OF_GAME_ROOMS; i++)
		{
			GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
			sessionBuilder.parentGame(game).gameRoomName("Zombie_ROOM_" + i)
					.protocol(DUMMY_PROTOCOL).eventDispatcher(new ExecutorEventDispatcher());
			Session gameRoomSession = new TestGameRoom(sessionBuilder);
			gameRoomSession.addHandler(new GameRoomSessionHandler(
					gameRoomSession));
			gameRoomList.add((GameRoom) gameRoomSession);
		}
		for (GameRoom gameRoom : gameRoomList)
		{
			for (int j = 1; j <= SESSIONS_PER_GAME_ROOM; j++)
			{
				PlayerSession playerSession = gameRoom.createPlayerSession(null);
				gameRoom.connectSession(playerSession);
				playerSession.addHandler(new SessionHandler(playerSession));
				sessionList.add(playerSession);
			}
		}
	}

	@Test
	@Category(Performance.class)
	public void eventHandlingPerformance() throws InterruptedException
	{
		long start = System.nanoTime();
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				for (Session session : sessionList)
				{
					for (int i = 1; i <= EVENTS_PER_SESSION; i++)
					{
						Event event = Events.event(null,
								Events.SESSION_MESSAGE);
						session.onEvent(event);
					}
				}
			}
		},"Event-Injector");
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
		public void onLogin(PlayerSession playerSession)
		{
			SessionHandler handler = new SessionHandler(playerSession);
			playerSession.addHandler(handler);
		}
	}

	private static class GameRoomSessionHandler extends
			DefaultSessionEventHandler
	{
		public GameRoomSessionHandler(Session session)
		{
			super(session);
		}

		@Override
		public void onNetworkMessage(NetworkEvent event)
		{
			COUNTER.incrementAndGet();
			LATCH.countDown();
		}
	}

	private static class SessionHandler extends DefaultSessionEventHandler
	{
		public SessionHandler(Session session)
		{
			super(session);
		}

		@Override
		public void onNetworkMessage(NetworkEvent event)
		{
			COUNTER.incrementAndGet();
			LATCH.countDown();
		}
	}
}
