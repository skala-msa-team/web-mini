package com.skala.team6.webmini.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI webMiniOpenApi(DemoUserProperties demoUserProperties) {
        String schemeName = "demoUserHeader";
        Components components = new Components()
                .addSecuritySchemes(
                        schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(demoUserProperties.headerName())
                )
                .addParameters(
                        "demoUserHeader",
                        new Parameter()
                                .in("header")
                                .name(demoUserProperties.headerName())
                                .required(true)
                                .description("브라우저 Local Storage에 저장한 Demo 사용자 UUID")
                );
        return new OpenAPI()
                .info(new Info()
                        .title("Web Mini Demo API")
                        .version("v1")
                        .description("Demo API 명세 기반 컨트롤러 스캐폴딩"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                ;
    }
}
