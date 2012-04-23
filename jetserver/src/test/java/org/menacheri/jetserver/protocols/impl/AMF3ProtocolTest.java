package org.menacheri.jetserver.protocols.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.junit.Before;
import org.junit.Test;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.handlers.netty.AMF3ToEventSourceDecoder;
import org.menacheri.jetserver.handlers.netty.EventDecoder;
import org.menacheri.jetserver.handlers.netty.EventEncoder;
import org.menacheri.jetserver.handlers.netty.EventSourceToAMF3Encoder;
import org.menacheri.jetserver.protocols.impl.AMF3Protocol;

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
		amf3Protocol.setEventDecoder(new EventDecoder());
		amf3Protocol.setAmf3ToEventSourceDecoder(new AMF3ToEventSourceDecoder());
		amf3Protocol.setEventSourceToAMF3Encoder(new EventSourceToAMF3Encoder());
		amf3Protocol.setEventEncoder(new EventEncoder());
		amf3Protocol
				.setLengthFieldPrepender(new LengthFieldPrepender(2, false));
		playerStats = new PlayerStats(10, 11.1f, 12.2f, 10);
	}

	@Test
	public void verifyAMF3BinaryEncodingAndDecoding()
			throws InterruptedException
	{
		DecoderEmbedder<Event> decoder = new DecoderEmbedder<Event>(
				frameDecoder, amf3Protocol.getEventDecoder(),
				amf3Protocol.getAmf3ToEventSourceDecoder());

		EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(
				amf3Protocol.getLengthFieldPrepender(),
				amf3Protocol.getEventEncoder(),
				amf3Protocol.getEventSourceToAMF3Encoder());
		Event event = Events.event(playerStats,Events.SESSION_MESSAGE);
		encoder.offer(event);
		ChannelBuffer encoded = encoder.peek();
		decoder.offer(encoded);
		Event decoded = decoder.peek();
		assertTrue(decoded.getType() == Events.SESSION_MESSAGE);
		PlayerStats playerStats = (PlayerStats) decoded.getSource();
		assertEquals(playerStats, this.playerStats);
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
