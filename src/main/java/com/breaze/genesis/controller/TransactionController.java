package com.breaze.genesis.controller;

import com.breaze.genesis.dto.transactions.requests.TransactionListQueryRequest;
import com.breaze.genesis.dto.transactions.responses.TransactionPageResponse;
import com.breaze.genesis.dto.transactions.responses.TransactionResponse;
import com.breaze.genesis.services.IOperationTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final IOperationTransactionService transactionService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionPageResponse getMyTransactions(
            Authentication authentication,
            @Valid @ModelAttribute TransactionListQueryRequest query
    ) {
        int page = query.getPage() == null ? 0 : query.getPage();
        int size = query.getSize() == null ? 10 : query.getSize();
        String sort = query.getSort() == null || query.getSort().isBlank() ? "executedAt,desc" : query.getSort();

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
