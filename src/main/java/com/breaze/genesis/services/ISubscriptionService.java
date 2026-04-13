package com.breaze.genesis.services;

import com.breaze.genesis.dto.subscriptions.requests.SubscriptionCreateRequest;
import com.breaze.genesis.dto.subscriptions.requests.MyActiveSubscriptionRequest;
import com.breaze.genesis.dto.subscriptions.requests.SubscriptionHistoryQueryRequest;
import com.breaze.genesis.dto.subscriptions.responses.CreateSubscriptionResponse;
import com.breaze.genesis.dto.subscriptions.responses.ListSubscriptionHistoryResponse;
import com.breaze.genesis.dto.subscriptions.responses.MyActiveSubscriptionResponse;

import java.util.List;

/**
 * Contrato de aplicacion para operaciones de suscripciones.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public interface ISubscriptionService {

    /**
     * Obtiene el historial paginado de suscripciones del usuario autenticado.
     *
     * @param authenticatedEmail correo del usuario autenticado
     * @param request parametros de paginacion
     * @return historial de suscripciones
     */
    List<ListSubscriptionHistoryResponse> getHistory(String authenticatedEmail , SubscriptionHistoryQueryRequest request);

    /**
     * Crea o reutiliza una suscripcion activa para el usuario autenticado.
     *
     * @param authenticatedEmail correo del usuario autenticado
     * @param request identificador del plan solicitado
     * @return resultado de la creacion de suscripcion
     */
    CreateSubscriptionResponse createSubscription(String authenticatedEmail, SubscriptionCreateRequest request);

    /**
     * Obtiene la suscripcion activa actual del usuario autenticado.
     *
     * @param request correo autenticado del usuario
     * @return suscripcion activa
     */
    MyActiveSubscriptionResponse getMyActiveSubscription(MyActiveSubscriptionRequest request);
}
