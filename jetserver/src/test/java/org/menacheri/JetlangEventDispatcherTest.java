package org.menacheri;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.event.IEventDispatcher;
import org.menacheri.event.IEventHandler;
import org.menacheri.event.impl.EventDispatchers;

public class JetlangEventDispatcherTest
{
	@Test
	public void specificEventReceiptOnSpecificEventHandler()
			throws InterruptedException
	{
		IEventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher();
		final CountDownLatch latch = new CountDownLatch(1);
		dispatcher.addHandler(new IEventHandler()
		{

			@Override
			public void onEvent(IEvent event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
				return Events.SESSION_MESSAGE;
			}
		});

		IEvent event = Events.event(null, Events.SESSION_MESSAGE);
		dispatcher.fireEvent(event);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void eventReceiptOnANYTypeEventHandler() throws InterruptedException
	{
		IEventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher();
		final CountDownLatch latch = new CountDownLatch(5);
		dispatcher.addHandler(new IEventHandler()
		{

			@Override
			public void onEvent(IEvent event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
				return Events.ANY;
			}
		});

		IEvent event = Events.event(null, Events.SESSION_MESSAGE);
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
			throws InterruptedException
	{
		IEventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher();
		final CountDownLatch latch = new CountDownLatch(1);
		dispatcher.addHandler(new IEventHandler()
		{

			@Override
			public void onEvent(IEvent event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
				return Events.SESSION_MESSAGE;
			}
		});

		IEvent event = Events.event(null, Events.NETWORK_MESSAGE);
		dispatcher.fireEvent(event);
		assertFalse(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void eventPublishingPerformance() throws InterruptedException
	{
		IEventDispatcher dispatcher = EventDispatchers
				.newJetlangEventDispatcher();
		int countOfEvents = 5000000;
		final CountDownLatch latch = new CountDownLatch(countOfEvents);
		dispatcher.addHandler(new IEventHandler()
		{

			@Override
			public void onEvent(IEvent event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
				return 0;
			}
		});
		long startTime = System.nanoTime();
		for (int i = 1; i <= countOfEvents; i++)
		{
			IEvent event = Events.event(null, Events.SESSION_MESSAGE);
			dispatcher.fireEvent(event);
		}
		long time = System.nanoTime() - startTime;
		latch.await(10, TimeUnit.SECONDS);
		System.out.printf("Took  %.3f seconds to send %d int events",
				time / 1e9, countOfEvents);
	}

}
