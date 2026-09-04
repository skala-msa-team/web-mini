package com.skala.team6.webmini.database.entity;

import com.skala.team6.webmini.common.model.TrialSide;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_votes_trial_voter", columnNames = {"trial_id", "voter_id"})
})
public class VoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trial_id", nullable = false)
    private TrialEntity trial;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voter_id", nullable = false)
    private UserEntity voter;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "selected_side", nullable = false, columnDefinition = "trial_side")
    private TrialSide selectedSide;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected VoteEntity() {
    }

    public VoteEntity(TrialEntity trial, UserEntity voter, TrialSide selectedSide) {
        this.trial = trial;
        this.voter = voter;
        this.selectedSide = selectedSide;
    }

    public TrialSide getSelectedSide() {
        return selectedSide;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
