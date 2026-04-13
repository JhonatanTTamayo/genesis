package com.breaze.genesis.controller;

import com.breaze.genesis.dto.operations.requests.UpdateOperationStatusRequest;
import com.breaze.genesis.dto.operations.requests.BMIRequest;
import com.breaze.genesis.dto.operations.requests.CreditExecutionRequest;
import com.breaze.genesis.dto.operations.requests.CurrencyConverterRequest;
import com.breaze.genesis.dto.operations.requests.SleepRequest;
import com.breaze.genesis.dto.operations.responses.BMIResponse;
import com.breaze.genesis.dto.operations.responses.CreditExecutionResponse;
import com.breaze.genesis.dto.operations.responses.CurrencyConverterResponse;
import com.breaze.genesis.dto.operations.responses.OperationCatalogResponse;
import com.breaze.genesis.dto.operations.responses.SleepResponse;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.services.IOperationService;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
public class OperationController {

    private final IOperationService operationService;
    private final IUserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado con email: " + email));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OperationCatalogResponse>> getAvailableOperations() {
        List<OperationCatalogResponse> operations = operationService.getAvailableOperations();
        return ResponseEntity.ok(operations);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperationCatalogResponse> updateOperationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOperationStatusRequest request) {
        OperationCatalogResponse response = operationService.updateOperationStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credit-execution")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<CreditExecutionResponse> calculateCredit(
            @Valid @RequestBody CreditExecutionRequest request,
            Authentication authentication){
        User user = getAuthenticatedUser(authentication);
        CreditExecutionResponse response = operationService.calculateCredit(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/currency-converter")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<CurrencyConverterResponse> convertCurrency(
            @Valid @RequestBody CurrencyConverterRequest request,
            Authentication authentication){
        User user = getAuthenticatedUser(authentication);
        CurrencyConverterResponse response = operationService.convertCurrency(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bmi")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<BMIResponse> calculateBMI(
            @Valid @RequestBody BMIRequest request,
            Authentication authentication){
        User user = getAuthenticatedUser(authentication);
        BMIResponse response = operationService.calculateBMI(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sleep-calculator")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<SleepResponse> calculateSleep(
            @Valid @RequestBody SleepRequest request,
            Authentication authentication){
        User user = getAuthenticatedUser(authentication);
        SleepResponse response = operationService.calculateSleep(request, user);
        return ResponseEntity.ok(response);
    }
}

