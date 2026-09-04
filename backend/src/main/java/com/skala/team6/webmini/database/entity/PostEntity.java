package com.skala.team6.webmini.database.entity;

import com.skala.team6.webmini.common.model.RelationshipType;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, columnDefinition = "text")
    private String content;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "relationship_type", nullable = false, columnDefinition = "relationship_type")
    private RelationshipType relationshipType;
    @Column(name = "trial_requested", nullable = false)
    private boolean trialRequested;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected PostEntity() {
    }

    public PostEntity(UserEntity author, String title, String content,
                      RelationshipType relationshipType, boolean trialRequested) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.relationshipType = relationshipType;
        this.trialRequested = trialRequested;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public boolean isTrialRequested() {
        return trialRequested;
    }
}
