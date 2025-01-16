package com.ntloc.demo.util;

import com.ntloc.demo.dto.TaskRequestDTO;
import com.ntloc.demo.dto.TaskResponseDTO;
import com.ntloc.demo.model.Task;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskMapper {
    // Convert Task to TaskResponseDTO
    public TaskResponseDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .isCompleted(task.getIsCompleted())
                .build();
    }

    // Convert TaskResponseDTO to Task
    public Task toEntity(TaskRequestDTO taskRequestDTO) {
        if (taskRequestDTO == null) {
            return null;
        }
        return Task.builder()
                .title(taskRequestDTO.getTitle())
                .description(taskRequestDTO.getDescription())
                .isCompleted(taskRequestDTO.getIsCompleted())
                .build();
    }
    public List<TaskResponseDTO> toDTOList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
