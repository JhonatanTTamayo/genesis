package com.breaze.genesis.dto.operations.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BMIRequest {
    private Integer weight;
    private Integer height;
}
