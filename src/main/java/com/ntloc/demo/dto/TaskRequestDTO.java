package com.ntloc.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Builder
@Getter
@Setter
public class TaskRequestDTO {
    @NotBlank(message = "Title is required and cannot be blank.")
    @Size(max = 100, message = "Title must not exceed 100 characters.")
    private String title;
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;
    @NotNull(message = "isCompleted field is required.")
    private Boolean isCompleted;
}
