package io.nadron.protocols.impl;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.nadron.networking.NadronMessages;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by saurabhshukul on 27/03/17.
 */
public class ProtobufProtocolTest {

    //private ProtobufProtocol protobufProtocol;

    @Before
    public void setUp()
    {
        //protobufProtocol = new ProtobufProtocol();
    }

    @Test
    public void verifyEventEncodingAndDecoding() throws InterruptedException, InvalidProtocolBufferException
    {
        NadronMessages.ConnectionConfig connectionConfig = NadronMessages.ConnectionConfig.newBuilder()
                .setUser("user")
                .setPass("pass")
                .setConnectionKey("TestRoom1")
                .build();

        NadronMessages.NadronEvent testLoginEvent = NadronMessages.NadronEvent.newBuilder()
                .setEventType(NadronMessages.EventType.LOG_IN)
                .setSource(Any.pack(connectionConfig))
                .setTimeStamp(new Date().getTime())
                .build();

        EmbeddedChannel outChannel = new EmbeddedChannel(
                new ProtobufVarint32LengthFieldPrepender(),
                new ProtobufEncoder());

        EmbeddedChannel inChannel = new EmbeddedChannel(new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(NadronMessages.NadronEvent.getDefaultInstance()));

        outChannel.writeOutbound(testLoginEvent);
        assertTrue(outChannel.finish());
        ByteBuf returnEvent = outChannel.readOutbound();
        assertNotNull(returnEvent);
        inChannel.writeInbound(returnEvent);

        assertTrue(inChannel.finish());
        NadronMessages.NadronEvent decoded = inChannel.readInbound();
		assertTrue(decoded.getEventType() == NadronMessages.EventType.LOG_IN);
        NadronMessages.ConnectionConfig connectionConfig1 = decoded.getSource().unpack(NadronMessages.ConnectionConfig.class);
		assertEquals(connectionConfig, connectionConfig1);
    }
}
