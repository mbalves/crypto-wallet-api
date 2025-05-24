package org.mbalves.sp.crypto.wallet.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents a cryptocurrency asset for simulation purposes.
 * This class is used to simulate the performance of individual assets
 * in a wallet over time.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Data
public class AssetSimulation {
    /**
     * The cryptocurrency symbol (e.g., "BTC", "ETH").
     */
    private String symbol;

    /**
     * The quantity of the cryptocurrency held.
     */
    private Double quantity;

    /**
     * The current value of the asset in USD.
     * This is calculated as quantity * current price.
     */
    private BigDecimal value;
}
