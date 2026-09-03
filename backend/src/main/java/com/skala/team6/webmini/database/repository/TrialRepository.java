package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.TrialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;

public interface TrialRepository extends JpaRepository<TrialEntity, Long> {
    boolean existsByPostId(Long postId);

    @EntityGraph(attributePaths = "post")
    Optional<TrialEntity> findDetailById(Long id);

    @EntityGraph(attributePaths = "post")
    Page<TrialEntity> findByVisibilityAndStatusIn(
            Visibility visibility,
            List<TrialStatus> statuses,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "post")
    Page<TrialEntity> findByVisibilityAndStatus(
            Visibility visibility,
            TrialStatus status,
            Pageable pageable
    );
}
