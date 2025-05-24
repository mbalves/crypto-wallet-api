package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletAlreadyExistsException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWalletUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @InjectMocks
    private CreateWalletUseCaseImpl createWalletUseCase;

    private String email;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setEmail(email);
    }

    @Test
    void createWallet_WhenEmailDoesNotExist_ShouldCreateAndReturnWallet() {
        // Arrange
        when(walletRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Act
        Wallet result = createWalletUseCase.createWallet(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(1L, result.getId());
        
        verify(walletRepository).findByEmail(email);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        when(walletRepository.findByEmail(email)).thenReturn(Optional.of(wallet));

        // Act & Assert
        WalletAlreadyExistsException exception = assertThrows(
            WalletAlreadyExistsException.class,
            () -> createWalletUseCase.createWallet(email)
        );
        
        assertEquals("Wallet already exists for email: " + email, exception.getMessage());
        
        verify(walletRepository).findByEmail(email);
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
