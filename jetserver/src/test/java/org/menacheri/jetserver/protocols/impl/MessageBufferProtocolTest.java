package org.menacheri.jetserver.protocols.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import io.netty.channel.embedded.EmbeddedMessageChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import org.junit.Before;
import org.junit.Test;
import org.menacheri.jetserver.communication.NettyMessageBuffer;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.handlers.netty.MessageBufferEventDecoder;
import org.menacheri.jetserver.handlers.netty.MessageBufferEventEncoder;

public class MessageBufferProtocolTest {

	private MessageBufferProtocol messageBufferProtocol;
	private LengthFieldBasedFrameDecoder frameDecoder;
	
	@Before
	public void setUp()
	{
		messageBufferProtocol = new MessageBufferProtocol();
		messageBufferProtocol.setLengthFieldPrepender(new LengthFieldPrepender(2, false));
		messageBufferProtocol.setMessageBufferEventDecoder(new MessageBufferEventDecoder());
		messageBufferProtocol.setMessageBufferEventEncoder(new MessageBufferEventEncoder());
		frameDecoder = messageBufferProtocol.createLengthBasedFrameDecoder();
	}
	
	@Test
	public void verifyEventEncodingAndDecoding() throws InterruptedException
	{
//		EmbeddedMessageChannel decoder = new EmbeddedMessageChannel(frameDecoder,
//				messageBufferProtocol.getMessageBufferEventDecoder(),
//				messageBufferProtocol.getLengthFieldPrepender(),
//				messageBufferProtocol.getMessageBufferEventEncoder());
//		NettyMessageBuffer payload = new NettyMessageBuffer();
//		payload.writeStrings("user","pass","TestRoom1");
//		Event event = Events.event(payload, Events.LOG_IN);
//		encoder.offer(event);
//		ChannelBuffer encoded = encoder.peek();
//		
//		Thread.sleep(100);// so that timestamps will differ.
//		decoder.offer(encoded);
//		Event decoded = decoder.peek();
//		assertEquals(decoded.getType(),Events.LOG_IN);
//		assertFalse("Timestamps should not be same",decoded.getTimeStamp() == event.getTimeStamp());
//		NettyMessageBuffer decodedPayload = (NettyMessageBuffer)decoded.getSource();
//		assertEquals("user",decodedPayload.readString());
//		assertEquals("pass",decodedPayload.readString());
//		assertEquals("TestRoom1",decodedPayload.readString());
	}
}
