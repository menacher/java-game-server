package org.menacheri.jetserver.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.impl.GameRoomSession;


public class TestGameRoom extends GameRoomSession {

	private final AtomicLong counter;
	private final CountDownLatch latch;

	public TestGameRoom(GameRoomSessionBuilder gameRoomSessionBuilder,
			AtomicLong counter, CountDownLatch latch) {
		super(gameRoomSessionBuilder);
		this.counter = counter;
		this.latch = latch;
	}

	@Override
	public void onLogin(PlayerSession playerSession) {
		SessionHandlerLatchCounter handler = new SessionHandlerLatchCounter(
				playerSession, counter, latch);
		playerSession.addHandler(handler);
	}

}
