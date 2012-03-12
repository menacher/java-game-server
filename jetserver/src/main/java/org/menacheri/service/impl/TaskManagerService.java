package org.menacheri.service.impl;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.menacheri.app.ITask;
import org.menacheri.service.ITaskManagerService;


/**
 * A thin wrapper on a <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html"
 * >ScheduledThreadPoolExecutor</a> class. It is used so as to keep track of all
 * the tasks. In future they could be made durable tasks which can be
 * transferred between multiple nodes for fail over, etc.
 * 
 * @author Abraham Menacherry
 * 
 */
public class TaskManagerService extends ScheduledThreadPoolExecutor implements
		ITaskManagerService
{
	/**
	 * Used to create a unique identifier for each task
	 */
	private AtomicInteger taskNum;

	public TaskManagerService(int corePoolSize)
	{
		super(corePoolSize);
		taskNum = new AtomicInteger(0);
	}

	@Override
	public void execute(ITask task)
	{
		super.execute(task);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ScheduledFuture schedule(final ITask task, long delay, TimeUnit unit)
	{
		task.setTaskId("" + taskNum.incrementAndGet());
		return super.schedule(task, delay, unit);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ScheduledFuture scheduleAtFixedRate(ITask task, long initialDelay,
			long period, TimeUnit unit)
	{
		task.setTaskId("" + taskNum.incrementAndGet());
		return super.scheduleAtFixedRate(task, initialDelay, period, unit);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ScheduledFuture scheduleWithFixedDelay(ITask task,
			long initialDelay, long delay, TimeUnit unit)
	{
		task.setTaskId("" + taskNum.incrementAndGet());
		return super.scheduleWithFixedDelay(task, initialDelay, delay, unit);
	}

}
