package com.skala.team6.webmini.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "demo_key", nullable = false, unique = true, length = 100)
    private String demoKey;
    @Column(nullable = false, length = 50)
    private String nickname;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected UserEntity() {
    }

    public UserEntity(String demoKey, String nickname) {
        this.demoKey = demoKey;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }
}
