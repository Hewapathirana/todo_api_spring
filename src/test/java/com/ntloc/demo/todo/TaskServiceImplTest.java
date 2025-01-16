package com.ntloc.demo.todo;

import com.ntloc.demo.dto.TaskRequestDTO;
import com.ntloc.demo.dto.TaskResponseDTO;
import com.ntloc.demo.dto.TaskResponsesDTO;
import com.ntloc.demo.exception.ResourceNotFoundException;
import com.ntloc.demo.model.Task;
import com.ntloc.demo.repo.TaskRepository;
import com.ntloc.demo.services.impl.TaskService;
import com.ntloc.demo.services.taskservices.TaskServiceImpl;
import com.ntloc.demo.util.TaskMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    TaskRepository taskRepository;
    TaskService underTest;
    @Mock
    TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        underTest = new TaskServiceImpl(taskRepository, taskMapper);
    }

    @AfterEach
    void tearDown() {
    }
    //======================== getRecentTasks tests start =============================

    @Test
    void getRecentTasks_ShouldReturn_Pending_Tasks_TaskResponsesDTO_WhenTasksExist() {
        // Given
        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .createdAt(LocalDateTime.now())
                .isCompleted(false)
                .description("Task Description")
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .createdAt(LocalDateTime.now())
                .isCompleted(false)
                .description("Task Description")
                .build();
        List<Task> pendingTasks = List.of(task1, task2);

        TaskResponseDTO taskResponse1 = TaskResponseDTO.builder()
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .build();

        TaskResponseDTO taskResponse2 = TaskResponseDTO.builder()
                .title("Task 2")
                .description("Task Description")
                .isCompleted(false)
                .build();
        List<TaskResponseDTO> taskResponseDTOList = List.of(taskResponse1, taskResponse2);

        // Mocking the repository and mapper
        when(taskRepository.findTop5ByIsCompletedFalseOrderByCreatedAtDesc()).thenReturn(pendingTasks);
        when(taskMapper.toDTOList(pendingTasks)).thenReturn(taskResponseDTOList);

        // When
        TaskResponsesDTO result = underTest.getRecentTasks();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTaskResponseDTOList().size());
        Assertions.assertEquals("Task 1", result.getTaskResponseDTOList().get(0).getTitle());
        Assertions.assertEquals("Task 2", result.getTaskResponseDTOList().get(1).getTitle());
        Assertions.assertEquals(false, result.getTaskResponseDTOList().get(0).getIsCompleted());
        Assertions.assertEquals(false, result.getTaskResponseDTOList().get(1).getIsCompleted());
    }
    @Test
    void getRecentTasks_ShouldReturnEmptyList_WhenNoTasksExist() {
        // Given
        List<Task> tasks = Collections.emptyList();
        List<TaskResponseDTO> taskResponseDTOList = Collections.emptyList();

        // Mocking the repository and mapper
        when(taskRepository.findTop5ByIsCompletedFalseOrderByCreatedAtDesc()).thenReturn(tasks);
        when(taskMapper.toDTOList(tasks)).thenReturn(taskResponseDTOList);

        // When
        TaskResponsesDTO result = underTest.getRecentTasks();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTaskResponseDTOList().isEmpty());
    }

    @Test
    void getRecentTasks_ShouldThrowCustomDataAccessException_WithCorrectMessage_WhenSQLErrorOccurs() {
        // Given
        when(taskRepository.findTop5ByIsCompletedFalseOrderByCreatedAtDesc()).thenThrow(new DataAccessException("SQL error") {});

        // When
        Throwable exception = assertThrows(DataAccessException.class, () -> underTest.getRecentTasks());

        // Then
        Assertions.assertEquals("Error fetching recent tasks from the database: SQL error", exception.getMessage());
    }
    @Test
    void getRecentTasks_ShouldMapTasksToDTOsCorrectly() {
        // Given
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .createdAt(LocalDateTime.now())
                .isCompleted(false)
                .description("Task Description")
                .build();
        TaskResponseDTO taskResponseDto = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .build();

        List<Task> tasks = List.of(task);
        List<TaskResponseDTO> taskResponseDTOList = List.of(taskResponseDto);

        // Mocking the repository and mapper
        when(taskRepository.findTop5ByIsCompletedFalseOrderByCreatedAtDesc()).thenReturn(tasks);
        when(taskMapper.toDTOList(tasks)).thenReturn(taskResponseDTOList);

        // When
        TaskResponsesDTO result = underTest.getRecentTasks();

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Task 1", result.getTaskResponseDTOList().get(0).getTitle());
    }
    //======================== getRecentTasks tests ends =============================

    //======================== create tasks tests starts =============================
    @Test
    void createTask_ShouldReturnTaskResponseDTO_WhenValidInput() {
        // Given
        TaskRequestDTO taskRequestDTO = TaskRequestDTO.builder()
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .build();

        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Mocking the repository and mapper
        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskResponseDTO);

        // When
        TaskResponseDTO result = underTest.createTask(taskRequestDTO);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Task 1", result.getTitle());
        Assertions.assertEquals("Task Description", result.getDescription());
        Assertions.assertFalse(result.getIsCompleted());
    }
    @Test
    void createTask_ShouldThrowNullPointerException_WhenTitleIsBlank() {
        // Given
        TaskRequestDTO taskRequestDTO = TaskRequestDTO.builder()
                .title(null)
                .description("Task Description")
                .isCompleted(false)
                .build();

        // When & Then
        Assertions.assertThrows(NullPointerException.class, () -> {
            underTest.createTask(taskRequestDTO);
        });
    }
    @Test
    void createTask_ShouldThrowDataAccessException_WhenDatabaseErrorOccurs() {
        // Given
        TaskRequestDTO taskRequestDTO = TaskRequestDTO.builder()
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .build();

        // Mocking repository to throw DataAccessException
        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(Task.builder().build());
        when(taskRepository.save(any(Task.class))).thenThrow(new DataAccessException("Database error") {});

        // When & Then
        Assertions.assertThrows(DataAccessException.class, () -> {
            underTest.createTask(taskRequestDTO);
        });
    }
    //======================== create tasks tests ends =============================

    //======================== update tasks tests stats ============================
    @Test
    void testMarkTaskAsCompleted_Success() {
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .id(taskId)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskResponseDTO);

        TaskResponseDTO result = underTest.markTaskAsCompleted(taskId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getIsCompleted());
        verify(taskRepository).save(any(Task.class));
    }
    @Test
    void testMarkTaskAsCompleted_TaskNotFound() {
        Long taskId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> underTest.markTaskAsCompleted(taskId));
    }
    @Test
    void testMarkTaskAsCompleted_SQLException() {
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenThrow(new DataAccessException("SQL Error") {});

        assertThrows(DataAccessException.class, () -> underTest.markTaskAsCompleted(taskId));
    }
    @Test
    void testMarkTaskAsCompleted_AlreadyCompleted() {
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(true)
                .createdAt(LocalDateTime.now())
                .build();
        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .id(taskId)
                .title("Task 1")
                .description("Task Description")
                .isCompleted(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskResponseDTO);

        TaskResponseDTO result = underTest.markTaskAsCompleted(taskId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getIsCompleted());
        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    void testMarkTaskAsCompleted_NullId() {
        assertThrows(ResourceNotFoundException.class, () -> underTest.markTaskAsCompleted(null));
    }
    //======================== update tasks tests ends =============================
}