package com.stacklab.entity;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="tasks")
public class Task extends AuditedEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=180) private String title;
 @Column(length=3000) private String description;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="assignee_id",nullable=false) private AppUser assignee;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="project_id",nullable=false) private Project project;
 @Column(nullable=false) private Instant deadline;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private Priority priority;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private TaskStatus status;
 @Version @Column(nullable=false) private Long version;
 @Column(name="completed_at") private Instant completedAt;
 @Column(name="overdue_flag",nullable=false) private boolean overdueFlag=false;
 protected Task(){}
 public Task(String title,String description,AppUser assignee,Project project,Instant deadline,Priority priority,TaskStatus status){this.title=title;this.description=description;this.assignee=assignee;this.project=project;this.deadline=deadline;this.priority=priority;this.status=status;updateCompletion();}
 public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public AppUser getAssignee(){return assignee;} public Project getProject(){return project;} public Instant getDeadline(){return deadline;} public Priority getPriority(){return priority;} public TaskStatus getStatus(){return status;} public Long getVersion(){return version;} public Instant getCompletedAt(){return completedAt;} public boolean isOverdue(){return status!=TaskStatus.COMPLETED&&deadline.isBefore(Instant.now());} public boolean isOverdueFlag(){return overdueFlag;}
 public void update(String title,String description,AppUser assignee,Project project,Instant deadline,Priority priority,TaskStatus status){if(title!=null)this.title=title;if(description!=null)this.description=description;if(assignee!=null)this.assignee=assignee;if(project!=null)this.project=project;if(deadline!=null)this.deadline=deadline;if(priority!=null)this.priority=priority;if(status!=null)this.status=status;updateCompletion();}
 private void updateCompletion(){if(status==TaskStatus.COMPLETED){if(completedAt==null)completedAt=Instant.now();}else completedAt=null;}
 public void setOverdueFlag(boolean flag){overdueFlag=flag;}
}
