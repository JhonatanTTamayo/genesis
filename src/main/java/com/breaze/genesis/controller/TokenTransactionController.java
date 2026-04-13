package com.breaze.genesis.controller;

import com.breaze.genesis.dto.tokentransactions.requests.TokenTransactionHistoryRequest;
import com.breaze.genesis.dto.tokentransactions.responses.TokenTransactionHistoryResponse;
import com.breaze.genesis.services.ITokenTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token-transactions")
@RequiredArgsConstructor
public class TokenTransactionController {

    private final ITokenTransactionService tokenTransactionService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TokenTransactionHistoryResponse> getMyTokenTransactions(
            @Valid @ModelAttribute TokenTransactionHistoryRequest query,
            Authentication authentication
    ) {
        TokenTransactionHistoryRequest request = TokenTransactionHistoryRequest.builder()
                .authenticatedEmail(getAuthenticatedEmail(authentication))
                .page(query.getPage())
                .size(query.getSize())
                .build();

        return ResponseEntity.ok(tokenTransactionService.getMyTransactions(request));
    }
    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
        }
        return authentication.getName();
    }
}