package org.mbalves.sp.crypto.wallet.domain.exception;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(Long walletId, String symbol) {
        super("Asset [" + symbol + "] not found in Wallet: " + walletId);
    }
}
