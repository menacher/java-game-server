package io.nadron.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nadron.service.impl.ReconnectSessionRegistry;
import io.nadron.service.impl.SessionRegistry;
import io.nadron.service.impl.SimpleGameAdminService;
import io.nadron.service.impl.SimpleTaskManagerService;
import io.nadron.service.impl.SimpleUniqueIdGenerator;
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashSet;

/**
 * Spring Java configuration replacing service-beans.xml
 */
@Configuration
@PropertySource("classpath:nadron/props/nadron.properties")
public class ServiceBeansConfig {

    @Value("${reconnect.delay}")
    private long reconnectDelay;

    @Bean
    public SimpleTaskManagerService taskManagerService() {
        return new SimpleTaskManagerService(2);
    }

    @Bean
    public SimpleGameAdminService gameAdminService() {
        SimpleGameAdminService service = new SimpleGameAdminService();
        service.setGames(new HashSet<>());
        return service;
    }

    @Bean
    public SessionRegistry udpSessionRegistry() {
        return new SessionRegistry();
    }

    @Bean
    public ReconnectSessionRegistry reconnectSessionRegistry() {
        ReconnectSessionRegistry registry = new ReconnectSessionRegistry();
        registry.setTaskManagerService(taskManagerService());
        registry.setReconnectDelay((int) reconnectDelay);
        return registry;
    }

    @Bean
    public SimpleUniqueIdGenerator simpleUniqueIdGenerator() {
        return new SimpleUniqueIdGenerator();
    }

    @Bean
    public ObjectMapper jackson() {
        return new ObjectMapper();
    }

    @Bean
    public MessagePack msgPack() {
        return new MessagePack();
    }
}
