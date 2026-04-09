package com.breaze.genesis.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ValidationErrorResponse {

    private String message;
    private List<ValidationErrorItem> errors;
    private LocalDateTime timestamp;
}