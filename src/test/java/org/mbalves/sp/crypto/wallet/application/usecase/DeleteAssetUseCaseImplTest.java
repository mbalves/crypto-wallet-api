package org.mbalves.sp.crypto.wallet.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mbalves.sp.crypto.wallet.application.port.out.WalletRepositoryPort;
import org.mbalves.sp.crypto.wallet.domain.Asset;
import org.mbalves.sp.crypto.wallet.domain.Token;
import org.mbalves.sp.crypto.wallet.domain.Wallet;
import org.mbalves.sp.crypto.wallet.domain.exception.AssetNotFoundException;
import org.mbalves.sp.crypto.wallet.domain.exception.WalletNotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAssetUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @InjectMocks
    private DeleteAssetUseCaseImpl deleteAssetUseCase;

    private Wallet wallet;
    private final Long walletId = 1L;
    private final String symbol = "BTC";

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setEmail("test@example.com");
        wallet.setAssets(new ArrayList<>());

        Asset asset = new Asset();
        Token token = new Token();
        token.setSymbol(symbol);
        token.setPrice(BigDecimal.valueOf(50000.0));
        asset.setToken(token);
        asset.setQuantity(BigDecimal.valueOf(1.0));
        wallet.addAsset(asset);
    }

    @Test
    void deleteAsset_WhenWalletExists_ShouldDeleteAsset() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        // Act
        deleteAssetUseCase.deleteAsset(walletId, symbol);

        // Assert
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(captor.capture());
        Wallet captured = captor.getValue();

        assertNotNull(captured);
        assertEquals(0, captured.getAssets().size());
    }

    @Test
    void deleteAsset_WhenWalletDoesNotExist_ShouldThrowException() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> deleteAssetUseCase.deleteAsset(walletId, symbol)
        );
        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void deleteAsset_WhenAssetDoesNotExist_ShouldThrowException() {
        // Arrange
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(AssetNotFoundException.class,
                () -> deleteAssetUseCase.deleteAsset(walletId, "ETH")
        );
        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
