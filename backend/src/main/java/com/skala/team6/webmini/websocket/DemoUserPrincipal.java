package com.skala.team6.webmini.websocket;

import java.security.Principal;

public record DemoUserPrincipal(String name) implements Principal {

    @Override
    public String getName() {
        return name;
    }
}
