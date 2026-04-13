package com.breaze.genesis.dto.users.requests;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListQueryRequest {

    @Min(value = 0, message = "page must be greater than or equal to 0")
    private Integer page;

    @Min(value = 1, message = "size must be greater than or equal to 1")
    private Integer size;

    private String sort;
}
