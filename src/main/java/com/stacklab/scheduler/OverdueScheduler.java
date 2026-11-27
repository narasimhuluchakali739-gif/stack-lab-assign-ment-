package com.stacklab.scheduler;
import com.stacklab.service.TaskService;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;
@Component public class OverdueScheduler{private final TaskService tasks;public OverdueScheduler(TaskService tasks){this.tasks=tasks;}@Scheduled(fixedDelayString="${app.overdue-delay-ms:60000}") public void flagOverdue(){tasks.flagOverdue();}}
