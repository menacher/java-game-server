package org.menacheri.jetserver.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.event.NetworkEvent;
import org.menacheri.jetserver.event.impl.DefaultSessionEventHandler;

public class SessionHandlerLatchCounter extends DefaultSessionEventHandler {

	private final AtomicLong counter;
	private final CountDownLatch latch;

	public SessionHandlerLatchCounter(Session session, AtomicLong counter,
			CountDownLatch latch) {
		super(session);
		this.counter = counter;
		this.latch = latch;
	}

	@Override
	public void onNetworkMessage(NetworkEvent event) {
		counter.incrementAndGet();
		latch.countDown();
	}

	public AtomicLong getCounter() {
		return counter;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
