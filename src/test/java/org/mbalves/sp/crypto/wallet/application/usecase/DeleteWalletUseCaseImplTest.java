package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteWalletUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @InjectMocks
    private DeleteWalletUseCaseImpl deleteWalletUseCase;

    @Test
    void deleteWallet_WhenWalletExists_ShouldDelete() {
        Long walletId = 1L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));

        deleteWalletUseCase.deleteWallet(walletId);

        verify(walletRepository).deleteById(walletId);
    }

    @Test
    void deleteWallet_WhenWalletDoesNotExist_ShouldThrow() {
        Long walletId = 1L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> deleteWalletUseCase.deleteWallet(walletId));
        verify(walletRepository, never()).deleteById(walletId);
    }
}
