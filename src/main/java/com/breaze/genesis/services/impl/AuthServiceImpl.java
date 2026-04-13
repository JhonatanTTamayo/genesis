package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.auth.requests.LoginRequest;
import com.breaze.genesis.dto.auth.requests.RegisterRequest;
import com.breaze.genesis.dto.auth.responses.LoginResponse;
import com.breaze.genesis.dto.auth.responses.RegisterResponse;
import com.breaze.genesis.entity.Role;
import com.breaze.genesis.entity.tokens.TokenWallet;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exceptions.BusinessException;
import com.breaze.genesis.exceptions.ForbiddenException;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.security.JwtUtil;
import com.breaze.genesis.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
        private final ITokenWalletRepository tokenWalletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    private static final Integer INITIAL_TOKEN_BALANCE = 0;
    
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
                        throw new BusinessException("Email is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .active(true)
                .build();

        User saved = userRepository.save(user);

        TokenWallet wallet = tokenWalletRepository.save(
                TokenWallet.builder()
                        .user(saved)
                        .tokensAvailable(INITIAL_TOKEN_BALANCE)
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        return RegisterResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .active(saved.getActive())
                .tokenBalance(wallet.getTokensAvailable())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getActive())) {
                        throw new ForbiddenException("User is inactive");
        }

        String token = jwtUtil.generateToken(
                userDetailsService.loadUserByUsername(user.getEmail()),
                Map.of(
                        "role", user.getRole().name()
                )
        );

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn((int) (expirationMs / 1000))
                .build();
    }
}