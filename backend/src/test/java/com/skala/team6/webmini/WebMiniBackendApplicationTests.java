package com.skala.team6.webmini;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.UserRepository;

@SpringBootTest(properties = "spring.autoconfigure.exclude="
        + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
        + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
        + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration")
class WebMiniBackendApplicationTests {

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private PostRepository postRepository;

    @Test
    void contextLoads() {
    }

}
