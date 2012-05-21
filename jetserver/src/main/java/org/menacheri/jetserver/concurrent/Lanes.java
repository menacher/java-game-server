package org.menacheri.jetserver.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public enum Lanes
{
	LANES;
	final String serverCores = System.getProperty("jet.cores");
	final int numOfCores;
	final List<Lane<String, ExecutorService>> jetLanes;

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
		jetLanes = new ArrayList<Lane<String, ExecutorService>>();
		ThreadFactory threadFactory = new NamedThreadFactory("Lane");
		for (int i = 0; i <= cores; i++)
		{
			DefaultLane defaultLane = new DefaultLane("Lane[" + (i + 1) + "]",
					ManagedExecutor.newSingleThreadExecutor(threadFactory));
			jetLanes.add(defaultLane);
		}
	}

	public List<Lane<String, ExecutorService>> getJetLanes()
	{
		return jetLanes;
	}

	public int getNumOfCores()
	{
		return numOfCores;
	}
}
