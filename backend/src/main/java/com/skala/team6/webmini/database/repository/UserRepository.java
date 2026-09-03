package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByDemoKey(String demoKey);
}
