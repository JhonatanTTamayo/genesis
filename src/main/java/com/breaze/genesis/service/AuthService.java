package com.breaze.genesis.service;

import com.breaze.genesis.dto.request.LoginRequest;
import com.breaze.genesis.dto.request.RegisterRequest;
import com.breaze.genesis.dto.response.LoginResponse;
import com.breaze.genesis.dto.response.RegisterResponse;
import com.breaze.genesis.entity.Role;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exception.BusinessException;
import com.breaze.genesis.exception.ForbiddenException;
import com.breaze.genesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .active(true)
                .tokenBalance(0) // saldo inicia en 0
                .build();

        User saved = userRepository.save(user);

        return RegisterResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .active(saved.getActive())
                .tokenBalance(saved.getTokenBalance())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ForbiddenException("El usuario está inactivo");
        }

        String token = jwtService.generateToken(
                userDetailsService.loadUserByUsername(user.getEmail()),
                Map.of(
                        "role", user.getRole().name(),
                        "userId", user.getId()
                )
        );

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn((int) (expirationMs / 1000))
                .build();
    }
}