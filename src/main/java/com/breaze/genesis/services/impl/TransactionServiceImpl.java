package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.transactions.responses.TransactionPageResponse;
import com.breaze.genesis.dto.transactions.responses.TransactionResponse;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.entity.transactions.OperationExecution;
import com.breaze.genesis.entity.transactions.OperationExecutionStatus;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.repository.operation.IOperationExecutionRepository;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.services.ITransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final IUserRepository userRepository;
    private final IOperationExecutionRepository operationExecutionRepository;
    private final ITokenTransactionRepository tokenTransactionRepository;

    public TransactionServiceImpl(
            IUserRepository userRepository,
            IOperationExecutionRepository operationExecutionRepository,
            ITokenTransactionRepository tokenTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.operationExecutionRepository = operationExecutionRepository;
        this.tokenTransactionRepository = tokenTransactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionPageResponse getMyTransactions(String email, Pageable pageable) {
        User user = findUserByEmail(email);
        Page<OperationExecution> transactions = operationExecutionRepository.findByUserId(user.getId(), pageable);

        TransactionPageResponse response = new TransactionPageResponse();
        response.setContent(transactions.getContent().stream().map(this::mapToResponse).toList());
        response.setPage(transactions.getNumber());
        response.setSize(transactions.getSize());
        response.setTotalElements(transactions.getTotalElements());
        response.setTotalPages(transactions.getTotalPages());
        response.setLast(transactions.isLast());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(String email, Long transactionId) {
        User user = findUserByEmail(email);
        OperationExecution transaction = operationExecutionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion no encontrada"));
        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public void logSuccessfulOperation(
            User user,
            String operationCode,
            String operationName,
            String inputJson,
            String outputJson,
            Integer baseCost,
            Integer totalTokensConsumed,
            TokenTransaction tokenTransaction
    ) {
        OperationExecution execution = buildExecution(
                user,
                operationCode,
                operationName,
                inputJson,
                outputJson,
                baseCost,
                totalTokensConsumed,
                OperationExecutionStatus.SUCCESS,
                null
        );
        OperationExecution savedExecution = operationExecutionRepository.save(execution);

        if (tokenTransaction != null) {
            tokenTransaction.setOperationExecution(savedExecution);
            tokenTransactionRepository.save(tokenTransaction);
        }
    }

    @Override
    @Transactional
    public void logFailedOperation(
            User user,
            String operationCode,
            String operationName,
            String inputJson,
            Integer baseCost,
            String errorMessage
    ) {
        OperationExecution execution = buildExecution(
                user,
                operationCode,
                operationName,
                inputJson,
                null,
                baseCost,
                0,
                OperationExecutionStatus.FAILED,
                errorMessage
        );
        operationExecutionRepository.save(execution);
    }

    private OperationExecution buildExecution(
            User user,
            String operationCode,
            String operationName,
            String inputJson,
            String outputJson,
            Integer baseCost,
            Integer totalTokensConsumed,
            OperationExecutionStatus status,
            String errorMessage
    ) {
        OperationExecution execution = new OperationExecution();
        execution.setUser(user);
        execution.setOperationCode(operationCode);
        execution.setOperationName(operationName);
        execution.setInputJson(inputJson);
        execution.setOutputJson(outputJson);
        execution.setInputTokens(0);
        execution.setOutputTokens(0);
        execution.setBaseCost(baseCost != null ? baseCost : 0);
        execution.setTotalTokensConsumed(totalTokensConsumed != null ? totalTokensConsumed : 0);
        execution.setStatus(status);
        execution.setErrorMessage(errorMessage);
        return execution;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private TransactionResponse mapToResponse(OperationExecution execution) {
        TransactionResponse response = new TransactionResponse();
        response.setId(execution.getId());
        response.setOperationCode(execution.getOperationCode());
        response.setOperationName(execution.getOperationName());
        response.setInputJson(execution.getInputJson());
        response.setOutputJson(execution.getOutputJson());
        response.setInputTokens(execution.getInputTokens());
        response.setOutputTokens(execution.getOutputTokens());
        response.setBaseCost(execution.getBaseCost());
        response.setTotalTokensConsumed(execution.getTotalTokensConsumed());
        response.setStatus(execution.getStatus().name());
        response.setErrorMessage(execution.getErrorMessage());
        response.setExecutedAt(execution.getExecutedAt());
        return response;
    }
}
