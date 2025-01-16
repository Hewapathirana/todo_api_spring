package com.ntloc.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class TaskResponsesDTO {
    private List<TaskResponseDTO> taskResponseDTOList;
    private Meta meta;
}
