package io.nadron.client.event.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.nadron.client.event.Event;
import io.nadron.client.event.EventDispatcher;
import io.nadron.client.event.EventHandler;
import io.nadron.client.event.Events;
import io.nadron.client.event.impl.DefaultEventDispatcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class EventDispatcherTest
{
	@Test
	public void specificEventReceiptOnSpecificEventHandler()
			throws InterruptedException
	{
		EventDispatcher dispatcher = new DefaultEventDispatcher();
		final CountDownLatch latch = new CountDownLatch(1);
		dispatcher.addHandler(new EventHandler()
		{

			@Override
			public void onEvent(Event event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
				return Events.SESSION_MESSAGE;
			}
		});

		Event event = Events.event(null, Events.SESSION_MESSAGE);
		dispatcher.fireEvent(event);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void eventReceiptOnANYTypeEventHandler() throws InterruptedException
	{
		EventDispatcher dispatcher = new DefaultEventDispatcher();
		final CountDownLatch latch = new CountDownLatch(5);
		dispatcher.addHandler(new EventHandler()
		{

			@Override
			public void onEvent(Event event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
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
			throws InterruptedException
	{
		EventDispatcher dispatcher = new DefaultEventDispatcher();
		final CountDownLatch latch = new CountDownLatch(1);
		dispatcher.addHandler(new EventHandler()
		{

			@Override
			public void onEvent(Event event)
			{
				latch.countDown();
			}

			@Override
			public int getEventType()
			{
				return Events.SESSION_MESSAGE;
			}
		});

		Event event = Events.event(null, Events.NETWORK_MESSAGE);
		dispatcher.fireEvent(event);
		assertFalse(latch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void eventPublishingPerformance() throws InterruptedException
	{
		EventDispatcher dispatcher = new DefaultEventDispatcher();
		int countOfEvents = 5000000;
		final CountDownLatch latch = new CountDownLatch(countOfEvents);
		dispatcher.addHandler(new EventHandler()
		{

			@Override
			public void onEvent(Event event)
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
			Event event = Events.event(null, Events.SESSION_MESSAGE);
			dispatcher.fireEvent(event);
		}
		long time = System.nanoTime() - startTime;
		latch.await(10, TimeUnit.SECONDS);
		System.out.printf("Took  %.3f seconds to send %d int events",
				time / 1e9, countOfEvents);
	}
}
