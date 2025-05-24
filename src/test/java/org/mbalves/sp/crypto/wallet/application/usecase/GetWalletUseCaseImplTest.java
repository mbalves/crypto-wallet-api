package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWalletUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @InjectMocks
    private GetWalletUseCaseImpl getWalletUseCase;

    private Long walletId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletId = 1L;
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setEmail("test@example.com");
    }

    @Test
    void getWallet_WhenWalletExists_ShouldReturnWallet() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act
        Wallet result = getWalletUseCase.getWallet(walletId);

        // Assert
        assertNotNull(result);
        assertEquals(walletId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        
        verify(walletRepository).findById(walletId);
    }

    @Test
    void getWallet_WhenWalletDoesNotExist_ShouldThrowException() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        WalletNotFoundException exception = assertThrows(
            WalletNotFoundException.class,
            () -> getWalletUseCase.getWallet(walletId)
        );
        
        assertEquals("Wallet not found with ID: " + walletId, exception.getMessage());
        
        verify(walletRepository).findById(walletId);
    }
}
