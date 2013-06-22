package io.nadron.concurrent;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;

/**
 * This class is used by the AppManaged aspect to transform a normal method call
 * into an asynchronous one. This class thus behaves like a Clojure or GPars
 * Agent.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Agent
{
	/**
	 * The dedicated in-vm memory channel for this agent. Calls to this agent
	 * get queued up on this channel for execution by the thread.
	 */
	final Channel<Runnable> channel;
	/**
	 * The fiber associated with this agent. Used to subscribe to the channel
	 * and pass the incoming code to the callback for execution.
	 */
	final Fiber fiber;
	/**
	 * The incoming code is executed by this call back synchronously. Since the
	 * send itself is asynchronous it acts like an event handler.
	 */
	final Callback<Runnable> callback = new Callback<Runnable>()
	{
		@Override
		public void onMessage(Runnable message)
		{
			message.run();
		}
	};

	public Agent()
	{
		this.channel = new MemoryChannel<Runnable>();
		this.fiber = Fibers.pooledFiber();
		channel.subscribe(fiber, callback);
	}

	public void send(Runnable code)
	{
		channel.publish(code);
	}
}
