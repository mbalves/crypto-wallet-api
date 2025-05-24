package org.mbalves.sp.crypto.wallet.application.port.out;

import org.mbalves.sp.crypto.wallet.domain.Token;

import java.time.LocalDate;

public interface PriceProviderPort {
    Token getToken(String symbol);
    Double getTokenPrice(String tokenId);
    Double getTokenPrice(String tokenId, LocalDate date);
}
