package com.breaze.genesis.services;

import com.breaze.genesis.dto.tokentransactions.requests.TokenTransactionHistoryRequest;
import com.breaze.genesis.dto.tokentransactions.responses.TokenTransactionHistoryResponse;

public interface ITokenTransactionService {

    TokenTransactionHistoryResponse getMyTransactions(TokenTransactionHistoryRequest request);
}