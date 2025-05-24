package org.mbalves.sp.crypto.wallet.adapter.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WalletRequest {
    @Email
    @NotEmpty
    private String email;
}
