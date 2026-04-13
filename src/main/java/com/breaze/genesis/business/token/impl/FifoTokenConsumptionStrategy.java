package com.breaze.genesis.business.token.impl;

import com.breaze.genesis.business.token.TokenConsumptionStrategy;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.entity.tokens.TokenTransactionType;
import com.breaze.genesis.entity.tokens.TokenWallet;
import com.breaze.genesis.exceptions.BusinessException;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FifoTokenConsumptionStrategy implements TokenConsumptionStrategy {

    private final ITokenTransactionRepository tokenTransactionRepository;
    private final ITokenWalletRepository tokenWalletRepository;

    @Override
    @Transactional
    public TokenTransaction consumeTokens(User user, int amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to consume must be greater than 0");
        }

        // 1. Obtener billetera (locking optimista manejado por Spring con @Version en TokenWallet)
        TokenWallet wallet = tokenWalletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Wallet no encontrada para el usuario"));

        // 2. Obtener todas las transacciones positivas validas (no expiradas) ordenadas FIFO
        List<TokenTransaction> validTransactions = tokenTransactionRepository.findValidPositiveTransactionsByUserId(user.getId());

        int totalAvailable = validTransactions.stream()
                .mapToInt(TokenTransaction::getRemainingAmount)
                .sum();

        if (totalAvailable < amount) {
            throw new BusinessException("Saldo insuficiente para la operacion. Disponible real:  " + totalAvailable);
        }

        // 3. Consumo (FIFO por expiracion y creacion)
        int amountToConsume = amount;
        for (TokenTransaction tx : validTransactions) {
            if (amountToConsume <= 0) {
                break;
            }
            int remaining = tx.getRemainingAmount();
            int toDeduct = Math.min(remaining, amountToConsume);
            tx.setRemainingAmount(remaining - toDeduct);
            amountToConsume -= toDeduct;
        }

        tokenTransactionRepository.saveAll(validTransactions);

        // 4. Crear transaccion negativa
        TokenTransaction consumptionTx = TokenTransaction.builder()
                .user(user)
                .amount(-amount)
                .type(TokenTransactionType.CONSUMPTION)
                .description(description)
                .expiresAt(null)
                .createdAt(LocalDateTime.now())
                .build();
        tokenTransactionRepository.save(consumptionTx);

        // 5. Actualizar el snapshot de la wallet (Lazy expiration adjust)
        Integer calculatedBalance = tokenTransactionRepository.calculateValidBalance(user.getId());
        int updatedBalance = calculatedBalance != null ? calculatedBalance : 0;
        wallet.setTokensAvailable(updatedBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        tokenWalletRepository.save(wallet);
        log.info("Consumidos {} tokens para el usuario {}. Nuevo balance: {}", amount, user.getId(), updatedBalance);
        return consumptionTx;
    }
}
