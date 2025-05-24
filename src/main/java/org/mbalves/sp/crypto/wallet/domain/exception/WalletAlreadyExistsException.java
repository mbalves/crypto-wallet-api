package org.mbalves.sp.crypto.wallet.domain.exception;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(String email) {
        super("Wallet already exists for email: " + email);
    }
}
