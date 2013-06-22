package io.nadron.concurrent;

public interface Lane<ID_TYPE,UNDERLYING_LANE>
{
	boolean isOnSameLane(ID_TYPE currentLane);
	ID_TYPE getId();
	UNDERLYING_LANE getUnderlyingLane();
}
