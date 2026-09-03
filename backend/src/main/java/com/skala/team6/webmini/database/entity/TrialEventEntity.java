package com.skala.team6.webmini.database.entity;

import com.skala.team6.webmini.common.model.TrialSpeaker;
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
@Table(name = "trial_events", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trial_events_trial_sequence", columnNames = {"trial_id", "sequence_no"})
})
public class TrialEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trial_id", nullable = false)
    private TrialEntity trial;
    @Column(name = "sequence_no", nullable = false)
    private long sequenceNo;
    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "trial_speaker")
    private TrialSpeaker speaker;
    @Column(columnDefinition = "text")
    private String content;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected TrialEventEntity() {
    }

    public TrialEventEntity(TrialEntity trial, long sequenceNo, String eventType,
                            TrialSpeaker speaker, String content, String payload) {
        this.trial = trial;
        this.sequenceNo = sequenceNo;
        this.eventType = eventType;
        this.speaker = speaker;
        this.content = content;
        this.payload = payload;
    }

    public long getSequenceNo() {
        return sequenceNo;
    }
}
