package com.skala.team6.webmini.database.entity;

import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trials")
public class TrialEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private PostEntity post;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "trial_visibility")
    private Visibility visibility;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "trial_status")
    private TrialStatus status;
    @Column(name = "started_at")
    private OffsetDateTime startedAt;
    @Column(name = "phase_started_at")
    private OffsetDateTime phaseStartedAt;
    @Column(name = "phase_ends_at")
    private OffsetDateTime phaseEndsAt;
    @Column(name = "scheduled_end_at")
    private OffsetDateTime scheduledEndAt;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected TrialEntity() {
    }

    public TrialEntity(PostEntity post, UserEntity creator) {
        this.post = post;
        this.creator = creator;
        this.visibility = Visibility.PUBLIC;
        this.status = TrialStatus.PREPARING;
    }

    public Long getId() {
        return id;
    }

    public TrialStatus getStatus() {
        return status;
    }

    public OffsetDateTime getPhaseStartedAt() {
        return phaseStartedAt;
    }

    public OffsetDateTime getPhaseEndsAt() {
        return phaseEndsAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void startPhase(TrialStatus status, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        this.status = status;
        this.phaseStartedAt = startsAt;
        this.phaseEndsAt = endsAt;
    }

    public void complete(OffsetDateTime completedAt) {
        this.status = TrialStatus.ENDED;
        this.completedAt = completedAt;
    }
}
