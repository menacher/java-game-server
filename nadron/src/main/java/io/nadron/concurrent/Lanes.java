package io.nadron.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Lanes
{
	LANES;
	final String serverCores = System.getProperty("jet.lanes");
	final int numOfCores;
	final Lane<String, ExecutorService>[] jetLanes;

	@SuppressWarnings("unchecked")
	Lanes()
	{
		final Logger LOG = LoggerFactory.getLogger(Lanes.class);
		
		int cores = 1;
		if (null != serverCores)
		{
			try
			{
				cores = Integer.parseInt(serverCores);
			}
			catch (NumberFormatException e)
			{
				LOG.warn("Invalid server cores {} passed in, going to ignore",serverCores);
				// ignore;
			}
		}
		numOfCores = cores;
		jetLanes = new Lane[cores];
		ThreadFactory threadFactory = new NamedThreadFactory("Lane",true);
		for (int i = 1; i <= cores; i++)
		{
			DefaultLane defaultLane = new DefaultLane("Lane[" + i + "]",
					ManagedExecutor.newSingleThreadExecutor(threadFactory));
			jetLanes[i - 1] = defaultLane;
		}
	}

	public Lane<String, ExecutorService>[] getJetLanes()
	{
		return jetLanes;
	}

	public int getNumOfCores()
	{
		return numOfCores;
	}
}
