package com.ntloc.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Boolean isCompleted;
}
