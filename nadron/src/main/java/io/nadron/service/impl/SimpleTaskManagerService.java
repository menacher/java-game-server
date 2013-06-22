package io.nadron.service.impl;

import io.nadron.app.Task;
import io.nadron.service.TaskManagerService;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * A thin wrapper on a <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html"
 * >ScheduledThreadPoolExecutor</a> class. It is used so as to keep track of all
 * the tasks. In future they could be made durable tasks which can be
 * transferred between multiple nodes for fail over, etc.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SimpleTaskManagerService extends ScheduledThreadPoolExecutor implements
		TaskManagerService
{
	/**
	 * Used to create a unique identifier for each task
	 */
	private AtomicInteger taskNum;

	public SimpleTaskManagerService(int corePoolSize)
	{
		super(corePoolSize);
		taskNum = new AtomicInteger(0);
	}

	@Override
	public void execute(Task task)
	{
		super.execute(task);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ScheduledFuture schedule(final Task task, long delay, TimeUnit unit)
	{
		task.setId(taskNum.incrementAndGet());
		return super.schedule(task, delay, unit);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ScheduledFuture scheduleAtFixedRate(Task task, long initialDelay,
			long period, TimeUnit unit)
	{
		task.setId(taskNum.incrementAndGet());
		return super.scheduleAtFixedRate(task, initialDelay, period, unit);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ScheduledFuture scheduleWithFixedDelay(Task task,
			long initialDelay, long delay, TimeUnit unit)
	{
		task.setId(taskNum.incrementAndGet());
		return super.scheduleWithFixedDelay(task, initialDelay, delay, unit);
	}

}
