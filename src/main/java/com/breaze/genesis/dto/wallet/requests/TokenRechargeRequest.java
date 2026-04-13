package com.breaze.genesis.dto.wallet.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRechargeRequest {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "El monto debe ser de al menos 1")
    private Integer amount;
}
