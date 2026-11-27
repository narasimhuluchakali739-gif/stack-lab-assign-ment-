package com.stacklab.dto;
import com.stacklab.entity.*; import jakarta.validation.constraints.*; import java.time.Instant;
public final class ApiDtos {
 private ApiDtos(){}
 public record RegisterRequest(@NotBlank @Size(max=120) String name,@NotBlank @Email String email,@NotBlank @Size(min=8,max=100) String password){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record AuthResponse(String token,String tokenType,long expiresIn,UserView user){}
 public record UserView(Long id,String name,String email,Role role,boolean active) { public static UserView of(AppUser u){return new UserView(u.getId(),u.getName(),u.getEmail(),u.getRole(),u.isActive());} }
 public record UserCreate(@NotBlank String name,@NotBlank @Email String email,@NotBlank @Size(min=8) String password,@NotNull Role role){}
 public record UserUpdate(String name,@Email String email,Role role,Boolean active){}
 public record ProjectInput(@NotBlank String name,String description,Long managerId,Boolean archived){}
 public record ProjectView(Long id,String name,String description,Long managerId,String managerName,boolean archived,Instant createdAt,Instant updatedAt){public static ProjectView of(Project p){return new ProjectView(p.getId(),p.getName(),p.getDescription(),p.getManager()==null?null:p.getManager().getId(),p.getManager()==null?null:p.getManager().getName(),p.isArchived(),p.getCreatedAt(),p.getUpdatedAt());}}
 public record TaskInput(String title,String description,Long assigneeId,Long projectId,Instant deadline,Priority priority,TaskStatus status,@NotNull Long version){}
 public record TaskCreate(@NotBlank String title,String description,@NotNull Long assigneeId,@NotNull Long projectId,@NotNull Instant deadline,@NotNull Priority priority,TaskStatus status){}
 public record TaskView(Long id,String title,String description,Long assigneeId,String assignee,Long projectId,Instant deadline,Priority priority,TaskStatus status,Long version,Instant completedAt,boolean isOverdue){public static TaskView of(Task t){return new TaskView(t.getId(),t.getTitle(),t.getDescription(),t.getAssignee().getId(),t.getAssignee().getName(),t.getProject().getId(),t.getDeadline(),t.getPriority(),t.getStatus(),t.getVersion(),t.getCompletedAt(),t.isOverdue());}}
 public record ErrorView(Instant timestamp,int status,String error,String message,String path){}
 public record ActivityView(Long id,Long taskId,Long projectId,Long actorId,String action,String field,String oldValue,String newValue,Instant occurredAt){public static ActivityView of(com.stacklab.entity.ActivityLog a){return new ActivityView(a.getId(),a.getTask()==null?null:a.getTask().getId(),a.getProject()==null?null:a.getProject().getId(),a.getActor()==null?null:a.getActor().getId(),a.getAction(),a.getFieldName(),a.getOldValue(),a.getNewValue(),a.getOccurredAt());}}
 public record NotificationView(Long id,Long taskId,String message,boolean read,Instant createdAt){public static NotificationView of(com.stacklab.entity.Notification n){return new NotificationView(n.getId(),n.getTask()==null?null:n.getTask().getId(),n.getMessage(),n.isRead(),n.getCreatedAt());}}
}
