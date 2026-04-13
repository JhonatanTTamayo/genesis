package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.wallet.requests.TokenRechargeRequest;
import com.breaze.genesis.dto.wallet.requests.WalletBalanceRequest;
import com.breaze.genesis.dto.wallet.responses.TokenRechargeResponse;
import com.breaze.genesis.dto.wallet.responses.WalletBalanceResponse;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.entity.tokens.TokenTransactionReferenceType;
import com.breaze.genesis.entity.tokens.TokenWallet;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import com.breaze.genesis.services.IWalletService;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
/**
 * Implementacion del servicio de consulta de wallet y transacciones.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class WalletServiceImpl implements IWalletService {

    private final IUserRepository userRepository;
    private final ITokenWalletRepository tokenWalletRepository;
    private final ITokenTransactionRepository tokenTransactionRepository;

        /**
         * Obtiene el saldo actual del usuario autenticado.
         *
         * @param request solicitud con correo autenticado
         * @return saldo y fecha de actualizacion del wallet
         */
    @Override
    @Transactional
    public WalletBalanceResponse getMyBalance(WalletBalanceRequest request) {
        User user = findAuthenticatedUser(request.getAuthenticatedEmail());

        TokenWallet wallet = tokenWalletRepository.findByUserId(user.getId())
                .orElseGet(() -> TokenWallet.builder()
                        .userId(user.getId())
                        .user(user)
                        .tokensAvailable(0)
                        .updatedAt(user.getCreatedAt())
                        .build());

        // Actualizar wallet omitiendo tokens expirados
        Integer realBalance = tokenTransactionRepository.calculateValidBalance(user.getId());
        if (realBalance == null) {
            realBalance = 0;
        }

        if (!realBalance.equals(wallet.getTokensAvailable())) {
            wallet.setTokensAvailable(realBalance);
            wallet.setUpdatedAt(LocalDateTime.now());
            if (wallet.getUserId() != null) {
                wallet = tokenWalletRepository.save(wallet);
            }
        }

        return WalletBalanceResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .currentBalance(wallet.getTokensAvailable())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public TokenRechargeResponse rechargeTokens(TokenRechargeRequest request) {
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the specified ID"));

        // Crear wallet si no existe
        TokenWallet wallet = tokenWalletRepository.findByUserId(targetUser.getId())
                .orElseGet(() -> createEmptyWallet(targetUser));

        // Crear registro de transaccion - amount positivo
        TokenTransaction transaction = TokenTransaction.builder()
                .user(targetUser)
                .amount(request.getAmount())  // amount > 0 y será mantenido como remaining_amount
                .referenceType(TokenTransactionReferenceType.ADMIN_RECHARGE)
                .referenceId(null)
                .expiresAt(null)  // Las recargas por admin no expiran
                .createdAt(LocalDateTime.now())
                .build();
        tokenTransactionRepository.save(transaction);

        // Actualizar wallet snapshot (suma directo, ADD nunca expira)
        wallet.setTokensAvailable(wallet.getTokensAvailable() + request.getAmount());
        wallet.setUpdatedAt(LocalDateTime.now());
        tokenWalletRepository.save(wallet);

        return TokenRechargeResponse.builder()
                .userId(targetUser.getId())
                .newBalance(wallet.getTokensAvailable())
                .rechargedAmount(request.getAmount())
                .transactionDate(transaction.getCreatedAt())
                .build();
    }

    private TokenWallet createEmptyWallet(User user) {
        return tokenWalletRepository.save(TokenWallet.builder()
                .userId(user.getId())
                .user(user)
                .tokensAvailable(0)
                .updatedAt(LocalDateTime.now())
                .build());
    }

        /**
         * Resuelve el usuario autenticado por correo.
         *
         * @param authenticatedEmail correo autenticado
         * @return entidad de usuario
         */
    private User findAuthenticatedUser(String authenticatedEmail) {
        return userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
