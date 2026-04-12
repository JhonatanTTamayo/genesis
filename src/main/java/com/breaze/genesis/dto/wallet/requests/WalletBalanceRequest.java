package com.breaze.genesis.dto.wallet.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceRequest {

    @NotBlank(message = "authenticatedEmail is required")
    private String authenticatedEmail;
}
