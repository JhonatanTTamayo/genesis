package com.breaze.genesis.dto.operations.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BMIResponse {
    private String imc;
    private String category;
    private Float minimumHealthyWeight;
    private Float maximumHealthyWeight;
    private String weightDifference;
}
