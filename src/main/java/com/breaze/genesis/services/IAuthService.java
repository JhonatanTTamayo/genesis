package com.breaze.genesis.services;

import com.breaze.genesis.dto.auth.requests.LoginRequest;
import com.breaze.genesis.dto.auth.requests.RegisterRequest;
import com.breaze.genesis.dto.auth.responses.LoginResponse;
import com.breaze.genesis.dto.auth.responses.RegisterResponse;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
