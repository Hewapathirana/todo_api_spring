package com.ntloc.demo.todo;

import com.ntloc.demo.AbstractTestcontainersTest;
import com.ntloc.demo.dto.GlobalResponse;
import com.ntloc.demo.dto.TaskRequestDTO;
import com.ntloc.demo.dto.TaskResponseDTO;
import com.ntloc.demo.dto.TaskResponsesDTO;
import com.ntloc.demo.model.Task;
import com.ntloc.demo.repo.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.springframework.http.HttpMethod.PUT;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoIntegrationTest extends AbstractTestcontainersTest {
    public static final String API_TASKS_PATH = "/v1/tasks";
    public static final String API_CUSTOMERS_PATH = "/api/v1/customers";
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    private TaskRepository taskRepository;
    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void testCreateTask() {
        // Given
        TaskRequestDTO taskRequestDTO = createTaskRequest("New Task", "Task Description");

        // When
        ResponseEntity<GlobalResponse<TaskResponseDTO>> response = testRestTemplate.exchange(
                API_TASKS_PATH, HttpMethod.POST,
                new HttpEntity<>(taskRequestDTO),
                new ParameterizedTypeReference<GlobalResponse<TaskResponseDTO>>() {}
        );

        // Then
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        GlobalResponse<TaskResponseDTO> globalResponse = response.getBody();
        Assertions.assertNotNull(globalResponse);
        Assertions.assertTrue(globalResponse.isSuccess());
        Assertions.assertNotNull(globalResponse.getData());
        Assertions.assertEquals("New Task", globalResponse.getData().getTitle());
    }
    @Test
    void testCreateTask_when_mandatory_fields_missing() {
        // Given
        TaskRequestDTO taskRequestDTO = createTaskRequest("", "Task Description");

        // When
        ResponseEntity<GlobalResponse<TaskResponseDTO>> response = testRestTemplate.exchange(
                API_TASKS_PATH, HttpMethod.POST,
                new HttpEntity<>(taskRequestDTO),
                new ParameterizedTypeReference<GlobalResponse<TaskResponseDTO>>() {}
        );

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetRecentTasks() {
        // Given
        Task task = Task.builder()
                .title("Task 1")
                .createdAt(LocalDateTime.now())
                .isCompleted(false)
                .description("Task Description")
                .build();
        taskRepository.save(task);

        // When
        ResponseEntity<GlobalResponse<TaskResponsesDTO>> response = testRestTemplate.exchange(
                API_TASKS_PATH, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<GlobalResponse<TaskResponsesDTO>>() {}
        );

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        GlobalResponse<TaskResponsesDTO> globalResponse = response.getBody();
        Assertions.assertNotNull(globalResponse);
        Assertions.assertFalse(globalResponse.getData().getTaskResponseDTOList().isEmpty());
        Assertions.assertTrue(globalResponse.isSuccess());
    }

    @Test
    void testMarkTaskAsCompleted() {
        // Given
        Task task = Task.builder()
                .title("Task 1")
                .createdAt(LocalDateTime.now())
                .isCompleted(false)
                .description("Task Description")
                .build();
        taskRepository.save(task);

        // When
        ResponseEntity<GlobalResponse<TaskResponseDTO>> response = testRestTemplate.exchange(
                API_TASKS_PATH + "/" + task.getId() + "/complete"
                , PUT,
                null,
                new ParameterizedTypeReference<GlobalResponse<TaskResponseDTO>>() {}
        );
        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        GlobalResponse<TaskResponseDTO> globalResponse = response.getBody();
        Assertions.assertNotNull(globalResponse);
        Assertions.assertTrue(globalResponse.isSuccess());
        Assertions.assertTrue(globalResponse.getData().getIsCompleted());
    }

    @Test
    void testMarkTaskAsCompleted_AlreadyCompleted() {
        // Given: Task that is already completed
        Task task = Task.builder()
                .title("Task 1")
                .createdAt(LocalDateTime.now())
                .isCompleted(true)
                .description("Task Description")
                .build();
        taskRepository.save(task);

        // When
        ResponseEntity<GlobalResponse<TaskResponseDTO>> response = testRestTemplate.exchange(
                API_TASKS_PATH + "/" + task.getId() + "/complete"
                , PUT,
                null,
                new ParameterizedTypeReference<GlobalResponse<TaskResponseDTO>>() {}
        );

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        GlobalResponse<TaskResponseDTO> globalResponse = response.getBody();
        Assertions.assertNotNull(globalResponse);
        Assertions.assertTrue(globalResponse.isSuccess());
        Assertions.assertTrue(globalResponse.getData().getIsCompleted());
    }

    @Test
    void testCreateTask_MissingRequiredFields() {
        // Given: Task with missing required description
        TaskRequestDTO taskRequestDTO = createTaskRequest("New Task", "");

        // When
        ResponseEntity<GlobalResponse<TaskResponseDTO>> response = testRestTemplate.exchange(
                API_TASKS_PATH, HttpMethod.POST,
                new HttpEntity<>(taskRequestDTO),
                new ParameterizedTypeReference<GlobalResponse<TaskResponseDTO>>() {}
        );

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GlobalResponse<?> globalResponse = response.getBody();
        Assertions.assertNotNull(globalResponse);
        Assertions.assertFalse(globalResponse.isSuccess());
    }

    private TaskRequestDTO createTaskRequest(String title, String description) {
        return TaskRequestDTO.builder()
                .title(title)
                .description(description)
                .build();
    }
}
