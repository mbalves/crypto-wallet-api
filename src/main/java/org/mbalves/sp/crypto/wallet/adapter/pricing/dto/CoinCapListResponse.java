package org.mbalves.sp.crypto.wallet.adapter.pricing.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoinCapListResponse {
    private List<CoinCapData> data;
    private Long timestamp;
}

