package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.tokentransactions.dto.TokenTransactionItemDTO;
import com.breaze.genesis.dto.tokentransactions.requests.TokenTransactionHistoryRequest;
import com.breaze.genesis.dto.tokentransactions.responses.TokenTransactionHistoryResponse;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.services.ITokenTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenTransactionServiceImpl implements ITokenTransactionService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final IUserRepository userRepository;
    private final ITokenTransactionRepository tokenTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public TokenTransactionHistoryResponse getMyTransactions(TokenTransactionHistoryRequest request) {
        User user = findAuthenticatedUser(request.getAuthenticatedEmail());

        int page = request.getPage() == null || request.getPage() < 0 ? DEFAULT_PAGE : request.getPage();
        int size = request.getSize() == null || request.getSize() <= 0
                ? DEFAULT_SIZE
                : Math.min(request.getSize(), MAX_SIZE);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TokenTransaction> transactions = tokenTransactionRepository.findByUserId(user.getId(), pageRequest);

        return TokenTransactionHistoryResponse.builder()
                .content(transactions.getContent().stream().map(this::mapTransaction).toList())
                .page(transactions.getNumber())
                .size(transactions.getSize())
                .totalElements(transactions.getTotalElements())
                .totalPages(transactions.getTotalPages())
                .last(transactions.isLast())
                .build();
    }

    private User findAuthenticatedUser(String authenticatedEmail) {
        return userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private TokenTransactionItemDTO mapTransaction(TokenTransaction transaction) {
        return TokenTransactionItemDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .referenceType(transaction.getReferenceType())
                .referenceId(transaction.getReferenceId())
                .expiresAt(transaction.getExpiresAt())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}