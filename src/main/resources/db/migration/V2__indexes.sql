CREATE INDEX idx_tasks_assignee_status ON tasks(assignee_id,status);
CREATE INDEX idx_tasks_project_status ON tasks(project_id,status);
CREATE INDEX idx_tasks_deadline_status ON tasks(deadline,status);
CREATE INDEX idx_tasks_priority_deadline ON tasks(priority,deadline);
CREATE INDEX idx_tasks_created_id ON tasks(created_at,id);
CREATE INDEX idx_activity_task_time ON activity_logs(task_id,occurred_at DESC);
CREATE INDEX idx_activity_project_time ON activity_logs(project_id,occurred_at DESC);
CREATE INDEX idx_notifications_user_read_time ON notifications(user_id,is_read,created_at DESC);
CREATE INDEX idx_projects_manager_archived ON projects(manager_id,archived);
