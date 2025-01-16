package com.ntloc.demo.controller;

import com.ntloc.demo.dto.GlobalResponse;
import com.ntloc.demo.dto.TaskRequestDTO;
import com.ntloc.demo.dto.TaskResponseDTO;
import com.ntloc.demo.dto.TaskResponsesDTO;
import com.ntloc.demo.services.impl.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public GlobalResponse<TaskResponsesDTO> getRecentTasks() {
        return GlobalResponse.<TaskResponsesDTO>builder()
                .success(true)
                .message("Request successful")
                .statusCode(HttpStatus.OK)
                .data(taskService.getRecentTasks())
                .build();
    }

    @PostMapping
    public GlobalResponse<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        return GlobalResponse.<TaskResponseDTO>builder()
                .success(true)
                .message("Request successful")
                .statusCode(HttpStatus.CREATED)
                .data(taskService.createTask(taskRequestDTO))
                .build();
    }

    @PatchMapping("/{id}/complete")
    public GlobalResponse<TaskResponseDTO> markTaskAsCompleted(@PathVariable Long id) {
        return GlobalResponse.<TaskResponseDTO>builder()
                .success(true)
                .message("Request successful")
                .statusCode(HttpStatus.CREATED)
                .data(taskService.markTaskAsCompleted(id))
                .build();
    }
}
