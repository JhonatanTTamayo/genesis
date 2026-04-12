package com.breaze.genesis.controller;

import com.breaze.genesis.dto.wallet.requests.WalletBalanceRequest;
import com.breaze.genesis.dto.wallet.requests.WalletTransactionHistoryRequest;
import com.breaze.genesis.dto.wallet.responses.WalletBalanceResponse;
import com.breaze.genesis.dto.wallet.responses.WalletTransactionHistoryResponse;
import com.breaze.genesis.services.IWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
/**
 * Controlador REST para consultar saldo e historial de transacciones del wallet.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class WalletController {

    private final IWalletService walletService;

    /**
     * Consulta el saldo actual del usuario autenticado.
     *
     * @param authentication contexto de autenticacion actual
     * @return saldo y metadatos del wallet
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<WalletBalanceResponse> getMyWallet(Authentication authentication) {
        WalletBalanceRequest request = WalletBalanceRequest.builder()
                .authenticatedEmail(getAuthenticatedEmail(authentication))
                .build();
        return ResponseEntity.ok(walletService.getMyBalance(request));
    }

    /**
     * Consulta el historial de transacciones del wallet del usuario autenticado.
     *
     * @param query parametros de paginacion
     * @param authentication contexto de autenticacion actual
     * @return historial paginado de transacciones
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<WalletTransactionHistoryResponse> getMyWalletTransactions(
            @Valid @ModelAttribute WalletTransactionHistoryRequest query,
            Authentication authentication
    ) {
        WalletTransactionHistoryRequest request = WalletTransactionHistoryRequest.builder()
                .authenticatedEmail(getAuthenticatedEmail(authentication))
                .page(query.getPage())
                .size(query.getSize())
                .build();

        return ResponseEntity.ok(walletService.getMyTransactions(request));
    }

    /**
     * Extrae y valida el correo del usuario autenticado.
     *
     * @param authentication contexto de autenticacion actual
     * @return correo autenticado
     */
    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
        }
        return authentication.getName();
    }
}
