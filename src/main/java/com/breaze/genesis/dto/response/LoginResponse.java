package com.breaze.genesis.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String token;
    private String type;
    private Integer expiresIn;
}