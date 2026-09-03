package com.skala.team6.webmini.post;

import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class PostCreationAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    void createsPostsAndReusesDemoUser() throws Exception {
        String demoUserId = UUID.randomUUID().toString();

        createPost(demoUserId, " 첫 게시글 ")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("첫 게시글"));
        createPost(demoUserId, "두 번째 게시글")
                .andExpect(status().isCreated());

        assertThat(userRepository.findByDemoKey(demoUserId)).isPresent();
        assertThat(postRepository.findAll())
                .filteredOn(post -> post.getAuthor().getDemoKey().equals(demoUserId))
                .extracting(PostEntity::getTitle)
                .containsExactlyInAnyOrder("첫 게시글", "두 번째 게시글");
    }

    @Test
    void rejectsMissingDemoUserAndInvalidPost() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .content(validRequest("게시글")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DEMO_USER_REQUIRED"));

        createPost(UUID.randomUUID().toString(), "   ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        createPost(UUID.randomUUID().toString(), "x".repeat(151))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(post("/api/v1/posts")
                        .header("X-Demo-User-Id", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"title":"게시글","content":"%s","relationshipType":"COUPLE","trialRequested":true}
                                """.formatted("x".repeat(5001))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(post("/api/v1/posts")
                        .header("X-Demo-User-Id", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"title":"게시글","content":"내용","relationshipType":"INVALID","trialRequested":true}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private org.springframework.test.web.servlet.ResultActions createPost(
            String demoUserId,
            String title
    ) throws Exception {
        return mockMvc.perform(post("/api/v1/posts")
                .header("X-Demo-User-Id", demoUserId)
                .contentType("application/json")
                .content(validRequest(title)));
    }

    private String validRequest(String title) {
        return """
                {
                  "title": "%s",
                  "content": "연락 문제로 갈등이 생겼습니다.",
                  "relationshipType": "COUPLE",
                  "trialRequested": true
                }
                """.formatted(title);
    }
}
