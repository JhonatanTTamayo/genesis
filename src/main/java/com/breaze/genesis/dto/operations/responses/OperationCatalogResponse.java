package com.breaze.genesis.dto.operations.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationCatalogResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer baseCost;
    private Boolean active;
}