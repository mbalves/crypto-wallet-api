package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AssetRequest {
    @NotBlank
    private String symbol;
    @NotNull
    @Positive
    private Double quantity;
}
