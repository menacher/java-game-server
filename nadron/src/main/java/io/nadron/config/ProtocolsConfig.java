package io.nadron.config;

import io.nadron.handlers.netty.*;
import io.nadron.protocols.impl.*;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;

/**
 * Spring Java configuration replacing server-protocols.xml
 */
@Configuration
@Import(NettyHandlersConfig.class)
public class ProtocolsConfig {

    @Bean
    public AMF3Protocol amf3Protocol(LengthFieldPrepender lengthFieldPrepender,
                                     JavaObjectToAMF3Encoder javaToAMF3Encoder) {
        AMF3Protocol protocol = new AMF3Protocol();
        protocol.setLengthFieldPrepender(lengthFieldPrepender);
        protocol.setJavaObjectToAMF3Encoder(javaToAMF3Encoder);
        return protocol;
    }

    @Bean
    public AMF3StringProtocol amf3StringProtocol(NulEncoder nulEncoder,
                                                  Base64Decoder base64Decoder,
                                                  Base64Encoder base64Encoder,
                                                  JavaObjectToAMF3Encoder javaToAMF3Encoder) {
        AMF3StringProtocol protocol = new AMF3StringProtocol();
        protocol.setMaxFrameSize(4096);
        protocol.setNulEncoder(nulEncoder);
        protocol.setBase64Decoder(base64Decoder);
        protocol.setBase64Encoder(base64Encoder);
        protocol.setJavaObjectToAMF3Encoder(javaToAMF3Encoder);
        return protocol;
    }

    @Bean
    public StringProtocol stringProtocol(NulEncoder nulEncoder,
                                          StringDecoder stringDecoder,
                                          StringEncoder stringMessageEncoder) {
        StringProtocol protocol = new StringProtocol();
        protocol.setFrameSize(4096);
        protocol.setNulEncoder(nulEncoder);
        protocol.setStringDecoder(stringDecoder);
        protocol.setStringEncoder(stringMessageEncoder);
        return protocol;
    }

    @Bean
    public ChannelBufferProtocol channelBufferProtocol(EventDecoder eventDecoder,
                                                         LengthFieldPrepender lengthFieldPrepender,
                                                         EventEncoder eventEncoder) {
        ChannelBufferProtocol protocol = new ChannelBufferProtocol();
        protocol.setEventDecoder(eventDecoder);
        protocol.setLengthFieldPrepender(lengthFieldPrepender);
        protocol.setEventEncoder(eventEncoder);
        return protocol;
    }

    @Bean
    public MessageBufferProtocol messageBufferProtocol(LengthFieldPrepender lengthFieldPrepender,
                                                        MessageBufferEventDecoder messageBufferEventDecoder,
                                                        @org.springframework.beans.factory.annotation.Qualifier("messageBufferEventEncoder") MessageBufferEventEncoder messageBufferEventEncoder) {
        MessageBufferProtocol protocol = new MessageBufferProtocol();
        protocol.setLengthFieldPrepender(lengthFieldPrepender);
        protocol.setMessageBufferEventDecoder(messageBufferEventDecoder);
        protocol.setMessageBufferEventEncoder(messageBufferEventEncoder);
        return protocol;
    }

    @Bean
    public WebSocketProtocol webSocketProtocol(TextWebsocketDecoder textWebsocketDecoder,
                                                TextWebsocketEncoder textWebsocketEncoder) {
        WebSocketProtocol protocol = new WebSocketProtocol();
        protocol.setTextWebsocketDecoder(textWebsocketDecoder);
        protocol.setTextWebsocketEncoder(textWebsocketEncoder);
        return protocol;
    }

    @Bean
    public NettyObjectProtocol nettyObjectProtocol(LengthFieldPrepender lengthFieldPrepender) {
        NettyObjectProtocol protocol = new NettyObjectProtocol();
        protocol.setLengthFieldPrepender(lengthFieldPrepender);
        return protocol;
    }

    @Bean
    public MsgPackProtocol msgPackProtocol(LengthFieldPrepender lengthFieldPrepender,
                                            MsgPackDecoder msgPackDecoder,
                                            MsgPackEncoder msgPackEncoder) {
        MsgPackProtocol protocol = new MsgPackProtocol();
        protocol.setLengthFieldPrepender(lengthFieldPrepender);
        protocol.setMsgPackDecoder(msgPackDecoder);
        protocol.setMsgPackEncoder(msgPackEncoder);
        return protocol;
    }

    // Login Protocols
    @Bean
    public LoginProtocol.HTTPProtocol httpLoginProtocol(WebSocketLoginHandler webSocketLoginHandler) {
        LoginProtocol.HTTPProtocol protocol = new LoginProtocol.HTTPProtocol();
        protocol.setWebSocketLoginHandler(webSocketLoginHandler);
        return protocol;
    }

    @Bean
    public LoginProtocol.DefaultNadProtocol defaultNadLoginProtocol(EventDecoder eventDecoder,
                                                                      LoginHandler loginHandler,
                                                                      LengthFieldPrepender lengthFieldPrepender) {
        LoginProtocol.DefaultNadProtocol protocol = new LoginProtocol.DefaultNadProtocol();
        protocol.setEventDecoder(eventDecoder);
        protocol.setLoginHandler(loginHandler);
        protocol.setLengthFieldPrepender(lengthFieldPrepender);
        return protocol;
    }

    @Bean
    @Scope("prototype")
    public LoginProtocol.CompositeProtocol compositeLoginProtocol(LoginProtocol.DefaultNadProtocol defaultNadLoginProtocol,
                                                                    LoginProtocol.HTTPProtocol httpLoginProtocol) {
        LoginProtocol.CompositeProtocol protocol = new LoginProtocol.CompositeProtocol();
        protocol.setProtocols(Arrays.asList(defaultNadLoginProtocol, httpLoginProtocol));
        return protocol;
    }
}
