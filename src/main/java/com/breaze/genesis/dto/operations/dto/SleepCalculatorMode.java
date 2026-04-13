package com.breaze.genesis.dto.operations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SleepCalculatorMode {
    WAKE_UP_TIME("WAKE_UP_TIME"),
    BEDTIME("BEDTIME");
    private final String value;
}
