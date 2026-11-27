package com.stacklab.repository;
import com.stacklab.entity.ActivityLog; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ActivityRepository extends JpaRepository<ActivityLog,Long>{List<ActivityLog> findByTaskIdOrderByOccurredAtDesc(Long id); List<ActivityLog> findByProjectIdOrderByOccurredAtDesc(Long id);}
