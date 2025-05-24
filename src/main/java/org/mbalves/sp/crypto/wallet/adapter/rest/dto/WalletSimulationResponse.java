package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.mbalves.sp.crypto.wallet.adapter.rest.config.BigDecimalSerializer;
import org.mbalves.sp.crypto.wallet.adapter.rest.config.MoneyValueSerializer;

import java.math.BigDecimal;

@Data
public class WalletSimulationResponse {
    @JsonSerialize(using = MoneyValueSerializer.class)
    private BigDecimal total;
    private String bestAsset;
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bestPerformance;
    private String worstAsset;
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal worstPerformance;
}
