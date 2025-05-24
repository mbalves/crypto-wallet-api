package org.mbalves.sp.crypto.wallet.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a cryptocurrency token with its current market information.
 * This class stores the token's symbol, unique identifier, and current price.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Data
public class Token {
    /**
     * Unique identifier for the token in the external price provider system.
     * This ID is used to fetch price updates from the external API.
     */
    private String id;

    /**
     * The cryptocurrency symbol (e.g., "BTC", "ETH").
     * This is the standard trading symbol for the cryptocurrency.
     */
    private String symbol;

    /**
     * The current price of the token in USD.
     * This value is updated periodically from the external price provider.
     */
    private BigDecimal price;

    private Instant lastUpdated;
}
