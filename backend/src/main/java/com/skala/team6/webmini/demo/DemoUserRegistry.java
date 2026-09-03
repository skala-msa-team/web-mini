package com.skala.team6.webmini.demo;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DemoUserRegistry {

    private final Map<String, DemoUser> users = new ConcurrentHashMap<>();

    public DemoUser getOrCreate(String demoUserId) {
        return users.computeIfAbsent(demoUserId, DemoUser::new);
    }
}
