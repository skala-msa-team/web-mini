package com.skala.team6.webmini.demo;

import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DemoUserPersistenceService {
    private static final String DEFAULT_NICKNAME = "Demo 사용자";

    private final UserRepository userRepository;

    public DemoUserPersistenceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity getOrCreate(String demoUserId) {
        return userRepository.findByDemoKey(demoUserId)
                .orElseGet(() -> userRepository.save(new UserEntity(demoUserId, DEFAULT_NICKNAME)));
    }
}
