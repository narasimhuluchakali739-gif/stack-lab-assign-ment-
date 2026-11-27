package com.stacklab.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="task_id") private Task task;
 @Column(nullable=false,length=500) private String message;
 @Column(name="is_read",nullable=false) private boolean read=false;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 protected Notification(){}
 public Notification(AppUser user,Task task,String message){this.user=user;this.task=task;this.message=message;}
 public Long getId(){return id;} public AppUser getUser(){return user;} public Task getTask(){return task;} public String getMessage(){return message;} public boolean isRead(){return read;} public Instant getCreatedAt(){return createdAt;} public void markRead(){read=true;}
}
