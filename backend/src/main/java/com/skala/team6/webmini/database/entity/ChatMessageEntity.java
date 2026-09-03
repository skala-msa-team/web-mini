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
@Table(name = "chat_messages", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_messages_trial_sequence", columnNames = {"trial_id", "sequence_no"})
})
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trial_id", nullable = false)
    private TrialEntity trial;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;
    @Column(name = "sequence_no", nullable = false)
    private long sequenceNo;
    @Column(nullable = false, length = 500)
    private String content;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected ChatMessageEntity() {
    }

    public ChatMessageEntity(TrialEntity trial, UserEntity sender, long sequenceNo, String content) {
        this.trial = trial;
        this.sender = sender;
        this.sequenceNo = sequenceNo;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public long getSequenceNo() {
        return sequenceNo;
    }

    public TrialEntity getTrial() {
        return trial;
    }

    public UserEntity getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
