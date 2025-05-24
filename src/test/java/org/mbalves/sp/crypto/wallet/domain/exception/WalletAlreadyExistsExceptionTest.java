package org.mbalves.sp.crypto.wallet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletAlreadyExistsExceptionTest {

    @Test
    void testConstructorAndMessage() {
        // Arrange
        String email = "test@example.com";
        
        // Act
        WalletAlreadyExistsException exception = new WalletAlreadyExistsException(email);
        
        // Assert
        assertEquals("Wallet already exists for email: test@example.com", exception.getMessage());
    }
}
