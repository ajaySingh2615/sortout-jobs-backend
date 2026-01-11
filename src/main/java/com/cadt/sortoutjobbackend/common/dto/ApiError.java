package com.cadt.sortoutjobbackend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private boolean success;
    private String message;
    private String errorCode;
    private List<String> errors;
    private String path;
    private LocalDateTime timestamp;

    public static ApiError of(String errorCode, String message) {
        return ApiError.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiError of(String errorCode, String message, List<String> errors) {
        return ApiError.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
