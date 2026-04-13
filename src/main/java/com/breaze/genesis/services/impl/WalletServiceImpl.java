package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.wallet.dto.WalletTransactionItemDTO;
import com.breaze.genesis.dto.wallet.requests.TokenRechargeRequest;
import com.breaze.genesis.dto.wallet.requests.WalletBalanceRequest;
import com.breaze.genesis.dto.wallet.requests.WalletTransactionHistoryRequest;
import com.breaze.genesis.dto.wallet.responses.TokenRechargeResponse;
import com.breaze.genesis.dto.wallet.responses.WalletBalanceResponse;
import com.breaze.genesis.dto.wallet.responses.WalletTransactionHistoryResponse;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.entity.tokens.TokenWallet;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import com.breaze.genesis.services.IWalletService;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

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

        /**
         * Obtiene el historial paginado de transacciones del usuario autenticado.
         *
         * @param request solicitud con correo y paginacion
         * @return transacciones paginadas del wallet
         */
    @Override
    @Transactional(readOnly = true)
    public WalletTransactionHistoryResponse getMyTransactions(WalletTransactionHistoryRequest request) {
        User user = findAuthenticatedUser(request.getAuthenticatedEmail());

        int page = request.getPage() == null || request.getPage() < 0 ? DEFAULT_PAGE : request.getPage();
        int size = request.getSize() == null || request.getSize() <= 0
                ? DEFAULT_SIZE
                : Math.min(request.getSize(), MAX_SIZE);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TokenTransaction> transactions = tokenTransactionRepository.findByUserId(user.getId(), pageRequest);

        return WalletTransactionHistoryResponse.builder()
                .content(transactions.getContent().stream().map(this::mapTransaction).toList())
                .page(transactions.getNumber())
                .size(transactions.getSize())
                .totalElements(transactions.getTotalElements())
                .totalPages(transactions.getTotalPages())
                .last(transactions.isLast())
                .build();
    }

    @Override
    @Transactional
    public TokenRechargeResponse rechargeTokens(TokenRechargeRequest request) {
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID especificado"));

        // Crear wallet si no existe
        TokenWallet wallet = tokenWalletRepository.findByUserId(targetUser.getId())
                .orElseGet(() -> createEmptyWallet(targetUser));

        // Crear registro de transaccion - amount positivo
        TokenTransaction transaction = TokenTransaction.builder()
                .user(targetUser)
                .amount(request.getAmount())  // amount > 0 y será mantenido como remaining_amount
                .type(com.breaze.genesis.entity.tokens.TokenTransactionType.ADD)
                .description("Manual token recharge")
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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

        /**
         * Convierte una transaccion de dominio a su DTO de respuesta.
         *
         * @param transaction transaccion de tokens
         * @return item DTO de transaccion
         */
    private WalletTransactionItemDTO mapTransaction(TokenTransaction transaction) {
        return WalletTransactionItemDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .expiresAt(transaction.getExpiresAt())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
