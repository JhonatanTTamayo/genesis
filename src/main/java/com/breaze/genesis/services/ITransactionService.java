package com.breaze.genesis.services;

import com.breaze.genesis.dto.transactions.responses.TransactionPageResponse;
import com.breaze.genesis.dto.transactions.responses.TransactionResponse;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {

    TransactionPageResponse getMyTransactions(String email, Pageable pageable);

    TransactionResponse getTransactionById(String email, Long transactionId);

    void logSuccessfulOperation(
            User user,
            String operationCode,
            String operationName,
            String inputJson,
            String outputJson,
            Integer baseCost,
            Integer totalTokensConsumed,
            TokenTransaction tokenTransaction
    );

    void logFailedOperation(
            User user,
            String operationCode,
            String operationName,
            String inputJson,
            Integer baseCost,
            String errorMessage
    );
}
