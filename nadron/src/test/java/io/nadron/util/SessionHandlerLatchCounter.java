package io.nadron.util;

import io.nadron.app.Session;
import io.nadron.event.Event;
import io.nadron.event.NetworkEvent;
import io.nadron.event.impl.DefaultSessionEventHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;


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
        System.out.println("invoked onNetworkMessage");
    }

    @Override
    protected void onDisconnect(Event event)
    {
        counter.incrementAndGet();
        latch.countDown();
        System.out.println("invoked onDisconnect");
        super.onDisconnect(event);
    }
    
	public AtomicLong getCounter() {
		return counter;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
