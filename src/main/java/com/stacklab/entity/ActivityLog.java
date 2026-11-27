package com.stacklab.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="activity_logs")
public class ActivityLog {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="task_id") private Task task;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="project_id") private Project project;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_id") private AppUser actor;
 @Column(nullable=false,length=80) private String action;
 @Column(name="field_name",length=80) private String fieldName;
 @Column(name="old_value",length=2000) private String oldValue;
 @Column(name="new_value",length=2000) private String newValue;
 @Column(nullable=false) private Instant occurredAt=Instant.now();
 protected ActivityLog(){}
 public ActivityLog(Task task,Project project,AppUser actor,String action,String fieldName,String oldValue,String newValue){this.task=task;this.project=project;this.actor=actor;this.action=action;this.fieldName=fieldName;this.oldValue=oldValue;this.newValue=newValue;}
 public Long getId(){return id;} public Task getTask(){return task;} public Project getProject(){return project;} public AppUser getActor(){return actor;} public String getAction(){return action;} public String getFieldName(){return fieldName;} public String getOldValue(){return oldValue;} public String getNewValue(){return newValue;} public Instant getOccurredAt(){return occurredAt;}
}
