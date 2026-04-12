package com.breaze.genesis.services;

import com.breaze.genesis.dto.wallet.requests.WalletBalanceRequest;
import com.breaze.genesis.dto.wallet.requests.WalletTransactionHistoryRequest;
import com.breaze.genesis.dto.wallet.responses.WalletBalanceResponse;
import com.breaze.genesis.dto.wallet.responses.WalletTransactionHistoryResponse;

public interface IWalletService {

    WalletBalanceResponse getMyBalance(WalletBalanceRequest request);

    WalletTransactionHistoryResponse getMyTransactions(WalletTransactionHistoryRequest request);
}
