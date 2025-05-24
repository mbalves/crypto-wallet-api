package org.mbalves.sp.crypto.wallet.adapter.pricing.dto;

import lombok.Data;

@Data
public class CoinCapPriceResponse {
    private CoinCapData data;
    private Long timestamp;
}

