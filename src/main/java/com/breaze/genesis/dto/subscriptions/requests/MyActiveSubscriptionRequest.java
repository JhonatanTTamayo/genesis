package com.breaze.genesis.dto.subscriptions.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de entrada para consultar la suscripcion activa del usuario autenticado.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class MyActiveSubscriptionRequest {

    @NotBlank(message = "authenticatedEmail is required")
    private String authenticatedEmail;
}
