package org.mbalves.sp.crypto.wallet.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cryptocurrency wallet that can hold multiple crypto assets.
 * A wallet is associated with a user's email and contains a collection of assets.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Getter
@Setter
public class Wallet {
    /**
     * Unique identifier for the wallet.
     */
    private Long id;

    /**
     * Email address of the wallet owner.
     */
    private String email;

    /**
     * List of crypto assets held in this wallet.
     */
    private List<Asset> assets = new ArrayList<>();

    /**
     * Adds a new asset to the wallet.
     *
     * @param asset The asset to be added to the wallet.
     */
    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    /**
     * Retrieves the total value of all assets in the wallet.
     */
    public BigDecimal getTotal() {
        return assets.stream()
            .map(Asset::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
