package org.mbalves.sp.crypto.wallet.domain.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String symbol) {
        super("Insufficient funds for symbol: " + symbol);
    }
}
