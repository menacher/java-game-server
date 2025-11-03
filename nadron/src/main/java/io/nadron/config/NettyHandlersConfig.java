package io.nadron.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nadron.concurrent.NamedThreadFactory;
import io.nadron.handlers.netty.*;
import io.nadron.server.netty.NettyConfig;
import io.nadron.service.impl.ReconnectSessionRegistry;
import io.nadron.service.impl.SessionRegistry;
import io.nadron.service.impl.SimpleUniqueIdGenerator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Java configuration replacing netty-handlers.xml
 */
@Configuration
@Import(ServiceBeansConfig.class)
public class NettyHandlersConfig {

    @Value("${tcp.port}")
    private int tcpPort;

    @Value("${udp.port}")
    private int udpPort;

    @Value("${flash.policyport}")
    private int flashPolicyPort;

    @Value("${bossThreadCount}")
    private int bossThreadCount;

    @Value("${workerThreadCount}")
    private int workerThreadCount;

    @Value("${so.keepalive}")
    private boolean soKeepAlive;

    @Value("${so.backlog}")
    private int soBacklog;

    @Value("${so.sndbuf}")
    private int soSndbuf;

    @Value("${so.rcvbuf}")
    private int soRcvbuf;

    @Value("${so.broadcast}")
    private boolean soBroadcast;

    // Netty codecs
    @Bean
    public StringDecoder stringDecoder() {
        return new StringDecoder();
    }

    @Bean
    public StringEncoder stringMessageEncoder() {
        return new StringEncoder();
    }

    @Bean
    public Base64Decoder base64Decoder() {
        return new Base64Decoder();
    }

    @Bean
    public Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }

    @Bean
    public NulEncoder nulEncoder() {
        return new NulEncoder();
    }

    @Bean
    public LengthFieldPrepender lengthFieldPrepender() {
        return new LengthFieldPrepender(2, false);
    }

    @Bean
    @Scope("prototype")
    public LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder() {
        return new LengthFieldBasedFrameDecoder(4096, 0, 2, 0, 2);
    }

    @Bean
    public FlashPolicyServerDecoder flashPolicyServerDecoder() {
        return new FlashPolicyServerDecoder();
    }

    @Bean
    @Scope("prototype")
    public FlashPolicyServerHandler flashPolicyServerHandler() {
        return new FlashPolicyServerHandler(String.valueOf(tcpPort));
    }

    @Bean
    public AMF3ToJavaObjectDecoder AMF3ToJavaObjectDecoder() {
        return new AMF3ToJavaObjectDecoder();
    }

    @Bean
    public JavaObjectToAMF3Encoder javaToAMF3Encoder() {
        return new JavaObjectToAMF3Encoder();
    }

    @Bean
    public EventDecoder eventDecoder() {
        return new EventDecoder();
    }

    @Bean
    public EventEncoder eventEncoder() {
        return new EventEncoder();
    }

    @Bean
    public MessageBufferEventDecoder messageBufferEventDecoder() {
        return new MessageBufferEventDecoder();
    }

    @Bean
    public MessageBufferEventEncoder messageBufferEventEncoder() {
        return new MessageBufferEventEncoder();
    }

    @Bean
    public UDPEventEncoder udpEventEncoder() {
        return new UDPEventEncoder();
    }

    @Bean
    public UDPUpstreamHandler udpUpstreamHandler(@org.springframework.beans.factory.annotation.Qualifier("udpSessionRegistry") SessionRegistry udpSessionRegistry,
                                                   MessageBufferEventDecoder messageBufferEventDecoder) {
        UDPUpstreamHandler handler = new UDPUpstreamHandler();
        handler.setUdpSessionRegistry(udpSessionRegistry);
        handler.setMessageBufferEventDecoder(messageBufferEventDecoder);
        return handler;
    }

    @Bean
    public LoginHandler loginHandler(@org.springframework.beans.factory.annotation.Qualifier("udpSessionRegistry") SessionRegistry udpSessionRegistry,
                                      SimpleUniqueIdGenerator simpleUniqueIdGenerator,
                                      ReconnectSessionRegistry reconnectSessionRegistry) {
        LoginHandler handler = new LoginHandler();
        handler.setUdpSessionRegistry(udpSessionRegistry);
        handler.setIdGeneratorService(simpleUniqueIdGenerator);
        handler.setReconnectRegistry(reconnectSessionRegistry);
        return handler;
    }

    @Bean
    public WebSocketLoginHandler webSocketLoginHandler(SimpleUniqueIdGenerator simpleUniqueIdGenerator,
                                                        ReconnectSessionRegistry reconnectSessionRegistry,
                                                        ObjectMapper jackson) {
        WebSocketLoginHandler handler = new WebSocketLoginHandler();
        handler.setIdGeneratorService(simpleUniqueIdGenerator);
        handler.setReconnectRegistry(reconnectSessionRegistry);
        handler.setJackson(jackson);
        return handler;
    }

    @Bean
    public TextWebsocketEncoder textWebsocketEncoder(ObjectMapper jackson) {
        TextWebsocketEncoder encoder = new TextWebsocketEncoder();
        encoder.setJackson(jackson);
        return encoder;
    }

    @Bean
    public TextWebsocketDecoder textWebsocketDecoder(ObjectMapper jackson) {
        TextWebsocketDecoder decoder = new TextWebsocketDecoder();
        decoder.setJackson(jackson);
        return decoder;
    }

    @Bean
    public MsgPackDecoder msgPackDecoder(MessagePack msgPack) {
        MsgPackDecoder decoder = new MsgPackDecoder();
        decoder.setMsgPack(msgPack);
        return decoder;
    }

    @Bean
    public MsgPackEncoder msgPackEncoder(MessagePack msgPack) {
        MsgPackEncoder encoder = new MsgPackEncoder();
        encoder.setMsgPack(msgPack);
        return encoder;
    }

    // Event loop groups
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossThreadCount, bossThreadFactory());
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerThreadCount, workerThreadFactory());
    }

    @Bean
    public NamedThreadFactory bossThreadFactory() {
        return new NamedThreadFactory("Server-Boss");
    }

    @Bean
    public NamedThreadFactory workerThreadFactory() {
        return new NamedThreadFactory("Server-Worker");
    }

    // Channel options
    @Bean
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<>();
        options.put(ChannelOption.SO_KEEPALIVE, soKeepAlive);
        options.put(ChannelOption.SO_BACKLOG, soBacklog);
        return options;
    }

    @Bean
    public Map<ChannelOption<?>, Object> udpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<>();
        options.put(ChannelOption.SO_SNDBUF, soSndbuf);
        options.put(ChannelOption.SO_RCVBUF, soRcvbuf);
        options.put(ChannelOption.SO_BROADCAST, soBroadcast);
        return options;
    }

    // Netty configs
    @Bean
    public NettyConfig tcpConfig() {
        NettyConfig config = new NettyConfig();
        config.setChannelOptions(tcpChannelOptions());
        config.setBossGroup(bossGroup());
        config.setWorkerGroup(workerGroup());
        config.setPortNumber(tcpPort);
        return config;
    }

    @Bean
    public NettyConfig udpConfig() {
        NettyConfig config = new NettyConfig();
        config.setChannelOptions(udpChannelOptions());
        config.setBossGroup(bossGroup());
        config.setPortNumber(udpPort);
        return config;
    }

    @Bean
    public NettyConfig flashConfig() {
        NettyConfig config = new NettyConfig();
        config.setChannelOptions(tcpChannelOptions());
        config.setBossGroup(bossGroup());
        config.setWorkerGroup(workerGroup());
        config.setPortNumber(flashPolicyPort);
        return config;
    }
}
