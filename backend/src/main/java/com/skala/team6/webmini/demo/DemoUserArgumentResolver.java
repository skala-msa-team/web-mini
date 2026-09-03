package com.skala.team6.webmini.demo;

import com.skala.team6.webmini.common.config.DemoUserProperties;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class DemoUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final DemoUserProperties demoUserProperties;
    private final DemoUserRegistry demoUserRegistry;

    public DemoUserArgumentResolver(
            DemoUserProperties demoUserProperties,
            DemoUserRegistry demoUserRegistry
    ) {
        this.demoUserProperties = demoUserProperties;
        this.demoUserRegistry = demoUserRegistry;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DemoUserId.class)
                && DemoUserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String demoUserId = webRequest.getHeader(demoUserProperties.headerName());
        if (demoUserId == null || demoUserId.isBlank()) {
            throw new ApiException(ErrorCode.DEMO_USER_REQUIRED);
        }

        try {
            UUID.fromString(demoUserId);
        } catch (IllegalArgumentException exception) {
            throw new ApiException(ErrorCode.DEMO_USER_REQUIRED);
        }

        demoUserRegistry.getOrCreate(demoUserId);
        return new DemoUserContext(demoUserId);
    }
}
