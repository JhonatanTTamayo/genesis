package com.breaze.genesis.dto.operations.requests;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    
    @NotBlank
    private String mode;

    @NotNull
    private LocalTime time;

    @NotNull
    @Min(1)
    private Integer sleepDurationMinutes;
}
