package com.breaze.genesis.dto.auth.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String type;
    private Integer expiresIn;
}