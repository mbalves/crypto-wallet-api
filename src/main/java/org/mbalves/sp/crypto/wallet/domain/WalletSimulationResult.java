package org.mbalves.sp.crypto.wallet.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the result of a wallet profit simulation.
 * This class contains information about the total value of the wallet
 * and the performance of individual assets.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Data
public class WalletSimulationResult {
    /**
     * The total value of the wallet after simulation.
     * This is the sum of all asset values at the simulation date.
     */
    private BigDecimal total;

    /**
     * The symbol of the best performing asset in the wallet.
     * This is the asset with the highest percentage gain.
     */
    private String bestAsset;

    /**
     * The percentage gain of the best performing asset.
     * This value represents the percentage increase from the initial value.
     */
    private BigDecimal bestPerformance;

    /**
     * The symbol of the worst performing asset in the wallet.
     * This is the asset with the lowest percentage gain (or highest loss).
     */
    private String worstAsset;

    /**
     * The percentage gain of the worst performing asset.
     * This value represents the percentage increase from the initial value.
     * A negative value indicates a loss.
     */
    private BigDecimal worstPerformance;
}
