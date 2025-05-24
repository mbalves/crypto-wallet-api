package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.application.port.in.AddAssetUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.PriceProviderPort;
import org.mbalves.sp.crypto.wallet.application.port.out.TokenRepositoryPort;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.InsufficientFundsException;
import org.mbalves.sp.crypto.wallet.domain.exception.InvalidTokenException;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of the AddAssetUseCase interface.
 * This use case handles adding or updating assets in a wallet.
 * It validates the wallet existence, token validity, and handles quantity updates.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AddAssetUseCaseImpl implements AddAssetUseCase {
    private final WalletRepositoryPort walletRepository;
    private final TokenRepositoryPort tokenRepository;
    private final PriceProviderPort priceProvider;

    /**
     * Adds an asset to the wallet or updates its quantity if it already exists.
     * This method implements the following business rules:
     * 1. Validates wallet existence
     * 2. Validates token existence and updates its price
     * 3. Updates existing asset quantity or creates a new asset
     * 4. Handles negative quantities for asset reduction
     *
     * @param walletId The ID of the wallet to add the asset to
     * @param symbol The cryptocurrency symbol (e.g., "BTC", "ETH")
     * @param quantity The quantity to add (can be negative to reduce)
     * @return The updated wallet
     * @throws WalletNotFoundException if the wallet doesn't exist
     * @throws InvalidTokenException if the token symbol is invalid
     * @throws InsufficientFundsException if trying to reduce more than available
     */
    @Override
    public Wallet addAsset(Long walletId, String symbol, Double quantity) {
        // Validate wallet existence
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        String normalizedSymbol = symbol.toUpperCase();

        Token token = validateAndUpdateToken(normalizedSymbol);

        Asset asset = getAssetFromWallet(wallet, normalizedSymbol);

        BigDecimal quantityBD = BigDecimal.valueOf(quantity);
        // If the asset already exists, update its quantity
        if (asset.getId() != null) {
            BigDecimal newQuantity = asset.getQuantity().add(quantityBD);
            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException(normalizedSymbol);
            }
            asset.setQuantity(newQuantity);
        } else {
            asset.setToken(token);
            asset.setQuantity(quantityBD);
            wallet.addAsset(asset);
        }

        return walletRepository.save(wallet);
    }

    /**
     * Helper method to get an asset from the wallet by its symbol.
     * If the asset doesn't exist, returns a new Asset instance.
     *
     * @param wallet The wallet to search in
     * @param normalizedSymbol The uppercase symbol to search for
     * @return The existing asset or a new Asset instance
     */
    private static Asset getAssetFromWallet(Wallet wallet, String normalizedSymbol) {
        return wallet.getAssets().stream()
                .filter(a -> a.getToken().getSymbol().equalsIgnoreCase(normalizedSymbol))
                .findFirst()
                .orElse(new Asset());
    }

    /**
     * Helper method to validate and update a token.
     * Fetches the latest token information and price from the price provider.
     *
     * @param normalizedSymbol The uppercase symbol to validate
     * @return The validated and updated token
     * @throws InvalidTokenException if the token doesn't exist
     */
    private Token validateAndUpdateToken(String normalizedSymbol) {
        Token token = priceProvider.getToken(normalizedSymbol);
        if (token == null) {
            throw new InvalidTokenException(normalizedSymbol);
        }
        // update the token price or create it if not exists
        tokenRepository.save(token);
        return token;
    }
}
