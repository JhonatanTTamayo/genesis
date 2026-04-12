package com.breaze.genesis.dto.plan.requests;

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
/**
 * DTO de entrada para listar planes con paginacion y ordenamiento.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class ListPlansRequest {
    @Min(value = 0, message = "page must be greater than or equal to 0")
    private Integer page;
    @Min(value = 1, message = "size must be greater than or equal to 1")
    @Max(value = 50, message = "size must be less than or equal to 50")
    private Integer size;
    private String sort;
}
