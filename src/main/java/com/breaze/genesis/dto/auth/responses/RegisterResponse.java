package com.breaze.genesis.dto.auth.responses;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RegisterResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private Boolean active;
    private Integer tokenBalance;
    private LocalDateTime createdAt;
}