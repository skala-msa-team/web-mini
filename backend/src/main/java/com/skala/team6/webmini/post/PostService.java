package com.skala.team6.webmini.post;

import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.demo.DemoUserPersistenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final DemoUserPersistenceService demoUserPersistenceService;
    private final PostRepository postRepository;

    public PostService(
            DemoUserPersistenceService demoUserPersistenceService,
            PostRepository postRepository
    ) {
        this.demoUserPersistenceService = demoUserPersistenceService;
        this.postRepository = postRepository;
    }

    @Transactional
    public PostEntity createPost(String demoUserId, CreatePostRequest request) {
        UserEntity author = demoUserPersistenceService.getOrCreate(demoUserId);
        PostEntity post = new PostEntity(
                author,
                request.title().trim(),
                request.content().trim(),
                request.relationshipType(),
                request.trialRequested()
        );
        return postRepository.save(post);
    }
}
