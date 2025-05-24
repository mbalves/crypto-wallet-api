package org.mbalves.sp.crypto.wallet.adapter.pricing.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoinCapPriceHistoryResponse {
    private List<CoinCapDataHistory> data;
    private Long timestamp;
}

