package org.menacheri.jetserver.concurrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.menacheri.jetserver.app.GameRoom;

public interface LaneStrategy
{
	<I, T, O> Lane<I, T> chooseLane(O group);

	public enum LaneStrategies implements LaneStrategy
	{

		ROUND_ROBIN
		{
			final AtomicInteger currentLane = new AtomicInteger(0);

			@SuppressWarnings("unchecked")
			@Override
			public Lane<String, ExecutorService> chooseLane(Object group)
			{
				synchronized (currentLane)
				{
					if (currentLane.get() == lanes.size())
					{
						currentLane.set(0);
					}
				}
				return lanes.get(currentLane.getAndIncrement());
			}
		},
		GAME_ROOM
		{
			@SuppressWarnings("rawtypes")
			Map laneRoomMap = new HashMap();

			@SuppressWarnings({ "hiding", "unchecked" })
			@Override
			public <String, ExecutorService, GameRoom> Lane<String, ExecutorService> chooseLane(
					GameRoom group)
			{
				synchronized (laneRoomMap)
				{
					if (laneRoomMap.isEmpty())
					{
						for (@SuppressWarnings("rawtypes")
						Lane lane : lanes)
						{
							List<GameRoom> roomList = new ArrayList<GameRoom>();
							laneRoomMap.put(lane, roomList);
						}
					}
				}

				return null;
			}

		};

		final List<Lane<String, ExecutorService>> lanes = Lanes.LANES
				.getJetLanes();
	}

}
