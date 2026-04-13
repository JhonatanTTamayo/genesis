package com.breaze.genesis.dto.transactions.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionListQueryRequest {

    @Min(value = 0, message = "page must be greater than or equal to 0")
    private Integer page;

    @Min(value = 1, message = "size must be greater than or equal to 1")
    @Max(value = 50, message = "size must be less than or equal to 50")
    private Integer size;

    private String sort;
}
