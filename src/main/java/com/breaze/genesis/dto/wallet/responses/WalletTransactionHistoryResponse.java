package com.breaze.genesis.dto.wallet.responses;

import com.breaze.genesis.dto.wallet.dto.WalletTransactionItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionHistoryResponse {
    private List<WalletTransactionItemDTO> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
}
