package org.mbalves.sp.crypto.wallet.application.usecase;

import lombok.RequiredArgsConstructor;
import org.mbalves.sp.crypto.wallet.application.port.in.DeleteAssetUseCase;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.AssetNotFoundException;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of the DeleteAssetUseCase interface.
 * This use case handles the removal of assets from a wallet.
 * It ensures that both the wallet and the asset exist before deletion.
 *
 * @author Marcelo Alves
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DeleteAssetUseCaseImpl implements DeleteAssetUseCase {
    private final WalletRepositoryPort walletRepository;

    /**
     * Deletes an asset from a wallet.
     * This method implements the following business rules:
     * 1. Validates wallet existence
     * 2. Validates asset existence in the wallet
     * 3. Removes the asset from the wallet
     * 4. Persists the updated wallet
     *
     * @param walletId The ID of the wallet containing the asset
     * @param symbol The symbol of the asset to delete
     * @return The updated wallet without the deleted asset
     * @throws WalletNotFoundException if the wallet doesn't exist
     * @throws AssetNotFoundException if the asset doesn't exist in the wallet
     */
    @Override
    @Transactional
    public Wallet deleteAsset(Long walletId, String symbol) {
        // Check if wallet exists
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        // Check if the asset exists in the wallet
        Optional<Asset> asset = wallet.getAssets().stream()
                .filter(a -> a.getToken().getSymbol().equals(symbol)).findFirst();

        if (asset.isEmpty()) {
            throw new AssetNotFoundException(walletId, symbol);
        }

        // Remove the asset from the wallet's assets collection
        wallet.getAssets().remove(asset.get());

        // Delete the asset and return the updated wallet
        return walletRepository.save(wallet);
    }
}
