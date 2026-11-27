package com.stacklab;
import com.stacklab.entity.*;import org.junit.jupiter.api.Test;import java.time.Instant;import static org.junit.jupiter.api.Assertions.*;
class TaskWorkflowTest{
 @Test void completionTimestampIsSetAndClearedOnStatusChange(){AppUser u=new AppUser("A","a@example.test","hash",Role.EMPLOYEE);Project p=new Project("P",null,null);Task t=new Task("T",null,u,p,Instant.now().plusSeconds(60),Priority.LOW,TaskStatus.TO_DO);assertNull(t.getCompletedAt());t.update(null,null,null,null,null,null,TaskStatus.COMPLETED);assertNotNull(t.getCompletedAt());t.update(null,null,null,null,null,null,TaskStatus.IN_PROGRESS);assertNull(t.getCompletedAt());}
 @Test void overdueStateIsCalculatedAndCompletionOverridesIt(){AppUser u=new AppUser("A","a@example.test","hash",Role.EMPLOYEE);Project p=new Project("P",null,null);Task t=new Task("T",null,u,p,Instant.now().minusSeconds(30),Priority.LOW,TaskStatus.IN_PROGRESS);assertTrue(t.isOverdue());t.update(null,null,null,null,null,null,TaskStatus.COMPLETED);assertFalse(t.isOverdue());}
}
