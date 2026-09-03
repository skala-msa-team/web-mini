package com.skala.team6.webmini;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;

@SpringBootTest(properties = "spring.autoconfigure.exclude="
        + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
        + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
        + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration")
class WebMiniBackendApplicationTests {

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private PostRepository postRepository;
    @MockitoBean
    private TrialRepository trialRepository;
    @MockitoBean
    private TrialPartyRepository trialPartyRepository;

    @Test
    void contextLoads() {
    }

}
