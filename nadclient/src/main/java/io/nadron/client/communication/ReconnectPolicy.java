package io.nadron.client.communication;

import io.nadron.client.app.Session;
import io.nadron.client.event.Event;
import io.nadron.client.event.EventHandler;
import io.nadron.client.event.Events;
import io.nadron.client.util.LoginHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Implementations of this policy determine the logic to be applied for
 * re-connecting sessions on exception/disconnect situations.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface ReconnectPolicy
{
	void applyPolicy(Session session);
	ReconnectPolicy NO_RECONNECT = new NoReconnect();
	
	/**
	 * This reconnect policy class will try to reconnect to server for n times
	 * before giving up and closing the session. The number of times, to try,
	 * the delay between reconnect attempts are configurable.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public static class ReconnectNTimes implements ReconnectPolicy
	{
		protected final int times;
		protected final int delay;
		protected final LoginHelper loginHelper;

		/**
		 * Constructor used to initialize the number of times reconnect should
		 * be tried, along with the delay between retries.
		 * 
		 * @param times
		 *            The number of times session reconnect should be attempted.
		 * @param delay
		 *            The delay between reconnect attempts in milliseconds. For
		 *            e.g 5000 = 5 seconds delay.
		 * @param loginHelper
		 *            Used to pass configuration information to setup the
		 *            connection to server once again.
		 */
		public ReconnectNTimes(int times, int delay, LoginHelper loginHelper)
		{
			this.times = times;
			this.delay = delay;
			this.loginHelper = loginHelper;
		}

		@Override
		public void applyPolicy(final Session session)
		{
			// Listen for log in success event to be received on the session.
			final CountDownLatch loginSuccessLatch = new CountDownLatch(1);
			final EventHandler loginSuccess = new EventHandler()
			{
				@Override
				public void onEvent(Event event)
				{
					// remove after use
					session.removeHandler(this);
					loginSuccessLatch.countDown();
				}

				@Override
				public int getEventType()
				{
					return Events.LOG_IN_SUCCESS;
				}
			};
			session.addHandler(loginSuccess);

			// try to reconnect for n times.
			int tries = 1;
			for (; tries <= times; tries++)
			{
				session.reconnect(loginHelper);
				try
				{
					if (loginSuccessLatch.await(delay, TimeUnit.MILLISECONDS))
					{
						break;
					}
					else
					{
						System.err.println("Reconnect try " + tries + " did not succeed");
					}
				}
				catch (InterruptedException e)
				{
					throw new RuntimeException(e);
				}
			}

			// if times == tries, then all the reconnect attempts were a
			// failure, close session.
			if (tries > times)
			{
				loginSuccessLatch.countDown();
				System.err.println("Reconnect attempted " + tries + " times did not succeed, going to close session");
				session.close();
			}
		}

	}

	class NoReconnect implements ReconnectPolicy
	{
		@Override
		public void applyPolicy(Session session)
		{
			session.close();
		}
	}
}
