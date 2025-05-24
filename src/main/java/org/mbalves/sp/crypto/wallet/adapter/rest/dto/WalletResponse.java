package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.mbalves.sp.crypto.wallet.adapter.rest.config.MoneyValueSerializer;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletResponse {
    private Long id;
    @JsonSerialize(using = MoneyValueSerializer.class)
    private BigDecimal total;
    private List<AssetResponse> assets;
}

