package org.mbalves.sp.crypto.wallet.domain.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(Long walletId) {
        super("Wallet not found with ID: " + walletId);
    }
}
