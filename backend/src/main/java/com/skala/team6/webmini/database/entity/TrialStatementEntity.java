package com.skala.team6.webmini.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trial_statements")
public class TrialStatementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trial_party_id", nullable = false, unique = true)
    private TrialPartyEntity trialParty;
    @Column(name = "incident_time", nullable = false, length = 100)
    private String incidentTime;
    @Column(nullable = false, columnDefinition = "text")
    private String situation;
    @Column(name = "counterpart_action", nullable = false, columnDefinition = "text")
    private String counterpartAction;
    @Column(name = "own_action", nullable = false, columnDefinition = "text")
    private String ownAction;
    @Column(name = "after_conversation", nullable = false, columnDefinition = "text")
    private String afterConversation;
    @Column(name = "desired_resolution", nullable = false, columnDefinition = "text")
    private String desiredResolution;
    @Column(name = "fact_summary", columnDefinition = "text")
    private String factSummary;
    @Column(name = "argument_text", columnDefinition = "text")
    private String argumentText;
    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected TrialStatementEntity() {
    }
}
