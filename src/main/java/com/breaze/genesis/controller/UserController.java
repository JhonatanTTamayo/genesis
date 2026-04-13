package com.breaze.genesis.controller;

import com.breaze.genesis.dto.auth.responses.UserProfileResponse;
import com.breaze.genesis.dto.users.requests.UserListQueryRequest;
import com.breaze.genesis.dto.users.requests.UpdateUserStatusRequest;
import com.breaze.genesis.dto.users.responses.UserPageResponse;
import com.breaze.genesis.dto.users.responses.UserStatusResponse;
import com.breaze.genesis.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserPageResponse listUsers(
            @Valid @ModelAttribute UserListQueryRequest query
    ) {
        int page = query.getPage() == null ? 0 : query.getPage();
        int size = query.getSize() == null ? 10 : query.getSize();
        String sort = query.getSort() == null || query.getSort().isBlank() ? "id,asc" : query.getSort();

        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
        return userService.listUsers(pageable);
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }
        return authentication.getName();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        return userService.getMyProfile(email);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatusResponse> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userService.updateUserStatus(id, request));
    }

    private Sort resolveSort(String sort) {
        String[] sortParts = sort.split(",", 2);
        String property = sortParts[0].trim();
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1].trim())) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, property);
    }
}