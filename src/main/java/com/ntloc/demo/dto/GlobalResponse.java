package com.ntloc.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
public class GlobalResponse <T> {
    private boolean success;
    private String message;
    private HttpStatus statusCode;
    private T data;
    private Meta meta;
}
