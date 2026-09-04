package com.skala.team6.webmini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebMiniBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebMiniBackendApplication.class, args);
    }

}
