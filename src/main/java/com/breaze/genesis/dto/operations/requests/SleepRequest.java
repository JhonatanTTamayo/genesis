package com.breaze.genesis.dto.operations.requests;

import com.breaze.genesis.dto.operations.dto.SleepCalculatorMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleepRequest {
    private SleepCalculatorMode mode;
    private LocalTime time;
    private Integer sleepDuration;
}
