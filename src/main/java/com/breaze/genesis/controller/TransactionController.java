package com.breaze.genesis.controller;

import com.breaze.genesis.dto.transactions.responses.TransactionPageResponse;
import com.breaze.genesis.dto.transactions.responses.TransactionResponse;
import com.breaze.genesis.services.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionPageResponse getMyTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "executedAt,desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
        return transactionService.getMyTransactions(authentication.getName(), pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionResponse getTransactionById(Authentication authentication, @PathVariable Long id) {
        return transactionService.getTransactionById(authentication.getName(), id);
    }

    private Sort resolveSort(String sort) {
        String[] sortParts = sort.split(",", 2);
        String property = sortParts[0].trim();
        Sort.Direction direction = Sort.Direction.DESC;

        if (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1].trim())) {
            direction = Sort.Direction.ASC;
        }

        return Sort.by(direction, property);
    }
}
