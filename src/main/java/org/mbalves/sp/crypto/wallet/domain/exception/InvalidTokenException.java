package org.mbalves.sp.crypto.wallet.domain.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String symbol) {
        super("Invalid token or price not found for symbol: " + symbol);
    }
}
