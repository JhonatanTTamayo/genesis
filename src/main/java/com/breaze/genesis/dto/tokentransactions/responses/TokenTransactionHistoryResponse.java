package com.breaze.genesis.dto.tokentransactions.responses;

import com.breaze.genesis.dto.tokentransactions.dto.TokenTransactionItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransactionHistoryResponse {
    private List<TokenTransactionItemDTO> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
}