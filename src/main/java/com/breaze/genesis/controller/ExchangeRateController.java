package com.breaze.genesis.controller;

import com.breaze.genesis.dto.operations.requests.ExchangeRateRequest;
import com.breaze.genesis.dto.operations.responses.ExchangeRateResponse;
import com.breaze.genesis.services.IOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final IOperationService operationService;
    @GetMapping
    public ResponseEntity<ExchangeRateResponse> getExchangeRate() {
        return ResponseEntity.ok(operationService.getExchangeRate());
    }
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExchangeRateResponse> updateExchangeRate(@RequestBody @Valid ExchangeRateRequest request) {
        return ResponseEntity.ok(operationService.updateExchangeRate(request));
    }
}
