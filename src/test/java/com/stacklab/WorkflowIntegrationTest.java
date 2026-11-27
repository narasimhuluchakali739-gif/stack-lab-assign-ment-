package com.stacklab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklab.repository.NotificationRepository;
import com.stacklab.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.jwt.secret=test-only-secret-not-used-outside-tests-0123456789",
        "app.demo.seed-password=DemoPassword123!",
        "spring.datasource.url=jdbc:h2:mem:workflow-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE"
})
@AutoConfigureMockMvc
class WorkflowIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired NotificationRepository notifications;
    @Autowired TaskService taskService;

    @Test
    void seededManagerEmployeeTaskReportingAndDeactivationFlow() throws Exception {
        String manager = login("maya@stacklab.local");
        String admin = login("admin@stacklab.local");
        String employee = login("eli@stacklab.local");
        String otherEmployee = login("sam@stacklab.local");

        MvcResult projectResult = mvc.perform(post("/api/projects").header("Authorization", bearer(manager))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Integration project\",\"description\":\"workflow\",\"managerId\":2}"))
                .andExpect(status().isCreated()).andReturn();
        long projectId = mapper.readTree(projectResult.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get("/api/projects/{id}", projectId).header("Authorization", bearer(manager)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Integration project"));
        mvc.perform(put("/api/projects/{id}", projectId).header("Authorization", bearer(manager))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Integration project\",\"description\":\"updated\",\"managerId\":2,\"archived\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.description").value("updated"));

        mvc.perform(post("/api/tasks").header("Authorization", bearer(employee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Forbidden task\",\"assigneeId\":3,\"projectId\":1,\"deadline\":\"2026-12-31T00:00:00Z\",\"priority\":\"LOW\"}"))
                .andExpect(status().isForbidden());

        MvcResult created = mvc.perform(post("/api/tasks").header("Authorization", bearer(manager))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Integration task\",\"description\":\"workflow\",\"assigneeId\":3,\"projectId\":" + projectId + ",\"deadline\":\"2026-12-31T00:00:00Z\",\"priority\":\"HIGH\",\"status\":\"TO_DO\"}"))
                .andExpect(status().isCreated()).andReturn();
        JsonNode task = mapper.readTree(created.getResponse().getContentAsString());
        long taskId = task.get("id").asLong();
        long version = task.get("version").asLong();

        mvc.perform(get("/api/tasks/{id}", taskId).header("Authorization", bearer(otherEmployee)))
                .andExpect(status().isForbidden());
        MvcResult progress = mvc.perform(put("/api/tasks/{id}", taskId).header("Authorization", bearer(employee))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":" + version + ",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk()).andReturn();
        version = mapper.readTree(progress.getResponse().getContentAsString()).get("version").asLong();
        mvc.perform(put("/api/tasks/{id}", taskId).header("Authorization", bearer(employee))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":" + version + ",\"title\":\"forbidden edit\"}"))
                .andExpect(status().isForbidden());
        org.junit.jupiter.api.Assertions.assertTrue(notifications.findByUserIdOrderByCreatedAtDesc(3L).stream()
                .anyMatch(n -> n.getMessage().equals("You have been assigned task: Integration task")));
        mvc.perform(get("/api/notifications").header("Authorization", bearer(employee)))
                .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Integration task")));

        MvcResult completed = mvc.perform(put("/api/tasks/{id}", taskId).header("Authorization", bearer(manager))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":" + version + ",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completedAt").isNotEmpty()).andReturn();
        long completedVersion = mapper.readTree(completed.getResponse().getContentAsString()).get("version").asLong();
        org.junit.jupiter.api.Assertions.assertTrue(completedVersion > version);

        mvc.perform(put("/api/tasks/{id}", taskId).header("Authorization", bearer(manager))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":" + version + ",\"status\":\"REVIEW\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("reload")));
        mvc.perform(get("/api/tasks/{id}/activity", taskId).header("Authorization", bearer(employee)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[?(@.field=='status')]").isNotEmpty());

        mvc.perform(get("/api/analytics").header("Authorization", bearer(manager)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalTasks").isNumber()).andExpect(jsonPath("$.completedTasks").isNumber());
        mvc.perform(get("/api/analytics/employees/3/performance").header("Authorization", bearer(manager)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completionRatePercent").isNumber()).andExpect(jsonPath("$.averageCompletionSeconds").exists());

        org.junit.jupiter.api.Assertions.assertTrue(taskService.flagOverdue() >= 1);
        org.junit.jupiter.api.Assertions.assertEquals(0, taskService.flagOverdue());
        mvc.perform(put("/api/projects/{id}", projectId).header("Authorization", bearer(manager))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Integration project\",\"description\":\"updated\",\"managerId\":2,\"archived\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.archived").value(true));

        mvc.perform(delete("/api/users/4").header("Authorization", bearer(admin)))
                .andExpect(status().isNoContent());
        String samLogin = "{\"email\":\"sam@stacklab.local\",\"password\":\"DemoPassword123!\"}";
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(samLogin))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/tasks").header("Authorization", bearer(otherEmployee)))
                .andExpect(status().isUnauthorized());
    }

    private String login(String email) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"DemoPassword123!\"}";
        MvcResult result = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String bearer(String token) { return "Bearer " + token; }
}
