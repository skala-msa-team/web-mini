package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.TrialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.time.OffsetDateTime;

import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;

public interface TrialRepository extends JpaRepository<TrialEntity, Long> {
    boolean existsByPostId(Long postId);

    @EntityGraph(attributePaths = "post")
    Optional<TrialEntity> findDetailById(Long id);

    Optional<TrialEntity> findByIdAndVisibilityAndStatusNot(
            Long id,
            Visibility visibility,
            TrialStatus excludedStatus
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select trial from TrialEntity trial where trial.id = :trialId")
    Optional<TrialEntity> findByIdForUpdate(@Param("trialId") Long trialId);

    @Query("select trial.id from TrialEntity trial "
            + "where trial.status in :statuses and trial.phaseEndsAt <= :now")
    List<Long> findExpiredTrialIds(@Param("statuses") List<TrialStatus> statuses,
                                   @Param("now") OffsetDateTime now);

    @Query("select trial.id from TrialEntity trial where trial.status = :status")
    List<Long> findTrialIdsByStatus(@Param("status") TrialStatus status);

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
