package org.mbalves.sp.crypto.wallet.adapter.rest;

import org.junit.jupiter.api.Test;
import org.mbalves.sp.crypto.wallet.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleWalletAlreadyExists_ShouldReturnConflictStatus() {
        // Arrange
        String email = "test@example.com";
        String errorMessage = "Wallet already exists for email: " + email;
        WalletAlreadyExistsException exception = new WalletAlreadyExistsException(email);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleWalletAlreadyExists(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleWalletNotFound_ShouldReturnNotFoundStatus() {
        // Arrange
        Long walletId = 1L;
        WalletNotFoundException exception = new WalletNotFoundException(walletId);
        String expectedMessage = "Wallet not found with ID: " + walletId;

        // Act
        ResponseEntity<String> response = exceptionHandler.handleWalletNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    void handleAssetNotFound_ShouldReturnNotFoundStatus() {
        // Arrange
        Long walletId = 1L;
        String symbol = "BTC";
        AssetNotFoundException exception = new AssetNotFoundException(walletId, symbol);
        String expectedMessage = "Asset [" + symbol + "] not found in Wallet: " + walletId;

        // Act
        ResponseEntity<String> response = exceptionHandler.handleAssetNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    void handleInvalidToken_ShouldReturnBadRequestStatus() {
        // Arrange
        String symbol = "XYZ";
        String errorMessage = "Invalid token or price not found for symbol: " + symbol;
        InvalidTokenException exception = new InvalidTokenException(symbol);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleInvalidToken(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleInsufficientFunds_ShouldReturnBadRequestStatus() {
        // Arrange
        String symbol = "XYZ";
        String errorMessage = "Insufficient funds for symbol: " + symbol;
        InsufficientFundsException exception = new InsufficientFundsException(symbol);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleInsufficientFunds(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        String errorMessage = "Some unexpected error";
        Exception exception = new RuntimeException(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }
}
