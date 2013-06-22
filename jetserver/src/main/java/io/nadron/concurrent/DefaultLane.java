package io.nadron.concurrent;

import java.util.concurrent.ExecutorService;

public class DefaultLane implements Lane<String,ExecutorService>
{

	final String laneName;
	final ExecutorService exec;

	public DefaultLane(String threadName, ExecutorService exec)
	{
		this.laneName = threadName;
		this.exec = exec;
	}

	@Override
	public boolean isOnSameLane(String currentThread)
	{
		return currentThread.equals(laneName);
	}

	@Override
	public ExecutorService getUnderlyingLane()
	{
		return exec;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((laneName == null) ? 0 : laneName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultLane other = (DefaultLane) obj;
		if (laneName == null)
		{
			if (other.laneName != null)
				return false;
		}
		else if (!laneName.equals(other.laneName))
			return false;
		return true;
	}

	@Override
	public String getId()
	{
		return laneName;
	}

}
