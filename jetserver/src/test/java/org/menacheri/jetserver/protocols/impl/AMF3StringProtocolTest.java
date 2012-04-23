package org.menacheri.jetserver.protocols.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.base64.Base64Decoder;
import org.jboss.netty.handler.codec.base64.Base64Encoder;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.junit.Before;
import org.junit.Test;
import org.menacheri.jetserver.communication.NettyMessageBuffer;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.handlers.netty.AMF3ToJavaObjectDecoder;
import org.menacheri.jetserver.handlers.netty.JavaObjectToAMF3Encoder;
import org.menacheri.jetserver.handlers.netty.NulEncoder;
import org.menacheri.jetserver.protocols.impl.AMF3StringProtocol;

public class AMF3StringProtocolTest {

	private AMF3StringProtocol amf3StringProtocol;
	private DelimiterBasedFrameDecoder delimiterDecoder;
	@Before
	public void setUp()
	{
		amf3StringProtocol = new AMF3StringProtocol();
		amf3StringProtocol.setMaxFrameSize(1024);
		delimiterDecoder = new DelimiterBasedFrameDecoder(amf3StringProtocol.getMaxFrameSize(),
				Delimiters.nulDelimiter());
		amf3StringProtocol.setBase64Decoder(new Base64Decoder());
		amf3StringProtocol.setAmf3ToJavaObjectDecoder(new AMF3ToJavaObjectDecoder());
		amf3StringProtocol.setNulEncoder(new NulEncoder());
		amf3StringProtocol.setBase64Encoder(new Base64Encoder());
		amf3StringProtocol.setJavaObjectToAMF3Encoder(new JavaObjectToAMF3Encoder());
	}
	
	@Test
	public void verifyAMF3StringEncodingAndDecoding() throws InterruptedException
	{
		DecoderEmbedder<Event> decoder = new DecoderEmbedder<Event>(
				delimiterDecoder, amf3StringProtocol.getBase64Decoder(),
				amf3StringProtocol.getAmf3ToJavaObjectDecoder());
		
		EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(
				amf3StringProtocol.getNulEncoder(),
				amf3StringProtocol.getBase64Encoder(),
				amf3StringProtocol.getJavaObjectToAMF3Encoder());
		
		NettyMessageBuffer payload = new NettyMessageBuffer();
		payload.writeStrings("user","pass","TestRoom1");
		
		Event event = Events.event(payload, Events.LOG_IN);
		encoder.offer(event);
		ChannelBuffer encoded = encoder.peek();
		
		Thread.sleep(10);// despite delay the timestamps should be same since we are decoding the whole object.
		decoder.offer(encoded);
		Event decoded = decoder.peek();
		assertEquals(decoded.getType(),Events.LOG_IN);
		assertTrue("Timestamps should be same" ,decoded.getTimeStamp() == event.getTimeStamp());
		NettyMessageBuffer decodedPayload = (NettyMessageBuffer)decoded.getSource();
		// NettyMessageBuffer will not get de-serialized properly.
		assertNull(decodedPayload.readString()); 
	}
}
