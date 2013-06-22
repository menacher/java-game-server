package io.nadron.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;

/**
 * This class acts as a factory for creating <a
 * href="http://code.google.com/p/jetlang/">jetlang</a> {@link Fiber}s.
 * 
 * @author Abraham Menacherry
 * 
 */
public class Fibers
{
	// TODO inject this from spring or AppContext
	private static final ExecutorService SERVICE;
	private static final PoolFiberFactory FACT;
	private static final ConcurrentHashMap<Lane<String,ExecutorService>, PoolFiberFactory> lanePoolFactoryMap = new ConcurrentHashMap<Lane<String,ExecutorService>, PoolFiberFactory>();
	
	static{
		SERVICE = Executors.newSingleThreadExecutor();
		FACT = new PoolFiberFactory(SERVICE);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				SERVICE.shutdown();
			}
		});
	}
	
	/**
	 * Creates and starts a fiber and returns the created instance.
	 * @return The created fiber.
	 */
	public static Fiber pooledFiber()
	{
		Fiber fiber = FACT.create();
		fiber.start();
		return fiber;
	}
	
	/**
	 * Creates and starts a fiber and returns the created instance.
	 * @return The created fiber.
	 */
	public static Fiber pooledFiber(Lane<String,ExecutorService> lane)
	{
		if(null == lanePoolFactoryMap.get(lane))
		{
			lanePoolFactoryMap.putIfAbsent(lane, new PoolFiberFactory(lane.getUnderlyingLane()));
		}
		
		Fiber fiber = lanePoolFactoryMap.get(lane).create();
		fiber.start();
		return fiber;
	}
	
	public static Fiber threadFiber()
	{
		Fiber fiber = new ThreadFiber();
		fiber.start();
		return fiber;
	}
	
}
