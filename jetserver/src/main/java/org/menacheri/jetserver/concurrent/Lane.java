package org.menacheri.jetserver.concurrent;

public interface Lane<I,T>
{
	boolean isOnSameLane(I currentLane);
	I getId();
	T getUnderlyingLane();
}
