package org.menacheri.jetserver.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public enum Lanes
{
	LANES;
	final String serverCores = System.getProperty("jet.lanes");
	final int numOfCores;
	final Lane<String, ExecutorService>[] jetLanes;

	@SuppressWarnings("unchecked")
	Lanes()
	{
		int cores = 1;
		if (null != serverCores)
		{
			try
			{
				cores = Integer.parseInt(serverCores);
			}
			catch (NumberFormatException e)
			{
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
