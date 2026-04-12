package com.breaze.genesis.controller;

import com.breaze.genesis.dto.response.TransactionPageResponse;
import com.breaze.genesis.dto.response.TransactionResponse;
import com.breaze.genesis.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final TransactionService transactionService;

    @GetMapping("/me")
    public TransactionPageResponse getMyTransactions(Authentication authentication,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "executedAt,desc") String sort) {
        String email = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, resolveSort(sort));
        return transactionService.getMyTransactions(email, pageable);
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(Authentication authentication,
                                                  @PathVariable Long id) {
        String email = authentication.getName();
        return transactionService.getTransactionById(email, id);
    }

    private Sort resolveSort(String sort) {
        String[] sortParts = sort.split(",", 2);
        String property = sortParts[0].trim();
        Sort.Direction direction = Sort.Direction.DESC;

        if (sortParts.length > 1) {
            String directionValue = sortParts[1].trim();
            if ("asc".equalsIgnoreCase(directionValue)) {
                direction = Sort.Direction.ASC;
            } else if ("desc".equalsIgnoreCase(directionValue)) {
                direction = Sort.Direction.DESC;
            }
        }

        return Sort.by(direction, property);
    }
}
