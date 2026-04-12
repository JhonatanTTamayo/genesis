package com.breaze.genesis.dto.subscriptions.responses;

import com.breaze.genesis.dto.subscriptions.dto.SubscriptionPlanDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de salida para exponer la suscripcion activa del usuario autenticado.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class MyActiveSubscriptionResponse {
    private Long id;
    private SubscriptionPlanDTO plan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
}
