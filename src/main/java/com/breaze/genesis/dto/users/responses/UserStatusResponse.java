package com.breaze.genesis.dto.users.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponse {
    
    private Long userId;
    private String email;
    private Boolean active;
    private String resultMessage;

}