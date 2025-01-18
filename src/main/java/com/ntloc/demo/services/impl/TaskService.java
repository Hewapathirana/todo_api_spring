package com.ntloc.demo.services.impl;

import com.ntloc.demo.dto.TaskRequestDTO;
import com.ntloc.demo.dto.TaskResponseDTO;
import com.ntloc.demo.dto.TaskResponsesDTO;

public interface TaskService {
    /**
     * Fetches the top 5 recent incomplete tasks, ordered by creation time in descending order.
     *
     * @return a  TaskResponsesDTO objects representing recent incomplete tasks.
     */
    TaskResponsesDTO getRecentTasks();

    /**
     * Creates a new task.
     *
     * @param taskRequestDTO the task entity to be created.
     * @return a TaskRequestDTO object representing the created task.
     */
    TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO);

    /**
     * Marks a task as completed by its ID.
     *
     * @param taskId the ID of the task to be marked as completed.
     */
    TaskResponseDTO markTaskAsCompleted(Long taskId);
}
