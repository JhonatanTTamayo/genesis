package com.breaze.genesis.controller;

import com.breaze.genesis.dto.response.UserProfileResponse;
import com.breaze.genesis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getMyProfile(email);
    }
}