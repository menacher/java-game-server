package io.nadron.protocols.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.handlers.netty.JavaObjectToAMF3Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

public class AMF3ProtocolTest
{
	private AMF3Protocol amf3Protocol;
	private LengthFieldBasedFrameDecoder frameDecoder;
	private PlayerStats playerStats;

	@Before
	public void setUp()
	{
		amf3Protocol = new AMF3Protocol();
		frameDecoder = amf3Protocol.createLengthBasedFrameDecoder();
		amf3Protocol.setJavaObjectToAMF3Encoder(new JavaObjectToAMF3Encoder());
		amf3Protocol
				.setLengthFieldPrepender(new LengthFieldPrepender(2, false));
		playerStats = new PlayerStats(10, 11.1f, 12.2f, 10);
	}

	@Test
	public void verifyAMF3BinaryEncodingAndDecoding()
			throws InterruptedException
	{
		EmbeddedChannel outChannel = new EmbeddedChannel(
				amf3Protocol.getLengthFieldPrepender(),
				amf3Protocol.getJavaObjectToAMF3Encoder());
		EmbeddedChannel inChannel = new EmbeddedChannel(frameDecoder,
				amf3Protocol.createAMF3ToJavaObjectDecoder());
		Event event = Events.event(playerStats,Events.SESSION_MESSAGE);
		outChannel.writeOutbound(event);
		assertTrue(outChannel.finish());
		ByteBuf buffer = (ByteBuf)outChannel.readOutbound();
		assertNotNull(buffer);
		inChannel.writeInbound(buffer);
		//assertTrue(inChannel.finish());
//		Event decoded = (Event)inChannel.readInbound();
//		assertTrue(decoded.getType() == Events.SESSION_MESSAGE);
//		PlayerStats playerStats = (PlayerStats) decoded.getSource();
//		assertEquals(playerStats, this.playerStats);
	}

	public static class PlayerStats implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public PlayerStats()
		{

		}

		public PlayerStats(int life, float xPos, float yPos, int wealth)
		{
			super();
			this.life = life;
			this.xPos = xPos;
			this.yPos = yPos;
			this.wealth = wealth;
		}

		int life;
		float xPos;
		float yPos;
		int wealth;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + life;
			result = prime * result + wealth;
			result = prime * result + Float.floatToIntBits(xPos);
			result = prime * result + Float.floatToIntBits(yPos);
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
			if (life != other.life)
				return false;
			if (wealth != other.wealth)
				return false;
			if (Float.floatToIntBits(xPos) != Float.floatToIntBits(other.xPos))
				return false;
			if (Float.floatToIntBits(yPos) != Float.floatToIntBits(other.yPos))
				return false;
			return true;
		}

		public int getLife()
		{
			return life;
		}

		public void setLife(int life)
		{
			this.life = life;
		}

		public float getxPos()
		{
			return xPos;
		}

		public void setxPos(float xPos)
		{
			this.xPos = xPos;
		}

		public float getyPos()
		{
			return yPos;
		}

		public void setyPos(float yPos)
		{
			this.yPos = yPos;
		}

		public int getWealth()
		{
			return wealth;
		}

		public void setWealth(int wealth)
		{
			this.wealth = wealth;
		}
	}
}
