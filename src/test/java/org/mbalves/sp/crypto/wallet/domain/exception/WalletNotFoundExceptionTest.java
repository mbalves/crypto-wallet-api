package org.mbalves.sp.crypto.wallet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletNotFoundExceptionTest {

    @Test
    void testConstructorAndMessage() {
        // Arrange
        Long walletId = 123L;
        
        // Act
        WalletNotFoundException exception = new WalletNotFoundException(walletId);
        
        // Assert
        assertEquals("Wallet not found with ID: 123", exception.getMessage());
    }
}
