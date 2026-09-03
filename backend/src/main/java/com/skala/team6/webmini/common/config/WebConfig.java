package com.skala.team6.webmini.common.config;

import com.skala.team6.webmini.demo.DemoUserArgumentResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        DemoUserProperties.class,
        AppAiProperties.class,
})
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;
    private final DemoUserArgumentResolver demoUserArgumentResolver;

    public WebConfig(
            CorsProperties corsProperties,
            DemoUserArgumentResolver demoUserArgumentResolver
    ) {
        this.corsProperties = corsProperties;
        this.demoUserArgumentResolver = demoUserArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(corsProperties.allowedOrigins().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(demoUserArgumentResolver);
    }
}
