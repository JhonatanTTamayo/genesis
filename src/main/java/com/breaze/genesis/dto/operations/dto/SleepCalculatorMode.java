package com.breaze.genesis.dto.operations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SleepCalculatorMode {
    WAKING_UP("WAKING_UP"),
    GOING_TO_SLEEP("GOING_TO_SLEEP");
    private final String value;
}
