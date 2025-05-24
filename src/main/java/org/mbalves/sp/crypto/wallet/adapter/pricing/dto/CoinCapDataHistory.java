package org.mbalves.sp.crypto.wallet.adapter.pricing.dto;

import lombok.Data;

@Data
public class CoinCapDataHistory {
    private String priceUsd;
    private String date;
    private Long time;
}
