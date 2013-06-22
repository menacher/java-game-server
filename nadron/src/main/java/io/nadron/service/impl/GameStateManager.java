package io.nadron.service.impl;

import io.nadron.service.GameStateManagerService;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameStateManager implements GameStateManagerService
{
	private static final Logger LOG = LoggerFactory.getLogger(GameStateManager.class);
	
	private Object state;
	byte [] serializedBytes;
	private AtomicInteger syncKey;

	public GameStateManager()
	{
		state = null;
		syncKey = new AtomicInteger(-1);
	}

	public GameStateManager(Object state, AtomicInteger syncKey)
	{
		super();
		this.state = state;
		this.syncKey = syncKey;
	}

	@Override
	public Object getState()
	{
		return state;
	}

	@Override
	public void setState(Object state)
	{
		this.state = state;
	}
	
	@Override
	public boolean compareAndSetState(Object key, Object state)
	{
		boolean syncKeySet = compareAndSetSyncKey(key);
		if(compareAndSetSyncKey(key))
		{
			this.state = state;
		}
		return syncKeySet;
	}
	
	@Override
	public Object getSyncKey()
	{
		return syncKey.get();
	}

	@Override
	public boolean compareAndSetSyncKey(Object key)
	{
		if (null == key || !(key instanceof Integer))
		{
			LOG.error("Invalid key provided: {}", key);
			return false;
		}

		Integer newKey = (Integer) key;
		return syncKey.compareAndSet(newKey, (++newKey));
	}

	@Override
	public byte[] getSerializedByteArray()
	{
		return serializedBytes;
	}

	@Override
	public void setSerializedByteArray(byte[] serializedBytes)
	{
		this.serializedBytes = serializedBytes;
	}
	
	@Override
	public Object computeAndSetNextState(Object state, Object syncKey,
			Object stateAlgorithm) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("computeAndSetNextState"
				+ "(Object state, Object syncKey,"
				+ "Object stateAlgorithm) not supported yet");
	}

	@Override
	public Object computeNextState(Object state, Object syncKey,
			Object stateAlgorithm) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("computeNextState"
				+ "(Object state, Object syncKey, Object stateAlgorithm)"
				+ " not supported yet");
	}

	@Override
	public Object getStateAlgorithm() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("getStateAlgorithm()"
				+ " not supported yet");
	}

}
