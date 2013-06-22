package io.nadron.protocols.impl;

import static org.junit.Assert.assertEquals;
import io.nadron.event.Event;
import io.nadron.event.impl.DefaultEvent;
import io.nadron.junitcategories.Performance;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class WebsocketProtocolTest
{
	private ObjectMapper mapper;
	
	@Before
	public void setUp()
	{
		 mapper = new ObjectMapper();
	}
	
	@Test
	@Category(Performance.class)
	public void testJacksonConversion() throws Exception
	{
		PlayerStats stats = new PlayerStats(1, 2, 5, "ablajdlajldkfjaldjfcd");
		AGameEvent gEvent = new AGameEvent();
		gEvent.setSource(stats);
		gEvent.setcName(AGameEvent.class.getName());
		String json = mapper.writeValueAsString(gEvent);
		PlayerStats stats2 = null;
		long start = System.nanoTime();
		for (int i = 0; i < 10000000; i++)
		{
			DefaultEvent evt = mapper.readValue(json, DefaultEvent.class);
			@SuppressWarnings("unchecked")
			Class<? extends Event> forName = (Class<? extends Event>)Class.forName(evt.getcName());
			Event readValue = mapper.readValue(json, forName);
			stats2 = (PlayerStats)readValue.getSource();
		}
		long time = System.nanoTime() - start;
		System.out.printf("Gson parsing rate was %.3f million/sec",
				10000000 / ((time / 1e9) * 1000000));

		assertEquals(stats, stats2);
	}
	
	@Test
	public void testFromAndToJson() throws Exception
	{
		PlayerStats stats = new PlayerStats(1, 2, 5, "ablajdlajldkfjaldjfcd");
		AGameEvent gEvent = new AGameEvent();
		gEvent.setSource(stats);
		gEvent.setcName(AGameEvent.class.getName());
		String json = mapper.writeValueAsString(gEvent);
		Event readValue = mapper.readValue(json, AGameEvent.class);
		PlayerStats stats2 = (PlayerStats)readValue.getSource();
		assertEquals(stats, stats2);
	}
	
	public static class PlayerStats
	{
		public PlayerStats(int x, int y, int speed, String name)
		{
			super();
			this.x = x;
			this.y = y;
			this.speed = speed;
			this.name = name;
		}

		long id, ab, cd;
		int x, y, speed;
		String name;

		public PlayerStats()
		{

		}

		public int getX()
		{
			return x;
		}

		public void setX(int x)
		{
			this.x = x;
		}

		public int getY()
		{
			return y;
		}

		public void setY(int y)
		{
			this.y = y;
		}

		public int getSpeed()
		{
			return speed;
		}

		public void setSpeed(int speed)
		{
			this.speed = speed;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + speed;
			result = prime * result + x;
			result = prime * result + y;
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
			PlayerStats other = (PlayerStats) obj;
			if (name == null)
			{
				if (other.name != null)
					return false;
			}
			else if (!name.equals(other.name))
				return false;
			if (speed != other.speed)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		public long getId()
		{
			return id;
		}

		public void setId(long id)
		{
			this.id = id;
		}

		public long getAb()
		{
			return ab;
		}

		public void setAb(long ab)
		{
			this.ab = ab;
		}

		public long getCd()
		{
			return cd;
		}

		public void setCd(long cd)
		{
			this.cd = cd;
		}
	}

	public static class AGameEvent extends DefaultEvent
	{
		private static final long serialVersionUID = 1L;
		private PlayerStats source;

		public PlayerStats getSource()
		{
			return source;
		}

		public void setSource(PlayerStats source)
		{
			this.source = source;
		}
	}
}
