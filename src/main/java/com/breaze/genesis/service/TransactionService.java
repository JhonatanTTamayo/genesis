package com.breaze.genesis.service;

import com.breaze.genesis.dto.response.TransactionPageResponse;
import com.breaze.genesis.dto.response.TransactionResponse;
import com.breaze.genesis.entity.Transaction;
import com.breaze.genesis.entity.TransactionStatus;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exception.ForbiddenException;
import com.breaze.genesis.exception.ResourceNotFoundException;
import com.breaze.genesis.repository.TransactionRepository;
import com.breaze.genesis.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public TransactionPageResponse getMyTransactions(String email, Pageable pageable) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        User user = userOptional.get();

        Page<Transaction> transactions = transactionRepository.findByUser(user, pageable);

        List<TransactionResponse> responseList = new ArrayList<>();
        for (Transaction transaction : transactions.getContent()) {
            responseList.add(mapToResponse(transaction));
        }

        TransactionPageResponse pageResponse = new TransactionPageResponse();
        pageResponse.setContent(responseList);
        pageResponse.setPage(transactions.getNumber());
        pageResponse.setSize(transactions.getSize());
        pageResponse.setTotalElements(transactions.getTotalElements());
        pageResponse.setTotalPages(transactions.getTotalPages());
        pageResponse.setLast(transactions.isLast());
        return pageResponse;
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(String email, Long transactionId) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        User user = userOptional.get();

        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
        if (!transactionOptional.isPresent()) {
            throw new ResourceNotFoundException("Transaccion no encontrada");
        }
        Transaction transaction = transactionOptional.get();

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("No tienes permiso para ver esta transaccion");
        }

        return mapToResponse(transaction);
    }

    @Transactional
    public Transaction logTransaction(User user, String operationCode, String operationName, String inputJson,
                                      String outputJson, Integer inputTokens, Integer outputTokens,
                                      Integer baseCost, TransactionStatus status, String errorMessage) {
        int inTokens = inputTokens != null ? inputTokens : 0;
        int outTokens = outputTokens != null ? outputTokens : 0;
        int defaultBaseCost = baseCost != null ? baseCost : 0;

        int totalTokens = defaultBaseCost + inTokens + outTokens;
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setOperationCode(operationCode);
        transaction.setOperationName(operationName);
        transaction.setInputJson(inputJson);
        transaction.setOutputJson(outputJson);
        transaction.setInputTokens(inTokens);
        transaction.setOutputTokens(outTokens);
        transaction.setBaseCost(defaultBaseCost);
        transaction.setTotalTokensConsumed(totalTokens);
        transaction.setStatus(status);
        transaction.setErrorMessage(errorMessage);

        return transactionRepository.save(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setOperationCode(transaction.getOperationCode());
        response.setOperationName(transaction.getOperationName());
        response.setInputJson(transaction.getInputJson());
        response.setOutputJson(transaction.getOutputJson());
        response.setInputTokens(transaction.getInputTokens());
        response.setOutputTokens(transaction.getOutputTokens());
        response.setBaseCost(transaction.getBaseCost());
        response.setTotalTokensConsumed(transaction.getTotalTokensConsumed());

        if (transaction.getStatus() != null) {
            response.setStatus(transaction.getStatus().name());
        }

        response.setErrorMessage(transaction.getErrorMessage());
        response.setExecutedAt(transaction.getExecutedAt());
        return response;
    }
}
