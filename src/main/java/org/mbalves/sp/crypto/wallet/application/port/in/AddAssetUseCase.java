package org.mbalves.sp.crypto.wallet.application.port.in;

import org.mbalves.sp.crypto.wallet.domain.Wallet;

public interface AddAssetUseCase {
    Wallet addAsset(Long walletId, String symbol, Double quantity);
}
