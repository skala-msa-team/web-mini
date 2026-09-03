package com.skala.team6.webmini.common.config;

import com.skala.team6.webmini.websocket.DemoUserChannelInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final CorsProperties corsProperties;
    private final WebSocketProperties webSocketProperties;
    private final DemoUserChannelInterceptor demoUserChannelInterceptor;

    public WebSocketStompConfig(
            CorsProperties corsProperties,
            WebSocketProperties webSocketProperties,
            DemoUserChannelInterceptor demoUserChannelInterceptor
    ) {
        this.corsProperties = corsProperties;
        this.webSocketProperties = webSocketProperties;
        this.demoUserChannelInterceptor = demoUserChannelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocketProperties.endpoint())
                .setAllowedOrigins(corsProperties.allowedOrigins().toArray(String[]::new));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(webSocketProperties.heartbeat())
                .setTaskScheduler(stompHeartbeatTaskScheduler());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(demoUserChannelInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(webSocketProperties.messageSizeLimit());
        registry.setSendBufferSizeLimit(webSocketProperties.sendBufferSizeLimit());
        registry.setSendTimeLimit(webSocketProperties.sendTimeLimit());
    }

    @Bean
    TaskScheduler stompHeartbeatTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("stomp-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }
}
