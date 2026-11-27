package com.stacklab.repository;
import com.stacklab.entity.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.Instant; import java.util.*;
public interface TaskRepository extends JpaRepository<Task,Long>,JpaSpecificationExecutor<Task>{
 @Query("select t from Task t join fetch t.assignee join fetch t.project where t.id=:id") Optional<Task> findDetailedById(@Param("id") Long id);
 @Query("select t from Task t where t.status<>com.stacklab.entity.TaskStatus.COMPLETED and t.deadline < :now and t.overdueFlag=false") List<Task> findNewlyOverdue(@Param("now") Instant now);
 long countByStatus(TaskStatus status);
 @Query("select count(t) from Task t where t.status<>com.stacklab.entity.TaskStatus.COMPLETED and t.deadline < :now") long countOverdue(@Param("now") Instant now);
 @Query("select t.assignee.id, t.assignee.name, count(t) from Task t group by t.assignee.id,t.assignee.name") List<Object[]> countsByEmployee();
 @Query("select t.status,count(t) from Task t group by t.status") List<Object[]> countsByStatus();
 @Query("select t.priority,count(t) from Task t group by t.priority") List<Object[]> countsByPriority();
 @Query("select count(t) from Task t where t.assignee.id=:uid") long assignedCount(@Param("uid")Long uid);
 @Query("select count(t) from Task t where t.assignee.id=:uid and t.status=com.stacklab.entity.TaskStatus.COMPLETED") long completedCount(@Param("uid")Long uid);
 @Query("select count(t) from Task t where t.assignee.id=:uid and t.status=com.stacklab.entity.TaskStatus.COMPLETED and t.completedAt<=t.deadline") long onTimeCount(@Param("uid")Long uid);
 @Query("select count(t) from Task t where t.assignee.id=:uid and t.status=com.stacklab.entity.TaskStatus.COMPLETED and t.completedAt>t.deadline") long lateCount(@Param("uid")Long uid);
 @Query("select avg(timestampdiff(SECOND,t.createdAt,t.completedAt)) from Task t where t.assignee.id=:uid and t.completedAt is not null") Double avgCompletionSeconds(@Param("uid")Long uid);
}
