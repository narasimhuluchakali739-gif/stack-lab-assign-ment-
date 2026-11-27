package com.stacklab.entity;
import jakarta.persistence.*;
import java.time.Instant;
@MappedSuperclass
public abstract class AuditedEntity {
 @Column(name="created_at",nullable=false,updatable=false) protected Instant createdAt;
 @Column(name="updated_at",nullable=false) protected Instant updatedAt;
 @PrePersist protected void onCreate(){createdAt=updatedAt=Instant.now();}
 @PreUpdate protected void onUpdate(){updatedAt=Instant.now();}
 public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
