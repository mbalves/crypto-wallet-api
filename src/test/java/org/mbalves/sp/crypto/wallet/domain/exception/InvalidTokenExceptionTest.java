package org.mbalves.sp.crypto.wallet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidTokenExceptionTest {

    @Test
    void testConstructorAndMessage() {
        // Arrange
        String symbol = "INVALID";
        
        // Act
        InvalidTokenException exception = new InvalidTokenException(symbol);
        
        // Assert
        assertEquals("Invalid token or price not found for symbol: INVALID", exception.getMessage());
    }
}
