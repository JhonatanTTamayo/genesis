package com.breaze.genesis.controller;

import com.breaze.genesis.dto.wallet.requests.TokenRechargeRequest;
import com.breaze.genesis.dto.wallet.requests.WalletBalanceRequest;
import com.breaze.genesis.dto.wallet.responses.TokenRechargeResponse;
import com.breaze.genesis.dto.wallet.responses.WalletBalanceResponse;
import com.breaze.genesis.services.IWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * Recarga una cantidad especifica de tokens a la billetera de un usuario.
     * Solo accesible por usuarios con rol ADMIN.
     *
     * @param request datos para la recarga
     * @return informacion de la recarga ejecutada
     */
    @PostMapping("/recharge")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenRechargeResponse> rechargeTokens(@Valid @RequestBody TokenRechargeRequest request) {
        return ResponseEntity.ok(walletService.rechargeTokens(request));
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
