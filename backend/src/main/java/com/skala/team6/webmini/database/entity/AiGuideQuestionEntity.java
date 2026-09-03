package com.skala.team6.webmini.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_guide_questions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_guide_questions_party_sequence",
                columnNames = {"trial_party_id", "sequence_no"})
})
public class AiGuideQuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trial_party_id", nullable = false)
    private TrialPartyEntity trialParty;
    @Column(name = "sequence_no", nullable = false)
    private int sequenceNo;
    @Column(nullable = false, length = 1000)
    private String question;
    @Column(columnDefinition = "text")
    private String answer;
    @Column(name = "answered_at")
    private OffsetDateTime answeredAt;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected AiGuideQuestionEntity() {
    }
}
