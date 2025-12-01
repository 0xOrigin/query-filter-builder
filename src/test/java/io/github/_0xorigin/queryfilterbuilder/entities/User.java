package io.github._0xorigin.queryfilterbuilder.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "AuthUser")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @UuidGenerator
    private UUID id;

    private String firstName;

    private String lastName;

    private Boolean isActive;

    private OffsetDateTime lastLogin;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = true, updatable = false)
    private User createdBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public OffsetDateTime getLastLogin() {
        return lastLogin;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setLastLogin(OffsetDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public enum Role {
        ADMIN,
        USER,
        GUEST
    }
}
