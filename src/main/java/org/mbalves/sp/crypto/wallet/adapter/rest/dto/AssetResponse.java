package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.mbalves.sp.crypto.wallet.adapter.rest.config.BigDecimalSerializer;
import org.mbalves.sp.crypto.wallet.adapter.rest.config.MoneyValueSerializer;

import java.math.BigDecimal;

@Data
public class AssetResponse {
    private String symbol;
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal quantity;
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal price;
    @JsonSerialize(using = MoneyValueSerializer.class)
    private BigDecimal value;
}
