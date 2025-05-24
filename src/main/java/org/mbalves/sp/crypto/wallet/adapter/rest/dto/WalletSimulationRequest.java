package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class WalletSimulationRequest {
    @NotEmpty
    private List<AssetSimulationRequest> assets;

    @PastOrPresent
    private LocalDate date;

    @Data
    public static class AssetSimulationRequest {
        @NotNull
        private String symbol;
        @NotNull
        private Double quantity;
        @NotNull
        private BigDecimal value;
    }
}
