package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.TrialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface TrialRepository extends JpaRepository<TrialEntity, Long> {
    boolean existsByPostId(Long postId);

    @EntityGraph(attributePaths = "post")
    Optional<TrialEntity> findDetailById(Long id);
}
