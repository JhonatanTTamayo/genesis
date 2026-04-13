package com.breaze.genesis.dto.operations.responses;

import com.breaze.genesis.dto.operations.dto.BMICategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BMIResponse {
    private Double bmi;
    private BMICategory category;
    private Double minimumHealthyWeight;
    private Double maximumHealthyWeight;
    private Double weightDifferenceFromRange;
}
