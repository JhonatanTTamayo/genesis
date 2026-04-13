package com.breaze.genesis.services;

import com.breaze.genesis.dto.wallet.requests.TokenRechargeRequest;
import com.breaze.genesis.dto.wallet.requests.WalletBalanceRequest;
import com.breaze.genesis.dto.wallet.requests.WalletTransactionHistoryRequest;
import com.breaze.genesis.dto.wallet.responses.TokenRechargeResponse;
import com.breaze.genesis.dto.wallet.responses.WalletBalanceResponse;
import com.breaze.genesis.dto.wallet.responses.WalletTransactionHistoryResponse;

/**
 * Contrato de aplicacion para consultas del wallet de usuario.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public interface IWalletService {

    /**
     * Obtiene el saldo actual del wallet para el usuario autenticado.
     *
     * @param request correo autenticado del usuario
     * @return saldo actual y metadatos del wallet
     */
    WalletBalanceResponse getMyBalance(WalletBalanceRequest request);

    /**
     * Obtiene el historial paginado de transacciones del wallet.
     *
     * @param request correo autenticado y parametros de paginacion
     * @return transacciones paginadas
     */
    WalletTransactionHistoryResponse getMyTransactions(WalletTransactionHistoryRequest request);

    /**
     * Recarga una cantidad especifica de tokens al usuario (solo Admin).
     *
     * @param request datos de la recarga y usuario
     * @return estado actual del balance tras la recarga
     */
    TokenRechargeResponse rechargeTokens(TokenRechargeRequest request);
}
