package io.nadron.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * An anemic implementation of Gpars <a
 * href="http://gpars.codehaus.org/">GPars</a> dataflow variable. This class
 * will block the getVal call using a simple count down latch, also it does not
 * really prevent resetting the value using bind.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DataFlowVariable
{
	final CountDownLatch latch;
	Object val = null;

	public DataFlowVariable()
	{
		this.latch = new CountDownLatch(1);
	}

	public DataFlowVariable(CountDownLatch latch)
	{
		this.latch = latch;
	}

	/**
	 * The method will bind the incoming value to the value in the class and
	 * then do a countDown on the latch.
	 * 
	 * @param val
	 */
	public void bind(Object val)
	{
		this.val = val;
		latch.countDown();
	}

	/**
	 * This method blocks till the count down latch has reset to 0.
	 * 
	 * @return Returns the value set by the bind method
	 * @throws InterruptedException
	 */
	public Object getVal() throws InterruptedException
	{
		latch.await();
		return val;
	}

	/**
	 * This method blocks for a specified amount of time to retrieve the value
	 * bound in bind method.
	 * 
	 * @param waitTime
	 *            the amount of time to wait
	 * @param timeUnit
	 *            the unit, milliseconds, seconds etc.
	 * @return Returns the bound value or null if the time out has exceeded.
	 * @throws InterruptedException
	 */
	public Object getVal(long waitTime, TimeUnit timeUnit)
			throws InterruptedException
	{
		if(latch.await(waitTime, timeUnit)){
			return val;
		}
		else
		{
			return null;
		}
	}
}
