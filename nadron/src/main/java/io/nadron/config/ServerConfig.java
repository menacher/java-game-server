package io.nadron.config;

import io.nadron.context.AppContext;
import io.nadron.handlers.netty.FlashPolicyServerHandler;
import io.nadron.handlers.netty.LoginProtocol;
import io.nadron.handlers.netty.UDPEventEncoder;
import io.nadron.handlers.netty.UDPUpstreamHandler;
import io.nadron.server.netty.*;
import io.nadron.service.impl.SimpleGameAdminService;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Java configuration replacing server-beans.xml
 */
@Configuration
@Import(ProtocolsConfig.class)
public class ServerConfig {

    @Bean
    public AppContext appContext() {
        return new AppContext();
    }

    @Bean
    public ServerManagerImpl serverManager() {
        return new ServerManagerImpl();
    }

    @Bean(destroyMethod = "stopServer")
    public NettyTCPServer tcpServer(@org.springframework.beans.factory.annotation.Qualifier("tcpConfig") NettyConfig tcpConfig,
                                     ProtocolMultiplexerChannelInitializer protocolMultiplexerChannelInitializer,
                                     SimpleGameAdminService gameAdminService) {
        NettyTCPServer server = new NettyTCPServer(tcpConfig, protocolMultiplexerChannelInitializer);
        server.setGameAdminService(gameAdminService);
        return server;
    }

    @Bean(destroyMethod = "stopServer")
    public NettyUDPServer udpServer(@org.springframework.beans.factory.annotation.Qualifier("udpConfig") NettyConfig udpConfig,
                                     UDPChannelInitializer udpChannelInitializer,
                                     SimpleGameAdminService gameAdminService) {
        NettyUDPServer server = new NettyUDPServer(udpConfig, udpChannelInitializer);
        server.setGameAdminService(gameAdminService);
        return server;
    }

    @Bean(destroyMethod = "stopServer")
    public NettyTCPServer flashPolicyServer(@org.springframework.beans.factory.annotation.Qualifier("flashConfig") NettyConfig flashConfig,
                                             FlashPolicyServerChannelInitalizer flashPolicyServerChannelInitializer,
                                             SimpleGameAdminService gameAdminService) {
        NettyTCPServer server = new NettyTCPServer(flashConfig, flashPolicyServerChannelInitializer);
        server.setGameAdminService(gameAdminService);
        return server;
    }

    @Bean
    public FlashPolicyServerChannelInitalizer flashPolicyServerChannelInitializer() {
        return new FlashPolicyServerChannelInitalizer() {
            @Override
            @Lookup
            public FlashPolicyServerHandler getFlashPolicyServerHandler() {
                return null; // Spring will override this with the actual bean lookup
            }
        };
    }

    @Bean
    public UDPChannelInitializer udpChannelInitializer(UDPEventEncoder udpEventEncoder,
                                                        UDPUpstreamHandler upstream) {
        UDPChannelInitializer initializer = new UDPChannelInitializer();
        initializer.setUdpEventEncoder(udpEventEncoder);
        initializer.setUpstream(upstream);
        return initializer;
    }

    @Bean
    public ProtocolMultiplexerChannelInitializer protocolMultiplexerChannelInitializer(
            LoginProtocol.CompositeProtocol compositeLoginProtocol) {
        ProtocolMultiplexerChannelInitializer initializer = new ProtocolMultiplexerChannelInitializer();
        initializer.setBytesForProtocolCheck(5);
        initializer.setLoginProtocol(compositeLoginProtocol);
        return initializer;
    }
}
