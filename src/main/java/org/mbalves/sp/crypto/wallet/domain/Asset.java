package org.mbalves.sp.crypto.wallet.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents a cryptocurrency asset in a wallet.
 * An asset consists of a token and its quantity.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Data
public class Asset {
    /**
     * Unique identifier for the asset.
     */
    private Long id;

    /**
     * The cryptocurrency token information.
     */
    private Token token;

    /**
     * The quantity of the token held.
     */
    private BigDecimal quantity;

    /**
     * Calculates the total value of this asset.
     * The value is calculated as quantity * token price.
     *
     * @return The total value of the asset in USD
     */
    public BigDecimal getValue() {
        return token.getPrice().multiply(quantity);
    }
}
