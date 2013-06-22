package io.nadron;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.nadron.app.Game;
import io.nadron.app.GameRoom;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.app.impl.SimpleGame;
import io.nadron.app.impl.GameRoomSession.GameRoomSessionBuilder;
import io.nadron.event.Event;
import io.nadron.event.EventDispatcher;
import io.nadron.event.EventHandler;
import io.nadron.event.Events;
import io.nadron.event.impl.EventDispatchers;
import io.nadron.event.impl.JetlangEventDispatcher;
import io.nadron.junitcategories.Performance;
import io.nadron.protocols.Protocol;
import io.nadron.protocols.impl.DummyProtocol;
import io.nadron.util.SessionHandlerLatchCounter;
import io.nadron.util.TestGameRoom;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jetlang.core.Disposable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class JetlangEventDispatcherTest {
	@Test
	public void specificEventReceiptOnSpecificEventHandler()
			throws InterruptedException {
		EventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher(null, null);
		final CountDownLatch latch = new CountDownLatch(1);
		dispatcher.addHandler(new EventHandler() {

			@Override
			public void onEvent(Event event) {
				latch.countDown();
			}

			@Override
			public int getEventType() {
				return Events.SESSION_MESSAGE;
			}
		});

		Event event = Events.event(null, Events.SESSION_MESSAGE);
		dispatcher.fireEvent(event);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void eventReceiptOnANYTypeEventHandler() throws InterruptedException {
		EventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher(null, null);
		final CountDownLatch latch = new CountDownLatch(5);
		dispatcher.addHandler(new EventHandler() {

			@Override
			public void onEvent(Event event) {
				latch.countDown();
			}

			@Override
			public int getEventType() {
				return Events.ANY;
			}
		});

		Event event = Events.event(null, Events.SESSION_MESSAGE);
		dispatcher.fireEvent(event);
		event = Events.event(null, Events.NETWORK_MESSAGE);
		dispatcher.fireEvent(event);
		event = Events.event(null, Events.EXCEPTION);
		dispatcher.fireEvent(event);
		event = Events.event(null, Events.LOG_IN);
		dispatcher.fireEvent(event);
		event = Events.event(null, Events.CONNECT);
		dispatcher.fireEvent(event);

		assertTrue(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void nonReceiptOfWrongEventOnSpecificEventHandler()
			throws InterruptedException {
		EventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher(null, null);
		final CountDownLatch latch = new CountDownLatch(1);
		dispatcher.addHandler(new EventHandler() {

			@Override
			public void onEvent(Event event) {
				latch.countDown();
			}

			@Override
			public int getEventType() {
				return Events.SESSION_MESSAGE;
			}
		});

		Event event = Events.event(null, Events.NETWORK_MESSAGE);
		dispatcher.fireEvent(event);
		assertFalse(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	@Category(Performance.class)
	public void eventPublishingPerformance() throws InterruptedException {
		EventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher(null, null);
		int countOfEvents = 5000000;
		final CountDownLatch latch = new CountDownLatch(countOfEvents);
		dispatcher.addHandler(new EventHandler() {

			@Override
			public void onEvent(Event event) {
				latch.countDown();
			}

			@Override
			public int getEventType() {
				return 0;
			}
		});
		long startTime = System.nanoTime();
		for (int i = 1; i <= countOfEvents; i++) {
			Event event = Events.event(null, Events.SESSION_MESSAGE);
			dispatcher.fireEvent(event);
		}
		long time = System.nanoTime() - startTime;
		latch.await(10, TimeUnit.SECONDS);
		System.out.printf("Took  %.3f seconds to send %d int events",
				time / 1e9, countOfEvents);
	}

	@Test
	public void sessionDisconnectValidation() throws InterruptedException {
		// create necessary setup objects.
		Game game = new SimpleGame(1, "Test");
		Protocol dummyProtocol = new DummyProtocol();
		GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
		sessionBuilder.parentGame(game).gameRoomName("Zombie_ROOM_1")
				.protocol(dummyProtocol);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicLong counter = new AtomicLong(1l);
		Session gameRoomSession = new TestGameRoom(sessionBuilder, counter,
				latch);
		GameRoom gameRoom = (GameRoom) gameRoomSession;
		PlayerSession playerSession = gameRoom.createPlayerSession(null);
		gameRoom.connectSession(playerSession);
		playerSession.addHandler(new SessionHandlerLatchCounter(playerSession,
				counter, latch));

		// start test
		gameRoom.disconnectSession(playerSession);
		JetlangEventDispatcher gameDispatcher = (JetlangEventDispatcher) gameRoomSession
				.getEventDispatcher();
		assertNoListeners(gameDispatcher);
		Event event = Events.event(null, Events.SESSION_MESSAGE);
		playerSession.onEvent(event);
		assertFalse(latch.await(500, TimeUnit.MILLISECONDS));
		
		// Connect to another game room
		sessionBuilder.gameRoomName("Zombie_ROOM_2");
		
		Session gameRoomSession2 = new TestGameRoom(sessionBuilder, counter,
				latch);
		GameRoom gameRoom2 = (GameRoom) gameRoomSession2;
		gameRoom2.connectSession(playerSession);
		playerSession.addHandler(new SessionHandlerLatchCounter(playerSession,
				counter, latch));
		playerSession.onEvent(event);
		assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
	}

	@Test
    public void multiSessionDisconnectValidation() throws InterruptedException {
        // create necessary setup objects.
        Game game = new SimpleGame(1, "Test");
        Protocol dummyProtocol = new DummyProtocol();
        GameRoomSessionBuilder sessionBuilder = new GameRoomSessionBuilder();
        sessionBuilder.parentGame(game).gameRoomName("Zombie_ROOM_1")
                .protocol(dummyProtocol);
        CountDownLatch latch1 = new CountDownLatch(2);
        CountDownLatch latch2 = new CountDownLatch(2);
        AtomicLong counter = new AtomicLong(0l);
        Session gameRoomSession = new TestGameRoom(sessionBuilder, counter,
                latch1);
        GameRoom gameRoom = (GameRoom) gameRoomSession;
        PlayerSession playerSession = gameRoom.createPlayerSession(null);
        PlayerSession playerSession2 = gameRoom.createPlayerSession(null);
        PlayerSession playerSession3 = gameRoom.createPlayerSession(null);
        gameRoom.connectSession(playerSession);
        gameRoom.connectSession(playerSession2);
        gameRoom.connectSession(playerSession3);
        playerSession.addHandler(new SessionHandlerLatchCounter(playerSession,
                counter, latch1));
        playerSession2.addHandler(new SessionHandlerLatchCounter(playerSession2,
                counter, latch2));
        playerSession3.addHandler(new SessionHandlerLatchCounter(playerSession3,
                counter, latch2));
        // start test
        Event event1 = Events.event(null, Events.DISCONNECT);
        playerSession.onEvent(event1);// disconnect session 1.
        assertFalse(latch1.await(1000, TimeUnit.MILLISECONDS));// This is just a wait
        Event message = Events.event(null, Events.SESSION_MESSAGE);
        playerSession.onEvent(message);
        assertFalse(latch1.await(500, TimeUnit.MILLISECONDS));// Ensure that the message is not sent.
        Event event2 = Events.event(null, Events.DISCONNECT);
        Event event3 = Events.event(null, Events.DISCONNECT);
        playerSession2.onEvent(event2);
        playerSession3.onEvent(event3);

        assertTrue(latch2.await(500, TimeUnit.MILLISECONDS));
        // 1 ondisconnect(session1) + 0 onnetwork(session1) + 2 ondisconnect(session2 and 3)
        assertTrue(counter.get() == 3);
    }
	
	private void assertNoListeners(JetlangEventDispatcher dispatcher) {
		Map<Integer, List<EventHandler>> listeners = dispatcher
				.getListenersByEventType();
		Collection<List<EventHandler>> eventHandlers = listeners.values();
		for(List<EventHandler> handlers: eventHandlers){
			assertEquals(0,handlers.size());
		}
		List<EventHandler> anyHandler = dispatcher.getHandlers(Events.ANY);
		assertEquals(0, anyHandler.size());
		Map<EventHandler, Disposable>  disposableMap = dispatcher.getDisposableHandlerMap();
		assertEquals(0,disposableMap.entrySet().size());
	}

}
