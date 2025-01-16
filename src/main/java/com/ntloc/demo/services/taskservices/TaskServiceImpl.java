package com.ntloc.demo.services.taskservices;

import com.ntloc.demo.dto.TaskRequestDTO;
import com.ntloc.demo.dto.TaskResponseDTO;
import com.ntloc.demo.dto.TaskResponsesDTO;
import com.ntloc.demo.exception.ResourceNotFoundException;
import com.ntloc.demo.model.Task;
import com.ntloc.demo.repo.TaskRepository;
import com.ntloc.demo.services.impl.TaskService;
import com.ntloc.demo.util.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }
    public TaskResponsesDTO getRecentTasks() {
        try {
            log.info("Fetching recent tasks...");
            return TaskResponsesDTO.builder()
                    .taskResponseDTOList(this.taskMapper.toDTOList(taskRepository.findTop5ByIsCompletedFalseOrderByCreatedAtDesc()))
                    .build();
        } catch (DataAccessException e) {
            log.error("SQL Error occurred while fetching recent tasks: ", e);
            // Pass custom message for this exception
            throw new DataAccessException("Error fetching recent tasks from the database: " + e.getMessage()) {};
        }
    }

    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        try {
            log.info("Creating task with request: {}", taskRequestDTO);
            Task task = taskRepository.save(taskMapper.toEntity(taskRequestDTO));
            task.setIsCompleted(false);
            log.info("Task created successfully with ID: {}", task.getId());
            return taskMapper.toDTO(task);
        } catch (DataAccessException e) {
            log.error("SQL Error occurred while creating task: ", e);
            // Pass custom message for this exception
            throw new DataAccessException("Error creating task in the database: " + e.getMessage()) {};
        }
    }

    public TaskResponseDTO markTaskAsCompleted(Long id) {
        try {
            log.info("Marking task with ID: {} as completed", id);
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
            if (!task.getIsCompleted()) {
                task.setIsCompleted(true);
                task = taskRepository.save(task);
                log.info("Task with ID: {} marked as completed", id);
            }
            return taskMapper.toDTO(task);
        } catch (DataAccessException e) {
            log.error("SQL Error occurred while marking task as completed with ID: {}", id, e);
            // Pass custom message for this exception
            throw new DataAccessException("Error marking task as completed in the database: " + e.getMessage()) {};
        }
    }
}
